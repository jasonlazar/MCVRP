package gr.ntua.vrp;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import gr.ntua.vrp.init.GreedySolver;
import gr.ntua.vrp.init.RandomSolver;
import gr.ntua.vrp.tabu.TabuSearchSolver;

public class VRPRunner {
	@Parameter(names = { "--algorithm", "-alg" })
	private String alg = "tabu";
	@Parameter(names = { "--instance", "-i" })
	public String instance = "datasets/cvrp/big/Golden_20.vrp";
	@Parameter(names = "--iterations")
	public int iterations = 5;
	@Parameter(names = "--tabu")
	public Integer tenure = 5;
	@Parameter(names = { "--restarts", "-r" })
	public int restarts = 2;
	@Parameter(names = "--init")
	public String initFile;
	@Parameter(names = "--round")
	public boolean round = false;

	public static void main(String[] args) throws IOException {
		VRPRunner jct = new VRPRunner();
		JCommander jCommander = new JCommander(jct, args);
		jCommander.setProgramName(VRPRunner.class.getSimpleName());

		Solver s = null;

		switch (jct.alg) {
		case "tabu":
			s = new TabuSearchSolver(jct);
			break;
		case "greedy":
			s = new GreedySolver(jct);
			break;
		case "random":
			s = new RandomSolver(jct);
			break;
		default:
			System.out.println("Unknown algorithm: " + jct.alg);
			System.exit(1);
		}

		s.solve().print();

	}
}
