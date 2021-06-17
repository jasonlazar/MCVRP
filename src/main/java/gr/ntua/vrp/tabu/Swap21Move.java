package gr.ntua.vrp.tabu;

import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class Swap21Move extends Move {
	private int SwapRoute1;
	private int SwapRoute1Index;
	private int SwapRoute2;
	private int SwapRoute2Index;

	public Swap21Move(double cost, int SwapRoute1, int SwapRoute1Index, int SwapRoute2, int SwapRoute2Index) {
		super(cost);
		this.SwapRoute1 = SwapRoute1;
		this.SwapRoute1Index = SwapRoute1Index;
		this.SwapRoute2 = SwapRoute2;
		this.SwapRoute2Index = SwapRoute2Index;
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		List<Node> route1;
		List<Node> route2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[SwapRoute1].route.getRoutes();
		route2 = vehicles[SwapRoute2].route.getRoutes();

		Node SwapNode11 = route1.get(SwapRoute1Index);
		Node SwapNode12 = route1.get(SwapRoute1Index + 1);
		Node SwapNode2 = route2.get(SwapRoute2Index);

		int NodeIDBefore1 = route1.get(SwapRoute1Index - 1).NodeId;
		int NodeIDAfter1 = route1.get(SwapRoute1Index + 2).NodeId;
		int NodeIDBefore2 = route2.get(SwapRoute2Index - 1).NodeId;
		int NodeIDAfter2 = route2.get(SwapRoute2Index + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);
		int randomDelay4 = TabuRan.nextInt(5);

		s.TABU_Matrix[NodeIDBefore1][SwapNode11.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode12.NodeId][NodeIDAfter1] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeIDBefore2][SwapNode2.NodeId] = s.TABU_Horizon + randomDelay3;
		s.TABU_Matrix[SwapNode2.NodeId][NodeIDAfter2] = s.TABU_Horizon + randomDelay4;

		vehicles[SwapRoute1].removeNode(SwapRoute1Index);
		vehicles[SwapRoute1].removeNode(SwapRoute1Index);
		vehicles[SwapRoute1].addNode(SwapNode2, SwapRoute1Index);
		vehicles[SwapRoute2].removeNode(SwapRoute2Index);
		vehicles[SwapRoute2].addNode(SwapNode12, SwapRoute2Index);
		vehicles[SwapRoute2].addNode(SwapNode11, SwapRoute2Index);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		List<Node> route1;
		List<Node> route2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[SwapRoute1].route.getRoutes();
		route2 = vehicles[SwapRoute2].route.getRoutes();

		Node SwapNode11 = route1.get(SwapRoute1Index);
		Node SwapNode12 = route1.get(SwapRoute1Index + 1);
		Node SwapNode2 = route2.get(SwapRoute2Index);
		int[] SwapNode11Demand = SwapNode11.demands;
		int[] SwapNode12Demand = SwapNode12.demands;
		int[] SwapNode2Demand = SwapNode2.demands;

		int[] MovingFrom1Demand = new int[SwapNode11Demand.length + SwapNode12Demand.length];
		System.arraycopy(SwapNode11Demand, 0, MovingFrom1Demand, 0, SwapNode11Demand.length);
		System.arraycopy(SwapNode12Demand, 0, MovingFrom1Demand, SwapNode11Demand.length, SwapNode12Demand.length);

		return (vehicles[SwapRoute1].checkIfFits(SwapNode2Demand, List.of(SwapNode11, SwapNode12))
		        && vehicles[SwapRoute2].checkIfFits(MovingFrom1Demand, List.of(SwapNode2)));
	}

	@Override
	public int[] getVehicleIndexes() {
		return new int[] { SwapRoute1, SwapRoute2 };
	}

}
