package gr.ntua.vrp.tabu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Solver;
import gr.ntua.vrp.VRPRunner;
import gr.ntua.vrp.Vehicle;
import gr.ntua.vrp.init.GreedySolver;
import gr.ntua.vrp.init.InitFromRoutesSolver;

public class TabuSearchSolver extends Solver {
	final int tabuTenure;
	final int tabuList[][];
	EmptyVehicleSet emptyVehicles;
	private final int iterations;
	private final int restarts;
	private final Vehicle[] bestSolutionVehicles;

	private double bestSolutionCost;

	public TabuSearchSolver(VRPRunner jct) throws IOException {
		super(jct);
		this.tabuTenure = jct.tenure;
		this.iterations = jct.iterations;
		this.restarts = jct.restarts;

		this.bestSolutionVehicles = new Vehicle[this.noOfVehicles];

		for (int i = 0; i < this.noOfVehicles; i++) {
			this.bestSolutionVehicles[i] = this.vehicles[i].makeCopy();
		}

		Solver initSolver = (jct.initFile == null) ? new GreedySolver(this) : new InitFromRoutesSolver(jct);
		initSolver.solve();
		this.vehicles = initSolver.getVehicles();
		this.cost = initSolver.getCost();

		int dimension = this.distances[1].length;
		this.tabuList = new int[dimension + 1][dimension + 1];

		this.emptyVehicles = new EmptyVehicleSet();
		for (Vehicle veh : vehicles) {
			if (veh.routes.size() == 2)
				emptyVehicles.add(veh);
		}
	}

