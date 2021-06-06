package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class SwapMove extends Move {
	private int SwapRoute1;
	private int SwapRoute1Index;
	private int SwapRoute2;
	private int SwapRoute2Index;

	public SwapMove(double cost, int SwapRoute1, int SwapRoute1Index, int SwapRoute2, int SwapRoute2Index) {
		super(cost);
		this.SwapRoute1 = SwapRoute1;
		this.SwapRoute1Index = SwapRoute1Index;
		this.SwapRoute2 = SwapRoute2;
		this.SwapRoute2Index = SwapRoute2Index;
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[SwapRoute1].routes;
		route2 = vehicles[SwapRoute2].routes;

		Node SwapNode1 = route1.get(SwapRoute1Index);
		Node SwapNode2 = route2.get(SwapRoute2Index);

		int NodeIDBefore1 = route1.get(SwapRoute1Index - 1).NodeId;
		int NodeIDAfter1 = route1.get(SwapRoute1Index + 1).NodeId;
		int NodeIDBefore2 = route2.get(SwapRoute2Index - 1).NodeId;
		int NodeIDAfter2 = route2.get(SwapRoute2Index + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);
		int randomDelay4 = TabuRan.nextInt(5);

		s.TABU_Matrix[NodeIDBefore1][SwapNode1.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode1.NodeId][NodeIDAfter1] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeIDBefore2][SwapNode2.NodeId] = s.TABU_Horizon + randomDelay3;
		s.TABU_Matrix[SwapNode2.NodeId][NodeIDAfter2] = s.TABU_Horizon + randomDelay4;

		vehicles[SwapRoute1].removeNode(SwapRoute1Index);
		vehicles[SwapRoute1].addNode(SwapNode2, SwapRoute1Index);
		vehicles[SwapRoute2].removeNode(SwapRoute2Index);
		vehicles[SwapRoute2].addNode(SwapNode1, SwapRoute2Index);
	}
}
