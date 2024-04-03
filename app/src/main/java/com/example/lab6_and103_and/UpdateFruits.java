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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lab6_and103_and.model.Fruits;
import com.example.lab6_and103_and.services.APIService;

import java.io.File;
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

public class UpdateFruits extends AppCompatActivity {
    EditText udname, udquantity, udprice, uddistributor, uddescription;
    ImageView udImage;
    private ArrayList<File> ds_image;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fruits);

        udname = findViewById(R.id.udname);
        udquantity = findViewById(R.id.udquantity);
        udprice = findViewById(R.id.udprice);
        uddescription = findViewById(R.id.uddescription);
        uddistributor = findViewById(R.id.uddistributor);
        udImage = findViewById(R.id.udimage);

        ds_image = new ArrayList<>();

        Intent intent = getIntent();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create((APIService.class));

        udImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        if (intent != null) {
            Fruits fruits = (Fruits) intent.getSerializableExtra("fruits");
            if (fruits != null) {
                udname.setText(fruits.getName());
                udquantity.setText(fruits.getQuantity() + "");
                udprice.setText(fruits.getPrice() + "");
                uddistributor.setText(fruits.getDistributor());
                uddescription.setText(fruits.getDescription());
                Glide.with(UpdateFruits.this)
                        .load(fruits.getImage().get(0))
                        .thumbnail(Glide.with(UpdateFruits.this).load(R.drawable.baseline_broken_image_24))
                        .skipMemoryCache(true)
                        .into(udImage);
            }

            findViewById(R.id.btnUdSP).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {;
                    Map<String, RequestBody> mapRequestBody = new HashMap<>();
                    String _name = udname.getText().toString().trim();
                    String _quantity = udquantity.getText().toString().trim();
                    String _price = udprice.getText().toString().trim();
                    String _description = uddescription.getText().toString().trim();
                    String _distributor = uddistributor.getText().toString().trim();

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

                    Call<Fruits> call = apiService.update(mapRequestBody, fruits.get_id(), _ds_image);
                    call.enqueue(new Callback<Fruits>() {
                        @Override
                        public void onResponse(Call<Fruits> call, Response<Fruits> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(UpdateFruits.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdateFruits.this, MainActivity.class));
                            }
                        }

                        @Override
                        public void onFailure(Call<Fruits> call, Throwable t) {
                            Toast.makeText(UpdateFruits.this, "Loi OnFailure", Toast.LENGTH_SHORT).show();
                            Log.e("Main", t.getMessage());
                        }
                    });
                }
            });
        }

    }

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"),value);
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        getImage.launch(intent);

    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Uri tempUri = null;
                        ds_image.clear();
                        Intent data = o.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                tempUri = imageUri;
                                File file = createFileFromUri(imageUri, "image" + i);
                                ds_image.add(file);
                            }


                        } else if (data.getData() != null) {
                            // Trường hợp chỉ chọn một hình ảnh
                            Uri imageUri = data.getData();
                            tempUri = imageUri;
                            // Thực hiện các xử lý với imageUri
                            File file = createFileFromUri(imageUri, "image");
                            ds_image.add(file);

                        }
                        if (tempUri != null) {
                            Glide.with(UpdateFruits.this)
                                    .load(tempUri)
                                    .thumbnail(Glide.with(UpdateFruits.this).load(R.drawable.baseline_broken_image_24))
                                    .skipMemoryCache(true)
                                    .into(udImage);
                        }
                    }
                }
            });

    private File createFileFromUri(Uri uri, String name) {
        File file = new File(getCacheDir(), name + ".png");
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}