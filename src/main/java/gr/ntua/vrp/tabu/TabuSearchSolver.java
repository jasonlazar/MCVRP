package gr.ntua.vrp.tabu;

import java.io.IOException;
import java.util.ArrayList;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Solver;
import gr.ntua.vrp.VRPRunner;
import gr.ntua.vrp.Vehicle;
import gr.ntua.vrp.greedy.GreedySolver;

public class TabuSearchSolver extends Solver {
    protected final int TABU_Horizon;
    private final int iterations;
    private final Vehicle[] BestSolutionVehicles;
    protected final int TABU_Matrix[][];


    private double BestSolutionCost;

    public TabuSearchSolver(VRPRunner jct) throws IOException {
        super(jct);
        this.TABU_Horizon = jct.TabuHorizon;
        this.iterations = jct.iterations;

        this.BestSolutionVehicles = new Vehicle[this.noOfVehicles];

        for (int i = 0; i < this.noOfVehicles; i++) {
            this.BestSolutionVehicles[i] = this.vehicles[i].makeCopy();
        }
        
        GreedySolver greedySolver = new GreedySolver(this);
        greedySolver.solve();
        this.vehicles = greedySolver.getVehicles();
        this.cost = greedySolver.getCost();
        
        int DimensionCustomer = this.distances[1].length;
        TABU_Matrix = new int[DimensionCustomer + 1][DimensionCustomer + 1];
    }

    public TabuSearchSolver solve() {
        int iteration_number = 0;

        Move BestMove;

        this.BestSolutionCost = this.cost;

        while (true) {
            BestMove = new Move(Double.MAX_VALUE);

            Move NeighborMove = singleInsertion();
            
            if (NeighborMove.compareTo(BestMove) < 0)
            	BestMove = NeighborMove;
            
            NeighborMove = swap();
            
            if (NeighborMove.compareTo(BestMove) < 0)
            	BestMove = NeighborMove;

            for (int o = 0; o < TABU_Matrix[0].length; o++) {
                for (int p = 0; p < TABU_Matrix[0].length; p++) {
                    if (TABU_Matrix[o][p] > 0) {
                        TABU_Matrix[o][p]--;
                    }
                }
            }
            
            BestMove.applyMove(this);

            this.cost += BestMove.cost;

            if (this.cost < this.BestSolutionCost) {
                iteration_number = 0;
                this.SaveBestSolution();
            } else {
                iteration_number++;
            }

            if (iterations == iteration_number) {
                break;
            }
        }

        this.vehicles = this.BestSolutionVehicles;
        this.cost = this.BestSolutionCost;

        return this;
    }

    private void SaveBestSolution() {
        this.BestSolutionCost = this.cost;
        for (int j = 0; j < this.noOfVehicles; j++) {
            this.BestSolutionVehicles[j].routes.clear();
            if (!this.vehicles[j].routes.isEmpty()) {
                int RoutSize = this.vehicles[j].routes.size();
                for (int k = 0; k < RoutSize; k++) {
                    Node n = this.vehicles[j].routes.get(k);
                    this.BestSolutionVehicles[j].routes.add(n);
                }
            }
        }
    }
    
    public Move singleInsertion() {
    	//We use 1-0 exchange move
        ArrayList<Node> routesFrom;
        ArrayList<Node> routesTo;

        int MovingNodeDemand[] = null;

        int VehIndexFrom, VehIndexTo;
        double BestNCost, NeighborCost;

        int SwapIndexA = -1, SwapIndexB = -1, SwapRouteFrom = -1, SwapRouteTo = -1;

	    BestNCost = Double.MAX_VALUE;
	
	    for (VehIndexFrom = 0; VehIndexFrom < this.vehicles.length; VehIndexFrom++) {
	        routesFrom = this.vehicles[VehIndexFrom].routes;
	        int RoutFromLength = routesFrom.size();
	
	        for (int i = 1; i < (RoutFromLength - 1); i++) { //Not possible to move depot!
	            for (VehIndexTo = 0; VehIndexTo < this.vehicles.length; VehIndexTo++) {
	            	if (VehIndexFrom == VehIndexTo) continue;
	                routesTo = this.vehicles[VehIndexTo].routes;
	                int RouteToLength = routesTo.size();
	                for (int j = 0; (j < RouteToLength - 1); j++) {//Not possible to move after last Depot!
	
	                    MovingNodeDemand = routesFrom.get(i).demands;
	
	                    double MinusCost1 = this.distances[routesFrom.get(i - 1).NodeId][routesFrom.get(i).NodeId];
	                    double MinusCost2 = this.distances[routesFrom.get(i).NodeId][routesFrom.get(i + 1).NodeId];
	                    double MinusCost3 = this.distances[routesTo.get(j).NodeId][routesTo.get(j + 1).NodeId];
	
	                    double AddedCost1 = this.distances[routesFrom.get(i - 1).NodeId][routesFrom.get(i + 1).NodeId];
	                    double AddedCost2 = this.distances[routesTo.get(j).NodeId][routesFrom.get(i).NodeId];
	                    double AddedCost3 = this.distances[routesFrom.get(i).NodeId][routesTo.get(j + 1).NodeId];
	
	                    //Check if the move is a Tabu! - If it is Tabu break
	                    if ((TABU_Matrix[routesFrom.get(i - 1).NodeId][routesFrom.get(i + 1).NodeId] != 0)
	                            || (TABU_Matrix[routesTo.get(j).NodeId][routesFrom.get(i).NodeId] != 0)
	                            || (TABU_Matrix[routesFrom.get(i).NodeId][routesTo.get(j + 1).NodeId] != 0)) {
	                        break;
	                    }
	
	                    NeighborCost = AddedCost1 + AddedCost2 + AddedCost3
	                            - MinusCost1 - MinusCost2 - MinusCost3;
	
	                    if (NeighborCost < BestNCost && this.vehicles[VehIndexTo].checkIfFits(MovingNodeDemand)) {
	                        BestNCost = NeighborCost;
	                        SwapIndexA = i;
	                        SwapIndexB = j;
	                        SwapRouteFrom = VehIndexFrom;
	                        SwapRouteTo = VehIndexTo;
	                    }
	                }
	            }
	        }
	    }
    	return new SingleInsertionMove(BestNCost, SwapRouteFrom, SwapIndexA, SwapRouteTo, SwapIndexB);
    }
    
