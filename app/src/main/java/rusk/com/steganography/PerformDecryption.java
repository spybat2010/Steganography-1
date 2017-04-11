package rusk.com.steganography;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static rusk.com.steganography.MainActivity.UPLOAD_URL;

public class PerformDecryption extends AppCompatActivity {


    EditText editText;
    Service service;
    DecryptedTextAPI decryptedTextAPI;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform_decryption);
        editText = (EditText) findViewById(R.id.editTextOfKey);
        Button submitUserKey = (Button) findViewById(R.id.buttonSubmit);
        final TextView decryptedTextView = (TextView) findViewById(R.id.decryptedText);
        final ImageView imageViewForUnlock = (ImageView) findViewById(R.id.imageViewForUnLock);
        imageViewForUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerformDecryption.this, MainActivity.class);
                startActivity(intent);
            }
        });

        submitUserKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userKey = editText.getText().toString();
                if (userKey.isEmpty()) {
                    Toast.makeText(PerformDecryption.this, "Please enter the key !!", Toast.LENGTH_LONG).show();
                } else {

                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                    service = new Retrofit.Builder().baseUrl(UPLOAD_URL + "uploads_userkey/").client(client).build().create(Service.class);
                    RequestBody user_key = RequestBody.create(MediaType.parse("text/plain"), userKey);
                    Log.d("USERKEY ", "onClick: text to encrypt " + userKey);
                    final retrofit2.Call<okhttp3.ResponseBody> req = service.postUserKey(user_key);

                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("Inside", "onResponse: &&&&&&&&&&&&&&&&&&&& ");

                            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(UPLOAD_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            decryptedTextAPI = retrofit.create(DecryptedTextAPI.class);
                            decryptedTextAPI.getDecryptedText().enqueue(new Callback<DecryptedText>() {
                                @Override
                                public void onResponse(Call<DecryptedText> call, Response<DecryptedText> response) {
                                    String data = response.body().getDecryptedText();
                                    decryptedTextView.setText(data);

                                }

                                @Override
                                public void onFailure(Call<DecryptedText> call, Throwable t) {
                                    Toast.makeText(PerformDecryption.this, "Entered User Key might be wrong", Toast.LENGTH_LONG).show();
                                    Log.d("Wrong", "onFailure: wrong key");

                                }
                            });



                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                }



            }
        });





    }
}
