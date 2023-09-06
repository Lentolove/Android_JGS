package com.tsp.learn;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by zhuguangwen on 14/11/19.
 * email 979343670@qq.com
 */
public class IMMHelper {

    public static void hideSoftInput(Context mContext, IBinder windowToken){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showSoftInput(EditText mEditText, Context mContext){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!mEditText.isFocused()) {
            mEditText.requestFocus();
        }
        if (imm != null) imm.showSoftInput(mEditText, 0);
    }

    public static void showSoftInput(EditText mEditText){
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!mEditText.isFocused()) {
            mEditText.requestFocus();
        }
        if (imm != null) imm.showSoftInput(mEditText, 0);
    }

    public static void showSoftInputDelay(final EditText mEditText, long delay){
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!mEditText.isFocused()) {
                    mEditText.requestFocus();
                }
                if (imm != null) {
                    imm.showSoftInput(mEditText, 0);
                }
            }
        }, delay);
    }

}
