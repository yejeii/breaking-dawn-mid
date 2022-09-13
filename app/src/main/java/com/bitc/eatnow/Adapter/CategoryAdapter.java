package com.bitc.eatnow.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bitc.eatnow.Home;
import com.bitc.eatnow.Interface.ItemClickListener;
import com.bitc.eatnow.Model.Category;
import com.bitc.eatnow.R;
import com.bitc.eatnow.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends FirebaseRecyclerAdapter<Category, MenuViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * options: FirebaseRecyclerView에 DB의 쿼리문옵션을 넣어 해당 쿼리문에
     *          맞는 데이터를 자동으로 세팅해주기 위해 사용
     */
    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options) {
        super(options);
    }

    // 뷰 홀더(뷰 객체)에 데이터를 세팅(바인딩)해 뷰가 정상적으로 보이게 처리
    // 유저가 스크롤을 내리면(데이터를 더 로드할 때) 호출되어 상단 뷰 홀더가 재활용되어 사용!
    // 재활용 == 뷰 하나하나의 데이터가 소지된 상태에서 필요한 데이터로 바인딩된다는 말
    @Override
    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {

        Log.d("myLog"," loadMenu() - onBindViewholder 1");

        // Category obj를 MenuViewHolder에 바인딩
        holder.txtMenuName.setText(model.getName());
        Picasso.get()
                .load(model.getImage())
                .into(holder.imageView);
        final Category clickItem = model;
        Log.d("myLog"," loadMenu() - onBindViewholder 2");

        // item 클릭시
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Toast.makeText(view.getContext(), ""+clickItem.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 처음 생성될 때 뷰 홀더(뷰를 담는 그릇) 생성
    // 재활용될 떄부터는 호출 X
    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Use a custom layout called R.layout.menu_item for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, parent, false);

        Log.d("myLog"," loadMenu() - onCreateViewHolder OK");

        // 각각의 Category 아이템을 위한 뷰를 담고 있는 뷰홀더 객체를 반환
        return new MenuViewHolder(view);
    }

}
