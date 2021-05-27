package gr.ntua.vrp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gr.ntua.vrp.CompartmentedVehicle;

class CompartmentedVehicleTest {
	
	private CompartmentedVehicle vehicle;
	
	@BeforeEach
	public void setUp() throws Exception {
		Integer[] compartments = {10, 20, 20, 30};
		vehicle = new CompartmentedVehicle(compartments);
	}

	@Test
	@DisplayName("First test")
	void testCheckIfFits() {
		int[] order1 = { 30, 40, 10 };
		int[] order2 = { 20, 10, 20, 30 };
		int[] order3 = { 20, 20, 20 };
		int[] order4 = { 20, 20, 20, 20 };
		assertEquals(true, vehicle.checkIfFits(order1),
				"First test should be true");
		assertEquals(true, vehicle.checkIfFits(order2),
				"Second test should be true");
		assertEquals(true, vehicle.checkIfFits(order3),
				"Third test should be true");
		assertEquals(false, vehicle.checkIfFits(order4),
				"Fourth test should be false");
	}
}
