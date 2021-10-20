package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class SingleInsertionMove extends Move {
	public SingleInsertionMove(double cost, Vehicle SrcVeh, int SrcRouteIndex, Vehicle DstVeh, int DstRouteIndex) {
		super(cost, SrcVeh, SrcRouteIndex, DstVeh, DstRouteIndex);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		ArrayList<Node> routesTo;

		routesFrom = vehicle1.routes;
		routesTo = vehicle2.routes;

		if (routesTo.size() == 2) {
			s.emptyVehicles.remove(vehicle2);
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

		s.tabuList[NodeIDBefore][SwapNode.NodeId] = s.tabuTenure + randomDelay1;
		s.tabuList[SwapNode.NodeId][NodeIDAfter] = s.tabuTenure + randomDelay2;
		s.tabuList[NodeID_F][NodeID_G] = s.tabuTenure + randomDelay3;

		vehicle1.removeNode(route1NodeIndex);
		vehicle2.addNode(SwapNode, route2NodeIndex + 1);

		if (vehicle1.routes.size() == 2) {
			s.emptyVehicles.add(vehicle1);
		}

		if (needsTransfer) {
			transfer(s);
		}
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		routesFrom = vehicle1.routes;

		int MovingNodeDemand[] = routesFrom.get(route1NodeIndex).demands;

		firstFeasible = true;
		secondFeasible = vehicle2.checkIfFits(MovingNodeDemand);
		return secondFeasible;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		ArrayList<Node> routesFrom = vehicle1.routes;

		return transferFeasible(s, List.of(routesFrom.get(route1NodeIndex)), Collections.emptyList());
	}
}
