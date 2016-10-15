package wind.newwindalarm.data;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by giacomo on 07/06/2015.
 */
public class MeteoStationData implements Serializable {

    public long id;
    public Double speed = null;
    public Double averagespeed = null;
    public String direction;
    public Double directionangle = null;
    public Date date;
    public Double temperature = null;
    public Double pressure = null;
    public Double humidity = null;
    public Double rainrate = null;
    public Date sampledatetime;
    public String spotName;
    public long spotID;
    public Double trend = 0.0;
    public String source = null;
    public List<String> webcamurlList = new ArrayList<>();
    public Double lat = null;
    public Double lon = null;
    public boolean offline = false;


    public MeteoStationData() {
    }

    public MeteoStationData(MeteoStationData md) {

        if (md == null)
            return;
        id = md.id;
        speed = md.speed;
        averagespeed = md.averagespeed;
        direction = md.direction;
        directionangle = md.directionangle;
        date = md.date;
        temperature = md.temperature;
        pressure  = md.pressure;
        humidity  = md.humidity;
        rainrate  = md.rainrate;
        sampledatetime = md.sampledatetime;;
        spotName = md.spotName;;
        spotID = md.spotID;;
        trend = md.trend;
        int count = 0;
        for (String url : md.webcamurlList) {
            webcamurlList.add(url);
        }
        offline  = md.offline;
    }

    public MeteoStationData(JSONObject jObject) throws JSONException {

        if (!jObject.isNull("windid"))
            id = jObject.getLong("windid");
        if (!jObject.isNull("speed"))
            speed = jObject.getDouble("speed");
        if (!jObject.isNull("avspeed"))
            averagespeed = jObject.getDouble("avspeed");
        if (!jObject.isNull("direction"))
            direction = jObject.getString("direction");
        if (!jObject.isNull("directionangle"))
            directionangle = jObject.getDouble("directionangle");
        if (!jObject.isNull("datetime")) {
            String strDate;
            strDate = jObject.getString("datetime");
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
            try {
                date = df.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!jObject.isNull("temperature"))
            temperature = jObject.getDouble("temperature");
        if (!jObject.isNull("pressure"))
            pressure = jObject.getDouble("pressure");
        if (!jObject.isNull("humidity"))
            humidity = jObject.getDouble("humidity");
        if (!jObject.isNull("rainrate"))
            rainrate = jObject.getDouble("rainrate");
        if (!jObject.isNull("sampledatetime")) {
            String strDate;
            strDate = jObject.getString("sampledatetime");
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
            try {
                sampledatetime = df.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!jObject.isNull("spotname"))
            spotName = jObject.getString("spotname");
        if (!jObject.isNull("trend"))
            trend = jObject.getDouble("trend");
        if (!jObject.isNull("spotid"))
            spotID = jObject.getLong("spotid");
        if (!jObject.isNull("webcamurl"))
            webcamurlList.add(jObject.getString("webcamurl"));
        if (!jObject.isNull("webcamurl2"))
            webcamurlList.add(jObject.getString("webcamurl2"));
        if (!jObject.isNull("webcamurl3"))
            webcamurlList.add(jObject.getString("webcamurl3"));
        if (!jObject.isNull("offline"))
            offline = jObject.getBoolean("offline");
        if (!jObject.isNull("source"))
            source = jObject.getString("source");
        if (!jObject.isNull("lat"))
            lat = jObject.getDouble("lat");
        if (!jObject.isNull("lon"))
            lon = jObject.getDouble("lon");
    }

    public String toJson() {

        JSONObject obj = new JSONObject();

        try {
            obj.put("speed", id);
            obj.put("speed", speed);
            obj.put("avspeed", averagespeed);
            obj.put("direction", direction);
            obj.put("date", date);
            obj.put("temperature", temperature);
            obj.put("pressure", pressure);
            obj.put("humidity", humidity);
            obj.put("rainrate", rainrate);
            obj.put("sampledatetime", sampledatetime);
            obj.put("trend", trend);
            obj.put("spotid", spotID);
            int count = 0;
            for (String url : webcamurlList) {
                if (count++ == 0)
                    obj.put("webcamurl", url);
                else
                    obj.put("webcamurl"+count, url);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return obj.toString();

    }

}
