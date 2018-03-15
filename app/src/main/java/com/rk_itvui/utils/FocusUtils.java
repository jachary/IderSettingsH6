//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rk_itvui.utils;

import com.rk_itvui.settings.animator.AnimatorFocusView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public class FocusUtils extends AFocusUtils {
    private ViewGroup root;
    private AnimatorFocusView focusView;
    private Context context;

    public FocusUtils(Context context, View actLayout, int bitmapRes) {
        this(context, actLayout, bitmapRes, bitmapRes);
    }

    public FocusUtils(Context context, View actLayout, int bitmapRes, boolean isInitMoveHideFocus) {
        this(context, actLayout, bitmapRes, bitmapRes, isInitMoveHideFocus);
    }

    public FocusUtils(Context context, View actLayout, int bitmapRes, int bitmapResTwo) {
        this(context, actLayout, bitmapRes, bitmapResTwo, true);
    }

    public FocusUtils(Context context, View actLayout, int bitmapRes, int bitmapResTwo, boolean isInitMoveHideFocus) {
        super(context, actLayout, bitmapRes, bitmapResTwo, isInitMoveHideFocus);
        this.context = context;
        this.initFocusView(actLayout, bitmapRes, bitmapResTwo, isInitMoveHideFocus);
    }

    public void initFocusView(View actLayout, int bitmapRes, int bitmapResTwo, boolean isInitMoveHideFocus) {
        this.focusView = new AnimatorFocusView(this.context);
        this.focusView.initFocusBitmapRes(bitmapRes, bitmapResTwo, isInitMoveHideFocus);
        this.focusView.setFocusable(false);
        this.focusView.setClickable(false);
        if(actLayout instanceof ViewGroup) {
            this.root = (ViewGroup)actLayout;
            this.root.addView(this.focusView);
        }

        this.focusView.setFocusLayout(-50.0F, -50.0F, -50.0F, -50.0F);
    }

    public void setFocusLayout(float l, float t, float r, float b) {
        this.focusView.setFocusLayout(l, t, r, b);
    }

    public void setFocusLayout(View view, boolean isScalable, float scale) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if(isScalable) {
            float pL = ((float)view.getWidth() * scale - (float)view.getWidth()) / 3.0F;
            float pT = ((float)view.getHeight() * scale - (float)view.getHeight()) / 3.0F;
            this.focusView.setFocusLayout((float)((int)((float)location[0] - pL)), (float)((int)((float)location[1] - pT)), (float)((int)((float)(location[0] + view.getWidth()) + pL)), (float)((int)((float)(location[1] + view.getHeight()) + pT)));
        } else {
            this.focusView.setFocusLayout((float)location[0], (float)location[1], (float)(location[0] + view.getWidth()), (float)(location[1] + view.getHeight()));
        }

    }

    public void setFocusLayout(View view, Rect clipRect, boolean isScalable, float scale) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if(isScalable) {
            float pL = ((float)view.getWidth() * scale - (float)view.getWidth()) / 3.0F;
            float pT = ((float)view.getHeight() * scale - (float)view.getHeight()) / 3.0F;
            this.focusView.setFocusLayout((float)((int)((float)location[0] - pL + (float)clipRect.left)), (float)((int)((float)location[1] - pT + (float)clipRect.top)), (float)((int)((float)(location[0] + view.getWidth()) + pL - (float)clipRect.right)), (float)((int)((float)(location[1] + view.getHeight()) + pT - (float)clipRect.bottom)));
        } else {
            this.focusView.setFocusLayout((float)(location[0] + clipRect.left), (float)(location[1] + clipRect.top), (float)(location[0] + view.getWidth() - clipRect.right), (float)(location[1] + view.getHeight() - clipRect.bottom));
        }

    }

    public void startMoveFocus(float l, float t, float r, float b) {
        this.focusView.focusMove(l, t, r, b);
    }

    public void startMoveFocus(View view, boolean isScalable, float scale) {
        this.startMoveFocus(view, (Rect)null, isScalable, scale, 0.0F, 0.0F);
    }

    public void startMoveFocus(View view, Rect clipRect, boolean isScalable, float scale) {
        this.startMoveFocus(view, clipRect, isScalable, scale, 0.0F, 0.0F);
    }

    public void startMoveFocus(View view, boolean isScalable, float scale, float scrollerX, float scrollerY) {
        this.startMoveFocus(view, (Rect)null, isScalable, scale, scrollerX, scrollerY);
    }

    public void startMoveFocus(View view, Rect clipRect, boolean isScalable, float scale, float scrollerX, float scrollerY) {
        if(view != null) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            int[] offset = new int[2];
            this.root.getLocationInWindow(offset);
            int vWidth = view.getWidth();
            float xScaleOff = ((float)vWidth * view.getScaleX() - (float)vWidth) / 3.0F;
            int vHeight = view.getHeight();
            float yScaleOff = ((float)vHeight * view.getScaleY() - (float)vHeight) / 3.0F;
            int left = (int)((float)(location[0] - offset[0]) + xScaleOff + 0.49F);
            int top = (int)((float)(location[1] - offset[1]) + yScaleOff + 0.49F);
            float pL;
            float pT;
            if(clipRect == null) {
                if(isScalable) {
                    pL = ((float)view.getWidth() * scale - (float)view.getWidth()) / 3.0F;
                    pT = ((float)view.getHeight() * scale - (float)view.getHeight()) / 3.0F; 
                    if(view.getWidth()==0){
                    	 this.focusView.focusMove(104,145,310,382);
                    }
                    else{
                    this.focusView.focusMove((float)((int)((float)left - pL + scrollerX)), 
                    		(float)((int)((float)top - pT + scrollerY)), 
                    		(float)((int)((float)(left + view.getWidth()) + pL + scrollerX)), 
                    		(float)((int)((float)(top + view.getHeight()) + pT + scrollerY)));
                    }
                } else {
                    this.focusView.focusMove((float)left + scrollerX, (float)top + scrollerY, (float)(left + view.getWidth()) + scrollerX, (float)(top + view.getHeight()) + scrollerY);
                }
            } else if(isScalable) {
            	
                pL = ((float)view.getWidth() * scale - (float)view.getWidth()) / 3.0F;
                pT = ((float)view.getHeight() * scale - (float)view.getHeight()) / 3.0F;
                this.focusView.focusMove((float)((int)((float)left - pL + (float)clipRect.left + scrollerX)), (float)((int)((float)top - pT + (float)clipRect.top + scrollerY)), (float)((int)((float)(left + view.getWidth()) + pL - (float)clipRect.right + scrollerX)), (float)((int)((float)(top + view.getHeight()) + pT - (float)clipRect.bottom + scrollerY)));
            } else {
                this.focusView.focusMove((float)(left + clipRect.left) + scrollerX, (float)(top + clipRect.top) + scrollerY, (float)(left + view.getWidth() - clipRect.right) + scrollerX, (float)(top + view.getHeight() - clipRect.bottom) + scrollerY);
            }

        }
    }

    public void scrollerFocusX(float scrollerX) {
        this.focusView.scrollerFocusX(scrollerX);
    }

    public void scrollerFocusY(float scrollerY) {
        this.focusView.scrollerFocusY(scrollerY);
    }

    public void hideFocusForStartMove(long delayMillis) {
        this.focusView.hideFocus();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                FocusUtils.this.focusView.showFocus();
            }
        }, delayMillis);
    }

    public void hideFocus() {
        this.focusView.hideFocus();
    }

    public void showFocus() {
        this.focusView.showFocus();
    }

    public void changeBitmapForTopView() {
        this.focusView.setBitmapForTop();
    }

    public void clearBitMapForTopView() {
        this.focusView.clearBitmapForTop();
    }

    public void setFocusBitmap(int resId) {
        this.focusView.setFocusBitmap(resId);
    }

    public void setMoveVelocity(int movingNumberDefault, int movingVelocityDefault) {
        this.focusView.setMoveVelocity(movingNumberDefault, movingVelocityDefault);
    }

    public void setMoveVelocityTemporary(int movingNumberTemporary, int movingVelocityTemporary) {
        this.focusView.setMoveVelocityTemporary(movingNumberTemporary, movingVelocityTemporary);
    }

    public void setOnFocusMoveEndListener(BaseFocusView.OnFocusMoveEndListener onFocusMoveEndListener, View changeFocusView) {
        this.focusView.setOnFocusMoveEndListener(onFocusMoveEndListener, changeFocusView);
    }

    public View getFocusView() {
        return this.focusView;
    }

    public void pause() {
        this.focusView.pause();
    }

    public void resume() {
        this.focusView.resume();
    }
}
