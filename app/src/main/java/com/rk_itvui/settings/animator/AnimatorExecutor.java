//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rk_itvui.settings.animator;

import com.rk_itvui.utils.BaseFocusView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


public class AnimatorExecutor {
    private AnimatorFocusView focusView;
    protected long mAnimatorDuration;
    private int animatorDuration = 250;
    private int animatorDurationTemporary = -1;
    private float fL;
    private float fT;
    private int fW;
    private int fH;
    private float endL;
    private float endT;
    private int endW;
    private int endH;
    private ObjectAnimator transAnimatorX;
    private ObjectAnimator transAnimatorY;
    private ObjectAnimator scaleX;
    private ObjectAnimator scaleY;
    private AnimatorSet animatorSet = new AnimatorSet();
    private FloatEvaluator floatEvaluator;
    private Interpolator decelerateInterpolator = new LinearInterpolator();
    private BaseFocusView.OnFocusMoveEndListener onFocusMoveEndListener;
    private View changeFocusView;

    public AnimatorExecutor(AnimatorFocusView focusView) {
        if(focusView == null) {
            throw new NullPointerException("focusView not is null");
        } else {
            this.focusView = focusView;
        }
    }

    public void layout(float l, float t, int w, int h) {
        this.fL = this.endL;
        this.fT = this.endT;
        this.fW = this.endW;
        this.fH = this.endH;
        this.endL = l;
        this.endT = t;
        this.endW = w;
        this.endH = h;
        if(this.animatorSet.isStarted()) {
            this.animatorSet.cancel();
        }

        this.focusView.setX(this.endL);
        this.focusView.setY(this.endT);
        this.focusView.setWidth(this.endW);
        this.focusView.setHeight(this.endH);
    }

    public void move(float l, float t, int w, int h) {
        this.fL = this.endL;
        this.fT = this.endT;
        this.fW = this.endW;
        this.fH = this.endH;
        this.endL = l;
        this.endT = t;
        this.endW = w;
        this.endH = h;
        this.startAnimator();
    }

    public void moveX(float scrollerX) {
        if(scrollerX != 0.0F) {
            this.endL += scrollerX;
            this.startAnimator();
        }
    }

    public void moveY(float scrollerY) {
        if(scrollerY != 0.0F) {
            this.endT += scrollerY;
            this.startAnimator();
        }
    }

    private void startAnimator() {
        this.animatorSet.cancel();
        if(this.transAnimatorX == null) {
            this.transAnimatorX = ObjectAnimator.ofFloat(this.focusView, "x", new float[]{this.fL, this.endL});
            this.transAnimatorY = ObjectAnimator.ofFloat(this.focusView, "y", new float[]{this.fT, this.endT});
            this.scaleX = ObjectAnimator.ofInt(this.focusView, "width", new int[]{this.fW, this.endW});
            this.scaleY = ObjectAnimator.ofInt(this.focusView, "height", new int[]{this.fH, this.endH});
            this.floatEvaluator = new FloatEvaluator();
            this.animatorSet.playTogether(new Animator[]{this.transAnimatorX, this.transAnimatorY, this.scaleX, this.scaleY});
            this.animatorSet.addListener(new AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if(AnimatorExecutor.this.focusView.isMoveHideFocus) {
                        AnimatorExecutor.this.focusView.isMoveHideFocus = false;
                        AnimatorExecutor.this.focusView.showFocus();
                    }

                    if(AnimatorExecutor.this.onFocusMoveEndListener != null) {
                        AnimatorExecutor.this.onFocusMoveEndListener.focusEnd(AnimatorExecutor.this.changeFocusView);
                    }

                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            this.transAnimatorX.setEvaluator(this.floatEvaluator);
            this.transAnimatorY.setEvaluator(this.floatEvaluator);
            this.transAnimatorX.setFloatValues(new float[]{this.fL, this.endL});
            this.transAnimatorY.setFloatValues(new float[]{this.fT, this.endT});
            this.scaleX.setIntValues(new int[]{this.fW, this.endW});
            this.scaleY.setIntValues(new int[]{this.fH, this.endH});
        }

        this.animatorSet.setInterpolator(this.decelerateInterpolator);
        if(this.animatorDurationTemporary != -1) {
            this.mAnimatorDuration = (long)this.animatorDurationTemporary;
            this.animatorDurationTemporary = -1;
        } else {
            this.mAnimatorDuration = (long)this.animatorDuration;
        }

        this.animatorSet.setDuration(this.mAnimatorDuration);
        this.animatorSet.start();
    }

    public void setOnFocusMoveEndListener(BaseFocusView.OnFocusMoveEndListener onFocusMoveEndListener, View changeFocusView) {
        this.onFocusMoveEndListener = onFocusMoveEndListener;
        this.changeFocusView = changeFocusView;
    }

    public void setAnimatorDuration(int animatorDuration) {
        this.animatorDuration = animatorDuration;
    }

    public void setAnimatorDurationTemporary(int animatorDurationTemporary) {
        this.animatorDurationTemporary = animatorDurationTemporary;
    }

    public void changeFocusRect(Rect rectBitmap, Rect rectBitmapNew) {
        this.fL = this.endL;
        this.fT = this.endT;
        this.fW = this.endW;
        this.fH = this.endH;
        this.endL -= (float)(rectBitmapNew.left - rectBitmap.left);
        this.endT -= (float)(rectBitmapNew.top - rectBitmap.top);
        this.endW += rectBitmapNew.right - rectBitmap.right + rectBitmapNew.left - rectBitmap.left;
        this.endH += rectBitmapNew.bottom - rectBitmap.bottom + rectBitmapNew.top - rectBitmap.top;
        if(this.animatorSet.isStarted()) {
            this.animatorSet.cancel();
        }

        this.focusView.setX(this.endL);
        this.focusView.setY(this.endT);
        this.focusView.setWidth(this.endW);
        this.focusView.setHeight(this.endH);
    }
}
