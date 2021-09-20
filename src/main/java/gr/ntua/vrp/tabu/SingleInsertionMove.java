package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class SingleInsertionMove extends Move {
	public SingleInsertionMove(double cost, int SrcRoute, int SrcRouteIndex, int DstRoute, int DstRouteIndex) {
		super(cost, SrcRoute, SrcRouteIndex, DstRoute, DstRouteIndex);
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

		Node SwapNode = routesFrom.get(route1NodeIndex);

		int NodeIDBefore = routesFrom.get(route1NodeIndex - 1).NodeId;
		int NodeIDAfter = routesFrom.get(route1NodeIndex + 1).NodeId;
		int NodeID_F = routesTo.get(route2NodeIndex).NodeId;
		int NodeID_G = routesTo.get(route2NodeIndex + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);

		s.TABU_Matrix[NodeIDBefore][SwapNode.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode.NodeId][NodeIDAfter] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeID_F][NodeID_G] = s.TABU_Horizon + randomDelay3;

		vehicles[route1Index].removeNode(route1NodeIndex);
		vehicles[route2Index].addNode(SwapNode, route2NodeIndex + 1);

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
		Vehicle[] vehicles = s.getVehicles();
		routesFrom = vehicles[route1Index].routes;

		int MovingNodeDemand[] = routesFrom.get(route1NodeIndex).demands;

		firstFeasible = true;
		secondFeasible = vehicles[route2Index].checkIfFits(MovingNodeDemand);
		return secondFeasible;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		Vehicle[] vehicles = s.getVehicles();
		ArrayList<Node> routesFrom = vehicles[route1Index].routes;

		return transferFeasible(s, List.of(routesFrom.get(route1NodeIndex)), Collections.emptyList());
	}
}
