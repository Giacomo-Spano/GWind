package gwind.windalarm.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import gwind.windalarm.R;


/**
 * Created by giacomo on 28/02/14.
 */
public class WindIndicator extends LinearLayout {

    View[] coloredbar = new View[11];
    private String[] windcolors = {"#9600FE","#0032FE","#0064FE","#0096FE","#00C8FE","#25C192","#00FA00","#FEE100","#FE9600","#DC4A1D","#AA001D","#FE0096"};
    private int[] windvalues = {0,4,8,12,16,19,23,27,31,35,39};
    private double speed = 20;

    /*OnListener mCallback;
    public interface OnListener {
        void onClick(long spotId);
    }*/



    public WindIndicator(Context context) {
        super(context);
        inflate(context, R.layout.windindicator, this);
        setWillNotDraw(false);
    }

    public WindIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.windindicator, this);
        setWillNotDraw(false);
    }

    public WindIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.windindicator, this);
        setWillNotDraw(false);
    }

    public void setWindSpeed(Double speed, String unit) {
        this.speed = speed;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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
            if (speed >= windvalues[i])
                coloredbar[i].setBackgroundColor(Color.parseColor(windcolors[i]));
            else
                coloredbar[i].setBackgroundColor(Color.WHITE);
        }

        //drawArrow(canvas,mAngle,speed);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        //this.height = h;
        //this.width = h;//w;
        super.onSizeChanged(w, h, oldw, oldh);
    }
}