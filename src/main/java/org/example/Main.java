package org.example;

import bussinesslogic.SimulationManager;
import gui.SimulationFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new SimulationFrame().setVisible(true);
        });
    }
}