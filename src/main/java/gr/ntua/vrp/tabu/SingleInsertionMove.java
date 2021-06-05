package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.Random;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class SingleInsertionMove extends Move {
	private int SrcRoute;
	private int SrcRouteIndex;
	private int DstRoute;
	private int DstRouteIndex;
	
	public SingleInsertionMove(double cost, int SrcRoute, int SrcRouteIndex, int DstRoute, int DstRouteIndex) {
		super(cost);
		this.SrcRoute = SrcRoute;
		this.SrcRouteIndex = SrcRouteIndex;
		this.DstRoute =  DstRoute;
		this.DstRouteIndex = DstRouteIndex;
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
        ArrayList<Node> routesFrom;
        ArrayList<Node> routesTo;
        
        Vehicle[] vehicles = s.getVehicles();
        
		routesFrom = vehicles[SrcRoute].routes;
        routesTo = vehicles[DstRoute].routes;

        Node SwapNode = routesFrom.get(SrcRouteIndex);

        int NodeIDBefore = routesFrom.get(SrcRouteIndex - 1).NodeId;
        int NodeIDAfter = routesFrom.get(SrcRouteIndex + 1).NodeId;
        int NodeID_F = routesTo.get(DstRouteIndex).NodeId;
        int NodeID_G = routesTo.get(DstRouteIndex + 1).NodeId;

        Random TabuRan = new Random();
        int randomDelay1 = TabuRan.nextInt(5);
        int randomDelay2 = TabuRan.nextInt(5);
        int randomDelay3 = TabuRan.nextInt(5);

        s.TABU_Matrix[NodeIDBefore][SwapNode.NodeId] = s.TABU_Horizon + randomDelay1;
        s.TABU_Matrix[SwapNode.NodeId][NodeIDAfter] = s.TABU_Horizon + randomDelay2;
        s.TABU_Matrix[NodeID_F][NodeID_G] = s.TABU_Horizon + randomDelay3;

        vehicles[SrcRoute].removeNode(SrcRouteIndex);
        vehicles[DstRoute].addNode(SwapNode, DstRouteIndex+1);
	}

}
