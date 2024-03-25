package com.oop.view;

import com.oop.cart.ShoppingCart;
import com.oop.model.Clothing;
import com.oop.model.Electronics;
import com.oop.model.Product;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ShoppingCartWindow extends JFrame {

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private final ShoppingCart shoppingCart;

    private JLabel totalLabel;
    private JLabel firstPurchaseDiscountLabel;
    private JLabel threeItemsDiscountLabel;
    private JLabel finalTotalLabel;
    // Panel for displaying discount-related information
    private JPanel discountPanel;

    public ShoppingCartWindow(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
        initUI();
    }

    private void initUI() {
        // Set the title
        setTitle("Shopping Cart");

        // Set the size of the window
        setSize(700, 500);

        // Create a table model for the cart table
        cartTableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Return the column class as Object to allow rendering of any type
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };
        cartTableModel.addColumn("Product");
        cartTableModel.addColumn("Quantity");
        cartTableModel.addColumn("Price($)");

        // Create the cart table with the table model
        cartTable = new JTable(cartTableModel);

        // Set the row height
        cartTable.setRowHeight(30);

        // Make table headings bold
        JTableHeader tableHeader = cartTable.getTableHeader();
        tableHeader.setFont(tableHeader.getFont().deriveFont(Font.BOLD));

        // Add the cart table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(cartTable);

        // Set layout manager
        setLayout(new BorderLayout());

        // Add the scroll pane to the center of the frame
        add(tableScrollPane, BorderLayout.CENTER);

        // Add labels for displaying discount information
        totalLabel = new JLabel("Total");
        firstPurchaseDiscountLabel = new JLabel("First Purchase Discount(10%)");
        threeItemsDiscountLabel = new JLabel("Three Items in same Category Discount(20%)");
        finalTotalLabel = new JLabel("Final Total");

        // Set layout manager for the discount labels
        discountPanel = new JPanel(new GridLayout(4, 2));
        discountPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        discountPanel.add(totalLabel);
        discountPanel.add(firstPurchaseDiscountLabel);
        discountPanel.add(threeItemsDiscountLabel);
        discountPanel.add(finalTotalLabel);

        // Add the discount labels to the SOUTH position of the frame
        add(discountPanel, BorderLayout.SOUTH);

        // Set the default close operation
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    // Method to update the cart table with the current shopping cart items
    public void updateCartTable(List<Product> cartItems) {
        // Clear existing data in the table model
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        model.setRowCount(0);

        // Create a mapping to keep track of product rows in the table
        // Key: Product ID, Value: Row index in the table
        Map<String, Integer> productRowMap = new HashMap<>();

        // Iterate through the cart items and add/update them in the table
        for (Product product : cartItems) {
            String productId = product.getProductId();
            int quantity = shoppingCart.getProductQuantity(product);
            double totalPrice = product.getPrice() * quantity;

            // Check if the product is already in the table
            if (productRowMap.containsKey(productId)) {
                // If yes, update the quantity and total price
                int rowIndex = productRowMap.get(productId);
                model.setValueAt(quantity, rowIndex, 1);  // Update Quantity
                model.setValueAt(totalPrice, rowIndex, 2);  // Update Total Price
            } else {
                // If no, add a new row with a custom string for the product
                String productInfo = getProductInfoString(product);  // Custom string based on product category
                Object[] rowData = new Object[]{productInfo, quantity, totalPrice};
                model.addRow(rowData);
                // Update the mapping with the new row index
                productRowMap.put(productId, model.getRowCount() - 1);
            }
        }

        // Clear the existing components in the discountPanel
        discountPanel.removeAll();

        // Calculate discounts and final total
        double totalCost = shoppingCart.calculateTotalCost();
        double firstPurchaseDiscount = shoppingCart.hasMadePurchase() ? 0.1 * totalCost : 0;
        double categoryDiscount = shoppingCart.hasThreeItemsInSameCategory() ? 0.2 * totalCost : 0;
        double finalTotal = totalCost - firstPurchaseDiscount - categoryDiscount;

        // Add labels with discount-related information to the discountPanel
        addLabelWithMargin(discountPanel, "Total", SwingConstants.RIGHT);
        addLabelWithMargin(discountPanel, String.format("%.2f$", totalCost), SwingConstants.LEFT);

        addLabelWithMargin(discountPanel, "First Purchase Discount (10%)", SwingConstants.RIGHT);
        addLabelWithMargin(discountPanel, String.format("- %.2f$", firstPurchaseDiscount), SwingConstants.LEFT);

        addLabelWithMargin(discountPanel, "Three Items in Same Category Discount (20%)", SwingConstants.RIGHT);
        addLabelWithMargin(discountPanel, String.format("- %.2f$", categoryDiscount), SwingConstants.LEFT);

        addLabelWithMargin(discountPanel, "Final Total", SwingConstants.RIGHT);
        addLabelWithMargin(discountPanel, String.format("%.2f$", finalTotal), SwingConstants.LEFT);

        // Repaint the panel to reflect the changes
        discountPanel.revalidate();
        discountPanel.repaint();
    }

    // Helper method to get a custom string based on product category
    private String getProductInfoString(Product product) {
        if (product instanceof Electronics) {
            Electronics electronicsProduct = (Electronics) product;
            return String.format("%s, %s, %s, %s",
                    electronicsProduct.getProductId(),
                    electronicsProduct.getProductName(),
                    electronicsProduct.getBrand(),
                    electronicsProduct.getWarrantyPeriod());
        } else if (product instanceof Clothing) {
            Clothing clothingProduct = (Clothing) product;
            return String.format("%s, %s, %s, %s",
                    clothingProduct.getProductId(),
                    clothingProduct.getProductName(),
                    clothingProduct.getSize(),
                    clothingProduct.getColor());
        } else {
            return "";
        }
    }

    private void addLabelWithMargin(JPanel panel, String text, int alignment) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(alignment);
        label.setBorder(BorderFactory.createEmptyBorder(12, 30, 0, 0));
        panel.add(label);
    }

}
