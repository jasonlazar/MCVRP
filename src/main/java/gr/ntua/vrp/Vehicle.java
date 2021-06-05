package gr.ntua.vrp;

import java.util.ArrayList;

public abstract class Vehicle {
    public ArrayList<Node> routes = new ArrayList<>();;
    public int currentLocation;

    public Vehicle() {
        this.currentLocation = 0; //In depot Initially
        this.routes.clear();
    }
    
    public abstract Vehicle makeCopy();

    public abstract void appendNode(Node Customer); //Add Customer to Vehicle routes

    public abstract boolean checkIfFits(int[] dem); //Check if we have Capacity Violation
    public abstract boolean checkIfFits(int[] dem, Node remove); //Check if we have Capacity Violation
    
    public void addNode(Node Customer, int pos) {
    	routes.add(pos, Customer);
    }
    
    public void removeNode(int pos) {
    	routes.remove(pos);
    }
}