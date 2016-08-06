package wind.newwindalarm.cardui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import wind.newwindalarm.R;
import wind.newwindalarm.controls.WindControl;

/**
 * Created by giacomo on 28/02/14.
 */
public class MeteoCard extends LinearLayout {

    private String Id;
    private boolean mDetailsHidden = false;
    private ImageView mDetailsImage;
    private TextView mTitle;
    private RelativeLayout mDetailsLinearLayout;
    private Space mBottomSpace;
    private TextView mSourceUrl;

    public MeteoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {

        mDetailsLinearLayout = (RelativeLayout) findViewById(R.id.detailView);
        mBottomSpace = (Space) findViewById(R.id.bottomspace);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openclose();
            }
        });

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

    public void setTitle(String title) {

        if (title == null) return;
        mTitle.setText(title);
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




    public void setDescription(String description) {
        TextView tv = (TextView) findViewById(R.id.title);
        tv.setText(description);
    }

    public void setID(String Id) {
        this.Id = Id;
    }
    public String getID() {
        return Id;
    }

    public void setImageTextUp(String text) {
        TextView tv = (TextView) findViewById(R.id.imagetextup);
        tv.setText(text);
    }
    public void setImageTextDown(String text) {
        TextView tv = (TextView) findViewById(R.id.imagetextdown);
        tv.setText(text);
    }

    public void setStatusText(String text) {
        TextView tv = (TextView) findViewById(R.id.statusText);
        tv.setText(text);
    }

    public void setLabelImage(int resId) {
        ImageView iv = (ImageView) findViewById(R.id.labelImage);
        iv.setImageResource(resId);
    }
    public void addLine(String line) {// todo eliminare??
        TextView valueTV = new TextView(getContext());
        valueTV.setText(line);
        valueTV.setId(5);
        valueTV.setLayoutParams(new ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        addView(valueTV);
    }
    public void addSeparator() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.cardsubitem_layout, this, false);

        LinearLayout view = new LinearLayout(getContext());

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.height = 5;
        view.setLayoutParams(lp);
        view.setBackgroundResource(R.drawable.card_separator);

        addView(view);
    }
    public AlarmCardSubitem addSubItem(String text) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        AlarmCardSubitem v = (AlarmCardSubitem) inflater.inflate(R.layout.cardsubitem_layout, this, false);
        addView(v);

        v.setDescription(text);
        return v;
    }

}
