package rusk.com.steganography;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;

/**
 * Created by kunal on 23-03-2017.
 */

public interface UserKeyAPI {

    /*Retrofit get annotation with our URL
       And our method that will return us the user key
    */

    @GET("toencrypt/key.json")
    Call<UserKey> getUserKey();
//    public void getUserKey(Callback<UserKey> response);
}

