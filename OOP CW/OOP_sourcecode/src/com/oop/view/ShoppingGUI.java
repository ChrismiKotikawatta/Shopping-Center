package com.oop.view;

import com.oop.cart.ShoppingCart;
import com.oop.model.Clothing;
import com.oop.model.Electronics;
import com.oop.model.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ShoppingGUI extends JFrame {

    // Declare variable for dropdown menu
    private JComboBox<String> productTypeComboBox;
    // Declare variable for products table to be populated according to dropdown menu
    private JTable productTable;
    // Declare variable for the product details panel
    private JPanel detailsPanel;
    // Declare variable for shopping cart button
    private JButton shoppingCartButton;
    // Declare variable for ShoppingCart class
    private final ShoppingCart shoppingCart;
    // Add a member variable for the ShoppingCartWindow
    private final ShoppingCartWindow shoppingCartWindow;

    // Constructor to load the components
    public ShoppingGUI() {
        shoppingCart = new ShoppingCart();
        shoppingCartWindow = new ShoppingCartWindow(shoppingCart);
        initUI();
        // Show all products by default
        loadAndDisplayProducts("All");
    }

    private void initUI() {

        // Set the initial size
        setSize(800, 600);

        // Set the layout manager of the main content pane
        setLayout(new BorderLayout());

        // Set the title
        setTitle("Westminster Shopping Centre");

        // Create a JPanel for label and combo box with FlowLayout
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 0, 0)); // Add an empty border for indentation

        // Create a JLabel for the product category selection
        JLabel categoryLabel1 = new JLabel("Select Product Category    ");
        topPanel.add(categoryLabel1);

        // Create a JComboBox for product types
        productTypeComboBox = new JComboBox<>();
        // Set initial product types
        setProductTypes();
        topPanel.add(productTypeComboBox);

        // Add an action listener to the JComboBox
        productTypeComboBox.addActionListener((ActionEvent e) -> {
            // Load and display products based on the selected category
            String selectedCategory = (String) productTypeComboBox.getSelectedItem();
            loadAndDisplayProducts(selectedCategory);
        });

        // Add the topPanel to the WEST position
        add(topPanel, BorderLayout.WEST);

        // Create a JPanel to hold the JTable and detailsPanel vertically
        JPanel verticalPanel = new JPanel(new BorderLayout());

        // Create a JTable for displaying product information
        productTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Increase row height
        productTable.setRowHeight(30);

        // Bold column names
        JTableHeader tableHeader = productTable.getTableHeader();
        Font headerFont = new Font(tableHeader.getFont().getName(), Font.BOLD, tableHeader.getFont().getSize());
        tableHeader.setFont(headerFont);

        // Center-align row values
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        productTable.setDefaultRenderer(Object.class, centerRenderer);

        // Add a ListSelectionListener to the productTable
        productTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int selectedRow = productTable.getSelectedRow();
                String productId = (String) productTable.getValueAt(selectedRow, 0);
                String category = (String) productTable.getValueAt(selectedRow, 2);
                String name1 = (String) productTable.getValueAt(selectedRow, 1);
                String[] values = productTable.getValueAt(selectedRow, 4).toString().split(", "); // Split the info string
                // Retrieve the itemsAvailable directly from the products.txt file
                String itemsAvailable = getItemsAvailable(productId);
                updateDetailsPanel(productId, category, name1, values, itemsAvailable);
            }
        });

        // Add tableScrollPane to the CENTER position of verticalPanel
        verticalPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Create a JPanel for displaying product details
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(6, 2));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Add detailsPanel to the SOUTH position of mainPanel
        verticalPanel.add(detailsPanel, BorderLayout.SOUTH);

        // Add mainPanel to the CENTER position
        add(verticalPanel, BorderLayout.SOUTH);

        // Create the Shopping Cart button
        shoppingCartButton = new JButton("Shopping Cart");

        // Set preferred size to make the button smaller
        Dimension buttonSize = new Dimension(150, 40);
        shoppingCartButton.setPreferredSize(buttonSize);

        // Create a panel for the button with FlowLayout and right alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(shoppingCartButton);

        // Add the button panel to the top of the frame
        add(buttonPanel, BorderLayout.NORTH);

        // Add an action listener to the JButton
        shoppingCartButton.addActionListener((ActionEvent e) -> {
            // Update the shopping cart window with the current cart items
            shoppingCartWindow.updateCartTable(shoppingCart.getCartItems());

            // Show the shopping cart window
            shoppingCartWindow.setVisible(true);
        });

        //pack(); // Adjust the frame size to fit components
        setLocationRelativeTo(null); // Center the frame on the screen
        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void setProductTypes() {
        // Add product types to the JComboBox
        productTypeComboBox.addItem("All");
        productTypeComboBox.addItem("Electronics");
        productTypeComboBox.addItem("Clothing");
    }

    private void loadAndDisplayProducts(String category) {
        // Read product information from the "products.txt" file
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Product ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("Price($)");
        model.addColumn("Info"); // This will include size, color, model, warranty

        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\user\\OneDrive - University of Westminster\\Desktop\\OOP CW\\OOP_sourcecode\\src\\products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (category.equals("All") || values[0].equals(category)) {
                    String productId = values[1];
                    String name = values[2];
                    String productCategory = values[0];
                    String price = values[4];
                    String info = getInfoString(productCategory, values);
                    model.addRow(new Object[]{productId, name, productCategory, price, info});
                }
            }
        } catch (IOException e) {
        }

        // Set up the TableRowSorter
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        productTable.setRowSorter(sorter);

        // Add a listener to the column header to trigger sorting
        productTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = productTable.columnAtPoint(e.getPoint());
                sortProductTable(columnIndex, SortOrder.ASCENDING, sorter);
            }
        });

        // Set the model to the table
        productTable.setModel(model);

        // Apply the custom renderer for each column
        for (int i = 0; i < model.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(new AvailabilityRenderer());
        }
    }

    private void sortProductTable(int columnIndex, SortOrder sortOrder, TableRowSorter<TableModel> sorter) {
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(columnIndex, sortOrder));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private String getInfoString(String category, String[] values) {
        switch (category) {
            case "Electronics":
                return values[5] + ", " + values[6] + " months warranty";
            case "Clothing":
                return values[5] + ", " + values[6];
            default:
                return "";
        }
    }

    private void updateDetailsPanel(String productId, String category, String name, String[] values, String itemsAvailable) {
        // Clear the existing components in the detailsPanel
        detailsPanel.removeAll();

        // Use GridLayout with a single column
        detailsPanel.setLayout(new GridLayout(0, 1));

        // Add labels with left alignment and increased line spacing
        addLabelWithMargin(detailsPanel, "<html><b>Selected Product - Details</b></html>", SwingConstants.LEFT);
        addLabelWithMargin(detailsPanel, "Product Id: " + productId, SwingConstants.LEFT);
        addLabelWithMargin(detailsPanel, "Category: " + category, SwingConstants.LEFT);
        addLabelWithMargin(detailsPanel, "Name: " + name, SwingConstants.LEFT);

        switch (category) {
            case "Electronics":
                addLabelWithMargin(detailsPanel, "Brand: " + values[0], SwingConstants.LEFT);
                addLabelWithMargin(detailsPanel, "Warranty: " + values[1], SwingConstants.LEFT);
                addLabelWithMargin(detailsPanel, "Items Available: " + itemsAvailable, SwingConstants.LEFT);
                break;
            case "Clothing":
                addLabelWithMargin(detailsPanel, "Size: " + values[0], SwingConstants.LEFT);
                addLabelWithMargin(detailsPanel, "Color: " + values[1], SwingConstants.LEFT);
                addLabelWithMargin(detailsPanel, "Items Available: " + itemsAvailable, SwingConstants.LEFT);
                break;
            // Add more cases if you have other categories
            default:
                break;
        }

        JButton addToShoppingCartButton = new JButton("Add to Shopping Cart");

        // Set preferred size to make the button smaller
        Dimension addToCartButtonSize = new Dimension(200, 40);
        addToShoppingCartButton.setBounds(10, 150, addToCartButtonSize.width, addToCartButtonSize.height);

        addToShoppingCartButton.addActionListener((var e) -> {
            // Get the selected product details
            String productId1 = productTable.getValueAt(productTable.getSelectedRow(), 0).toString();
            String name1 = productTable.getValueAt(productTable.getSelectedRow(), 1).toString();
            String category1 = productTable.getValueAt(productTable.getSelectedRow(), 2).toString();
            double price = Double.parseDouble(productTable.getValueAt(productTable.getSelectedRow(), 3).toString());
            String infoColumnValue = productTable.getValueAt(productTable.getSelectedRow(), 4).toString();
            String[] infoValues = infoColumnValue.split(", ");
            String firstInfo = infoValues[0];
            String secondInfo = infoValues[1];
            int productAvailability = Integer.parseInt(getItemsAvailable(productId1));
            // Create a Product object
            Product selectedProduct = null;
            switch (category1) {
                case "Electronics":
                    selectedProduct = new Electronics(productId1, name1, productAvailability, price, firstInfo, extractWarrantyPeriod(secondInfo));
                    break;
                case "Clothing":
                    selectedProduct = new Clothing(productId1, name1, productAvailability, price, firstInfo, secondInfo);
                    break;
                default:
                    System.out.println("Invalid product type. Please try again.");
            }
            // Add the selected product to the shopping cart
            shoppingCart.addProduct(selectedProduct);
            // Print a message to indicate that the product has been added to the cart
            System.out.println("Product added to the shopping cart: " + selectedProduct.getProductId());
        });

        // Add the buttons panel to the bottom of the details panel
        detailsPanel.add(addToShoppingCartButton);

        // Repaint the panel to reflect the changes
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private int extractWarrantyPeriod(String secondInfo) {
        // Assuming secondInfo is "3 months warranty"
        Pattern pattern = Pattern.compile("\\b(\\d+)\\b");
        Matcher matcher = pattern.matcher(secondInfo);

        // Check if there is a match
        int warrantyPeriod = 0;
        if (matcher.find()) {
            String extractedNumber = matcher.group(1);
            warrantyPeriod = Integer.parseInt(extractedNumber);
            System.out.println("Warranty Period: " + warrantyPeriod);
        } else {
            System.out.println("No warranty information found");
        }
        return warrantyPeriod;
    }

    private void addLabelWithMargin(JPanel panel, String text, int alignment) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(alignment);
        label.setBorder(BorderFactory.createEmptyBorder(12, 30, 0, 0));
        panel.add(label);
    }

    private String getItemsAvailable(String productId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\user\\OneDrive - University of Westminster\\Desktop\\OOP CW\\OOP_sourcecode\\src\\products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (productId.equals(values[1])) {
                    return values[3];
                }
            }
        } catch (IOException e) {
        }
        return "";
    }

    private class AvailabilityRenderer extends DefaultTableCellRenderer {

        private final Color LIGHT_RED = new Color(255, 200, 200); // Adjust the RGB values as needed

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String productId = table.getValueAt(row, 0).toString();
            String availabilityString = getItemsAvailable(productId);

            int availability = 0;
            try {
                availability = Integer.parseInt(availabilityString);
            } catch (NumberFormatException e) {
            }

            // Check if availability is less than 3 and set the background color to red for the entire row
            if (availability < 3) {
                cellComponent.setBackground(LIGHT_RED);
            } else {
                cellComponent.setBackground(table.getBackground());
            }

            return cellComponent;
        }
    }

}
