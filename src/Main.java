import java.util.*;

public class Main {
    public static void main(String[] args) {
        int balance;
        int productCount;
        ArrayList<Product> products= new ArrayList<>();

        System.out.println("Enter number of your products:");
        Scanner scanner = new Scanner(System.in);
        productCount = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter your current cash balance:");
        balance = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < productCount; i++) {
            String productName;
            int productConsumerPrice;
            int productFactoryPrice;
            int hasInitialBalance;
            int productInitialBalance;
            System.out.println("Enter product number " + (i + 1) + "'s name:");
            productName = scanner.nextLine();
            System.out.println("Enter product number " + (i + 1) + "'s factory price:");
            productFactoryPrice = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter product number " + (i + 1) + "'s consumer price:");
            productConsumerPrice = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Is there any initial balance of your product " + productName + " in storage? (1 for yes,0 for no)");
            hasInitialBalance=scanner.nextInt();
            if (hasInitialBalance==0){
                products.add(new Product(productFactoryPrice,productConsumerPrice));
                System.out.println("Product " + productName + " registered Successfully with factory price of " + productFactoryPrice + " and customer price of " + productConsumerPrice);
            }
            else{
                System.out.println("Enter product number " + (i + 1) + "'s initial balance:");
                productInitialBalance = scanner.nextInt();
                products.add(new Product(productFactoryPrice,productConsumerPrice,productInitialBalance));
                System.out.println("Product " + productName + " registered Successfully with factory price of " + productFactoryPrice + " ,customer price of " + productConsumerPrice +" and initial balance of "+ productInitialBalance);
            }
        }
        scanner.close();
        System.out.println(productCount);
        System.out.println(balance);
    }
}