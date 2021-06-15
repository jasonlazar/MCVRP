package gr.ntua.vrp.tabu;

public abstract class Move implements Comparable<Move> {
	protected double cost;

	public Move(double cost) {
		this.cost = cost;
	}

	public abstract void applyMove(TabuSearchSolver s);

	public abstract boolean isFeasible(TabuSearchSolver s);

	public abstract int[] getVehicleIndexes();

	@Override
	public int compareTo(Move m) {
		if (cost < m.cost)
			return -1;
		else if (cost == m.cost)
			return 0;
		else
			return 1;
	}
}
