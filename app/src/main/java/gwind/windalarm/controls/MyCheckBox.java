package gwind.windalarm.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import gwind.windalarm.R;

/**
 * Created by Giacomo Spanò on 19/07/2016.
 */
public class MyCheckBox extends CheckBox {



    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setButtonDrawable(new StateListDrawable());
    }
    @Override
    public void setChecked(boolean t){
        if(t)
        {
            this.setBackgroundResource(R.drawable.checkboxselect);
        }
        else
        {
            this.setBackgroundResource(R.drawable.checkboxdeselect);
        }
        super.setChecked(t);
    }
}
