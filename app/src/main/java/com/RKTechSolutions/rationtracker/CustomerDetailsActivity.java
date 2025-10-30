package com.RKTechSolutions.rationtracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class CustomerDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_NUMBER = "card_number";

    private DBHelper dbHelper;
    private String cardNumber;
    private boolean isEditMode = false;

    // Header
    private TextView tvHeaderName, tvHeaderCard;
    private MaterialButton btnEditSave;

    // Input fields
    private TextInputLayout tilCustomerName, tilCardNumber, tilPhone, tilAddress;
    private TextInputEditText etCustomerName, etCardNumber, etPhone, etAddress;

    // Ration status
    private ImageView ivRationStatus;
    private TextView tvRationStatus, tvRationDate;
    private MaterialButton btnViewHistory, btnRecordRation;

    // Action buttons
    private LinearLayout layoutActionButtons;
    private MaterialButton btnCancel, btnSaveChanges, btnDeleteCustomer;

    // Original values for cancel functionality
    private String originalName, originalPhone, originalAddress;

    // Back press callback
    private OnBackPressedCallback backPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        dbHelper = DBHelper.getInstance(this);

        // Get card number from intent
        cardNumber = getIntent().getStringExtra(EXTRA_CARD_NUMBER);
        if (cardNumber == null) {
            Toast.makeText(this, "Error: No customer specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadCustomerData();
        setupListeners();
        setupBackPressHandler();
    }

    private void setupBackPressHandler() {
        backPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(CustomerDetailsActivity.this)
                        .setTitle("Unsaved Changes")
                        .setMessage("You have unsaved changes. Do you want to discard them?")
                        .setPositiveButton("Discard", (dialog, which) -> {
                            cancelEdit();
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        })
                        .setNegativeButton("Keep Editing", null)
                        .show();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    private void initializeViews() {
        // Header
        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvHeaderCard = findViewById(R.id.tvHeaderCard);
        btnEditSave = findViewById(R.id.btnEditSave);

        // Input layouts and fields
        tilCustomerName = findViewById(R.id.tilCustomerName);
        tilCardNumber = findViewById(R.id.tilCardNumber);
        tilPhone = findViewById(R.id.tilPhone);
        tilAddress = findViewById(R.id.tilAddress);

        etCustomerName = findViewById(R.id.etCustomerName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        // Ration status
        ivRationStatus = findViewById(R.id.ivRationStatus);
        tvRationStatus = findViewById(R.id.tvRationStatus);
        tvRationDate = findViewById(R.id.tvRationDate);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnRecordRation = findViewById(R.id.btnRecordRation);

        // Action buttons
        layoutActionButtons = findViewById(R.id.layoutActionButtons);
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteCustomer = findViewById(R.id.btnDeleteCustomer);
    }

    private void loadCustomerData() {
        DBHelper.Customer customer = dbHelper.getCustomer(cardNumber);
        if (customer == null) {
            Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set header
        tvHeaderName.setText(customer.customerName);
        tvHeaderCard.setText("Card: " + customer.cardNumber);

        // Set form fields
        etCustomerName.setText(customer.customerName);
        etCardNumber.setText(customer.cardNumber);
        etPhone.setText(customer.phone);
        etAddress.setText(customer.address);

        // Store original values
        originalName = customer.customerName;
        originalPhone = customer.phone;
        originalAddress = customer.address;

        // Check ration status for current month
        checkRationStatus();
    }

    private void checkRationStatus() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        boolean hasTaken = dbHelper.hasTakenRation(cardNumber, year, month);

        if (hasTaken) {
            ivRationStatus.setImageResource(R.drawable.baseline_check_circle_24);
            ivRationStatus.setColorFilter(getResources().getColor(android.R.color.holo_green_dark));
            tvRationStatus.setText("Ration Taken");
            tvRationStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvRationDate.setText("Taken this month");
            btnRecordRation.setText("Add More Items");
        } else {
            ivRationStatus.setImageResource(R.drawable.baseline_cancel_24);
            ivRationStatus.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark));
            tvRationStatus.setText("Ration Pending");
            tvRationStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            tvRationDate.setText("Not taken this month");
            btnRecordRation.setText("Record Ration");
        }
    }

    private void setupListeners() {
        btnEditSave.setOnClickListener(v -> {
            if (isEditMode) {
                saveChanges();
            } else {
                enableEditMode();
            }
        });

        btnCancel.setOnClickListener(v -> cancelEdit());

        btnSaveChanges.setOnClickListener(v -> saveChanges());

        btnDeleteCustomer.setOnClickListener(v -> confirmDelete());

        btnViewHistory.setOnClickListener(v -> {
            // Navigate to Customer History Activity
            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerHistoryActivity.class);
            intent.putExtra(CustomerHistoryActivity.EXTRA_CARD_NUMBER, cardNumber);
            intent.putExtra(CustomerHistoryActivity.EXTRA_CUSTOMER_NAME, originalName);
            startActivity(intent);
        });

        btnRecordRation.setOnClickListener(v -> {
            // Navigate to Record Ration Activity
            Intent intent = new Intent(CustomerDetailsActivity.this, RecordRationActivity.class);
            intent.putExtra(RecordRationActivity.EXTRA_CARD_NUMBER, cardNumber);
            intent.putExtra(RecordRationActivity.EXTRA_CUSTOMER_NAME, originalName);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRationStatus();
    }

    private void enableEditMode() {
        isEditMode = true;
        backPressedCallback.setEnabled(true);

        btnEditSave.setText("Save");
        btnEditSave.setIcon(getResources().getDrawable(R.drawable.baseline_check_24));

        etCustomerName.setEnabled(true);
        etPhone.setEnabled(true);
        etAddress.setEnabled(true);

        layoutActionButtons.setVisibility(View.VISIBLE);
        btnRecordRation.setVisibility(View.GONE);

        tilCustomerName.setBoxBackgroundColorResource(R.color.edit_background);
        tilPhone.setBoxBackgroundColorResource(R.color.edit_background);
        tilAddress.setBoxBackgroundColorResource(R.color.edit_background);

        Toast.makeText(this, "Edit mode enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableEditMode() {
        isEditMode = false;
        backPressedCallback.setEnabled(false);

        btnEditSave.setText("Edit");
        btnEditSave.setIcon(getResources().getDrawable(R.drawable.baseline_edit_24));

        etCustomerName.setEnabled(false);
        etPhone.setEnabled(false);
        etAddress.setEnabled(false);

        layoutActionButtons.setVisibility(View.GONE);
        btnRecordRation.setVisibility(View.VISIBLE);

        tilCustomerName.setBoxBackgroundColorResource(android.R.color.transparent);
        tilPhone.setBoxBackgroundColorResource(android.R.color.transparent);
        tilAddress.setBoxBackgroundColorResource(android.R.color.transparent);
    }

    private void cancelEdit() {
        etCustomerName.setText(originalName);
        etPhone.setText(originalPhone);
        etAddress.setText(originalAddress);

        tilCustomerName.setError(null);
        tilPhone.setError(null);

        disableEditMode();
        Toast.makeText(this, "Changes cancelled", Toast.LENGTH_SHORT).show();
    }

    private void saveChanges() {
        String name = etCustomerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            tilCustomerName.setError("Name is required");
            etCustomerName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            tilPhone.setError("Enter valid 10-digit phone number");
            etPhone.requestFocus();
            return;
        }

        tilCustomerName.setError(null);
        tilPhone.setError(null);

        boolean success = dbHelper.updateCustomer(cardNumber, name, phone, address);

        if (success) {
            originalName = name;
            originalPhone = phone;
            originalAddress = address;

            tvHeaderName.setText(name);

            disableEditMode();
            Toast.makeText(this, "Customer details updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update customer", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Deactivate Customer")
                .setMessage("Are you sure you want to deactivate this customer? This will hide them from the customer list but preserve their transaction history.")
                .setPositiveButton("Deactivate", (dialog, which) -> deleteCustomer())
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.baseline_warning_24)
                .show();
    }

    private void deleteCustomer() {
        boolean success = dbHelper.deactivateCustomer(cardNumber);

        if (success) {
            Toast.makeText(this, "Customer deactivated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to deactivate customer", Toast.LENGTH_SHORT).show();
        }
    }
}