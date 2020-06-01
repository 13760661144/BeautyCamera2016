package cn.poco.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;

import static cn.poco.tianutils.SimpleHorizontalListView.GetMyMeasureSpec;

/**
 * 最底层只是封装变换的方法
 * Created by admin on 2017/1/11.
 */

public abstract class BaseView extends View
{
	protected float mDownX;
	protected float mDownY;
	protected float mUpX;
	protected float mUpY;
	protected float mMoveX;
	protected float mMoveY;


	protected float mDownX1;
	protected float mDownY1;
	protected float mDownX2;
	protected float mDownY2;

	protected float mGammaX; //移动
	protected float mGammaY;
	protected float mDelta; //放大
	protected float mBeta; //旋转

	protected int view_w;
	protected int view_h;
	protected Matrix mOldMatrix = new Matrix();
	protected Matrix m_temp_global_matrix = new Matrix();

	protected boolean GlobalChange = false;// 发生触摸事件
	protected boolean mLockUI = false;

	protected Shape global, img, frame, mTarget, mInit;
	protected Paint mPaint;

	protected float def_img_max_scale = 2.5f; //img_size / view_size 最大比例
	protected float def_img_min_scale = 0.5f; //img_size / view_size 最小比例

	protected ControlCallback m_cb = null;

	protected boolean mCancelTouch = false;

	public BaseView(Context context)
	{
		super(context);
		ShareData.InitData(context);
		InitData();
	}

	protected void InitData()
	{
		global = new Shape();

		img = new Shape();

		frame = new Shape();

		mTarget = new Shape();// 用于变换

		mInit = new Shape();// 初始化不做处理

		mPaint = new Paint();
	}

	/**
	 * 添加img的图片
	 * @param bitmap
	 */
	public void setImage(Bitmap bitmap)
	{
		if(bitmap != null && !bitmap.isRecycled()){
			img.m_bmp = bitmap;
		}
		this.invalidate();
	}

	/**
	 * 获取当前显示的图片
	 * @return Bitmap对象
	 */
	public Bitmap getImage()
	{
		return img.m_bmp;
	}

	/**
	 * 获取当前图片的高度
	 * @return 图片高度
	 */
	public float getImgHeight()
	{
		float[] values = new float[9];
		img.m_matrix.getValues(values);
		return img.m_bmp.getHeight() * values[4];
	}

	public void LockUI(boolean isLockUI)
	{
		if(isLockUI)
		{
			mTarget = mInit;
			mLockUI = true;
		}
		else
		{
			mLockUI = false;
		}
	}

	public boolean isLockUI()
	{
		if(mLockUI)
		{
			return true;
		}
		return false;
	}

	public void setControlCallback(ControlCallback callback)
	{
		m_cb = callback;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		widthMeasureSpec = GetMyMeasureSpec(0, widthMeasureSpec);
		heightMeasureSpec = GetMyMeasureSpec(0, heightMeasureSpec);
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(w, h);
		updateContent(w,h);
	}

