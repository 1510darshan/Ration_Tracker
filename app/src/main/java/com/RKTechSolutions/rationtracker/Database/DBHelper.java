// File: DBHelper.java
package com.RKTechSolutions.rationtracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RationStoreDB";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_OWNERS = "owners";
    private static final String TABLE_CUSTOMERS = "customers";
    private static final String TABLE_FAMILY_MEMBERS = "family_members";
    private static final String TABLE_RATION_ITEMS = "ration_items";
    private static final String TABLE_MONTHLY_RECORDS = "monthly_ration_records";
    private static final String TABLE_TRANSACTIONS = "ration_transactions";

    // Common Columns
    private static final String COL_ID = "id";

    // === OWNERS TABLE ===
    private static final String COL_OWNER_NAME = "owner_name";
    private static final String COL_OWNER_PHONE = "owner_phone";
    private static final String COL_STORE_NUMBER = "store_number"; // Changed from store_name
    private static final String COL_CREATED_AT = "created_at";

    // === CUSTOMERS TABLE ===
    private static final String COL_CARD_NUMBER = "ration_card_number"; // 12-digit
    private static final String COL_CUSTOMER_NAME = "customer_name";
    private static final String COL_PHONE = "phone";
    private static final String COL_ADDRESS = "address";
    private static final String COL_IS_ACTIVE = "is_active";

    // === FAMILY MEMBERS ===
    private static final String COL_MEMBER_NAME = "member_name";
    private static final String COL_RELATION = "relation";
    private static final String COL_AGE = "age";

    // === RATION ITEMS ===
    private static final String COL_ITEM_NAME = "item_name";
    private static final String COL_UNIT = "unit";
    private static final String COL_DEFAULT_QTY = "default_qty";
    private static final String COL_PRICE = "price_per_unit";

    // === MONTHLY RECORDS ===
    private static final String COL_YEAR = "year";
    private static final String COL_MONTH = "month";
    private static final String COL_ENTITLED = "is_entitled";
    private static final String COL_ENTITLED_DATE = "entitled_date";
    private static final String COL_NOTES = "notes";

    // === TRANSACTIONS ===
    private static final String COL_ITEM_ID = "item_id";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_UNIT_PRICE = "unit_price";
    private static final String COL_TAKEN_DATE = "taken_date";
    private static final String COL_ISSUED_BY = "issued_by";

    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_OWNERS);
        db.execSQL(CREATE_TABLE_CUSTOMERS);
        db.execSQL(CREATE_TABLE_FAMILY_MEMBERS);
        db.execSQL(CREATE_TABLE_RATION_ITEMS);
        db.execSQL(CREATE_TABLE_MONTHLY_RECORDS);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);

        insertDefaultRationItems(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHLY_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATION_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAMILY_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNERS);
        onCreate(db);
    }

    // ===================================================================
    // TABLE CREATION QUERIES
    // ===================================================================

    private static final String CREATE_TABLE_OWNERS = "CREATE TABLE " + TABLE_OWNERS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_OWNER_NAME + " TEXT NOT NULL, " +
            COL_OWNER_PHONE + " TEXT NOT NULL, " +
            COL_STORE_NUMBER + " TEXT NOT NULL, " +  // Store Number
            COL_CREATED_AT + " INTEGER DEFAULT 0" +
            ");";

    private static final String CREATE_TABLE_CUSTOMERS = "CREATE TABLE " + TABLE_CUSTOMERS + " (" +
            COL_CARD_NUMBER + " TEXT PRIMARY KEY, " +
            COL_CUSTOMER_NAME + " TEXT NOT NULL, " +
            COL_PHONE + " TEXT NOT NULL, " +
            COL_ADDRESS + " TEXT, " +
            COL_IS_ACTIVE + " INTEGER DEFAULT 1" +
            ");";

    private static final String CREATE_TABLE_FAMILY_MEMBERS = "CREATE TABLE " + TABLE_FAMILY_MEMBERS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_CARD_NUMBER + " TEXT NOT NULL, " +
            COL_MEMBER_NAME + " TEXT NOT NULL, " +
            COL_RELATION + " TEXT, " +
            COL_AGE + " INTEGER, " +
            "FOREIGN KEY(" + COL_CARD_NUMBER + ") REFERENCES " + TABLE_CUSTOMERS + "(" + COL_CARD_NUMBER + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TABLE_RATION_ITEMS = "CREATE TABLE " + TABLE_RATION_ITEMS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_ITEM_NAME + " TEXT NOT NULL, " +
            COL_UNIT + " TEXT NOT NULL, " +
            COL_DEFAULT_QTY + " REAL DEFAULT 0, " +
            COL_PRICE + " REAL DEFAULT 0" +
            ");";

    private static final String CREATE_TABLE_MONTHLY_RECORDS = "CREATE TABLE " + TABLE_MONTHLY_RECORDS + " (" +
            COL_CARD_NUMBER + " TEXT NOT NULL, " +
            COL_YEAR + " INTEGER NOT NULL, " +
            COL_MONTH + " INTEGER NOT NULL, " +
            COL_ENTITLED + " INTEGER DEFAULT 1, " +
            COL_ENTITLED_DATE + " INTEGER, " +
            COL_NOTES + " TEXT, " +
            "PRIMARY KEY(" + COL_CARD_NUMBER + ", " + COL_YEAR + ", " + COL_MONTH + "), " +
            "FOREIGN KEY(" + COL_CARD_NUMBER + ") REFERENCES " + TABLE_CUSTOMERS + "(" + COL_CARD_NUMBER + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_CARD_NUMBER + " TEXT NOT NULL, " +
            COL_ITEM_ID + " INTEGER NOT NULL, " +
            COL_QUANTITY + " REAL NOT NULL, " +
            COL_UNIT_PRICE + " REAL NOT NULL, " +
            COL_TAKEN_DATE + " INTEGER NOT NULL, " +
            COL_ISSUED_BY + " TEXT, " +
            COL_YEAR + " INTEGER NOT NULL, " +
            COL_MONTH + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COL_CARD_NUMBER + ") REFERENCES " + TABLE_CUSTOMERS + "(" + COL_CARD_NUMBER + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + COL_ITEM_ID + ") REFERENCES " + TABLE_RATION_ITEMS + "(" + COL_ID + ")" +
            ");";

    // ===================================================================
    // DEFAULT RATION ITEMS
    // ===================================================================

    private void insertDefaultRationItems(SQLiteDatabase db) {
        String[] items = {"Rice", "Wheat", "Sugar", "Kerosene"};
        String[] units = {"kg", "kg", "kg", "liter"};
        double[] qty = {5.0, 3.0, 1.0, 2.0};
        double[] price = {35.0, 25.0, 40.0, 30.0};

        for (int i = 0; i < items.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put(COL_ITEM_NAME, items[i]);
            cv.put(COL_UNIT, units[i]);
            cv.put(COL_DEFAULT_QTY, qty[i]);
            cv.put(COL_PRICE, price[i]);
            db.insert(TABLE_RATION_ITEMS, null, cv);
        }
    }

    // ===================================================================
    // OWNER: Register Once
    // ===================================================================

    public boolean registerOwner(String ownerName, String ownerPhone, String storeNumber) {
        if (getOwner() != null) return false; // Already registered

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_OWNER_NAME, ownerName);
        cv.put(COL_OWNER_PHONE, ownerPhone);
        cv.put(COL_STORE_NUMBER, storeNumber);
        cv.put(COL_CREATED_AT, System.currentTimeMillis());

        long result = db.insert(TABLE_OWNERS, null, cv);
        db.close();
        return result != -1;
    }

    public Owner getOwner() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OWNERS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Owner owner = new Owner(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OWNER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OWNER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STORE_NUMBER))
            );
            cursor.close();
            return owner;
        }
        return null;
    }

    // ===================================================================
    // CUSTOMER: Add Ration Card Holder
    // ===================================================================

    public boolean addCustomer(String cardNumber, String customerName, String phone, String address) {
        if (cardNumber.length() != 12 || !cardNumber.matches("\\d{12}")) return false;
        if (getCustomer(cardNumber) != null) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CARD_NUMBER, cardNumber);
        cv.put(COL_CUSTOMER_NAME, customerName);
        cv.put(COL_PHONE, phone);
        cv.put(COL_ADDRESS, address);
        cv.put(COL_IS_ACTIVE, 1);

        long result = db.insert(TABLE_CUSTOMERS, null, cv);
        db.close();
        return result != -1;
    }

    public Customer getCustomer(String cardNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOMERS, null, COL_CARD_NUMBER + "=?", new String[]{cardNumber}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Customer customer = new Customer(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CARD_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS))
            );
            cursor.close();
            return customer;
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOMERS, null, COL_IS_ACTIVE + "=1", null, null, null, COL_CUSTOMER_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Customer(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CARD_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS))
                ));
            }
            cursor.close();
        }
        return list;
    }

    // ===================================================================
    // MONTHLY RATION RECORD
    // ===================================================================

    public boolean createMonthlyRecord(String cardNumber, int year, int month) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CARD_NUMBER, cardNumber);
        cv.put(COL_YEAR, year);
        cv.put(COL_MONTH, month);
        cv.put(COL_ENTITLED, 1);
        cv.put(COL_ENTITLED_DATE, System.currentTimeMillis());

        long result = db.insertWithOnConflict(TABLE_MONTHLY_RECORDS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;
    }

    public boolean hasTakenRation(String cardNumber, int year, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COL_CARD_NUMBER + "=? AND " + COL_YEAR + "=? AND " + COL_MONTH + "=?",
                new String[]{cardNumber, String.valueOf(year), String.valueOf(month)}
        );
        boolean has = false;
        if (cursor != null && cursor.moveToFirst()) {
            has = cursor.getInt(0) > 0;
            cursor.close();
        }
        return has;
    }

    // ===================================================================
    // RATION TRANSACTION
    // ===================================================================

    public boolean addRationTransaction(String cardNumber, long itemId, double quantity, double unitPrice, String issuedBy) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CARD_NUMBER, cardNumber);
        cv.put(COL_ITEM_ID, itemId);
        cv.put(COL_QUANTITY, quantity);
        cv.put(COL_UNIT_PRICE, unitPrice);
        cv.put(COL_TAKEN_DATE, System.currentTimeMillis());
        cv.put(COL_ISSUED_BY, issuedBy);
        cv.put(COL_YEAR, year);
        cv.put(COL_MONTH, month);

        long result = db.insert(TABLE_TRANSACTIONS, null, cv);
        db.close();
        return result != -1;
    }

    public List<RationTransaction> getMonthlyTransactions(String cardNumber, int year, int month) {
        List<RationTransaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT t.*, i." + COL_ITEM_NAME + ", i." + COL_UNIT +
                " FROM " + TABLE_TRANSACTIONS + " t" +
                " JOIN " + TABLE_RATION_ITEMS + " i ON t." + COL_ITEM_ID + " = i." + COL_ID +
                " WHERE t." + COL_CARD_NUMBER + "=? AND t." + COL_YEAR + "=? AND t." + COL_MONTH + "=?" +
                " ORDER BY t." + COL_TAKEN_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{cardNumber, String.valueOf(year), String.valueOf(month)});
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        if (cursor != null) {
            while (cursor.moveToNext()) {
                RationTransaction tx = new RationTransaction();
                tx.itemName = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
                tx.quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY));
                tx.unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT));
                tx.takenDate = sdf.format(new java.util.Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TAKEN_DATE))));
                tx.unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_UNIT_PRICE));
                list.add(tx);
            }
            cursor.close();
        }
        return list;
    }

    // ===================================================================
    // RATION ITEMS
    // ===================================================================

    public List<RationItem> getAllRationItems() {
        List<RationItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RATION_ITEMS, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                RationItem item = new RationItem();
                item.id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                item.name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
                item.unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT));
                item.defaultQty = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DEFAULT_QTY));
                item.price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE));
                list.add(item);
            }
            cursor.close();
        }
        return list;
    }

    // ===================================================================
    // POJO CLASSES
    // ===================================================================

    public static class Owner {
        public long id;
        public String ownerName, ownerPhone, storeNumber;
        public Owner(long id, String ownerName, String ownerPhone, String storeNumber) {
            this.id = id;
            this.ownerName = ownerName;
            this.ownerPhone = ownerPhone;
            this.storeNumber = storeNumber;
        }
    }

    public static class Customer {
        public String cardNumber, customerName, phone, address;
        public Customer(String cardNumber, String customerName, String phone, String address) {
            this.cardNumber = cardNumber;
            this.customerName = customerName;
            this.phone = phone;
            this.address = address;
        }
    }

    public static class RationItem {
        public long id;
        public String name, unit;
        public double defaultQty, price;
    }

    public static class RationTransaction {
        public String itemName;
        public double quantity, unitPrice;
        public String unit, takenDate;
    }


    // Add these methods to your existing DBHelper.java class

