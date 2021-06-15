package gr.ntua.vrp.tabu;

class DummyMove extends Move {

	public DummyMove() {
		super(Double.MAX_VALUE);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		return false;
	}

	@Override
	public int[] getVehicleIndexes() {
		return new int[] {};
	}
}
