package gr.ntua.vrp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteTest {

	private Route route;

	@BeforeEach
	public void setUp() {
		double[][] distance = { { 0, 1, 15, 6 }, { 2, 0, 7, 3 }, { 9, 6, 0, 12 }, { 10, 4, 8, 0 } };
		route = new Route(distance);
	}

	@Test
	@DisplayName("Run 1")
	void firstTest() {
		Node depot = new Node(0, 0);
		route.addNode(depot);
		route.addNode(new Node(1, 0));
		route.addNode(new Node(2, 0));
		route.addNode(new Node(3, 0));
		route.addNode(depot);
		assertEquals(30.0, route.getCost());
		assertEquals(-9.0, route.optimize());
	}

	@Test
	@DisplayName("Run 2")
	void secondTest() {
		Node depot = new Node(0, 0);
		route.addNode(depot);
		route.addNode(new Node(3, 0));
		route.addNode(new Node(1, 0));
		route.addNode(new Node(2, 0));
		route.addNode(depot);
		assertEquals(26.0, route.getCost());
		assertEquals(-5.0, route.optimize());
	}

	@Test
	@DisplayName("Run 3")
	void thirdTest() {
		Node depot = new Node(0, 0);
		route.addNode(depot);
		route.addNode(new Node(3, 0));
		route.addNode(new Node(1, 0));
		route.addNode(depot);
		assertEquals(12.0, route.getCost());
		assertEquals(0.0, route.optimize());
	}

}
