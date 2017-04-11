package rusk.com.steganography;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by kunal on 27-03-2017.
 */

public interface DecryptedTextAPI {

    @GET("getdecrypt/decrypted.json")
    Call<DecryptedText> getDecryptedText();
}
