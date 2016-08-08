package wind.newwindalarm.cardui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import wind.newwindalarm.R;



/**
 * Created by giacomo on 28/02/14.
 */
public class AlarmCard extends LinearLayout {

    private String Id;
    //private String SpotName;

    public AlarmCard(Context context) {
        super(context);
    }

    public AlarmCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSpotName(String name) {
        TextView tv = (TextView) findViewById(R.id.spot);
        tv.setText(name);
    }
    public void setTitle(String title) {
        TextView tv = (TextView) findViewById(R.id.title);
        tv.setText(title);
    }
    public void setDescription(String description) {
        TextView tv = (TextView) findViewById(R.id.descritption);
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
