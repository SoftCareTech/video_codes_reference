package com.softcaretech.excelapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softcaretech.excelapp.model.AddProductBottomSheetDialog;
import com.softcaretech.excelapp.model.ExcelHelper;
import com.softcaretech.excelapp.model.ExcelHelperXLS;
import com.softcaretech.excelapp.model.Product;
import com.softcaretech.excelapp.model.TransactionType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AddProductBottomSheetDialog.OnProductAddedListener {

    public static final String TAG = "Excel1";
/*
Apache POI is a powerful Java library for reading and writing Microsoft Office documents, including Excel,
Word, and PDF files. Although it is primarily designed for Java SE and Java EE environments,it can also be
used in Android development with some adjustments.
Here, we'll walk through the steps to configure Apache POI and POI-OOXML for an Android project to work
with Excel files.


 1. Create a project or open an existing android project.
 2. Select
  File->project structure->app
 3.  In declarative dependencies section
   Click add library ico(plus ico) -> library dependence
 //Search for "org.apache.poi"
            Version 4.0.1 work for min sdk version 21.
            Version greater than 4.1.0  require Android min sdk 26.
            Make sure poi and poi-ooxml are having the same version.
            I advise you to use the latest(now it 5.2.5 if you are  running  at min SDK.
    add  --poi
         --poi-ooxml.
  Search for "javax.xml.stream" if you are using  Version 4.0.1 of poi and poi.ooxml
    add  --stax-api

 Optional  Library for View
 Search for "com.google.android.material"
            -- add material
 */

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ProductAdapter productAdapter;
    private List<Product> productList;

    private FloatingActionButton addProductButton;
    private ExcelHelperXLS excelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FloatingActionButton fabAddProduct = findViewById(R.id.addProductButton);
        fabAddProduct.setOnClickListener(v -> {
            AddProductBottomSheetDialog dialog = new AddProductBottomSheetDialog();
            dialog.setOnProductAddedListener(MainActivity.this);
            dialog.show(getSupportFragmentManager(), "AddProductBottomSheetDialog");

//           Product  product=new Product("Lap", 10, 99.99,
//                   TransactionType.SELL, "High");
//            if (excelHelper.addProduct(product)) {
//                productList.add(product);
//                productAdapter.notifyItemInserted(productList.size() - 1);
//            } else {
//                Toast.makeText(this, "Error adding product", Toast.LENGTH_SHORT).show();
//            }


        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize product list and adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);



    }


    @Override
    protected void onResume() {
        super.onResume();
        // Load products and notify adapter
        loadProducts();
    }

    private void loadProducts26() {
        try {
            File file = new File(this.getFilesDir(), "excel");
            ExcelHelper  excelHelper = new ExcelHelper(file.getAbsolutePath());
            ExcelHelper  excelHelper1 = new ExcelHelper(file.getAbsolutePath());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String result = "Result";
                   excelHelper.openNew ();
                    excelHelper.addProduct(new Product("Laptop1", 10, 999.99, TransactionType.SELL, "High-end gaming laptop"));
                    excelHelper.addProduct(new Product("Monitor", 5, 199.99, TransactionType.RECEIPT, "24-inch LED monitor"));
                    try {
                        excelHelper.save();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                      excelHelper1.open();
                       List<Product> products =excelHelper1.getProducts();

                    // Update UI from main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI with result
                            productList.addAll(products);
                            productAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Products loaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            // Notify adapter of data changes
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void onPause() {
        try {
            executorService.execute(() -> {
                try {
                    boolean saved=   excelHelper.save();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI with result
                            if(saved) {
                                Toast.makeText(MainActivity.this, "Saved products", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(MainActivity.this, "Error Saving products", Toast.LENGTH_SHORT).show();}
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI with result
                            Toast.makeText(MainActivity.this, "Error Saving products", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            });

            // Notify adapter of data changes
        } catch (Exception e) {
            Toast.makeText(this, "Error Saving products", Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }



    private void loadProducts21() {
        try {
            File file = new File(this.getFilesDir(), "excel");
            ExcelHelperXLS excelHelper = new ExcelHelperXLS(file.getAbsolutePath());
            ExcelHelperXLS  excelHelper1 = new ExcelHelperXLS(file.getAbsolutePath());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    excelHelper.openNew ();
                    excelHelper.addProduct(new Product("Laptop1", 10, 999.99, TransactionType.SELL, "High-end gaming laptop"));
                    excelHelper.addProduct(new Product("Monitor", 5, 199.99, TransactionType.RECEIPT, "24-inch LED monitor"));
                    try {
                        excelHelper.save();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    excelHelper1.open();
                    List<Product> products =excelHelper1.getProducts();
                    // Update UI from main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI with result
                            productList.addAll(products);
                            productAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

            // Notify adapter of data changes
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void loadProducts() {
        try {
            File file = new File(this.getFilesDir(), "excel");
            excelHelper = new ExcelHelperXLS(file.getAbsolutePath());
            executorService.execute(() -> {
                excelHelper.openNew ();
                excelHelper.addProduct(new Product("Laptop1", 10, 999.99, TransactionType.SELL, "High-end gaming laptop"));
                excelHelper.addProduct(new Product("Monitor", 5, 199.99, TransactionType.RECEIPT, "24-inch LED monitor"));
                try {
                    excelHelper.open();
                    List<Product> products =excelHelper.getProducts();
                    // Update UI from main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI with result
                            productList.addAll(products);
                            productAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            });

            // Notify adapter of data changes
        } catch (IOException e) {
            Toast.makeText(this, "Error Loading products", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onProductAdded(Product product) {
        if (excelHelper.addProduct(product)) {
            productList.add(product);
            productAdapter.notifyItemInserted(productList.size() - 1);
        } else {
            Toast.makeText(this, "Error adding product", Toast.LENGTH_SHORT).show();
        }
    }
}