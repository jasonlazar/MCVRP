package gr.ntua.vrp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InitFromRoutesSolver extends Solver {
	private String filename;

	public InitFromRoutesSolver(VRPRunner jct) throws IOException {
		super(jct);
		this.filename = jct.initFile;
	}

	@Override
	public Solver solve() {
		List<List<Integer>> initialRoutes = generateInitialRoutes();
		int vehIndex = 0;

		for (List<Integer> route : initialRoutes) {
			checkVehiclesUsed(vehIndex);
			vehicles[vehIndex].appendNode(nodes[0]);
			int i = 0;
			while (i < route.size()) {
				checkVehiclesUsed(vehIndex);
				int nodeIndex = route.get(i);
				Node node = nodes[nodeIndex];
				if (vehicles[vehIndex].checkIfFits(node.demands)) {
					vehicles[vehIndex].appendNode(node);
					++i;
				} else {
					vehicles[vehIndex].appendNode(nodes[0]);
					checkVehiclesUsed(++vehIndex);
					vehicles[vehIndex].appendNode(nodes[0]);
				}
			}
			vehicles[vehIndex].appendNode(nodes[0]);
			vehIndex++;
		}

		for (++vehIndex; vehIndex < vehicles.length; ++vehIndex) {
			vehicles[vehIndex].appendNode(nodes[0]);
			vehicles[vehIndex].appendNode(nodes[0]);
		}

		cost = 0;
		for (Vehicle veh : vehicles) {
			List<Node> route = veh.routes;
			for (int i = 0; i < route.size() - 1; ++i) {
				int firstIndex = route.get(i).NodeId;
				int secondIndex = route.get(i + 1).NodeId;
				cost += distances[firstIndex][secondIndex];
			}
		}
		return this;
	}

	private List<List<Integer>> generateInitialRoutes() {
		List<List<Integer>> routes = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split(": ");
				if (split.length == 1)
					break;
				List<Integer> route = Arrays.stream(split[1].split(" +")).map(Integer::parseInt)
				        .collect(Collectors.toList());
				routes.add(route);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("The file " + filename + " does not exist");
			return null;
		} catch (IOException e) {
			System.err.println("Something went wrong");
			return null;
		}
		return routes;
	}

	private void checkVehiclesUsed(int index) {
		if (index >= vehicles.length) {
			System.out.println("Impossible to obtain a solution from that init file");
			System.exit(1);
		}
	}

}
