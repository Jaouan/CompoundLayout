package com.jaouan.compoundlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

/**
 * Radio layout with a gradient foreground.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class GradientRadioLayout extends RadioLayout {

    /**
     * First color.
     */
    private int mColorA;

    /**
     * Second color.
     */
    private int mColorB;

    /**
     * Gradient angle in degrees.
     */
    private double mDegreesAngle;

    /**
     * Foreground gradien layout.
     */
    private FrameLayout mForegroundLayout;

    /**
     * View bounds' hypot.
     */
    private int mSideHypot;

    public GradientRadioLayout(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public GradientRadioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public GradientRadioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    public GradientRadioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Initialize view.
     *
     * @param context      Context.
     * @param attrs        Attributes.
     * @param defStyleAttr Def styles attributes.
     * @param defStyleRes  Def style ressources.
     */
    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // - Check API version.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.e("GradientRadioLayout", "GradientRadioLayout is only Android 21+ compatible. The view will crash !");
        }

        // - Initialize widget from XML attributes.
        final TypedArray styleAttributes = context.obtainStyledAttributes(
                attrs, R.styleable.GradientRadioLayout, defStyleAttr, defStyleRes);
        mColorA = styleAttributes.getColor(R.styleable.GradientRadioLayout_colorA, getResources().getColor(R.color.color_a_default));
        mColorB = styleAttributes.getColor(R.styleable.GradientRadioLayout_colorB, getResources().getColor(R.color.color_b_default));
        mDegreesAngle = styleAttributes.getInt(R.styleable.GradientRadioLayout_angle, 0);
        styleAttributes.recycle();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        updateGradientParameters();
    }

    @Override
    public void setChecked(final boolean checked) {
        final boolean lastCheckedState = isChecked();
        super.setChecked(checked);

        // If foreground layout exists and checked state really changed, then animate it.
        if (mForegroundLayout != null && lastCheckedState != isChecked()) {
            // Initialize alpha and reveal animation.
            AlphaAnimation alphaAnimation;
            Animator circularReveal;
            if (checked) {
                alphaAnimation = new AlphaAnimation(0, 1);
                circularReveal = ViewAnimationUtils.createCircularReveal(mForegroundLayout, (int) (getWidth() * -.2f), getHeight() / 2, 0, mSideHypot);
            } else {
                alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setStartOffset(100);
                circularReveal = ViewAnimationUtils.createCircularReveal(mForegroundLayout, (int) (getWidth() * 1.2f), getHeight() / 2, mSideHypot, 0);
            }
            alphaAnimation.setDuration(200);
            alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

            circularReveal.setDuration(300);
            circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());

            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mForegroundLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mForegroundLayout.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
                }
            });

            // Start animations.
            circularReveal.start();
            mForegroundLayout.startAnimation(alphaAnimation);
        }
    }

    /**
     * Retrieve first color.
     *
     * @return First color.
     */
    public int getColorA() {
        return mColorA;
    }

    /**
     * Defines first color.
     *
     * @param colorA First color.
     */
    public void setColorA(int colorA) {
        this.mColorA = colorA;
        updateGradientParameters();
    }

    /**
     * Retrieve second color.
     *
     * @return Second color.
     */
    public int getColorB() {
        return mColorB;
    }

    /**
     * Defines second color.
     *
     * @param colorB Second color.
     */
    public void setColorB(int colorB) {
        this.mColorB = colorB;
        updateGradientParameters();
    }

    /**
     * Retrieve gradient angle in degrees.
     *
     * @return Gradient angle in degrees.
     */
    public double getAngle() {
        return mDegreesAngle;
    }

    /**
     * Defines gradient angle in degrees.
     *
     * @param degreesAngle Gradient angle in degrees.
     */
    public void setAngle(double degreesAngle) {
        this.mDegreesAngle = degreesAngle;
        updateGradientParameters();
    }

    /**
     * Update gradient parameters.
     */
    private void updateGradientParameters() {
        // - Initialize gradient.
        mSideHypot = (int) Math.hypot(getWidth(), getHeight());
        ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
        final double radiansAngle = Math.toRadians(mDegreesAngle);
        mDrawable.getPaint().setShader(new LinearGradient(0, 0, (int) (mSideHypot * Math.cos(radiansAngle)), (int) (mSideHypot * Math.sin(radiansAngle)), mColorA, mColorB, Shader.TileMode.REPEAT));

        // - Initialize foreground gradient layout.
        if (mForegroundLayout == null) {
            mForegroundLayout = new FrameLayout(getContext());
            mForegroundLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(mForegroundLayout);
        }
        mForegroundLayout.setVisibility(isChecked() ? View.VISIBLE : View.INVISIBLE);
        mForegroundLayout.setForeground(mDrawable);
    }

}
