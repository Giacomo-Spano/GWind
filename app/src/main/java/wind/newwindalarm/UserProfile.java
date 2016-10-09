package wind.newwindalarm;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Giacomo Spanò on 24/06/2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class UserProfile implements Serializable {

    public String userName;
    public String email;
    //public Bitmap userImage;
    public String photoUrl;
    public String personId;

}
