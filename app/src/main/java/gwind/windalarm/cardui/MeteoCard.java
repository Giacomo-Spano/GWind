package gwind.windalarm.cardui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import gwind.windalarm.R;
import gwind.windalarm.controls.WindControl;

/**
 * Created by giacomo on 28/02/14.
 */
public class MeteoCard extends LinearLayout {

    private TextView mSourceUrl;
    private TextView mSpotName;

    View[] coloredbar = new View[11];
    private String[] windcolors = {"#9600FE","#0032FE","#0064FE","#0096FE","#00C8FE","#25C192","#00FA00","#FEE100","#FE9600","#DC4A1D","#AA001D","#FE0096"};
    private int[] windvalues = {0,4,8,12,16,19,23,27,31,35,39};

public MeteoCard(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public void init() {

        mSpotName = (TextView) findViewById(R.id.spotNameTextView);

        mSourceUrl = (TextView) findViewById(R.id.sourceTextView);

        coloredbar[0] = (View) findViewById(R.id.colored_bar0);
        coloredbar[1] = (View) findViewById(R.id.colored_bar1);
        coloredbar[2] = (View) findViewById(R.id.colored_bar2);
        coloredbar[3] = (View) findViewById(R.id.colored_bar3);
        coloredbar[4] = (View) findViewById(R.id.colored_bar4);
        coloredbar[5] = (View) findViewById(R.id.colored_bar5);
        coloredbar[6] = (View) findViewById(R.id.colored_bar6);
        coloredbar[7] = (View) findViewById(R.id.colored_bar7);
        coloredbar[8] = (View) findViewById(R.id.colored_bar8);
        coloredbar[9] = (View) findViewById(R.id.colored_bar9);
        coloredbar[10] = (View) findViewById(R.id.colored_bar10);

        for (int i = 0; i < coloredbar.length; i++) {
            coloredbar[i].setBackgroundColor(Color.parseColor(windcolors[i]));
        }
    }

    public void setTitle(String name) {
        if (name == null)
            return;
        if (mSpotName != null)
            mSpotName.setText(name);
    }

    public void setSourceUrl(String url) {
        if (url == null) return;
        if (mSourceUrl != null)
            mSourceUrl.setText(url);
    }

    public void setSpeed(Double speed) {

        if (speed == null) return;

        TextView tv = (TextView) findViewById(R.id.speedTextView);
        if (tv != null)
            tv.setText(""+speed);

        WindControl wc = (WindControl) findViewById(R.id.windcontrol);
        if (wc!=null)
            wc.setPower(speed);

        tv = (TextView) findViewById(R.id.speedTextView);
        if (tv != null)
            tv.setText(speed.toString());

        for (int i = 0; i < coloredbar.length; i++) {
            if (coloredbar[i] == null)
                continue;
            if (speed >= windvalues[i])
                coloredbar[i].setBackgroundColor(Color.parseColor(windcolors[i]));
            else
                coloredbar[i].setBackgroundColor(Color.WHITE);
        }
    }


    public void setDirection(Double angle, String directionSymbol) {

        if (angle == null) return;
        if (directionSymbol == null) return;
        WindControl wc = (WindControl) findViewById(R.id.windcontrol);
        if (wc != null)
            wc.setDirection(angle, directionSymbol);

        TextView tv = (TextView) findViewById(R.id.directionTextView);
        if (tv != null)
            tv.setText(directionSymbol+" "); // lo spazio serve perchè con direction W è brutto
    }
    public void setAvSpeed(String avspeed) {

        if (avspeed == null) return;
        TextView tv = (TextView) findViewById(R.id.avspeedTextView);
        if (tv != null)
            tv.setText("(" + avspeed + ")");
    }
    public void setTrend(Double trend) {

       /*if (trend == null) return;
        ImageView iv = (ImageView) findViewById(R.id.trendImageView);
        if (iv == null)
            return;
        if (trend >= 1)
            iv.setImageResource(R.drawable.up);
        else if (trend <= -1)
            iv.setImageResource(R.drawable.down);
        else
            iv.setImageResource(R.drawable.flat);
            */
    }
    public void setTemperature(String temperature) {

        if (temperature == null) return;
        TextView tv = (TextView) findViewById(R.id.temperatureTextView);
        if (tv != null)
            tv.setText(temperature);
    }
    public void setPressure(String pressure) {

        if (pressure == null) return;
        TextView tv = (TextView) findViewById(R.id.pressureTextView);
        if (tv != null)
            tv.setText(pressure);
    }
    public void setHumidity(String humidity) {

        if (humidity == null) return;
        TextView tv = (TextView) findViewById(R.id.humidityTextView);
        if (tv != null)
            tv.setText(humidity);
    }
    public void setRainRate(String rainrate) {

        /*if (rainrate == null) return;
        TextView tv = (TextView) findViewById(R.id.rainrateTextView);
        if (tv != null)
            tv.setText(rainrate);*/
    }
    public void setDate(String date) {

        if (date == null) return;
        TextView tv = (TextView) findViewById(R.id.dateTextView);
        if (tv != null)
            tv.setText(date);
    }
}
