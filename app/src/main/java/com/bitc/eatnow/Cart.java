package com.bitc.eatnow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitc.eatnow.Adapter.CartAdapter;
import com.bitc.eatnow.Database.DBHelper;
import com.bitc.eatnow.Model.Order;
import com.bitc.eatnow.Model.Request;
import com.bitc.eatnow.databinding.ActivityCartBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    private ActivityCartBinding binding;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlaceHolder;

    List<Order> cart = new ArrayList<>();   // 이 변수에 장바구니에 추가한 음식이 담긴다

    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Init Views
        recyclerView = binding.listCart;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = binding.total;
        btnPlaceHolder = binding.btnPlaceOrder;

        // btnPlaceHolder 클릭시
        // show alert dialog to fill address
        btnPlaceHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // show alert dialog to fill address
                showAlertDialog();

            }
        });

        // 장바구니에 추가한 음식 목록(수량 * 가격) 가져오기
        loadListFood();
    }

    // show alert dialog to fill address
    private void showAlertDialog() {

        Log.d("myLog", " Cart >> showAlertDialog() 호출");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("주문 전 마지막 단계입니다!!");
        alertDialog.setMessage("주소를 입력하세요!!");

        // EditText 생성, 설정
        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);    // Add EditText to alert dialog
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        // "YES" 클릭시
        // submit the order information to firebase
        alertDialog.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Create new Request instance
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );

                // Submit to Firebase
                // Will use System.CurrentMilli to key, set value as request
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                // Delete cart
                new DBHelper(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "감사합니다. 주문이 정상적으로 접수되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // "NO" 클릭시
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    // 장바구니에 추가한 음식 목록(수량 * 가격) 가져오기
    private void loadListFood() {

        Log.d("myLog", " Cart >> loadListFood() 호출");

        cart = new DBHelper(this).getCarts();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        // Calculate total price
        int total = 0;
        for(Order order: cart) {
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

            Locale locale = new Locale("ko", "KR");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));
        }
    }

}