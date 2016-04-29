package utility.polygonmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import utility.delaunay.Voronoi;
import utility.generation.NoiseArrayGenerator;
import utility.geom.*;
import utility.graph.*;
import utility.math.SeededRandom;

public class Map {
	public static final double LAKE_THRESHOLD = 0.3; // 0 to 1, fraction of
														// water corners for
														// water polygon

	private boolean needsMoreRandomness;
	private int size;
	private int numPoints;
	private int seed;
	private IslandShape.Type islandType;
	private PointSelector.Type pointType;
	private List<Point> points;
	private List<Corner> corners;
	private List<Edge> edges;
	private List<Center> centers;
	private SeededRandom mapRandom;

	public Map(int size, int seed, IslandShape.Type islandType, PointSelector.Type pointType) {
		this.size = size;
		this.numPoints = 1;
		this.points = new ArrayList<Point>();
		this.corners = new ArrayList<Corner>();
		this.edges = new ArrayList<Edge>();
		this.centers = new ArrayList<Center>();
		this.islandType = islandType;
		this.pointType = pointType;
		this.mapRandom = new SeededRandom(seed);
	}

	public void go() {

		long startTime, stepStartTime, currentTime;
		startTime = stepStartTime = System.currentTimeMillis();

		// Generate the initial random set of points
		points = PointSelector.generate(size, seed, numPoints, pointType);

		// Create a graph structure from the Voronoi edge list. The
		// methods in the Voronoi object are somewhat inconvenient for
		// my needs, so I transform that data into the data I actually
		// need: edges connected to the Delaunay triangles and the
		// Voronoi polygons, a reverse map from those four points back
		// to the edge, a map from these four points to the points
		// they connect to (both along the edge and crosswise).

		System.out.println("Generating graph...");

		Voronoi voronoi = new Voronoi(points, null, new Rectangle(0, 0, size, size));
		buildGraph(voronoi);
		improveCorners();

		currentTime = System.currentTimeMillis();

		System.out.println("Graph generated in " + (currentTime - stepStartTime) / 1000 + "s");
		System.out.println("Generating elevation...");

		stepStartTime = System.currentTimeMillis();

		// Determine the elevations and water at Voronoi corners.
		assignCornerElevations();

		// Determine polygon and corner type: ocean, coast, land.
		assignOceanCoastAndLand();

		// Rescale elevations so that the highest is 1.0, and they're
		// distributed well. We want lower elevations to be more common
		// than higher elevations, in proportions approximately matching
		// concentric rings. That is, the lowest elevation is the
		// largest ring around the island, and therefore should more
		// land area than the highest elevation, which is the very
		// center of a perfectly circular island.
		redistributeElevations(landCorners(corners));

		// Assign elevations to non-land corners
		corners.forEach(corner -> {
			if (corner.isOcean() || corner.isCoast())
				corner.setElevation(0.0);
		});

		// Polygon elevations are the average of their corners
		assignPolygonElevations();

		currentTime = System.currentTimeMillis();

		System.out.println("Elevation generated in " + (currentTime - stepStartTime) / 1000 + "s");
		System.out.println("Generating features...");

		stepStartTime = System.currentTimeMillis();

		// Determine downslope paths.
		calculateDownslopes();

		// Determine watersheds: for every corner, where does it flow
		// out into the ocean?
		calculateWatersheds();

		// Create rivers.
		createRivers();

		currentTime = System.currentTimeMillis();

		System.out.println("Features generated in " + (currentTime - stepStartTime) / 1000 + "s");
		System.out.println("Generating biomes...");

		stepStartTime = System.currentTimeMillis();

		// Determine moisture at corners, starting at rivers
		// and lakes, but not oceans. Then redistribute
		// moisture to cover the entire range evenly from 0.0
		// to 1.0. Then assign polygon moisture as the average
		// of the corner moisture.
		assignCornerMoisture();
		redistributeMoisture(landCorners(corners));
		assignPolygonMoisture();

		assignBiomes();

		currentTime = System.currentTimeMillis();

		System.out.println("Biomes generated in " + (currentTime - stepStartTime) / 1000 + "s");
		System.out.println("Map generated in " + (currentTime - startTime) / 1000 + "s");

	}

	private void assignBiomes() {
		centers.forEach(p -> {
			p.setBiome(getBiome(p));
		});
	}