// ===================================================================
// ENHANCED CUSTOMER SEARCH & RETRIEVAL METHODS
// ===================================================================

    /**
     * Search customers by name or card number
     * @param query Search query (name or card number)
     * @return List of matching customers
     */
    public List<Customer> searchCustomers(String query) {
        List<Customer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "(" + COL_CUSTOMER_NAME + " LIKE ? OR " +
                COL_CARD_NUMBER + " LIKE ?) AND " +
                COL_IS_ACTIVE + "=1";
        String searchPattern = "%" + query + "%";
        String[] selectionArgs = {searchPattern, searchPattern};

        Cursor cursor = db.query(TABLE_CUSTOMERS, null, selection, selectionArgs,
                null, null, COL_CUSTOMER_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Customer(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CARD_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS))
                ));
            }
            cursor.close();
        }
        return list;
    }

    /**
     * Get customer name by card number
     * @param cardNumber 12-digit ration card number
     * @return Customer name or null if not found
     */
    public String getCustomerName(String cardNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;

        Cursor cursor = db.query(TABLE_CUSTOMERS,
                new String[]{COL_CUSTOMER_NAME},
                COL_CARD_NUMBER + "=?",
                new String[]{cardNumber},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME));
            cursor.close();
        }
        return name;
    }

    /**
     * Get card number by customer name (returns first match)
     * @param customerName Customer name
     * @return Card number or null if not found
     */
    public String getCardNumberByName(String customerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String cardNumber = null;

        Cursor cursor = db.query(TABLE_CUSTOMERS,
                new String[]{COL_CARD_NUMBER},
                COL_CUSTOMER_NAME + "=? AND " + COL_IS_ACTIVE + "=1",
                new String[]{customerName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            cardNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_CARD_NUMBER));
            cursor.close();
        }
        return cardNumber;
    }

    /**
     * Get customer basic info (name and card number) for display
     * @param cardNumber 12-digit ration card number
     * @return CustomerBasicInfo object or null
     */
    public CustomerBasicInfo getCustomerBasicInfo(String cardNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        CustomerBasicInfo info = null;

        Cursor cursor = db.query(TABLE_CUSTOMERS,
                new String[]{COL_CUSTOMER_NAME, COL_CARD_NUMBER},
                COL_CARD_NUMBER + "=?",
                new String[]{cardNumber},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            info = new CustomerBasicInfo(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CARD_NUMBER))
            );
            cursor.close();
        }
        return info;
    }

    /**
     * Check if customer exists by card number
     * @param cardNumber 12-digit ration card number
     * @return true if customer exists, false otherwise
     */
    public boolean isCustomerExists(String cardNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOMERS,
                new String[]{COL_CARD_NUMBER},
                COL_CARD_NUMBER + "=?",
                new String[]{cardNumber},
                null, null, null);

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    /**
     * Get total number of active customers
     * @return Count of active customers
     */
    public int getCustomerCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_CUSTOMERS + " WHERE " + COL_IS_ACTIVE + "=1",
                null
        );

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /**
     * Get customers who have taken ration this month
     * @param year Year
     * @param month Month (1-12)
     * @return List of customers who took ration
     */
    public List<Customer> getCustomersWhoTookRation(int year, int month) {
        List<Customer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT c.* FROM " + TABLE_CUSTOMERS + " c " +
                "INNER JOIN " + TABLE_TRANSACTIONS + " t " +
                "ON c." + COL_CARD_NUMBER + " = t." + COL_CARD_NUMBER + " " +
                "WHERE t." + COL_YEAR + "=? AND t." + COL_MONTH + "=? " +
                "AND c." + COL_IS_ACTIVE + "=1 " +
                "ORDER BY c." + COL_CUSTOMER_NAME + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(year), String.valueOf(month)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Customer(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CARD_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS))
                ));
            }
            cursor.close();
        }
        return list;
    }

    /**
     * Get customers who haven't taken ration this month
     * @param year Year
     * @param month Month (1-12)
     * @return List of customers who didn't take ration
     */
    public List<Customer> getCustomersWhoDidntTakeRation(int year, int month) {
        List<Customer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.* FROM " + TABLE_CUSTOMERS + " c " +
                "WHERE c." + COL_IS_ACTIVE + "=1 " +
                "AND c." + COL_CARD_NUMBER + " NOT IN (" +
                "SELECT DISTINCT " + COL_CARD_NUMBER + " FROM " + TABLE_TRANSACTIONS + " " +
                "WHERE " + COL_YEAR + "=? AND " + COL_MONTH + "=?" +
                ") ORDER BY c." + COL_CUSTOMER_NAME + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(year), String.valueOf(month)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Customer(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CARD_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS))
                ));
            }
            cursor.close();
        }
        return list;
    }

    /**
     * Update customer details
     * @param cardNumber Card number (cannot be changed)
     * @param customerName New customer name
     * @param phone New phone number
     * @param address New address
     * @return true if updated successfully
     */
    public boolean updateCustomer(String cardNumber, String customerName, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CUSTOMER_NAME, customerName);
        cv.put(COL_PHONE, phone);
        cv.put(COL_ADDRESS, address);

        int rows = db.update(TABLE_CUSTOMERS, cv, COL_CARD_NUMBER + "=?", new String[]{cardNumber});
        db.close();
        return rows > 0;
    }

    /**
     * Deactivate customer (soft delete)
     * @param cardNumber Card number
     * @return true if deactivated successfully
     */
    public boolean deactivateCustomer(String cardNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_ACTIVE, 0);

        int rows = db.update(TABLE_CUSTOMERS, cv, COL_CARD_NUMBER + "=?", new String[]{cardNumber});
        db.close();
        return rows > 0;
    }

