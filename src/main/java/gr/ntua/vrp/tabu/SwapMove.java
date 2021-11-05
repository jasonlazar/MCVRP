package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class SwapMove extends Move {
	public SwapMove(double cost, Vehicle SwapVeh1, int SwapRoute1Index, Vehicle SwapVeh2, int SwapRoute2Index) {
		super(cost, SwapVeh1, SwapRoute1Index, SwapVeh2, SwapRoute2Index);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		Node swapNode1 = route1.get(route1NodeIndex);
		Node swapNode2 = route2.get(route2NodeIndex);

		int nodeIDBefore1 = route1.get(route1NodeIndex - 1).nodeId;
		int nodeIDAfter1 = route1.get(route1NodeIndex + 1).nodeId;
		int nodeIDBefore2 = route2.get(route2NodeIndex - 1).nodeId;
		int nodeIDAfter2 = route2.get(route2NodeIndex + 1).nodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);
		int randomDelay4 = TabuRan.nextInt(5);

		s.tabuList[nodeIDBefore1][swapNode1.nodeId] = s.tabuTenure + randomDelay1;
		s.tabuList[swapNode1.nodeId][nodeIDAfter1] = s.tabuTenure + randomDelay2;
		s.tabuList[nodeIDBefore2][swapNode2.nodeId] = s.tabuTenure + randomDelay3;
		s.tabuList[swapNode2.nodeId][nodeIDAfter2] = s.tabuTenure + randomDelay4;

		vehicle1.removeNode(route1NodeIndex);
		vehicle1.addNode(swapNode2, route1NodeIndex);
		vehicle2.removeNode(route2NodeIndex);
		vehicle2.addNode(swapNode1, route2NodeIndex);

		if (needsTransfer)
			transfer(s);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		Node firstNode = route1.get(route1NodeIndex);
		Node secondNode = route2.get(route2NodeIndex);
		int firstNodeDemand[] = route1.get(route1NodeIndex).demands;
		int secondNodeDemand[] = route2.get(route2NodeIndex).demands;

		firstFeasible = vehicle1.checkIfFits(secondNodeDemand, List.of(firstNode));
		secondFeasible = vehicle2.checkIfFits(firstNodeDemand, List.of(secondNode));
		return firstFeasible && secondFeasible;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1 = vehicle1.routes;
		ArrayList<Node> route2 = vehicle2.routes;
		Node swapNode1 = route1.get(route1NodeIndex);
		Node swapNode2 = route2.get(route2NodeIndex);

		return transferFeasible(s, List.of(swapNode1), List.of(swapNode2));
	}
}
