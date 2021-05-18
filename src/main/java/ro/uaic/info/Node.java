package ro.uaic.info;

public class Node {
    public int NodeId;
    public int demands[];
    public boolean IsRouted;

    public Node(int id, int demand) //Constructor for Customers
    {
        this.NodeId = id;
        this.demands = new int[1];
        this.demands[0] = demand;
        this.IsRouted = false;
    }
    
    public Node(int id, int[] demands)
    {
    	this.NodeId = id;
    	this.demands = demands.clone();
    	this.IsRouted = false;
    }
}