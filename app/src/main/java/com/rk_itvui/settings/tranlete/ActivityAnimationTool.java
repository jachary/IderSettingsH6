package com.rk_itvui.settings.tranlete;

import android.app.Activity;
import android.content.Intent;
import android.view.animation.DecelerateInterpolator;

public class ActivityAnimationTool {
   // public static BaseEffect mEffect;

    public static void init(BaseEffect effect) {
       // mEffect = effect;
    }

    public static void startActivity(Activity currActivity, Intent intent) {

        // Preparing the bitmaps that we need to show
     //   mEffect.prepare(currActivity);
        currActivity.startActivity(intent);
     //   currActivity.overridePendingTransition(0, 0);
    }
    public static void startActivityForResult(Activity currActivity, Intent intent,int result) {

        // Preparing the bitmaps that we need to show
     //   mEffect.prepare(currActivity);
        currActivity.startActivityForResult(intent,result);
       // currActivity.overridePendingTransition(0, 0);
    }
    public static void animate(final Activity destActivity, final int duration) {
       // mEffect.animate(destActivity, duration);
    }

    public static void cancel(final Activity destActivity) {
       // mEffect.cancel();
        //mEffect.clean(destActivity);
    }

    public static void prepareAnimation(final Activity destActivity) {
      //  mEffect.prepareAnimation(destActivity);
    }

}
