package gr.ntua.vrp.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import gr.ntua.vrp.VRPLibReader;
import gr.ntua.vrp.Vehicle;

public class Loading {

	public static void main(String[] args) throws IOException {
		String file = args[0];
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			VRPLibReader vrp;
			String instance = null;
			int[][] demands = null;
			Vehicle[] vehicles = null;
			int capacity = 0;

			String line = br.readLine();
			while (line != null) {
				if (line.endsWith(".vrp")) {
					System.out.println();
					instance = line;
					vrp = new VRPLibReader(new File(instance));
					demands = vrp.getDemand();
					vehicles = vrp.getVehicles();
					System.out.println(instance);
				} else if (line.startsWith("Vehicle")) {
					String[] split = line.split(":");
					int[] route = Stream.of(split[1].split("->")).mapToInt(Integer::parseInt).toArray();
					int load = 0;
					int vehicleIndex = Integer.parseInt(split[0].split(" ")[1].strip());
					capacity = vehicles[vehicleIndex].getCapacity();

					for (int i = 1; i < route.length; ++i) {
						int current = route[i];
						load += demands[current][0];
					}
					if (load > capacity) {
						System.out.println("In instance " + instance + " :");
						System.out.println("Route: " + split[1] + " is infeasible");
					}
					System.out.print("Load% of vehicle " + String.valueOf(vehicleIndex) + " = ");
					System.out.println(load * 100.0 / capacity);
				}
				line = br.readLine();
			}
		}
	}

}
