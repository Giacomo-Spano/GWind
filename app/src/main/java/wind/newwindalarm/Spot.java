package wind.newwindalarm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by giacomo on 15/07/2015.
 */
public class Spot<T> {
    public String name;
    public Long id;
    public boolean enabled = false;

    public double speed = 0.0;
    public double avSpeed = 0.0;
    public double directionAngle = 0.0;
    public String direction = "";
    public String date = "";
    public boolean offline = false;

    public Spot(JSONObject jObject) throws JSONException {

        if (!jObject.isNull("spotname"))
            name = jObject.getString("spotname");
        if (!jObject.isNull("id"))
            id = jObject.getLong("id");

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

    public String toString() {
        return name;
    }
}
