package ro.uaic.info;

public class SimpleVehicle extends Vehicle {
	private int capacity;
	
	public SimpleVehicle(int cap) {
		super();
		this.capacity = cap;
	}

	@Override
	public void AddNode(Node Customer) {
		// TODO Auto-generated method stub
		routes.add(Customer);
        this.load += Customer.demands[0];
        this.currentLocation = Customer.NodeId;
	}

	@Override
	public boolean CheckIfFits(int[] dem) {
		// TODO Auto-generated method stub
		return load + dem[0] <= capacity;
	}

}
