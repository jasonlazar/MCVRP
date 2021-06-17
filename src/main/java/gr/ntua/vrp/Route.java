package gr.ntua.vrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interview.graph.TravelingSalesmanHeldKarp;

public class Route {
	private ArrayList<Node> routes;
	private int length;
	private double[][] distances;
	private double cost;

	public Route(double[][] distances) {
		this.routes = new ArrayList<>();
		this.distances = distances;
		this.cost = 0.0;
	}

	void addNode(Node customer) {
		routes.add(customer);
		++length;
		if (length > 1) {
			int prelast = routes.get(length - 2).NodeId;
			int last = routes.get(length - 1).NodeId;
			cost += distances[prelast][last];
		}
	}

	void addNode(Node customer, int pos) {
		int previous = routes.get(pos - 1).NodeId;
		int current = customer.NodeId;
		int next = routes.get(pos).NodeId;

		routes.add(pos, customer);
		++length;

		cost -= distances[previous][next];
		cost += distances[previous][current];
		cost += distances[current][next];
	}

	void removeNode(int pos) {
		int removed = routes.get(pos).NodeId;
		int previous = routes.get(pos - 1).NodeId;
		int next = routes.get(pos + 1).NodeId;

		routes.remove(pos);
		--length;

		cost -= distances[previous][removed];
		cost -= distances[removed][next];
		cost += distances[previous][next];
	}

	public double optimize() {
		if (routes.size() <= 3)
			return 0.0;

		double initial_cost = cost;
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
		cost = final_cost;

		return final_cost - initial_cost;
	}

	public ArrayList<Node> getRoutes() {
		return routes;
	}

	public double getCost() {
		return cost;
	}
}
