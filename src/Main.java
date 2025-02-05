import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        int balance;
        int productCount;
        LinkedList<Product> products = new LinkedList<>();
        HashMap<String, Product> productMap = new HashMap<>();

        System.out.println("Enter number of your products:");
        Scanner scanner = new Scanner(System.in);
        productCount = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter your current cash balance:");
        balance = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < productCount; i++) {
            System.out.println("Enter product number " + (i + 1) + "'s name:");
            String productName = scanner.nextLine();

            System.out.println("Enter product number " + (i + 1) + "'s factory price:");
            int productFactoryPrice = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter product number " + (i + 1) + "'s consumer price:");
            int productConsumerPrice = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Is there any initial balance of your product " + productName + " in storage? (1 for yes,0 for no)");
            int hasInitialBalance = scanner.nextInt();
            scanner.nextLine();

            Product product;
            if (hasInitialBalance == 0) {
                product = new Product(productFactoryPrice, productConsumerPrice);
            } else {
                System.out.println("Enter product number " + (i + 1) + "'s initial balance:");
                int productInitialBalance = scanner.nextInt();
                scanner.nextLine();
                product = new Product(productFactoryPrice, productConsumerPrice, productInitialBalance);
            }
            products.add(product);
            productMap.put(productName, product);
            System.out.println("Product " + productName + " registered successfully!\n");
        }

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
                continue;
            }

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program...\n");
                break;
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
                    System.out.println("Product not found.\n");
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
}
