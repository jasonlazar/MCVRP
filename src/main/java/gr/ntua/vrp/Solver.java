package gr.ntua.vrp;

import java.io.File;
import java.io.IOException;

public abstract class Solver {
	protected final int noOfVehicles;
	protected final Node[] nodes;
	protected final double[][] distances;
	protected final int noOfCustomers;
	protected Vehicle[] vehicles;

	protected double cost;

	public Solver(VRPRunner jct) throws IOException {
		VRPLibReader reader = new VRPLibReader(new File(jct.instance));
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
				vehicles[i] = new SimpleVehicle(distances, capacity);
			}
		} else if (reader.getType().equalsIgnoreCase("MCVRP")) {
			this.noOfVehicles = reader.getDimension() * 2 / 3;
			this.vehicles = new Vehicle[this.noOfVehicles];

			Integer[] compartments = reader.getCompartments();

			for (int i = 0; i < this.noOfVehicles; i++) {
				vehicles[i] = new CompartmentedVehicle(distances, compartments);
			}
		} else {
			this.vehicles = reader.getVehicles();
			this.noOfVehicles = vehicles.length;
		}

		cost = 0;
	}

	public Solver(Solver s) {
		this.noOfVehicles = s.noOfVehicles;
		this.nodes = s.nodes;
		this.distances = s.distances;
		this.noOfCustomers = s.noOfCustomers;
		this.cost = s.cost;
		this.vehicles = s.vehicles;
	}

	public abstract Solver solve();

	public void print() {
		System.out.println("=========================================================");

		for (int j = 0; j < this.noOfVehicles; j++) {
			if (this.vehicles[j].routes.size() > 2) {
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

	public Vehicle[] getVehicles() {
		return vehicles;
	}
}
