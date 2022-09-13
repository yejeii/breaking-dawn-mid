package com.bitc.eatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitc.eatnow.Interface.ItemClickListener;
import com.bitc.eatnow.Model.Food;
import com.bitc.eatnow.ViewHolder.FoodViewHolder;
import com.bitc.eatnow.databinding.ActivityFoodListBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    private ActivityFoodListBinding binding;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId = "";

    // 검색 기능(MaterialSearchBar)
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        // Set the adapter
        // activity_food_list.xml의 <RecyclerView/>에 어댑터 장착
        // Load Foods to Recylcer View using Firebase UI
        recyclerView = binding.recyclerFood;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Get CategoryId from Intent
        // Home.java에서 건너온 intent 받기
        if(getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null) {   // 뭐가 들어있다
            Log.d("myLog", " FoodList >> categoryId is " + categoryId);

            loadListFood(categoryId);
        }

        // Search
        materialSearchBar = binding.searchBar;
        materialSearchBar.setHint("음식을 입력하세요");
//        materialSearchBar.setSpeechMode(false); No need - already defined it at XML
        loadSuggest();  // Add name of food to suggest list(suggestList 초기 셋팅!)
        // --> suggestList에 같은 부모를 둔 음식 이름이 저장된 상태
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // When user type their text, we will change suggest list
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                List<String> suggest = new ArrayList<>();
                for(String search: suggestList) {   // suggestList를 반복해서 돌린다
                    if(search.contains(materialSearchBar.getText())) {
                        suggest.add(search);
                    }
                    materialSearchBar.setLastSuggestions(suggest);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {

            // When SearchBar is close
            // Restore original suggest adapter
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            // When search finish
            // Show result of search adapter
            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });



    }

    // 입력한 검색조건에 맞는 adapter 세팅하기
    // Show result of search adapter
    private void startSearch(CharSequence text) {

        Log.d("myLog", " FoodList >> startSearch() ");

        // Configure the adapter by building FirebaseRecyclerOptions
        // select * from Foods where Name = ?(User가 입력한 검색조건)
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(foodList.orderByChild("Name").equalTo(text.toString()), Food.class)
                        .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

            // 처음 생성될 때 뷰 홀더(뷰를 담는 그릇) 생성
            // 재활용될 떄부터는 호출 X
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                Log.d("myLog", "  >> onCreateViewHolder() ");

                // Use a custom layout called R.layout.food_item for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                Log.d("myLog","  >> onCreateViewHolder() OK");

                // 각각의 Food 아이템을 위한 뷰를 담고 있는 뷰홀더 객체를 반환
                return new FoodViewHolder(view);
            }

            // 생성된 뷰 홀더(뷰 객체)에 데이터를 넣어서(바인딩) 뷰가 정상적으로 보이게 처리
            // 유저가 스크롤을 내리면(데이터를 더 로드할 때) 호출되어 상단 뷰 홀더가 재활용되어 사용!
            // 재활용 == 뷰 하나하나의 데이터가 소지된 상태에서 필요한 데이터로 바인딩된다는 말
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

                Log.d("myLog","  >> onBindViewholder()");

                // Category obj를 MenuViewHolder에 바인딩
                holder.food_name.setText(model.getName());
                Picasso.get()
                        .load(model.getImage())
                        .into(holder.food_image);

                final Food local = model;
                Log.d("myLog","  >> View Set OK");

                // item(Food) 클릭시
                // item의 FoodId를 Intent 변수에 담아 FoodDetail.java(new Activity)로 보낸다
                holder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Log.d("myLog", "  >> item Clicked, Move Activity");

                        // Start new Activity(FoodDetail.java)
                        // Send FoodId to FoodDetail Activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        // Because FoodId is key, so we just get key of this item
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        Log.d("myLog"," FoodList >> Set Adapter");

        // Set Adapter for Recycler View is Search result
        // activity_food_list.xml의 <RecyclerView/>에 어댑터 장착
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
        Log.d("myLog"," FoodList >> setAdapter OK");

    }

    // Load Suggest from Firebase
    private void loadSuggest() {

        // 같은 부모(같은 CategoryId)를 둔 메뉴안에서 찾도록 설정
        // select * from Foods where MenuId = ?
        foodList.orderByChild("MenuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());    // Add name of food to suggest list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    // 부모 categoryId와 같은 메뉴 출력
    private void loadListFood(String categoryId) {

        // Configure the adapter by building FirebaseRecyclerOptions
        // select * from Foods where MenuId = categoryId
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(foodList.orderByChild("MenuId").equalTo(categoryId), Food.class)
                        .build();

        Log.d("myLog"," FoodList >> loadListFood() - options OK");

        // 2. Create the FirebaseRecyclerAdapter obj - 각 항목을 구성
        // Food 클래스 형식으로 넘어온 정보를 가지고 데이터(아이템)를 그려줄 클래스
        // Need a ViewHolder subclass for display each item(Food)
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

            // 처음 생성될 때 뷰 홀더(뷰를 담는 그릇) 생성
            // 재활용될 떄부터는 호출 X
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                Log.d("myLog", " FoodList >> loadListFood() - onCreateViewHolder 호출");

                // Use a custom layout called R.layout.food_item for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                Log.d("myLog"," FoodList >> onCreateViewHolder OK");

                // 각각의 Food 아이템을 위한 뷰를 담고 있는 뷰홀더 객체를 반환
                return new FoodViewHolder(view);
            }

            // 생성된 뷰 홀더(뷰 객체)에 데이터를 넣어서(바인딩) 뷰가 정상적으로 보이게 처리
            // 유저가 스크롤을 내리면(데이터를 더 로드할 때) 호출되어 상단 뷰 홀더가 재활용되어 사용!
            // 재활용 == 뷰 하나하나의 데이터가 소지된 상태에서 필요한 데이터로 바인딩된다는 말
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

                Log.d("myLog"," FoodList >> loadListFood() - onBindViewholder() 호출");

                // Category obj를 MenuViewHolder에 바인딩
                holder.food_name.setText(model.getName());
                Picasso.get()
                        .load(model.getImage())
                        .into(holder.food_image);

                final Food local = model;
                Log.d("myLog"," FoodList >> View Set OK");

                // item(Food) 클릭시
                // item의 FoodId를 Intent 변수에 담아 FoodDetail.java(new Activity)로 보낸다
                holder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Log.d("myLog", " FoodList >> item Clicked, Move Activity");

                        // Start new Activity(FoodDetail.java)
                        // Send FoodId to FoodDetail Activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        // Because FoodId is key, so we just get key of this item
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

        };

        Log.d("myLog"," FoodList >> Set Adapter");

        // Set Adapter
        // activity_food_list.xml의 <RecyclerView/>에 어댑터 장착
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        Log.d("myLog"," FoodList >> setAdapter OK");
        Log.d("myLog"," FoodList >> itemCount is "+adapter.getItemCount());
    }
}