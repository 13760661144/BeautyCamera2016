package cn.poco.view.beauty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;

import cn.poco.tianutils.ShareData;

/**
 * 祛痘
 * Created by admin on 2017/2/13.
 */
public class AcneViewEx extends BeautyViewEx
{
	private ShapeEx m_acneTool;
	protected float m_acneToolDefR;
	protected float m_acneToolR; //大小为实际屏幕显示尺寸
	protected ControlCallback mCB;

	public int def_color = 0xffffffff;
	public int def_stroke_width = 2;
	private int m_sonWinRadius;
	private int m_sonWinOffset;
	private int m_sonWinBorder;
	private int m_sonWinX = 0;
	private int m_sonWinY = 0;
	private Bitmap m_SonWinBmp;
	private float magnifyX, magnifyY; // 用于平移放大镜的视野

	private boolean isDrawAcneTool = false;
	protected static final long DISPLAY_DURATION = 500;
	protected long m_displayTime; //调整大小时显示圆圈的结束时刻

	protected int def_click_size; //判断为click状态的最大size

	protected boolean mCancelAcne = false;

	PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	public AcneViewEx(Context context, ControlCallback cb)
	{
		super(context);
		mCB = cb;
	}

	@Override
	protected void InitData()
	{
		super.InitData();
		def_img_max_scale = 15.0f;
		m_sonWinRadius = (int) (ShareData.m_screenWidth * 0.145f);
		m_sonWinOffset = ShareData.PxToDpi_xhdpi(10);
		m_sonWinBorder = ShareData.PxToDpi_xhdpi(5);

		int view_w = ShareData.m_screenWidth;
		m_acneToolDefR = (int)(view_w * 0.013f + 0.5f);

		m_acneTool = new ShapeEx();
		m_acneToolR = m_acneToolDefR;

		def_click_size = ShareData.PxToDpi_xhdpi(20);
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();

		mCB = null;
		if(m_SonWinBmp != null && !m_SonWinBmp.isRecycled())
		{
			m_SonWinBmp.recycle();
			m_SonWinBmp = null;
		}
	}

