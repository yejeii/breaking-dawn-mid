package com.bitc.eatnow.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitc.eatnow.Interface.ItemClickListener;
import com.bitc.eatnow.Model.Order;
import com.bitc.eatnow.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// ViewHolder 생성 - 뷰 객체(이미지 뷰, 텍스트 뷰 등)를 담을 수 있는 뷰홀더
class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    // ViewHolder
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        img_cart_count = itemView.findViewById(R.id.cart_item_count);
    }

    // onClickListener 구현
    @Override
    public void onClick(View view) {

    }
}

// Adatper 생성 - 각 항목을 구성
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Context context;

    // 생성자(Init CartAdapter)
    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    // 처음 생성될 때 뷰 홀더(뷰를 담는 그릇) 생성
    // 재활용될 떄부터는 호출 X
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Use a custom layout called R.layout.cart_layout for each cart
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);

        return new CartViewHolder(itemView);
    }

    // 생성된 뷰 홀더(뷰 객체)에 데이터를 넣어서(바인딩) 뷰가 정상적으로 보이게 처리
    // 유저가 스크롤을 내리면(데이터를 더 로드할 때) 호출되어 상단 뷰 홀더가 재활용되어 사용!
    // 재활용 == 뷰 하나하나의 데이터가 소지된 상태에서 필요한 데이터로 바인딩된다는 말
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        // 드로어블 만들기
//        holder.img_cart_count.setImageDrawable(drawable);

        Locale locale = new Locale("ko", "KR");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
