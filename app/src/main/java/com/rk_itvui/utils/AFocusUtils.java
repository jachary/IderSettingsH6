//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rk_itvui.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

public abstract class AFocusUtils {
    public AFocusUtils(Context context, View actLayout, int bitmapRes) {
    }

    public AFocusUtils(Context context, View actLayout, int bitmapRes, boolean isInitMoveHideFocus) {
    }

    public AFocusUtils(Context context, View actLayout, int bitmapRes, int bitmapResTwo) {
    }

    public AFocusUtils(Context context, View actLayout, int bitmapRes, int bitmapResTwo, boolean isInitMoveHideFocus) {
    }

    public abstract void initFocusView(View var1, int var2, int var3, boolean var4);

    public abstract void setFocusLayout(float var1, float var2, float var3, float var4);

    public abstract void setFocusLayout(View var1, boolean var2, float var3);

    public abstract void setFocusLayout(View var1, Rect var2, boolean var3, float var4);

    public abstract void startMoveFocus(float var1, float var2, float var3, float var4);

    public abstract void startMoveFocus(View var1, boolean var2, float var3);

    public abstract void startMoveFocus(View var1, Rect var2, boolean var3, float var4);

    public abstract void startMoveFocus(View var1, boolean var2, float var3, float var4, float var5);

    public abstract void startMoveFocus(View var1, Rect var2, boolean var3, float var4, float var5, float var6);

    public abstract void scrollerFocusX(float var1);

    public abstract void scrollerFocusY(float var1);

    public abstract void hideFocusForStartMove(long var1);

    public abstract void hideFocus();

    public abstract void showFocus();

    public abstract void changeBitmapForTopView();

    public abstract void clearBitMapForTopView();

    public abstract void setFocusBitmap(int var1);

    public abstract void setMoveVelocity(int var1, int var2);

    public abstract void setMoveVelocityTemporary(int var1, int var2);

    public abstract void setOnFocusMoveEndListener(BaseFocusView.OnFocusMoveEndListener var1, View var2);

    public abstract View getFocusView();

    public abstract void pause();

    public abstract void resume();
}
