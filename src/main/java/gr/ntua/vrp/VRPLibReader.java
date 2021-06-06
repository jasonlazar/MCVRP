package gr.ntua.vrp;

import thiagodnf.jacof.util.io.InstanceReader;

import java.time.LocalTime;

public class VRPLibReader {

	private InstanceReader reader;

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

	public VRPLibReader(InstanceReader reader) {
		this.reader = reader;

		readHeader();
		if (edge_type == "DISTANCE_MATRIX")
			readDistances();
		else
			readCoordinates();
		readDemand();
		// readPickup();
		// readTimeWindows();
		// readStandtime();
		// readDepots();
		if (edge_type != "DISTANCE_MATRIX")
			convertCoordToDistance();
	}

	private void readHeader() {
		String line = reader.readLine();

		while (!line.equalsIgnoreCase("NODE_COORD_SECTION") && !line.equalsIgnoreCase("DISTANCE_SECTION")) {
			String[] split = line.split(":");

			String key = split[0].trim();

			if (key.equalsIgnoreCase("TYPE")) {
				type = split[1].trim();
			}

			else if (key.equalsIgnoreCase("DIMENSION")) {
				dimension = Integer.valueOf(split[1].trim());
			}

			else if (key.equalsIgnoreCase("EDGE_WEIGHT_TYPE")) {
				edge_type = split[1].trim();
			}

			else if (key.equalsIgnoreCase("CAPACITY")) {
				if (type.equalsIgnoreCase("CVRP"))
					vehicleCapacity = Integer.valueOf(split[1].trim());
				else {
					String[] comps = split[1].split(",");
					compartments = new Integer[comps.length];
					for (int i = 0; i < comps.length; ++i)
						compartments[i] = Integer.valueOf(comps[i].trim());
				}
			}

			line = reader.readLine();

			if (line == null) {
				break;
			}
		}
	}

	private void readDistances() {
		distance = new double[dimension][dimension];
		String line = reader.readLine();
		for (int i = 0; i < dimension; ++i) {
			String[] split = line.split("\\s+");
			for (int j = 0; j < dimension; ++j) {
				distance[i][j] = Integer.parseInt(split[j + 1].trim());
			}
			line = reader.readLine();
		}
	}

	private void readCoordinates() {
		coord = new double[dimension][2];

		String line = reader.readLine();
		while (!line.equalsIgnoreCase("DEMAND_SECTION")) {
			parseRow(line, coord);

			line = reader.readLine();
		}
	}

	private void parseRow(String line, double[][] coord) {
		String[] split = line.split("\\s+");

		int i = Integer.valueOf(split[0].trim()) - 1;
		coord[i][0] = Double.valueOf(split[1].trim());
		coord[i][1] = Double.valueOf(split[2].trim());
	}

	private void readDemand() {
		demand = new int[dimension][];

		String line = reader.readLine();
		while (!line.equalsIgnoreCase("DEPOT_SECTION")) {

			String[] split = line.split("\\s+");

			int i = Integer.valueOf(split[0].trim()) - 1;
			int nr_demands = split.length - 1;

			demand[i] = new int[nr_demands];

			for (int j = 0; j < nr_demands; ++j) {
				demand[i][j] = Integer.valueOf(split[j + 1].trim());
			}

			line = reader.readLine();
		}
	}

	@SuppressWarnings("unused")
	private void readPickup() {
		pickup = new double[dimension][2];

		String line = reader.readLine();
		while (!line.equalsIgnoreCase("TIME_WINDOW_SECTION")) {
			parseRow(line, pickup);

			line = reader.readLine();
		}
	}

	@SuppressWarnings("unused")
	private void readTimeWindows() {
		timeWindows = new LocalTime[dimension][2];

		String line = reader.readLine();
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

			line = reader.readLine();
		}
	}

	@SuppressWarnings("unused")
	private void readStandtime() {
		standTime = new int[dimension];

		String line = reader.readLine();
		while (!line.equalsIgnoreCase("DEPOT_SECTION")) {
			String[] split = line.split("\\s+");

			int i = Integer.valueOf(split[0].trim()) - 1;
			standTime[i] = Integer.valueOf(split[1].trim());

			line = reader.readLine();
		}
	}

	@SuppressWarnings("unused")
	private void readDepots() {
		depots = new int[2];

		String line = reader.readLine();
		int i = 0;
		while (!line.equalsIgnoreCase("EOF")) {
			depots[i] = Double.valueOf(line.trim()).intValue();
			i++;

			line = reader.readLine();
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

					distance[i][j] = euclideanDistance(x1, y1, x2, y2);
					distance[j][i] = distance[i][j];
				}
			}
		}
	}

	private static double euclideanDistance(double x1, double y1, double x2, double y2) {
		double xDistance = Math.abs(x1 - x2);
		double yDistance = Math.abs(y1 - y2);

		return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
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
}
