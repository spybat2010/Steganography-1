package rusk.com.steganography;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

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

import static android.R.attr.data;

 //Text To Encrypt

public class MainActivity extends AppCompatActivity {


    public static final String UPLOAD_URL = "http://10.10.25.106:5000/";


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 01234;
    private static final String TAG = "";

    private ImageView imageViewToEncryptIn;

    UserKeyAPI userKeyAPI;
    Service service;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final Button buttonEncrypt = (Button) findViewById(R.id.encrypt);


        imageViewToEncryptIn = (ImageView) findViewById(R.id.imageToEncryptIn);
        imageViewToEncryptIn.setImageResource(R.drawable.first);

        final ImageView imageViewForLock = (ImageView) findViewById(R.id.imageViewForLock);


        imageViewForLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PerformDecryption.class);
                startActivity(intent);

            }
        });



//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View promptView = layoutInflater.inflate(R.layout.prompt, null);
//
//        final AlertDialog alertD = new AlertDialog.Builder(this).create();
//
//        Button btnEncryption = (Button) promptView.findViewById(R.id.buttonEncryption);
//
//        Button btnDecryption = (Button) promptView.findViewById(R.id.buttonDecryption);
//
//        btnEncryption.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                alertD.cancel();
//                // btnAdd1 has been clicked
//
//            }
//        });

//        btnDecryption.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                alertD.cancel();
//                finish();
//                Intent intent = new Intent(MainActivity.this, PerformDecryption.class);
//                startActivity(intent);
//                // btnAdd2 has been clicked
//
//            }
//        });
//
//        alertD.setView(promptView);
//        alertD.setCancelable(false);
//
//        alertD.show();
//

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }


        imageViewToEncryptIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                imageViewToEncryptIn.postInvalidate();
                imageViewToEncryptIn.setImageResource(android.R.color.transparent);
                ImageToEncryptIn imageToEncryptIn = new ImageToEncryptIn();
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout , imageToEncryptIn);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });





//        generateToken.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//                SecureRandom rnd = new SecureRandom();
//                StringBuilder sb = new StringBuilder(20);
//                for (int i = 0; i < 20; i++)
//                    sb.append(AB.charAt(rnd.nextInt(AB.length())));
//                randomString = sb.toString();
//                textViewOfToken.setText(randomString);
//                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//                service = new Retrofit.Builder().baseUrl("http://192.168.43.61" +
//                        ":5000/uploads_token/").client(client).build().create(Service.class);
//                RequestBody token = RequestBody.create(MediaType.parse("text/plain"), randomString);
//                retrofit2.Call<okhttp3.ResponseBody> req = service.postToken(token);
//
//                req.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });
//
//
//
//            }
//        });




        // Encryption Button

        buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    if ( imageViewToEncryptIn != null) {

                        Toast.makeText(MainActivity.this, "Encrypting ...", Toast.LENGTH_LONG).show();
                        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(UPLOAD_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        userKeyAPI = retrofit.create(UserKeyAPI.class);



//                        userKeyAPI = new Retrofit.Builder().baseUrl().addConverterFactory(GsonConverterFactory.create()).build().create(UserKeyAPI.class);
//                        Response<UserKey> userKeyResponse = Response.success("userKey");
                        Log.d(TAG, "onClick: getuserkey is ************************** ");
                        userKeyAPI.getUserKey().enqueue(new Callback<UserKey>() {
                            @Override
                            public void onResponse(Call<UserKey> call, Response<UserKey> response) {


                                String user_key = response.body().getUserKey();
                                Log.d(TAG, "onResponse: response is -------------------" + user_key);
                                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                                service = new Retrofit.Builder().baseUrl( UPLOAD_URL + "uploads_token/").client(client).build().create(Service.class);
                                RequestBody token = RequestBody.create(MediaType.parse("text/plain"), "encryption is being done");
                                retrofit2.Call<okhttp3.ResponseBody> req = service.getEncrypted(token);

                                req.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                                Intent intent = new Intent(MainActivity.this, GenerateRE.class);
                                intent.putExtra("user_key", user_key);
                                startActivity(intent);



                            }

                            @Override
                            public void onFailure(Call<UserKey> call, Throwable t) {
                                Log.d(TAG, "onFailure: call contains " + call.toString());
                                Log.d(TAG, "onFailure: failed : i do not know why !!");
                            }
                        });


                    }


            }
        });
    }

//    private boolean hasImage(@NonNull ImageView view) {
//        Drawable drawable = view.getDrawable();
//        boolean hasImage = (drawable != null);
//
//        if (hasImage && (drawable instanceof BitmapDrawable)) {
//            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
//        }
//
//        return hasImage;
//    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.textButton:
                if (checked) {
                    TextActivity textActivity = new TextActivity();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layoutToReplace, textActivity);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
            case R.id.fileButton:
                if (checked) {
                    FileActivity fileActivity = new FileActivity();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layoutToReplace, fileActivity);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;

                }
            case R.id.imageButton:
                if (checked) {

                    ImageActivity imageActivity = new ImageActivity();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layoutToReplace, imageActivity);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
        }
    }
}
