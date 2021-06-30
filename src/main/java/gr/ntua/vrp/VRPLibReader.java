package gr.ntua.vrp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;

import com.google.common.base.Preconditions;

public class VRPLibReader implements Closeable {

	private BufferedReader reader;

	private int dimension;
	private int vehicleCapacity;
	private double[][] coord;
	private double[][] distance;
	private int[][] demand;
	private double[][] pickup;
	private LocalTime[][] timeWindows;
	private int[] standTime;
	private int[] depots;
	private Integer[] compartments;
	private String type;
	private String edge_type;
	private int noOfVehicles;
	private Vehicle[] vehicles;

	private static final int MARK_BUFFER = 1000;

	public VRPLibReader(File filename) throws IOException {
		Preconditions.checkNotNull(filename, "The filename cannot be null");
		Preconditions.checkArgument(filename.exists(), "The file does not exist");
		Preconditions.checkArgument(filename.isFile(), "The filename cannot be a directory");

		this.reader = new BufferedReader(new FileReader(filename));

		readHeader();

		boolean endOfFile = false;

		while (!endOfFile) {
			String line = readLineAndTrim();

			switch (line) {
			case "DISTANCE_SECTION":
				readDistances();
				break;
			case "NODE_COORD_SECTION":
				readCoordinates();
				convertCoordToDistance();
				break;
			case "DEMAND_SECTION":
				readDemand();
				break;
			case "VEHICLE_SECTION":
				readVehicles();
				break;
			case "DEPOT_SECTION":
				readDepots();
				break;
			case "EOF":
				endOfFile = true;
				break;
			default:
				throw new IOException("Unexpected line: " + line);
			// readPickup();
			// readTimeWindows();
			// readStandtime();
			}
		}
	}

	private void readHeader() throws IOException {
		boolean endOfHeader = false;

		while (!endOfHeader) {
			reader.mark(MARK_BUFFER);
			String line = readLineAndTrim();
			String[] split = line.split(":");

			String key = split[0].trim();

			switch (key.toUpperCase()) {
			case "NAME":
			case "COMMENT":
				break;
			case "TYPE":
				type = split[1].trim();
				break;
			case "DIMENSION":
				dimension = Integer.valueOf(split[1].trim());
				break;
			case "EDGE_WEIGHT_TYPE":
				edge_type = split[1].trim();
				break;
			case "CAPACITY":
				if (type.equalsIgnoreCase("CVRP"))
					vehicleCapacity = Integer.valueOf(split[1].trim());
				else {
					String[] comps = split[1].split(",");
					compartments = new Integer[comps.length];
					for (int i = 0; i < comps.length; ++i)
						compartments[i] = Integer.valueOf(comps[i].trim());
				}
				break;
			case "VEHICLES":
				noOfVehicles = Integer.valueOf(split[1].trim());
				break;
			default:
				reader.reset();
				endOfHeader = true;
			}
		}
	}

	private void readDistances() throws IOException {
		distance = new double[dimension][dimension];
		for (int i = 0; i < dimension; ++i) {
			String line = readLineAndTrim();
			String[] split = line.split("\\s+");
			for (int j = 0; j < dimension; ++j) {
				distance[i][j] = Double.parseDouble(split[j + 1].trim());
			}
		}
	}

	private void readCoordinates() throws IOException {
		coord = new double[dimension][2];

		for (int i = 0; i < dimension; ++i) {
			String line = readLineAndTrim();
			parseRow(line, coord);
		}
	}

	private void parseRow(String line, double[][] coord) {
		String[] split = line.split("\\s+");

		int i = Integer.valueOf(split[0].trim()) - 1;
		coord[i][0] = Double.valueOf(split[1].trim());
		coord[i][1] = Double.valueOf(split[2].trim());
	}

	private void readDemand() throws IOException {
		demand = new int[dimension][];

		for (int iter = 0; iter < dimension; ++iter) {
			String line = readLineAndTrim();
			String[] split = line.split("\\s+");

			int i = Integer.valueOf(split[0].trim()) - 1;
			int nr_demands = split.length - 1;

			demand[i] = new int[nr_demands];

			for (int j = 0; j < nr_demands; ++j) {
				demand[i][j] = Integer.valueOf(split[j + 1].trim());
			}
		}
	}

