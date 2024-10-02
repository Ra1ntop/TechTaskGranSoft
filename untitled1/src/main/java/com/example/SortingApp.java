package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
            isDescending = !isDescending;
            sortButton.setText("Sort (" + (isDescending ? "Descending" : "Ascending") + ")");
            quickSort(0, numbers.size() - 1);
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
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

        updateNumbersPanel();
    }

    private void updateNumbersPanel() {
        numbersPanel.removeAll();
        int columns = (numbers.size() + 9) / 10;
        numbersPanel.setLayout(new GridLayout(0, columns));

        for (Integer num : numbers) {
            JButton button = new JButton(num.toString());
            button.addActionListener(e -> {
                if (num <= 30) {
                    generateNumbers(numbers.size());
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a value smaller or equal to 30.");
                }
            });
            numbersPanel.add(button);
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int partitionIndex = partition(low, high);

            quickSort(low, partitionIndex - 1);
            quickSort(partitionIndex + 1, high);

            updateNumbersPanel();
        }
    }

    private int partition(int low, int high) {
        int pivot = numbers.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if ((isDescending && numbers.get(j) >= pivot) || (!isDescending && numbers.get(j) <= pivot)) {
                i++;
                Collections.swap(numbers, i, j);
            }
        }

        Collections.swap(numbers, i + 1, high);
        return i + 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SortingApp().setVisible(true);
        });
    }
}