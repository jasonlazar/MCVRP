package gr.ntua.vrp.tabu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Solver;
import gr.ntua.vrp.VRPRunner;
import gr.ntua.vrp.Vehicle;
import gr.ntua.vrp.greedy.GreedySolver;

public class TabuSearchSolver extends Solver {
	final int TABU_Horizon;
	final int TABU_Matrix[][];
	private final int iterations;
	private final int restarts;
	private final Vehicle[] BestSolutionVehicles;

	private double BestSolutionCost;

	public TabuSearchSolver(VRPRunner jct) throws IOException {
		super(jct);
		this.TABU_Horizon = jct.TabuHorizon;
		this.iterations = jct.iterations;
		this.restarts = jct.restarts;

		this.BestSolutionVehicles = new Vehicle[this.noOfVehicles];

		for (int i = 0; i < this.noOfVehicles; i++) {
			this.BestSolutionVehicles[i] = this.vehicles[i].makeCopy();
		}

		GreedySolver greedySolver = new GreedySolver(this);
		greedySolver.solve();
		this.vehicles = greedySolver.getVehicles();
		this.cost = greedySolver.getCost();
		this.usedVehicles = greedySolver.getUsedVehicles();

		int DimensionCustomer = this.distances[1].length;
		TABU_Matrix = new int[DimensionCustomer + 1][DimensionCustomer + 1];
	}

	public TabuSearchSolver solve() {
		int restart_number = 0;
		int iteration_number = 0;

		Move BestMove;

		for (Vehicle v : vehicles) {
			this.cost += v.optimizeRoute();
		}

		this.BestSolutionCost = this.cost;

		while (true) {
			BestMove = findBestNeighbor();

			for (int o = 0; o < TABU_Matrix[0].length; o++) {
				for (int p = 0; p < TABU_Matrix[0].length; p++) {
					if (TABU_Matrix[o][p] > 0) {
						TABU_Matrix[o][p]--;
					}
				}
			}

			BestMove.applyMove(this);

			this.cost += BestMove.cost;

			int[] MoveVehicles = BestMove.getVehicleIndexes();
			for (int i : MoveVehicles)
				this.cost += this.vehicles[i].optimizeRoute();

			if (this.cost < this.BestSolutionCost) {
				iteration_number = 0;
				this.SaveBestSolution();
			} else {
				iteration_number++;
			}

			if (iterations == iteration_number) {
				if (restart_number == restarts)
					break;
				else {
					restart_number++;
					iteration_number = 0;
					restoreBest();
					applyRandomMove();
				}
			}
		}

		this.vehicles = this.BestSolutionVehicles;
		this.cost = this.BestSolutionCost;

		return this;
	}

	private void SaveBestSolution() {
		this.BestSolutionCost = this.cost;
		for (int j = 0; j < this.noOfVehicles; j++) {
			this.BestSolutionVehicles[j].routes.clear();
			if (!this.vehicles[j].routes.isEmpty()) {
				int RoutSize = this.vehicles[j].routes.size();
				for (int k = 0; k < RoutSize; k++) {
					Node n = this.vehicles[j].routes.get(k);
					this.BestSolutionVehicles[j].routes.add(n);
				}
			}
		}
	}

	private void restoreBest() {
		cost = BestSolutionCost;
		for (int j = 0; j < noOfVehicles; j++) {
			while (!vehicles[j].routes.isEmpty())
				vehicles[j].removeNode(0);
			if (!BestSolutionVehicles[j].routes.isEmpty()) {
				int routSize = BestSolutionVehicles[j].routes.size();
				for (int k = 0; k < routSize; k++) {
					Node n = BestSolutionVehicles[j].routes.get(k);
					vehicles[j].appendNode(n);
				}
			}
		}
	}