	static public String getBiome(Center p) {
		if (p.isOcean()) {
			return "OCEAN";
		} else if (p.isWater()) {
			if (p.getElevation() < 0.1)
				return "MARSH";
			if (p.getElevation() > 0.8)
				return "ICE";
			return "LAKE";
		} else if (p.isCoast()) {
			return "BEACH";
		} else if (p.getElevation() > 0.8) {
			if (p.getMoisture() > 0.50)
				return "SNOW";
			else if (p.getMoisture() > 0.33)
				return "TUNDRA";
			else if (p.getMoisture() > 0.16)
				return "BARE";
			else
				return "SCORCHED";
		} else if (p.getElevation() > 0.6) {
			if (p.getMoisture() > 0.66)
				return "TAIGA";
			else if (p.getMoisture() > 0.33)
				return "SHRUBLAND";
			else
				return "TEMPERATE_DESERT";
		} else if (p.getElevation() > 0.3) {
			if (p.getMoisture() > 0.83)
				return "TEMPERATE_RAIN_FOREST";
			else if (p.getMoisture() > 0.50)
				return "TEMPERATE_DECIDUOUS_FOREST";
			else if (p.getMoisture() > 0.16)
				return "GRASSLAND";
			else
				return "TEMPERATE_DESERT";
		} else {
			if (p.getMoisture() > 0.66)
				return "TROPICAL_RAIN_FOREST";
			else if (p.getMoisture() > 0.33)
				return "TROPICAL_SEASONAL_FOREST";
			else if (p.getMoisture() > 0.16)
				return "GRASSLAND";
			else
				return "SUBTROPICAL_DESERT";
		}
	}

	private void assignPolygonMoisture() {
		Center p;
		Corner q;
		List<Corner> list;
		double sumMoisture;
		for (int i = 0; i < centers.size(); ++i) {
			p = centers.get(i);
			sumMoisture = 0.0;
			list = p.getCorners();
			for (int j = 0; j < list.size(); ++j) {
				q = list.get(j);
				if (q.getMoisture() > 1.0)
					q.setMoisture(1.0);
				sumMoisture += q.getMoisture();
			}
			p.setMoisture(sumMoisture / p.getCorners().size());
		}
	}

	private void redistributeMoisture(List<Corner> locations) {
		Collections.sort(locations, new Comparator<Center>() {
			@Override
			public int compare(Center o1, Center o2) {
				if (o1.getMoisture() < o2.getMoisture())
					return -1;
				if (o1.getMoisture() > o2.getMoisture())
					return 1;
				return 0;
			}
		});
		for (int i = 0; i < locations.size(); i++) {
			locations.get(i).setMoisture(i / (locations.size() - 1));
		}
	}

	private void assignCornerMoisture() {
		Corner q, r;
		double newMoisture;
		List<Corner> queue = new ArrayList<Corner>();
		;
		// Fresh water
		for (int i = 0; i < corners.size(); ++i) {
			q = corners.get(i);
			if ((q.isWater() || q.getRiver() > 0) && !q.isOcean()) {
				q.setMoisture(q.getRiver() > 0 ? Math.min(3.0, (0.2 * q.getRiver())) : 1.0);
				queue.add(q);
			} else {
				q.setMoisture(0.0);
			}
		}
		while (queue.size() > 0) {
			q = queue.get(0);
			queue.remove(0);

			List<Corner> adj = q.getAdjacent();
			for (int i = 0; i < adj.size(); ++i) {
				r = adj.get(i);
				newMoisture = q.getMoisture() * 0.9;
				if (newMoisture > r.getMoisture()) {
					r.setMoisture(newMoisture);
					queue.add(r);
				}
			}
		}
		// Salt water
		corners.forEach(cor -> {
			if (cor.isOcean() || cor.isCoast()) {
				cor.setMoisture(1.0);
			}
		});
	}

	private void createRivers() {
		Corner q;
		Edge edge;

		for (int i = 0; i < size / 2; i++) {
			q = corners.get(mapRandom.nextIntRange(0, corners.size() - 1));
			if (q.isOcean() || q.getElevation() < 0.3 || q.getElevation() > 0.9)
				continue;
			// Bias rivers to go west: if (q.downslope.x > q.x) continue;
			while (!q.isCoast()) {
				if (q.equals(q.getDownslope())) {
					break;
				}
				edge = lookupEdgeFromCorner(q, q.getDownslope());
				edge.setRiver(edge.getRiver() + 1);
				q.setRiver(q.getRiver() + 1);
				q.getDownslope().setRiver(q.getDownslope().getRiver() + 1); // TODO:
																			// fix
																			// double
																			// count
				q = q.getDownslope();
			}
		}
	}