	/**
	 * 让view可以整体缩放
	 */
	protected void syncScaling()
	{
		if(!GlobalChange)
		{
			GlobalChange = true;
		}
		view_w = getWidth();
		view_h = getHeight();
		if(GlobalChange){
			m_temp_global_matrix.set(global.m_matrix);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(!mLockUI)
		{
			switch(event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_MOVE:
				{
					if(event.getPointerCount() > 1)
					{
						EvenMove(event);
					}
					else
					{
						mMoveX = event.getX();
						mMoveY = event.getY();
						OddMove(event);
					}
					break;
				}

				case MotionEvent.ACTION_DOWN:
				{
					mCancelTouch = false;
					mDownX = event.getX();
					mDownY = event.getY();
					GlobalChange = true;
					OddDown(event);
					break;
				}

				case MotionEvent.ACTION_UP:
				{
					mUpX = event.getX();
					mUpY = event.getY();
					OddUp(event);
					break;
				}

				case MotionEvent.ACTION_POINTER_DOWN:
				{
					mDownX1 = event.getX(0);
					mDownY1 = event.getY(0);
					mDownX2 = event.getX(1);
					mDownY2 = event.getY(1);
					GlobalChange = true;
					EvenDown(event);
					break;
				}

				case MotionEvent.ACTION_POINTER_UP:
				{
					EvenUp(event);
					break;
				}

				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE:
				{
					mCancelTouch = true;
					if(event.getPointerCount() >= 2)
					{
						EvenUp(event);
					}
					else if(event.getPointerCount() == 1)
					{
						OddUp(event);
					}
					break;
				}
			}
			syncScaling();
		}
		return true;
	}

	public boolean IsCancelTouch()
	{
		return mCancelTouch;
	}

	/**
	 * 初始化移动
	 */
	protected void Init_M_Data(float x, float y)
	{
		mGammaX = x;
		mGammaY = y;
	}

	protected void Init_M_Data(Shape target, float x, float y)
	{
		mOldMatrix.set(target.m_matrix);
		Init_M_Data(x, y);
	}

	/**
	 * 子元件移动
	 */
	protected void Run_M(Matrix matrix, float x, float y)
	{
		matrix.postTranslate(x - mGammaX, y - mGammaY);
	}

	protected void Run_M(Shape target, float x, float y)
	{
		target.m_matrix.set(mOldMatrix);
		Run_M(target.m_matrix, x, y);
	}

	/**
	 * 初始化旋转
	 */
	protected void Init_R_Data(float x1, float y1, float x2, float y2)
	{
		if(x1 - x2 == 0)
		{
			if(y1 >= y2)
			{
				mBeta = 90;
			}
			else
			{
				mBeta = -90;
			}
		}
		else if(y1 - y2 != 0)
		{
			mBeta = (float)Math.toDegrees(Math.atan(((double)(y1 - y2)) / (x1 - x2)));
			if(x1 < x2)
			{
				mBeta += 180;
			}
		}
		else
		{
			if(x1 >= x2)
			{
				mBeta = 0;
			}
			else
			{
				mBeta = 180;
			}
		}
	}

	/**
	 * 旋转
	 */
	protected void Run_R(Shape target, float x1, float y1, float x2, float y2)
	{
		float tempAngle;
		if(x1 - x2 == 0)
		{
			if(y1 >= y2)
			{
				tempAngle = 90;
			}
			else
			{
				tempAngle = -90;
			}
		}
		else if(y1 - y2 != 0)
		{
			tempAngle = (float)Math.toDegrees(Math.atan(((double)(y1 - y2)) / (x1 - x2)));
			if(x1 < x2)
			{
				tempAngle += 180;
			}
		}
		else
		{
			if(x1 >= x2)
			{
				tempAngle = 0;
			}
			else
			{
				tempAngle = 180;
			}
		}
		target.m_matrix.postRotate(tempAngle - mBeta, (mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
	}

	/**
	 * 初始化缩放
	 * (引起误差的原因是按钮不是在交点的上,放大的时候空隙也放大了)
	 */
	protected void Init_Z_Data(float x1, float y1, float x2, float y2)
	{
		mDelta = ImageUtils.Spacing(x1 - x2, y1 - y2);
	}

	/**
	 * 缩放
	 */
	protected void Run_Z(Shape target, float x1, float y1, float x2, float y2)
	{
		float tempDist = ImageUtils.Spacing(x1 - x2, y1 - y2);
		if(tempDist > 10)
		{
			float scale = tempDist / mDelta;
			target.m_matrix.postScale(scale, scale, (mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
		}
	}

	/**
	 * 初始化移动旋转放大
	 */
	protected void Init_MRZ_Data(Shape target, float x1, float y1, float x2, float y2)
	{
		mOldMatrix.set(target.m_matrix);
		Init_R_Data(x1, y1, x2, y2);
		Init_Z_Data(x1, y1, x2, y2);
		Init_M_Data((x1 + x2) / 2f, (y1 + y2) / 2f);
	}

	/**
	 * 子元件的移动旋转放大
	 */
	protected void Run_MRZ(Shape target, float x1, float y1, float x2, float y2)
	{
		target.m_matrix.set(mOldMatrix);
		Run_R(target, x1, y1, x2, y2);
		Run_Z(target, x1, y1, x2, y2);
		Run_M(target.m_matrix, (x1 + x2) / 2f, (y1 + y2) / 2f);
	}

	public void ReleaseMem()
	{
		if(img != null && img.m_bmp != null)
		{
			img.m_matrix = null;
			img.m_bmp = null;
		}

		if(frame != null && frame.m_bmp != null)
		{
			frame.m_matrix = null;
			frame.m_bmp = null;
		}

		if(mTarget != null && mTarget.m_bmp != null)
		{
			mTarget.m_matrix = null;
			mTarget.m_bmp = null;
		}

		m_cb = null;
	}

	/**
	 * 计算点的坐标在哪个矩阵变换范围内
	 * @param pts 坐标
	 * @return 在某个matrix范围内，返回这个Matrix的shape对象集合
	 */
	protected Shape getShowMatrix(float...pts)
	{
		return img;
	}

	protected abstract void updateContent(int width, int height);

	protected abstract void OddDown(MotionEvent event);

	protected abstract void OddMove(MotionEvent event);

	protected abstract void OddUp(MotionEvent event);

	protected abstract void EvenDown(MotionEvent event);

	protected abstract void EvenMove(MotionEvent event);

	protected abstract void EvenUp(MotionEvent event);

	public static interface ControlCallback
	{
		Bitmap MakeShowImg(Object info, int frW, int frH);

		Bitmap MakeOutputImg(Object info, int outW, int outH);

		Bitmap MakeShowFrame(Object info, int frW, int frH);

		Bitmap MakeOutputFrame(Object info, int outW, int outH);

		Bitmap MakeShowBK(Object info, int frW, int frH);

		Bitmap MakeOutputBK(Object info, int outW, int outH);

		Bitmap MakeShowPendant(Object info, int frW, int frH);

		Bitmap MakeOutputPendant(Object info, int outW, int outH);
	}

	public static class Shape
	{
		public Matrix m_matrix = new Matrix();
		public Bitmap m_bmp;
		public Object m_ex;
		public Object m_info;

		public Matrix CloneMatrix()
		{
			Matrix temp = new Matrix();
			temp.set(m_matrix);
			return temp;
		}
	}
}