	private void applyRandomMove() {
		Random ran = new Random();
		Move m;
		do {
			int move = ran.nextInt(5);
			int vehIndex1 = ran.nextInt(usedVehicles);
			int i = ran.nextInt(vehicles[vehIndex1].routes.size() - 2) + 1;
			int vehIndex2;

			do {
				vehIndex2 = ran.nextInt(usedVehicles);
			} while (vehIndex2 == vehIndex1);

			int j = ran.nextInt(vehicles[vehIndex2].routes.size() - 1);

			switch (move) {
			case 0:
				m = singleInsertion(vehIndex1, vehIndex2, i, j);
				break;
			case 1:
				m = swap(vehIndex1, vehIndex2, i, j);
				break;
			case 2:
				m = doubleInsertion(vehIndex1, vehIndex2, i, j);
				break;
			case 3:
				m = swap21(vehIndex1, vehIndex2, i, j);
				break;
			case 4:
				m = cross(vehIndex1, vehIndex2, i, j);
				break;
			default:
				m = new DummyMove();
			}
		} while (!m.isFeasible(this));
		m.applyMove(this);

		cost += m.cost;
		int[] moveVehicles = m.getVehicleIndexes();
		for (int i : moveVehicles)
			cost += vehicles[i].optimizeRoute();
	}

	public Move findBestNeighbor() {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		int VehIndex1, VehIndex2;

		Move BestNeighbor = new DummyMove();

		for (VehIndex1 = 0; VehIndex1 < this.vehicles.length; VehIndex1++) {
			route1 = this.vehicles[VehIndex1].routes;
			int Route1Length = route1.size();

			for (int i = 1; i < (Route1Length - 1); i++) { // Not possible to move depot!
				for (VehIndex2 = 0; VehIndex2 < this.vehicles.length; VehIndex2++) {
					if (VehIndex1 == VehIndex2)
						continue;
					route2 = this.vehicles[VehIndex2].routes;
					int Route2Length = route2.size();

					for (int j = 0; j < (Route2Length - 1); j++) {// Not possible to move after last Depot!
						Move[] Neighbors = new Move[] { singleInsertion(VehIndex1, VehIndex2, i, j),
						        swap(VehIndex1, VehIndex2, i, j), doubleInsertion(VehIndex1, VehIndex2, i, j),
						        swap21(VehIndex1, VehIndex2, i, j), cross(VehIndex1, VehIndex2, i, j) };
						for (Move neigh : Neighbors) {
							if (neigh.compareTo(BestNeighbor) < 0 && neigh.isFeasible(this))
								BestNeighbor = neigh;
						}
					}
				}
			}
		}
		return BestNeighbor;
	}

