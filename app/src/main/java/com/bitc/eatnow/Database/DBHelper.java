package com.bitc.eatnow.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bitc.eatnow.Model.Order;

import java.util.ArrayList;
import java.util.List;


/* SQLiteOpenHelper 상속 클래스 - 데이터베이스 프로그램을 좀 더 구조적으로 작성하기 위함
 * onCreate(): 앱이 설치된 후 SQLiteOpenHelper 클래스가 이용되는 순간 한 번 호출
 * onUpgrade(): 생성자에 지정한 DB 버전 정보가 변경될 때마다 호출
*/
public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_NAME = "EatNowDB.db";
    private static final int DB_VER = 1;
    private static final String TABLE_NAME = "OrderDetail";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_PRODUCTNAME = "ProductName";
    private static final String COLUMN_PRODUCTID = "ProductId";
    private static final String COLUMN_QUANTITY = "Quantity";
    private static final String COLUMN_PRICE = "Price";
    private static final String COLUMN_DISCOUNT = "Discount";

    public DBHelper(@Nullable Context context)  {
        super(context, DB_NAME, null, DB_VER);
        this.context = context;
    }

    // 앱이 설치된 후 SQLiteOpenHelper 클래스가 이용되는 순간 한 번 호출
    @Override
    public void onCreate (SQLiteDatabase db){

        Log.d("myLog", " DBHelper >> onCreate() ");

        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCTID + " TEXT, " +
                COLUMN_PRODUCTNAME + " TEXT, " +
                COLUMN_QUANTITY + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_DISCOUNT + " TEXT);";
        db.execSQL(query);
    }
    // 생성자에 지정한 DB 버전 정보가 변경될 때마다 호출
    @Override
    public void onUpgrade (SQLiteDatabase db,int i, int i1){

        Log.d("myLog", " DBHelper >> onUpgrade() ");

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    @SuppressLint("Range")
    public List<Order> getCarts () {

        Log.d("myLog", " DBHelper >> getCarts() ");

        SQLiteDatabase db = this.getReadableDatabase();
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String query = "SELECT * FROM " + TABLE_NAME;
        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount"};

//        queryBuilder.setTables(TABLE_NAME);
//        Cursor cursor = queryBuilder.query(db, sqlSelect, null, null, null, null, null);

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }

        final List<Order> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(new Order(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTNAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DISCOUNT))
                ));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public void addToCart (Order order){

        Log.d("myLog", " DBHelper >> addToCart() ");

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCTID, order.getProductId());
        cv.put(COLUMN_PRODUCTNAME, order.getProductName());
        cv.put(COLUMN_QUANTITY, order.getQuantity());
        cv.put(COLUMN_PRICE, order.getPrice());
        cv.put(COLUMN_DISCOUNT, order.getDiscount());
//        String query = String.format("INSERT INTO " + TABLE_NAME + " VALUES('%s', '%s', '%s', '%s', '%s');",
//                order.getProductId(),
//                order.getProductName(),
//                order.getQuantity(),
//                order.getPrice(),
//                order.getDiscount());

//        db.execSQL(query);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1) {
            Toast.makeText(context, "실패했습니다", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "장바구니에 저장되었습니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanCart () {

        Log.d("myLog", " DBHelper >> cleanCart() ");

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM " + TABLE_NAME);
        db.execSQL(query);
    }

}
