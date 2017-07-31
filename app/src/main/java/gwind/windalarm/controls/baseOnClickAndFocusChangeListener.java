package gwind.windalarm.controls;

import android.view.View;

/**
 * Created by giacomo on 17/02/14.
 */
public class baseOnClickAndFocusChangeListener extends myOnClickListener implements View.OnFocusChangeListener {

    public baseOnClickAndFocusChangeListener() {
    }

    @Override
    public void onFocusChange(View view, boolean focus) {
        if (focus) {
            showPicker(view);
        }
    }

    @Override
    public void onClick(View view) {
        showPicker(view);
    }

    public void showPicker(View view) {
    }
}





