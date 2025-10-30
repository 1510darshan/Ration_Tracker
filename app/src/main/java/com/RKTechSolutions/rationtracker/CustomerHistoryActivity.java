package com.RKTechSolutions.rationtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.RKTechSolutions.rationtracker.Adapter.TransactionHistoryAdapter;
import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CustomerHistoryActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_NUMBER = "card_number";
    public static final String EXTRA_CUSTOMER_NAME = "customer_name";

    private DBHelper dbHelper;
    private String cardNumber;
    private String customerName;

    // Views
    private TextView tvCustomerName, tvCardNumber, tvNoTransactions;
    private Spinner spinnerMonth, spinnerYear;
    private RecyclerView recyclerTransactions;
    private MaterialCardView cardSummary;
    private TextView tvTotalItems, tvTotalAmount;
    private MaterialButton btnBack;
    private ImageView ivEmptyState;

    // Adapter
    private TransactionHistoryAdapter adapter;

    // Current selected month and year
    private int selectedMonth;
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history);

        dbHelper = DBHelper.getInstance(this);

        // Get data from intent
        cardNumber = getIntent().getStringExtra(EXTRA_CARD_NUMBER);
        customerName = getIntent().getStringExtra(EXTRA_CUSTOMER_NAME);

        if (cardNumber == null || customerName == null) {
            Toast.makeText(this, "Error: Invalid customer data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initializeViews();

        // Setup spinners
        setupMonthYearSpinners();

        // Setup RecyclerView
        setupRecyclerView();

        // Load initial data
        loadTransactions();

        // Setup listeners
        setupListeners();
    }

    private void initializeViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        cardSummary = findViewById(R.id.cardSummary);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnBack = findViewById(R.id.btnBack);
        tvNoTransactions = findViewById(R.id.tvNoTransactions);
        ivEmptyState = findViewById(R.id.ivEmptyState);

        // Set customer info
        tvCustomerName.setText(customerName);
        tvCardNumber.setText("Card: " + cardNumber);
    }

    private void setupMonthYearSpinners() {
        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);

        // Month spinner
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(selectedMonth - 1);

        // Year spinner (last 5 years to current year)
        List<String> years = new ArrayList<>();
        int currentYear = calendar.get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 4; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(0); // Current year
    }

    private void setupRecyclerView() {
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerTransactions.setHasFixedSize(true);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position + 1;
                loadTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                loadTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadTransactions() {
        List<DBHelper.RationTransaction> transactions =
                dbHelper.getMonthlyTransactions(cardNumber, selectedYear, selectedMonth);

        if (transactions.isEmpty()) {
            // Show empty state
            recyclerTransactions.setVisibility(View.GONE);
            cardSummary.setVisibility(View.GONE);
            tvNoTransactions.setVisibility(View.VISIBLE);
            ivEmptyState.setVisibility(View.VISIBLE);
        } else {
            // Show transactions
            recyclerTransactions.setVisibility(View.VISIBLE);
            cardSummary.setVisibility(View.VISIBLE);
            tvNoTransactions.setVisibility(View.GONE);
            ivEmptyState.setVisibility(View.GONE);

            // Setup adapter
            if (adapter == null) {
                adapter = new TransactionHistoryAdapter(transactions);
                recyclerTransactions.setAdapter(adapter);
            } else {
                adapter.updateTransactions(transactions);
            }

            // Calculate and display summary
            calculateSummary(transactions);
        }
    }

    private void calculateSummary(List<DBHelper.RationTransaction> transactions) {
        int totalItems = transactions.size();
        double totalAmount = 0.0;

        for (DBHelper.RationTransaction transaction : transactions) {
            totalAmount += (transaction.quantity * transaction.unitPrice);
        }

        tvTotalItems.setText(totalItems + " items");
        tvTotalAmount.setText("₹" + String.format(Locale.getDefault(), "%.2f", totalAmount));
    }
}