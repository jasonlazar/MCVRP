package gr.ntua.vrp.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.VRPLibReader;
import gr.ntua.vrp.Vehicle;

public class ValidateSolution {

	public static void main(String[] args) throws IOException {
		String file = args[0];
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			VRPLibReader vrp;
			double[][] distances = null;
			Map<String, Node> node = null;
			String instance = null;
			double cost = 0;
			Map<String, Vehicle> vehicle = null;
			Map<String, Boolean> visited = null;

			String line = br.readLine();
			while (line != null) {
				if (line.endsWith(".vrp")) {
					instance = line;
					vrp = new VRPLibReader(new File(instance));
					distances = vrp.getDistance();
					Node[] nodes = vrp.getNodes();
					node = new HashMap<>();
					visited = new HashMap<>();
					for (Node n : nodes) {
						node.put(n.name, n);
						visited.put(n.name, false);
					}
					cost = 0;
					Vehicle[] vehicles = vrp.getVehicles();
					vehicle = new HashMap<>();
					for (Vehicle v : vehicles)
						vehicle.put(v.getName(), v);
				} else if (line.startsWith("Vehicle")) {
					String[] split = line.split(":");
					Vehicle veh = vehicle.get(split[0].replaceFirst("Vehicle", "").strip());
					String[] route = (String[]) Stream.of(split[1].split("->")).toArray(String[]::new);
					List<Integer> demands = new ArrayList<>();
					for (int i = 1; i < route.length; ++i) {
						int current = node.get(route[i]).NodeId;
						int previous = node.get(route[i - 1]).NodeId;
						cost += distances[previous][current];
						visited.put(route[i], true);
					}
					if (!veh.checkIfFits(demands.stream().mapToInt(i -> i).toArray())) {
						System.out.println("In instance " + instance + " :");
						System.out.println("Route: " + split[1] + " is infeasible");
					}
				} else if (line.startsWith("Best")) {
					String[] split = line.split(":");
					double value = Double.parseDouble(split[1].strip());
					if (Math.abs(cost - value) > 0.05) {
						System.out.println("In instance " + instance + " :");
						System.out.print("Cost" + split[1] + " is different than actual cost ");
						System.out.println(cost);
					} else {
						if (!visited.containsValue(false))
							System.out.println(instance + " feasible");
						else {
							for (String key : visited.keySet()) {
								if (visited.get(key) == false)
									System.out.println("Node " + key + " not visited");
							}
						}
					}
				}
				line = br.readLine();
			}
		}
	}

}
