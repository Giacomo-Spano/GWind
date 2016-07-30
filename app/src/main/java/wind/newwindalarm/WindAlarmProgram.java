package wind.newwindalarm;

import org.json.JSONException;
import org.json.JSONObject;

public class WindAlarmProgram {

	public String startDate = "01/01/2015";
	public String endDate = "01/01/2018";
	public String lastRingDate = "01/01/2000";
	public String lastRingTime = "00:00";
	public Double speed = 20.0;
	public Double avspeed = 16.0;
	public String startTime = "00:00";
	public String endTime = "23:59";
	public Boolean enabled = true;
	public String direction = "N";
	public Long id = 0L;
	public Boolean mo = true;
	public Boolean tu = true;
	public Boolean we = true;
	public Boolean th = true;
	public Boolean fr = true;
	public Boolean sa = true;
	public Boolean su = true;
	public Long spotId = 0L;
	public int deviceId = -1;

	public WindAlarmProgram() {

	}
	public WindAlarmProgram(JSONObject jObject) throws JSONException {

		if (jObject.has("deviceId"))
			deviceId = jObject.getInt("deviceId");

		startTime = jObject.getString("startTime");
		endTime = jObject.getString("endTime");
		startDate = jObject.getString("startDate");
		endDate = jObject.getString("endDate");
		if (jObject.has("lastRingDate"))
			lastRingDate = jObject.getString("lastRingDate");
		if (jObject.has("lastRingTime"))
			lastRingTime = jObject.getString("lastRingTime");

		speed = Double.parseDouble(jObject.getString("speed"));
		avspeed = Double.parseDouble(jObject.getString("avspeed"));

		enabled = jObject.getBoolean("enabled");
		direction = jObject.getString("direction");
		id = jObject.getLong("id");
		spotId = jObject.getLong("spotid");

		mo = jObject.getBoolean("mo");
		tu = jObject.getBoolean("tu");
		we = jObject.getBoolean("we");
		th = jObject.getBoolean("th");
		fr = jObject.getBoolean("fr");
		sa = jObject.getBoolean("sa");
		su = jObject.getBoolean("su");
	}


}


