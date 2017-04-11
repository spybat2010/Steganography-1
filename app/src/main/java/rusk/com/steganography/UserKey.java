package rusk.com.steganography;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by kunal on 23-03-2017.
 */

public class UserKey {


    @SerializedName("userKey")
    @Expose
    private String userKey;
    public String getUserKey() {
        System.out.println("User key is returned ^^^^^^^^^^^^6 " + userKey);
        return userKey;
    }
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }


}
