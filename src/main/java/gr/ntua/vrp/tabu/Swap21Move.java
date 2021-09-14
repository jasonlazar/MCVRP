package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class Swap21Move extends Move {

	public Swap21Move(double cost, int SwapRoute1, int SwapRoute1Index, int SwapRoute2, int SwapRoute2Index) {
		super(cost, SwapRoute1, SwapRoute1Index, SwapRoute2, SwapRoute2Index);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[route1Index].routes;
		route2 = vehicles[route2Index].routes;

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

		s.TABU_Matrix[NodeIDBefore1][SwapNode11.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode12.NodeId][NodeIDAfter1] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeIDBefore2][SwapNode2.NodeId] = s.TABU_Horizon + randomDelay3;
		s.TABU_Matrix[SwapNode2.NodeId][NodeIDAfter2] = s.TABU_Horizon + randomDelay4;

		vehicles[route1Index].removeNode(route1NodeIndex);
		vehicles[route1Index].removeNode(route1NodeIndex);
		vehicles[route1Index].addNode(SwapNode2, route1NodeIndex);
		vehicles[route2Index].removeNode(route2NodeIndex);
		vehicles[route2Index].addNode(SwapNode12, route2NodeIndex);
		vehicles[route2Index].addNode(SwapNode11, route2NodeIndex);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[route1Index].routes;
		route2 = vehicles[route2Index].routes;

		Node SwapNode11 = route1.get(route1NodeIndex);
		Node SwapNode12 = route1.get(route1NodeIndex + 1);
		Node SwapNode2 = route2.get(route2NodeIndex);
		int[] SwapNode11Demand = SwapNode11.demands;
		int[] SwapNode12Demand = SwapNode12.demands;
		int[] SwapNode2Demand = SwapNode2.demands;

		int[] MovingFrom1Demand = new int[SwapNode11Demand.length + SwapNode12Demand.length];
		System.arraycopy(SwapNode11Demand, 0, MovingFrom1Demand, 0, SwapNode11Demand.length);
		System.arraycopy(SwapNode12Demand, 0, MovingFrom1Demand, SwapNode11Demand.length, SwapNode12Demand.length);

		return (vehicles[route1Index].checkIfFits(SwapNode2Demand, List.of(SwapNode11, SwapNode12))
		        && vehicles[route2Index].checkIfFits(MovingFrom1Demand, List.of(SwapNode2)));
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void transfer(TabuSearchSolver s) {
		// TODO Auto-generated method stub
		
	}
}
