package gr.ntua.vrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interview.graph.TravelingSalesmanHeldKarp;

public class Route {
	public List<Double> costAfter;
	private List<Node> routes;
	private int length;
	private double[][] distances;
	private double cost;

	public Route(double[][] distances) {
		this.costAfter = new ArrayList<>();
		this.routes = new ArrayList<>();
		this.distances = distances;
		this.cost = 0.0;
	}

	void addNode(Node customer) {
		routes.add(customer);
		costAfter.add(0.0);
		++length;
		if (length > 1) {
			int prelast = routes.get(length - 2).NodeId;
			int last = routes.get(length - 1).NodeId;
			cost += distances[prelast][last];

			for (int i = 0; i < length - 1; ++i) {
				Double cur = costAfter.get(i);
				costAfter.set(i, cur + distances[prelast][last]);
			}
		}
	}

	void addNode(Node customer, int pos) {
		int previous = routes.get(pos - 1).NodeId;
		int current = customer.NodeId;
		int next = routes.get(pos).NodeId;
		double costDiff = 0.0;

		routes.add(pos, customer);
		++length;

		costDiff = distances[previous][current] + distances[current][next] - distances[previous][next];
		cost += costDiff;

		Double costAfterNext = costAfter.get(pos);
		costAfter.add(pos, costAfterNext + distances[current][next]);

		for (int i = 0; i < pos; ++i) {
			Double cur = costAfter.get(i);
			costAfter.set(i, cur + costDiff);
		}
	}

	void removeNode(int pos) {
		int removed = routes.get(pos).NodeId;
		int previous = routes.get(pos - 1).NodeId;
		int next = routes.get(pos + 1).NodeId;
		double costDiff = 0.0;

		routes.remove(pos);
		--length;

		costDiff = distances[previous][next] - distances[removed][next] - distances[previous][removed];
		cost += costDiff;

		costAfter.remove(pos);

		for (int i = 0; i < pos; ++i) {
			Double cur = costAfter.get(i);
			costAfter.set(i, cur + costDiff);
		}
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

		costAfter.set(length - 1, 0.0);
		for (int i = length - 2; i >= 0; --i) {
			int cur = routes.get(i).NodeId;
			int next = routes.get(i + 1).NodeId;

			costAfter.set(i, costAfter.get(i + 1) + distances[cur][next]);
		}

		return final_cost - initial_cost;
	}

	public List<Node> getRoutes() {
		return routes;
	}

	public double getCost() {
		return cost;
	}
}
