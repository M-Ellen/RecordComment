package com.example.recordcomment.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.recordcomment.utils.DensityUtil;

/**
 * @author pzm
 */
public class HorItemDecoration extends RecyclerView.ItemDecoration {
	private int		divHeight;
	private boolean	hasHead;
	private Paint	dividerPaint;
	
	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		int childCount = parent.getChildCount();
		int left = parent.getPaddingLeft();
		int right = parent.getWidth() - parent.getPaddingRight();
		
		for (int i = 0; i < childCount - 1; i++) {
			View view = parent.getChildAt(i);
			float top = view.getBottom();
			float bottom = view.getBottom() + divHeight;
			c.drawRect(left, top, right, bottom, dividerPaint);
		}
	}
	
	public HorItemDecoration(Context context, int divHeight, int colorResId, boolean hasHead) {
		this.divHeight = DensityUtil.dpTopx(context, divHeight);
		this.hasHead = hasHead;
		dividerPaint = new Paint();
		dividerPaint.setColor(context.getResources().getColor(colorResId));
	}
	
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		int position = parent.getChildAdapterPosition(view);
		if (hasHead && position == 0) {
			return;
		}
		outRect.bottom = divHeight;
	}
}
