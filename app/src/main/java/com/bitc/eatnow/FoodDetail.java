package com.bitc.eatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitc.eatnow.Database.DBHelper;
import com.bitc.eatnow.Model.Food;
import com.bitc.eatnow.Model.Order;
import com.bitc.eatnow.databinding.ActivityFoodDetailBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    private ActivityFoodDetailBinding binding;
    int totalQuantity = 1;
    int totalPrice = 1000;

    TextView food_name, food_price, food_description, quantity;
    ImageView food_image, add_item, remove_item;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton cartBtn;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;

    // AddToCart를 위해 전역변수로 설정
    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init FirebaseDatabase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        // Init View
        cartBtn = binding.cartBtn;

        // cartBtn 클릭 시 cart에 추가
        cartBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new DBHelper(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        String.valueOf(totalQuantity),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));

                Toast.makeText(FoodDetail.this, "장바구니에 추가되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        food_description = binding.foodDescription;
        food_name = binding.foodName;
        food_price = binding.foodPrice;
        food_image = binding.imgFood;
        quantity = binding.quantity;
        add_item = binding.addItem;
        remove_item = binding.removeItem;

        // add_item / remove_item 클릭시 quantity 증가 / 감소
        add_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(totalQuantity < 10) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
                if(totalQuantity == 10) {
                    Toast.makeText(FoodDetail.this, "최대 10개까지 가능합니다!", Toast.LENGTH_SHORT).show();
                }
                Log.d("myLog", " FoodDetail >> add_item click!, totalQuantity is "+totalQuantity);
            }
        });

        remove_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(totalQuantity > 0) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
                Log.d("myLog", " FoodDetail >> remove_item click!, totalQuantity is "+totalQuantity);
            }
        });

        // themes.xml에서 설정해둔 CollapsingToolbar 코스튬 설정
        collapsingToolbarLayout = binding.collapsing;
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        // Get FoodId from Intent
        // FoodList.java에서 건너온 intent 받기
        if(getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodId");
        }
        if(!foodId.isEmpty() && foodId != null) {   // foodId 변수에 뭐가 들어있다

            // FirebaseDatabase에서 Foods의 데이터 중 해당 FoodId(key)에 따른 데이터만 get
            getDetailFood(foodId);
        }

        // set totalPrice
        // trim() : 빈칸 입력시 삭제 함수
        totalPrice = Integer.parseInt(food_price.getText().toString().replace(",", "")) * totalQuantity;
    }

    // FirebaseDatabase에서 Foods의 데이터 중 해당 FoodId(key)에 따른 데이터만 get
    private void getDetailFood(String foodId) {

        foods.child(foodId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);

                // <ImageView/>, <TextView/>, <CollapsingToolbarLayout/>에 text 설정
                Picasso.get().load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}