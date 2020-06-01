package cn.poco.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class LoginDialogBackground extends View{

	public LoginDialogBackground(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public LoginDialogBackground(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public LoginDialogBackground(Context context) {
		super(context);
		initialize(context);
	}

	private Bitmap mBmpTop = null;
	private Bitmap mBmpBottom = null;
	private Bitmap mBmpMid = null;
	
	protected void initialize(Context context)
	{
//		mBmpTop = BitmapFactory.decodeResource(getResources(), R.drawable.share_favour_top);
//		mBmpBottom = BitmapFactory.decodeResource(getResources(), R.drawable.share_favour_bottom);
//		mBmpMid = BitmapFactory.decodeResource(getResources(), R.drawable.share_favour_fill);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();
		int realHeight = getHeight()-paddingTop-paddingBottom;
		canvas.drawBitmap(mBmpTop, paddingLeft, paddingTop, null);
		canvas.drawBitmap(mBmpBottom, paddingLeft, paddingTop+realHeight-mBmpBottom.getHeight(), null);
		int t = paddingTop+mBmpTop.getHeight();
		int b = paddingTop+realHeight-mBmpBottom.getHeight();
		canvas.drawBitmap(mBmpMid, 
				new Rect(0, 0, mBmpMid.getWidth(), mBmpMid.getHeight()),
				new Rect(paddingLeft, t, paddingLeft+mBmpMid.getWidth(), b),  null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(getPaddingLeft()+mBmpMid.getWidth()+getPaddingRight(), height);
	}
}
