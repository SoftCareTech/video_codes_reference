package com.softcaretech.excelapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softcaretech.excelapp.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.nameTextView.setText(product.getName());
        holder.countTextView.setText("Count: " + product.getCount());
        holder.priceTextView.setText("Price: $" + product.getPrice());
        holder.transactionTypeTextView.setText("Transaction: " + product.getTransactionType().getDisplayName());
        holder.descriptionTextView.setText("Description: " + product.getDescription());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, countTextView, priceTextView, transactionTypeTextView, descriptionTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            countTextView = itemView.findViewById(R.id.countTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            transactionTypeTextView = itemView.findViewById(R.id.transactionTypeTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
