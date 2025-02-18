package com.example.TrafficSimulationApplication.controller;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.TrafficSimulationApplication.model.TrafficLightState;
import com.example.TrafficSimulationApplication.model.Vehicle;
import com.example.TrafficSimulationApplication.service.TrafficSimulationService;

@RestController
@RequestMapping("/api/traffic")
public class TrafficSimulationController {

    @Autowired
    private TrafficSimulationService simulationService;
    
    // Get the current traffic light state
    @GetMapping("/state")
    public TrafficLightState getTrafficLightState() {
        return simulationService.getCurrentState();
    }
    
    // Get the number of vehicles waiting
    @GetMapping("/queue")
    public int getVehicleQueueSize() {
        return simulationService.getQueueSize();
    }
    
    // Add a new vehicle (simulate vehicle arrival)
    @PostMapping("/vehicle")
    public String addVehicle(@RequestParam("id") String id) {
        Vehicle vehicle = new Vehicle(id);
        simulationService.addVehicle(vehicle);
        return "Vehicle " + id + " added to the queue.";
    }
}