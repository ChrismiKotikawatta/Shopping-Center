package com.oop.intf;

import com.oop.model.Product;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

// Interface for ShoppingManager
public interface ShoppingManager {

    public void addProductToInventory(Scanner scanner, List<Product> productList);

    public void removeProductFromInventory(Scanner scanner, List<Product> productList);

    public void printProductList(List<Product> productList);

    public void saveInventoryToFile(Path filePath, List<Product> productList);

    public void loadInventoryFromFile(Path filePath, List<Product> productList);

    public void displayMenu();

    public void openGUI();
}
