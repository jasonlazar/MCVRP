package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class SingleInsertionMove extends Move {
	private int transferTo;

	public SingleInsertionMove(double cost, int SrcRoute, int SrcRouteIndex, int DstRoute, int DstRouteIndex) {
		super(cost, SrcRoute, SrcRouteIndex, DstRoute, DstRouteIndex);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		ArrayList<Node> routesTo;

		Vehicle[] vehicles = s.getVehicles();

		routesFrom = vehicles[route1Index].routes;
		routesTo = vehicles[route2Index].routes;

		if (routesTo.size() == 2) {
			s.emptyVehicles.remove(route2Index);
		}

		Node SwapNode = routesFrom.get(route1NodeIndex);

		int NodeIDBefore = routesFrom.get(route1NodeIndex - 1).NodeId;
		int NodeIDAfter = routesFrom.get(route1NodeIndex + 1).NodeId;
		int NodeID_F = routesTo.get(route2NodeIndex).NodeId;
		int NodeID_G = routesTo.get(route2NodeIndex + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);

		s.TABU_Matrix[NodeIDBefore][SwapNode.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode.NodeId][NodeIDAfter] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeID_F][NodeID_G] = s.TABU_Horizon + randomDelay3;

		vehicles[route1Index].removeNode(route1NodeIndex);
		vehicles[route2Index].addNode(SwapNode, route2NodeIndex + 1);

		if (vehicles[route1Index].routes.size() == 2) {
			s.emptyVehicles.add(route1Index);
		}

		if (needsTransfer) {
			transfer(s);
		}
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		Vehicle[] vehicles = s.getVehicles();
		routesFrom = vehicles[route1Index].routes;

		int MovingNodeDemand[] = routesFrom.get(route1NodeIndex).demands;

		return vehicles[route2Index].checkIfFits(MovingNodeDemand);
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		Vehicle[] vehicles = s.getVehicles();
		ArrayList<Node> routesFrom = vehicles[route1Index].routes;

		int[] routeDemands = vehicles[route2Index].calculateDemandsPlus(List.of(routesFrom.get(route1NodeIndex)));

		List<Integer> canMoveTo = feasibleVehicles(s, routeDemands, 1);
		needsTransfer = canMoveTo.size() > 0;
		if (needsTransfer)
			transferTo = canMoveTo.get(0);

		return needsTransfer;
	}

	@Override
	protected void transfer(TabuSearchSolver s) {
		swapRoutes(s.getVehicles(), route2Index, transferTo);
		s.emptyVehicles.add(route2Index);
		s.emptyVehicles.remove(transferTo);
		route2Index = transferTo;
	}
}