	private Edge lookupEdgeFromCorner(Corner q, Corner s) {
		List<Edge> pro = q.getProtrudes();
		Edge edge;
		for (int i = 0; i < pro.size(); ++i) {
			edge = pro.get(i);
			if (edge.getV0().equals(s) || edge.getV1().equals(s))
				return edge;
		}
		return null;
	}

	private void calculateWatersheds() {

		boolean changed;

		// Initially the watershed pointer points downslope one step.
		corners.forEach(q -> {
			q.setWatershed(q);
			if (!q.isOcean() && !q.isCoast()) {
				q.setWatershed(q.getDownslope());
			}
		});
		// Follow the downslope pointers to the coast. Limit to 100
		// iterations although most of the time with numPoints==2000 it
		// only takes 20 iterations because most points are not far from
		// a coast. can run faster by looking at
		// p.watershed.watershed instead of p.downslope.watershed.
		Corner q;
		for (int i = 0; i < 100; i++) {
			changed = false;
			for (int j = 0; j < corners.size(); ++j) {
				q = corners.get(j);
				if (!q.isOcean() && !q.isCoast() && !q.getWatershed().isCoast()) {
					Corner r = q.getDownslope().getWatershed();
					if (!r.isOcean()) {
						q.setWatershed(r);
						changed = true;
					}
				}
			}
			if (!changed)
				break;
		}
		// How big is each watershed?
		corners.forEach(s -> {
			Corner r = s.getWatershed();
			r.setWatershedSize(1 + r.getWatershedSize());
		});
	}

	private void calculateDownslopes() {
		Corner r, q, s;
		for (int i = 0; i < corners.size(); ++i) {
			q = corners.get(i);
			r = q;
			List<Corner> adj = q.getAdjacent();
			for (int j = 0; j < adj.size(); ++j) {
				s = adj.get(j);
				if (s.getElevation() <= r.getElevation()) {
					r = s;
				}
			}
			q.setDownslope(r);
		}
	}

	private void assignPolygonElevations() {
		centers.forEach(p -> {
			double sumElevation = 0.0;
			List<Corner> cor = p.getCorners();
			Corner q;
			for (int i = 0; i < cor.size(); ++i) {
				q = cor.get(i);
				sumElevation += q.getElevation();
			}
			p.setElevation(sumElevation / p.getCorners().size());
		});
	}

	private void redistributeElevations(List<Corner> locations) {
		// SCALE_FACTOR increases the mountain area. At 1.0 the maximum
		// elevation barely shows up on the map, so we set it to 1.1.
		double SCALE_FACTOR = 1.1;
		double y, x;

		Collections.sort(locations, new Comparator<Center>() {
			@Override
			public int compare(Center o1, Center o2) {
				if (o1.getElevation() < o2.getElevation())
					return -1;
				if (o1.getElevation() > o2.getElevation())
					return 1;
				return 0;
			}
		});
		for (int i = 0; i < locations.size(); ++i) {
			// Let y(x) be the total area that we want at elevation <= x.
			// We want the higher elevations to occur less than lower
			// ones, and set the area to be y(x) = 1 - (1-x)^2.
			y = i / (locations.size() - 1);
			// Now we have to solve for x, given the known y.
			// * y = 1 - (1-x)^2
			// * y = 1 - (1 - 2x + x^2)
			// * y = 2x - x^2
			// * x^2 - 2x + y = 0
			// From this we can use the quadratic equation to get:
			x = Math.sqrt(SCALE_FACTOR) - Math.sqrt(SCALE_FACTOR * (1 - y));
			if (x > 1.0)
				x = 1.0;
			locations.get(i).setElevation(x);
		}
	}

	private List<Corner> landCorners(List<Corner> corners) {
		List<Corner> locations = new ArrayList<Corner>();
		corners.forEach(q -> {
			if (!q.isOcean() && !q.isCoast()) {
				locations.add(q);
			}
		});
		return locations;
	}

