package com.example.lab6_and103_and;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    EditText search, searchGia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        search = findViewById(R.id.search);
        searchGia = findViewById(R.id.searchGia);

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

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyword =editable.toString().trim();
                searchDistributor(keyword);
            }
        });

        searchGia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyword =editable.toString().trim();
                searchGiaTien(keyword);
            }
        });

        findViewById(R.id.giam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<List<Fruits>> call = apiService.getGiam();
                call.enqueue(new Callback<List<Fruits>>() {
                    @Override
                    public void onResponse(Call<List<Fruits>> call, Response<List<Fruits>> response) {
                        if (response.isSuccessful()) {
                            list = (ArrayList<Fruits>) response.body();
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
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Fruits>> call, Throwable t) {
                        Log.e("Main", t.getMessage());
                    }
                });
            }
        });

        findViewById(R.id.tang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<List<Fruits>> call = apiService.getTang();
                call.enqueue(new Callback<List<Fruits>>() {
                    @Override
                    public void onResponse(Call<List<Fruits>> call, Response<List<Fruits>> response) {
                        if (response.isSuccessful()) {
                            list = (ArrayList<Fruits>) response.body();
                            adapter = new FruitsAdapter(list, getApplicationContext(), MainActivity.this, new FruitsAdapter.OnItemLongClickListener() {
                                @Override
                                public void onItemLongClick(Fruits fruits) {
                                    Intent intent = new Intent(MainActivity.this, UpdateFruits.class);
                                    intent.putExtra("fruits", fruits);
                                    startActivity(intent);
                                }
                            });

                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Fruits>> call, Throwable t) {
                        Log.e("Main", t.getMessage());
                    }
                });
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

    private void searchDistributor(String keyword) {
        Call<ArrayList<Fruits>> call = apiService.searchFruits(keyword);
        call.enqueue(new Callback<ArrayList<Fruits>>() {
            @Override
            public void onResponse(Call<ArrayList<Fruits>> call, Response<ArrayList<Fruits>> response) {
                if (response.isSuccessful()) {
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
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Fruits>> call, Throwable t) {
                Log.e("Search", "Search failed: " + t.toString());
                Toast.makeText(MainActivity.this, "Đã xảy ra lỗi kh tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchGiaTien(String keyword) {
        double gia = 0;
        if(keyword.length() == 0){
            gia = 0;
        }else{
            gia = Double.parseDouble(keyword);
        }


        Call<ArrayList<Fruits>> call = apiService.searchGia(gia);
        call.enqueue(new Callback<ArrayList<Fruits>>() {
            @Override
            public void onResponse(Call<ArrayList<Fruits>> call, Response<ArrayList<Fruits>> response) {
                if (response.isSuccessful()) {
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
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Fruits>> call, Throwable t) {
                Log.e("Search", "Search failed: " + t.toString());
                Toast.makeText(MainActivity.this, "Đã xảy ra lỗi kh tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}