	public TabuSearchSolver solve() {
		int restart_number = 0;
		int iteration_number = 0;

		Move BestMove;

		for (Vehicle v : vehicles) {
			this.cost += v.optimizeRoute();
		}

		this.bestSolutionCost = this.cost;

		while (true) {
			BestMove = findBestNeighbor();

			for (int o = 0; o < tabuList[0].length; o++) {
				for (int p = 0; p < tabuList[0].length; p++) {
					if (tabuList[o][p] > 0) {
						tabuList[o][p]--;
					}
				}
			}

			BestMove.applyMove(this);

			this.cost += BestMove.cost;

			Vehicle[] MoveVehicles = BestMove.getVehicles();
			for (Vehicle v : MoveVehicles)
				this.cost += v.optimizeRoute();

			if (this.cost < this.bestSolutionCost) {
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

		this.vehicles = this.bestSolutionVehicles;
		this.cost = this.bestSolutionCost;

		return this;
	}

	private void SaveBestSolution() {
		this.bestSolutionCost = this.cost;
		for (int j = 0; j < this.noOfVehicles; j++) {
			this.bestSolutionVehicles[j].routes.clear();
			if (!this.vehicles[j].routes.isEmpty()) {
				int RoutSize = this.vehicles[j].routes.size();
				for (int k = 0; k < RoutSize; k++) {
					Node n = this.vehicles[j].routes.get(k);
					this.bestSolutionVehicles[j].routes.add(n);
				}
			}
		}
	}

	private void restoreBest() {
		cost = bestSolutionCost;
		for (int j = 0; j < noOfVehicles; j++) {
			while (!vehicles[j].routes.isEmpty())
				vehicles[j].removeNode(0);
			if (!bestSolutionVehicles[j].routes.isEmpty()) {
				int routSize = bestSolutionVehicles[j].routes.size();
				for (int k = 0; k < routSize; k++) {
					Node n = bestSolutionVehicles[j].routes.get(k);
					vehicles[j].appendNode(n);
				}
			}
		}
	}

	private void applyRandomMove() {
		Random ran = new Random();
		Move m;
		do {
			MoveType move = MoveType.randomMove();
			int vehIndex1, vehIndex2;
			do {
				vehIndex1 = ran.nextInt(noOfVehicles);
			} while (vehicles[vehIndex1].routes.size() <= 2);
			int i = ran.nextInt(vehicles[vehIndex1].routes.size() - 2) + 1;

			do {
				vehIndex2 = ran.nextInt(noOfVehicles);
			} while ((vehIndex2 == vehIndex1) || (vehicles[vehIndex2].routes.size() <= 2));
			int j = ran.nextInt(vehicles[vehIndex2].routes.size() - 1);

			switch (move) {
			case SINGLE_INSERTION:
				m = singleInsertion(vehIndex1, vehIndex2, i, j);
				break;
			case SWAP:
				m = swap(vehIndex1, vehIndex2, i, j);
				break;
			case DOUBLE_INSERTION:
				m = doubleInsertion(vehIndex1, vehIndex2, i, j);
				break;
			case SWAP21:
				m = swap21(vehIndex1, vehIndex2, i, j);
				break;
			case CROSS:
				m = cross(vehIndex1, vehIndex2, i, j);
				break;
			default:
				m = new DummyMove();
			}
		} while (!m.isFeasible(this));
		m.applyMove(this);

		cost += m.cost;
		Vehicle[] moveVehicles = m.getVehicles();
		for (Vehicle v : moveVehicles)
			cost += v.optimizeRoute();

		if (cost < bestSolutionCost)
			SaveBestSolution();
	}

	public Move findBestNeighbor() {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		int vehIndex1, vehIndex2;

		Move bestNeighbor = new DummyMove();

		for (vehIndex1 = 0; vehIndex1 < this.vehicles.length; vehIndex1++) {
			route1 = this.vehicles[vehIndex1].routes;
			int route1Length = route1.size();

			for (int i = 1; i < (route1Length - 1); i++) { // Not possible to move depot!
				for (vehIndex2 = 0; vehIndex2 < this.vehicles.length; vehIndex2++) {
					if (vehIndex1 == vehIndex2)
						continue;
					route2 = this.vehicles[vehIndex2].routes;
					int route2Length = route2.size();

					for (int j = 0; j < (route2Length - 1); j++) {// Not possible to move after last Depot!
						Move[] neighbors = new Move[] { singleInsertion(vehIndex1, vehIndex2, i, j),
						        swap(vehIndex1, vehIndex2, i, j), doubleInsertion(vehIndex1, vehIndex2, i, j),
						        swap21(vehIndex1, vehIndex2, i, j), cross(vehIndex1, vehIndex2, i, j) };
						for (Move neigh : neighbors) {
							if (neigh.compareTo(bestNeighbor) < 0 && neigh.isFeasible(this))
								bestNeighbor = neigh;
							else if (neigh.compareTo(bestNeighbor) < 0 && neigh.transferFeasible(this)) {
								bestNeighbor = neigh;
							}
						}
					}
				}
			}
		}
		return bestNeighbor;
	}

	public Move singleInsertion(int VehIndexFrom, int VehIndexTo, int index1, int index2) {
		ArrayList<Node> routesFrom = this.vehicles[VehIndexFrom].routes;
		ArrayList<Node> routesTo = this.vehicles[VehIndexTo].routes;

		if (routesFrom.size() == 3 && routesTo.size() == 2)
			return new DummyMove();

		double neighborCost;

		double minusCost1 = this.distances[routesFrom.get(index1 - 1).nodeId][routesFrom.get(index1).nodeId];
		double minusCost2 = this.distances[routesFrom.get(index1).nodeId][routesFrom.get(index1 + 1).nodeId];
		double minusCost3 = this.distances[routesTo.get(index2).nodeId][routesTo.get(index2 + 1).nodeId];

		double addedCost1 = this.distances[routesFrom.get(index1 - 1).nodeId][routesFrom.get(index1 + 1).nodeId];
		double addedCost2 = this.distances[routesTo.get(index2).nodeId][routesFrom.get(index1).nodeId];
		double addedCost3 = this.distances[routesFrom.get(index1).nodeId][routesTo.get(index2 + 1).nodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((tabuList[routesFrom.get(index1 - 1).nodeId][routesFrom.get(index1 + 1).nodeId] != 0)
		        || (tabuList[routesTo.get(index2).nodeId][routesFrom.get(index1).nodeId] != 0)
		        || (tabuList[routesFrom.get(index1).nodeId][routesTo.get(index2 + 1).nodeId] != 0)) {
			return new DummyMove();
		}

		neighborCost = addedCost1 + addedCost2 + addedCost3 - minusCost1 - minusCost2 - minusCost3;

		return new SingleInsertionMove(neighborCost, vehicles[VehIndexFrom], index1, vehicles[VehIndexTo], index2);
	}

	public Move swap(int VehIndex1, int VehIndex2, int index1, int index2) {
		if (index2 == 0)
			return new DummyMove();

		ArrayList<Node> route1 = this.vehicles[VehIndex1].routes;
		ArrayList<Node> route2 = this.vehicles[VehIndex2].routes;

		double neighborCost;

		int Route1Length = route1.size();
		int Route2Length = route2.size();

		// No point in swapping nodes from routes where they are alone
		if (Route1Length == 3 && Route2Length == 3)
			return new DummyMove();

		double minusCost1 = this.distances[route1.get(index1 - 1).nodeId][route1.get(index1).nodeId];
		double minusCost2 = this.distances[route1.get(index1).nodeId][route1.get(index1 + 1).nodeId];
		double minusCost3 = this.distances[route2.get(index2 - 1).nodeId][route2.get(index2).nodeId];
		double minusCost4 = this.distances[route2.get(index2).nodeId][route2.get(index2 + 1).nodeId];

		double addedCost1 = this.distances[route1.get(index1 - 1).nodeId][route2.get(index2).nodeId];
		double addedCost2 = this.distances[route2.get(index2).nodeId][route1.get(index1 + 1).nodeId];
		double addedCost3 = this.distances[route2.get(index2 - 1).nodeId][route1.get(index1).nodeId];
		double addedCost4 = this.distances[route1.get(index1).nodeId][route2.get(index2 + 1).nodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((tabuList[route1.get(index1 - 1).nodeId][route2.get(index2).nodeId] != 0)
		        || (tabuList[route2.get(index2).nodeId][route1.get(index1 + 1).nodeId] != 0)
		        || (tabuList[route2.get(index2 - 1).nodeId][route1.get(index1).nodeId] != 0)
		        || (tabuList[route1.get(index1).nodeId][route2.get(index2 + 1).nodeId] != 0)) {
			return new DummyMove();
		}

		neighborCost = addedCost1 + addedCost2 + addedCost3 + addedCost4 - minusCost1 - minusCost2 - minusCost3
		        - minusCost4;

		return new SwapMove(neighborCost, vehicles[VehIndex1], index1, vehicles[VehIndex2], index2);
	}

	public Move doubleInsertion(int VehIndexFrom, int VehIndexTo, int index1, int index2) {
		ArrayList<Node> routesFrom = this.vehicles[VehIndexFrom].routes;
		ArrayList<Node> routesTo = this.vehicles[VehIndexTo].routes;

		double neighborCost;

		int RoutFromLength = routesFrom.size();
		int RouteToLength = routesTo.size();

		if ((RoutFromLength == 4 && RouteToLength == 2) || index1 >= RoutFromLength - 2)
			return new DummyMove();

		double minusCost1 = this.distances[routesFrom.get(index1 - 1).nodeId][routesFrom.get(index1).nodeId];
		double minusCost2 = this.distances[routesFrom.get(index1 + 1).nodeId][routesFrom.get(index1 + 2).nodeId];
		double minusCost3 = this.distances[routesTo.get(index2).nodeId][routesTo.get(index2 + 1).nodeId];

		double addedCost1 = this.distances[routesFrom.get(index1 - 1).nodeId][routesFrom.get(index1 + 2).nodeId];
		double addedCost2 = this.distances[routesTo.get(index2).nodeId][routesFrom.get(index1).nodeId];
		double addedCost3 = this.distances[routesFrom.get(index1 + 1).nodeId][routesTo.get(index2 + 1).nodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((tabuList[routesFrom.get(index1 - 1).nodeId][routesFrom.get(index1 + 2).nodeId] != 0)
		        || (tabuList[routesTo.get(index2).nodeId][routesFrom.get(index1).nodeId] != 0)
		        || (tabuList[routesFrom.get(index1 + 1).nodeId][routesTo.get(index2 + 1).nodeId] != 0)) {
			return new DummyMove();
		}

		neighborCost = addedCost1 + addedCost2 + addedCost3 - minusCost1 - minusCost2 - minusCost3;

		return new DoubleInsertionMove(neighborCost, vehicles[VehIndexFrom], index1, vehicles[VehIndexTo], index2);
	}

	public Move swap21(int VehIndex1, int VehIndex2, int index1, int index2) {
		ArrayList<Node> route1 = this.vehicles[VehIndex1].routes;
		ArrayList<Node> route2 = this.vehicles[VehIndex2].routes;

		double neighborCost;

		int Route1Length = route1.size();
		int Route2Length = route2.size();

		if (index2 == 0 || index1 >= Route1Length - 2)
			return new DummyMove();

		// No point in swapping nodes from routes where they are alone
		if (Route1Length == 4 && Route2Length == 3)
			return new DummyMove();

		double minusCost1 = this.distances[route1.get(index1 - 1).nodeId][route1.get(index1).nodeId];
		double minusCost2 = this.distances[route1.get(index1 + 1).nodeId][route1.get(index1 + 2).nodeId];
		double minusCost3 = this.distances[route2.get(index2 - 1).nodeId][route2.get(index2).nodeId];
		double minusCost4 = this.distances[route2.get(index2).nodeId][route2.get(index2 + 1).nodeId];

		double addedCost1 = this.distances[route1.get(index1 - 1).nodeId][route2.get(index2).nodeId];
		double addedCost2 = this.distances[route2.get(index2).nodeId][route1.get(index1 + 2).nodeId];
		double addedCost3 = this.distances[route2.get(index2 - 1).nodeId][route1.get(index1).nodeId];
		double addedCost4 = this.distances[route1.get(index1 + 1).nodeId][route2.get(index2 + 1).nodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((tabuList[route1.get(index1 - 1).nodeId][route2.get(index2).nodeId] != 0)
		        || (tabuList[route2.get(index2).nodeId][route1.get(index1 + 2).nodeId] != 0)
		        || (tabuList[route2.get(index2 - 1).nodeId][route1.get(index1).nodeId] != 0)
		        || (tabuList[route1.get(index1 + 1).nodeId][route2.get(index2 + 1).nodeId] != 0)) {
			return new DummyMove();
		}

		neighborCost = addedCost1 + addedCost2 + addedCost3 + addedCost4 - minusCost1 - minusCost2 - minusCost3
		        - minusCost4;

		return new Swap21Move(neighborCost, vehicles[VehIndex1], index1, vehicles[VehIndex2], index2);
	}

	public Move cross(int VehIndex1, int VehIndex2, int index1, int index2) {
		ArrayList<Node> route1 = this.vehicles[VehIndex1].routes;
		ArrayList<Node> route2 = this.vehicles[VehIndex2].routes;

		double neighborCost;

		int Route1Length = route1.size();
		int Route2Length = route2.size();

		if ((index1 >= Route1Length - 2) && (index2 >= Route2Length - 2))
			return new DummyMove();

		double minusCost1 = this.distances[route1.get(index1).nodeId][route1.get(index1 + 1).nodeId];
		double minusCost2 = this.distances[route2.get(index2).nodeId][route2.get(index2 + 1).nodeId];

		double addedCost1 = this.distances[route1.get(index1).nodeId][route2.get(index2 + 1).nodeId];
		double addedCost2 = this.distances[route2.get(index2).nodeId][route1.get(index1 + 1).nodeId];

		// Check if the move is a Tabu! - If it is Tabu break
		if ((tabuList[route1.get(index1).nodeId][route2.get(index2 + 1).nodeId] != 0)
		        || (tabuList[route2.get(index2).nodeId][route1.get(index1 + 1).nodeId] != 0)) {
			return new DummyMove();
		}

		neighborCost = addedCost1 + addedCost2 - minusCost1 - minusCost2;

		return new CrossMove(neighborCost, vehicles[VehIndex1], index1, vehicles[VehIndex2], index2);
	}
}
