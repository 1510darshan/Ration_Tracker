package com.RKTechSolutions.rationtracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RKTechSolutions.rationtracker.AddEditInventoryActivity;
import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.RKTechSolutions.rationtracker.Model.Inventory;
import com.RKTechSolutions.rationtracker.R;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<Inventory> inventoryList;
    private List<Inventory> inventoryListFull;
    private List<DBHelper.RationItem> rationItemsList;
    private Context context;

    public InventoryAdapter(Context context, List<Inventory> inventoryList, List<DBHelper.RationItem> rationItems) {
        this.context = context;
        this.inventoryList = inventoryList;
        this.inventoryListFull = new ArrayList<>(inventoryList);
        this.rationItemsList = rationItems;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_list_item, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Inventory item = inventoryList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemQuantity.setText(item.getQuantity() + " kg");

        // Set appropriate icon based on item name
        int iconResId = getItemIcon(item.getItemName());
        holder.itemImage.setImageResource(iconResId);

        // Click listener to open edit activity
        holder.itemView.setOnClickListener(v -> {
            // Find the corresponding RationItem to get the ID
            DBHelper.RationItem rationItem = findRationItem(item.getItemName());
            if (rationItem != null) {
                Intent intent = new Intent(context, AddEditInventoryActivity.class);
                intent.putExtra(AddEditInventoryActivity.EXTRA_ITEM_ID, rationItem.id);
                intent.putExtra(AddEditInventoryActivity.EXTRA_ITEM_NAME, rationItem.name);
                intent.putExtra(AddEditInventoryActivity.EXTRA_QUANTITY, rationItem.defaultQty);
                intent.putExtra(AddEditInventoryActivity.EXTRA_IS_EDIT_MODE, true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    // Find RationItem by name
    private DBHelper.RationItem findRationItem(String itemName) {
        for (DBHelper.RationItem item : rationItemsList) {
            if (item.name.equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Search/Filter method
    public void filter(String query) {
        inventoryList.clear();

        if (query.isEmpty()) {
            inventoryList.addAll(inventoryListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Inventory item : inventoryListFull) {
                if (item.getItemName().toLowerCase().contains(lowerCaseQuery)) {
                    inventoryList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Update inventory list
    public void updateList(List<Inventory> newList, List<DBHelper.RationItem> rationItems) {
        inventoryList.clear();
        inventoryListFull.clear();
        inventoryList.addAll(newList);
        inventoryListFull.addAll(newList);
        this.rationItemsList = rationItems;
        notifyDataSetChanged();
    }

    // Get icon based on item name
    private int getItemIcon(String itemName) {
        String lowerName = itemName.toLowerCase();

        if (lowerName.contains("wheat")) {
            return R.drawable.wheat;
        } else if (lowerName.contains("rice")) {
            return R.drawable.wheat;
        } else if (lowerName.contains("sugar")) {
            return R.drawable.wheat;
        } else if (lowerName.contains("kerosene") || lowerName.contains("oil")) {
            return R.drawable.wheat;
        } else {
            return R.drawable.wheat;
        }
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemQuantity;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.listImage);
            itemName = itemView.findViewById(R.id.userName);
            itemQuantity = itemView.findViewById(R.id.userPhone);
        }
    }
}