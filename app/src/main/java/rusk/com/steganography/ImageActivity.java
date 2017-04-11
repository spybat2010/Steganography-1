package rusk.com.steganography;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

public class ImageActivity extends Fragment {

    public static final int PICK_IMAGE = 100;
    private static final String TAG = "";
    private static int IMAGE_FOR_ENC = 1234;
    Service service;
    private ImageView mImageView;
    private Uri filePathURI;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_for_image, container, false);

        mImageView = (ImageView) view.findViewById(R.id.toEncryptImage);


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");

//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/*");
//
//                Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

//                startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_FOR_ENC);

//                startActivityForResult(chooserIntent, IMAGE_FOR_ENC);


                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                service = new Retrofit.Builder().baseUrl(UPLOAD_URL + "uploads_image/").client(client).build().create(Service.class);
                Intent intent = new Intent();
                String[] mimeTypes = {"image/bmp", "image/png", "image/jpeg"};
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {


            // SDK < API11
            String realPath;
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getContext(), data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(getContext(), data.getData());

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(getContext(), data.getData());

            filePathURI = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePathURI);
                mImageView.setImageBitmap(bitmap);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(330, 70);
//                mImageView.setLayoutParams(layoutParams);
//                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            File file = new File(realPath);
//            File file = new File(filePath);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("up", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

            Log.d("THIS", data.getData().getPath());

            retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getContext(), "Type of image should be .png or .bmp", Toast.LENGTH_LONG).show();
        }
    }
}
