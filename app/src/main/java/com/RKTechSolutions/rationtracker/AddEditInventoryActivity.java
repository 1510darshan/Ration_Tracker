package com.RKTechSolutions.rationtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditInventoryActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";
    public static final String EXTRA_ITEM_NAME = "item_name";
    public static final String EXTRA_QUANTITY = "quantity";
    public static final String EXTRA_IS_EDIT_MODE = "is_edit_mode";

    private TextInputEditText itemNameEditText, quantityEditText;
    private MaterialButton saveButton, cancelButton, deleteButton;
    private TextView headerTitle;
    private ImageView headerIcon;

    private DBHelper dbHelper;
    private boolean isEditMode = false;
    private long itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_inventory);

        // Initialize views
        initViews();

        // Initialize database
        dbHelper = DBHelper.getInstance(this);

        // Check if in edit mode
        checkEditMode();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        itemNameEditText = findViewById(R.id.itemNameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        deleteButton = findViewById(R.id.deleteButton);
        headerTitle = findViewById(R.id.headerTitle);
        headerIcon = findViewById(R.id.headerIcon);
    }

    private void checkEditMode() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isEditMode = extras.getBoolean(EXTRA_IS_EDIT_MODE, false);

            if (isEditMode) {
                // Edit mode
                headerTitle.setText("Edit Inventory Item");
                saveButton.setText("Update Item");
                deleteButton.setVisibility(View.VISIBLE);

                // Load existing data
                itemId = extras.getLong(EXTRA_ITEM_ID, -1);
                String itemName = extras.getString(EXTRA_ITEM_NAME, "");
                double quantity = extras.getDouble(EXTRA_QUANTITY, 0);

                // Set data to fields
                itemNameEditText.setText(itemName);
                quantityEditText.setText(String.valueOf(quantity));

                // Set icon based on item
                setItemIcon(itemName);
            } else {
                // Add mode
                headerTitle.setText("Add Inventory Item");
                saveButton.setText("Save Item");
                deleteButton.setVisibility(View.GONE);
            }
        }
    }

    private void setItemIcon(String itemName) {
        String lowerName = itemName.toLowerCase();
        if (lowerName.contains("wheat")) {
            headerIcon.setImageResource(R.drawable.wheat);
        } else if (lowerName.contains("rice")) {
            headerIcon.setImageResource(R.drawable.wheat);
        } else if (lowerName.contains("sugar")) {
            headerIcon.setImageResource(R.drawable.wheat);
        } else if (lowerName.contains("kerosene") || lowerName.contains("oil")) {
            headerIcon.setImageResource(R.drawable.wheat);
        } else {
            headerIcon.setImageResource(R.drawable.wheat);
        }
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveItem());
        cancelButton.setOnClickListener(v -> finish());
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void saveItem() {
        // Validate inputs
        String itemName = itemNameEditText.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();

        if (itemName.isEmpty()) {
            itemNameEditText.setError("Item name is required");
            itemNameEditText.requestFocus();
            return;
        }

        if (quantityStr.isEmpty()) {
            quantityEditText.setError("Quantity is required");
            quantityEditText.requestFocus();
            return;
        }

        double quantity = Double.parseDouble(quantityStr);

        if (quantity <= 0) {
            quantityEditText.setError("Quantity must be greater than 0");
            quantityEditText.requestFocus();
            return;
        }

        boolean success;
        if (isEditMode) {
            // Update existing item - get current item from database to preserve unit and price
            DBHelper.RationItem currentItem = dbHelper.getRationItem(itemId);
            if (currentItem != null) {
                success = dbHelper.updateRationItem(itemId, itemName, currentItem.unit, quantity, currentItem.price);
                if (success) {
                    Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                success = false;
            }
        } else {
            // Add new item - use default values for unit and price
            success = dbHelper.addRationItem(itemName, "kg", quantity, 0.0);
            if (success) {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        }

        if (success) {
            finish();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem() {
        if (itemId != -1) {
            boolean success = dbHelper.deleteRationItem(itemId);
            if (success) {
                Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
            }
        }
    }
}