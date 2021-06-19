package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class CrossMove extends Move {
	private int Route1;
	private int Route1Index;
	private int Route2;
	private int Route2Index;

	public CrossMove(double cost, int Route1, int Route1Index, int Route2, int Route2Index) {
		super(cost);
		this.Route1 = Route1;
		this.Route1Index = Route1Index;
		this.Route2 = Route2;
		this.Route2Index = Route2Index;
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;
		List<Node> after1;
		List<Node> after2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[Route1].routes;
		route2 = vehicles[Route2].routes;

		Node SwapNode1 = route1.get(Route1Index);
		Node SwapNode2 = route2.get(Route2Index);

		int NodeIDAfter1 = route1.get(Route1Index + 1).NodeId;
		int NodeIDAfter2 = route2.get(Route2Index + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);

		s.TABU_Matrix[SwapNode1.NodeId][NodeIDAfter1] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode2.NodeId][NodeIDAfter2] = s.TABU_Horizon + randomDelay2;

		after1 = new ArrayList<>(route1.subList(Route1Index + 1, route1.size()));
		after2 = new ArrayList<>(route2.subList(Route2Index + 1, route2.size()));

		while (route1.size() > Route1Index + 1)
			vehicles[Route1].removeNode(Route1Index + 1);
		while (route2.size() > Route2Index + 1)
			vehicles[Route2].removeNode(Route2Index + 1);

		for (Node n : after2)
			vehicles[Route1].appendNode(n);
		for (Node n : after1)
			vehicles[Route2].appendNode(n);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;
		List<Node> after1;
		List<Node> after2;

		Vehicle[] vehicles = s.getVehicles();

		route1 = vehicles[Route1].routes;
		route2 = vehicles[Route2].routes;

		after1 = new ArrayList<>(route1.subList(Route1Index + 1, route1.size()));
		after2 = new ArrayList<>(route2.subList(Route2Index + 1, route2.size()));

		int totalFirstDemands = 0;
		int totalSecondDemands = 0;
		int[] firstDemands;
		int[] secondDemands;

		for (int i = 0; i < after1.size() - 1; ++i)
			totalFirstDemands += after1.get(i).demands.length;
		for (int i = 0; i < after2.size() - 1; ++i)
			totalSecondDemands += after2.get(i).demands.length;

		firstDemands = new int[totalFirstDemands];
		int arrayIndex = 0;
		for (int i = 0; i < after1.size() - 1; ++i) {
			int[] curDemands = after1.get(i).demands;
			System.arraycopy(curDemands, 0, firstDemands, arrayIndex, curDemands.length);
			arrayIndex += curDemands.length;
		}

		secondDemands = new int[totalSecondDemands];
		arrayIndex = 0;
		for (int i = 0; i < after2.size() - 1; ++i) {
			int[] curDemands = after2.get(i).demands;
			System.arraycopy(curDemands, 0, secondDemands, arrayIndex, curDemands.length);
			arrayIndex += curDemands.length;
		}

		boolean fits1 = (after2.size() > 1) ? vehicles[Route1].checkIfFits(secondDemands, after1) : true;
		boolean fits2 = (after1.size() > 1) ? vehicles[Route2].checkIfFits(firstDemands, after2) : true;

		return (fits1 && fits2);
	}

	@Override
	public int[] getVehicleIndexes() {
		return new int[] { Route1, Route2 };
	}

}
