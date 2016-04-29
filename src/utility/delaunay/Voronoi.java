package utility.delaunay;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import utility.geom.Point;
import utility.geom.Rectangle;

public class Voronoi {

	private SiteList sites;
	private Dictionary<Point, Site> sitesIndexedByLocation;
	private List<Triangle> triangles;
	private List<Edge> edges;
	private Rectangle plotBounds;

	public Voronoi(List<Point> points, List<Integer> colors, Rectangle plotBounds) {
		sites = new SiteList();
		sitesIndexedByLocation = new Hashtable<Point, Site>();
		addSites(points, colors);
		this.plotBounds = plotBounds;
		triangles = new ArrayList<Triangle>();
		edges = new ArrayList<Edge>();
		fortunesAlgorithm();
	}

	private void addSites(List<Point> points, List<Integer> colors) {
		int length = points.size();
		for (int i = 0; i < length; ++i) {
			addSite(points.get(i), colors != null ? colors.get(i) : 0, i);
		}
	}

	private void addSite(Point p, int color, int index) {
		double weight = Math.random() * 100;
		Site site = Site.create(p, index, weight, color);
		sites.add(site);
		sitesIndexedByLocation.put(p, site);
	}

	public static int compareByYThenX(Site s1, Site s2) {
		if (s1.getY() < s2.getY())
			return -1;
		if (s1.getY() > s2.getY())
			return 1;
		if (s1.getX() < s2.getX())
			return -1;
		if (s1.getX() > s2.getX())
			return 1;
		return 0;
	}

	public static int compareByYThenX(Site s1, Point s2) {
		if (s1.getY() < s2.getY())
			return -1;
		if (s1.getY() > s2.getY())
			return 1;
		if (s1.getX() < s2.getX())
			return -1;
		if (s1.getX() > s2.getX())
			return 1;
		return 0;
	}

