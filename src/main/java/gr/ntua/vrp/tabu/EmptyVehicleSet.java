package gr.ntua.vrp.tabu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gr.ntua.vrp.Node;
import gr.ntua.vrp.Vehicle;

public class EmptyVehicleSet {
	private Map<String, Set<Vehicle>> unusedVehicles;

	public EmptyVehicleSet() {
		this.unusedVehicles = new HashMap<>();
	}

	public boolean add(Vehicle v) {
		String category = v.getCategory();

		if (unusedVehicles.containsKey(category)) {
			Set<Vehicle> set = unusedVehicles.get(category);
			return set.add(v);
		} else {
			Set<Vehicle> set = new HashSet<>();
			set.add(v);
			unusedVehicles.put(category, set);
			return true;
		}
	}

	public boolean remove(Vehicle v) {
		String category = v.getCategory();

		if (!unusedVehicles.containsKey(category))
			return false;

		Set<Vehicle> set = unusedVehicles.get(category);

		if (set.contains(v)) {
			set.remove(v);
			if (set.isEmpty()) {
				unusedVehicles.remove(category);
			}
			return true;
		}
		return false;
	}

	public List<Vehicle> feasibleVehicles(int[] routeDemands, int limit) {
		List<Vehicle> feasible = new ArrayList<>();
		for (Set<Vehicle> set : unusedVehicles.values()) {
			Vehicle veh = set.iterator().next();
			if (veh.checkIfFits(routeDemands, new HashSet<Node>(veh.routes))) {
				Iterator<Vehicle> it = set.iterator();
				do {
					feasible.add(it.next());
					if (feasible.size() == limit)
						return feasible;
				} while (it.hasNext());
			}
		}
		return feasible;
	}
}
