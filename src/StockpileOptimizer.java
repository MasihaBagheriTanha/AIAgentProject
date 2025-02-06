import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class StockpileOptimizer {
    private HashMap<String, Integer> demandForecast;
    private HashMap<String, Product> productMap;
    private int availableBudget;
    private String logFileName;

    public StockpileOptimizer(int budget, HashMap<String, Product> productMap, String logFileName) {
        this.availableBudget = budget;
        this.demandForecast = new HashMap<>();
        this.productMap = productMap;
        this.logFileName = logFileName;
    }

    public void analyzeTransactions() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(logFileName));
            HashMap<String, List<Integer>> salesData = new HashMap<>();

            // Regex to match logs with a timestamp
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
            System.out.println("Error reading transaction log file: " + logFileName);
        }
    }

    public HashMap<String, Integer> optimizeStockpile() {
        HashMap<String, Integer> purchasePlan = new HashMap<>();
        analyzeTransactions();

        int remainingBudget = availableBudget;

        for (Map.Entry<String, Integer> entry : demandForecast.entrySet()) {
            String product = entry.getKey();
            int forecastedDemand = entry.getValue();
            int factoryPrice = productMap.containsKey(product) ? productMap.get(product).getFactoryPrice() : Integer.MAX_VALUE;

            int maxAffordable = (factoryPrice == 0) ? 0 : remainingBudget / factoryPrice;
            int purchaseAmount = Math.min(forecastedDemand, maxAffordable);

            if (purchaseAmount > 0) {
                purchasePlan.put(product, purchaseAmount);
                remainingBudget -= purchaseAmount * factoryPrice;
            }
        }
        return purchasePlan;
    }
}
