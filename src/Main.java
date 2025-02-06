import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Product> products = new HashMap<>();
    private static double balance;
    private static String logFile;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'IRST' yyyy");

    public static void main(String[] args) {
        System.out.println("Do you want to continue a previous session? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.println("Enter the log file name:");
            logFile = scanner.nextLine();
            loadSession(logFile);
        } else {
            int sessionNumber = new File(".").list((dir, name) -> name.startsWith("transactions") && name.endsWith(".log")).length + 1;
            logFile = "transactions" + sessionNumber + ".log";
            System.out.println("Enter your starting cash balance:");
            balance = scanner.nextDouble();
            scanner.nextLine(); // Consume newline
        }

        while (true) {
            System.out.println("Enter a command, type 'exit' to quit:");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program...");
                System.out.println("Final balance: " + balance);
                logTransaction("Final balance: " + balance);
                break;
            }
            processCommand(command);
        }
    }

    private static void loadSession(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    // Skip the date and " - " part.
                    String logEntry = line.substring(line.indexOf("-") + 2).trim();

                    if (logEntry.startsWith("Final balance:")) {
                        balance = Double.parseDouble(logEntry.split(": ")[1]);
                    } else if (logEntry.startsWith("Initialized product:")) {
                        String[] parts = logEntry.split(", ");
                        String productName = parts[0].split(": ")[1];
                        int factoryPrice = Integer.parseInt(parts[1].split(": ")[1]);
                        int consumerPrice = Integer.parseInt(parts[2].split(": ")[1]);
                        int initialStock = Integer.parseInt(parts[3].split(": ")[1]);
                        products.put(productName, new Product(factoryPrice, consumerPrice, initialStock));
                    } else if (logEntry.startsWith("Sold")) {
                        String[] parts = logEntry.split(" ");
                        int quantity = Integer.parseInt(parts[1]);
                        String productName = parts[5];
                        if (products.containsKey(productName)) {
                            balance = products.get(productName).setProductBalance_sell(quantity, (int) balance);
                        }
                    } else if (logEntry.startsWith("Recharged")) {
                        String[] parts = logEntry.split(" ");
                        int quantity = Integer.parseInt(parts[1]);
                        String productName = parts[5];
                        if (products.containsKey(productName)) {
                            balance = products.get(productName).setProductBalance_fill(quantity, (int) balance);
                        }
                    } else if (logEntry.startsWith("Modified product")) {
                        String[] parts = logEntry.split(": ");
                        String productName = parts[1].split(",")[0];

                        if (!products.containsKey(productName)) {
                            throw new IllegalArgumentException("Error: Modified product not found in session: " + productName);
                        }

                        Product product = products.get(productName);
                        for (String change : logEntry.split(", ")) {
                            if (change.contains("Factory Price changed from")) {
                                int newFactoryPrice = Integer.parseInt(change.split(" to ")[1]);
                                product.setFactoryPrice(newFactoryPrice);
                            } else if (change.contains("Consumer Price changed from")) {
                                int newConsumerPrice = Integer.parseInt(change.split(" to ")[1]);
                                product.setConsumerPrice(newConsumerPrice);
                            } else if (change.contains("Stock changed from")) {
                                int newStock = Integer.parseInt(change.split(" to ")[1]);
                                product.setProductBalance(newStock);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Error: Unrecognized log entry format -> " + logEntry);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading session: " + e.getMessage());
        }
    }

    private static void processCommand(String command) {
        if (command.equalsIgnoreCase("addProduct")) {
            addProduct();
        } else if (command.startsWith("sell")) {
            sellProduct(command);
        } else if (command.startsWith("recharge")) {
            rechargeProduct(command);
        } else if (command.equalsIgnoreCase("optimizeRestock")) {
            optimizeRestock();
        } else if (command.equalsIgnoreCase("addBalance")) {
            addBalance();
        } else if (command.equalsIgnoreCase("reduceBalance")) {
            reduceBalance();
        } else if (command.equalsIgnoreCase("products")) {
            listProducts();
        } else if (command.equalsIgnoreCase("modifyProduct")) {
            modifyProduct();
        } else if (command.equalsIgnoreCase("aIAnalysis")) {
            aIAnalysis();
        } else if (command.equalsIgnoreCase("advancedAIAnalysis")) {
            advancedAIAnalysis();
        } else if (command.equalsIgnoreCase("help")) {
            displayHelp();
        } else {
            System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
    }

    private static void addProduct() {
        System.out.println("Enter product name:");
        String productName = scanner.nextLine();
        System.out.println("Enter factory price:");
        int factoryPrice = scanner.nextInt();
        System.out.println("Enter consumer price:");
        int consumerPrice = scanner.nextInt();
        System.out.println("Enter initial balance in storage:");
        int initialBalance = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product product = new Product(factoryPrice, consumerPrice, initialBalance);
        products.put(productName, product);
        logTransaction("Initialized product: " + productName + ", Factory Price: " + factoryPrice + ", Consumer Price: " + consumerPrice + ", Initial Stock: " + initialBalance);
        System.out.println("Product registered successfully with name: " + productName);
    }

    private static void sellProduct(String command) {
        System.out.println("Enter product name:");
        String productName = scanner.nextLine();
        if (!products.containsKey(productName)) {
            System.out.println("Product not found.");
            return;
        }
        System.out.println("Enter quantity to sell:");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product product = products.get(productName);
        balance = product.setProductBalance_sell(quantity, (int) balance);
        logTransaction("Sold " + quantity + " units of product: " + productName);
    }

    private static void rechargeProduct(String command) {
        System.out.println("Enter product name:");
        String productName = scanner.nextLine();
        if (!products.containsKey(productName)) {
            System.out.println("Product not found.");
            return;
        }
        System.out.println("Enter quantity to add:");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product product = products.get(productName);
        balance = product.setProductBalance_fill(quantity, (int) balance);
        logTransaction("Recharged " + quantity + " units of product: " + productName);
    }

    private static void addBalance() {
        System.out.println("Enter amount to add to balance:");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        if (amount > 0) {
            balance += amount;
            logTransaction("Added " + amount + " to balance. New balance: " + balance);
            System.out.println("Balance updated successfully.");
        } else {
            System.out.println("Invalid amount. Please enter a positive value.");
        }
    }

    private static void reduceBalance() {
        System.out.println("Enter amount to reduce from balance:");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            logTransaction("Reduced " + amount + " from balance. New balance: " + balance);
            System.out.println("Balance updated successfully.");
        } else {
            System.out.println("Invalid amount. Ensure it's positive and does not exceed current balance.");
        }
    }

    private static void listProducts() {
        if (products.isEmpty()) {
            System.out.println("No products available.");
        } else {
            System.out.println("Product List:");
            for (Map.Entry<String, Product> entry : products.entrySet()) {
                String name = entry.getKey();
                Product p = entry.getValue();
                System.out.println("Product Name: " + name
                        + ", Factory Price: " + p.getFactoryPrice()
                        + ", Consumer Price: " + p.getConsumerPrice()
                        + ", Stock: " + p.getProductBalance());
            }
        }
    }

    private static void modifyProduct() {
        System.out.println("Enter product name to modify:");
        String productName = scanner.nextLine();
        if (!products.containsKey(productName)) {
            System.out.println("Product not found.");
            return;
        }
        Product product = products.get(productName);
        System.out.println("Current details for " + productName + ":");
        System.out.println("Factory Price: " + product.getFactoryPrice()
                + ", Consumer Price: " + product.getConsumerPrice()
                + ", Stock: " + product.getProductBalance());
        boolean modified = false;

        System.out.println("Enter new factory price (or press enter to skip):");
        String input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            try {
                int newFactoryPrice = Integer.parseInt(input.trim());
                int oldFactoryPrice = product.getFactoryPrice();
                product.setFactoryPrice(newFactoryPrice);
                logTransaction("Modified product " + productName + ": Factory Price changed from " + oldFactoryPrice + " to " + newFactoryPrice);
                modified = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for factory price. Skipping modification for factory price.");
            }
        }

        System.out.println("Enter new consumer price (or press enter to skip):");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            try {
                int newConsumerPrice = Integer.parseInt(input.trim());
                int oldConsumerPrice = product.getConsumerPrice();
                product.setConsumerPrice(newConsumerPrice);
                logTransaction("Modified product " + productName + ": Consumer Price changed from " + oldConsumerPrice + " to " + newConsumerPrice);
                modified = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for consumer price. Skipping modification for consumer price.");
            }
        }

        System.out.println("Enter new stock level (or press enter to skip):");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            try {
                int newStock = Integer.parseInt(input.trim());
                int oldStock = product.getProductBalance();
                product.setProductBalance(newStock);
                logTransaction("Modified product " + productName + ": Stock changed from " + oldStock + " to " + newStock);
                modified = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for stock level. Skipping modification for stock level.");
            }
        }

        if (modified) {
            System.out.println("Product modified successfully.");
        } else {
            System.out.println("No modifications applied.");
        }
    }

    private static void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("addProduct - Add a new product");
        System.out.println("sell - Sell a product");
        System.out.println("recharge - Restock a product");
        System.out.println("products - List all products with their details");
        System.out.println("modifyProduct - Modify properties of an existing product");
        System.out.println("addBalance - Add funds to balance");
        System.out.println("reduceBalance - Deduct funds from balance");
        System.out.println("optimizeRestock - Suggests optimized restock levels");
        System.out.println("aIAnalysis - Perform basic AI log analysis");
        System.out.println("advancedAIAnalysis - Perform advanced AI analysis for optimal restocking");
        System.out.println("help - Display this help message");
        System.out.println("exit - Exit the program");
    }

    private static void optimizeRestock() {
        System.out.println("Your current balance is: " + balance);
        System.out.println("Do you want to use the full balance for restocking? (yes/no)");

        int budget;
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            budget = (int) balance;
        } else {
            System.out.println("Enter the amount you want to invest in restocking:");
            try {
                budget = Integer.parseInt(scanner.nextLine().trim());
                if (budget > balance) {
                    System.out.println("You cannot invest more than your current balance.");
                    return;
                } else if (budget <= 0) {
                    System.out.println("Invalid amount. Please enter a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                return;
            }
        }

        StockpileOptimizer optimizer = new StockpileOptimizer(budget, (HashMap<String, Product>) products);
        HashMap<String, Integer> restockPlan = optimizer.optimizeStockpile();

        if (restockPlan.isEmpty()) {
            System.out.println("No products can be restocked within the budget.");
            return;
        }

        System.out.println("Optimized Restock Plan:");
        for (Map.Entry<String, Integer> entry : restockPlan.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units");
        }

        System.out.println("Do you want to proceed with this restock? (yes/no)");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            for (Map.Entry<String, Integer> entry : restockPlan.entrySet()) {
                String productName = entry.getKey();
                int restockAmount = entry.getValue();
                Product product = products.get(productName);

                if (product != null) {
                    product.setProductBalance(product.getProductBalance() + restockAmount);
                    logTransaction("Recharged " + restockAmount + " units of Product: " + productName);
                    System.out.println(productName + ": " + restockAmount + " units added to stock.");
                }
            }

            balance -= budget;
            System.out.println("Restocking completed! Remaining balance: " + balance);
        } else {
            System.out.println("Restocking canceled.");
        }
    }

    private static void logTransaction(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(dateFormat.format(new Date()) + " - " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }

    // New method for basic AI log analysis using OpenAiClient.sendLogToOpenAI()
    private static void aIAnalysis() {
        System.out.println("Performing basic AI log analysis...");
        OpenAiClient client = OpenAiClient.getInstance();
        client.sendLogToOpenAI();
    }

    // New method for advanced AI log analysis using OpenAiClient.sendLogToOpenAIBetterPrompt()
    private static void advancedAIAnalysis() {
        System.out.println("Performing advanced AI analysis...");
        OpenAiClient client = OpenAiClient.getInstance();
        client.sendLogToOpenAIBetterPrompt();
    }
}
