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

import com.RKTechSolutions.rationtracker.CustomerDetailsActivity;
import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.RKTechSolutions.rationtracker.R;
import com.RKTechSolutions.rationtracker.RecordRationActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<DBHelper.Customer> customerList;

    public CustomerAdapter(Context context, List<DBHelper.Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        DBHelper.Customer customer = customerList.get(position);

        // Set data
        holder.image.setImageResource(R.drawable.listuser);
        holder.userName.setText(customer.customerName);
        holder.rationNumber.setText(customer.cardNumber);

        // Set click listener for the entire card (except the button)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerDetailsActivity.class);
            intent.putExtra(CustomerDetailsActivity.EXTRA_CARD_NUMBER, customer.cardNumber);
            context.startActivity(intent);
        });

        // Set click listener for the record button
        holder.btnRecordRation.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecordRationActivity.class);
            intent.putExtra(RecordRationActivity.EXTRA_CARD_NUMBER, customer.cardNumber);
            intent.putExtra(RecordRationActivity.EXTRA_CUSTOMER_NAME, customer.customerName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return customerList != null ? customerList.size() : 0;
    }

    /**
     * Update the list when searching or refreshing
     */
    public void updateList(List<DBHelper.Customer> newList) {
        this.customerList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder pattern for better performance
     */
    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, rationNumber;
        MaterialButton btnRecordRation;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.listImage);
            userName = itemView.findViewById(R.id.userName);
            rationNumber = itemView.findViewById(R.id.rationNumber);
            btnRecordRation = itemView.findViewById(R.id.btnRecordRation);
        }
    }
}