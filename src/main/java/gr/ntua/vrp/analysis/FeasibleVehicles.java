package gr.ntua.vrp.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gr.ntua.vrp.VRPLibReader;
import gr.ntua.vrp.Vehicle;

public class FeasibleVehicles {

	public static void main(String[] args) throws IOException {
		String instanceFile = args[0];
		String routeFile = args[1];

		try (VRPLibReader reader = new VRPLibReader(new File(instanceFile));
		        BufferedReader br = new BufferedReader(new FileReader(routeFile))) {
			int[][] demands = reader.getDemand();
			Vehicle[] vehicles = reader.getVehicles();

			String line = br.readLine();
			while (line != null) {
				if (line.startsWith("Vehicle")) {
					String route = line.split(":")[1];
					String[] routeSplit = route.split("->");
					int[] routeNodes = new int[routeSplit.length - 2];
					int totalDemands = 0;
					int[] routeDemands;
					List<Integer> currentFeasible = new ArrayList<>();

					for (int i = 1; i < routeSplit.length - 1; ++i) {
						routeNodes[i - 1] = Integer.parseInt(routeSplit[i].trim());
						totalDemands += demands[i].length;
					}
					routeDemands = new int[totalDemands];

					for (int i = 0, arrayIndex = 0; i < routeNodes.length; ++i) {
						int[] curDemands = demands[routeNodes[i]];
						System.arraycopy(curDemands, 0, routeDemands, arrayIndex, curDemands.length);
						arrayIndex += curDemands.length;
					}

					for (int i = 0; i < vehicles.length; ++i) {
						Vehicle veh = vehicles[i];
						if (veh.checkIfFits(routeDemands)) {
							currentFeasible.add(i);
						}
					}

					System.out.print("Route " + route + " is feasible on vehicles: ");
					for (Integer i : currentFeasible) {
						System.out.print(i);
						System.out.print(" ");
					}
					System.out.println();
				}
				line = br.readLine();
			}
		}
	}
}
