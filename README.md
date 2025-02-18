TrafficSimulationService:
– Uses a volatile field for the current traffic light state to ensure visibility between threads.
– Runs a dedicated thread (cycleTrafficLightLoop) to cycle through RED (5 sec), GREEN (5 sec), and YELLOW (2 sec).
– Maintains a thread‑safe queue for vehicles. A scheduled task runs every second to check if the light is GREEN and, if so, processes one vehicle.
– Uses an ExecutorService to simulate the crossing (with a delay).

REST Controller:
– Exposes endpoints to view the current state (GET /api/traffic/state), view the number of waiting vehicles (GET /api/traffic/queue), and add a vehicle (POST /api/traffic/vehicle?id=...).

Models:
– Simple classes for Vehicle and an enum for TrafficLightState.

This design leverages Spring Boot’s scheduling capabilities along with Java multithreading and synchronization (using a BlockingQueue and volatile variables) to simulate real-time traffic control.

