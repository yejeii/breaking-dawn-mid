package com.bitc.eatnow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bitc.eatnow.Adapter.CategoryAdapter;
import com.bitc.eatnow.Interface.ItemClickListener;
import com.bitc.eatnow.Model.Category;
import com.bitc.eatnow.Model.Order;
import com.bitc.eatnow.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitc.eatnow.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("myLog", " Home >> onCreate()");

        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 툴바 적용
        Toolbar toolbar = binding.appBarHome.toolbar;
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        Log.d("myLog","  >> toolbar OK");

        // Firebase 초기화
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        Log.d("myLog","  >> Firebase Init OK");

        // Floating Action Button 클릭 시
        // Cart.java(Activity)로 이동
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        // 토글 버튼 적용, 드로어블 장착
        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.drawer_opened,
                R.string.drawer_closed
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();
        Log.d("myLog","  >> toggle OK");

        // 네비 바 적용
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);
        Log.d("myLog","  >> navi OK1");
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
//                .setOpenableLayout(drawer)
//                .build();

//        NavController navController = Navigation.findNavController(this, R.id.drawer_layout);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        // 네비 바에 유저 이름 설정
        // navigation의 HeaderView에 접근, txtFullName 값에 currentUser에 저장된 이름 set
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());
        Log.d("myLog","  >> navi OK2");

        // Set the adapter
        // content_home.xml의 <RecyclerView/>에 어댑터 장착
        // Load menu to Recylcer View using Firebase UI
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        Log.d("myLog","  >> navi OK3");

        // Firebase DB에 있는 Menu 목록 가져와서 RecyclerView에 뿌리기
        loadMenu();
        Log.d("myLog","  >> loadMenu OK");
    }

    // Firebase DB에 있는 Menu 목록 가져와서 RecyclerView에 뿌리기
    public void loadMenu() {

        Log.d("myLog"," Home >> loadMenu() ");

        // Configure the adapter by building FirebaseRecyclerOptions
        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(category, Category.class)
                        .build();
        Log.d("myLog","  >> FirebaseRecyclerOptions OK");

        // 2. Create the FirebaseRecyclerAdapter obj - 각 항목을 구성
        // Category 클래스 형식의 넘어온 정보를 가지고 데이터(아이템)를 그려줄 클래스
        // Need a ViewHolder subclass for display each item(Category)
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {

            // 처음 생성될 때 뷰 홀더(뷰를 담는 그릇) 생성
            // 재활용될 떄부터는 호출 X
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                // Use a custom layout called R.layout.menu_item for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                Log.d("myLog","  >> onCreateViewHolder OK");
                // 각각의 Category 아이템을 위한 뷰를 담고 있는 뷰홀더 객체를 반환
                return new MenuViewHolder(view);
            }

            // 생성된 뷰 홀더(뷰 객체)에 데이터를 넣어서(바인딩) 뷰가 정상적으로 보이게 처리
            // 유저가 스크롤을 내리면(데이터를 더 로드할 때) 호출되어 상단 뷰 홀더가 재활용되어 사용!
            // 재활용 == 뷰 하나하나의 데이터가 소지된 상태에서 필요한 데이터로 바인딩된다는 말
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {

                Log.d("myLog","  >> onBindViewholder() 호출");

                // Category obj를 MenuViewHolder에 바인딩
                holder.txtMenuName.setText(model.getName());
                Picasso.get()
                        .load(model.getImage())
                        .into(holder.imageView);
                final Category clickItem = model;
                Log.d("myLog","  >> Menu View Set OK");

                // item(Category) 클릭시
                // item의 CategoryId를 Intent 변수에 담아 FoodList.java로 보낸다
                holder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Log.d("myLog", "  >> item clicked, " +
                                "Move to FoodList Activity");

                        // Get CategoryId and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        // Because CategoryId is key, so we just get key of this item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

        };

        Log.d("myLog","  >> Set Adapter");

        // content_home.xml의 <RecyclerView/>에 어댑터 장착
        adapter.startListening();
        recycler_menu.setAdapter(adapter);
        Log.d("myLog","  >> setAdapter OK");

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 메뉴바 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.drawer_layout);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // 네비 아이템 클릭시 Activity 이동
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here
        int id = item.getItemId();

        if(id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_log_out) {
            // Log Out
            Intent signInIntent = new Intent(Home.this, SignIn.class);
            signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signInIntent);
        }

        DrawerLayout drawer = binding.drawerLayout;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}