package com.example.lab6_and103_and;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lab6_and103_and.model.Fruits;
import com.example.lab6_and103_and.services.APIService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFruits extends AppCompatActivity {
    ImageView image;
    EditText edname, edquatity, edprice, eddistributor, eddescription;
    private ArrayList<File> ds_image;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fruits);

        image = findViewById(R.id.edimage);
        edname = findViewById(R.id.edname);
        edquatity = findViewById(R.id.edquantity);
        edprice = findViewById(R.id.edprice);
        eddistributor = findViewById(R.id.eddistributor);
        eddescription = findViewById(R.id.eddescription);

        ds_image = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        findViewById(R.id.btnAddSP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, RequestBody> mapRequestBody = new HashMap<>();
                String _name = edname.getText().toString().trim();
                String _quantity = edquatity.getText().toString().trim();
                String _price = edprice.getText().toString().trim();
                String _description = eddescription.getText().toString().trim();
                String _distributor = eddistributor.getText().toString().trim();

                mapRequestBody.put("name", getRequestBody(_name));
                mapRequestBody.put("quantity", getRequestBody(_quantity));
                mapRequestBody.put("price", getRequestBody(_price));
                mapRequestBody.put("description", getRequestBody(_description));
                mapRequestBody.put("distributor", getRequestBody(_distributor));
                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
                ds_image.forEach(file1 -> {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file1);
                    MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
                    _ds_image.add(multipartBodyPart);
                });

                Call<Fruits> call = apiService.addFruits(mapRequestBody, _ds_image);
                call.enqueue(new Callback<Fruits>() {
                    @Override
                    public void onResponse(Call<Fruits> call, Response<Fruits> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(AddFruits.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddFruits.this, MainActivity.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<Fruits> call, Throwable t) {
                        Toast.makeText(AddFruits.this, "Loi OnFailure", Toast.LENGTH_SHORT).show();
                        Log.e("Main", t.getMessage());
                    }
                });
            }
        });
    }

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"),value);
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImage.launch(intent);

    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        ds_image.clear();
                        Intent data = o.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();

                                File file = createFileFormUri(imageUri, "image" + i);
                                ds_image.add(file);
                            }


                        } else if (data.getData() != null) {
                            // Trường hợp chỉ chọn một hình ảnh
                            Uri imageUri = data.getData();
                            // Thực hiện các xử lý với imageUri
                            File file = createFileFormUri(imageUri, "image");
                            ds_image.add(file);

                        }
                        Glide.with(AddFruits.this)
                                .load(ds_image.get(0))
                                .thumbnail(Glide.with(AddFruits.this).load(R.drawable.baseline_broken_image_24))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(image);
                    }
                }
            });

    private File createFileFormUri(Uri path, String name) {
        File _file = new File(AddFruits.this.getCacheDir(), name + ".png");
        try {
            InputStream in = AddFruits.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            Log.d("123123", "createFileFormUri: " + _file);
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}