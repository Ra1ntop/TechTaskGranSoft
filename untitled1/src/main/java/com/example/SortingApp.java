package com.example;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SortingApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel introPanel;
    private JPanel sortPanel;
    private JTextField numberInput;
    private JPanel numbersPanel;
    private List<Integer> numbers;
    private boolean isDescending = true;
    private JButton sortButton;
    private Timer sortingTimer;
    private List<SortStep> sortSteps;
    private int currentStepIndex;

    private static class SortStep {
        final List<Integer> numbers;
        final int highlightIndex1;
        final int highlightIndex2;

        SortStep(List<Integer> numbers, int highlightIndex1, int highlightIndex2) {
            this.numbers = new ArrayList<>(numbers);
            this.highlightIndex1 = highlightIndex1;
            this.highlightIndex2 = highlightIndex2;
        }
    }

    public SortingApp() {
        setTitle("Sorting Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        createIntroPanel();
        createSortPanel();

        cardPanel.add(introPanel, "Intro");
        cardPanel.add(sortPanel, "Sort");

        add(cardPanel);

        cardLayout.show(cardPanel, "Intro");
    }

    private void createIntroPanel() {
        introPanel = new JPanel();
        introPanel.setLayout(new FlowLayout());

        numberInput = new JTextField(10);
        JButton enterButton = new JButton("Enter");

        enterButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(numberInput.getText());
                if (count > 0) {
                    generateNumbers(count);
                    cardLayout.show(cardPanel, "Sort");
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a positive number.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        introPanel.add(new JLabel("Enter number of random numbers:"));
        introPanel.add(numberInput);
        introPanel.add(enterButton);
    }

    private void createSortPanel() {
        sortPanel = new JPanel(new BorderLayout());

        numbersPanel = new JPanel(new GridLayout(0, 1));
        JScrollPane scrollPane = new JScrollPane(numbersPanel);

        sortButton = new JButton("Sort (Descending)");
        sortButton.addActionListener(e -> {
            if (sortingTimer != null && sortingTimer.isRunning()) {
                sortingTimer.stop();
            }
            isDescending = !isDescending;
            sortButton.setText("Sort (" + (isDescending ? "Descending" : "Ascending") + ")");
            sortButton.setEnabled(false);
            sortSteps = new ArrayList<>();
            quickSort(new ArrayList<>(numbers), 0, numbers.size() - 1);
            currentStepIndex = 0;

            sortingTimer = new Timer(500, evt -> {
                if (currentStepIndex < sortSteps.size()) {
                    SortStep step = sortSteps.get(currentStepIndex);
                    numbers = new ArrayList<>(step.numbers);
                    updateNumbersPanel(step.highlightIndex1, step.highlightIndex2);
                    currentStepIndex++;
                } else {
                    ((Timer) evt.getSource()).stop();
                    sortButton.setEnabled(true);
                    updateNumbersPanel(-1, -1);
                }
            });
            sortingTimer.start();
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            if (sortingTimer != null) {
                sortingTimer.stop();
            }
            cardLayout.show(cardPanel, "Intro");
            numberInput.setText("");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sortButton);
        buttonPanel.add(resetButton);

        sortPanel.add(scrollPane, BorderLayout.CENTER);
        sortPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generateNumbers(int count) {
        numbers = new ArrayList<>();
        Random random = new Random();
        boolean hasSmallNumber = false;

        for (int i = 0; i < count; i++) {
            int num = random.nextInt(1000) + 1;
            if (num <= 30) hasSmallNumber = true;
            numbers.add(num);
        }

        if (!hasSmallNumber) {
            numbers.set(random.nextInt(count), random.nextInt(30) + 1);
        }

        updateNumbersPanel(-1, -1);
    }

    private void updateNumbersPanel(int highlightIndex1, int highlightIndex2) {
        numbersPanel.removeAll();
        int columns = (numbers.size() + 9) / 10;
        numbersPanel.setLayout(new GridLayout(0, columns));

        for (int i = 0; i < numbers.size(); i++) {
            Integer num = numbers.get(i);
            JButton button = new JButton(num.toString());
            if (i == highlightIndex1 || i == highlightIndex2) {
                button.setBackground(Color.YELLOW);
            }
            button.addActionListener(e -> {
                if (num <= 30) {
                    int currentCount = numbers.size();
                    generateNumbers(currentCount);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a value smaller or equal to 30.");
                }
            });
            numbersPanel.add(button);
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private void quickSort(List<Integer> arr, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(arr, low, high);

            quickSort(arr, low, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, high);
        }
    }

    private int partition(List<Integer> arr, int low, int high) {
        int pivot = arr.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if ((isDescending && arr.get(j) >= pivot) || (!isDescending && arr.get(j) <= pivot)) {
                i++;
                Collections.swap(arr, i, j);
                sortSteps.add(new SortStep(arr, i, j));
            }
        }

        Collections.swap(arr, i + 1, high);
        sortSteps.add(new SortStep(arr, i + 1, high));
        return i + 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SortingApp().setVisible(true));
    }
}