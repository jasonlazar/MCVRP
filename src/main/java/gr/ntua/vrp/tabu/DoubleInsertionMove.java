package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class DoubleInsertionMove extends Move {
	public DoubleInsertionMove(double cost, int src, int sri, int dst, int dri) {
		super(cost, src, sri, dst, dri);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		ArrayList<Node> routesTo;

		Vehicle[] vehicles = s.getVehicles();

		routesFrom = vehicles[route1Index].routes;
		routesTo = vehicles[route2Index].routes;

		if (routesTo.size() == 2) {
			s.emptyVehicles.remove(route2Index);
		}

		Node SwapNode1 = routesFrom.get(route1NodeIndex);
		Node SwapNode2 = routesFrom.get(route1NodeIndex + 1);

		int NodeIDBefore = routesFrom.get(route1NodeIndex - 1).NodeId;
		int NodeIDAfter = routesFrom.get(route1NodeIndex + 2).NodeId;
		int NodeID_F = routesTo.get(route2NodeIndex).NodeId;
		int NodeID_G = routesTo.get(route2NodeIndex + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);

		s.TABU_Matrix[NodeIDBefore][SwapNode1.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode2.NodeId][NodeIDAfter] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeID_F][NodeID_G] = s.TABU_Horizon + randomDelay3;

		vehicles[route1Index].removeNode(route1NodeIndex);
		vehicles[route1Index].removeNode(route1NodeIndex);
		vehicles[route2Index].addNode(SwapNode2, route2NodeIndex + 1);
		vehicles[route2Index].addNode(SwapNode1, route2NodeIndex + 1);

		if (vehicles[route1Index].routes.size() == 2) {
			s.emptyVehicles.add(route1Index);
		}

		if (needsTransfer) {
			transfer(s);
		}
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		int MovingNodeDemand[];

		Vehicle[] vehicles = s.getVehicles();
		routesFrom = vehicles[route1Index].routes;

		int[] firstDemands = routesFrom.get(route1NodeIndex).demands;
		int[] secondDemands = routesFrom.get(route1NodeIndex + 1).demands;
		MovingNodeDemand = new int[firstDemands.length + secondDemands.length];
		System.arraycopy(firstDemands, 0, MovingNodeDemand, 0, firstDemands.length);
		System.arraycopy(secondDemands, 0, MovingNodeDemand, firstDemands.length, secondDemands.length);

		firstFeasible = true;
		secondFeasible = vehicles[route2Index].checkIfFits(MovingNodeDemand);
		return secondFeasible;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		Vehicle[] vehicles = s.getVehicles();
		ArrayList<Node> routesFrom = vehicles[route1Index].routes;
		Node moved1 = routesFrom.get(route1NodeIndex);
		Node moved2 = routesFrom.get(route1NodeIndex + 1);

		return transferFeasible(s, List.of(moved1, moved2), Collections.emptyList());
	}
}