	private void assignOceanCoastAndLand() {
		// Compute polygon attributes 'ocean' and 'water' based on the
		// corner attributes. Count the water corners per
		// polygon. Oceans are all polygons connected to the edge of the
		// map. In the first pass, mark the edges of the map as ocean;
		// in the second pass, mark any water-containing polygon
		// connected an ocean as ocean.
		List<Center> queue = new ArrayList<Center>();
		List<Corner> c;
		Center p;
		Corner q;
		int numWater;

		for (int i = 0; i < centers.size(); ++i) {
			p = centers.get(i);
			numWater = 0;
			c = p.getCorners();
			for (int j = 0; j < c.size(); ++j) {
				q = c.get(j);
				if (q.isBorder()) {
					p.setBorder(true);
					p.setOcean(true);
					q.setWater(true);
					queue.add(p);
				}
				if (q.isWater()) {
					numWater += 1;
				}
			}
			p.setWater(p.isOcean() || numWater >= p.getCorners().size() * LAKE_THRESHOLD);
		}
		while (queue.size() > 0) {
			p = queue.get(0);
			queue.remove(0);
			p.getNeighbours().forEach(r -> {
				if (r.isWater() && !r.isOcean()) {
					r.setOcean(true);
					queue.add(r);
				}
			});
		}
	}

	private void assignCornerElevations() {
		List<Corner> queue = new ArrayList<Corner>();

		corners.forEach(q -> {
			q.setWater(!inside(q.getPoint()));
		});

		corners.forEach(q -> {
			// The edges of the map are elevation 0
			if (q.isBorder()) {
				q.setElevation(0.0);
				queue.add(q);
			} else {
				q.setElevation(Double.MAX_VALUE);
			}
		});
		// Traverse the graph and assign elevations to each point. As we
		// move away from the map border, increase the elevations. This
		// guarantees that rivers always have a way down to the coast by
		// going downhill (no local minima).
		Corner q;
		while (queue.size() > 0) {
			q = queue.get(0);
			queue.remove(0);

			List<Corner> adj = q.getAdjacent();
			for (int i = 0; i < adj.size(); ++i) {
				Corner s = adj.get(i);
				// Every step up is epsilon over water or 1 over land. The
				// number doesn't matter because we'll rescale the
				// elevations later.
				double newElevation = 0.01 + q.getElevation();
				if (!q.isWater() && !s.isWater()) {
					newElevation += 1;
					if (needsMoreRandomness) {
						// HACK: the map looks nice because of randomness of
						// points, randomness of rivers, and randomness of
						// edges. Without random point selection, I needed to
						// inject some more randomness to make maps look
						// nicer. I'm doing it here, with elevations, but I
						// think there must be a better way. This hack is only
						// used with square/hexagon grids.
						newElevation += mapRandom.nextDouble();
					}
				}
				// If this point changed, we'll add it to the queue so
				// that we can process its neighbors too.
				if (newElevation < s.getElevation()) {
					s.setElevation(newElevation);
					queue.add(s);
				}
			}
		}
	}

	private void improveCorners() {
		List<Point> newCorners = new ArrayList<Point>(corners.size());
		// First we compute the average of the centers next to each corner.
		corners.forEach(q -> {
			if (q.isBorder()) {
				newCorners.set(q.getIndex(), q.getPoint());
			} else {
				Point point = new Point(0.0, 0.0);
				q.getTouches().forEach(r -> {
					point.setX(point.getX() + r.getPoint().getX());
					point.setY(point.getY() + r.getPoint().getY());
				});
				point.setX(point.getX() / q.getTouches().size());
				point.setY(point.getY() / q.getTouches().size());
				newCorners.set(q.getIndex(), point);
			}
		});

		// Move the corners to the new locations.
		for (int i = 0; i < corners.size(); i++) {
			corners.get(i).setPoint(newCorners.get(i));
		}

		// The edge midpoints were computed for the old corners and need
		// to be recomputed.
		edges.forEach(edge -> {
			if (edge.getV0() != null && edge.getV1() != null) {
				edge.setMidPoint(Point.interpolate(edge.getV0().getPoint(), edge.getV1().getPoint(), 0.5));
			}
		});
	}

