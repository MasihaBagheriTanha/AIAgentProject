import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class StockpileOptimizer {
    private HashMap<String, Integer> demandForecast;
    private HashMap<String, Integer> productFactoryPrices;
    private int availableBudget;

    public StockpileOptimizer(int budget, HashMap<String, Product> productMap) {
        this.availableBudget = budget;
        this.demandForecast = new HashMap<>();
        this.productFactoryPrices = new HashMap<>();

        for (Map.Entry<String, Product> entry : productMap.entrySet()) {
            productFactoryPrices.put(entry.getKey(), entry.getValue().getFactoryPrice());
        }
    }

    public void analyzeTransactions() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("transactions.log"));
            HashMap<String, List<Integer>> salesData = new HashMap<>();

            // Regex to match logs with a timestamp, e.g.,
            // "Fri Feb 07 00:53:13 IRST 2025 - Sold 3 units of product: apple"
            Pattern pattern = Pattern.compile(".* - Sold (\\d+) units of product: (.+)");

            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int amount = Integer.parseInt(matcher.group(1));
                    String productName = matcher.group(2).trim();

                    salesData.putIfAbsent(productName, new ArrayList<>());
                    salesData.get(productName).add(amount);
                }
            }

            for (Map.Entry<String, List<Integer>> entry : salesData.entrySet()) {
                String product = entry.getKey();
                List<Integer> sales = entry.getValue();

                int weightedSum = 0;
                int weight = 1;
                int weightSum = 0;

                for (int i = sales.size() - 1; i >= 0; i--) {
                    weightedSum += sales.get(i) * weight;
                    weightSum += weight;
                    weight++;
                }

                int forecast = (weightSum == 0) ? 0 : weightedSum / weightSum;
                demandForecast.put(product, forecast);
            }

        } catch (IOException e) {
            System.out.println("Error reading transaction log.");
        }
    }

    public HashMap<String, Integer> optimizeStockpile() {
        HashMap<String, Integer> purchasePlan = new HashMap<>();
        analyzeTransactions();

        for (Map.Entry<String, Integer> entry : demandForecast.entrySet()) {
            String product = entry.getKey();
            int forecastedDemand = entry.getValue();
            int factoryPrice = productFactoryPrices.getOrDefault(product, Integer.MAX_VALUE);

            int maxAffordable = availableBudget / factoryPrice;
            int purchaseAmount = Math.min(forecastedDemand, maxAffordable);

            if (purchaseAmount > 0) {
                purchasePlan.put(product, purchaseAmount);
                availableBudget -= purchaseAmount * factoryPrice;
            }
        }
        return purchasePlan;
    }
}
