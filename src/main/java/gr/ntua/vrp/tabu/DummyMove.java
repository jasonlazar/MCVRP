package gr.ntua.vrp.tabu;

class DummyMove extends Move {

	public DummyMove() {
		super(Double.MAX_VALUE);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
	}

	@Override
	public int[] getVehicleIndexes() {
		return new int[] {};
	}

}