	private void buildGraph(Voronoi voronoi) {
		List<utility.delaunay.Edge> libedges = voronoi.getEdges();
		Dictionary<Point, Center> centerLookup = new Hashtable<Point, Center>();

		// Build Center objects for each of the points, and a lookup map
		// to find those Center objects again as we build the graph
		points.forEach(point -> {
			Center p = new Center(centers.size(), point);
			centers.add(p);
			centerLookup.put(point, p);
		});

		// Workaround for Voronoi lib bug: we need to call region()
		// before Edges or neighboringSites are available
		centers.forEach(center -> {
			voronoi.region(center.getPoint());
		});

		// The Voronoi library generates multiple Point objects for
		// corners, and we need to canonicalize to one Corner object.
		// To make lookup fast, we keep an array of Points, bucketed by
		// x value, and then we only have to look at other Points in
		// nearby buckets. When we fail to find one, we'll create a new
		// Corner object.
		Dictionary<Integer, List<Corner>> cornerMap = new Hashtable<Integer, List<Corner>>();

		libedges.forEach(libedge -> {
			LineSegment dedge = libedge.delaunayLine();
			LineSegment vedge = libedge.voronoiEdge();

			// Fill the graph data. Make an Edge object corresponding to
			// the edge from the voronoi library.
			Edge edge = new Edge(edges.size());
			edges.add(edge);
			if (vedge.getP0() != null && vedge.getP1() != null)
				edge.setMidPoint(Point.interpolate(vedge.getP0(), vedge.getP1(), 0.5));

			// Edges point to corners. Edges point to centers.
			edge.setV0(makeCorner(vedge.getP0(), cornerMap));
			edge.setV1(makeCorner(vedge.getP1(), cornerMap));
			edge.setD0(centerLookup.get(dedge.getP0()));
			edge.setD1(centerLookup.get(dedge.getP1()));

			// Centers point to edges. Corners point to edges.
			if (edge.getD0() != null) {
				edge.getD0().getBorders().add(edge);
			}
			if (edge.getD1() != null) {
				edge.getD1().getBorders().add(edge);
			}
			if (edge.getV0() != null) {
				edge.getV0().getProtrudes().add(edge);
			}
			if (edge.getV1() != null) {
				edge.getV1().getProtrudes().add(edge);
			}

			// Centers point to centers.
			if (edge.getD0() != null && edge.getD1() != null) {
				addToCenterList(edge.getD0().getNeighbours(), edge.getD1());
				addToCenterList(edge.getD1().getNeighbours(), edge.getD0());
			}

			// Corners point to corners
			if (edge.getV0() != null && edge.getV1() != null) {
				addToCornerList(edge.getV0().getAdjacent(), edge.getV1());
				addToCornerList(edge.getV1().getAdjacent(), edge.getV0());
			}

			// Centers point to corners
			if (edge.getD0() != null) {
				addToCornerList(edge.getD0().getCorners(), edge.getV0());
				addToCornerList(edge.getD0().getCorners(), edge.getV1());
			}
			if (edge.getD1() != null) {
				addToCornerList(edge.getD1().getCorners(), edge.getV0());
				addToCornerList(edge.getD1().getCorners(), edge.getV1());
			}

			// Corners point to centers
			if (edge.getV0() != null) {
				addToCenterList(edge.getV0().getTouches(), edge.getD0());
				addToCenterList(edge.getV0().getTouches(), edge.getD1());
			}
			if (edge.getV1() != null) {
				addToCenterList(edge.getV1().getTouches(), edge.getD0());
				addToCenterList(edge.getV1().getTouches(), edge.getD1());
			}
		});
	}

	private void addToCornerList(List<Corner> v, Corner x) {
		if (x != null && v.indexOf(x) < 0) {
			v.add(x);
		}
	}

	private void addToCenterList(List<Center> v, Center x) {
		if (x != null && v.indexOf(x) < 0) {
			v.add(x);
		}
	}

	private Corner makeCorner(Point point, Dictionary<Integer, List<Corner>> cornerMap) {
		Corner q;
		int bucket;
		if (point == null)
			return null;
		for (bucket = (int) ((point.getX()) - 1); bucket <= (int) (point.getX()) + 1; ++bucket) {
			if (cornerMap.get(bucket) == null)
				continue;
			List<Corner> list = cornerMap.get(bucket);

			for (int i = 0; i < list.size(); ++i) {
				Corner corner = list.get(i);
				double dx = point.getX() - corner.getPoint().getX();
				double dy = point.getY() - corner.getPoint().getY();
				if (dx * dx + dy * dy < 1e-6)
					return corner;
			}
		}
		bucket = (int) (point.getX());
		if (cornerMap.get(bucket) == null)
			cornerMap.put(bucket, new ArrayList<Corner>());
		q = new Corner(corners.size());
		corners.add(q);
		q.setPoint(point);
		q.setBorder(point.getX() == 0 || point.getX() == size || point.getY() == 0 || point.getY() == size);
		cornerMap.get(bucket).add(q);
		return q;
	}

	public static double getLakeThreshold() {
		return LAKE_THRESHOLD;
	}

	public boolean needsMoreRandomness() {
		return needsMoreRandomness;
	}

	public int getSize() {
		return size;
	}

	public int getNumPoints() {
		return numPoints;
	}

	public List<Point> getPoints() {
		return points;
	}

