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

		Node SwapNode11 = route1.get(route1NodeIndex);
		Node SwapNode12 = route1.get(route1NodeIndex + 1);
		Node SwapNode2 = route2.get(route2NodeIndex);

		int NodeIDBefore1 = route1.get(route1NodeIndex - 1).NodeId;
		int NodeIDAfter1 = route1.get(route1NodeIndex + 2).NodeId;
		int NodeIDBefore2 = route2.get(route2NodeIndex - 1).NodeId;
		int NodeIDAfter2 = route2.get(route2NodeIndex + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);
		int randomDelay4 = TabuRan.nextInt(5);

		s.tabuList[NodeIDBefore1][SwapNode11.NodeId] = s.tabuTenure + randomDelay1;
		s.tabuList[SwapNode12.NodeId][NodeIDAfter1] = s.tabuTenure + randomDelay2;
		s.tabuList[NodeIDBefore2][SwapNode2.NodeId] = s.tabuTenure + randomDelay3;
		s.tabuList[SwapNode2.NodeId][NodeIDAfter2] = s.tabuTenure + randomDelay4;

		vehicle1.removeNode(route1NodeIndex);
		vehicle1.removeNode(route1NodeIndex);
		vehicle1.addNode(SwapNode2, route1NodeIndex);
		vehicle2.removeNode(route2NodeIndex);
		vehicle2.addNode(SwapNode12, route2NodeIndex);
		vehicle2.addNode(SwapNode11, route2NodeIndex);

		if (needsTransfer)
			transfer(s);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		Node SwapNode11 = route1.get(route1NodeIndex);
		Node SwapNode12 = route1.get(route1NodeIndex + 1);
		Node SwapNode2 = route2.get(route2NodeIndex);
		int[] SwapNode11Demand = SwapNode11.demands;
		int[] SwapNode12Demand = SwapNode12.demands;
		int[] SwapNode2Demand = SwapNode2.demands;

		int[] MovingFrom1Demand = new int[SwapNode11Demand.length + SwapNode12Demand.length];
		System.arraycopy(SwapNode11Demand, 0, MovingFrom1Demand, 0, SwapNode11Demand.length);
		System.arraycopy(SwapNode12Demand, 0, MovingFrom1Demand, SwapNode11Demand.length, SwapNode12Demand.length);

		firstFeasible = vehicle1.checkIfFits(SwapNode2Demand, List.of(SwapNode11, SwapNode12));
		secondFeasible = vehicle2.checkIfFits(MovingFrom1Demand, List.of(SwapNode2));
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
