package com.RKTechSolutions.rationtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordRationActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_NUMBER = "card_number";
    public static final String EXTRA_CUSTOMER_NAME = "customer_name";

    private DBHelper dbHelper;
    private String cardNumber;
    private String customerName;

    private TextView tvCustomerInfo, tvTotalItems, tvTotalAmount;
    private LinearLayout itemsContainer;
    private TextInputEditText etNotes;
    private MaterialButton btnCancel, btnRecordRation;

    private List<ItemHolder> itemHolders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_ration);

        dbHelper = DBHelper.getInstance(this);

        // Get data from intent
        cardNumber = getIntent().getStringExtra(EXTRA_CARD_NUMBER);
        customerName = getIntent().getStringExtra(EXTRA_CUSTOMER_NAME);

        if (cardNumber == null) {
            Toast.makeText(this, "Error: No customer specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupCustomerInfo();
        checkRationStatus();
        loadRationItems();
        setupListeners();
    }

    private void initializeViews() {
        tvCustomerInfo = findViewById(R.id.tvCustomerInfo);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        itemsContainer = findViewById(R.id.itemsContainer);
        etNotes = findViewById(R.id.etNotes);
        btnCancel = findViewById(R.id.btnCancel);
        btnRecordRation = findViewById(R.id.btnRecordRation);
    }

    private void setupCustomerInfo() {
        if (customerName != null) {
            tvCustomerInfo.setText(customerName + " - " + cardNumber);
        } else {
            DBHelper.Customer customer = dbHelper.getCustomer(cardNumber);
            if (customer != null) {
                tvCustomerInfo.setText(customer.customerName + " - " + cardNumber);
            }
        }
    }

    private void checkRationStatus() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        boolean hasTaken = dbHelper.hasTakenRation(cardNumber, year, month);

        if (hasTaken) {
            new AlertDialog.Builder(this)
                    .setTitle("Already Taken")
                    .setMessage("This customer has already taken ration for this month. Do you want to record additional items?")
                    .setPositiveButton("Continue", null)
                    .setNegativeButton("Cancel", (dialog, which) -> finish())
                    .show();
        }
    }

    private void loadRationItems() {
        List<DBHelper.RationItem> items = dbHelper.getAllRationItems();

        for (DBHelper.RationItem item : items) {
            addItemView(item);
        }

        // Update totals initially
        updateTotals();
    }

    private void addItemView(DBHelper.RationItem item) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.ration_item_row, itemsContainer, false);

        CheckBox cbItem = itemView.findViewById(R.id.cbItem);
        TextView tvItemName = itemView.findViewById(R.id.tvItemName);
        TextView tvItemDetails = itemView.findViewById(R.id.tvItemDetails);
        TextInputEditText etQuantity = itemView.findViewById(R.id.etQuantity);
        TextView tvItemTotal = itemView.findViewById(R.id.tvItemTotal);

        // Set item data
        tvItemName.setText(item.name);
        tvItemDetails.setText(item.defaultQty + " " + item.unit + " @ ₹" + item.price + "/" + item.unit);
        etQuantity.setText(String.valueOf(item.defaultQty));

        // Create holder
        ItemHolder holder = new ItemHolder(item, cbItem, etQuantity, tvItemTotal);
        itemHolders.add(holder);

        // Set up listeners
        cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etQuantity.setEnabled(isChecked);
            holder.calculateTotal();
            updateTotals();
        });

        etQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.calculateTotal();
                updateTotals();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Initially disable quantity input
        etQuantity.setEnabled(false);

        itemsContainer.addView(itemView);
    }

    private void updateTotals() {
        int totalItems = 0;
        double totalAmount = 0.0;

        for (ItemHolder holder : itemHolders) {
            if (holder.isSelected()) {
                totalItems++;
                totalAmount += holder.getTotal();
            }
        }

        tvTotalItems.setText(String.valueOf(totalItems));
        tvTotalAmount.setText("₹" + String.format("%.2f", totalAmount));
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> finish());

        btnRecordRation.setOnClickListener(v -> {
            recordRation();
        });
    }

    private void recordRation() {
        // Get selected items
        List<ItemHolder> selectedItems = new ArrayList<>();
        for (ItemHolder holder : itemHolders) {
            if (holder.isSelected()) {
                selectedItems.add(holder);
            }
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate quantities
        for (ItemHolder holder : selectedItems) {
            if (holder.getQuantity() <= 0) {
                Toast.makeText(this, "Please enter valid quantities for all selected items", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm Ration Record")
                .setMessage("Record ration for " + selectedItems.size() + " item(s)?\n\nTotal: " + tvTotalAmount.getText())
                .setPositiveButton("Confirm", (dialog, which) -> {
                    saveRationRecord(selectedItems);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveRationRecord(List<ItemHolder> selectedItems) {
        DBHelper.Owner owner = dbHelper.getOwner();
        String issuedBy = owner != null ? owner.ownerName : "Store";

        boolean allSuccess = true;

        for (ItemHolder holder : selectedItems) {
            boolean success = dbHelper.addRationTransaction(
                    cardNumber,
                    holder.item.id,
                    holder.getQuantity(),
                    holder.item.price,
                    issuedBy
            );

            if (!success) {
                allSuccess = false;
                break;
            }
        }

        if (allSuccess) {
            // Create monthly record
            Calendar cal = Calendar.getInstance();
            dbHelper.createMonthlyRecord(cardNumber, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);

            Toast.makeText(this, "Ration recorded successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to record ration. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper class to hold item data
    private class ItemHolder {
        DBHelper.RationItem item;
        CheckBox checkBox;
        TextInputEditText quantityEditText;
        TextView totalTextView;

        ItemHolder(DBHelper.RationItem item, CheckBox checkBox, TextInputEditText quantityEditText, TextView totalTextView) {
            this.item = item;
            this.checkBox = checkBox;
            this.quantityEditText = quantityEditText;
            this.totalTextView = totalTextView;
        }

        boolean isSelected() {
            return checkBox.isChecked();
        }

        double getQuantity() {
            try {
                String qtyStr = quantityEditText.getText().toString().trim();
                return qtyStr.isEmpty() ? 0 : Double.parseDouble(qtyStr);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        double getTotal() {
            return getQuantity() * item.price;
        }

        void calculateTotal() {
            double total = getTotal();
            totalTextView.setText("₹" + String.format("%.2f", total));
        }
    }
}