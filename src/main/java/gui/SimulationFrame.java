package gui;

import bussinesslogic.SelectionPolicy;
import bussinesslogic.SimulationManager;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private JTextField tfNumberOfClients = new JTextField(6);
    private JTextField tfNumberOfQueues = new JTextField(6);
    private JTextField tfTimeLimit = new JTextField(6);
    private JTextField tfMaxProcessingTime = new JTextField(6);
    private JTextField tfMinProcessingTime = new JTextField(6);
    private JTextField tfMaxArrivalTime = new JTextField(6);
    private JTextField tfMinArrivalTime = new JTextField(6);
    private JComboBox<String> combo = new JComboBox<>(new String[]{"SHORTEST_QUEUE", "SHORTEST_TIME"});
    private ExecutorService managerExecutor = Executors.newSingleThreadExecutor();
    private SimulationPanel visualPanel = new SimulationPanel();
    private JButton btnResults = new JButton("Display Results");
    private JLabel resultsLabel = new JLabel("Average Waiting Time:");
    private JLabel lbPeakHr = new JLabel("Peak Hour:");

    public SimulationFrame() {
        setTitle("Queue Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        JPanel operatorPanel = createOperatorPanel();
        JPanel simulationScreen = createSimulationScreen();
        JPanel resultsPanel = createResultsPanel();

        mainPanel.add(operatorPanel, "SETUP");
        mainPanel.add(simulationScreen, "SIMULATION");
        mainPanel.add(resultsPanel, "RESULTS");

        this.add(mainPanel);
        cardLayout.show(mainPanel, "SETUP");
        setVisible(true);
    }

    private JPanel createOperatorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.lightGray.brighter());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, "Number of Clients:", tfNumberOfClients, c, 0);
        addRow(panel, "Number of Queues:", tfNumberOfQueues, c, 1);
        addRow(panel, "Time Limit (s):", tfTimeLimit, c, 2);
        addRow(panel, "Min Service Time:", tfMinProcessingTime, c, 3);
        addRow(panel, "Max Service Time:", tfMaxProcessingTime, c, 4);
        addRow(panel, "Min Arrival Time:", tfMinArrivalTime, c, 5);
        addRow(panel, "Max Arrival Time:", tfMaxArrivalTime, c, 6);
        addRow(panel, "Strategy Type:", combo, c, 7);

        JButton btnStart = new JButton("Start Simulation");
        c.gridx = 0; c.gridy = 8; c.gridwidth = 2;
        c.insets = new Insets(20, 5, 5, 5);
        panel.add(btnStart, c);

        btnStart.addActionListener(e -> {
            if (validFields()) {
                cardLayout.show(mainPanel, "SIMULATION");
                startSimulation();
            }
        });

        return panel;
    }

    private void addRow(JPanel p, String label, JComponent comp, GridBagConstraints c, int y) {
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = y;
        p.add(new JLabel(label), c);
        c.gridx = 1;
        p.add(comp, c);
    }

    private JPanel createSimulationScreen() {
        JPanel screen = new JPanel(new BorderLayout());

        JLabel simLabel = new JLabel("Real-Time Queue Simulation");
        simLabel.setHorizontalAlignment(SwingConstants.CENTER);
        simLabel.setFont(new Font("Arial", Font.BOLD, 22));
        simLabel.setForeground(Color.MAGENTA.darker());
        simLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.lightGray.brighter());

        JButton btnStop = new JButton("Stop & Return to Setup");
        btnStop.addActionListener(e -> cardLayout.show(mainPanel, "SETUP"));

        btnResults.setVisible(false);
        btnResults.addActionListener(e -> cardLayout.show(mainPanel, "RESULTS"));

        btnPanel.add(btnStop);
        btnPanel.add(btnResults);

        screen.add(simLabel, BorderLayout.NORTH);
        screen.add(new JScrollPane(visualPanel), BorderLayout.CENTER);
        screen.add(btnPanel, BorderLayout.SOUTH);

        return screen;
    }

    public JPanel createResultsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
        JLabel titleLabel = new JLabel("Simulation Results");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.TOP);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.MAGENTA.darker());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        c.insets = new Insets(20, 10, 30, 10);
        panel.add(titleLabel, c);

        c.gridx = 0; c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        lbPeakHr.setFont(new Font("Arial", Font.BOLD, 18));
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 18));

        c.gridy = 2;
        panel.add(resultsLabel, c);

        c.gridy = 3;
        panel.add(lbPeakHr, c);

        JButton btnSetup = new JButton("Return to Setup");
        btnSetup.addActionListener(e -> {
            btnResults.setVisible(false);
            cardLayout.show(mainPanel, "SETUP");
        });

        c.gridy = 4;
        c.insets = new Insets(10, 10, 10, 10);
        btnSetup.setFont(new Font("Arial", Font.BOLD, 20));
        btnSetup.setForeground(Color.RED.darker());
        c.insets = new Insets(20, 20, 20, 20);
        panel.add(btnSetup, c);

        return  panel;
    }

    public void onSimulationFinished(float avgWait, int peak, int maxClients) {
        SwingUtilities.invokeLater(() -> {
            resultsLabel.setText("Average Waiting Time: " + avgWait);
            lbPeakHr.setText("Peak Hour: " + peak + " with " + maxClients + " clients");

            btnResults.setVisible(true);
            btnResults.revalidate();
            btnResults.repaint();
        });
    }

    public boolean validFields() {
        try {
            if (Integer.parseInt(tfNumberOfClients.getText()) <= 0 ||
                    Integer.parseInt(tfNumberOfQueues.getText()) <= 0 ||
                    Integer.parseInt(tfTimeLimit.getText()) <= 0) {
                JOptionPane.showMessageDialog(this, "All numbers must be greater than 0");
                return false;
            }
            if (Integer.parseInt(tfMinArrivalTime.getText()) > Integer.parseInt(tfMaxArrivalTime.getText())) {
                JOptionPane.showMessageDialog(this, "Min Arrival cannot be greater than Max Arrival");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers");
            return false;
        }
        return true;
    }

    public void startSimulation() {
        int clients = Integer.parseInt(tfNumberOfClients.getText());
        int queues = Integer.parseInt(tfNumberOfQueues.getText());
        int time = Integer.parseInt(tfTimeLimit.getText());
        int maxP = Integer.parseInt(tfMaxProcessingTime.getText());
        int minP = Integer.parseInt(tfMinProcessingTime.getText());
        int maxA = Integer.parseInt(tfMaxArrivalTime.getText());
        int minA = Integer.parseInt(tfMinArrivalTime.getText());

        SelectionPolicy policy = combo.getSelectedIndex() == 0 ?
                SelectionPolicy.SHORTEST_QUEUE : SelectionPolicy.SHORTEST_TIME;

        SimulationManager manager = new SimulationManager(this, clients, queues, time, maxP, minP, maxA, minA, policy);

        managerExecutor.execute(manager);
    }

    public SimulationPanel getVisualPanel() {
        return visualPanel;
    }
}