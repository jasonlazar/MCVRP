package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public abstract class Move implements Comparable<Move> {
	protected double cost;
	protected Vehicle vehicle1;
	protected int route1NodeIndex;
	protected Vehicle vehicle2;
	protected int route2NodeIndex;
	protected boolean needsTransfer;
	protected boolean firstFeasible;
	protected boolean secondFeasible;
	private Vehicle transfer1To;
	private Vehicle transfer2To;

	public Move(double cost, Vehicle v1, int r1Node, Vehicle v2, int r2Node) {
		this.cost = cost;
		this.vehicle1 = v1;
		this.route1NodeIndex = r1Node;
		this.vehicle2 = v2;
		this.route2NodeIndex = r2Node;
		this.needsTransfer = false;
	}

	public abstract void applyMove(TabuSearchSolver s);

	public abstract boolean isFeasible(TabuSearchSolver s);

	public abstract boolean transferFeasible(TabuSearchSolver s);

	protected boolean transferFeasible(TabuSearchSolver s, Collection<Node> from1To2, Collection<Node> from2To1) {
		List<Vehicle> firstCanMoveTo = new ArrayList<>();
		List<Vehicle> secondCanMoveTo = new ArrayList<>();

		int[] firstRouteDemands = vehicle1.calculateDemandsPlusMinus(from2To1, from1To2);
		int[] secondRouteDemands = vehicle2.calculateDemandsPlusMinus(from1To2, from2To1);

		EmptyVehicleSet empty = s.emptyVehicles;

		if (!firstFeasible) {

			if (!secondFeasible || secondRouteDemands.length == 0)
				s.emptyVehicles.add(vehicle2);

			int limit = secondFeasible ? 1 : 2;
			firstCanMoveTo = empty.feasibleVehicles(firstRouteDemands, limit);
			s.emptyVehicles.remove(vehicle2);

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
				s.emptyVehicles.add(vehicle1);

			int limit = (firstCanMoveTo.size() != 1) ? 1 : 2;
			secondCanMoveTo = empty.feasibleVehicles(secondRouteDemands, limit);
			s.emptyVehicles.remove(vehicle1);

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
		Vehicle[] vehicles = s.getVehicles();
		if (!firstFeasible) {
			swapRoutes(vehicles, vehicle1, transfer1To);
			s.emptyVehicles.add(vehicle1);
			s.emptyVehicles.remove(transfer1To);
			vehicle1 = transfer1To;
		}
		if (!secondFeasible) {
			swapRoutes(s.getVehicles(), vehicle2, transfer2To);
			s.emptyVehicles.add(vehicle2);
			s.emptyVehicles.remove(transfer2To);
			vehicle2 = transfer2To;
		}
	}

	protected void swapRoutes(Vehicle[] vehicles, Vehicle vehicleFrom, Vehicle vehicleTo) {
		ArrayList<Node> tmp = vehicleFrom.routes;
		vehicleFrom.routes = vehicleTo.routes;
		vehicleTo.routes = tmp;
	}

	public Vehicle[] getVehicles() {
		return new Vehicle[] { vehicle1, vehicle2 };
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
