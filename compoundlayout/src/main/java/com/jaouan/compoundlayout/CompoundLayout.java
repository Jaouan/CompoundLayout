package com.jaouan.compoundlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.ViewDebug;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * Checkable view group.
 */
public class CompoundLayout extends FrameLayout implements Checkable {

    /**
     * Checked state.
     */
    private boolean mChecked;

    /**
     * Listener.
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;

    /**
     * Widget listener.
     */
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;


    /**
     * Checked state attribute.
     */
    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    /**
     * Broadcasting state to avoid infinite recursions if setChecked() is called from a listener
     */
    private boolean mBroadcasting;

    public CompoundLayout(Context context) {
        this(context, null);
    }

    public CompoundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CompoundLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // - Initialize from XML attributes.
        final TypedArray styleAttributes = context.obtainStyledAttributes(
                attrs, R.styleable.CompoundLayout, defStyleAttr, defStyleRes);
        final boolean checked = styleAttributes.getBoolean(
                R.styleable.CompoundLayout_checked, false);
        setChecked(checked);
        styleAttributes.recycle();

        // - Set clickable.
        setClickable(true);
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    public void setChecked(final boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
            }

            mBroadcasting = false;
        }
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
    
    /**
     * Defines on checked change callback.
     *
     * @param onCheckedChangeListener On checked change callback.
     */
    public void setOnCheckedChangeListener(final OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    /**
     * Defines on checked change widget callback.
     *
     * @param onCheckedChangeWidgetListener On checked change widget callback.
     */
    void setOnCheckedChangeWidgetListener(final OnCheckedChangeListener onCheckedChangeWidgetListener) {
        this.mOnCheckedChangeWidgetListener = onCheckedChangeWidgetListener;
    }

    /**
     * Saved state.
     */
    static class SavedState extends BaseSavedState {
        boolean checked;

        /**
         * Constructor called from {@link CompoundLayout#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        @Override
        public String toString() {
            return "CompoundLayout.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.checked = isChecked();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    /**
     * On checked change callback interface.
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundLayout compoundLayout, boolean checked);
    }

}
