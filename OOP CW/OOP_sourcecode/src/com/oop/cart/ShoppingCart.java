package com.oop.cart;

import com.oop.model.Clothing;
import com.oop.model.Electronics;
import com.oop.model.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ShoppingCart class which represents the user's cart
public class ShoppingCart {

    // List of products as instance variable
    private List<Product> products;

    // Map to track products and their quantities
    private Map<Product, Integer> productQuantityMap;

    // Variable to track whether a purchase has been made
    private boolean firstPurchaseMade;

    // Map to track categories and their counts
    private Map<String, Integer> categoryCountMap;

    // Non-Parameterized Constructor
    public ShoppingCart() {
        this.products = new ArrayList<>();
        this.productQuantityMap = new HashMap<>();
        this.firstPurchaseMade = false; // Set to false initially
        this.categoryCountMap = new HashMap<>();
    }

    // Method to add product
    public void add(Product product) {
        products.add(product);
    }

    // Method to remove product
    public void remove(Product product) {
        products.remove(product);
    }

    // Method to calculate total
    public double calculateTotal() {
        double totalCost = 0;
        for (Product product : products) {
            totalCost += product.getPrice();
        }
        return totalCost;
    }

    // Method to add a product to the cart
    public void addProduct(Product product) {
        int quantity = productQuantityMap.getOrDefault(product, 0);
        productQuantityMap.put(product, quantity + 1);

        // Update the category count
        String category = getCategoryForProduct(product);
        updateCategoryCount(category, 1);
    }

    // Method to get the category of a product
    private String getCategoryForProduct(Product product) {
        // Implement logic to determine the category for a given product
        if (product instanceof Electronics) {
            return "Electronics";
        } else if (product instanceof Clothing) {
            return "Clothing";
        } else {
            return "Unknown";
        }
    }

    // Method to update the category count
    private void updateCategoryCount(String category, int countChange) {
        int currentCount = categoryCountMap.getOrDefault(category, 0);
        categoryCountMap.put(category, currentCount + countChange);
    }

    // Method to get the list of products in the cart
    public List<Product> getCartItems() {
        List<Product> cartItems = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : productQuantityMap.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            for (int i = 0; i < quantity; i++) {
                cartItems.add(product);
            }
        }
        return cartItems;
    }

    // Method to remove a product from the cart
    public void removeProduct(Product product) {
        int quantity = productQuantityMap.getOrDefault(product, 0);
        if (quantity > 0) {
            productQuantityMap.put(product, quantity - 1);
        }
    }

    // Method to get the quantity of a specific product in the cart
    public int getProductQuantity(Product product) {

        return productQuantityMap.getOrDefault(product, 0);
    }

    // Method to calculate the total cost of products in the cart
    public double calculateTotalCost() {
        double totalCost = 0;
        for (Map.Entry<Product, Integer> entry : productQuantityMap.entrySet()) {
            totalCost += entry.getKey().getPrice() * entry.getValue();
        }
        return totalCost;
    }

    public boolean hasThreeItemsInSameCategory() {
        // Check if any category has at least three items
        for (int count : categoryCountMap.values()) {
            if (count >= 3) {
                return true;
            }
        }
        return false;
    }

    // Method to check if a first purchase has been made
    public boolean hasMadePurchase() {
        return firstPurchaseMade;
    }
}
