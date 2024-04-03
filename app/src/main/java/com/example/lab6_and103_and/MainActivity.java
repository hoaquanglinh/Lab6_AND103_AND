package com.example.lab6_and103_and;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lab6_and103_and.adapter.FruitsAdapter;
import com.example.lab6_and103_and.model.Fruits;
import com.example.lab6_and103_and.services.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements FruitsAdapter.OnItemLongClickListener {
    ListView listView;
    APIService apiService;
    ArrayList<Fruits> list;
    FruitsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddFruits.class);
                startActivity(intent);
            }
        });

        loadData();
    }

    void loadData() {
        Call<ArrayList<Fruits>> call = apiService.getFruits();
        call.enqueue(new Callback<ArrayList<Fruits>>() {
            @Override
            public void onResponse(Call<ArrayList<Fruits>> call, Response<ArrayList<Fruits>> response) {
                list = response.body();

                adapter = new FruitsAdapter(list, getApplicationContext(), MainActivity.this, new FruitsAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(Fruits fruits) {
                        // Xử lý sự kiện khi item được nhấn giữ
                        Intent intent = new Intent(MainActivity.this, UpdateFruits.class);
                        intent.putExtra("fruits", fruits);
                        startActivity(intent);
                    }
                });

                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Fruits>> call, Throwable t) {
                Log.e("Main: ", t.getMessage());
            }
        });
    }

    public void xoa(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có chắc chắn muốn xóa?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Fruits> call = apiService.deleteFruits(id);
                call.enqueue(new Callback<Fruits>() {
                    @Override
                    public void onResponse(Call<Fruits> call, Response<Fruits> response) {
                        if (response.isSuccessful()) {
                            loadData();
                            Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Fruits> call, Throwable t) {
                        Log.e("Home", "Call failed: " + t.toString());
                        Toast.makeText(MainActivity.this, "Đã xảy ra lỗi khi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        builder.show();

    }

    @Override
    public void onItemLongClick(Fruits fruits) {
        Intent intent = new Intent(this, UpdateFruits.class);
        intent.putExtra("fruits", fruits);
        startActivity(intent);
    }
}