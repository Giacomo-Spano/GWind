package wind.newwindalarm.cardui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import wind.newwindalarm.R;
import wind.newwindalarm.controls.TouchImageView;

/**
 * Created by giacomo on 28/02/14.
 */
public class InfoCard extends LinearLayout {

    private TextView titleTextView;
    private TextView valueTextView;
    private TextView descriptionTextView;
    private ImageView imageView;

    public InfoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        imageView = (ImageView) findViewById(R.id.imageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        valueTextView = (TextView) findViewById(R.id.valueTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
    }

    public void setTitle(String title) {

        if (title == null || titleTextView == null) return;
            titleTextView.setText(title);
    }

    public void setValue(String value) {

        if (value == null || valueTextView == null) return;
            valueTextView.setText(value);
    }

    public void setDescription(String value) {

        if (value == null || valueTextView == null) return;
        descriptionTextView.setText(value);
    }

    public void setImageView(int imageResource) {

        imageView.setImageResource(imageResource);
    }

}
