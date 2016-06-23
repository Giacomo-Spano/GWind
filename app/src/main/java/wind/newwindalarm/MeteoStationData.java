package wind.newwindalarm;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by giacomo on 07/06/2015.
 */
public class MeteoStationData extends Object {

    static public String Spot_All = "all";

    public Double speed = 0.0;
    public Double averagespeed = 0.0;
    public String direction;
    public Double directionangle = 0.0;
    public String date;
    //public String time;
    public Double temperature = 0.0;
    public Double pressure = 0.0;
    public Double humidity = 0.0;
    public Double rainrate = 0.0;
    public String sampledatetime;
    public String spotName;
    public Long spotID;
    public Double trend = 0.0;
    public String webcamurl = "";

    public MeteoStationData(JSONObject jObject) throws JSONException {

        if (!jObject.isNull("speed"))
            speed = jObject.getDouble("speed");
        if (!jObject.isNull("avspeed"))
            averagespeed = jObject.getDouble("avspeed");
        if (!jObject.isNull("direction"))
            direction = jObject.getString("direction");
        if (!jObject.isNull("directionangle"))
            directionangle = jObject.getDouble("directionangle");
        if (!jObject.isNull("date"))
            date = jObject.getString("date");
        /*if (!jObject.isNull("time"))
            time = jObject.getString("time");*/
        if (!jObject.isNull("temperature"))
            temperature = jObject.getDouble("temperature");
        if (!jObject.isNull("pressure"))
            pressure = jObject.getDouble("pressure");
        if (!jObject.isNull("humidity"))
            humidity = jObject.getDouble("humidity");
        if (!jObject.isNull("rainrate"))
            rainrate = jObject.getDouble("rainrate");
        if (!jObject.isNull("sampledatetime"))
            sampledatetime = jObject.getString("sampledatetime");
        if (!jObject.isNull("spotname"))
            spotName = jObject.getString("spotname");
        if (!jObject.isNull("trend"))
            trend = jObject.getDouble("trend");
        if (!jObject.isNull("spotid"))
            spotID = jObject.getLong("spotid");
        if (!jObject.isNull("webcamurl"))
            webcamurl = jObject.getString("webcamurl");
    }

    public String toJson() {

        JSONObject obj = new JSONObject();

        try {
            obj.put("speed", speed);
            obj.put("avspeed", averagespeed);
            obj.put("direction", direction);
            obj.put("date", date);
            //obj.put("time", time);
            obj.put("temperature", temperature);
            obj.put("pressure", pressure);
            obj.put("humidity", humidity);
            obj.put("rainrate", rainrate);
            obj.put("sampledatetime", sampledatetime);
            obj.put("trend", trend);
            obj.put("spotid", spotID);
            obj.put("webcamurl", webcamurl);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return obj.toString();

    }

}
