package com.softcare.sample;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.material.snackbar.Snackbar;
import com.softcare.sample.bill.BillingClientSetup;
import com.softcare.sample.bill.ProductDetailsAdapter;
import com.softcare.sample.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuyProductActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    Context mContext;
    private static final String T = "softcareD";
    private RecyclerView productsRecyclerView;
    private ProductDetailsAdapter productDetailsAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);
        mContext = getApplicationContext();
        productsRecyclerView = findViewById(R.id.products_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(layoutManager);
        productDetailsAdapter = new ProductDetailsAdapter(this,   (productDetails -> {
                    // Handle the selected productDetails
                    launchBillingFlow(productDetails );
                }));
        productsRecyclerView.setAdapter(productDetailsAdapter);
        progressBar = findViewById(R.id.progressBar);

        setupBillingClient();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setupBillingClient() {

        acknowledgePurchaseResponseListener = billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                ///Toast.makeText(mContext, "Billing client ok", Toast.LENGTH_LONG).show();

            } else {
                Log.e(T, "Billing client not okay 55");
            }
        };

        billingClient = BillingClientSetup.getInstance(this,//context
                this// PurchasesUpdatedListener
        );
        progressBar.setVisibility(View.VISIBLE);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build(),
                            (billingResult1, purchases) -> handlePurchase(billingResult1,purchases)
                    );
                } else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(productsRecyclerView, mContext.getString(R.string.bill_error),
                            Snackbar.LENGTH_INDEFINITE).setAction(mContext.getString(R.string.try_again),
                            k -> {
                                setupBillingClient();
                            }).show();
                     Log.e(T, "Billing client not okay 77");
                }

            }


            @Override
            public void onBillingServiceDisconnected() {
                // Toast.makeText(mContext, "You are disconnected", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    //PurchasesUpdatedListener  impleted method
   public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            handlePurchase(billingResult,purchases);
    }

    Set<String> purchasedIds  = new HashSet<>();
    private void handlePurchase(BillingResult billingResult, List<Purchase> purchases  ) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK  && purchases != null) {

            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.isAcknowledged()) {
                        Log.d(T,"Not acknowledge "+ purchase.toString());
                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.getPurchaseToken())
                                        .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);

                    }else{
                        for(String productId:purchase.getProducts()) {
                            purchasedIds.add(productId);
                            if (BillingClientSetup.ADVANCE_MIC_1.equals(productId)) {
                                SharedPreferences s = getSharedPreferences("Softcare", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = s.edit();
                                editor.putBoolean("advance1", true);
                                editor.apply();
                                editor.commit();
                            }else{
                                Log.i(T, "Another product with ID "+productId);
                            }

                            //End all product acknowledge at a purchase
                        }

                    }
                }

            }

            loadProduct();

        }
        else if ( billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            Toast.makeText(mContext, getString(R.string.already_purchase), Toast.LENGTH_LONG).show();
            setupBillingClient();
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(mContext, getString(R.string.you_canceled_the_operation), Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(mContext, getString(R.string.error_occurred_try_again), Toast.LENGTH_LONG).show();
        }

    }

    public void loadProduct() {
        if (billingClient != null) {
            if (billingClient.isReady()) {
                final List<QueryProductDetailsParams.Product> products =
                        //ImmutableList.of
                        Arrays.asList(
                        QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(BillingClientSetup.ADVANCE_MIC_1)
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()
                               // ,... others
                );
                QueryProductDetailsParams queryProductDetailsParams=
                        QueryProductDetailsParams.newBuilder()
                        .setProductList(products).build();

                billingClient.queryProductDetailsAsync(queryProductDetailsParams, (billingResult, list) -> {

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update UI elements here
                                productDetailsAdapter.setProductDetailsList(list, purchasedIds);
                                Log.e(T, "Product "+purchasedIds  );
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update UI elements here
                                progressBar.setVisibility(View.GONE);
                                Log.e(T, "Billing client not okay ");
                                Snackbar.make(productsRecyclerView, mContext.getString(R.string.bill_error),
                                        Snackbar.LENGTH_INDEFINITE).setAction(mContext.getString(R.string.try_again),
                                        k -> {
                                            loadProduct();
                                        }).show();
                            }
                        });


                    }
                });

            } else{
                progressBar.setVisibility(View.GONE);
                //else view.setEnabled(true);
            }
        } else{
            progressBar.setVisibility(View.GONE);
            //else view.setEnabled(true);
        }


    }

    private void launchBillingFlow(ProductDetails selectedProduct  ) {

        BillingFlowParams.ProductDetailsParams productDetailsParams =
              BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(selectedProduct)
                        .build();
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().
                setProductDetailsParamsList(Arrays.asList(productDetailsParams)).build();


        int response = billingClient.launchBillingFlow(this, billingFlowParams). getResponseCode();

    }









}