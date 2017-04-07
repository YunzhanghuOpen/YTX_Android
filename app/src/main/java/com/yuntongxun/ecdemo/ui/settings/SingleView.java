package com.yuntongxun.ecdemo.ui.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;

public class SingleView extends LinearLayout implements Checkable {

	private TextView mText;
	private CheckBox mCheckBox;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SingleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SingleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public SingleView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context){
		// ��䲼��
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.item_single_layout, this, true);
		mText = (TextView) v.findViewById(R.id.title);
		mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
	}

	@Override
	public void setChecked(boolean checked) {
		mCheckBox.setChecked(checked);
		
	}

	@Override
	public boolean isChecked() {
		return mCheckBox.isChecked();
	}

	@Override
	public void toggle() {
		mCheckBox.toggle();
	}
	
	public void setTitle(String text){
		mText.setText(text);
	}
}
