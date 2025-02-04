import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Transaction implements Serializable {
    String type; // "Income" or "Expense"
    String category;
    String description;
    double amount;

    Transaction(String type, String category, String description, double amount) {
        this.type = type;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return type + " | " + category + " | " + description + " - $" + amount;
    }
}

public class ExpenseTracker extends JFrame implements ActionListener {
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> transactionList;
    private JTextField descriptionField, amountField;
    private JComboBox<String> typeComboBox, categoryComboBox;
    private JLabel balanceLabel;
    private static final String DATA_FILE = "transactions.dat";

    public ExpenseTracker() {
        setTitle("Expense Tracker");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        inputPanel.add(new JLabel("Type:"));
        String[] types = {"Income", "Expense"};
        typeComboBox = new JComboBox<>(types);
        inputPanel.add(typeComboBox);

        inputPanel.add(new JLabel("Category:"));
        String[] categories = {"Salary", "Food", "Transportation", "Utilities", "Entertainment", "Other"};
        categoryComboBox = new JComboBox<>(categories);
        inputPanel.add(categoryComboBox);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        JButton addButton = new JButton("Add Transaction");
        addButton.addActionListener(this);
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        // Transaction List
        transactionList = new JList<>(listModel);
        add(new JScrollPane(transactionList), BorderLayout.CENTER);

        // Balance Label
        balanceLabel = new JLabel("Balance: $0.00");
        add(balanceLabel, BorderLayout.SOUTH);

        // Load transactions from file
        loadTransactions();
        updateBalance();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String type = (String) typeComboBox.getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();
        String description = descriptionField.getText();
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);
            Transaction transaction = new Transaction(type, category, description, amount);
            transactions.add(transaction);
            listModel.addElement(transaction.toString());
            descriptionField.setText("");
            amountField.setText("");
            updateBalance();
            saveTransactions();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount.");
        }
    }

    private void updateBalance() {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t.type.equals("Income")) {
                balance += t.amount;
            } else {
                balance -= t.amount;
            }
        }
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }

    private void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving transactions.");
        }
    }

    private void loadTransactions() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                transactions = (ArrayList<Transaction>) ois.readObject();
                for (Transaction t : transactions) {
                    listModel.addElement(t.toString());
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading transactions.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTracker().setVisible(true));
    }
}
