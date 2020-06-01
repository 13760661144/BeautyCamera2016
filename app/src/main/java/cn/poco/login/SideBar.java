package cn.poco.login;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.tianutils.ShareData;

public class SideBar extends View {
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private Drawable mBgDrawable;
	private int mTextColor=0xff000000;
//	private int mTouchColor=0x7fffffff;
	public static String[] b = {"★", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z"};
	private int choose = -1;
	private Paint paint = new Paint();

//	private TextView mTextDialog;

//	public void setTextView(TextView mTextDialog) {
//		this.mTextDialog = mTextDialog;
//	}


	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mBgDrawable=getRoundRectShapeDrawable(30,0x4ffffff);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBgDrawable=getRoundRectShapeDrawable(30,0x4fffffff);
	}

	public SideBar(Context context) {
		super(context);
		mBgDrawable=getRoundRectShapeDrawable(30,0x4fffffff);
	}


	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight();
		int width = getWidth();
		int singleHeight = (height- ShareData.PxToDpi_xhdpi(20)) / b.length;

		for (int i = 0; i < b.length; i++) {
			paint.setColor(mTextColor);
			// paint.setColor(Color.WHITE);
//			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
//			paint.setTextSize(15.5f);
			paint.setTextSize(ShareData.PxToDpi_xhdpi(18));
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
//			if (mTextDialog != null) {
//				mTextDialog.setVisibility(View.INVISIBLE);
//			}
			break;

		default:
			setBackgroundColor(0x4fffffff);
			setBackgroundDrawable(mBgDrawable);
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(b[c]);
					}
//					if (mTextDialog != null) {
//						mTextDialog.setText(b[c]);
//						mTextDialog.setVisibility(View.VISIBLE);
//					}
					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}

	public void setTextColor(int color){
		mTextColor=color;
	}

	/**背景图*/
	private Drawable getRoundRectShapeDrawable(float degree,int color){
		RoundRectShape roundRectShape=new RoundRectShape(new float[]{degree,degree,degree,degree,degree,degree,degree,degree},null,null);
		ShapeDrawable shape = new ShapeDrawable(roundRectShape);
		shape.getPaint().setColor(color);
		shape.getPaint().setStyle(Paint.Style.FILL);
		return shape;
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}


	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}