	private void readVehicles() throws IOException {
		vehicles = new Vehicle[noOfVehicles];

		for (int i = 0; i < noOfVehicles; ++i) {
			String line = readLineAndTrim();
			if (type.equalsIgnoreCase("HFVRP")) {
				int vehCap = Integer.valueOf(line);
				vehicles[i] = new SimpleVehicle(distance, vehCap);
			} else {
				String split[] = line.split(", ");
				Integer comps[] = new Integer[split.length];
				for (int j = 0; j < split.length; ++j) {
					comps[j] = Integer.valueOf(split[j].trim());
				}
				vehicles[i] = new CompartmentedVehicle(distance, comps);
			}
		}
	}

	@SuppressWarnings("unused")
	private void readPickup() throws IOException {
		pickup = new double[dimension][2];

		String line = readLineAndTrim();
		while (!line.equalsIgnoreCase("TIME_WINDOW_SECTION")) {
			parseRow(line, pickup);

			line = readLineAndTrim();
		}
	}

	@SuppressWarnings("unused")
	private void readTimeWindows() throws IOException {
		timeWindows = new LocalTime[dimension][2];

		String line = readLineAndTrim();
		while (!line.equalsIgnoreCase("STANDTIME_SECTION")) {
			String[] split = line.split("\\s+");

			int i = Integer.valueOf(split[0].trim()) - 1;

			String startTime = split[1].trim();
			String endTime = split[2].trim();
			if (startTime.equals("")) {
				startTime = "0" + split[2].trim();
				endTime = split[3].trim();

				if (endTime.equals("")) {
					endTime = "0" + split[4].trim();
				}
			}

			timeWindows[i][0] = LocalTime.parse(startTime);
			timeWindows[i][1] = LocalTime.parse(endTime);

			line = readLineAndTrim();
		}
	}

	@SuppressWarnings("unused")
	private void readStandtime() throws IOException {
		standTime = new int[dimension];

		String line = readLineAndTrim();
		while (!line.equalsIgnoreCase("DEPOT_SECTION")) {
			String[] split = line.split("\\s+");

			int i = Integer.valueOf(split[0].trim()) - 1;
			standTime[i] = Integer.valueOf(split[1].trim());

			line = readLineAndTrim();
		}
	}

	private void readDepots() throws IOException {
		depots = new int[2];

		for (int i = 0; i < 2; ++i) {
			String line = readLineAndTrim();
			depots[i] = Double.valueOf(line.trim()).intValue();
		}
	}

	private void convertCoordToDistance() {
		distance = new double[dimension][dimension];

		for (int i = 0; i < dimension; i++) {
			for (int j = i; j < dimension; j++) {
				if (i != j) {
					double x1 = coord[i][0];
					double y1 = coord[i][1];
					double x2 = coord[j][0];
					double y2 = coord[j][1];

					if (edge_type.equals("EUC_2D")) {
						distance[i][j] = euclideanDistance(x1, y1, x2, y2);
						distance[j][i] = distance[i][j];
					}
				}
			}
		}
	}

	private static double euclideanDistance(double x1, double y1, double x2, double y2) {
		double xDistance = Math.abs(x1 - x2);
		double yDistance = Math.abs(y1 - y2);

		return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
	}

	private String readLineAndTrim() throws IOException {
		return reader.readLine().trim();
	}

	public int getDimension() {
		return dimension;
	}

	public double[][] getDistance() {
		return distance;
	}

	public int getVehicleCapacity() {
		return vehicleCapacity;
	}

	public int[][] getDemand() {
		return demand;
	}

	public int[] getDepots() {
		return depots;
	}

	public Integer[] getCompartments() {
		return compartments;
	}

	public String getType() {
		return type;
	}

	public Vehicle[] getVehicles() {
		switch (type) {
		case "CVRP":
			if (vehicles != null)
				return vehicles;
			noOfVehicles = dimension / 3;
			vehicles = new Vehicle[this.noOfVehicles];

			for (int i = 0; i < this.noOfVehicles; i++) {
				vehicles[i] = new SimpleVehicle(distance, vehicleCapacity);
			}
		case "MCVRP":
			if (vehicles != null)
				return vehicles;
			noOfVehicles = dimension * 2 / 3;
			vehicles = new Vehicle[this.noOfVehicles];

			for (int i = 0; i < this.noOfVehicles; i++) {
				vehicles[i] = new CompartmentedVehicle(distance, compartments);
			}
		case "HFMCVRP":
			return vehicles;
		default:
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
