import java.util.*;

public class Main {
    public static void main(String[] args) {
        int balance;
        int productCount;
        System.out.println("Enter number of your products:");
        Scanner scanner = new Scanner(System.in);
        productCount = scanner.nextInt();
        System.out.println("Enter your current cash balance:");
        balance = scanner.nextInt();
        for (int i = 0; i < productCount; i++) {
            String productName;
            int productConsumerPrice;
            int productFactoryPrice;
            System.out.println("Enter product number " + (i + 1) + "'s name:");
            productName = scanner.nextLine();
            System.out.println("Enter product number " + (i + 1) + "'s factory price:");
            productFactoryPrice = scanner.nextInt();
            System.out.println("Enter product number " + (i + 1) + "'s consumer price:");
            productConsumerPrice = scanner.nextInt();
            System.out.println("Product " + productName + " registered Successfully with factory price of " + productFactoryPrice + " and customer price of " + productConsumerPrice);
        }
        scanner.close();
        System.out.println(productCount);
        System.out.println(balance);
    }
}