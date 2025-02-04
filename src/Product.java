import java.util.*;

public class Product {
    private int factoryPrice;
    private int sellPrice;
    private int productBalance = 0;

    public Product(int factoryPrice, int sellPrice, int productBalance) {
        this.factoryPrice = factoryPrice;
        this.sellPrice = sellPrice;
        this.productBalance = productBalance;
    }

    public Product(int factoryPrice, int sellPrice) {
        this.factoryPrice = factoryPrice;
        this.sellPrice = sellPrice;
    }

    public void setFactoryPrice(int factoryPrice) {
        this.factoryPrice = factoryPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int setProductBalance_sell(int change, int balance) {
        if (change <= productBalance && change > 0) {
            productBalance -= change;
            System.out.println("Purchase successful");
            return balance + change * sellPrice;
        } else {
            System.out.println("invalid purchase; not enough of product in storage available");
            return balance;
        }
    }

    public int setProductBalance_fill(int change, int balance) {
        if (change >= 0 && balance >= change * factoryPrice) {
            productBalance += change;
            System.out.println("Storage successfully recharged");
            return balance - change * factoryPrice;
        } else {
            System.out.println("Enter a Valid amount to add to storage");
            return balance;
        }
    }

    public int getFactoryPrice() {
        return factoryPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public int getProductBalance() {
        return productBalance;
    }
}