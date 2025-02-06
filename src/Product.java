import java.util.*;

public class Product {
    private int factoryPrice;
    private int consumerPrice;
    private int productBalance = 0;

    public Product(int factoryPrice, int consumerPrice, int productBalance) {
        this.factoryPrice = factoryPrice;
        this.consumerPrice = consumerPrice;
        this.productBalance = productBalance;
    }

    public Product(int factoryPrice, int consumerPrice) {
        this.factoryPrice = factoryPrice;
        this.consumerPrice = consumerPrice;
    }

    public void setFactoryPrice(int factoryPrice) {
        this.factoryPrice = factoryPrice;
    }

    public void setConsumerPrice(int consumerPrice) {
        this.consumerPrice = consumerPrice;
    }

    public void setProductBalance(int productBalance) {
        if (productBalance >= 0) {
            this.productBalance = productBalance;
        } else {
            System.out.println("Invalid product balance. It cannot be negative.");
        }
    }

    public int setProductBalance_sell(int change, int balance) {
        if (change <= productBalance && change > 0) {
            productBalance -= change;
            System.out.println("Purchase successful");
            return balance + change * consumerPrice;
        } else {
            System.out.println("Invalid purchase; not enough product in storage available");
            return balance;
        }
    }

    public int setProductBalance_fill(int change, int balance) {
        if (change >= 0 && balance >= change * factoryPrice) {
            productBalance += change;
            System.out.println("Storage successfully recharged");
            return balance - change * factoryPrice;
        } else {
            System.out.println("Enter a valid amount to add to storage");
            return balance;
        }
    }

    public int getFactoryPrice() {
        return factoryPrice;
    }

    public int getConsumerPrice() {
        return consumerPrice;
    }

    public int getProductBalance() {
        return productBalance;
    }
}
