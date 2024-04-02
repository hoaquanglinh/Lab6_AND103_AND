package com.example.lab6_and103_and;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lab6_and103_and.model.Response;
import com.example.lab6_and103_and.model.User;
import com.example.lab6_and103_and.services.APIService;;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DangKy extends AppCompatActivity {
    EditText edname, edusername, edpassword, edemail;
    ImageView avatar;
    private File file;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);

        edname = findViewById(R.id.edten);
        edemail = findViewById(R.id.edemail);
        edusername = findViewById(R.id.edusername);
        edpassword = findViewById(R.id.edpassword);
        avatar = findViewById(R.id.imageAvatar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create((APIService.class));

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        findViewById(R.id.btndangnhapdk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), edusername.getText().toString().trim());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), edpassword.getText().toString().trim());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), edemail.getText().toString().trim());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), edname.getText().toString().trim());
                MultipartBody.Part multipartBody;
                if (file != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    multipartBody = MultipartBody.Part.createFormData("avartar", file.getName(), requestFile);
                } else {
                    multipartBody = null;
                }

                Call<Response<User>> call = apiService.register(_username, _password, _email, _name, multipartBody);
                call.enqueue(new Callback<Response<User>>() {
                    @Override
                    public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: linh dang ky");
                            Toast.makeText(DangKy.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DangKy.this, DangNhap.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<User>> call, Throwable t) {
                        Toast.makeText(DangKy.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private File createFileFormUri(Uri path, String name) {
        File _file = new File(DangKy.this.getCacheDir(), name + ".png");
        try {
            InputStream in = DangKy.this.getContentResolver().openInputStream(path);
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

    private void chooseImage() {
        Log.d("123123", "chooseAvatar: " + 123123);
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
                        Intent data = o.getData();
                        Uri imageUri = data.getData();

                        Log.d("RegisterActivity", imageUri.toString());

                        Log.d("123123", "onActivityResult: " + data);

                        file = createFileFormUri(imageUri, "avartar");

                        //binding.avatar.setImageURI(imageUri);

                        Glide.with(avatar)
                                .load(imageUri)
                                .centerCrop()
                                .circleCrop()
                                .into(avatar);
                    }
                }
            });
}