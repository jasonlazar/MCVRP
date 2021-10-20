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

		Node SwapNode1 = route1.get(route1NodeIndex);
		Node SwapNode2 = route2.get(route2NodeIndex);

		int NodeIDBefore1 = route1.get(route1NodeIndex - 1).NodeId;
		int NodeIDAfter1 = route1.get(route1NodeIndex + 1).NodeId;
		int NodeIDBefore2 = route2.get(route2NodeIndex - 1).NodeId;
		int NodeIDAfter2 = route2.get(route2NodeIndex + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);
		int randomDelay4 = TabuRan.nextInt(5);

		s.tabuList[NodeIDBefore1][SwapNode1.NodeId] = s.tabuTenure + randomDelay1;
		s.tabuList[SwapNode1.NodeId][NodeIDAfter1] = s.tabuTenure + randomDelay2;
		s.tabuList[NodeIDBefore2][SwapNode2.NodeId] = s.tabuTenure + randomDelay3;
		s.tabuList[SwapNode2.NodeId][NodeIDAfter2] = s.tabuTenure + randomDelay4;

		vehicle1.removeNode(route1NodeIndex);
		vehicle1.addNode(SwapNode2, route1NodeIndex);
		vehicle2.removeNode(route2NodeIndex);
		vehicle2.addNode(SwapNode1, route2NodeIndex);

		if (needsTransfer)
			transfer(s);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		Node FirstNode = route1.get(route1NodeIndex);
		Node SecondNode = route2.get(route2NodeIndex);
		int FirstNodeDemand[] = route1.get(route1NodeIndex).demands;
		int SecondNodeDemand[] = route2.get(route2NodeIndex).demands;

		firstFeasible = vehicle1.checkIfFits(SecondNodeDemand, List.of(FirstNode));
		secondFeasible = vehicle2.checkIfFits(FirstNodeDemand, List.of(SecondNode));
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
