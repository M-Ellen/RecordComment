package com.example.recordcomment.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class CommonUtils {
	public static void showSoftInput(Context context, View view) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		manager.showSoftInput(view, 0);
	}
	
	public static void hideSoftInput(Context context, View view) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
