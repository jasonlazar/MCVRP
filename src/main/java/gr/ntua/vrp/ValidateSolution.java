package gr.ntua.vrp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import thiagodnf.jacof.util.io.InstanceReader;

public class ValidateSolution {

	public static void main(String[] args) throws IOException {
		String file = args[0];
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			VRPLibReader vrp;
			double[][] distances = null;
			int[][] demands = null;
			String instance = null;
			double cost = 0;
			int capacity = 0;

			String line = br.readLine();
			while (line != null) {
				if (line.endsWith(".vrp")) {
					instance = line;
					vrp = new VRPLibReader(new InstanceReader(new File(instance)));
					distances = vrp.getDistance();
					demands = vrp.getDemand();
					cost = 0;
					capacity = vrp.getVehicleCapacity();
				} else if (line.startsWith("Vehicle")) {
					String[] split = line.split(":");
					int[] route = Stream.of(split[1].split("->")).mapToInt(Integer::parseInt).toArray();
					int load = 0;
					for (int i = 1; i < route.length; ++i) {
						int current = route[i];
						int previous = route[i - 1];
						load += demands[current][0];
						cost += distances[previous][current];
					}
					if (load > capacity) {
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
						System.out.println(instance + " feasible");
					}
				}
				line = br.readLine();
			}
		}
	}

}
