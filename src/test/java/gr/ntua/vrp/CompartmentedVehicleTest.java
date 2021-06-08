package gr.ntua.vrp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompartmentedVehicleTest {

	private CompartmentedVehicle vehicle;

	@BeforeEach
	public void setUp() throws Exception {
		Integer[] compartments = { 10, 20, 20, 30 };
		vehicle = new CompartmentedVehicle(null, compartments);
	}

	@Test
	@DisplayName("Test fitness of demands")
	void testCheckIfFits() {
		int[] order1 = { 30, 40, 10 };
		int[] order2 = { 20, 10, 20, 30 };
		int[] order3 = { 20, 20, 20 };
		int[] order4 = { 20, 20, 20, 20 };
		assertEquals(true, vehicle.checkIfFits(order1), "First test should be true");
		assertEquals(true, vehicle.checkIfFits(order2), "Second test should be true");
		assertEquals(true, vehicle.checkIfFits(order3), "Third test should be true");
		assertEquals(false, vehicle.checkIfFits(order4), "Fourth test should be false");
	}

	@Test
	@DisplayName("Test fitness while excluding a node")
	void testCheckIfFitsWithoutNode() {
		Node n1 = new Node(1, 20);
		Node n2 = new Node(2, 10);
		vehicle.appendNode(n1);
		vehicle.appendNode(n2);
		int[] order1 = { 30, 40 };
		int[] order2 = { 20, 10, 30 };
		assertEquals(true, vehicle.checkIfFits(order1, n1), "First test should be true");
		assertEquals(false, vehicle.checkIfFits(order1, n2), "Second test should be false");
		assertEquals(true, vehicle.checkIfFits(order2, n1), "Third test should be true");
		assertEquals(true, vehicle.checkIfFits(order2, n2), "Fourth test should be true");
	}
}
