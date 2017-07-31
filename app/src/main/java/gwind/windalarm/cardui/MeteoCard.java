package gwind.windalarm.cardui;

import android.content.Context;
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

    private boolean mDetailsHidden = false;
    private ImageView mDetailsImage;
    private TextView mTitle;
    private RelativeLayout mDetailsLinearLayout;
    private Space mBottomSpace;
    private TextView mSourceUrl;
    private TextView mSpotName;

    public MeteoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {

        mDetailsLinearLayout = (RelativeLayout) findViewById(R.id.detailView);
        mBottomSpace = (Space) findViewById(R.id.bottomspace);
        //mTitle = (TextView) findViewById(R.id.spotNameTextView);
        mSpotName = (TextView) findViewById(R.id.spotNameTextView);
        /*mSpotName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openclose();
            }
        });*/

        mDetailsImage = (ImageView) findViewById(R.id.opencloseImageView);
        mDetailsImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openclose();
            }
        });

        mSourceUrl = (TextView) findViewById(R.id.sourceTextView);

    }

    private void openclose() {
        if (mDetailsHidden) {
            mDetailsImage.setImageResource(R.drawable.close);
            mDetailsHidden = false;
            mDetailsLinearLayout.setVisibility(View.VISIBLE);
            mBottomSpace.setVisibility(View.VISIBLE);

        } else if (!mDetailsHidden) {
            mDetailsImage.setImageResource(R.drawable.open);
            mDetailsHidden = true;
            mDetailsLinearLayout.setVisibility(View.GONE);
            mBottomSpace.setVisibility(View.GONE);
        }
    }

    public void setTitle(String name) {

        if (name == null) return;
        mSpotName.setText(name);
    }

    public void setSourceUrl(String url) {
        if (url == null) return;
        mSourceUrl.setText(url);
    }

    public void setSpeed(String speed) {

        if (speed == null) return;
        TextView tv = (TextView) findViewById(R.id.speedTextView);
        tv.setText(speed);
    }

    public void setSpeed(Double speed) {

        if (speed == null) return;
        WindControl wc = (WindControl) findViewById(R.id.windcontrol);
        wc.setPower(speed);

        TextView tv = (TextView) findViewById(R.id.speedTitleTextView);
        tv.setText(speed.toString());
    }
    public void setDirection(Double angle, String directionSymbol) {

        if (angle == null) return;
        if (directionSymbol == null) return;
        WindControl wc = (WindControl) findViewById(R.id.windcontrol);
        wc.setDirection(angle, directionSymbol);

        TextView tv = (TextView) findViewById(R.id.directiontextView);
        tv.setText(directionSymbol);
    }
    public void setAvSpeed(String avspeed) {

        if (avspeed == null) return;
        TextView tv = (TextView) findViewById(R.id.avspeedTextView);
        tv.setText(avspeed);
    }
    public void setTrend(Double trend) {

        if (trend == null) return;
        ImageView iv = (ImageView) findViewById(R.id.trendImageView);
        if (trend >= 1)
            iv.setImageResource(R.drawable.up);
        else if (trend <= -1)
            iv.setImageResource(R.drawable.down);
        else
            iv.setImageResource(R.drawable.flat);
    }
    public void setTemperature(String temperature) {

        if (temperature == null) return;
        TextView tv = (TextView) findViewById(R.id.temperatureTextView);
        tv.setText(temperature);
    }
    public void setPressure(String pressure) {

        if (pressure == null) return;
        TextView tv = (TextView) findViewById(R.id.pressureTextView);
        tv.setText(pressure);
    }
    public void setHumidity(String humidity) {

        if (humidity == null) return;
        TextView tv = (TextView) findViewById(R.id.humidityTextView);
        tv.setText(humidity);
    }
    public void setRainRate(String rainrate) {

        if (rainrate == null) return;
        TextView tv = (TextView) findViewById(R.id.rainrateTextView);
        tv.setText(rainrate);
    }
    public void setDate(String date) {

        if (date == null) return;
        TextView tv = (TextView) findViewById(R.id.dateTextView);
        tv.setText(date);
    }
}
