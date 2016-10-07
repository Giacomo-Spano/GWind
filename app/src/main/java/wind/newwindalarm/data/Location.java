package wind.newwindalarm.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class Location {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public String id;
    public String name;
    public double lat;
    public double lon;
    public String countryCode;
    public String country;

    public Location() {

    }

    public Location(JSONObject json) {
        fromJson(json);
    }

    private void fromJson(JSONObject jobj) {
        try {
            if (jobj.has("id")) {
                id = jobj.getString("id");
            }
            if (jobj.has("name")) {
                name = jobj.getString("name");
            }

            if (jobj.has("lat")) {
                lat = jobj.getDouble("lat");
            }

            if (jobj.has("lon")) {
                lon = jobj.getDouble("lon");
            }

            if (jobj.has("countrycode")) {
                countryCode = jobj.getString("countrycode");
            }

            if (jobj.has("country")) {
                country = jobj.getString("country");
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
