package com.softcaretech.excelapp.model;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;
import com.softcaretech.excelapp.R;

import java.util.List;

public class AddProductBottomSheetDialog extends BottomSheetDialogFragment {

    private EditText editTextName, editTextCount, editTextPrice, editTextDescription;
    private Spinner spinnerTransactionType;
    private Button buttonSave;
    private OnProductAddedListener listener;

    public interface OnProductAddedListener {
        void onProductAdded(Product product);
    }

    public void setOnProductAddedListener(OnProductAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_product, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextCount = view.findViewById(R.id.editTextCount);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerTransactionType = view.findViewById(R.id.spinnerTransactionType);
        buttonSave = view.findViewById(R.id.buttonSave);
        // First, get the list of display names
        List<String> displayNames = TransactionType.getDisplayNames();

        // Create the ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayNames);

        // Set the dropdown view resource
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the adapter to the spinner
        spinnerTransactionType.setAdapter(adapter);


        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            int count = Integer.parseInt(editTextCount.getText().toString());
            double price = Double.parseDouble(editTextPrice.getText().toString());
            String transactionTypeStr = spinnerTransactionType.getSelectedItem().toString();
            TransactionType transactionType = TransactionType.valueOf(transactionTypeStr.toUpperCase());
            String description = editTextDescription.getText().toString();

            Product product = new Product(name, count, price, transactionType, description);
            if (listener != null) {
                listener.onProductAdded(product);
            }
            dismiss();
        });

        return view;
    }
}
