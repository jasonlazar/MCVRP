package gr.ntua.vrp;

public class SimpleVehicle extends Vehicle {
	private int capacity;
	
	public SimpleVehicle(int cap) {
		super();
		this.capacity = cap;
	}

	@Override
	public void AddNode(Node Customer) {
		routes.add(Customer);
        this.load += Customer.demands[0];
        this.currentLocation = Customer.NodeId;
	}
	
	@Override
	public Vehicle makeCopy() {
		return new SimpleVehicle(this.capacity);
	}

	@Override
	public boolean CheckIfFits(int[] dem) {
		// TODO Auto-generated method stub
		return load + dem[0] <= capacity;
	}

}
