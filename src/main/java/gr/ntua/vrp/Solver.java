package gr.ntua.vrp;

import java.io.File;
import java.io.IOException;

import thiagodnf.jacof.util.io.InstanceReader;

public abstract class Solver {
    protected final int noOfVehicles;
    protected final Node[] nodes;
    protected final double[][] distances;
    protected final int noOfCustomers;
    protected Vehicle[] vehicles;
    
    protected double cost;
    
    public Solver(VRPRunner jct) throws IOException {
    	VRPLibReader reader = new VRPLibReader(new InstanceReader(new File(jct.instance)));
        this.noOfCustomers = reader.getDimension();
        
        this.distances = reader.getDistance();
        
        nodes = new Node[noOfCustomers];
        
        for (int i = 0; i < noOfCustomers; i++) {
            nodes[i] = new Node(i, reader.getDemand()[i]);
        }
        nodes[0].IsRouted = true;
        
        
        if (reader.getType().equalsIgnoreCase("CVRP")) {
        	this.noOfVehicles = reader.getDimension() / 3;
        	this.vehicles = new Vehicle[this.noOfVehicles];
        	int capacity = reader.getVehicleCapacity(); 

	        for (int i = 0; i < this.noOfVehicles; i++) {
	            vehicles[i] = new SimpleVehicle(capacity);
	        }
        }
        
        else {
        	this.noOfVehicles = reader.getDimension() * 2 / 3;
        	this.vehicles = new Vehicle[this.noOfVehicles];
        	
        	Integer[] compartments = reader.getCompartments(); 

	        for (int i = 0; i < this.noOfVehicles; i++) {
	            vehicles[i] = new CompartmentedVehicle(compartments);
	        }
        }
        
        cost = 0;
    }
    
    public Solver(Solver s) {
    	this.noOfVehicles = s.noOfVehicles;
    	this.nodes = s.nodes;
    	this.distances = s.distances;
    	this.noOfCustomers = s.noOfCustomers;
    	this.cost = s.cost;
    	
    	this.vehicles = new Vehicle[this.noOfVehicles];

        for (int i = 0; i < this.noOfVehicles; i++) {
            this.vehicles[i] = s.vehicles[i].makeCopy();
        }
    }
    
    public abstract Solver solve();
    
    public abstract void print();

	public Vehicle[] getVehicles() {
		return vehicles;
	}
    
}
