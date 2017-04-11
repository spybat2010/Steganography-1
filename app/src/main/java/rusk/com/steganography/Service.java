package rusk.com.steganography;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by kunal on 18-03-2017.
 */

public interface Service {
    @Multipart
    @POST("/uploads_image")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @Multipart
    @POST("/uploads_file")
    Call<ResponseBody> postFile(@Part MultipartBody.Part file, @Part("name") RequestBody textFileName);


    @POST("/uploads_text")
    Call<ResponseBody> postText(@Body RequestBody text);
//    Call<ResponseBody> postText(@Part MultipartBody.Part text, @Part("name") RequestBody textName);

    @POST("/uploads_userkey")
    Call<ResponseBody> postUserKey(@Body RequestBody text);

//    @POST("/uploads_token")
//    Call<ResponseBody> postToken(@Body RequestBody body);
//    Call<ResponseBody> postToken(@Field("token") String token);

//    @GET("/to_encrypt/token")
//    void getToken(@Query("token") String param, Callback<List<GetToken>> callback);
    @POST("/to_encrypt")
    Call<ResponseBody> getEncrypted(@Body RequestBody body);
//    Call<ResponseBody> getEncrypted(@Body RequestBody body, Callback token);
//    Call<ResponseBody> getEncrypted(@Field("token") String token);

    @Multipart
//    @POST("/upload")
    @POST("/uploads_encryptin")
    Call<ResponseBody> postEncryptImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);


}