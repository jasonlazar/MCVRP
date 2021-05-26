package gr.ntua.vrp;

import java.util.ArrayList;

public abstract class Vehicle {
    public ArrayList<Node> routes = new ArrayList<>();
    public int load;
    public int currentLocation;

    public Vehicle() {
        this.load = 0;
        this.currentLocation = 0; //In depot Initially
        this.routes.clear();
    }
    
    public abstract Vehicle makeCopy();

    public abstract boolean AddNode(Node Customer); //Add Customer to Vehicle routes

    public abstract boolean CheckIfFits(int[] dem); //Check if we have Capacity Violation
}