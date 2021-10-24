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

public class Loading {

	public static void main(String[] args) throws IOException {
		String file = args[0];
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			VRPLibReader vrp;
			String instance = null;
			Map<String, Node> node = null;
			Vehicle[] vehicles = null;
			int capacity = 0;
			List<Double> loadings = null;

			String line = br.readLine();
			while (line != null) {
				if (line.endsWith(".vrp")) {
					if (loadings != null) {
						System.out.println("Gmean of loading: " + geometricMean(loadings));
						System.out.println(
						        "Amean of loading: " + loadings.stream().mapToDouble(d -> d).average().orElse(0.0));
					}
					System.out.println();
					instance = line;
					vrp = new VRPLibReader(new File(instance));
					node = new HashMap<>();
					loadings = new ArrayList<>();
					Node[] nodes = vrp.getNodes();
					for (Node n : nodes) {
						node.put(n.name, n);
					}
					vehicles = vrp.getVehicles();
					System.out.println(instance);
				} else if (line.startsWith("Vehicle")) {
					String[] split = line.split(":");
					Node[] route = Stream.of(split[1].split("->")).map(node::get).toArray(Node[]::new);
					int load = 0;
					int vehicleIndex = Integer.parseInt(split[0].split(" ")[1].strip());
					capacity = vehicles[vehicleIndex].getCapacity();

					for (Node n : route) {
						for (int x : n.demands)
							load += x;
					}
					if (load > capacity) {
						System.out.println("In instance " + instance + " :");
						System.out.println("Route: " + split[1] + " is infeasible");
					}
					double loading = load * 100.0 / capacity;
					System.out.print("Load% of vehicle " + String.valueOf(vehicleIndex) + " = ");
					System.out.println(loading);
					loadings.add(loading);
				}
				line = br.readLine();
			}
			if (loadings != null) {
				System.out.println("Gmean of loading: " + geometricMean(loadings));
				System.out.println("Amean of loading: " + loadings.stream().mapToDouble(d -> d).average().orElse(0.0));
			}
		}
	}

	private static double geometricMean(List<Double> data) {
		double sum = data.get(0);

		for (int i = 1; i < data.size(); i++) {
			sum *= data.get(i);
		}
		return Math.pow(sum, 1.0 / data.size());
	}

}
