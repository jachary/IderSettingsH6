//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rk_itvui.settings.animator;

import com.rk_itvui.utils.BaseFocusView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
@SuppressLint("NewApi") public class AnimatorFocusView extends View implements BaseFocusView {
    private static String TAG = "AnimatorFocusView";
    private AnimatorExecutor animatorExecutor;
    int currentFocusImageRes = -1;
    int imageRes;
    int imageResTow;
    boolean isMoveHideFocus;
    Rect rectBitmap;
    Rect rectBitmapMain;
    Rect rectBitmapTwo;

    public AnimatorFocusView(Context context) {
        super(context);
        this.init();
    }

    public AnimatorFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimatorFocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }


    private void init() {
        this.animatorExecutor = new AnimatorExecutor(this);
        LayoutParams layoutParams = new LayoutParams(100, 100);
        this.setLayoutParams(layoutParams);
    }

    public void initFocusBitmapRes(int imageRes) {
        this.initFocusBitmapRes(imageRes, imageRes);
    }

    public void initFocusBitmapRes(int imageRes, boolean isMoveHideFocus) {
        this.initFocusBitmapRes(imageRes, imageRes, isMoveHideFocus);
    }

    public void initFocusBitmapRes(int imageRes, int imageResTwo, boolean isMoveHideFocus) {
        this.isMoveHideFocus = isMoveHideFocus;
        if(isMoveHideFocus) {
            this.hideFocus();
        }

        this.initFocusBitmapRes(imageRes, imageResTwo);
    }

    public void initFocusBitmapRes(int imageRes, int imageResTwo) {
        this.imageRes = imageRes;
        this.imageResTow = imageResTwo;
        this.rectBitmapMain = new Rect();
        Drawable drawableMain = this.getResources().getDrawable(imageRes);
        if(drawableMain != null) {
            drawableMain.getPadding(this.rectBitmapMain);
        }

        if(imageRes != imageResTwo) {
            this.rectBitmapTwo = new Rect();
            Drawable drawableTwo = this.getResources().getDrawable(imageResTwo);
            if(drawableTwo != null) {
                drawableTwo.getPadding(this.rectBitmapTwo);
            }
        } else {
            this.rectBitmapTwo = this.rectBitmapMain;
        }

        this.rectBitmap = this.rectBitmapMain;
        this.currentFocusImageRes = imageRes;
        this.setBackgroundResource(this.currentFocusImageRes);
    }

    public void setBitmapForTop() {
        this.animatorExecutor.changeFocusRect(this.rectBitmap, this.rectBitmapTwo);
        this.rectBitmap = this.rectBitmapTwo;
        this.currentFocusImageRes = this.imageResTow;
        this.setBackgroundResource(this.currentFocusImageRes);
    }

    public void clearBitmapForTop() {
        this.animatorExecutor.changeFocusRect(this.rectBitmap, this.rectBitmapMain);
        this.rectBitmap = this.rectBitmapMain;
        this.currentFocusImageRes = this.imageRes;
        this.setBackgroundResource(this.currentFocusImageRes);
    }

    public void setFocusBitmap(int resId) {
        try {
            Rect e = new Rect();
            Drawable drawableNew = this.getResources().getDrawable(resId);
            if(drawableNew != null) {
                drawableNew.getPadding(e);
            }

            this.animatorExecutor.changeFocusRect(this.rectBitmap, e);
            this.rectBitmap = e;
        } catch (Exception var4) {
            throw new RuntimeException("resId == null ?");
        }

        this.currentFocusImageRes = resId;
        this.setBackgroundResource(this.currentFocusImageRes);
    }

    public void setFocusLayout(float l, float t, float r, float b) {
        float ll = l - (float)this.rectBitmap.left;
        float tt = t - (float)this.rectBitmap.top;
        float rr = r + (float)this.rectBitmap.right;
        float bb = b + (float)this.rectBitmap.bottom;
        this.animatorExecutor.layout(ll, tt, (int)(rr - ll), (int)(bb - tt));
    }

    public void focusMove(float l, float t, float r, float b) {
//    	Log.i("zzz", "***="+l+"**"+t+"***"+r+"***"+b);
        float ll = l - (float)this.rectBitmap.left;
        float tt = t - (float)this.rectBitmap.top;
        float rr = r + (float)this.rectBitmap.right;
        float bb = b + (float)this.rectBitmap.bottom;
        this.animatorExecutor.move(ll, tt, (int)(rr - ll ), (int)(bb - tt));
    }

    public void scrollerFocusX(float scrollerX) {
        this.animatorExecutor.moveX(scrollerX);
    }

    public void scrollerFocusY(float scrollerY) {
        this.animatorExecutor.moveY(scrollerY);
    }

    public void hideFocus() {
        this.setVisibility(4);
    }

    public void showFocus() {
        this.setVisibility(0);
    }

    public void setMoveVelocity(int movingNumberDefault, int movingVelocityDefault) {
        this.animatorExecutor.setAnimatorDuration(movingVelocityDefault);
    }

    public void setMoveVelocityTemporary(int movingNumberTemporary, int movingVelocityTemporary) {
        this.animatorExecutor.setAnimatorDurationTemporary(movingVelocityTemporary);
    }

    public void setOnFocusMoveEndListener(OnFocusMoveEndListener onFocusMoveEndListener, View changeFocusView) {
        this.animatorExecutor.setOnFocusMoveEndListener(onFocusMoveEndListener, changeFocusView);
    }

    public void setWidth(int width) {
        if(this.getLayoutParams().width != width) {
            this.getLayoutParams().width = width;
            this.requestLayout();
        }

    }

    public void setHeight(int height) {
        if(this.getLayoutParams().height != height) {
            this.getLayoutParams().height = height;
            this.requestLayout();
        }

    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        Drawable wrappedDrawable = drawable.mutate();//DrawableCompat.wrap(drawable.mutate());
        wrappedDrawable.setAlpha(102);
        return wrappedDrawable;
    }

    public void pause() {
        Drawable focusDrawable = this.getBackground();
        if(focusDrawable != null) {
            this.setBackgroundDrawable(tintDrawable(focusDrawable, ColorStateList.valueOf(-7829368)));
        }

    }

    public void resume() {
        if(this.currentFocusImageRes != -1) {
            this.setBackground(this.getResources().getDrawable(this.currentFocusImageRes));
        }

    }
}
