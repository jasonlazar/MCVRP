package ro.uaic.info;

public class SimpleVehicle extends Vehicle {
	private int capacity;
	
	public SimpleVehicle(int cap) {
		super();
		this.capacity = cap;
	}

	@Override
	public boolean AddNode(Node Customer) {
		// TODO Auto-generated method stub
		if (!CheckIfFits(Customer.demands))
			return false;
		
		routes.add(Customer);
        this.load += Customer.demands[0];
        this.currentLocation = Customer.NodeId;
        return true;
	}

	@Override
	public boolean CheckIfFits(int[] dem) {
		// TODO Auto-generated method stub
		return load + dem[0] <= capacity;
	}

}
