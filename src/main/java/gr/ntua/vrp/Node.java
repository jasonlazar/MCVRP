package gr.ntua.vrp;

public class Node {
	public int nodeId;
	public int demands[];
	public String name;
	public boolean IsRouted;

	public Node(int id, int demand, String name) // Constructor for Customers
	{
		this(id, new int[] { demand }, name);
	}

	public Node(int id, int[] demands, String name) {
		this.nodeId = id;
		this.demands = demands.clone();
		this.name = (name != null) ? name : String.valueOf(id);
		this.IsRouted = false;
	}
}