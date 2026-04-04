package org.example;

import bussinesslogic.SimulationManager;

public class Main {
    public static void main(String[] args) {
        SimulationManager simulationManager = new SimulationManager();
        Thread thread = new Thread(simulationManager);
        thread.start();
    }
}