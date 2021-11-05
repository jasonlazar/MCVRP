# Multi-Compartment Vehicle Routing Problem

The Multi-Compartment Vehicle Routing Problem is a variation of the classic
[VRP](https://en.wikipedia.org/wiki/Vehicle_routing_problem) in which vehicles are compartmentalized.
Each compartment can only hold products of the same type. In our case the vehicles do not possess flow meters and thus
every order must be loaded into separate compartments.
This problem is also referred to as the Petrol Station Replenishment Problem.

## Algorithm

A tabu search algorithm is used for tackling the problem.
You can find more information in my diploma thesis.
A link will be added soon.

## Dependencies

- Java 11
- Maven
- CPLEX 20.1

## Usage

In order to build the project run:

```
mvn install
```

To solve a particular instance (e.g. X-101-k25) run the following command:

```
java -Djava.library.path=/path/to/CPLEX/shared/library -jar target/vrp-1.0.0.jar --instance datasets/hfmcvrp/big/X-n101-k25.vrp
```

Our program can also handle classic VRP instances.

## Notes

The code in this repository is based on [this](https://github.com/afurculita/VehicleRoutingProblem) GitHub repository.