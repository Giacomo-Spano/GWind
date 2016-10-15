package wind.newwindalarm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by giacomo on 15/07/2015.
 */
public class Spot {
    public String spotName;
    public Long id;
    public String sourceUrl;
    public String webcamUrl;
    public boolean enabled = false;
    public double speed = 0.0;
    public double avSpeed = 0.0;
    public double directionAngle = 0.0;
    public String direction = "";
    public String date = "";
    public boolean offline = false;
    public boolean favorites;
    public double lat = 0.0;
    public double lon = 0.0;


    public Spot(JSONObject jObject) throws JSONException {

        if (!jObject.isNull("id"))
            id = jObject.getLong("id");
        if (!jObject.isNull("lat"))
            lat = jObject.getDouble("lat");
        if (!jObject.isNull("lon"))
            lon = jObject.getDouble("lon");
        if (!jObject.isNull("spotname"))
            spotName = jObject.getString("spotname");
        if (!jObject.isNull("sourceurl"))
            sourceUrl = jObject.getString("sourceurl");
        if (!jObject.isNull("webcamurl"))
            webcamUrl = jObject.getString("webcamurl");
        if (jObject.isNull("speed") || jObject.isNull("avspeed") || jObject.isNull("direction") || jObject.isNull("directionangle") || jObject.isNull("datetime")) {
            offline = true;
        } else {
            offline = false;
            speed = jObject.getLong("speed");
            avSpeed = jObject.getLong("avspeed");
            directionAngle = jObject.getLong("directionangle");
            direction = jObject.getString("direction");
            date = jObject.getString("datetime");
        }
    }
}
