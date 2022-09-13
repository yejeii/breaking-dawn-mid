package com.bitc.eatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitc.eatnow.Model.Request;
import com.bitc.eatnow.ViewHolder.OrderViewHolder;
import com.bitc.eatnow.databinding.ActivityOrderStatusBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    private ActivityOrderStatusBinding binding;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("myLog", " OrderStatus >> onCreate()");

        super.onCreate(savedInstanceState);

        binding = ActivityOrderStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Set the adapter
        // activity_order_status.xml의 <RecyclerView/>에 어댑터 장착
        // Load menu to Recylcer View using Firebase UI
        recyclerView = binding.listOrders;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //
        loadOrders(Common.currentUser.getPhone());



    }

    // 파라미터로 들어가는 phone 번호에 해당하는 Requests 목록(Firebase DB) 가져와서 RecyclerView에 뿌리기
    private void loadOrders(String phone) {

        Log.d("myLog", " OrderStatus >> loadOrders() ");

        // Configure the adapter by building FirebaseRecyclerOptions
        // select * from requests where phone =
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(requests.orderByChild("phone").equalTo(phone), Request.class)
                        .build();

        Log.d("myLog","  >> FirebaseRecyclerOptions OK");

        // 2. Create the FirebaseRecyclerAdapter obj - 각 항목을 구성
        // Request 클래스 형식의 넘어온 정보를 가지고 데이터(아이템)를 그려줄 클래스
        // Need a ViewHolder subclass for display each item(Request)
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {

            // 생성된 뷰 홀더(뷰 객체)에 데이터를 넣어서(바인딩) 뷰가 정상적으로 보이게 처리
            // 유저가 스크롤을 내리면(데이터를 더 로드할 때) 호출되어 상단 뷰 홀더가 재활용되어 사용!
            // 재활용 == 뷰 하나하나의 데이터가 소지된 상태에서 필요한 데이터로 바인딩된다는 말
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder,
                                            int position,
                                            @NonNull Request model) {

                // OrderViewHolder holder 변수에 OrderID & OrderStatus 설정
                holder.txtOrderId.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderPhone.setText(model.getPhone());

                Log.d("myLog","  >> onBindViewHolder() OK");

            }

            // 처음 생성될 때 뷰 홀더(뷰를 담는 그릇) 생성
            // 재활용될 떄부터는 호출 X
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                // Use a custom layout called R.layout.order_layout for each item(requests)
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);

                Log.d("myLog","  >> onCreateViewHolder() OK");

                // 각각의 Status뷰를 담고 있는 뷰홀더 객체를 반환
                return new OrderViewHolder(view);
            }
        };

        // Set Adapter
        // activity_order_status.xml의 <RecyclerView/>에 어댑터 장착
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        Log.d("myLog","  >> setAdapter OK");
        Log.d("myLog","  >> 접수된 주문갯수 is "+adapter.getItemCount());

    }

    private String convertCodeToStatus(String status) {

        if(status.equals("0"))
            return "주문 접수 완료";
        else if(status.equals("1"))
            return "배송 중";
        else
            return "배송 완료";
    }
}