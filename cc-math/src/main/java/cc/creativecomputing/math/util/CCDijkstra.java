package cc.creativecomputing.math.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CCDijkstra<NodeType extends CCGraphNode> {
	
	public class CCDijkstraEdge {
		private final NodeType source;
		private final NodeType destination;
		private final double weight;

		public CCDijkstraEdge(NodeType theSource, NodeType theDestination, double theWeight) {
			source = theSource;
			destination = theDestination;
			weight = theWeight;
		}

		@Override
		public String toString() {
			return source + " " + destination;
		}

	}

	private final List<CCDijkstraEdge> edges;
	private Set<NodeType> settledNodes;
	private Set<NodeType> unSettledNodes;
	private Map<NodeType, NodeType> predecessors;
	private Map<NodeType, Double> distance;

	public CCDijkstra(List<NodeType> theNodes) {
		this.edges = new ArrayList<>();
		for(int i = 0; i < theNodes.size();i++){
			NodeType myNodeI = theNodes.get(i);
			for(int j = i + 1; j < theNodes.size();j++){
				NodeType myNodeJ = theNodes.get(j);
				edges.add(new CCDijkstraEdge(myNodeI, myNodeJ, myNodeI.position().distance(myNodeJ.position())));
			}
		}
		execute(theNodes.get(0));
	}

	public void execute(NodeType source) {
		settledNodes = new HashSet<>();
		unSettledNodes = new HashSet<>();
		distance = new HashMap<>();
		predecessors = new HashMap<>();
		distance.put(source, 0d);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			NodeType node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(NodeType node) {
		List<NodeType> adjacentNodes = getNeighbors(node);
		for (NodeType target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
				distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}

	}

	private double getDistance(NodeType node, NodeType target) {
		for (CCDijkstraEdge edge : edges) {
			if (edge.source.equals(node) && edge.destination.equals(target)) {
				return edge.weight;
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private List<NodeType> getNeighbors(NodeType node) {
		List<NodeType> neighbors = new ArrayList<>();
		for (CCDijkstraEdge edge : edges) {
			if (edge.source.equals(node) && !isSettled(edge.destination)) {
				neighbors.add(edge.destination);
			}
		}
		return neighbors;
	}

	private NodeType getMinimum(Set<NodeType> vertexes) {
		NodeType minimum = null;
		for (NodeType vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(NodeType vertex) {
		return settledNodes.contains(vertex);
	}

	private double getShortestDistance(NodeType destination) {
		Double d = distance.get(destination);
		if (d == null) {
			return Double.MAX_VALUE;
		} else {
			return d;
		}
	}

	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public LinkedList<NodeType> getPath(NodeType target) {
		LinkedList<NodeType> path = new LinkedList<>();
		NodeType step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}
}
