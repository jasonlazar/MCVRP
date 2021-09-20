package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class SwapMove extends Move {
	public SwapMove(double cost, int SwapRoute1, int SwapRoute1Index, int SwapRoute2, int SwapRoute2Index) {
		super(cost, SwapRoute1, SwapRoute1Index, SwapRoute2, SwapRoute2Index);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[route1Index].routes;
		route2 = vehicles[route2Index].routes;

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

		s.TABU_Matrix[NodeIDBefore1][SwapNode1.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode1.NodeId][NodeIDAfter1] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeIDBefore2][SwapNode2.NodeId] = s.TABU_Horizon + randomDelay3;
		s.TABU_Matrix[SwapNode2.NodeId][NodeIDAfter2] = s.TABU_Horizon + randomDelay4;

		vehicles[route1Index].removeNode(route1NodeIndex);
		vehicles[route1Index].addNode(SwapNode2, route1NodeIndex);
		vehicles[route2Index].removeNode(route2NodeIndex);
		vehicles[route2Index].addNode(SwapNode1, route2NodeIndex);

		if (needsTransfer)
			transfer(s);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[route1Index].routes;
		route2 = vehicles[route2Index].routes;

		Node FirstNode = route1.get(route1NodeIndex);
		Node SecondNode = route2.get(route2NodeIndex);
		int FirstNodeDemand[] = route1.get(route1NodeIndex).demands;
		int SecondNodeDemand[] = route2.get(route2NodeIndex).demands;

		firstFeasible = vehicles[route1Index].checkIfFits(SecondNodeDemand, List.of(FirstNode));
		secondFeasible = vehicles[route2Index].checkIfFits(FirstNodeDemand, List.of(SecondNode));
		return firstFeasible && secondFeasible;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		Vehicle[] vehicles = s.getVehicles();
		Vehicle veh1 = vehicles[route1Index];
		Vehicle veh2 = vehicles[route2Index];
		ArrayList<Node> route1 = veh1.routes;
		ArrayList<Node> route2 = veh2.routes;
		Node swapNode1 = route1.get(route1NodeIndex);
		Node swapNode2 = route2.get(route2NodeIndex);

		return transferFeasible(s, List.of(swapNode1), List.of(swapNode2));
	}
}
