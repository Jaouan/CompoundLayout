package com.jaouan.compoundlayout;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Radio layout. It's like a RadioButton, but it's a layout.
 */
public class RadioLayout extends CompoundLayout {

    public RadioLayout(Context context) {
        super(context);
    }

    public RadioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RadioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked()) {
            super.toggle();
        }
    }

}
