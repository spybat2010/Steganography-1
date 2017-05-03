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
 * Created by kunal on 20-03-2017.
 */

public class ImageToEncryptIn extends Fragment {

    private static final int PICK_IMAGE_TO_ENCRYPT_IN = 0123;
    private static final String TAG = "";
    Service service;
    private Uri filePathURI;
    private ImageView imageToEncryptInFinal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_for_image_to_encrypt_in, container, false);

        imageToEncryptInFinal = (ImageView) view.findViewById(R.id.imageToEncryptInFinal);


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//        service = new Retrofit.Builder().baseUrl(UPLOAD_URL +"upload/").client(client).build().create(Service.class);
        service = new Retrofit.Builder().baseUrl(UPLOAD_URL + "uploads_encryptin/").client(client).build().create(Service.class);

        Intent intent = new Intent();
        String[] mimeTypes = {"image/bmp", "image/png", "image/jpeg"};
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_TO_ENCRYPT_IN);

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_TO_ENCRYPT_IN && resultCode == Activity.RESULT_OK) {

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
            Log.d(TAG, "onActivityResult: filepathuri " + filePathURI.toString());
            System.out.println("realpath is " + realPath);


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePathURI);
                Log.d(TAG, "onActivityResult: image to encrypt in @@@@@@@@@@@@@@@@@@@@@@@@@@@ " + bitmap);
                imageToEncryptInFinal.setImageBitmap(bitmap);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(330, 70);
//                imageToEncryptIn.setLayoutParams(layoutParams);
//                imageToEncryptIn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            File file = new File(realPath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("encrypt_in", file.getName(), reqFile);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("image_file", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_image_to_encrypt_in");

            retrofit2.Call<okhttp3.ResponseBody> req = service.postEncryptImage(body, name);
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

