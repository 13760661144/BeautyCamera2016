package cn.poco.login;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PickerCtrl extends View{

	public PickerCtrl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public PickerCtrl(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public PickerCtrl(Context context) {
		super(context);
		initialize(context);
	}
	private int mCurrentIndex = 0;
	private float mMaxTextSize = 30;
	private int mMainLineH = 0;
	private int mOtherLineH = 0;
	private float mTextSize = mMaxTextSize;
	private int mLineColor = 0xff000000;
	private int mTextColor = 0xff000000;
	private float mLastDownY = 0;
	private float mMoveLen = 0;
	private float mMoveLenCache = 0;
	private float mMinScale = 0.6f;
	private boolean mIsDown = false;
	private int mScaledMaximumFlingVelocity;
	private VelocityTracker mVTracker = null;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private ArrayList<Item> mItems = new ArrayList<Item>();
	private TimerTask mTask;
	private Timer mTimer = new Timer();
	private Handler mHandler = new Handler();
	private OnSelectListener mOnSelectListener;
	private int mGrivity = Gravity.CENTER;
	private Camera mCamera = new Camera();
	private Matrix mM = new Matrix();
	
	public interface OnSelectListener
	{
		void onSelected(int index, String item);
	}
	
	private void initialize(Context context)
	{
		mPaint.setStyle(Style.FILL);
		mPaint.setTextAlign(Align.CENTER);
		ViewConfiguration  viewCfg = ViewConfiguration.get(context);
		mScaledMaximumFlingVelocity = viewCfg.getScaledMaximumFlingVelocity();
	}
	
	public void setItems(String[] items, int selected)
	{
		if(items != null)
		{
			mItems.clear();
			for(int i = 0; i < items.length; i++)
			{
				Item item = new Item();
				item.text = items[i];
				mItems.add(item);
			}
			mCurrentIndex = selected;
			if(mCurrentIndex < 0){
				mCurrentIndex = 0;
			}
			if(mCurrentIndex >= items.length){
				mCurrentIndex = items.length-1;
			}
			update();
		}
	}
	
	public void setItems(ArrayList<String> items, int selected)
	{
		if(items != null)
		{
			mItems.clear();
			for(int i = 0; i < items.size(); i++)
			{
				Item item = new Item();
				item.text = items.get(i);
				mItems.add(item);
			}
			mCurrentIndex = selected;
			if(mCurrentIndex < 0){
				mCurrentIndex = 0;
			}
			if(mCurrentIndex >= items.size()){
				mCurrentIndex = items.size()-1;
			}
			update();
		}
	}
	
	public void setData(int index, Object data)
	{
		if(index >= 0 && index < mItems.size())
		{
			mItems.get(index).data = data;
		}
	}
	
	public Object getData(int index)
	{
		if(index >= 0 && index < mItems.size())
		{
			return mItems.get(index).data;
		}
		return null;
	}
	
	public String getItem(int index)
	{
		if(index >= 0 && index < mItems.size())
		{
			return mItems.get(index).text;
		}
		return null;
	}
	
	public int getSel()
	{
		return mCurrentIndex;
	}
	
	public void setSel(int index)
	{
		mCurrentIndex = index;
		if(mMainLineH > 0){
			updateItemPos();
		}
	}
	
	public void setLineColor(int color)
	{
		mLineColor = color;
	}
	
	public void setTextSize(int size)
	{
		setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
	}
	
	public void setTextSize(int unit, int size)
	{
		Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        mMaxTextSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}
	
	public void setTextColor(int color)
	{
		mTextColor = color;
	}
	
	public void setOnSelectListener(OnSelectListener l)
	{
		mOnSelectListener = l;
	}
	
	public void setGrivity(int grivity)
	{
		mGrivity = grivity;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		if(w == 0 || h == 0 || mItems.size() == 0)
		{
			return;
		}
		
		Item item = null;
		
		float yCenter = h/2;
		
		int size = mItems.size();
		
		//计算高度
		for(int i = 0; i < size; i++)
		{
			item = mItems.get(i);
			item.scale = mMinScale + Math.max((1-mMinScale)*(mOtherLineH-Math.abs(item.yPos+mMoveLen-yCenter))/mOtherLineH, 0);
			item.height = mMainLineH*item.scale;
		}
		
		//调整位置
		Item last = mItems.get(mCurrentIndex);

		for(int i = mCurrentIndex-1; i >= 0; i--)
		{
			item = mItems.get(i);
			item.yPos = last.yPos - last.height/2 - item.height/2;
			last = item;
		}
		last = mItems.get(mCurrentIndex);
		for(int i = mCurrentIndex+1; i < size; i++)
		{
			item = mItems.get(i);
			item.yPos = last.yPos + last.height/2 + item.height/2;
			last = item;
		}
		
		//绘制文本
		mPaint.setColor(mTextColor);
		FontMetricsInt fmi = null;
		float x =0;
		if(mGrivity == Gravity.LEFT)
		{
			mPaint.setTextAlign(Align.LEFT);
			x = 0;
		}
		else if(mGrivity == Gravity.CENTER)
		{
			mPaint.setTextAlign(Align.CENTER);
			x = w/2;
		}
		else
		{
			mPaint.setTextAlign(Align.RIGHT);
			x = w;
		}
		for(int i = 0; i < size; i++)
		{
			item = mItems.get(i);
			if(item.yPos+mMoveLen+item.height/2 >= 0 && item.yPos+mMoveLen-item.height/2 <= h)
			{
				mPaint.setAlpha((int)(255*Math.max((yCenter-Math.abs(item.yPos+mMoveLen-yCenter))/yCenter, 0.0f)));
				mPaint.setTextSize(mTextSize*item.scale);
				fmi = mPaint.getFontMetricsInt();
				item.baseline = (float) (item.yPos + mMoveLen - (fmi.bottom / 2.0 + fmi.top / 2.0));


				mCamera.save();
				float rate = 1 - (yCenter-Math.abs(item.yPos+mMoveLen-yCenter))/yCenter;
				if(item.yPos+mMoveLen-yCenter >= 0)
				{
					mCamera.rotateX(-15 * rate);
				}
				else
				{
					mCamera.rotateX(15 * rate);
				}
				mCamera.getMatrix(mM);
				mM.preTranslate(-w / 2, -h / 2);
				mM.postTranslate(w / 2, h / 2);
				mCamera.restore();

				canvas.save();
				canvas.concat(mM);
				canvas.drawText(item.text, x, item.baseline, mPaint);
				canvas.restore();
			}
		}
		
		mPaint.setAlpha(255);
		mPaint.setColor(mLineColor);
		canvas.drawLine(0, yCenter-mMainLineH/2, w, yCenter-mMainLineH/2, mPaint);
		canvas.drawLine(0, yCenter+mMainLineH/2, w, yCenter+mMainLineH/2, mPaint);
	}
	
	private void updateItemPos()
	{
		int w = getWidth();
		int h = getHeight();
		if(w == 0 || h == 0 || mItems.size() == 0)
		{
			return;
		}
		int size = mItems.size();
		
		Item item = null;
		
		float yCenter = h/2;
		
		item = mItems.get(mCurrentIndex);
		item.yPos = yCenter;
		item.scale = 1;
		item.height = mMainLineH;

		int baseDegree = 5;

		Item last = mItems.get(mCurrentIndex);
		for(int i = mCurrentIndex-1; i >= 0; i--)
		{
			item = mItems.get(i);
			item.height = mOtherLineH;
			item.yPos = last.yPos - last.height/2 - item.height/2;
			item.scale = mMinScale;
			baseDegree += 3;
			last = item;
		}

		baseDegree = -5;
		last = mItems.get(mCurrentIndex);
		for(int i = mCurrentIndex+1; i < size; i++)
		{
			item = mItems.get(i);
			item.height = mOtherLineH;
			item.yPos = last.yPos + last.height/2 + item.height/2;
			item.scale = mMinScale;
			baseDegree -= 3;
			last = item;
		}
		invalidate();
	}
	
	private int getCurSel(float offsetY)
	{
		int w = getWidth();
		int h = getHeight();
		if(w == 0 || h == 0)
		{
			return -1;
		}
		
		Item item = null;
		
		float yCenter = h/2;
		int size = mItems.size();
		
		float min = 0x00ffffff;
		int centerIndex = -1;
		
		//计算高度
		for(int i = 0; i < size; i++)
		{
			item = mItems.get(i);
			float offset = Math.abs(item.yPos+offsetY-yCenter);
			if(offset < min){
				min = offset;
				centerIndex = i;
			}
		}
		return centerIndex;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(mItems.size() <= 1){
			return super.dispatchTouchEvent(event);
		}
		if(mVTracker != null)
			mVTracker.addMovement(event);
		switch (event.getActionMasked())
		{
		case MotionEvent.ACTION_DOWN:
			doDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			doMove(event);
			break;
		case MotionEvent.ACTION_UP:
			doUp(event);
			break;
		}
		return true;
	}
	
	private void doDown(MotionEvent event)
	{
		mLastDownY = event.getY();
		mIsDown = true;
		if(mVTracker != null)
			mVTracker.clear();
		stopMove();
	}

	private void doMove(MotionEvent event)
	{
		mMoveLenCache += (event.getY() - mLastDownY);

		Item firstItem = mItems.get(0);
		Item lastItem = mItems.get(mItems.size()-1);
		if(firstItem.yPos+mMoveLenCache-firstItem.height/2 > getHeight()/2-mMainLineH/2)
		{
			mMoveLenCache = getHeight()/2-mMainLineH/2-firstItem.yPos+firstItem.height/2;
		}
		if(lastItem.yPos+mMoveLenCache+lastItem.height/2 < getHeight()/2+mMainLineH/2)
		{
			mMoveLenCache = getHeight()/2+mMainLineH/2-lastItem.yPos-lastItem.height/2;
		}
		
		mMoveLen = mMoveLenCache;
//		mCurrentIndex = getCurSel(mMoveLen);
		
		mLastDownY = event.getY();
		
		invalidate();
	}

	private void doUp(MotionEvent event)
	{
		if(mVTracker != null){
			mVTracker.computeCurrentVelocity(100, mScaledMaximumFlingVelocity);
			mMoveLenCache += mVTracker.getYVelocity();
			Item firstItem = mItems.get(0);
			Item lastItem = mItems.get(mItems.size()-1);
			if(firstItem.yPos+mMoveLenCache-firstItem.height/2 > getHeight()/2-mMainLineH/2)
			{
				mMoveLenCache = getHeight()/2-mMainLineH/2-firstItem.yPos+firstItem.height/2;
			}
			if(lastItem.yPos+mMoveLenCache+lastItem.height/2 < getHeight()/2+mMainLineH/2)
			{
				mMoveLenCache = getHeight()/2+mMainLineH/2-lastItem.yPos-lastItem.height/2;
			}
		}
		mIsDown = false;
		startMove();
	}
	
	private void startMove()
	{
		if(mTask == null)
		{
			mTask = new AnimTimer();
			mTimer.schedule(mTask, 0, 20);
		}
	}
	
	private void stopMove()
	{
		if(mTask != null)
		{
			mTask.cancel();
			mTask = null;
		}
	}
	
	private class AnimTimer extends TimerTask
	{

		@Override
		public void run() {
			mHandler.post(mTimerRunnable);
		}
	}
	
	private Runnable mTimerRunnable = new Runnable()
	{

		@Override
		public void run() {
			mMoveLen += (mMoveLenCache-mMoveLen)*0.2f;
			if (Math.abs(mMoveLenCache-mMoveLen) < 4)
			{
				mMoveLen = mMoveLenCache;
				if(mIsDown == false)
				{
					int sel = getCurSel(mMoveLen);
					if(sel != -1)
					{
						Item item = mItems.get(sel);
						if(item.yPos + mMoveLen != getHeight()/2)
						{
							mMoveLenCache = mMoveLen - (item.yPos + mMoveLen - getHeight()/2);
						}
						else
						{
							if(mCurrentIndex != sel && mOnSelectListener != null){
								mOnSelectListener.onSelected(sel, item.text);
							}
							mCurrentIndex = sel;
							updateItemPos();
							stopMove();
							mMoveLenCache = 0;
							mMoveLen = 0;
						}
					}
				}
			}
			invalidate();
		}
		
	};
	
	@Override
	protected void onAttachedToWindow() {
		if(mVTracker == null){
			mVTracker = VelocityTracker.obtain();
		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		if(mVTracker != null){
			mVTracker.recycle();
			mVTracker = null;
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		update();
	}

	private void update()
	{
		int w = getWidth();
		int h = getHeight();
		if(w == 0 || h == 0){
			return;
		}
		
		mTextSize = mMaxTextSize;
		
		mMainLineH = (int)(mTextSize*2.0f);
		mOtherLineH = (int)(mMainLineH*mMinScale);
		
		updateItemPos();
	}
	
	private class Item
	{
		public String text;
		public float height;
		public float scale;
		public float yPos;
		public Object data;
		public float baseline;
	}
}
