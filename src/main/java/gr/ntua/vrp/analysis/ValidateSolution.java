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
			boolean feasible = true;

			String line = br.readLine();
			while (line != null) {
				line = line.strip();
				if (line.endsWith(".vrp")) {
					instance = line;
					feasible = true;

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
					for (int i = 0; i < vehicles.length; ++i) {
						Vehicle v = vehicles[i];
						String vehName = v.getName();
						if (vehName == null)
							vehName = String.valueOf(i);
						vehicle.put(vehName, v);
					}
				} else if (line.startsWith("Vehicle")) {
					String[] split = line.split(":");
					Vehicle veh = vehicle.get(split[0].replaceFirst("Vehicle", "").strip());
					String[] route = (String[]) Stream.of(split[1].split("->")).toArray(String[]::new);
					List<Integer> demands = new ArrayList<>();
					for (int i = 1; i < route.length; ++i) {
						Node curNode = node.get(route[i]);
						int current = curNode.NodeId;
						int previous = node.get(route[i - 1]).NodeId;
						cost += distances[previous][current];
						visited.put(route[i], true);
						for (int d : curNode.demands)
							if (d > 0)
								demands.add(d);
					}
					if (!veh.checkIfFits(demands.stream().mapToInt(i -> i).toArray())) {
						System.out.println("In instance " + instance + " :");
						System.out.println("Route: " + split[1] + " is infeasible");
						feasible = false;
					}
				} else if (line.startsWith("Best")) {
					String[] split = line.split(":");
					double value = Double.parseDouble(split[1].strip());
					if (Math.abs(cost - value) > 0.05) {
						System.out.println("In instance " + instance + " :");
						System.out.print("Cost" + split[1] + " is different than actual cost ");
						System.out.println(cost);
						feasible = false;
					}
					if (visited.containsValue(false)) {
						feasible = false;
						for (String key : visited.keySet()) {
							if (visited.get(key) == false)
								System.out.println("Node " + key + " not visited");
						}
					}

					if (feasible)
						System.out.println(instance + " feasible");
				}
				line = br.readLine();
			}
		}
	}

}