	public Move singleInsertion(int VehIndexFrom, int VehIndexTo, int index1, int index2) {
		ArrayList<Node> routesFrom = this.vehicles[VehIndexFrom].routes;
		ArrayList<Node> routesTo = this.vehicles[VehIndexTo].routes;

		if (routesFrom.size() == 3 && routesTo.size() == 2)
			return new DummyMove();

		double NeighborCost;

		double MinusCost1 = this.distances[routesFrom.get(index1 - 1).NodeId][routesFrom.get(index1).NodeId];
		double MinusCost2 = this.distances[routesFrom.get(index1).NodeId][routesFrom.get(index1 + 1).NodeId];
		double MinusCost3 = this.distances[routesTo.get(index2).NodeId][routesTo.get(index2 + 1).NodeId];

		double AddedCost1 = this.distances[routesFrom.get(index1 - 1).NodeId][routesFrom.get(index1 + 1).NodeId];
		double AddedCost2 = this.distances[routesTo.get(index2).NodeId][routesFrom.get(index1).NodeId];
		double AddedCost3 = this.distances[routesFrom.get(index1).NodeId][routesTo.get(index2 + 1).NodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((TABU_Matrix[routesFrom.get(index1 - 1).NodeId][routesFrom.get(index1 + 1).NodeId] != 0)
		        || (TABU_Matrix[routesTo.get(index2).NodeId][routesFrom.get(index1).NodeId] != 0)
		        || (TABU_Matrix[routesFrom.get(index1).NodeId][routesTo.get(index2 + 1).NodeId] != 0)) {
			return new DummyMove();
		}

		NeighborCost = AddedCost1 + AddedCost2 + AddedCost3 - MinusCost1 - MinusCost2 - MinusCost3;

		return new SingleInsertionMove(NeighborCost, VehIndexFrom, index1, VehIndexTo, index2);
	}

	public Move swap(int VehIndex1, int VehIndex2, int index1, int index2) {
		if (index2 == 0)
			return new DummyMove();

		ArrayList<Node> route1 = this.vehicles[VehIndex1].routes;
		ArrayList<Node> route2 = this.vehicles[VehIndex2].routes;

		double NeighborCost;

		int Route1Length = route1.size();
		int Route2Length = route2.size();

		// No point in swapping nodes from routes where they are alone
		if (Route1Length == 3 && Route2Length == 3)
			return new DummyMove();

		double MinusCost1 = this.distances[route1.get(index1 - 1).NodeId][route1.get(index1).NodeId];
		double MinusCost2 = this.distances[route1.get(index1).NodeId][route1.get(index1 + 1).NodeId];
		double MinusCost3 = this.distances[route2.get(index2 - 1).NodeId][route2.get(index2).NodeId];
		double MinusCost4 = this.distances[route2.get(index2).NodeId][route2.get(index2 + 1).NodeId];

		double AddedCost1 = this.distances[route1.get(index1 - 1).NodeId][route2.get(index2).NodeId];
		double AddedCost2 = this.distances[route2.get(index2).NodeId][route1.get(index1 + 1).NodeId];
		double AddedCost3 = this.distances[route2.get(index2 - 1).NodeId][route1.get(index1).NodeId];
		double AddedCost4 = this.distances[route1.get(index1).NodeId][route2.get(index2 + 1).NodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((TABU_Matrix[route1.get(index1 - 1).NodeId][route2.get(index2).NodeId] != 0)
		        || (TABU_Matrix[route2.get(index2).NodeId][route1.get(index1 + 1).NodeId] != 0)
		        || (TABU_Matrix[route2.get(index2 - 1).NodeId][route1.get(index1).NodeId] != 0)
		        || (TABU_Matrix[route1.get(index1).NodeId][route2.get(index2 + 1).NodeId] != 0)) {
			return new DummyMove();
		}

		NeighborCost = AddedCost1 + AddedCost2 + AddedCost3 + AddedCost4 - MinusCost1 - MinusCost2 - MinusCost3
		        - MinusCost4;

		return new SwapMove(NeighborCost, VehIndex1, index1, VehIndex2, index2);
	}

	public Move doubleInsertion(int VehIndexFrom, int VehIndexTo, int index1, int index2) {
		ArrayList<Node> routesFrom = this.vehicles[VehIndexFrom].routes;
		ArrayList<Node> routesTo = this.vehicles[VehIndexTo].routes;

		double NeighborCost;

		int RoutFromLength = routesFrom.size();
		int RouteToLength = routesTo.size();

		if ((RoutFromLength == 4 && RouteToLength == 2) || index1 >= RoutFromLength - 2)
			return new DummyMove();

		double MinusCost1 = this.distances[routesFrom.get(index1 - 1).NodeId][routesFrom.get(index1).NodeId];
		double MinusCost2 = this.distances[routesFrom.get(index1 + 1).NodeId][routesFrom.get(index1 + 2).NodeId];
		double MinusCost3 = this.distances[routesTo.get(index2).NodeId][routesTo.get(index2 + 1).NodeId];

		double AddedCost1 = this.distances[routesFrom.get(index1 - 1).NodeId][routesFrom.get(index1 + 2).NodeId];
		double AddedCost2 = this.distances[routesTo.get(index2).NodeId][routesFrom.get(index1).NodeId];
		double AddedCost3 = this.distances[routesFrom.get(index1 + 1).NodeId][routesTo.get(index2 + 1).NodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((TABU_Matrix[routesFrom.get(index1 - 1).NodeId][routesFrom.get(index1 + 2).NodeId] != 0)
		        || (TABU_Matrix[routesTo.get(index2).NodeId][routesFrom.get(index1).NodeId] != 0)
		        || (TABU_Matrix[routesFrom.get(index1 + 1).NodeId][routesTo.get(index2 + 1).NodeId] != 0)) {
			return new DummyMove();
		}

		NeighborCost = AddedCost1 + AddedCost2 + AddedCost3 - MinusCost1 - MinusCost2 - MinusCost3;

		return new DoubleInsertionMove(NeighborCost, VehIndexFrom, index1, VehIndexTo, index2);
	}

	public Move swap21(int VehIndex1, int VehIndex2, int index1, int index2) {
		ArrayList<Node> route1 = this.vehicles[VehIndex1].routes;
		ArrayList<Node> route2 = this.vehicles[VehIndex2].routes;

		double NeighborCost;

		int Route1Length = route1.size();
		int Route2Length = route2.size();

		if (index2 == 0 || index1 >= Route1Length - 2)
			return new DummyMove();

		// No point in swapping nodes from routes where they are alone
		if (Route1Length == 4 && Route2Length == 3)
			return new DummyMove();

		double MinusCost1 = this.distances[route1.get(index1 - 1).NodeId][route1.get(index1).NodeId];
		double MinusCost2 = this.distances[route1.get(index1 + 1).NodeId][route1.get(index1 + 2).NodeId];
		double MinusCost3 = this.distances[route2.get(index2 - 1).NodeId][route2.get(index2).NodeId];
		double MinusCost4 = this.distances[route2.get(index2).NodeId][route2.get(index2 + 1).NodeId];

		double AddedCost1 = this.distances[route1.get(index1 - 1).NodeId][route2.get(index2).NodeId];
		double AddedCost2 = this.distances[route2.get(index2).NodeId][route1.get(index1 + 2).NodeId];
		double AddedCost3 = this.distances[route2.get(index2 - 1).NodeId][route1.get(index1).NodeId];
		double AddedCost4 = this.distances[route1.get(index1 + 1).NodeId][route2.get(index2 + 1).NodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((TABU_Matrix[route1.get(index1 - 1).NodeId][route2.get(index2).NodeId] != 0)
		        || (TABU_Matrix[route2.get(index2).NodeId][route1.get(index1 + 2).NodeId] != 0)
		        || (TABU_Matrix[route2.get(index2 - 1).NodeId][route1.get(index1).NodeId] != 0)
		        || (TABU_Matrix[route1.get(index1 + 1).NodeId][route2.get(index2 + 1).NodeId] != 0)) {
			return new DummyMove();
		}

		NeighborCost = AddedCost1 + AddedCost2 + AddedCost3 + AddedCost4 - MinusCost1 - MinusCost2 - MinusCost3
		        - MinusCost4;

		return new Swap21Move(NeighborCost, VehIndex1, index1, VehIndex2, index2);
	}

	public Move cross(int VehIndex1, int VehIndex2, int index1, int index2) {
		ArrayList<Node> route1 = this.vehicles[VehIndex1].routes;
		ArrayList<Node> route2 = this.vehicles[VehIndex2].routes;

		double NeighborCost;

		int Route1Length = route1.size();
		int Route2Length = route2.size();

		if ((index1 >= Route1Length - 2) && (index2 >= Route2Length - 2))
			return new DummyMove();

		double MinusCost1 = this.distances[route1.get(index1).NodeId][route1.get(index1 + 1).NodeId];
		double MinusCost2 = this.distances[route2.get(index2).NodeId][route2.get(index2 + 1).NodeId];

		double AddedCost1 = this.distances[route1.get(index1).NodeId][route2.get(index2 + 1).NodeId];
		double AddedCost2 = this.distances[route2.get(index2).NodeId][route1.get(index1 + 1).NodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((TABU_Matrix[route1.get(index1).NodeId][route2.get(index2 + 1).NodeId] != 0)
		        || (TABU_Matrix[route2.get(index2).NodeId][route1.get(index1 + 1).NodeId] != 0)) {
			return new DummyMove();
		}

		NeighborCost = AddedCost1 + AddedCost2 - MinusCost1 - MinusCost2;

		return new CrossMove(NeighborCost, VehIndex1, index1, VehIndex2, index2);
	}
}
