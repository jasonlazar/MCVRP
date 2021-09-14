package gr.ntua.vrp.tabu;

class DummyMove extends Move {

	public DummyMove() {
		super(Double.MAX_VALUE, -1, -1, -1, -1);
	}

	@Override
	public void applyMove(TabuSearchSolver s) {
	}

	@Override
	public boolean isFeasible(TabuSearchSolver s) {
		return false;
	}

	@Override
	public boolean transferFeasible(TabuSearchSolver s) {
		return false;
	}

	@Override
	protected void transfer(TabuSearchSolver s) {
	}
}
