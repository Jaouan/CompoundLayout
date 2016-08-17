package com.jaouan.compoundlayout.example;

import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jaouan.compoundlayout.CompoundLayout;

public class MainActivity extends AppCompatActivity {

    private TextView subtitleTextView;

    private View descriptionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subtitleTextView = (TextView) findViewById(R.id.subtitle);
        descriptionLayout = findViewById(R.id.description_layout);

        bindCompoundListener((CompoundLayout) findViewById(R.id.profile_1), R.string.audrey_hepburn);
        bindCompoundListener((CompoundLayout) findViewById(R.id.profile_2), R.string.doris_day);
        bindCompoundListener((CompoundLayout) findViewById(R.id.profile_3), R.string.grace_kelly);
        bindCompoundListener((CompoundLayout) findViewById(R.id.profile_4), R.string.tippi_hedren);
        bindCompoundListener((CompoundLayout) findViewById(R.id.profile_5), R.string.jaouan);
    }

    /**
     * Bind compound listener.
     *
     * @param compoundLayout Compound layout.
     * @param subtitle       Subtitle to set.
     */
    private void bindCompoundListener(final CompoundLayout compoundLayout, @StringRes final int subtitle) {
        compoundLayout.setOnCheckedChangeListener(new CompoundLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundLayout compoundLayout, boolean checked) {
                if (checked) {
                    final Animation fadeOutAnimation = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out);
                    fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            subtitleTextView.setText(getString(subtitle));
                            descriptionLayout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    descriptionLayout.startAnimation(fadeOutAnimation);
                }
            }
        });
    }

}