    public Move swap() {
    	//We use 1-1 exchange move
        ArrayList<Node> route1;
        ArrayList<Node> route2;

        int FirstNodeDemand[] = null;
        int SecondNodeDemand[];

        int VehIndex1, VehIndex2;
        double BestNCost, NeighborCost;

        int SwapIndexA = -1, SwapIndexB = -1, SwapRoute1 = -1, SwapRoute2 = -1;

	    BestNCost = Double.MAX_VALUE;
	
	    for (VehIndex1 = 0; VehIndex1 < this.vehicles.length; VehIndex1++) {
	        route1 = this.vehicles[VehIndex1].routes;
	        int Route1Length = route1.size();
	
	        for (int i = 1; i < (Route1Length - 1); i++) { //Not possible to move depot!
	            for (VehIndex2 = 0; VehIndex2 < this.vehicles.length; VehIndex2++) {
	            	if (VehIndex1 == VehIndex2) continue;
	                route2 = this.vehicles[VehIndex2].routes;
	                int Route2Length = route2.size();
	                
	                // No point in swapping nodes from routes where they are alone
	                if (Route1Length == 3 && Route2Length == 3) continue;
	                
	                for (int j = 1; j < (Route2Length - 1); j++) {//Not possible to move after last Depot!
	
	                	Node FirstNode = route1.get(i);
	                	Node SecondNode = route2.get(j);
	                    FirstNodeDemand = route1.get(i).demands;
	                    SecondNodeDemand = route2.get(j).demands;
	
	                    double MinusCost1 = this.distances[route1.get(i - 1).NodeId][route1.get(i).NodeId];
	                    double MinusCost2 = this.distances[route1.get(i).NodeId][route1.get(i + 1).NodeId];
	                    double MinusCost3 = this.distances[route2.get(j-1).NodeId][route2.get(j).NodeId];
	                    double MinusCost4 = this.distances[route2.get(j).NodeId][route2.get(j + 1).NodeId];
	
	                    double AddedCost1 = this.distances[route1.get(i - 1).NodeId][route2.get(j).NodeId];
	                    double AddedCost2 = this.distances[route2.get(j).NodeId][route1.get(i+1).NodeId];
	                    double AddedCost3 = this.distances[route2.get(j-1).NodeId][route1.get(i).NodeId];
	                    double AddedCost4 = this.distances[route1.get(i).NodeId][route2.get(j + 1).NodeId];
	
	                    //Check if the move is a Tabu! - If it is Tabu break
	                    if ((TABU_Matrix[route1.get(i - 1).NodeId][route2.get(j).NodeId] != 0)
	                    		|| (TABU_Matrix[route2.get(j).NodeId][route1.get(i+1).NodeId] != 0)
	                            || (TABU_Matrix[route2.get(j-1).NodeId][route1.get(i).NodeId] != 0)
	                            || (TABU_Matrix[route1.get(i).NodeId][route2.get(j + 1).NodeId] != 0)) {
	                        break;
	                    }
	
	                    NeighborCost = AddedCost1 + AddedCost2 + AddedCost3 + AddedCost4
	                            - MinusCost1 - MinusCost2 - MinusCost3 - MinusCost4;
	
	                    if (NeighborCost < BestNCost
	                    		&& this.vehicles[VehIndex1].checkIfFits(SecondNodeDemand, FirstNode)
	                    		&& this.vehicles[VehIndex2].checkIfFits(FirstNodeDemand, SecondNode)) {
	                        BestNCost = NeighborCost;
	                        SwapIndexA = i;
	                        SwapIndexB = j;
	                        SwapRoute1 = VehIndex1;
	                        SwapRoute2 = VehIndex2;
	                    }
	                }
	            }
	        }
	    }
    	return new SwapMove(BestNCost, SwapRoute1, SwapIndexA, SwapRoute2, SwapIndexB);
    }

    public void print() {
        System.out.println("=========================================================");

        for (int j = 0; j < this.noOfVehicles; j++) {
            if (!this.vehicles[j].routes.isEmpty()) {
                System.out.print("Vehicle " + j + ":");
                int RoutSize = this.vehicles[j].routes.size();
                for (int k = 0; k < RoutSize; k++) {
                    if (k == RoutSize - 1) {
                        System.out.print(this.vehicles[j].routes.get(k).NodeId);
                    } else {
                        System.out.print(this.vehicles[j].routes.get(k).NodeId + "->");
                    }
                }
                System.out.println();
            }
        }
        System.out.println("\nBest Value: " + this.cost + "\n");
    }
}


