
import com.oop.cart.WestminsterShoppingManager;
import com.oop.model.Clothing;
import com.oop.model.Electronics;
import com.oop.model.Product;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WestminsterShoppingManagerTest {

    private WestminsterShoppingManager shoppingManager;
    private List<Product> testProductList;
    private static final String TEST_FILE_PATH = "src/products_test.txt";

    public boolean createTestFileIfNotExists(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.notExists(path)) {
                Files.createFile(path);
                System.out.println("Test file created successfully: " + filePath);
                return true;
            } else {
                System.out.println("Test file already exists: " + filePath);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error creating test file: " + e.getMessage());
            return false;
        }
    }

    @Before
    public void setUp() {
        // Initialize a new WestminsterShoppingManager before each test
        shoppingManager = new WestminsterShoppingManager();
        // Initialize a new product list for testing
        testProductList = new ArrayList<>();
    }

    @Test
    public void testCreateTestFileIfNotExists() {
        // Ensure that the test file is created if it doesn't exist
        assertTrue(createTestFileIfNotExists(TEST_FILE_PATH));
        assertTrue(Files.exists(Paths.get(TEST_FILE_PATH)));
    }

    @Test
    public void testAddProductToInventory() {
        // Test adding a product to the inventory
        // Simulate user input
        String input = "2\nTestID\nTestProduct\n10\n20.0\nTestSize\nTestColor\n";
        java.io.ByteArrayInputStream testInput = new java.io.ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);

        // Add product and check if it's in the test list
        shoppingManager.addProductToInventory(new Scanner(System.in), testProductList);
        boolean productFound = false;
        for (Product product : testProductList) {
            if (product.getProductId().equals("TestID")) {
                productFound = true;
                break;
            }
        }
        assertTrue(productFound);

    }

    @Test
    public void testRemoveProductFromInventory() {
        // Test removing a product from the inventory
        // Add a product to the test list first
        Product testProduct = new Clothing("TestID", "TestProduct", 10, 20.0, "TestSize", "TestColor");
        testProductList.add(testProduct);

        // Simulate user input
        String input = "TestID\n";
        java.io.ByteArrayInputStream testInput = new java.io.ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);

        // Remove the product and check if it's not in the test list
        shoppingManager.removeProductFromInventory(new Scanner(System.in), testProductList);
        assertFalse(testProductList.contains(testProduct));
    }

    @Test
    public void testPrintProductList() {
        // Test printing the product list
        // Add some products to the list
        testProductList.add(new Electronics("E001", "Electronics1", 5, 50.0, "Brand1", 12));
        testProductList.add(new Clothing("C001", "Clothing1", 8, 30.0, "Size1", "Color1"));

        // Redirect System.out to capture printed output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        // Print the product list and check if it contains the expected output
        shoppingManager.printProductList(testProductList);
        assertTrue(output.toString().contains("Electronics1") && output.toString().contains("Clothing1"));
    }

    @Test
    public void testSaveAndLoadInventoryToFile() {
        // Test saving and loading the inventory to/from a file
        // Add some products to the list
        testProductList.add(new Electronics("E001", "Electronics1", 5, 50.0, "Brand1", 12));
        testProductList.add(new Clothing("C001", "Clothing1", 8, 30.0, "Size1", "Color1"));

        // Save to file
        shoppingManager.saveInventoryToFile(Paths.get("src/products_test.txt"), testProductList);

        // Clear the current product list
        testProductList.clear();

        // Load from file
        shoppingManager.loadInventoryFromFile(Paths.get("src/products_test.txt"), testProductList);

        // Check if the loaded product list matches the expected list
        assertTrue(testProductList.stream().anyMatch(p -> p.getProductName().equals("Electronics1")));
        assertTrue(testProductList.stream().anyMatch(p -> p.getProductName().equals("Clothing1")));
    }
}
