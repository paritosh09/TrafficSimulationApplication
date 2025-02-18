package com.example.TrafficSimulationApplication.service;

 
import org.springframework.stereotype.Service;

import com.example.TrafficSimulationApplication.model.TrafficLightState;
import com.example.TrafficSimulationApplication.model.Vehicle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TrafficSimulationService {
    private static final Logger logger = LoggerFactory.getLogger(TrafficSimulationService.class);
    private volatile TrafficLightState currentState;
    private BlockingQueue<Vehicle> vehicleQueue;
    private ExecutorService vehicleExecutor;
    private ScheduledExecutorService scheduledExecutor;

    public TrafficSimulationService() {
        currentState = TrafficLightState.RED;
        vehicleQueue = new LinkedBlockingQueue<>();
        vehicleExecutor = Executors.newSingleThreadExecutor();
        scheduledExecutor = Executors.newScheduledThreadPool(1);
    }

    /**
     * Initializes scheduled tasks for processing vehicles and cycling traffic light states.
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing Traffic Simulation Service...");
        scheduledExecutor.scheduleAtFixedRate(this::processVehicles, 0, 1, TimeUnit.SECONDS);
        new Thread(this::cycleTrafficLightLoop).start();
    }

    /**
     * Shuts down executors before destroying the service.
     */
    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down Traffic Simulation Service...");
        vehicleExecutor.shutdown();
        scheduledExecutor.shutdown();
    }

    /**
     * Adds a vehicle to the queue.
     * @param vehicle the vehicle to be added
     */
    public void addVehicle(Vehicle vehicle) {
        vehicleQueue.offer(vehicle);
        logger.info("Vehicle {} added to queue. Queue size: {}", vehicle.getId(), vehicleQueue.size());
    }

    /**
     * Processes vehicles when the traffic light is green.
     */
    private void processVehicles() {
        if (currentState == TrafficLightState.GREEN && !vehicleQueue.isEmpty()) {
            Vehicle vehicle = vehicleQueue.poll();
            if (vehicle != null) {
                vehicleExecutor.submit(() -> {
                    try {
                        logger.info("Vehicle {} is crossing the intersection.", vehicle.getId());
                        Thread.sleep(2000 + ThreadLocalRandom.current().nextInt(2000));
                        logger.info("Vehicle {} has crossed the intersection.", vehicle.getId());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Vehicle crossing interrupted.", e);
                    }
                });
            }
        }
    }

    /**
     * Cycles through traffic light states with predefined delays.
     */
    private void cycleTrafficLightLoop() {
        try {
            while (true) {
                changeState(TrafficLightState.RED);
                Thread.sleep(5000);
                changeState(TrafficLightState.GREEN);
                Thread.sleep(5000);
                changeState(TrafficLightState.YELLOW);
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Traffic light cycle interrupted.", e);
        }
    }

    /**
     * Changes the traffic light state.
     * @param newState the new state of the traffic light
     */
    private void changeState(TrafficLightState newState) {
        currentState = newState;
        logger.info("Traffic Light changed to {}", currentState);
    }

    /**
     * Gets the current traffic light state.
     * @return current traffic light state
     */
    public TrafficLightState getCurrentState() {
        return currentState;
    }

    /**
     * Gets the number of vehicles waiting in the queue.
     * @return number of vehicles in queue
     */
    public int getQueueSize() {
        return vehicleQueue.size();
    }
}
