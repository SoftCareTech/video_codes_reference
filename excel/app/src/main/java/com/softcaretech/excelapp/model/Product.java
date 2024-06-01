package com.softcaretech.excelapp.model;

import androidx.annotation.NonNull;

/**
 * Class representing a product with its details.
 */
public class Product {
    private final String name;                      // Name of the product
    private final int count;                        // Quantity of the product
    private final double price;                     // Price of the product
    private final TransactionType transactionType;  // Type of transaction
    private final String description;               // Description of the product

    /**
     * Constructor to initialize a product with its details.
     *
     * @param name            Name of the product.
     * @param count           Quantity of the product.
     * @param price           Price of the product.
     * @param transactionType Type of transaction for the product.
     * @param description     Description of the product.
     */
    public Product(String name, int count, double price, TransactionType transactionType, String description) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.transactionType = transactionType;
        this.description = description;
    }

    /**
     * Get the name of the product.
     *
     * @return Name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the quantity of the product.
     *
     * @return Quantity of the product.
     */
    public int getCount() {
        return count;
    }

    /**
     * Get the price of the product.
     *
     * @return Price of the product.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Get the transaction type of the product.
     *
     * @return Transaction type of the product.
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Get the description of the product.
     *
     * @return Description of the product.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a string representation of the product.
     *
     * @return A string containing the product details.
     */
    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", price=" + price +
                ", transactionType=" + transactionType.getDisplayName() +
                ", description='" + description + '\'' +
                '}';
    }
}
