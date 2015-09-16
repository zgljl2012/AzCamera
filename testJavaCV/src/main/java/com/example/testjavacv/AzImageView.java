package com.example.testjavacv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AzImageView extends ImageView{
	
	private int rx;
	private int ry;
	private int rWidth;
	private int rHeight;
	
	private boolean ifDraw = false;
	
	public AzImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AzImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	private void init(){
		setRx(0);
		setRy(0);
		setRWidth(0);
		setRHeight(0);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		if(ifDraw) {
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			canvas.drawRect(rx, ry, rx+rWidth, ry+rHeight, paint);
		}
	}
	
	public void drawRect(int x, int y, int width, int height) {
		setRx(x);
		setRy(y);
		setRWidth(width);
		setRHeight(height);
		ifDraw = true;
		invalidate();
	}
	
	public int getRx() {
		return rx;
	}

	public void setRx(int rx) {
		this.rx = rx;
	}

	public int getRy() {
		return ry;
	}

	public void setRy(int ry) {
		this.ry = ry;
	}

	public int getRWidth() {
		return rWidth;
	}

	public void setRWidth(int rWidth) {
		this.rWidth = rWidth;
	}

	public int getRHeight() {
		return rHeight;
	}

	public void setRHeight(int rHeight) {
		this.rHeight = rHeight;
	}
	
}
