package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class CrossMove extends Move {
	private List<Node> after1;
	private List<Node> after2;

	public CrossMove(double cost, Vehicle veh1, int Route1Index, Vehicle veh2, int Route2Index) {
		super(cost, veh1, Route1Index, veh2, Route2Index);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		if (route2.size() == 0) {
			s.emptyVehicles.remove(vehicle2);
		}

		Node SwapNode1 = route1.get(route1NodeIndex);
		Node SwapNode2 = route2.get(route2NodeIndex);

		int NodeIDAfter1 = route1.get(route1NodeIndex + 1).NodeId;
		int NodeIDAfter2 = route2.get(route2NodeIndex + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);

		s.tabuList[SwapNode1.NodeId][NodeIDAfter1] = s.tabuTenure + randomDelay1;
		s.tabuList[SwapNode2.NodeId][NodeIDAfter2] = s.tabuTenure + randomDelay2;

		while (route1.size() > route1NodeIndex + 1)
			vehicle1.removeNode(route1NodeIndex + 1);
		while (route2.size() > route2NodeIndex + 1)
			vehicle2.removeNode(route2NodeIndex + 1);

		for (Node n : after2)
			vehicle1.appendNode(n);
		for (Node n : after1)
			vehicle2.appendNode(n);

		if (vehicle2.routes.size() == 2) {
			s.emptyVehicles.add(vehicle2);
		}

		if (needsTransfer)
			transfer(s);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> route1;
		ArrayList<Node> route2;

		route1 = vehicle1.routes;
		route2 = vehicle2.routes;

		after1 = new ArrayList<>(route1.subList(route1NodeIndex + 1, route1.size()));
		after2 = new ArrayList<>(route2.subList(route2NodeIndex + 1, route2.size()));

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

		firstFeasible = (after2.size() > 1) ? vehicle1.checkIfFits(secondDemands, after1) : true;
		secondFeasible = (after1.size() > 1) ? vehicle2.checkIfFits(firstDemands, after2) : true;

		return (firstFeasible && secondFeasible);
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		Set<Node> after1Set = new HashSet<>(after1);
		Set<Node> after2Set = new HashSet<>(after2);

		return transferFeasible(s, after1Set, after2Set);
	}
}
