package wind.newwindalarm.cardui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;
import android.widget.TextView;

import wind.newwindalarm.R;


/**
 * Created by giacomo on 01/03/14.
 */
public class AlarmCardSubitem extends GridLayout {
    public AlarmCardSubitem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AlarmCardSubitem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmCardSubitem(Context context) {
        super(context);
    }

    public void setDescription(String description) {
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText(description);
    }
}
