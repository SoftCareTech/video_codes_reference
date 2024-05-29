package com.softcaretech.excelapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softcaretech.excelapp.model.ExcelHelper;
import com.softcaretech.excelapp.model.Product;
import com.softcaretech.excelapp.model.TransactionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
/*
 // search for "org.apache.poi"
    add  --poi
         --poi-ooxml 4.0.0 work for 21.  >  than this require Android min sdk 26
    search for "com.google.android.material"
            -- add material
com.bea.xml
 run

MethodHandle.invoke and MethodHandle.invokeExact are only supported starting with Android O (--min-api 26): Lorg/apache/logging/log4j/util/ServiceLoaderUtil;callServiceLoader(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Class;Ljava/lang/ClassLoader;Z)Ljava/lang/Iterable;

https://github.com/FasterXML/jackson-dataformat-xml/issues/533
 */


    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    private FloatingActionButton addProductButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize product list and adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        // Load products and notify adapter
        loadProducts();
    }

    private void loadProducts() {
        try {
            File file = new File(this.getFilesDir(), "excel1.xlsx");
            ExcelHelper  excelHelper = new ExcelHelper(file.getAbsolutePath());
            productList.addAll(excelHelper.getProducts());
            productAdapter.notifyDataSetChanged();
            // Notify adapter of data changes
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}