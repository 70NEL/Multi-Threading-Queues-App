package gui;

import model.Server;
import model.Task;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationPanel extends JPanel {
    private List<Server> servers;
    private List<Task> waitingTasks;
    private int currentTime = 0;

    public void updateData(List<Server> servers, List<Task> waitingTasks, int currentTime) {
        this.servers = new ArrayList<>(servers);
        this.waitingTasks = new ArrayList<>(waitingTasks);
        this.currentTime = currentTime;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(40, 40, 40));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 22));
        g2d.drawString("TIME: " + currentTime + "s", 700, 35);

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString("WAITING LOUNGE:", 50, 30);

        int wx = 50, wy = 45;
        if (waitingTasks != null) {
            for (Task t : waitingTasks) {
                drawTaskBox(g2d, t, wx, wy, new Color(240, 240, 240));
                wx += 85;
                if (wx > 750) { wx = 50; wy += 55; }
            }
        }

        int y = wy + 80;
        if (servers == null) return;

        for (int i = 0; i < servers.size(); i++) {
            Server server = servers.get(i);

            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRect(50, y, 50, 50);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Q" + (i + 1), 62, y + 32);

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(105, y, 750, 50);

            int x = 110;
            List<Task> tasksInQueue = new ArrayList<>(server.getTasks());

            for (Task task : tasksInQueue) {
                if (task == null) continue;

                Color taskColor = (task.getServiceTime() > 0) ? new Color(100, 150, 240) : Color.RED;
                drawTaskBox(g2d, task, x, y + 5, taskColor);
                x += 85;
            }
            y += 70;
        }
        setPreferredSize(new Dimension(900, y + 100));
        revalidate();
    }

    private void drawTaskBox(Graphics2D g2d, Task t, int x, int y, Color bg) {
        g2d.setColor(bg);
        g2d.fillRoundRect(x, y, 80, 40, 5, 5);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(x, y, 80, 40, 5, 5);

        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString("ID: " + t.getId(), x + 5, y + 12);

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Arr: " + t.getArrivalTime(), x + 5, y + 24);

        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(new Color(200, 0, 0));
        g2d.drawString("SER: " + t.getServiceTime(), x + 5, y + 36);
    }
}