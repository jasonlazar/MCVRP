package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class DoubleInsertionMove extends Move {
	private int SrcRoute;
	private int SrcRouteIndex;
	private int DstRoute;
	private int DstRouteIndex;

	public DoubleInsertionMove(double cost, int src, int sri, int dst, int dri) {
		super(cost);
		this.SrcRoute = src;
		this.SrcRouteIndex = sri;
		this.DstRoute = dst;
		this.DstRouteIndex = dri;
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		ArrayList<Node> routesTo;

		Vehicle[] vehicles = s.getVehicles();

		routesFrom = vehicles[SrcRoute].route.getRoutes();
		routesTo = vehicles[DstRoute].route.getRoutes();

		Node SwapNode1 = routesFrom.get(SrcRouteIndex);
		Node SwapNode2 = routesFrom.get(SrcRouteIndex + 1);

		int NodeIDBefore = routesFrom.get(SrcRouteIndex - 1).NodeId;
		int NodeIDAfter = routesFrom.get(SrcRouteIndex + 2).NodeId;
		int NodeID_F = routesTo.get(DstRouteIndex).NodeId;
		int NodeID_G = routesTo.get(DstRouteIndex + 1).NodeId;

		Random TabuRan = new Random();
		int randomDelay1 = TabuRan.nextInt(5);
		int randomDelay2 = TabuRan.nextInt(5);
		int randomDelay3 = TabuRan.nextInt(5);

		s.TABU_Matrix[NodeIDBefore][SwapNode1.NodeId] = s.TABU_Horizon + randomDelay1;
		s.TABU_Matrix[SwapNode2.NodeId][NodeIDAfter] = s.TABU_Horizon + randomDelay2;
		s.TABU_Matrix[NodeID_F][NodeID_G] = s.TABU_Horizon + randomDelay3;

		vehicles[SrcRoute].removeNode(SrcRouteIndex);
		vehicles[SrcRoute].removeNode(SrcRouteIndex);
		vehicles[DstRoute].addNode(SwapNode2, DstRouteIndex + 1);
		vehicles[DstRoute].addNode(SwapNode1, DstRouteIndex + 1);
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		ArrayList<Node> routesFrom;
		int MovingNodeDemand[];

		Vehicle[] vehicles = s.getVehicles();
		routesFrom = vehicles[SrcRoute].route.getRoutes();

		int[] firstDemands = routesFrom.get(SrcRouteIndex).demands;
		int[] secondDemands = routesFrom.get(SrcRouteIndex + 1).demands;
		MovingNodeDemand = new int[firstDemands.length + secondDemands.length];
		System.arraycopy(firstDemands, 0, MovingNodeDemand, 0, firstDemands.length);
		System.arraycopy(secondDemands, 0, MovingNodeDemand, firstDemands.length, secondDemands.length);

		return vehicles[DstRoute].checkIfFits(MovingNodeDemand);
	}

	@Override
	public int[] getVehicleIndexes() {
		return new int[] { SrcRoute, DstRoute };
	}
}
