package com.example.lightflashdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class CustomGridView extends GridView{

	private Context context;

	public CustomGridView(Context context, AttributeSet attrs,
                          int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public CustomGridView(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
		        MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
