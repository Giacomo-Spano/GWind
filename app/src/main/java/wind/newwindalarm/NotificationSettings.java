package wind.newwindalarm;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationSettings {

	public Boolean windIncrease = true;
	public Boolean windChangeDirection = true;

	public NotificationSettings() {

	}
	public NotificationSettings(JSONObject jObject) throws JSONException {

		windIncrease = jObject.getBoolean("windIncrease");
		windChangeDirection = jObject.getBoolean("windChangeDirection");

	}
}


