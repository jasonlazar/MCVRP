package gr.ntua.vrp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.cplex.IloCplex;

public class CompartmentedVehicle extends Vehicle {
	private Integer[] compartments;
	static int totalCalls = 0;
	static int totalCplex = 0;

	public CompartmentedVehicle(double[][] distances, Integer[] comps) {
		super(distances, 0);
		this.compartments = comps.clone();
		Arrays.sort(this.compartments, Collections.reverseOrder());
		int sum = 0;
		for (Integer n : compartments)
			sum += n;
		this.capacity = sum;
		this.category = Arrays.toString(compartments);
	}

	@Override
	public Vehicle makeCopy() {
		Vehicle veh = new CompartmentedVehicle(distances, compartments);
		veh.setName(name);
		return veh;
	}

	@Override
	public void appendNode(Node Customer) {
		routes.add(Customer);
		this.currentLocation = Customer.nodeId;
	}

	@Override
	public boolean checkIfFits(int[] dem) {
		return checkIfFits(dem, Collections.emptyList());
	}

	@Override
	public boolean checkIfFits(int[] dem, Collection<Node> remove) {
		totalCalls++;
		TreeMap<Integer, Integer> items = new TreeMap<>();
		for (Integer comp : compartments) {
			if (items.containsKey(comp)) {
				Integer count = items.get(comp);
				items.put(comp, count + 1);
			} else {
				items.put(comp, 1);
			}
		}

		ArrayList<Integer> bins = new ArrayList<Integer>();
		int totalDemands = 0;

		for (int order : dem) {
			bins.add(order);
			totalDemands += order;
		}
		for (Node customer : routes) {
			if (remove.contains(customer))
				continue;
			if (customer.nodeId == 0)
				continue;
			for (int order : customer.demands) {
				bins.add(order);
				totalDemands += order;
			}
		}
		if ((bins.size() > compartments.length) || totalDemands > capacity)
			return false;

		for (int order : bins) {
			int filled = 0;
			while (filled < order) {
				if (items.size() == 0) {
					totalCplex++;
					return solveWithCplex(bins);
				}
				Integer ceil = items.ceilingKey(order - filled);
				if (ceil != null) {
					filled += ceil;
					Integer count = items.get(ceil);
					if (count > 1)
						items.put(ceil, count - 1);
					else
						items.remove(ceil);
				} else {
					Integer maxKey = items.lastKey();
					filled += maxKey;
					Integer count = items.get(maxKey);
					if (count > 1)
						items.put(maxKey, count - 1);
					else
						items.remove(maxKey);
				}
			}
		}
		return true;
	}

	private boolean solveWithCplex(ArrayList<Integer> orders) {
		int norders = orders.size();
		int ncompartments = compartments.length;
		boolean ret = false;

		try {
			IloCplex cplex = new IloCplex();
			IloIntVar[][] y = new IloIntVar[norders][ncompartments];
			for (int i = 0; i < norders; ++i)
				for (int j = 0; j < ncompartments; ++j)
					y[i][j] = cplex.boolVar();

			IloLinearIntExpr obj = cplex.linearIntExpr();
			int[] coeffs = new int[ncompartments];
			Arrays.fill(coeffs, 1);
			for (IloIntVar[] boolArray : y) {
				obj.addTerms(boolArray, coeffs);
			}
			cplex.addMinimize(obj);

			for (int i = 0; i < norders; ++i) {
				IloLinearIntExpr expr = cplex.linearIntExpr();
				for (int j = 0; j < ncompartments; ++j) {
					expr.addTerm(compartments[j], y[i][j]);
				}
				cplex.addLe(orders.get(i), expr);
			}

			for (int j = 0; j < ncompartments; ++j) {
				IloLinearIntExpr expr = cplex.linearIntExpr();
				for (int i = 0; i < norders; ++i)
					expr.addTerm(y[i][j], 1);
				cplex.addLe(expr, 1);
			}
			cplex.setOut(null);
			ret = cplex.solve();
			cplex.end();
			cplex.close();
		} catch (IloException e) {
			System.err.println("Concert exception '" + e + "' caught");
		}
		return ret;
	}

	public Integer[] getCompartments() {
		return compartments;
	}
}
