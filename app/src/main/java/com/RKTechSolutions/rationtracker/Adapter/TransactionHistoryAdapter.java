package com.RKTechSolutions.rationtracker.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.RKTechSolutions.rationtracker.R;

import java.util.List;
import java.util.Locale;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewHolder> {

    private List<DBHelper.RationTransaction> transactions;

    public TransactionHistoryAdapter(List<DBHelper.RationTransaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_history, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        DBHelper.RationTransaction transaction = transactions.get(position);

        // Set item name
        holder.tvItemName.setText(transaction.itemName);

        // Set quantity with unit
        holder.tvQuantity.setText(String.format(Locale.getDefault(),
                "%.2f %s", transaction.quantity, transaction.unit));

        // Set price per unit
        holder.tvUnitPrice.setText(String.format(Locale.getDefault(),
                "₹%.2f/%s", transaction.unitPrice, transaction.unit));

        // Set total amount
        double totalAmount = transaction.quantity * transaction.unitPrice;
        holder.tvTotalAmount.setText(String.format(Locale.getDefault(), "₹%.2f", totalAmount));

        // Set date
        holder.tvDate.setText(transaction.takenDate);

        // Set icon based on item name
        int iconResId = getItemIcon(transaction.itemName);
        holder.ivItemIcon.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<DBHelper.RationTransaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

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

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemIcon;
        TextView tvItemName, tvQuantity, tvUnitPrice, tvTotalAmount, tvDate;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemIcon = itemView.findViewById(R.id.ivItemIcon);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}