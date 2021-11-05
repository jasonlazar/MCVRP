package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class DoubleInsertionMove extends Move {
	public DoubleInsertionMove(double cost, Vehicle src, int sri, Vehicle dst, int dri) {
		super(cost, src, sri, dst, dri);
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

		Node swapNode1 = routesFrom.get(route1NodeIndex);
		Node swapNode2 = routesFrom.get(route1NodeIndex + 1);

		int nodeIDBefore = routesFrom.get(route1NodeIndex - 1).nodeId;
		int nodeIDAfter = routesFrom.get(route1NodeIndex + 2).nodeId;
		int nodeID_F = routesTo.get(route2NodeIndex).nodeId;
		int nodeID_G = routesTo.get(route2NodeIndex + 1).nodeId;

		Random tabuRan = new Random();
		int randomDelay1 = tabuRan.nextInt(5);
		int randomDelay2 = tabuRan.nextInt(5);
		int randomDelay3 = tabuRan.nextInt(5);

		s.tabuList[nodeIDBefore][swapNode1.nodeId] = s.tabuTenure + randomDelay1;
		s.tabuList[swapNode2.nodeId][nodeIDAfter] = s.tabuTenure + randomDelay2;
		s.tabuList[nodeID_F][nodeID_G] = s.tabuTenure + randomDelay3;

		vehicle1.removeNode(route1NodeIndex);
		vehicle1.removeNode(route1NodeIndex);
		vehicle2.addNode(swapNode2, route2NodeIndex + 1);
		vehicle2.addNode(swapNode1, route2NodeIndex + 1);

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
		int movingNodeDemand[];
		routesFrom = vehicle1.routes;

		int[] firstDemands = routesFrom.get(route1NodeIndex).demands;
		int[] secondDemands = routesFrom.get(route1NodeIndex + 1).demands;
		movingNodeDemand = new int[firstDemands.length + secondDemands.length];
		System.arraycopy(firstDemands, 0, movingNodeDemand, 0, firstDemands.length);
		System.arraycopy(secondDemands, 0, movingNodeDemand, firstDemands.length, secondDemands.length);

		firstFeasible = true;
		secondFeasible = vehicle2.checkIfFits(movingNodeDemand);
		return secondFeasible;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		ArrayList<Node> routesFrom = vehicle1.routes;
		Node moved1 = routesFrom.get(route1NodeIndex);
		Node moved2 = routesFrom.get(route1NodeIndex + 1);

		return transferFeasible(s, List.of(moved1, moved2), Collections.emptyList());
	}
}
