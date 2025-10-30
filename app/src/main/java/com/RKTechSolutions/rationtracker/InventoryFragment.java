package com.RKTechSolutions.rationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.RKTechSolutions.rationtracker.Adapter.InventoryAdapter;
import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.RKTechSolutions.rationtracker.Model.Inventory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextInputEditText searchEditText;
    private FloatingActionButton fabAddItem;
    private InventoryAdapter adapter;
    private DBHelper dbHelper;
    private List<Inventory> inventoryList;
    private List<DBHelper.RationItem> rationItemsList;

    public InventoryFragment() {
        // Required empty public constructor
    }

    public static InventoryFragment newInstance() {
        return new InventoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBHelper.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        fabAddItem = view.findViewById(R.id.fabAddInventory);

        // Setup RecyclerView
        setupRecyclerView();

        // Load inventory data
        loadInventoryData();

        // Setup search functionality
        setupSearch();

        // Setup FAB click listener
        if (fabAddItem != null) {
            fabAddItem.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), AddEditInventoryActivity.class);
                intent.putExtra(AddEditInventoryActivity.EXTRA_IS_EDIT_MODE, false);
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
    }

    private void loadInventoryData() {
        inventoryList = new ArrayList<>();

        // Get all ration items from database
        rationItemsList = dbHelper.getAllRationItems();

        // Convert RationItem to Inventory model
        for (DBHelper.RationItem item : rationItemsList) {
            int quantity = (int) item.defaultQty;
            inventoryList.add(new Inventory(item.name, quantity));
        }

        // Initialize or update adapter
        if (adapter == null) {
            adapter = new InventoryAdapter(requireContext(), inventoryList, rationItemsList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(inventoryList, rationItemsList);
        }
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void refreshInventory() {
        if (adapter != null) {
            loadInventoryData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInventory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper = null;
    }
}