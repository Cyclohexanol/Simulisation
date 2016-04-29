package utility.polygonmap;

import java.util.ArrayList;
import java.util.List;

import utility.graph.Edge;
import utility.math.SeededRandom;

public class Lava {
	
	static public final double FRACTION_LAVA_FISSURES = 0.2;  // 0 to 1, probability of fissure
    
    // The lava array marks the edges that hava lava.
    private List<Boolean> lava;  // edge index -> Boolean

    public Lava()
    {
    	lava = new ArrayList<Boolean>();
    }
    // Lava fissures are at high elevations where moisture is low
    public void createLava(Map map, SeededRandom randomDouble) {
      
    for(int i=0; i<map.getEdges().size(); ++i)
    {
    	Edge edge = map.getEdges().get(i);
    	if (edge.isRiver() && !edge.getD0().isWater() && !edge.getD1().isWater()
           && edge.getD0().getElevation() > 0.8 && edge.getD1().getElevation() > 0.8
           && edge.getD0().getMoisture() < 0.3 && edge.getD1().getMoisture() < 0.3
           && Math.random() < FRACTION_LAVA_FISSURES) {
        lava.set(edge.getIndex(),true);
        }
    	}
    }
    
	public List<Boolean> getLava() {
		return lava;
	}
    
    
}