	private void fortunesAlgorithm() {
		Site newSite, bottomSite, topSite, tempSite;
		Vertex v, vertex;
		Point newintstar = null;
		LR leftRight;
		Halfedge lbnd, rbnd, llbnd, rrbnd, bisector;
		Edge edge;

		Rectangle dataBounds = sites.getSitesBounds();

		int sqrt_nsites = (int) (Math.sqrt(sites.size() + 4));
		HalfedgePriorityQueue heap = new HalfedgePriorityQueue(dataBounds.getY(), dataBounds.getHeight(), sqrt_nsites);
		EdgeList edgeList = new EdgeList(dataBounds.getX(), dataBounds.getWidth(), sqrt_nsites);
		List<Halfedge> halfEdges = new ArrayList<Halfedge>();
		List<Vertex> vertices = new ArrayList<Vertex>();

		Site bottomMostSite = sites.next();
		newSite = sites.next();

		while (true) {
			if (heap.isEmpty() == false) {
				newintstar = heap.min();
			}

			if (newSite != null && (heap.isEmpty() || compareByYThenX(newSite, newintstar) < 0)) {
				/* new site is smallest */
				// trace("smallest: new site " + newSite);

				// Step 8:
				lbnd = edgeList.edgeListLeftNeighbor(newSite.getCoord()); 
				// trace("lbnd: " + lbnd);
				rbnd = lbnd.getEdgeListRightNeighbor(); // the Halfedge just to
														// the right
				// trace("rbnd: " + rbnd);
				bottomSite = rightRegion(lbnd, bottomMostSite); // this is the
																// same as
																// leftRegion(rbnd)
				// this Site determines the region containing the new site
				// trace("new Site is in region of existing site: " +
				// bottomSite);

				// Step 9:
				edge = Edge.createBisectingEdge(bottomSite, newSite);
				// trace("new edge: " + edge);
				edges.add(edge);

				bisector = Halfedge.create(edge, LR.LEFT);
				halfEdges.add(bisector);
				// inserting two Halfedges into edgeList constitutes Step 10:
				// insert bisector to the right of lbnd:
				edgeList.insert(lbnd, bisector);

				// first half of Step 11:
				if ((vertex = Vertex.intersect(lbnd, bisector)) != null) {
					vertices.add(vertex);
					heap.remove(lbnd);
					lbnd.setVertex(vertex);
					lbnd.setYstar(vertex.getY() + Point.distance(newSite.getCoord(), vertex.getCoord()));
					heap.insert(lbnd);
				}

				lbnd = bisector;
				bisector = Halfedge.create(edge, LR.RIGHT);
				halfEdges.add(bisector);
				// second Halfedge for Step 10:
				// insert bisector to the right of lbnd:
				edgeList.insert(lbnd, bisector);

				// second half of Step 11:
				if ((vertex = Vertex.intersect(bisector, rbnd)) != null) {
					vertices.add(vertex);
					bisector.setVertex(vertex);
					bisector.setYstar(vertex.getY() + Point.distance(newSite.getCoord(), vertex.getCoord()));
					heap.insert(bisector);
				}

				newSite = sites.next();
			} else if (heap.isEmpty() == false) {
				/* intersection is smallest */
				lbnd = heap.extractMin();
				llbnd = lbnd.getEdgeListLeftNeighbor();
				rbnd = lbnd.getEdgeListRightNeighbor();
				rrbnd = rbnd.getEdgeListRightNeighbor();
				bottomSite = leftRegion(lbnd, bottomMostSite);
				topSite = rightRegion(rbnd, bottomMostSite);
				// these three sites define a Delaunay triangle
				// (not actually using these for anything...)
				// _triangles.push(new Triangle(bottomSite, topSite,
				// rightRegion(lbnd)));

				v = lbnd.getVertex();
				v.setIndex();
				lbnd.getEdge().setVertex(lbnd.getLeftRight(), v);
				rbnd.getEdge().setVertex(rbnd.getLeftRight(), v);
				edgeList.remove(lbnd);
				heap.remove(rbnd);
				edgeList.remove(rbnd);
				leftRight = LR.LEFT;
				if (bottomSite.getY() > topSite.getY()) {
					tempSite = bottomSite;
					bottomSite = topSite;
					topSite = tempSite;
					leftRight = LR.RIGHT;
				}
				edge = Edge.createBisectingEdge(bottomSite, topSite);
				edges.add(edge);
				bisector = Halfedge.create(edge, leftRight);
				halfEdges.add(bisector);
				edgeList.insert(llbnd, bisector);
				edge.setVertex(LR.other(leftRight), v);
				if ((vertex = Vertex.intersect(llbnd, bisector)) != null) {
					vertices.add(vertex);
					heap.remove(llbnd);
					llbnd.setVertex(vertex);
					llbnd.setYstar(vertex.getY() + Point.distance(bottomSite.getCoord(), vertex.getCoord()));
					heap.insert(llbnd);
				}
				if ((vertex = Vertex.intersect(bisector, rrbnd)) != null) {
					vertices.add(vertex);
					bisector.setVertex(vertex);
					bisector.setYstar(vertex.getY() + Point.distance(bottomSite.getCoord(), vertex.getCoord()));
					heap.insert(bisector);
				}
			} else {
				break;
			}
		}

		// heap should be empty now
		heap.dispose();
		edgeList.dispose();

		halfEdges.forEach(he -> {
			he.reallyDispose();
		});
		halfEdges = new ArrayList<Halfedge>();

		// we need the vertices to clip the edges
		edges.forEach(e -> {
			e.clipVertices(plotBounds);
		});
		// but we don't actually ever use them again!
		vertices.forEach(ver -> {
			ver.dispose();
		});
		vertices = new ArrayList<Vertex>();
	}

	Site rightRegion(Halfedge he, Site bottomMostSite) {
		Edge edge = he.getEdge();
		if (edge == null) {
			return bottomMostSite;
		}
		return edge.site(LR.other(he.getLeftRight()));
	}

	Site leftRegion(Halfedge he, Site bottomMostSite) {
		Edge edge = he.getEdge();
		if (edge == null) {
			return bottomMostSite;
		}
		return edge.site(he.getLeftRight());
	}
	
	public List<Point> region(Point p)
	{
		Site site = sitesIndexedByLocation.get(p);
		if (site == null)
		{
			return new ArrayList<Point>();
		}
		return site.region(plotBounds);
	}

	public List<Edge> getEdges() {
		return edges;
	}
	
	
}
