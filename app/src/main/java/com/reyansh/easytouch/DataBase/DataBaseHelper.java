package com.reyansh.easytouch.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.reyansh.easytouch.Models.EasyAppsModel;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.Constants;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "EasyTouch.db";
    public static final int DATABASE_VERSION = 1;


    private static DataBaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static final String _ID = "_id";
    public static final String EASY_APPS_TABLE = "easyAppsTable";

    public static final String EASY_APPS_NAME = "appName";
    public static final String EASY_APPS_ICON_ID = "appIcon";
    public static final String EASY_APPS_ORDER_WEIGHT = "appOrderWeight";
    public static final String EASY_APPS_PACKAGE_NAME = "appPackageName";


    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataBaseHelper getDatabaseHelper(Context context) {
        if (mDatabaseHelper == null)
            mDatabaseHelper = new DataBaseHelper(context.getApplicationContext());
        return mDatabaseHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] easyAppsTableCols =
                {EASY_APPS_ICON_ID,
                        EASY_APPS_ORDER_WEIGHT,
                        EASY_APPS_PACKAGE_NAME,
                        EASY_APPS_NAME};

        String[] easyAppsTableColTypes = {"INTEGER", "INTEGER", "TEXT", "TEXT"};

        String createEqualizerTable = buildCreateStatement(EASY_APPS_TABLE, easyAppsTableCols, easyAppsTableColTypes);
        db.execSQL(createEqualizerTable);
    }


    private String buildCreateStatement(String tableName, String[] columnNames, String[] columnTypes) {
        String createStatement = "";
        if (columnNames.length == columnTypes.length) {
            createStatement += "CREATE TABLE IF NOT EXISTS " + tableName + "("
                    +
                    _ID + " INTEGER PRIMARY KEY, ";

            for (int i = 0; i < columnNames.length; i++) {

                if (i == columnNames.length - 1) {
                    createStatement += columnNames[i]
                            + " "
                            + columnTypes[i]
                            + ")";
                } else {
                    createStatement += columnNames[i]
                            + " "
                            + columnTypes[i]
                            + ", ";
                }
            }
        }
        return createStatement;
    }


    public synchronized SQLiteDatabase getDatabase() {
        if (mDatabase == null)
            mDatabase = getWritableDatabase();
        return mDatabase;
    }


    /**
     * Adds a new EQ preset to the table.
     */
    public void addEasyApps(int easyAppIconId, int orderWeight, String packageName, String easAppName) {
        ContentValues values = new ContentValues();
        values.put(EASY_APPS_ICON_ID, easyAppIconId);
        values.put(EASY_APPS_ORDER_WEIGHT, orderWeight);
        values.put(EASY_APPS_PACKAGE_NAME, packageName);
        values.put(EASY_APPS_NAME, easAppName);
        getDatabase().insert(EASY_APPS_TABLE, null, values);
    }


    public ArrayList<EasyAppsModel> getEasyApps() {
        String query = "SELECT * FROM " + EASY_APPS_TABLE + " ORDER BY " + EASY_APPS_ORDER_WEIGHT + " ASC" + " LIMIT 9";
        Cursor cursor = getDatabase().rawQuery(query, null);
        ArrayList<EasyAppsModel> easyAppsModels = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {

                EasyAppsModel easyAppsModel = new EasyAppsModel();
                easyAppsModel.mAppIconId = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ICON_ID));
                easyAppsModel.mAppName = cursor.getString(cursor.getColumnIndex(EASY_APPS_NAME));
                easyAppsModel.mPackageName = cursor.getString(cursor.getColumnIndex(EASY_APPS_PACKAGE_NAME));
                easyAppsModel.mOrderWeight = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ORDER_WEIGHT));

                easyAppsModels.add(easyAppsModel);
            } while (cursor.moveToNext());
        }
        return easyAppsModels;
    }


    public ArrayList<EasyAppsModel> getEasyDeviceApps() {
        String query = "SELECT * FROM " + EASY_APPS_TABLE + " ORDER BY " + EASY_APPS_ORDER_WEIGHT + " ASC" + " LIMIT 9,8";
        Cursor cursor = getDatabase().rawQuery(query, null);
        ArrayList<EasyAppsModel> easyAppsModels = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                EasyAppsModel easyAppsModel = new EasyAppsModel();
                easyAppsModel.mAppIconId = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ICON_ID));
                easyAppsModel.mAppName = cursor.getString(cursor.getColumnIndex(EASY_APPS_NAME));
                easyAppsModel.mPackageName = cursor.getString(cursor.getColumnIndex(EASY_APPS_PACKAGE_NAME));
                easyAppsModel.mOrderWeight = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ORDER_WEIGHT));

                easyAppsModels.add(easyAppsModel);
            } while (cursor.moveToNext());
        }

        EasyAppsModel easyAppsModel = new EasyAppsModel();
        easyAppsModel.mAppIconId = R.drawable.ic_arrow_back;
        easyAppsModel.mPackageName = "";
        easyAppsModel.mAppName = "FAvBack";

        easyAppsModels.add(4, easyAppsModel);

        return easyAppsModels;
    }


    public Cursor getAllEasyApps() {
        String query = "SELECT * FROM " + EASY_APPS_TABLE;
        return getDatabase().rawQuery(query, null);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public ArrayList<EasyAppsModel> getInstalledApps() {
        String query = "SELECT * FROM " + EASY_APPS_TABLE + " WHERE " + EASY_APPS_PACKAGE_NAME + "!=" + "'" + "" + "'" + " ORDER BY " + EASY_APPS_ORDER_WEIGHT + " ASC" + " LIMIT 8";
        Cursor cursor = getDatabase().rawQuery(query, null);
        ArrayList<EasyAppsModel> easyAppsModels = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                EasyAppsModel easyAppsModel = new EasyAppsModel();
                easyAppsModel.mAppIconId = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ICON_ID));
                easyAppsModel.mAppName = cursor.getString(cursor.getColumnIndex(EASY_APPS_NAME));
                easyAppsModel.mPackageName = cursor.getString(cursor.getColumnIndex(EASY_APPS_PACKAGE_NAME));
                easyAppsModel.mOrderWeight = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ORDER_WEIGHT));

                easyAppsModels.add(easyAppsModel);
            } while (cursor.moveToNext());
        }

        EasyAppsModel easyAppsModel = new EasyAppsModel();
        easyAppsModel.mAppIconId = R.drawable.ic_arrow_back;
        easyAppsModel.mPackageName = "";
        easyAppsModel.mAppName = "FAvBack";

        easyAppsModels.add(4, easyAppsModel);
        return easyAppsModels;
    }


    public ArrayList<EasyAppsModel> getAllInstalledApps() {
        String query = "SELECT * FROM " + EASY_APPS_TABLE + " WHERE " + EASY_APPS_PACKAGE_NAME + "!=" + "'" + "" + "'" + " ORDER BY " + EASY_APPS_ORDER_WEIGHT + " ASC";
        Cursor cursor = getDatabase().rawQuery(query, null);
        ArrayList<EasyAppsModel> easyAppsModels = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                EasyAppsModel easyAppsModel = new EasyAppsModel();
                easyAppsModel.mAppIconId = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ICON_ID));
                easyAppsModel.mAppName = cursor.getString(cursor.getColumnIndex(EASY_APPS_NAME));
                easyAppsModel.mPackageName = cursor.getString(cursor.getColumnIndex(EASY_APPS_PACKAGE_NAME));
                easyAppsModel.mOrderWeight = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ORDER_WEIGHT));
                Log.d(Constants.TAG, "NAME   " + easyAppsModel.mAppName + "PACKAGE " + easyAppsModel.mPackageName);
                easyAppsModels.add(easyAppsModel);
            } while (cursor.moveToNext());
        }
        return easyAppsModels;
    }


    public ArrayList<EasyAppsModel> getCustomApps() {
        String query = "SELECT * FROM " + EASY_APPS_TABLE + " WHERE " + EASY_APPS_PACKAGE_NAME + "=" + "'" + "" + "'" + " ORDER BY " + EASY_APPS_ORDER_WEIGHT + " ASC";
        Cursor cursor = getDatabase().rawQuery(query, null);
        ArrayList<EasyAppsModel> easyAppsModels = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                EasyAppsModel easyAppsModel = new EasyAppsModel();
                easyAppsModel.mAppIconId = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ICON_ID));
                easyAppsModel.mAppName = cursor.getString(cursor.getColumnIndex(EASY_APPS_NAME));
                easyAppsModel.mPackageName = cursor.getString(cursor.getColumnIndex(EASY_APPS_PACKAGE_NAME));
                easyAppsModel.mOrderWeight = cursor.getInt(cursor.getColumnIndex(EASY_APPS_ORDER_WEIGHT));

                if (!easyAppsModel.mAppName.equalsIgnoreCase("Favorites") && !easyAppsModel.mAppName.equalsIgnoreCase("Device")) {
                    easyAppsModels.add(easyAppsModel);
                }
            } while (cursor.moveToNext());
        }
        return easyAppsModels;
    }

    public void swapOrders(String appName1, String appName2, int firstOrderWeight, int secondtOrderWeight) {
        ContentValues values = new ContentValues();
        values.put(EASY_APPS_ORDER_WEIGHT, secondtOrderWeight);
        getDatabase().update(EASY_APPS_TABLE, values, EASY_APPS_NAME + "= " + "'" + appName1 + "'", null);

        ContentValues values1 = new ContentValues();
        values1.put(EASY_APPS_ORDER_WEIGHT, firstOrderWeight);

        getDatabase().update(EASY_APPS_TABLE, values1, EASY_APPS_NAME + "= " + "'" + appName2 + "'", null);
    }
}