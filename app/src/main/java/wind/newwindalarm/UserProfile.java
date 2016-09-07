package wind.newwindalarm;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.Serializable;

/**
 * Created by Giacomo Spanò on 24/06/2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class UserProfile implements Serializable {

    String userName;
    String email;
    Bitmap userImage;
    String photoUrl;
    String personId;

}
