package wind.newwindalarm.chart;

import com.github.mikephil.charting.components.AxisBase;

/**
 * Created by Giacomo Spanò on 14/08/2016.
 */
public class DirectionAxisValueFormatter  /*implements IAxisValueFormatter*/ {

    private String[] mValues;

    public DirectionAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    //@Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value];
    }

    /** this is only needed if numbers are returned, else return 0 */
    //@Override
    public int getDecimalDigits() { return 0; }
}