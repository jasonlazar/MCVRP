package gr.ntua.vrp;

import java.util.Collection;

public class SimpleVehicle extends Vehicle {
	private int capacity;
	private int load;

	public SimpleVehicle(double[][] distances, int cap) {
		super(distances);
		this.capacity = cap;
		this.load = 0;
	}

	@Override
	public void appendNode(Node Customer) {
		routes.add(Customer);
		this.load += Customer.demands[0];
		this.currentLocation = Customer.NodeId;
	}

	@Override
	public Vehicle makeCopy() {
		return new SimpleVehicle(this.distances, this.capacity);
	}

	@Override
	public boolean checkIfFits(int[] dem) {
		int totalDemands = 0;
		for (int d : dem)
			totalDemands += d;
		return load + totalDemands <= capacity;
	}

	@Override
	public boolean checkIfFits(int[] dem, Collection<Node> remove) {
		int totalDemands = 0;
		int totalRemove = 0;

		for (int d : dem)
			totalDemands += d;
		for (Node n : remove)
			totalRemove += n.demands[0];

		return load + totalDemands - totalRemove <= capacity;
	}

	@Override
	public void addNode(Node Customer, int pos) {
		routes.add(pos, Customer);
		this.load += Customer.demands[0];
	}

	@Override
	public void removeNode(int pos) {
		this.load -= routes.get(pos).demands[0];
		routes.remove(pos);
	}

}
