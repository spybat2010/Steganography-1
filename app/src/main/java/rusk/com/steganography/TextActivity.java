package rusk.com.steganography;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static rusk.com.steganography.MainActivity.UPLOAD_URL;

/**
 * Created by kunal on 18-03-2017.
 */

public class TextActivity extends Fragment {

    private static final int MAX_COUNT = 300;
    private static final String TAG = "";
    public static String textToEncrypt;
    Service service;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_for_text, container, false);

        Button textDone = (Button) view.findViewById(R.id.textDone);
        final EditText editText = (EditText) view.findViewById(R.id.editText);
        final TextView labelCount = (TextView) view.findViewById(R.id.labelCount);


        editText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {

                // Display Remaining Character with respective color
                int count = MAX_COUNT - s.length();
                labelCount.setText(Integer.toString(count));
                labelCount.setTextColor(Color.GREEN);
                if(count < 10)
                {
                    labelCount.setTextColor(Color.YELLOW);
                }
                if(count < 0)
                {
                    labelCount.setTextColor(Color.RED);
                }

            }
        });

        textDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                service = new Retrofit.Builder().baseUrl(UPLOAD_URL + "uploads_text/").client(client).build().create(Service.class);

                textToEncrypt = editText.getText().toString();
                Log.d(TAG, "onClick: text to encrypt ------------ " + textToEncrypt);

                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), textToEncrypt);
                Log.d(TAG, "onClick: text to encrypt " + textToEncrypt);
                retrofit2.Call<okhttp3.ResponseBody> req = service.postText(name);

                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
        return view;
    }
}
