package com.oop.cart;

import com.oop.intf.ShoppingManager;
import com.oop.model.Clothing;
import com.oop.model.Electronics;
import com.oop.model.Product;
import com.oop.view.ShoppingGUI;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.SwingUtilities;

// WestminsterShoppingManager class which implements the interface ShoppingManager
public class WestminsterShoppingManager implements ShoppingManager {

    // Static variable declared to initialize the maximum number of products
    private static final int MAX_PRODUCTS = 50;
    // Variable declared to maintain the list of products in the system
    private List<Product> productList;

    // Constructor
    public WestminsterShoppingManager() {
        this.productList = new ArrayList<>();
        loadInventoryFromFile(Paths.get("src/products.txt"), productList); // Load existing products from file on startup
    }

    // Method implemented to add a new product to the system
    @Override
    public void addProductToInventory(Scanner scanner, List<Product> productList) {
        // Consideration of the maximum number of products
        if (productList.size() > MAX_PRODUCTS) {
            System.out.println("Cannot add more products. Maximum limit reached...");
        } else {
            try {
                // Allow user to select which type of product to be added, either clothing or electronics
                System.out.print("Enter product type to be added (1 for Electronics, 2 for Clothing): ");
                int productType = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Creation of new product object
                Product newProduct = null;

                switch (productType) {
                    case 1:
                        newProduct = createElectronics(scanner);
                        break;
                    case 2:
                        newProduct = createClothing(scanner);
                        break;
                    default:
                        System.out.println("Invalid product type. Please try again.");
                }

                if (newProduct != null) {
                    productList.add(newProduct);
                    System.out.println("Product added successfully!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume invalid input
            } catch (NoSuchElementException e) {
                System.out.println("Input not found. Please enter the required input.");
            }
        }
    }

    // Method implemented to add an electronic product
    public Electronics createElectronics(Scanner scanner) {
        try {
            System.out.print("Enter product ID: ");
            String productId = scanner.nextLine();

            System.out.print("Enter product name: ");
            String productName = scanner.nextLine();

            System.out.print("Enter number of available items: ");
            int availableItems = scanner.nextInt();

            System.out.print("Enter price (in $): ");
            double price = scanner.nextDouble();

            scanner.nextLine(); // Consume newline

            System.out.print("Enter brand: ");
            String brand = scanner.nextLine();

            System.out.print("Enter warranty period (in months): ");
            int warrantyPeriod = scanner.nextInt();

            return new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Consume invalid input
            return null; // Return null to indicate failure in creating the Electronics object
        } catch (NoSuchElementException e) {
            System.out.println("Input not found. Please enter the required input.");
            return null; // Return null to indicate failure in creating the Electronics object
        }
    }

    // Method implemented to add a cloting product
    private Clothing createClothing(Scanner scanner) {
        try {
            System.out.print("Enter product ID: ");
            String productId = scanner.nextLine();

            System.out.print("Enter product name: ");
            String productName = scanner.nextLine();

            System.out.print("Enter number of available items: ");
            int availableItems = scanner.nextInt();

            System.out.print("Enter price: ");
            double price = scanner.nextDouble();

            scanner.nextLine(); // Consume newline

            System.out.print("Enter size: ");
            String size = scanner.nextLine();

            System.out.print("Enter color: ");
            String color = scanner.nextLine();

            return new Clothing(productId, productName, availableItems, price, size, color);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Consume invalid input
            return null; // Return null to indicate failure in creating the Electronics object
        } catch (NoSuchElementException e) {
            System.out.println("Input not found. Please enter the required input.");
            return null; // Return null to indicate failure in creating the Electronics object
        }
    }

    // Method implemented to delete a product based on product id
    @Override
    public void removeProductFromInventory(Scanner scanner, List<Product> productList) {
        try {
            System.out.print("Enter the product ID to be deleted: ");
            String productIdToDelete = scanner.nextLine();

            Iterator<Product> iterator = productList.iterator();
            boolean found = false;

            while (iterator.hasNext()) {
                Product product = iterator.next();
                if (product.getProductId().equalsIgnoreCase(productIdToDelete)) {
                    System.out.println("Deleted product: " + product.getProductId() + " : " + product.getProductName()
                            + " (" + (product instanceof Electronics ? "Electronics" : "Clothing") + ")");
                    iterator.remove();
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Product with ID " + productIdToDelete + " not found in the inventory.");
            }

            System.out.println("Total number of products left in the system: " + productList.size());
        } catch (NoSuchElementException e) {
            System.out.println("Input not found. Please enter the required input.");
        }
    }

    // Method implemented to print the list of products in the system
    @Override
    public void printProductList(List<Product> productList) {
        try {
            // Sorting according to alphabetical order of the product id
            productList.sort(Comparator.comparing(Product::getProductId));

            System.out.println("Product List:\n");
            for (Product product : productList) {
                System.out.println("Product Type: " + (product instanceof Electronics ? "Electronics" : "Clothing"));

                if (product instanceof Electronics) {
                    Electronics electronics = (Electronics) product;
                    electronics.displayDetails();
                } else if (product instanceof Clothing) {
                    Clothing clothing = (Clothing) product;
                    clothing.displayDetails();
                }

                System.out.println("----------");
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred while printing the product list.");
            e.printStackTrace(); // Print the stack trace for debugging purposes
        }
    }

    // Method implemented to save the list of products that have been added to the system in a file
    @Override
    public void saveInventoryToFile(Path filePath, List<Product> productList) {
        filePath = Paths.get("C:\\Users\\user\\OneDrive - University of Westminster\\Desktop\\OOP CW\\OOP_sourcecode\\src\\products.txtp");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Product product : productList) {
                try {
                    if (product instanceof Electronics) {
                        Electronics electronics = (Electronics) product;
                        writer.write("Electronics," + electronics.getProductId() + ","
                                + electronics.getProductName() + "," + electronics.getAvailableItems() + ","
                                + electronics.getPrice() + "," + electronics.getBrand() + ","
                                + electronics.getWarrantyPeriod());
                    } else if (product instanceof Clothing) {
                        Clothing clothing = (Clothing) product;
                        writer.write("Clothing," + clothing.getProductId() + ","
                                + clothing.getProductName() + "," + clothing.getAvailableItems() + ","
                                + clothing.getPrice() + "," + clothing.getSize() + ","
                                + clothing.getColor());
                    }
                    writer.newLine(); // Move to the next line for the next product
                } catch (Exception e) {
                    // Handle specific exceptions that might occur during product writing
                    System.out.println("Error writing product to file: " + e.getMessage());
                }
            }
            System.out.println("Products saved to file successfully.");
        } catch (IOException e) {
            // Handle IOException for file operations
            System.out.println("Error saving products to file: " + e.getMessage());
        }
    }

    // Method implemented to read back all the information in the saved file
    @Override
    public void loadInventoryFromFile(Path filePath, List<Product> productList) {
        productList.clear(); // Clear existing products

        // Ensure that the file exists or create it
        filePath = Paths.get("src/products.txt");

        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                System.out.println("Error creating products.txt file: " + e.getMessage());
                return; // Stop loading if file creation fails
            }
        }

        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        if ("Electronics".equals(parts[0])) {
                            Electronics electronics = new Electronics(parts[1], parts[2],
                                    Integer.parseInt(parts[3]), Double.parseDouble(parts[4]),
                                    parts[5], Integer.parseInt(parts[6]));
                            productList.add(electronics);
                        } else if ("Clothing".equals(parts[0])) {
                            Clothing clothing = new Clothing(parts[1], parts[2],
                                    Integer.parseInt(parts[3]), Double.parseDouble(parts[4]),
                                    parts[5], parts[6]);
                            productList.add(clothing);
                        }
                    }
                } catch (Exception e) {
                    // Handle specific exceptions that might occur during product loading
                    System.out.println("Error loading product from file: " + e.getMessage());
                }
            }
            System.out.println("Products loaded from file successfully.");
        } catch (IOException e) {
            // Handle IOException for file operations
            System.out.println("Error loading products from file: " + e.getMessage());
        }
    }

    // Method implemented for the client to interact with the system through a gui
    @Override
    public void openGUI() {
        SwingUtilities.invokeLater(() -> {
            ShoppingGUI shoppingGUI = new ShoppingGUI();
            shoppingGUI.setVisible(true);
        });
    }

    // Method implemented to display a menu in the console containing the asked management actions
    @Override
    public void displayMenu() {
        System.out.println("*********** Westminster Shopping Manager Menu ***********");
        System.out.println("1. Add a new product to the system");
        System.out.println("2. Delete a product from the system");
        System.out.println("3. Print the list of products in the system");
        System.out.println("4. Save the list of products added to the system in a file");
        System.out.println("5. Open GUI");
        System.out.println("6. Exit");
        System.out.println("**********************************************************");
    }

    // Method implemented to activate all the managing actions
    public void manageSystem() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            try {
                // Display the menu for manager or client to select an option
                displayMenu();
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addProductToInventory(scanner, productList);
                        break;
                    case 2:
                        removeProductFromInventory(scanner, productList);
                        break;
                    case 3:
                        printProductList(productList);
                        break;
                    case 4:
                        saveInventoryToFile(Paths.get("src/products.txt"), productList);
                        break;
                    case 5:
                        openGUI();
                        break;
                    case 6:
                        System.out.println("Exiting the system. Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

}
