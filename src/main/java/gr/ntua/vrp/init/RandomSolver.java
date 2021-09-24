package gr.ntua.vrp.init;

import java.io.IOException;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Solver;
import gr.ntua.vrp.VRPRunner;
import gr.ntua.vrp.Vehicle;

public class RandomSolver extends Solver {

	public RandomSolver(VRPRunner jct) throws IOException {
		super(jct);
	}

	public RandomSolver(Solver s) {
		super(s);
	}

	@Override
	public Solver solve() {
		int vehIndex = 0;
		Vehicle curVehicle = vehicles[vehIndex];
		Node depot = nodes[0];
		curVehicle.appendNode(depot);
		int previous_id = 0;

		cost = 0;

		for (Node n : nodes) {
			while (!curVehicle.checkIfFits(n.demands)) {
				// Close route of current vehicle
				appendNodeAndUpdateCost(curVehicle, depot, previous_id);

				// Open route of next vehicle
				vehIndex++;
				curVehicle = vehicles[vehIndex];
				curVehicle.appendNode(depot);
				previous_id = 0;
			}
			appendNodeAndUpdateCost(curVehicle, n, previous_id);
			previous_id = n.NodeId;
		}

		appendNodeAndUpdateCost(curVehicle, depot, previous_id);

		for (++vehIndex; vehIndex < vehicles.length; ++vehIndex) {
			curVehicle = vehicles[vehIndex];
			curVehicle.appendNode(depot);
			curVehicle.appendNode(depot);
		}

		return this;
	}

	private void appendNodeAndUpdateCost(Vehicle v, Node n, int previous) {
		v.appendNode(n);
		cost += distances[previous][n.NodeId];
	}

}
