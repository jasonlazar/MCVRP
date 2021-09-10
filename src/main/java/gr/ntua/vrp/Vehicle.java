package gr.ntua.vrp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interview.graph.TravelingSalesmanHeldKarp;

public abstract class Vehicle {
	public ArrayList<Node> routes = new ArrayList<>();
	public int currentLocation;
	protected double[][] distances;
	protected int capacity;
	protected String name;

	public Vehicle(double[][] distances, int capacity) {
		this.currentLocation = 0; // In depot Initially
		this.routes.clear();
		this.distances = distances;
		this.capacity = capacity;
		this.name = null;
	}

	public abstract Vehicle makeCopy();

	public abstract void appendNode(Node Customer); // Add Customer to Vehicle routes

	public abstract boolean checkIfFits(int[] dem); // Check if we have Capacity Violation

	public abstract boolean checkIfFits(int[] dem, Collection<Node> remove); // Check if we have Capacity Violation

	public void addNode(Node Customer, int pos) {
		routes.add(pos, Customer);
	}

	public void removeNode(int pos) {
		routes.remove(pos);
	}

	public double optimizeRoute() {
		if (routes.size() <= 3)
			return 0.0;

		double initial_cost = getCost();
		Map<Integer, Node> mapping = new HashMap<>();
		double[][] myDist = new double[routes.size() - 1][routes.size() - 1];
		for (int i = 0; i < routes.size() - 1; ++i) {
			mapping.put(i, routes.get(i));
		}
		for (int i = 0; i < routes.size() - 1; ++i) {
			for (int j = 0; j < routes.size() - 1; ++j) {
				myDist[i][j] = distances[mapping.get(i).NodeId][mapping.get(j).NodeId];
			}
		}
		List<Integer> tsp_route = new ArrayList<>();
		double final_cost = TravelingSalesmanHeldKarp.minCost(myDist, tsp_route);
		routes.clear();
		for (Integer node : tsp_route) {
			routes.add(mapping.get(node));
		}
		return final_cost - initial_cost;
	}

	public double getCost() {
		double cost = 0;
		for (int i = 1; i < routes.size(); ++i) {
			int node1 = routes.get(i - 1).NodeId;
			int node2 = routes.get(i).NodeId;
			cost += distances[node1][node2];
		}
		return cost;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}