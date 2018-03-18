package cc.creativecomputing.math.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class will deal with finding the optimal or approximately optimal 
 * minimum tour (hamiltonian cycle) or commonly known as the <a 
 * href="http://mathworld.wolfram.com/TravelingSalesmanProblem.html">Traveling 
 * Salesman Problem</a>. 
 * 
 * @author Andrew Newell 
 */ 
public class CCHamiltonianCycle { 
    //~ Methods ---------------------------------------------------------------- 
 
    /**
     * This method will return an approximate minimal traveling salesman tour 
     * (hamiltonian cycle). This algorithm requires that the graph be complete 
     * and the triangle inequality exists (if x,y,z are vertices then 
     * d(x,y)+d(y,z)<d(x,z) for all x,y,z) then this algorithm will guarantee a 
     * hamiltonian cycle such that the total weight of the cycle is less than or 
     * equal to double the total weight of the optimal hamiltonian cycle. The 
     * optimal solution is NP-complete, so this is a decent approximation that 
     * runs in polynomial time. 
     * 
     * @param <NodeType> 
     * @param theNodes is the graph to find the optimal tour for. 
     * 
     * @return The optimal tour as a list of vertices. 
     */ 
    public static <NodeType extends CCGraphNode> List<NodeType> getApproximateOptimalForCompleteGraph( List<NodeType> theNodes) 
    { 
        List<NodeType> vertices = new ArrayList<>(theNodes); 
 
   
 
        List<NodeType> tour = new LinkedList<>(); 
 
        // Each iteration a new vertex will be added to the tour until all 
        // vertices have been added 
        while (tour.size() != theNodes.size()) { 
            boolean firstEdge = true; 
            double minEdgeValue = 0; 
            int minVertexFound = 0; 
            int vertexConnectedTo = 0; 
 
            // A check will be made for the shortest edge to a vertex not within 
            // the tour and that new vertex will be added to the vertex 
            for (int i = 0; i < tour.size(); i++) { 
                NodeType v = tour.get(i); 
                for (int j = 0; j < vertices.size(); j++) { 
                    double weight = v.position().distance(vertices.get(j).position());
                    if (firstEdge || (weight < minEdgeValue)) { 
                        firstEdge = false; 
                        minEdgeValue = weight; 
                        minVertexFound = j; 
                        vertexConnectedTo = i; 
                    } 
                } 
            } 
            tour.add(vertexConnectedTo, vertices.get(minVertexFound)); 
            vertices.remove(minVertexFound); 
        } 
        return tour; 
    } 
} 