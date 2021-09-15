package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.List;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public abstract class Move implements Comparable<Move> {
	protected double cost;
	protected int route1Index;
	protected int route1NodeIndex;
	protected int route2Index;
	protected int route2NodeIndex;
	protected boolean needsTransfer;

	public Move(double cost, int r1, int r1Node, int r2, int r2Node) {
		this.cost = cost;
		this.route1Index = r1;
		this.route1NodeIndex = r1Node;
		this.route2Index = r2;
		this.route2NodeIndex = r2Node;
		this.needsTransfer = false;
	}

	public abstract void applyMove(TabuSearchSolver s);

	public abstract boolean isFeasible(TabuSearchSolver s);

	public abstract boolean transferFeasible(TabuSearchSolver s);

	protected abstract void transfer(TabuSearchSolver s);

	protected List<Integer> feasibleVehicles(TabuSearchSolver s, int[] routeDemands, int limit) {
		List<Integer> feasible = new ArrayList<>();
		Vehicle[] vehicles = s.getVehicles();
		for (Integer i : s.emptyVehicles) {
			Vehicle veh = vehicles[i];
			if (veh.checkIfFits(routeDemands)) {
				needsTransfer = true;
				feasible.add(i);
				if (feasible.size() == limit)
					return feasible;
			}
		}
		return feasible;
	}

	protected void swapRoutes(Vehicle[] vehicles, int routeFrom, int routeTo) {
		ArrayList<Node> tmp = vehicles[routeFrom].routes;
		vehicles[routeFrom].routes = vehicles[routeTo].routes;
		vehicles[routeTo].routes = tmp;
	}

	public int[] getVehicleIndices() {
		return new int[] { route1Index, route2Index };
	}

	@Override
	public int compareTo(Move m) {
		if (cost < m.cost)
			return -1;
		else if (cost == m.cost)
			return 0;
		else
			return 1;
	}
}
