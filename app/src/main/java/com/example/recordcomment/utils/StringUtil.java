package com.example.recordcomment.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class StringUtil {
	
	public static boolean isNull(String str) {
		if (str != null && !TextUtils.isEmpty(str.trim())) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isNullOrEmpty(String str) {
		if (str != null && !TextUtils.isEmpty(str.trim()) && !"null".equals(str)) {
			return false;
		}
		
		return true;
		
	}
	
	public static boolean isContainsChinese(String str) {
		if (str == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}
	

	// 删除字符串中的空格
	public static String removeSpaceString(String str) {
		String spaceStr = "";
		if (str == null || TextUtils.isEmpty(str)) {
			return "";
		}
		String[] strArray = str.split(" ");
		if (strArray != null && strArray.length > 0) {
			for (int i = 0; i < strArray.length; i++) {
				spaceStr = spaceStr + strArray[i];
			}
			spaceStr = spaceStr.replace(" ", "");
		}
		return spaceStr;
	}
}
