//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rk_itvui.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import java.util.HashMap;
import java.util.Map;

public class FocusScaleUtils {
    private View oldView;
    private int durationLarge;
    private int durationSmall;
    private float scale;
    private AnimatorSet animatorSet;
    private Interpolator interpolatorLarge;
    private Interpolator interpolatorSmall;
    private Map<View, Animator> animatorSetMap;

    public FocusScaleUtils() {
        this.durationLarge = 300;
        this.durationSmall = 500;
        this.scale = 0.1F;
        this.interpolatorLarge = new AccelerateInterpolator(1.5F);
        this.interpolatorSmall = new DecelerateInterpolator(1.5F);
        this.init();
    }

    public FocusScaleUtils(int duration, float scale, Interpolator interpolator) {
        this(duration, duration, scale, interpolator, interpolator);
    }

    public FocusScaleUtils(int durationLarge, int durationSmall, float scale, Interpolator interpolatorLarge, Interpolator interpolatorSmall) {
        this.durationLarge = 300;
        this.durationSmall = 500;
        this.scale = 1.2F;
        this.durationLarge = durationLarge;
        this.durationSmall = durationSmall;
        this.scale = scale;
        this.interpolatorLarge = interpolatorLarge;
        this.interpolatorSmall = interpolatorSmall;
        this.init();
    }

    private void init() {
        this.animatorSetMap = new HashMap();
    }

    public void scaleToLarge(View item) {
//        if(item.isFocused()) {
//            this.scaleToLargeNotFocus(item);
//        }
    }

    public void scaleToLargeNotFocus(View item) {
        Animator animatorOld = (Animator)this.animatorSetMap.get(item);
        if(animatorOld != null && animatorOld.isRunning()) {
            animatorOld.cancel();
        }

        this.animatorSet = new AnimatorSet();
        ObjectAnimator largeX = ObjectAnimator.ofFloat(item, "ScaleX", new float[]{1.0F, this.scale});
        ObjectAnimator largeY = ObjectAnimator.ofFloat(item, "ScaleY", new float[]{1.0F, this.scale});
        this.animatorSet.setDuration((long)this.durationLarge);
        this.animatorSet.setInterpolator(this.interpolatorLarge);
        this.animatorSet.play(largeX).with(largeY);
        this.animatorSet.start();
        this.oldView = item;
    }

    public void scaleToNormal(final View item) {
        if(this.animatorSet != null && item != null) {
            if(this.animatorSet.isRunning()) {
                this.animatorSet.cancel();
            }

            AnimatorSet animatorSetForNormal = new AnimatorSet();
            ObjectAnimator oa = ObjectAnimator.ofFloat(item, "ScaleX", new float[]{1.0F});
            ObjectAnimator oa2 = ObjectAnimator.ofFloat(item, "ScaleY", new float[]{1.0F});
            animatorSetForNormal.setDuration((long)this.durationSmall);
            animatorSetForNormal.setInterpolator(this.interpolatorSmall);
            animatorSetForNormal.play(oa).with(oa2);
            animatorSetForNormal.addListener(new AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                    FocusScaleUtils.this.animatorSetMap.put(item, animation);
                }

                public void onAnimationEnd(Animator animation) {
                    FocusScaleUtils.this.animatorSetMap.remove(item);
                }

                public void onAnimationCancel(Animator animation) {
                    FocusScaleUtils.this.animatorSetMap.remove(item);
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            animatorSetForNormal.start();
            this.oldView = null;
        }
    }

    public void scaleToNormal() {
        this.scaleToNormal(this.oldView);
    }
}
