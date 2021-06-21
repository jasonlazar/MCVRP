package gr.ntua.vrp.tabu;

import java.util.Random;

enum MoveType {
	SINGLE_INSERTION, SWAP, DOUBLE_INSERTION, SWAP21, CROSS;

	private static final MoveType[] VALUES = values();
	private static final int LENGTH = VALUES.length;
	private static final Random RANDOM = new Random();

	public static MoveType randomMove() {
		return VALUES[RANDOM.nextInt(LENGTH)];
	}
}
