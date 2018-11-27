package com.g.laurent.backtobike.Utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.g.laurent.backtobike.R;

public class UtilsAnim {

    // slide the view from its current position to below itself
    public static void slideUp(View panel, LinearLayout centralArea, LinearLayout buttonsLeft, LinearLayout buttonsRight, ImageView arrow, Context context){

        AlphaAnimation anim = new AlphaAnimation(0f, 1.0f);
        anim.setDuration(1000);

        centralArea.setVisibility(View.VISIBLE);
        centralArea.startAnimation(anim);
        buttonsLeft.setVisibility(View.VISIBLE);
        buttonsLeft.startAnimation(anim);
        buttonsRight.setVisibility(View.VISIBLE);
        buttonsRight.startAnimation(anim);

        arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_keyboard_arrow_down_white_18));

        ObjectAnimator animation = ObjectAnimator.ofFloat(panel, "translationY", 0);
        animation.setDuration(1000);
        animation.start();
    }

    // slide the view from below itself to the current position
    public static void slideDown(View panel, Boolean panelExpanded, RelativeLayout middleLayout, LinearLayout centralArea, LinearLayout buttonsLeft, LinearLayout buttonsRight, ImageView arrow, Context context){

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0f);
        anim.setDuration(1000);

        centralArea.startAnimation(anim);
        centralArea.setVisibility(View.INVISIBLE);
        buttonsLeft.startAnimation(anim);
        buttonsLeft.setVisibility(View.INVISIBLE);
        buttonsRight.startAnimation(anim);
        buttonsRight.setVisibility(View.INVISIBLE);

        arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_keyboard_arrow_up_white_18));

        float height;

        if(9*panel.getHeight()/10 > middleLayout.getHeight())
            height = middleLayout.getHeight();
        else
            height = 9*panel.getHeight()/10;

        ObjectAnimator animation = ObjectAnimator.ofFloat(panel, "translationY",   !panelExpanded ? (height) : 0);
        animation.setDuration(1000);
        animation.start();
    }
}
