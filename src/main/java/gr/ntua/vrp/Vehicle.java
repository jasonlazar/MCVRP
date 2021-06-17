package gr.ntua.vrp;

import java.util.Collection;

public abstract class Vehicle {
	public Route route;
	public int currentLocation;
	protected double[][] distances;

	public Vehicle(double[][] distances) {
		this.route = new Route(distances);
		this.currentLocation = 0; // In depot Initially
		this.distances = distances;
	}

	public abstract Vehicle makeCopy();

	public abstract void appendNode(Node Customer); // Add Customer to Vehicle routes

	public abstract boolean checkIfFits(int[] dem); // Check if we have Capacity Violation

	public abstract boolean checkIfFits(int[] dem, Collection<Node> remove); // Check if we have Capacity Violation

	public void addNode(Node Customer, int pos) {
		route.addNode(Customer, pos);
	}

	public void removeNode(int pos) {
		route.removeNode(pos);
	}
}