	public List<Corner> getCorners() {
		return corners;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public List<Center> getCenters() {
		return centers;
	}

	public boolean inside(Point p) {
		return IslandShape.makeShape(seed, new Point(2 * (p.getX() / size - 0.5), 2 * (p.getY() / size - 0.5)),
				islandType);
	}

	private static class IslandShape {

		public static double ISLAND_FACTOR = 1.07; // 1.0 means no small
													// islands; 2.0 leads to a
													// lot

		public static boolean makeShape(int seed, Point q, Type type) {
			if (type == Type.PERLIN)
				return IslandShape.makePerlin(seed, q);
			if (type == Type.NONE)
				return true;
			return true;
		}

		// The Perlin-based island combines perlin noise with the radius
		private static boolean makePerlin(int seed, Point q) {
			float[][] perlin = NoiseArrayGenerator.generateNoise(256, 256, seed);
			double c = (perlin[(int) ((q.getX() + 1) * 128)][(int) ((q.getY() + 1) * 128)] + 1) / 2;
			return c > (0.3 + 0.3 * q.length() * q.length());
		}

		private static enum Type {
			PERLIN, NONE
		}

	}

	private static class PointSelector {
		private static int NUM_LLOYD_RELAXATIONS = 2;

		static public boolean needsMoreRandomness(Type type) {
			return type == Type.SQUARE || type == Type.HEXAGON;
		}

		static public List<Point> generate(int size, int seed, int numPoints, Type type) {
			if (type == Type.HEXAGON)
				return generateHexagon(size, seed, numPoints);
			if (type == Type.SQUARE)
				return generateSquare(size, seed, numPoints);
			if (type == Type.RELAXED)
				return generateRelaxed(size, seed, numPoints);
			return generateRandom(size, seed, numPoints);

		}

		static public List<Point> generateRandom(int size, int seed, int numPoints) {
			SeededRandom mapRandom = new SeededRandom(seed);
			Point p;
			int i;
			List<Point> points = new ArrayList<Point>();
			for (i = 0; i < numPoints; i++) {
				p = new Point(mapRandom.nextDoubleRange(10, size - 10), mapRandom.nextDoubleRange(10, size - 10));
				points.add(p);
			}
			return points;
		}

		public static List<Point> generateRelaxed(int size, int seed, int numPoints) {
			// We'd really like to generate "blue noise". Algorithms:
			// 1. Poisson dart throwing: check each new point against all
			// existing points, and reject it if it's too close.
			// 2. Start with a hexagonal grid and randomly perturb points.
			// 3. Lloyd Relaxation: move each point to the centroid of the
			// generated Voronoi polygon, then generate Voronoi again.
			// 4. Use force-based layout algorithms to push points away.
			// 5. More at http://www.cs.virginia.edu/~gfx/pubs/antimony/
			// Option 3 is implemented here. If it's run for too many
			// iterations,
			// it will turn into a grid, but convergence is very slow, and we
			// only
			// run it a few times.
			List<Point> points = generateRandom(size, seed, numPoints);
			for (int i = 0; i < NUM_LLOYD_RELAXATIONS; i++) {
				Voronoi voronoi = new Voronoi(points, null, new Rectangle(0, 0, size, size));
				points.forEach(point0 -> {
					List<Point> region = voronoi.region(point0);
					point0.setX(0.0);
					point0.setY(0.0);
					region.forEach(po -> {
						point0.setX(point0.getX() + po.getX());
						point0.setY(point0.getY() + po.getY());
					});
					point0.setX(point0.getX() / region.size());
					point0.setY(point0.getY() / region.size());
					region = new ArrayList<Point>();
				});
			}
			return points;
		}

		// Generate points on a square grid
		public static List<Point> generateSquare(int size, int seed, int numPoints) {
			List<Point> points = new ArrayList<Point>();
			int N = (int) Math.sqrt(numPoints);
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					points.add(new Point((0.5 + x) / N * size, (0.5 + y) / N * size));
				}
			}
			return points;
		}

		// Generate points on a hexagon grid
		public static List<Point> generateHexagon(int size, int seed, int numPoints) {
			List<Point> points = new ArrayList<Point>();
			int N = (int) Math.sqrt(numPoints);
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					points.add(new Point((0.5 + x) / N * size, (0.25 + 0.5 * x % 2 + y) / N * size));
				}
			}
			return points;

		}

		private static enum Type {
			SQUARE, HEXAGON, RELAXED, RANDOM
		}
	}

}
