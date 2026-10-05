import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DiseasePredictionGUI extends JFrame {

    JCheckBox fever, cough, headache, fatigue;
    JButton predictBtn, resetBtn;
    JLabel result, suggestion;
    JProgressBar confidenceBar;

    public DiseasePredictionGUI() {

        setTitle("AI Disease Predictor");
        setSize(600, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color c1 = new Color(0, 0, 0);
                Color c2 = new Color(30, 30, 60);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(null);

        // Title
        JLabel title = new JLabel("AI Disease Predictor");
        title.setBounds(140, 20, 350, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(0, 255, 200));
        panel.add(title);

        // Checkboxes
        fever = createCheckBox("Fever", 120);
        cough = createCheckBox("Cough", 160);
        headache = createCheckBox("Headache", 200);
        fatigue = createCheckBox("Fatigue", 240);

        panel.add(fever);
        panel.add(cough);
        panel.add(headache);
        panel.add(fatigue);

        // Buttons
        predictBtn = createButton("Predict", 120, 300, new Color(0, 200, 150));
        resetBtn = createButton("Reset", 300, 300, new Color(220, 50, 50));

        panel.add(predictBtn);
        panel.add(resetBtn);

        // Result
        result = new JLabel("Result: ");
        result.setBounds(100, 360, 400, 30);
        result.setFont(new Font("Segoe UI", Font.BOLD, 16));
        result.setForeground(Color.YELLOW);
        panel.add(result);

        // Suggestion
        suggestion = new JLabel("");
        suggestion.setBounds(100, 390, 400, 30);
        suggestion.setForeground(Color.WHITE);
        panel.add(suggestion);

        // Confidence Bar
        confidenceBar = new JProgressBar(0, 100);
        confidenceBar.setBounds(100, 420, 400, 20);
        confidenceBar.setStringPainted(true);
        panel.add(confidenceBar);

        add(panel);

        // Predict Action
        predictBtn.addActionListener(e -> {

            result.setText("Analyzing...");
            confidenceBar.setValue(0);

            javax.swing.Timer timer = new javax.swing.Timer(20, new ActionListener() {
                int progress = 0;

                public void actionPerformed(ActionEvent evt) {
                    progress++;
                    confidenceBar.setValue(progress);

                    if (progress >= 100) {
                        ((javax.swing.Timer) evt.getSource()).stop();

                        int[] input = {
                                fever.isSelected() ? 1 : 0,
                                cough.isSelected() ? 1 : 0,
                                headache.isSelected() ? 1 : 0,
                                fatigue.isSelected() ? 1 : 0
                        };

                        String output = predict(input);
                        result.setText(output);

                        if (output.contains("Flu"))
                            suggestion.setText("Suggestion: Take rest & stay hydrated");
                        else if (output.contains("Cold"))
                            suggestion.setText("Suggestion: Drink warm fluids");
                        else if (output.contains("Migraine"))
                            suggestion.setText("Suggestion: Avoid screen & take rest");
                        else if (output.contains("Malaria"))
                            suggestion.setText("Suggestion: Consult doctor immediately");
                        else
                            suggestion.setText("Suggestion: Monitor symptoms");
                    }
                }
            });

            timer.start();
        });

        // Reset
        resetBtn.addActionListener(e -> {
            fever.setSelected(false);
            cough.setSelected(false);
            headache.setSelected(false);
            fatigue.setSelected(false);
            result.setText("Result: ");
            suggestion.setText("");
            confidenceBar.setValue(0);
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JCheckBox createCheckBox(String text, int y) {
        JCheckBox cb = new JCheckBox(text);
        cb.setBounds(220, y, 200, 30);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cb.setForeground(Color.WHITE);
        cb.setOpaque(false);
        return cb;
    }

    private JButton createButton(String text, int x, int y, Color color) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 150, 40);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }

    // Prediction Logic
    public String predict(int[] input) {

        int[][] data = {
                {1,1,1,1}, {1,1,0,1}, {1,1,1,0},
                {0,1,1,0}, {0,1,0,0},
                {0,0,1,1}, {0,0,1,0},
                {1,0,0,1}, {1,0,0,0}
        };

        String[] labels = {
                "Flu","Flu","Flu",
                "Cold","Cold",
                "Migraine","Migraine",
                "Malaria","Malaria"
        };

        Map<String, Double> probs = new HashMap<>();
        Set<String> diseases = new HashSet<>(Arrays.asList(labels));

        for (String d : diseases) {
            double prob = 1.0;

            for (int i = 0; i < 4; i++) {
                int match = 0, total = 0;

                for (int j = 0; j < data.length; j++) {
                    if (labels[j].equals(d)) {
                        total++;
                        if (data[j][i] == input[i])
                            match++;
                    }
                }

                prob *= (match + 1.0) / (total + 2);
            }

            probs.put(d, prob);
        }

        String best = "";
        double max = 0;

        for (String d : probs.keySet()) {
            if (probs.get(d) > max) {
                max = probs.get(d);
                best = d;
            }
        }

        double sum = probs.values().stream().mapToDouble(Double::doubleValue).sum();
        double confidence = (max / sum) * 100;

        if (confidence < 40)
            return "Result: Not Sure";

        return "Result: " + best + " (" + String.format("%.2f", confidence) + "%)";
    }

    public static void main(String[] args) {
        new DiseasePredictionGUI();
    }
}
