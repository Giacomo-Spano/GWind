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
    public Spot(JSONObject jObject) throws JSONException {

        if (!jObject.isNull("spotname"))
            name = jObject.getString("spotname");
        if (!jObject.isNull("id"))
            id = jObject.getLong("id");

    }

    public String toString() {
        return name;
    }
}
