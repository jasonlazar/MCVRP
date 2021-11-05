package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class Swap21Move extends Move {
	public Swap21Move(double cost, Vehicle SwapVeh1, int SwapRoute1Index, Vehicle SwapVeh2, int SwapRoute2Index) {
		super(cost, SwapVeh1, SwapRoute1Index, SwapVeh2, SwapRoute2Index);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		Node swapNode11 = route1.get(route1NodeIndex);
		Node swapNode12 = route1.get(route1NodeIndex + 1);
		Node swapNode2 = route2.get(route2NodeIndex);

		int nodeIDBefore1 = route1.get(route1NodeIndex - 1).nodeId;
		int nodeIDAfter1 = route1.get(route1NodeIndex + 2).nodeId;
		int nodeIDBefore2 = route2.get(route2NodeIndex - 1).nodeId;
		int nodeIDAfter2 = route2.get(route2NodeIndex + 1).nodeId;

		Random tabuRan = new Random();
		int randomDelay1 = tabuRan.nextInt(5);
		int randomDelay2 = tabuRan.nextInt(5);
		int randomDelay3 = tabuRan.nextInt(5);
		int randomDelay4 = tabuRan.nextInt(5);

		s.tabuList[nodeIDBefore1][swapNode11.nodeId] = s.tabuTenure + randomDelay1;
		s.tabuList[swapNode12.nodeId][nodeIDAfter1] = s.tabuTenure + randomDelay2;
		s.tabuList[nodeIDBefore2][swapNode2.nodeId] = s.tabuTenure + randomDelay3;
		s.tabuList[swapNode2.nodeId][nodeIDAfter2] = s.tabuTenure + randomDelay4;

		vehicle1.removeNode(route1NodeIndex);
		vehicle1.removeNode(route1NodeIndex);
		vehicle1.addNode(swapNode2, route1NodeIndex);
		vehicle2.removeNode(route2NodeIndex);
		vehicle2.addNode(swapNode12, route2NodeIndex);
		vehicle2.addNode(swapNode11, route2NodeIndex);

		if (needsTransfer)
			transfer(s);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		Node swapNode11 = route1.get(route1NodeIndex);
		Node swapNode12 = route1.get(route1NodeIndex + 1);
		Node swapNode2 = route2.get(route2NodeIndex);
		int[] swapNode11Demand = swapNode11.demands;
		int[] swapNode12Demand = swapNode12.demands;
		int[] swapNode2Demand = swapNode2.demands;

		int[] movingFrom1Demand = new int[swapNode11Demand.length + swapNode12Demand.length];
		System.arraycopy(swapNode11Demand, 0, movingFrom1Demand, 0, swapNode11Demand.length);
		System.arraycopy(swapNode12Demand, 0, movingFrom1Demand, swapNode11Demand.length, swapNode12Demand.length);

		firstFeasible = vehicle1.checkIfFits(swapNode2Demand, List.of(swapNode11, swapNode12));
		secondFeasible = vehicle2.checkIfFits(movingFrom1Demand, List.of(swapNode2));
		return firstFeasible && secondFeasible;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1 = vehicle1.routes;
		ArrayList<Node> route2 = vehicle2.routes;
		Node swapNode11 = route1.get(route1NodeIndex);
		Node swapNode12 = route1.get(route1NodeIndex + 1);
		Node swapNode2 = route2.get(route2NodeIndex);

		return transferFeasible(s, List.of(swapNode11, swapNode12), List.of(swapNode2));
	}
}
