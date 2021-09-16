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
		try (VRPLibReader reader = new VRPLibReader(new File(jct.instance))) {
			this.noOfCustomers = reader.getDimension();

			this.distances = reader.getDistance();

			nodes = reader.getNodes();
			nodes[0].IsRouted = true;

			this.vehicles = reader.getVehicles();
			this.noOfVehicles = vehicles.length;

			cost = 0;
		}
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
				String name = "Vehicle " + ((vehicles[j].name != null) ? vehicles[j].name : j);
				System.out.print(name + ":");
				int RoutSize = this.vehicles[j].routes.size();
				for (int k = 0; k < RoutSize; k++) {
					if (k == RoutSize - 1) {
						System.out.print(this.vehicles[j].routes.get(k).name);
					} else {
						System.out.print(this.vehicles[j].routes.get(k).name + "->");
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

	public double getCost() {
		return cost;
	}
}
