package gr.ntua.vrp;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import gr.ntua.vrp.greedy.GreedySolver;
import gr.ntua.vrp.tabu.TabuSearchSolver;

public class VRPRunner {
	@Parameter(names = { "--algorithm", "-alg" }, required = true)
	private String alg = "tabu";
	@Parameter(names = { "--instance", "-i" })
	public String instance = "datasets/cvrp/big/Golden_20.vrp";
	@Parameter(names = "--iterations")
	public int iterations = 5;
	@Parameter(names = "--tabu")
	public Integer TabuHorizon = 10;
	@Parameter(names = { "--restarts", "-r" })
	public int restarts = 2;
	@Parameter(names = "--init")
	public String initFile;

	public static void main(String[] args) throws IOException {
		VRPRunner jct = new VRPRunner();
		JCommander jCommander = new JCommander(jct, args);
		jCommander.setProgramName(VRPRunner.class.getSimpleName());

		switch (jct.alg) {
		case "tabu": {
			new TabuSearchSolver(jct).solve().print();
			break;
		}
		case "greedy":
		default: {
			new GreedySolver(jct).solve().print();
			break;
		}
		}
	}
}
