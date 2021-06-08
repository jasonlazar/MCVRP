package com.interview.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Date 11/17/2015
 * 
 * @author Tushar Roy
 * @author Iasonas Lazaridis
 *
 *         Help Karp method of finding tour of traveling salesman.
 *
 *         Time complexity - O(2^n * n^2) Space complexity - O(2^n)
 *
 *         https://en.wikipedia.org/wiki/Held%E2%80%93Karp_algorithm
 */
public class TravelingSalesmanHeldKarp {

	private static int INFINITY = 100000000;

	private static class Index {
		int currentVertex;
		Set<Integer> vertexSet;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Index index = (Index) o;

			if (currentVertex != index.currentVertex)
				return false;
			return !(vertexSet != null ? !vertexSet.equals(index.vertexSet) : index.vertexSet != null);
		}

		@Override
		public int hashCode() {
			int result = currentVertex;
			result = 31 * result + (vertexSet != null ? vertexSet.hashCode() : 0);
			return result;
		}

		private static Index createIndex(int vertex, Set<Integer> vertexSet) {
			Index i = new Index();
			i.currentVertex = vertex;
			i.vertexSet = vertexSet;
			return i;
		}
	}

	private static class SetSizeComparator implements Comparator<Set<Integer>> {
		@Override
		public int compare(Set<Integer> o1, Set<Integer> o2) {
			return o1.size() - o2.size();
		}
	}

	public static double minCost(double[][] distance, List<Integer> route) {

		// stores intermediate values in map
		Map<Index, Double> minCostDP = new HashMap<>();
		Map<Index, Integer> parent = new HashMap<>();

		List<Set<Integer>> allSets = generateCombination(distance.length - 1);

		for (Set<Integer> set : allSets) {
			for (int currentVertex = 1; currentVertex < distance.length; currentVertex++) {
				if (set.contains(currentVertex)) {
					continue;
				}
				Index index = Index.createIndex(currentVertex, set);
				double minCost = INFINITY;
				int minPrevVertex = 0;
				// to avoid ConcurrentModificationException copy set into another set while
				// iterating
				Set<Integer> copySet = new HashSet<>(set);
				for (int prevVertex : set) {
					double cost = distance[prevVertex][currentVertex] + getCost(copySet, prevVertex, minCostDP);
					if (cost < minCost) {
						minCost = cost;
						minPrevVertex = prevVertex;
					}
				}
				// this happens for empty subset
				if (set.size() == 0) {
					minCost = distance[0][currentVertex];
				}
				minCostDP.put(index, minCost);
				parent.put(index, minPrevVertex);
			}
		}

		Set<Integer> set = new HashSet<>();
		for (int i = 1; i < distance.length; i++) {
			set.add(i);
		}
		double min = Integer.MAX_VALUE;
		int prevVertex = -1;
		// to avoid ConcurrentModificationException copy set into another set while
		// iterating
		Set<Integer> copySet = new HashSet<>(set);
		for (int k : set) {
			double cost = distance[k][0] + getCost(copySet, k, minCostDP);
			if (cost < min) {
				min = cost;
				prevVertex = k;
			}
		}

		parent.put(Index.createIndex(0, set), prevVertex);
		if (route != null)
			route.addAll(getTour(parent, distance.length));
		return min;
	}

	@SuppressWarnings("unused")
	private void printTour(Map<Index, Integer> parent, int totalVertices) {
		Set<Integer> set = new HashSet<>();
		for (int i = 0; i < totalVertices; i++) {
			set.add(i);
		}
		Integer start = 0;
		Deque<Integer> stack = new LinkedList<>();
		while (true) {
			stack.push(start);
			set.remove(start);
			start = parent.get(Index.createIndex(start, set));
			if (start == null) {
				break;
			}
		}
		StringJoiner joiner = new StringJoiner("->");
		stack.forEach(v -> joiner.add(String.valueOf(v)));
		System.out.println("\nTSP tour");
		System.out.println(joiner.toString());
	}

	private static List<Integer> getTour(Map<Index, Integer> parent, int totalVertices) {
		Set<Integer> set = new HashSet<>();
		for (int i = 0; i < totalVertices; i++) {
			set.add(i);
		}
		Integer start = 0;
		LinkedList<Integer> stack = new LinkedList<>();
		while (true) {
			stack.push(start);
			set.remove(start);
			start = parent.get(Index.createIndex(start, set));
			if (start == null) {
				break;
			}
		}
		return stack;
	}

	private static double getCost(Set<Integer> set, int prevVertex, Map<Index, Double> minCostDP) {
		set.remove(prevVertex);
		Index index = Index.createIndex(prevVertex, set);
		double cost = minCostDP.get(index);
		set.add(prevVertex);
		return cost;
	}

	private static List<Set<Integer>> generateCombination(int n) {
		int input[] = new int[n];
		for (int i = 0; i < input.length; i++) {
			input[i] = i + 1;
		}
		List<Set<Integer>> allSets = new ArrayList<>();
		int result[] = new int[input.length];
		generateCombination(input, 0, 0, allSets, result);
		Collections.sort(allSets, new SetSizeComparator());
		return allSets;
	}

	private static void generateCombination(int input[], int start, int pos, List<Set<Integer>> allSets, int result[]) {
		if (pos == input.length) {
			return;
		}
		Set<Integer> set = createSet(result, pos);
		allSets.add(set);
		for (int i = start; i < input.length; i++) {
			result[pos] = input[i];
			generateCombination(input, i + 1, pos + 1, allSets, result);
		}
	}

	private static Set<Integer> createSet(int input[], int pos) {
		if (pos == 0) {
			return new HashSet<>();
		}
		Set<Integer> set = new HashSet<>();
		for (int i = 0; i < pos; i++) {
			set.add(input[i]);
		}
		return set;
	}
}
