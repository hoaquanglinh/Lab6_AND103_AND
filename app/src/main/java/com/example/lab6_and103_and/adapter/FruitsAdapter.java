package com.example.lab6_and103_and.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab6_and103_and.MainActivity;
import com.example.lab6_and103_and.R;
import com.example.lab6_and103_and.model.Fruits;

import java.text.NumberFormat;
import java.util.ArrayList;

public class FruitsAdapter extends BaseAdapter {
    private ArrayList<Fruits> list;
    private Context context;
    MainActivity activity;

    public FruitsAdapter(ArrayList<Fruits> list, Context context, MainActivity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_fruits, null);

        Fruits fruits = list.get(i);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();

        TextView tvTen = rowView.findViewById(R.id.tvname);
        TextView tvSoLuong = rowView.findViewById(R.id.tvquantity);
        TextView tvGia = rowView.findViewById(R.id.tvprice);
        TextView tvCongTy = rowView.findViewById(R.id.tvdistributor);
        ImageView image = rowView.findViewById(R.id.image);

        tvTen.setText(fruits.getName());
        tvSoLuong.setText(fruits.getQuantity() + "");
        tvGia.setText(numberFormat.format(fruits.getPrice()));
        tvCongTy.setText(fruits.getDistributor());
        Glide.with(context)
                .load(fruits.getImage().get(0))
                .thumbnail(Glide.with(context).load(R.drawable.loading))
                .into(image);

        rowView.findViewById(R.id.btndelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.xoa((fruits.get_id()));
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });

        return rowView;
    }
}



