import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class FinanceTracker {
    private final String FILE_NAME = "transactions.txt";
    private final List<Transaction> transactions = new ArrayList<>();
    private final Scanner input = new Scanner(System.in);

    public void run() {
        loadFromFile();
        actions();
    }

    // Main method to run the finance tracker
    private void actions() {
        while (true) {
            System.out.println("\n--- Finance Tracker ---");
            System.out.println("1. Add Transaction");
            System.out.println("2. Remove Transaction");
            System.out.println("3. Edit Transaction");
            System.out.println("4. View Profile");
            System.out.println("5. Search Transactions");
            System.out.println("6. Exit");
            int action = input.nextInt();
            input.nextLine();

            switch (action) {
                case 1 -> addTransaction();
                case 2 -> removeTransaction();
                case 3 -> editTransaction();
                case 4 -> viewProfile();
                case 5 -> filterTransactions();
                case 6 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    //Method of adding a transaction to file
    private void addTransaction() {
        System.out.println("Income or Expense:");
        String type = input.nextLine();

        if (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense")) {
            System.out.println("Invalid type.");
            return;
        }

        System.out.print("Amount: ");
        int amount = input.nextInt();
        input.nextLine();

        System.out.print("Name: ");
        String name = input.nextLine();

        LocalDate date = getUserDateInput();

        Transaction t = new Transaction(amount, type, name, date);
        transactions.add(t);
        saveToFile();
        System.out.println("Transaction added: " + t.display());
    }

    //Method of removing a transaction from file
    private void removeTransaction() {
        System.out.print("Enter name of transaction to delete: ");
        String name = input.nextLine();

        transactions.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .forEach(System.out::println);

        System.out.print("Enter ID to delete: ");
        int id = input.nextInt();
        input.nextLine();

        transactions.removeIf(t -> t.getId() == id && t.getName().equalsIgnoreCase(name));
        saveToFile();
        System.out.println("Transaction removed.");

        System.out.println("Remaining transactions: ");
        transactions.forEach(t -> System.out.println(t.display()));
    }

    //editing a transaction from file
    private void editTransaction() {
        System.out.print("Enter name of transaction to edit: ");
        String name = input.nextLine();

        transactions.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .forEach(System.out::println);

        System.out.print("Enter ID to edit: ");
        int id = input.nextInt();
        input.nextLine();

        Transaction t = transactions.stream().filter(tx -> tx.getId() == id).findFirst().orElse(null);
        if (t == null) {
            System.out.println("Transaction not found.");
            return;
        }

        System.out.println("1. Name\n2. Amount\n3. Type\n4. Date");
        int choice = input.nextInt();
        input.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("New name: ");
                t.setTName(input.nextLine());
            }
            case 2 -> {
                System.out.print("New amount: ");
                t.setAmount(input.nextInt());
                input.nextLine();
            }
            case 3 -> {
                System.out.print("New type (income/expense): ");
                t.setType(input.nextLine());
            }
            case 4 -> t.setDate(getUserDateInput());
            default -> System.out.println("Invalid choice.");
        }
        saveToFile();
        System.out.println("Updated: " + t.display());
    }

    //
    private void viewProfile() {
        int incomeCount = 0, expenseCount = 0;
        double incomeSum = 0, expenseSum = 0;

        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("income")) {
                incomeCount++;
                incomeSum += t.getAmount();
            } else {
                expenseCount++;
                expenseSum += t.getAmount();
            }
        }

        System.out.println("Total transactions: " + transactions.size());
        System.out.println("Income transactions: " + incomeCount + ", Total income: " + incomeSum);
        System.out.println("Expense transactions: " + expenseCount + ", Total expenses: " + expenseSum);
    }

    // Method to filter transactions based on user input
    private void filterTransactions()
    {
        System.out.println("Search By: ");
        System.out.println("1. Transaction Type");
        System.out.println("2. Amount Range");
        System.out.println("3. Date Range");
        System.out.println("4. Name");
        int choice = input.nextInt();
        input.nextLine();

        switch(choice) {
            case 1 -> {
                System.out.println("Income or Expense");
                String type = input.nextLine().toLowerCase();
                for(Transaction t: transactions){
                    if(t.getType().equalsIgnoreCase(type))
                        System.out.println(t.display());
                }
            }
            case 2 -> {
                System.out.println("Enter Minimum Amount");
                int min = input.nextInt();
                System.out.println("Enter Maximum Amount");
                int max = input.nextInt();
                input.nextLine();

                for(Transaction t: transactions){
                    if(t.getAmount()>= min && t.getAmount()<= max)
                        System.out.println(t.display());
                }
            }
            case 3 -> {
                System.out.println("Enter Earliest Date");
                LocalDate min = getUserDateInput();
                System.out.println("Enter Latest Date");
                LocalDate max = getUserDateInput();
                for(Transaction t: transactions){
                    if((t.getDate().equals(min) || t.getDate().isAfter(min)) && (t.getDate().equals(max) || t.getDate().isBefore(max)))
                        System.out.println(t.display());
                }
            }
            case 4 -> {
                System.out.println("Enter keyword to filter by");
                String name = input.nextLine();
                for(Transaction t: transactions){
                    if(t.getName().toLowerCase().contains(name))
                        System.out.println(t.display());
                }
            }
        }

    }

    // Get user input for date with validation
    private LocalDate getUserDateInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String inputDate = input.nextLine();
            try {
                return LocalDate.parse(inputDate, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format.");
            }
        }
    }

    // Load transactions from file
    private void loadFromFile() {
        transactions.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(Transaction.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("No saved transactions found.");
        }
    }

    // Save transactions to file
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Transaction t : transactions) {
                writer.write(t.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}