// ===================================================================
// ADDITIONAL POJO CLASS
// ===================================================================

    /**
     * Lightweight customer info for quick lookups
     */
    public static class CustomerBasicInfo {
        public String name;
        public String cardNumber;

        public CustomerBasicInfo(String name, String cardNumber) {
            this.name = name;
            this.cardNumber = cardNumber;
        }
    }


    /**
     * Add a new ration item to inventory
     * @param itemName Name of the item
     * @param unit Unit of measurement (kg, liter, etc.)
     * @param defaultQty Default quantity
     * @param price Price per unit
     * @return true if added successfully
     */
    public boolean addRationItem(String itemName, String unit, double defaultQty, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ITEM_NAME, itemName);
        cv.put(COL_UNIT, unit);
        cv.put(COL_DEFAULT_QTY, defaultQty);
        cv.put(COL_PRICE, price);

        long result = db.insert(TABLE_RATION_ITEMS, null, cv);
        db.close();
        return result != -1;
    }




    /**
     * Update an existing ration item
     * @param itemId Item ID
     * @param itemName Name of the item
     * @param unit Unit of measurement
     * @param defaultQty Default quantity
     * @param price Price per unit
     * @return true if updated successfully
     */
    public boolean updateRationItem(long itemId, String itemName, String unit, double defaultQty, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ITEM_NAME, itemName);
        cv.put(COL_UNIT, unit);
        cv.put(COL_DEFAULT_QTY, defaultQty);
        cv.put(COL_PRICE, price);

        int rows = db.update(TABLE_RATION_ITEMS, cv, COL_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
        return rows > 0;
    }




    /**
     * Delete a ration item
     * @param itemId Item ID
     * @return true if deleted successfully
     */
    public boolean deleteRationItem(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_RATION_ITEMS, COL_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
        return rows > 0;
    }




    /**
     * Get a single ration item by ID
     * @param itemId Item ID
     * @return RationItem object or null
     */
    public RationItem getRationItem(long itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RATION_ITEMS, null, COL_ID + "=?",
                new String[]{String.valueOf(itemId)}, null, null, null);

        RationItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new RationItem();
            item.id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
            item.name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
            item.unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT));
            item.defaultQty = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DEFAULT_QTY));
            item.price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE));
            cursor.close();
        }
        return item;
    }



    /**
     * Check if item name already exists (for validation)
     * @param itemName Item name to check
     * @return true if exists
     */
    public boolean isItemNameExists(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RATION_ITEMS,
                new String[]{COL_ID},
                COL_ITEM_NAME + "=?",
                new String[]{itemName},
                null, null, null);

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }




    /**
     * Get total number of inventory items
     * @return Count of items
     */
    public int getInventoryItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RATION_ITEMS, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

}