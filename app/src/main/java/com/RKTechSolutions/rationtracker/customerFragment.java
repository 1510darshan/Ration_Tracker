package com.RKTechSolutions.rationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.RKTechSolutions.rationtracker.Adapter.CustomerAdapter;
import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class customerFragment extends Fragment {

    private FloatingActionButton addCustomer;
    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private DBHelper dbHelper;
    private EditText searchEditText;

    public customerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer, container, false);

        // Initialize views
        dbHelper = DBHelper.getInstance(getActivity());
        recyclerView = view.findViewById(R.id.recyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        addCustomer = view.findViewById(R.id.AddCustomer);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        // Load customer list
        loadCustomers();

        // Setup FAB click listener
        addCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCustomerActivity.class);
            startActivity(intent);
        });

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload customers every time fragment is visible (to reflect any updates)
        loadCustomers();
        // Clear search when returning
        searchEditText.setText("");
    }


    private void loadCustomers() {
        List<DBHelper.Customer> customers = dbHelper.getAllCustomers();

        if (adapter == null) {
            adapter = new CustomerAdapter(getActivity(), customers);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(customers);
        }
    }


    private void filterCustomers(String query) {
        if (adapter != null) {
            if (query.isEmpty()) {
                // If search is empty, show all customers
                loadCustomers();
            } else {
                // Search in database
                List<DBHelper.Customer> filtered = dbHelper.searchCustomers(query);
                adapter.updateList(filtered);
            }
        }
    }
}