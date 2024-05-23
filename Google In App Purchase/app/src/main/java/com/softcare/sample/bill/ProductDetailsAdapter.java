package com.softcare.sample.bill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.softcare.sample.R;

import java.util.List;
import java.util.Set;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {
    private static final String T = "softcaretech";
    public interface OnProductClickListener {
        void onProductClick(ProductDetails productDetails);
    }

    private List<ProductDetails> productDetailsList;
    private Set<String> alreadyPurchased ;
    private Context context;
    private OnProductClickListener onProductClickListener;

    public ProductDetailsAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.onProductClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetails productDetails = productDetailsList.get(position);

        holder.titleTextView.setText(productDetails.getTitle());
        holder.priceTextView.setText(productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());
        holder.descriptionTextView.setText(productDetails.getDescription());


        if(alreadyPurchased!=null) {
            if (alreadyPurchased.contains(productDetails.getProductId())) {
                holder.buyButton.setText("Paid");
                holder.buyButton.setEnabled(false);
                return;// return to avoid setting lisenter
            }
        }
        // Set click listener to notify when a product is clicked
        holder.buyButton.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductClick(productDetails);
            }else{
                Log.e(T, "NULL listener pass in the addapter");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (productDetailsList == null) return 0;
        return productDetailsList.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setProductDetailsList(List<ProductDetails> productDetailsList, Set<String> alreadyPurchased) {
        this.productDetailsList = productDetailsList;
        this.alreadyPurchased=alreadyPurchased;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView priceTextView;
        TextView descriptionTextView;
        Button buyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            buyButton= itemView.findViewById(R.id.buyButton);
        }
    }
}
