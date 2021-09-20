package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
	protected boolean firstFeasible;
	protected boolean secondFeasible;
	private int transfer1To;
	private int transfer2To;

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

	protected boolean transferFeasible(TabuSearchSolver s, Collection<Node> from1To2, Collection<Node> from2To1) {
		Vehicle[] vehicles = s.getVehicles();
		Vehicle veh1 = vehicles[route1Index];
		Vehicle veh2 = vehicles[route2Index];

		List<Integer> firstCanMoveTo = new ArrayList<>();
		List<Integer> secondCanMoveTo = new ArrayList<>();

		int[] firstRouteDemands = veh1.calculateDemandsPlusMinus(from2To1, from1To2);
		int[] secondRouteDemands = veh2.calculateDemandsPlusMinus(from1To2, from2To1);

		if (!firstFeasible) {

			if (!secondFeasible || secondRouteDemands.length == 0)
				s.emptyVehicles.add(route2Index);

			int limit = secondFeasible ? 1 : 2;
			firstCanMoveTo = feasibleVehicles(s, firstRouteDemands, limit);
			s.emptyVehicles.remove(route2Index);

			if (firstCanMoveTo.isEmpty())
				return false;
			else if (secondFeasible) {
				transfer1To = firstCanMoveTo.get(0);
				needsTransfer = true;
				return true;
			}
		}

		if (!secondFeasible) {

			if (!firstFeasible || firstRouteDemands.length == 0)
				s.emptyVehicles.add(route1Index);

			int limit = (firstCanMoveTo.size() != 1) ? 1 : 2;
			secondCanMoveTo = feasibleVehicles(s, secondRouteDemands, limit);
			s.emptyVehicles.remove(route1Index);

			if (secondCanMoveTo.isEmpty())
				return false;
			else if (firstFeasible) {
				transfer2To = secondCanMoveTo.get(0);
				needsTransfer = true;
				return true;
			}
		}

		transfer1To = firstCanMoveTo.get(0);
		transfer2To = secondCanMoveTo.get(0);

		if (transfer1To == transfer2To) {
			if (firstCanMoveTo.size() > 1)
				transfer1To = firstCanMoveTo.get(1);
			else if (secondCanMoveTo.size() > 1)
				transfer2To = secondCanMoveTo.get(1);
		}

		needsTransfer = transfer1To != transfer2To;
		return needsTransfer;
	}

	protected void transfer(TabuSearchSolver s) {
		if (!firstFeasible) {
			swapRoutes(s.getVehicles(), route1Index, transfer1To);
			s.emptyVehicles.add(route1Index);
			s.emptyVehicles.remove(transfer1To);
			if (route2Index == transfer1To)
				route2Index = route1Index;
			route1Index = transfer1To;
		}
		if (!secondFeasible) {
			swapRoutes(s.getVehicles(), route2Index, transfer2To);
			s.emptyVehicles.add(route2Index);
			s.emptyVehicles.remove(transfer2To);
			route2Index = transfer2To;
		}
	}

	protected List<Integer> feasibleVehicles(TabuSearchSolver s, int[] routeDemands, int limit) {
		List<Integer> feasible = new ArrayList<>();
		Vehicle[] vehicles = s.getVehicles();
		for (Integer i : s.emptyVehicles) {
			Vehicle veh = vehicles[i];
			if (veh.checkIfFits(routeDemands, new HashSet<Node>(veh.routes))) {
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
