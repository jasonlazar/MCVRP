package gr.ntua.vrp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.cplex.IloCplex;

public class CompartmentedVehicle extends Vehicle {
	private Integer[] compartments;
	
	public CompartmentedVehicle(Integer[] comps) {
		super();
		this.compartments = comps.clone();
		Arrays.sort(this.compartments, Collections.reverseOrder());
	}

	@Override
	public Vehicle makeCopy() {
		return new CompartmentedVehicle(compartments);
	}
	
	@Override
	public void AddNode(Node Customer) {		
		routes.add(Customer);
		this.currentLocation = Customer.NodeId;
	}

	@Override
	public boolean CheckIfFits(int[] dem) {
		Deque<Integer> items = new ArrayDeque<Integer>();
		for (Integer comp : compartments)
			items.addLast(comp);

		ArrayList<Integer> bins = new ArrayList<Integer>();
		for (int order: dem) {
			bins.add(order);
		}
		for (Node customer : routes) {
			for (int order : customer.demands)
				bins.add(order);
		}
		
		for (int order : bins) {
			int filled = 0;
			while (filled < order) {
				if (items.size() == 0) return solveWithCplex(bins);
				else if (filled + items.peekFirst() <= order)
					filled += items.removeFirst();
				else filled += items.removeLast();
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
			for (int i=0; i<norders; ++i)
				for (int j=0; j<ncompartments; ++j)
					y[i][j] = cplex.boolVar();
			
			IloLinearIntExpr obj = cplex.linearIntExpr();
			int[] coeffs = new int[ncompartments];
			Arrays.fill(coeffs, 1);
			for (IloIntVar[] boolArray : y) {
				obj.addTerms(boolArray, coeffs);
			}
			cplex.addMinimize(obj);
			
			for (int i=0; i<norders; ++i) {
				IloLinearIntExpr expr = cplex.linearIntExpr();
				for (int j=0; j<ncompartments; ++j) {
					expr.addTerm(compartments[j], y[i][j]);
				}
				cplex.addLe(orders.get(i), expr);
			}
			
			for (int j=0; j<ncompartments; ++j) {
				IloLinearIntExpr expr = cplex.linearIntExpr();
				for (int i=0; i<norders; ++i)
					expr.addTerm(y[i][j], 1);
				cplex.addLe(expr, 1);
			}
			cplex.setOut(null);
			ret = cplex.solve();
			cplex.end();
		} catch (IloException e) {
			System.err.println("Concert exception '" + e + "' caught");
		}
		return ret;
	}

}
