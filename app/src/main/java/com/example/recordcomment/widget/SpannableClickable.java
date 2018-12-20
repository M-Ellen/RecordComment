package com.example.recordcomment.widget;


import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.example.recordcomment.R;

public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {
	/**
	 * text颜色
	 */
	private int textColor;
	private int bgColor;

	public SpannableClickable(Context context) {
		this.textColor = context.getResources().getColor(R.color.colorPrimary);
		this.bgColor = context.getResources().getColor(R.color.gray);
	}
	
	public SpannableClickable(int textColor) {
		this.textColor = textColor;
	}

	/**
	 * 重写该方法
	 * 1.设置字体颜色，在构造函数可以自定义 传入
	 * 2.取消下划线
	 * 3.清除背景图层
	 * @param ds
	 */
	@Override
	public void updateDrawState(TextPaint ds) {
		super.updateDrawState(ds);
		ds.setColor(textColor);
		ds.setUnderlineText(false);
//		ds.bgColor = bgColor;
		ds.clearShadowLayer();
	}
}
