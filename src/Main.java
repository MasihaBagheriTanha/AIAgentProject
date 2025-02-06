import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        int balance;
        LinkedList<Product> products = new LinkedList<>();
        HashMap<String, Product> productMap = new HashMap<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your current cash balance:");
        balance = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter \"help\" to see available commands:\n");

        while (true) {
            System.out.println("Enter a command, type 'exit' to quit:");
            String command = scanner.nextLine();
            String[] parts = command.split(" ");

            if (command.equalsIgnoreCase("help")) {
                System.out.println("Available commands:\n");
                System.out.println("Exit: exits the program\n");
                System.out.println("recharge <product> <amount>: fills the storage of said product by the amount mentioned\n");
                System.out.println("sell <product> <amount>: sells the product and updates balance\n");
                System.out.println("addProduct: add a new product dynamically\n");
                System.out.println("saleDataAnalysis: sends transaction logs to OpenAI for analysis\n");
                System.out.println("advancedSaleDataAnalysis: sends transaction logs to OpenAI for more advanced analysis\n");
                System.out.println("optimizeRestock: locally analyzes sales data and recommends restocking\n");
                continue;
            }

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program...\n");
                break;
            }

            if (command.equalsIgnoreCase("addProduct")) {
                System.out.println("Enter product name:");
                String productName = scanner.nextLine();

                System.out.println("Enter factory price:");
                int productFactoryPrice = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Enter consumer price:");
                int productConsumerPrice = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Is there any initial balance in storage? (1 for yes, 0 for no)");
                int hasInitialBalance = scanner.nextInt();
                scanner.nextLine();

                Product product;
                if (hasInitialBalance == 0) {
                    product = new Product(productFactoryPrice, productConsumerPrice);
                } else {
                    System.out.println("Enter initial balance:");
                    int productInitialBalance = scanner.nextInt();
                    scanner.nextLine();
                    product = new Product(productFactoryPrice, productConsumerPrice, productInitialBalance);
                }
                products.add(product);
                productMap.put(productName, product);
                System.out.println("Product " + productName + " registered successfully!\n");
                continue;
            }

            if (parts.length == 3) {
                String action = parts[0];
                String productName = parts[1];
                int amount;

                try {
                    amount = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Please enter a valid amount.\n");
                    continue;
                }

                if (amount <= 0) {
                    System.out.println("Amount must be a positive number.\n");
                    continue;
                }

                if (!productMap.containsKey(productName)) {
                    System.out.println("Product not found. Use 'addProduct' to register new products.\n");
                    continue;
                }

                Product product = productMap.get(productName);

                if (action.equalsIgnoreCase("recharge")) {
                    int newBalance = product.setProductBalance_fill(amount, balance);
                    if (newBalance != balance) {
                        balance = newBalance;
                        logTransaction("Recharged " + amount + " of " + productName + ". New balance: " + balance);
                        System.out.println("Updated balance: " + balance + "\n");
                    }
                } else if (action.equalsIgnoreCase("sell")) {
                    int newBalance = product.setProductBalance_sell(amount, balance);
                    if (newBalance != balance) {
                        balance = newBalance;
                        logTransaction("Sold " + amount + " of " + productName + ". New balance: " + balance);
                        System.out.println("Updated balance: " + balance + "\n");
                    }
                } else {
                    System.out.println("Invalid command. Use 'help' to see available commands.\n");
                }
            } else if (command.equalsIgnoreCase("saleDataAnalysis")) {
                runSaleDataAnalysis();
            } else if (command.equalsIgnoreCase("advancedSaleDataAnalysis")) {
                runAdvancedSaleDataAnalysis();
            } else if (command.equalsIgnoreCase("optimizeRestock")) {
                runStockpileOptimization(balance, productMap);
            } else {
                System.out.println("Invalid command format. Use 'help' to see available commands.\n");
            }
        }

        System.out.println("Final balance: " + balance);
        scanner.close();
    }

    private static void logTransaction(String message) {
        try (FileWriter writer = new FileWriter("transactions.log", true)) {
            writer.write(new Date() + " - " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file.");
        }
    }

    private static void runSaleDataAnalysis() {
        File logFile = new File("transactions.log");
        if (!logFile.exists() || logFile.length() == 0) {
            System.out.println("No transaction data found. Please make some transactions before analyzing sales data.\n");
            return;
        }
        System.out.println("Sending transaction logs for analysis...\n");
        OpenAiClient.getInstance().sendLogToOpenAI();
    }

    private static void runAdvancedSaleDataAnalysis() {
        File logFile = new File("transactions.log");
        if (!logFile.exists() || logFile.length() == 0) {
            System.out.println("No transaction data found. Please make some transactions before analyzing sales data.\n");
            return;
        }
        System.out.println("Sending transaction logs for analysis...\n");
        OpenAiClient.getInstance().sendLogToOpenAIBetterPrompt();
    }

    private static void runStockpileOptimization(int balance, HashMap<String, Product> productMap) {
        if (productMap.isEmpty()) {
            System.out.println("No products available to optimize restocking.\n");
            return;
        }
        StockpileOptimizer optimizer = new StockpileOptimizer(balance, productMap);
        HashMap<String, Integer> purchasePlan = optimizer.optimizeStockpile();
        if (purchasePlan.isEmpty()) {
            System.out.println("No restocking needed or insufficient budget.\n");
        } else {
            System.out.println("Recommended restock amounts:");
            purchasePlan.forEach((key, value) -> System.out.println(key + ": " + value + " units"));
        }
    }
}
