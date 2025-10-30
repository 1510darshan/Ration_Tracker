package com.RKTechSolutions.rationtracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class AddCustomerActivity extends AppCompatActivity {

    private TextInputEditText etCardNumber, etName, etPhone, etAddress;
    private MaterialButton btnRegister;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        dbHelper = DBHelper.getInstance(this);

        etCardNumber = findViewById(R.id.etRationCardNumber);
        etName = findViewById(R.id.etCustomerName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegisterCustomer);

        btnRegister.setOnClickListener(v -> registerCustomer());

        findViewById(R.id.tvCancel).setOnClickListener(v -> finish());
    }




    private void registerCustomer() {
        String card = etCardNumber.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(card) || card.length() != 12 || !card.matches("\\d{12}")) {
            etCardNumber.setError("Enter valid 12-digit card number");
            etCardNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter customer name");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            etPhone.setError("Enter valid phone number");
            etPhone.requestFocus();
            return;
        }

        boolean success = dbHelper.addCustomer(card, name, phone, address);
        if (success) {
            Toast.makeText(this, "Customer registered!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Card number already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}