	@Override
	protected void updateContent(int width, int height)
	{
		super.updateContent(width, height);
		mTarget = mInit;
		isDrawAcneTool = false;
		if(mCB != null)
		{
			mCB.OnViewSizeChange();
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if(isDrawAcneTool)
		{
			canvas.save();
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setColor(def_color);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(def_stroke_width);
			canvas.drawCircle(m_acneTool.mCenter.x, m_acneTool.mCenter.y, m_acneToolR, mPaint);
			canvas.restore();

			// 放大镜
			drawMagnify();
		}

		if(img != null && !isDrawAcneTool && m_displayTime > System.currentTimeMillis())
		{
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(def_color);
			mPaint.setStrokeWidth(def_stroke_width);
			canvas.drawCircle(getWidth()/2f, getHeight()/2f, m_acneToolR, mPaint);
			invalidate();
		}
	}

	private void drawMagnify()
	{
		int size = m_sonWinRadius * 2;

		if (m_SonWinBmp == null)
		{
			m_SonWinBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(m_SonWinBmp);
		canvas.setDrawFilter(temp_filter);

		//清理
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);

		//draw mask
		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(0xffffffff);
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		canvas.drawRoundRect(new RectF(m_sonWinOffset, m_sonWinOffset, size - m_sonWinOffset, size - m_sonWinOffset), m_sonWinBorder << 1, m_sonWinBorder << 1, mPaint);

		// draw img
		canvas.save();
		canvas.translate(mCanvasX,mCanvasY);
		canvas.concat(global.m_matrix);
		mPaint.reset();
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		Matrix temp_matrix = new Matrix();

		float[] src = new float[]{size/2f, size/2f, magnifyX, magnifyY};
		float[] dst = new float[src.length];
		Matrix[] matrices = new Matrix[]{global.m_matrix};
		invMatrixCount(dst,src,matrices);
		temp_matrix.set(img.m_matrix);
		temp_matrix.postTranslate(dst[0] - dst[2],dst[1] - dst[3]);
		canvas.drawBitmap(img.m_bmp,temp_matrix,mPaint);
		canvas.restore();

		// draw 圆
		canvas.save();
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(def_color);
		mPaint.setStrokeWidth(def_stroke_width);
		canvas.drawCircle(size / 2f, size / 2f, m_acneToolR, mPaint);
		canvas.restore();

		if(mCB != null)
		{
			mCB.UpdateSonWin(m_SonWinBmp,m_sonWinX,m_sonWinY);
		}

		canvas.setBitmap(null);
	}

	protected void RefreshSonWinPos(float x, float y)
	{
		int size = m_sonWinRadius * 2;
		if (x < size && y < size) {
			m_sonWinX = getWidth() - size;
			m_sonWinY = 0;
		} else if (x > getWidth() - size && y < size) {
			m_sonWinX = 0;
			m_sonWinY = 0;
		}
	}

	/**
	 * 检查点是否在 img 里面
	 * @param points 需要判断的点
	 */
	protected void FixPoint(float[] points, float centerX, float centerY)
	{
		RectF curImgRect = getCurImgShowRect();
		float imgL = curImgRect.left - centerX;
		float imgT = curImgRect.top - centerY;
		float imgR = curImgRect.right - centerX;
		float imgB = curImgRect.bottom - centerY;

		int count = points.length;

		for(int i=0;i<count;i+=2)
		{
			if(points[i] < imgL)
			{
				points[i] = imgL;
			}
			else if(points[i] > imgR)
			{
				points[i] = imgR;
			}

			if(points[i+1] < imgT)
			{
				points[i+1] = imgT;
			}
			else if(points[i+1] > imgB)
			{
				points[i+1] = imgB;
			}
		}
	}

	protected boolean isClickImage(PointF pointF)
	{
		if(pointF.x == m_acneTool.mCenter.x && pointF.y == m_acneTool.mCenter.y)
		{
			return true;
		}
		return false;
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		// 画祛痘tool
		isDrawAcneTool = true;
		mTarget = mInit;
		magnifyX = mDownX;
		magnifyY = mDownY;
		float[] point = new float[]{mDownX, mDownY};
		FixPoint(point,0,0);
		magnifyX = point[0];
		magnifyY = point[1];
		updateAcneToolPos(magnifyX, magnifyY);
		RefreshSonWinPos(mDownX, mDownY);
		invalidate();

		if(mCB != null)
		{
			int count = event.getPointerCount();
			mCB.OnFingerDown(count);
		}
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		if (mCancelAcne)
		{
			return;
		}
		float[] point = new float[]{event.getX(), event.getY()};
		FixPoint(point,0,0);
		magnifyX = point[0];
		magnifyY = point[1];
		updateAcneToolPos(magnifyX, magnifyY);
		RefreshSonWinPos(event.getX(),event.getY());

		this.invalidate();
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		if (mCancelAcne)
		{
			mCancelAcne = false;
			isDrawAcneTool = false;
			invalidate();
			return;
		}

		if (isClickImage(new PointF(mUpX, mUpY)) && isAnimFinish && mCB != null)
		{
			PointF pointF = new PointF(m_acneTool.mCenter.x, m_acneTool.mCenter.y);
			GetFaceLogicPos(pointF, pointF);
			RectF curImgRect = getCurImgShowRect();
			mCB.OnTouchAcne(pointF.x, pointF.y, m_acneToolR / curImgRect.width());
		}

		if(mCB != null)
		{
			m_sonWinX = 0;
			m_sonWinY = 0;
			mCB.UpdateSonWin(null,m_sonWinX,m_sonWinY);

			mCB.OnFingerUp();
		}

		isDrawAcneTool = false;
		invalidate();
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		mCancelAcne = true;
		isDrawAcneTool = false;
		super.EvenDown(event);
		if(mCB != null)
		{
			m_sonWinX = 0;
			m_sonWinY = 0;
			mCB.UpdateSonWin(null,m_sonWinX,m_sonWinY);

			int count = event.getPointerCount();
			mCB.OnFingerDown(count);
		}
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		isDrawAcneTool = false;
		super.EvenUp(event);
	}

	public void SetAcneToolRScale(float scale)
	{
		m_acneToolR = scale * m_acneToolDefR;
		if (m_acneToolR < 1) {
			m_acneToolR = 1;
		}
		m_displayTime = System.currentTimeMillis() + DISPLAY_DURATION;
		invalidate();
	}

	/**
	 * 更新工具位置
	 */
	private void updateAcneToolPos(float x, float y)
	{
		m_acneTool.mCenter.set(x,y);
	}

	public interface ControlCallback
	{
		/**
		 * @param x  逻辑坐标
		 * @param y  逻辑坐标
		 * @param rw 圈的比例大小(r/w)
		 */
		void OnTouchAcne(float x, float y, float rw);

		void UpdateSonWin(Bitmap bmp, int x, int y);

		void OnFingerDown(int count);

		void OnFingerUp();

		void OnViewSizeChange(); // 改变 view 大小时的回调
	}

	public static class ShapeEx extends Shape
	{
		private PointF mCenter = new PointF();
	}
}