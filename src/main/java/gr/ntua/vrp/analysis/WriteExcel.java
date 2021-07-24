package gr.ntua.vrp.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import gr.ntua.vrp.CompartmentedVehicle;
import gr.ntua.vrp.VRPLibReader;
import gr.ntua.vrp.Vehicle;

public class WriteExcel {

	private static int[] findLoading(Integer[] compartments, List<Integer> demands) {
		int[] filled = new int[compartments.length];
		TreeMap<Integer, List<Integer>> items = new TreeMap<>();
		for (int i = 0; i < compartments.length; ++i) {
			Integer comp = compartments[i];
			if (items.containsKey(comp)) {
				List<Integer> indices = items.get(comp);
				indices.add(i);
			} else {
				List<Integer> indices = new LinkedList<>();
				indices.add(i);
				items.put(comp, indices);
			}
		}

		ArrayList<Integer> bins = new ArrayList<Integer>();

		for (int order : demands) {
			bins.add(order);
		}

		for (int order : bins) {
			int satisfied = 0;
			while (satisfied < order) {
				if (items.size() == 0) {
					System.out.println("Couldn't find loading with heuristic. CPLEX needed");
					for (int j = 0; j < demands.size(); ++j)
						filled[j] = demands.get(j);
					for (int j = demands.size(); j < filled.length; ++j)
						filled[j] = 0;
					return filled;
				}

				Integer ceil = items.ceilingKey(order - satisfied);
				if (ceil != null) {
					satisfied += ceil;
					List<Integer> indices = items.get(ceil);
					Integer index = indices.get(0);
					filled[index] = order - satisfied + ceil;
					indices.remove(0);
					if (indices.size() == 0)
						items.remove(ceil);
				} else {
					Integer maxKey = items.lastKey();
					satisfied += maxKey;
					List<Integer> indices = items.get(maxKey);
					Integer index = indices.get(0);
					filled[index] = maxKey;
					indices.remove(0);
					if (indices.size() == 0)
						items.remove(maxKey);
				}
			}
		}

		return filled;
	}

	public static void main(String[] args) throws IOException {
		File file = new File(args[0]);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			VRPLibReader vrp;
			String instance = null;
			int[][] demands = null;
			Vehicle[] vehicles = null;
			int maxdemands = 0;
			int maxcompartments = 0;

			Workbook workbook = new XSSFWorkbook();
			Sheet curSheet = null;

			CellStyle defaultStyle = workbook.createCellStyle();
			Font defaultFont = workbook.createFont();
			defaultStyle.setFont(defaultFont);

			String line = br.readLine();
			while (line != null) {
				if (line.endsWith(".vrp")) {
					System.out.println();
					instance = line;
					vrp = new VRPLibReader(new File(instance));
					demands = vrp.getDemand();
					vehicles = vrp.getVehicles();
					maxdemands = 0;

					for (Vehicle veh : vehicles) {
						int compslength = ((CompartmentedVehicle) veh).getCompartments().length;
						if (compslength > maxcompartments) {
							maxcompartments = compslength;
						}
					}

					System.out.println(instance);

					String[] instanceSplit = instance.split("/");
					String sheetName = instanceSplit[instanceSplit.length - 1];
					curSheet = workbook.createSheet(sheetName);
					curSheet.setFitToPage(true);

					for (int i = 1; i < demands.length; ++i) {
						Row row = curSheet.createRow(i - 1);
						if (demands[i].length > maxdemands)
							maxdemands = demands[i].length;
						for (int j = 0; j < demands[i].length; ++j) {
							Cell cell = row.createCell(j);
							cell.setCellValue(demands[i][j]);
							cell.setCellStyle(defaultStyle);
						}
					}

					CellStyle compStyle = workbook.createCellStyle();
					Font compFont = workbook.createFont();
					compFont.setBold(true);
					compStyle.setFont(compFont);
					for (int i = 0; i < vehicles.length; ++i) {
						Row row = curSheet.getRow(2 * i);
						if (row == null) {
							row = curSheet.createRow(2 * i);
						}

						Integer[] comps = ((CompartmentedVehicle) vehicles[i]).getCompartments();
						for (int j = 0; j < comps.length; ++j) {
							Cell cell = row.createCell(j + maxdemands + 1);
							cell.setCellValue(comps[j]);
							cell.setCellStyle(compStyle);
						}
					}
				} else if (line.startsWith("Vehicle")) {
					String[] split = line.split(":");
					int[] route = Stream.of(split[1].split("->")).mapToInt(Integer::parseInt).toArray();
					int load = 0;
					int vehicleIndex = Integer.parseInt(split[0].split(" ")[1].strip());
					int capacity = vehicles[vehicleIndex].getCapacity();
					List<Integer> routeDemands = new ArrayList<>();

					for (int i = 1; i < route.length; ++i) {
						int current = route[i];
						for (int j = 0; j < demands[current].length; ++j) {
							int curDemand = demands[current][j];
							load += curDemand;
							routeDemands.add(curDemand);
						}
					}

					int[] filled = findLoading(((CompartmentedVehicle) vehicles[vehicleIndex]).getCompartments(),
					        routeDemands);

					Row firstRow = curSheet.getRow(2 * vehicleIndex);
					Cell routeCell = firstRow.createCell(maxdemands + 1 + maxcompartments + 1);
					routeCell.setCellValue(split[1].strip());
					routeCell.setCellStyle(defaultStyle);

					Row row = curSheet.getRow(2 * vehicleIndex + 1);
					if (row == null) {
						row = curSheet.createRow(2 * vehicleIndex + 1);
					}
					for (int j = 0; j < filled.length; ++j) {
						Cell cell = row.createCell(j + maxdemands + 1);
						cell.setCellValue(filled[j]);
						cell.setCellStyle(defaultStyle);
					}

					if (load > capacity) {
						System.out.println("In instance " + instance + " :");
						System.out.println("Route: " + split[1] + " is infeasible");
					}
				}
				line = br.readLine();
			}

			String path = file.getParent() + "/";
			String[] filenameSplit = file.getName().split("\\.");
			path = path + filenameSplit[0] + "_routes.xlsx";
			try (FileOutputStream outputStream = new FileOutputStream(path)) {
				workbook.write(outputStream);
				System.out.println("Excel saved to " + path);
				workbook.close();
			}
		}
	}
}
