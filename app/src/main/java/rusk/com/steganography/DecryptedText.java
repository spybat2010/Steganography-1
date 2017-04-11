package rusk.com.steganography;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by kunal on 27-03-2017.
 */

public class DecryptedText {

    @SerializedName("data")
    @Expose
    private String data;
    public String getDecryptedText() {
        System.out.println("User key is returned ^^^^^^^^^^^^6 " + data);
        return data;
    }
    public void setDecryptedText(String userKey) {
        this.data = data;
    }
}
