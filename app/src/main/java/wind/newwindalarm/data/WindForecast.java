package wind.newwindalarm.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Giacomo Spanò on 28/09/2016.
 */
public class WindForecast {

    public long spotId = -1;
    public String source = null;
    public String sourceId;
    public String sourceSpotName;
    public List<Date> datetimes = new ArrayList<Date>();
    public List<Double> speeds = new ArrayList<Double>();
    public List<Double> maxSpeeds = new ArrayList<Double>();
    public List<Double> speedDirs = new ArrayList<Double>();
    public List<Double> temperatures = new ArrayList<Double>();
    public List<Double> maxtemperatures = new ArrayList<Double>();
    public List<Double> mintemperatures = new ArrayList<Double>();
    public List<Integer> humidities = new ArrayList<Integer>();
    public List<String> weathers = new ArrayList<String>();
    public List<String> weatherdescriptions = new ArrayList<String>();
    public List<String> icons = new ArrayList<String>();
    public List<Integer> cloudPercentages = new ArrayList<Integer>();

    public String sourceSpotCountry;
    public long id;

    public WindForecast() {
    }


    public WindForecast(String json) {

        fromJson(json);
    }

    private void fromJson(String json) {
        try {
            JSONObject jobj = new JSONObject(json);

            JSONArray jarray = null;

            if (jobj.has("datetimes")) {
                jarray = jobj.getJSONArray("datetimes");
                for (int i = 0; i < jarray.length(); i++) {
                    String strDate = (String) jarray.get(i);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date = df.parse(strDate);
                    datetimes.add(date);
                }
            }

            if (jobj.has("speeds")) {
                jarray = jobj.getJSONArray("speeds");
                for (int i = 0; i < jarray.length(); i++) {
                    Double value = (Double) jarray.get(i);
                    speeds.add(value);
                }
            }

            if (jobj.has("maxSpeeds")) {
                jarray = jobj.getJSONArray("maxSpeeds");
                for (int i = 0; i < jarray.length(); i++) {
                    Double value = (Double) jarray.get(i);
                    maxSpeeds.add(value);
                }
            }

            if (jobj.has("speedDirs")) {
                jarray = jobj.getJSONArray("speedDirs");
                for (int i = 0; i < jarray.length(); i++) {
                    Double value = (Double) jarray.get(i);
                    speedDirs.add(value);
                }
            }

            if (jobj.has("temperatures")) {
                jarray = jobj.getJSONArray("temperatures");
                for (int i = 0; i < jarray.length(); i++) {
                    Double value = (Double) jarray.get(i);
                    temperatures.add(value);
                }
            }

            if (jobj.has("maxtemperatures")) {
                jarray = jobj.getJSONArray("maxtemperatures");
                for (int i = 0; i < jarray.length(); i++) {
                    Double value = (Double) jarray.get(i);
                    maxtemperatures.add(value);
                }
            }

            if (jobj.has("mintemperatures")) {
                jarray = jobj.getJSONArray("mintemperatures");
                for (int i = 0; i < jarray.length(); i++) {
                    Double value = (Double) jarray.get(i);
                    mintemperatures.add(value);
                }
            }

            if (jobj.has("humidities")) {
                jarray = jobj.getJSONArray("humidities");
                for (int i = 0; i < jarray.length(); i++) {
                    Integer value = (Integer) jarray.get(i);
                    humidities.add(value);
                }
            }

            if (jobj.has("weathers")) {
                jarray = jobj.getJSONArray("weathers");
                for (int i = 0; i < jarray.length(); i++) {
                    String value = (String) jarray.get(i);
                    weathers.add(value);
                }
            }

            if (jobj.has("maxSpeeds")) {
                jarray = jobj.getJSONArray("weatherdescriptions");
                for (int i = 0; i < jarray.length(); i++) {
                    String value = (String) jarray.get(i);
                    weatherdescriptions.add(value);
                }
            }

            if (jobj.has("icons")) {
                jarray = jobj.getJSONArray("icons");
                for (int i = 0; i < jarray.length(); i++) {
                    String value = (String) jarray.get(i);
                    icons.add(value);
                }
            }

            if (jobj.has("cloudPercentages")) {
                jarray = jobj.getJSONArray("cloudPercentages");
                for (int i = 0; i < jarray.length(); i++) {
                    Integer value = (Integer) jarray.get(i);
                    cloudPercentages.add(value);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
