package com.interview.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TravelingSalesmanHeldKarpTest {

	@Test
	public void testDifferentCases() {
		double[][] distance = { { 0, 12, 3, 9, 6, 1, 2 }, { 12, 0, 6, 1, 8, 2, 10 }, { 3, 6, 0, 6, 7, 11, 7 },
				{ 9, 1, 6, 0, 9, 10, 3 }, { 6, 8, 7, 9, 0, 1, 11 }, { 1, 2, 11, 10, 1, 0, 12 },
				{ 2, 10, 7, 3, 11, 12, 0 } };

		double cost = TravelingSalesmanHeldKarp.minCost(distance, null);
		assertEquals(19.0, cost);

		double[][] distance1 = { { 0, 1, 15, 6 }, { 2, 0, 7, 3 }, { 9, 6, 0, 12 }, { 10, 4, 8, 0 } };

		cost = TravelingSalesmanHeldKarp.minCost(distance1, null);
		assertEquals(21.0, cost);
	}

	@Test
	public void testRoute() {
		double[][] distance1 = { { 0, 1, 15, 6 }, { 2, 0, 7, 3 }, { 9, 6, 0, 12 }, { 10, 4, 8, 0 } };
		List<Integer> correct = Arrays.asList(0, 1, 3, 2, 0);
		List<Integer> ret = new ArrayList<Integer>();
		TravelingSalesmanHeldKarp.minCost(distance1, ret);
		assertEquals(correct, ret);
	}

}
