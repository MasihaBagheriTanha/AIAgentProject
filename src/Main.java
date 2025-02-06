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
                String logEntry = line.substring(line.indexOf("-") + 2);

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
                        products.get(productName).setProductBalance_sell(quantity, (int) balance);
                    }
                } else if (logEntry.startsWith("Recharged")) {
                    String[] parts = logEntry.split(" ");
                    int quantity = Integer.parseInt(parts[1]);
                    String productName = parts[5];
                    if (products.containsKey(productName)) {
                        products.get(productName).setProductBalance_fill(quantity, (int) balance);
                    }
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

    private static void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("addProduct - Add a new product");
        System.out.println("sell - Sell a product");
        System.out.println("recharge - Restock a product");
        System.out.println("optimizeRestock - Suggests optimized restock levels");
        System.out.println("help - Display this help message");
        System.out.println("exit - Exit the program");
    }

    private static void optimizeRestock() {
        System.out.println("Optimization function is currently under development.");
    }

    private static void logTransaction(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(dateFormat.format(new Date()) + " - " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }
}
