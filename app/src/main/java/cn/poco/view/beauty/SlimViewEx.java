package cn.poco.view.beauty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;

import cn.poco.imagecore.ProcessorV2;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.view.BaseView;
import my.beautyCamera.R;

/**
 * 瘦脸瘦身
 * Created by admin on 2017/2/13.
 */

/*
注释： MODE_TOOL mTarget 的变化是全局的
		其他模式是img
 */
public class SlimViewEx extends BeautyCommonViewEx
{
	public static final int MODE_MANUAL = 0x1;
	public static final int MODE_AUTO = 0x1 << 1;
	public static final int MODE_TOOL = 0x1 << 2;
	private int mMode = MODE_MANUAL;
	private boolean initPosInNeed = true;

	public int def_color;
	public int def_stroke_width;

	PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	private boolean mIsClick = false;

	public SlimViewEx(Context context)
	{
		super(context);
	}

	@Override
	protected void InitData()
	{
		super.InitData();

		def_slim_tool_ab_btn_res = R.drawable.beauty_slim_view_path_out;
		def_slim_tool_r_btn_res = R.drawable.beauty_slim_view_rotate_out;

		m_slimToolDefR = (int)(ShareData.m_screenWidth * 0.15f);
		m_dragToolDefR = ShareData.PxToDpi_xhdpi(48);
		m_dragToolR = m_dragToolDefR;

		m_sonWinRadius = (int) (ShareData.m_screenWidth * 0.145f);
		m_sonWinOffset = ShareData.PxToDpi_xhdpi(10);
		m_sonWinBorder = ShareData.PxToDpi_xhdpi(5);

		dashedPath = new Path();
		def_stroke_width = ShareData.PxToDpi_xhdpi(2);
		def_color = 0xffffffff;
	}

	@Override
	public float getImgHeight()
	{
		RectF rectF = getCurImgShowRect();
		return rectF.height();
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();
		if(m_SonWinBmp != null && !m_SonWinBmp.isRecycled())
		{
			m_SonWinBmp.recycle();
			m_SonWinBmp = null;
		}

		if(m_slimToolBtnA != null && m_slimToolBtnA.m_bmp != null && !m_slimToolBtnA.m_bmp.isRecycled())
		{
			m_slimToolBtnA.m_matrix = null;
			m_slimToolBtnA.m_bmp.recycle();
			m_slimToolBtnA.m_bmp = null;
			m_slimToolBtnA = null;
		}

		if(m_slimToolBtnB != null && m_slimToolBtnB.m_bmp != null && !m_slimToolBtnB.m_bmp.isRecycled())
		{
			m_slimToolBtnB.m_matrix = null;
			m_slimToolBtnB.m_bmp.recycle();
			m_slimToolBtnB.m_bmp = null;
			m_slimToolBtnB = null;
		}

		if(m_slimToolBtnR != null && m_slimToolBtnR.m_bmp != null && !m_slimToolBtnR.m_bmp.isRecycled())
		{
			m_slimToolBtnR.m_matrix = null;
			m_slimToolBtnR.m_bmp.recycle();
			m_slimToolBtnR.m_bmp = null;
			m_slimToolBtnR = null;
		}
	}

	@Override
	protected BaseView.Shape getShowMatrix(float... pts)
	{
		switch(mMode)
		{
			case MODE_MANUAL:
				return mInit;
			case MODE_TOOL:
				if(m_slimTool != null && IsClickSlimTool(mDownX,mDownY))
				{
					return m_slimTool;
				}

				BaseView.Shape result = mInit;
				int count = pts.length;
				if(count %2 != 0)
				{
					return result;
				}

				Matrix matrix = new Matrix();
				matrix.postTranslate(-mCanvasX, -mCanvasY);
				matrix.mapPoints(pts);
				result = isClickResBtn(m_slimToolBtnA,pts);
				if(result != m_slimToolBtnA)
				{
					result = isClickResBtn(m_slimToolBtnB,pts);
					if(result != m_slimToolBtnB)
					{
						result = isClickResBtn(m_slimToolBtnR,pts);
					}
				}
				return result;
			default:
				return super.getShowMatrix(pts);
		}
	}

	@Override
	protected void updateContent(int width, int height)
	{
		super.updateContent(width, height);
		isDrawManualTool =  false;
		mTarget = mInit;
		if(mCallBack != null)
		{
			((ControlCallback)mCallBack).OnViewSizeChange();
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		DrawToCanvas(canvas);
	}

	private void DrawToCanvas(Canvas canvas)
	{
		switch(mMode)
		{
			case MODE_MANUAL:

				if(isDrawManualTool)
				{
					DrawManualSlimTool(canvas, def_color, m_dragToolX1, m_dragToolY1, m_dragToolX2, m_dragToolY2, m_dragToolR,false);

					// 放大镜
					drawMagnify();
				}
				else if(img != null && m_displayTime > System.currentTimeMillis())
				{
					canvas.save();
					mPaint.reset();
					mPaint.setAntiAlias(true);
					mPaint.setStyle(Paint.Style.STROKE);
					mPaint.setColor(def_color);
					mPaint.setStrokeWidth(def_stroke_width);
					canvas.drawCircle(getWidth()/2f, getHeight()/2f, m_dragToolR, mPaint);
					canvas.restore();
					invalidate();
				}
				break;
			case MODE_TOOL:
				PointF slimToolCenter = GetSlimToolCenterPos();
				DrawSlimTool(canvas, def_color, m_slimToolDefR / 2f, slimToolCenter.x, slimToolCenter.y, m_slimTool.m_centerX * m_slimTool.m_scaleX, m_slimTool.m_degree);
				UpdateToolPos();
				if (m_slimToolBtnA != null) {
					DrawButton(canvas, m_slimToolBtnA);
				}
				if (m_slimToolBtnB != null) {
					DrawButton(canvas, m_slimToolBtnB);
				}
				if (m_slimToolBtnR != null) {
					DrawButton(canvas, m_slimToolBtnR);
				}
				break;
		}
	}

	/**
	 * 切换模式
	 * @param mode
	 */
	public void setMode(int mode)
	{
		syncScaling();

		mMode = mode;
		isDrawManualTool = false;
		mTarget = mInit;
		switch(mMode)
		{
			case MODE_MANUAL:
				// 还原图片大小
				RectF orgImgRect = getOrgImgShowRect();
				Replacement1(img, orgImgRect.width(), orgImgRect.height(), mAnimType);
				break;
			case MODE_AUTO:
				break;
			case MODE_TOOL:
				if(initPosInNeed)
				{
					initPosInNeed = false;
					InitSlimTool();
				}
				// 还原图片大小
				orgImgRect = getOrgImgShowRect();
				Replacement1(img, orgImgRect.width(), orgImgRect.height(), mAnimType);
			default:
		}
		invalidate();
	}

	public int getMode()
	{
		return mMode;
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		mIsClick = true;
		mTween.M1End();
		mTarget = getShowMatrix(mDownX,mDownY);
		switch(mMode)
		{
			case MODE_MANUAL:
				float[] src = new float[] {mDownX, mDownY};
				FixPoint(src,0,0);
				m_dragToolX1 = src[0];
				m_dragToolY1 = src[1];
				m_dragToolX2 = m_dragToolX1;
				m_dragToolY2 = m_dragToolY1;
				isDrawManualTool = true;
				break;
			case MODE_AUTO:
				super.OddDown(event);
				break;
			case MODE_TOOL:
				if(mTarget == m_slimTool || mTarget == img)
				{
					Init_ST_M_Data(m_slimTool, mDownX, mDownY);
				}
				if(mTarget == m_slimToolBtnR)
				{
					PointF slimToolCenter = GetSlimToolCenterPos();
					getShowPos(slimToolCenter, slimToolCenter);
					temp_showCX = slimToolCenter.x;
					temp_showCY = slimToolCenter.y;
					Init_ST_RZ_Data(m_slimTool, temp_showCX, temp_showCY, mDownX, mDownY);
				}
				break;
		}

		if(mCallBack != null)
		{
			((ControlCallback)mCallBack).OnFingerDown(mMode, event.getPointerCount());
		}
		Init_M_Data(mTarget, mDownX, mDownY);
		invalidate();
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		switch(mMode)
		{
			case MODE_MANUAL:
				mTarget = mInit;
				float[] point = new float[] {event.getX(), event.getY()};
				FixPoint(point,0,0);
				m_dragToolX2 = point[0];
				m_dragToolY2 = point[1];
				RefreshSonWinPos(event.getX(),event.getY());
				break;
			case MODE_AUTO:
				super.OddMove(event);
				break;
			case MODE_TOOL:
				if(mTarget == m_slimTool)
				{
					Run_ST_M(m_slimTool, event.getX(), event.getY());

					//限制移动区域
					PointF slimToolCenter = GetSlimToolCenterPos();
					getShowPos(slimToolCenter, slimToolCenter);// 屏幕坐标
					point = new float[]{slimToolCenter.x, slimToolCenter.y};
					FixPoint(point, 0,0);
					slimToolCenter.set(point[0], point[1]);
					getLogicPos(slimToolCenter,slimToolCenter);// 逻辑坐标
					// 将tool的中心点转换成变换前的坐标
					Matrix[] matrices = new Matrix[]{global.m_matrix, img.m_matrix};
					invMatrixCount(slimToolCenter, slimToolCenter, matrices);

					m_slimTool.m_x = slimToolCenter.x - m_slimToolDefR;
					m_slimTool.m_y = slimToolCenter.y - m_slimToolDefR;
				}
				if(mTarget == m_slimToolBtnR)
				{
					//使用临时中心点
					Run_ST_RZ(m_slimTool, temp_showCX, temp_showCY, event.getX(), event.getY());
				}
		}
		invalidate();
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		switch(mMode)
		{
			case MODE_MANUAL:
				mTarget = mInit;
				isDrawManualTool = false;

				if(mCallBack != null)
				{
					if (mIsClick) {
						RectF curImgRect = getCurImgShowRect();
						PointF initPoint = new PointF(m_dragToolX1, m_dragToolY1);
						PointF endPoint = new PointF(m_dragToolX2, m_dragToolY2);
						GetFaceLogicPos(initPoint, initPoint);
						GetFaceLogicPos(endPoint, endPoint);

						((ControlCallback)mCallBack).OnDragSlim(initPoint.x, initPoint.y, endPoint.x, endPoint.y, m_dragToolR / curImgRect.width());
					}

					m_sonWinX = 0;
					m_sonWinY = 0;
					((ControlCallback)mCallBack).UpdateSonWin(null, m_sonWinX, m_sonWinY);
				}
				break;
			case MODE_AUTO:
				super.OddUp(event);
				break;
			case MODE_TOOL:
				// 图片当前状态构造的矩形
				RectF curImgRect = getCurImgShowRect();

				if (mTarget == m_slimToolBtnA && mCallBack != null && m_slimTool != null) {
					float[] src = new float[4];
					float[] dst = new float[] {0,0, -(m_slimTool.m_centerX * m_slimTool.m_scaleX) / 2f, 0};
					GetSlimToolData(src, dst);
					PointF slimToolCenter = GetSlimToolCenterPos();
					src[0] = slimToolCenter.x;
					src[1] = slimToolCenter.y;
					getShowPos(src,src);
					GetFaceLogicPos(dst,src);
					((ControlCallback)mCallBack).OnClickSlimTool(dst[0], dst[1], dst[2], dst[3], m_slimTool.m_centerX * m_slimTool.m_scaleX / curImgRect.width());
				}
				else if (mTarget == m_slimToolBtnB && mCallBack != null && m_slimTool != null) {
					float[] src = new float[4];
					float[] dst = new float[] {0, 0, (m_slimTool.m_centerX * m_slimTool.m_scaleX) / 2f, 0};
					GetSlimToolData(src, dst);
					PointF slimToolCenter = GetSlimToolCenterPos();
					src[0] = slimToolCenter.x;
					src[1] = slimToolCenter.y;
					getShowPos(src,src);
					GetFaceLogicPos(dst,src);
					((ControlCallback)mCallBack).OnClickSlimTool(dst[0], dst[1], dst[2], dst[3], m_slimTool.m_centerX * m_slimTool.m_scaleX / curImgRect.width());
				}
				else if ( (mTarget == m_slimToolBtnR || mTarget == m_slimTool) && mCallBack != null && m_slimTool != null) {
					((ControlCallback)mCallBack).OnResetSlimTool((m_slimTool.m_w * m_slimTool.m_scaleX) / curImgRect.width());
				}

				DoAnim();
		}
		if(mCallBack != null)
		{
			((ControlCallback)mCallBack).OnFingerUp(mMode, event.getPointerCount());
		}
		Init_M_Data(mTarget, mUpX, mUpY);
		invalidate();
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		mIsClick = false;
		mTween.M1End();
		switch(mMode)
		{
			case MODE_MANUAL:
				if (mCallBack != null) {
					m_sonWinX = 0;
					m_sonWinY = 0;
					((ControlCallback)mCallBack).UpdateSonWin(null,m_sonWinX,m_sonWinY);
				}
			case MODE_AUTO:
			case MODE_TOOL:
				mTarget = img;
				break;
		}
		Init_MRZ_Data(mTarget, mDownX1, mDownY1, mDownX2, mDownY2);
		isDrawManualTool = false;
		if(mCallBack != null)
		{
			((ControlCallback)mCallBack).OnFingerDown(mMode, event.getPointerCount());
		}
		this.invalidate();
	}

	@Override
	protected void EvenMove(MotionEvent event)
	{
		Run_MRZ(mTarget, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
		this.invalidate();
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		switch(mMode)
		{
			case MODE_MANUAL:
			case MODE_AUTO:
				super.EvenUp(event);
				break;
			case MODE_TOOL:

				break;
		}
		if(mCallBack != null)
		{
			((ControlCallback)mCallBack).OnFingerUp(mMode, event.getPointerCount());
		}
	}

	@Override
	protected void Run_MRZ(BaseView.Shape target, float x1, float y1, float x2, float y2)
	{
		target.m_matrix.set(mOldMatrix);
		Run_Z(target, x1, y1, x2, y2);
		Run_M(target.m_matrix, (x1 + x2) / 2f, (y1 + y2) / 2f);
	}

	/* ----------------------------------------- 手动模式 -------------------------------------------------- */

	//手动模式--拖动的工具
	protected float m_dragToolX1;
	protected float m_dragToolY1;
	protected float m_dragToolX2;
	protected float m_dragToolY2;
	protected float m_dragToolDefR; //默认大小
	protected float m_dragToolR; //大小为实际屏幕显示尺寸

	private int m_sonWinRadius;
	private int m_sonWinOffset;
	private int m_sonWinBorder;
	private int m_sonWinX = 0;
	private int m_sonWinY = 0;
	private Bitmap m_SonWinBmp;
	private Path dashedPath;

	private boolean isDrawManualTool = false;

	protected static final long DISPLAY_DURATION = 500;
	protected long m_displayTime;

	public void SetSlimDragRScale(float scale)
	{
		m_dragToolR = m_dragToolDefR * scale;
		if (m_dragToolR < 1) {
			m_dragToolR = 1;
		}
		m_displayTime = System.currentTimeMillis() + DISPLAY_DURATION;
		invalidate();
	}

	public void setManualCircleSize(float size) {
		float scale = (size / 2f + 100) / 100f;
		if (scale < 1) {
			scale = 1;
		} else if (scale > 1.5) {
			scale = 1.5f;
		}

		m_dragToolR = m_dragToolDefR * scale;
		if (m_dragToolR < 1) {
			m_dragToolR = 1;
		}
	}

	protected void DrawManualSlimTool(Canvas canvas, int color, float x1, float y1, float x2, float y2, float r, boolean drawToMagnify)
	{
		canvas.save();
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(color);
		mPaint.setStrokeWidth(def_stroke_width);
		mPaint.setStrokeJoin(Paint.Join.MITER);
		mPaint.setStrokeMiter(def_stroke_width * 2);
		if(drawToMagnify)
		{
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		}

		//画第一个圆
		canvas.drawCircle(x1, y1, r, mPaint);
		//画第二个圆
		canvas.drawCircle(x2, y2, r, mPaint);

		//画虚线
		mPaint.setPathEffect(new DashPathEffect(new float[]{15, 8}, 0));
		dashedPath.reset();
		dashedPath.moveTo(x1,y1);
		dashedPath.lineTo(x2,y2);
		canvas.drawPath(dashedPath,mPaint);

		int smallRadius = ShareData.PxToDpi_xhdpi(3);
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(color);
		if(drawToMagnify)
		{
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		}
		canvas.drawCircle(x1, y1, smallRadius, mPaint);
		canvas.drawCircle(x2, y2, smallRadius, mPaint);
		canvas.restore();
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

		float[] src = new float[]{size / 2, size / 2, m_dragToolX1, m_dragToolY1};
		float[] dst = new float[src.length];
		Matrix[] matrices = new Matrix[]{global.m_matrix};
		invMatrixCount(dst,src,matrices);
		temp_matrix.set(img.m_matrix);
		temp_matrix.postTranslate(dst[0] - dst[2], dst[1] - dst[3]);
		canvas.drawBitmap(img.m_bmp,temp_matrix,mPaint);
		canvas.restore();

		// draw tool
		canvas.save();
		src = new float[] {m_dragToolX1, m_dragToolY1, m_dragToolX2, m_dragToolY2};
		dst = new float[4];
		temp_matrix.reset();
		temp_matrix.postTranslate(size / 2 - src[0], size / 2 - src[1]);
		temp_matrix.mapPoints(dst,src);
		DrawManualSlimTool(canvas, def_color, dst[0], dst[1], dst[2], dst[3], m_dragToolR,true);
		canvas.restore();

		if(mCallBack != null)
		{
			((ControlCallback)mCallBack).UpdateSonWin(m_SonWinBmp,m_sonWinX,m_sonWinY);
		}
		canvas.setBitmap(null);
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

	/* ------------------------------------------ 瘦身工具 -------------------------------------------------- */

	// 瘦身工具
	protected float m_slimToolDefR;
	protected ShapeEx m_slimTool; //瘦身工具,大小为实际屏幕显示尺寸
	protected BaseView.Shape m_slimToolBtnA; //瘦身工具-A按钮
	protected BaseView.Shape m_slimToolBtnB; //瘦身工具-B按钮
	protected BaseView.Shape m_slimToolBtnR; //瘦身工具-旋转按钮
	public int def_slim_tool_ab_btn_res;
	public int def_slim_tool_r_btn_res;

	// 用于slimTool模式缩放旋转
	private float temp_showCX;
	private float temp_showCY;
	protected float m_oldDegree;
	protected float m_delta; //放大
	protected float m_gammaX; //移动
	protected float m_gammaY;
	protected float m_oldX;
	protected float m_oldY;
	protected float m_oldScaleX;
	protected float m_oldScaleY;

	private void InitSlimTool()
	{
		m_slimTool = new ShapeEx();
		m_slimTool.m_w = (int)(m_slimToolDefR * 2f);
		m_slimTool.m_h = m_slimTool.m_w;
		m_slimTool.m_centerX = m_slimTool.m_w / 2f;
		m_slimTool.m_centerY = m_slimTool.m_h / 2f;
		m_slimTool.m_degree = -45;
		m_slimTool.MIN_SCALE = (ShareData.m_screenWidth * 2f * 0.1f + 0.5f) / m_slimTool.m_w;
		m_slimTool.MAX_SCALE = (ShareData.m_screenWidth * 2f * 0.2f + 0.5f) / m_slimTool.m_w;

		m_slimTool.m_x = img.m_bmp.getWidth() / 2f - m_slimTool.m_centerX;
		m_slimTool.m_y = img.m_bmp.getHeight() / 2f - m_slimTool.m_centerY;

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), def_slim_tool_ab_btn_res);
		m_slimToolBtnA = new BaseView.Shape();
		m_slimToolBtnA.m_bmp = bmp;

		m_slimToolBtnB = new BaseView.Shape();
		m_slimToolBtnB.m_bmp = bmp;

		bmp = BitmapFactory.decodeResource(getResources(), def_slim_tool_r_btn_res);
		m_slimToolBtnR = new BaseView.Shape();
		m_slimToolBtnR.m_bmp = bmp;
		UpdateToolPos();
	}

	protected void UpdateToolPos()
	{
		// 以屏幕左上角为原点做的偏移量
		float offsetA = m_slimTool.m_centerX * m_slimTool.m_scaleX + m_slimToolBtnA.m_bmp.getWidth()/2f;
		float offsetR = m_slimTool.m_centerX * m_slimTool.m_scaleX + m_slimToolBtnR.m_bmp.getHeight()/2f;

		// 点
		float[] temp_slim_tool_src = new float[6];
		float[] temp_slim_tool_dst = new float[6];

		temp_slim_tool_src[0] = -offsetA;
		temp_slim_tool_src[1] = 0;
		temp_slim_tool_src[2] = offsetA;
		temp_slim_tool_src[3] = 0;
		temp_slim_tool_src[4] = 0;
		temp_slim_tool_src[5] = offsetR;

		GetSlimToolData(temp_slim_tool_dst,temp_slim_tool_src);

		// slimToolBtnA 的初始位置
		float stbCenterX = m_slimToolBtnA.m_bmp.getWidth() / 2f;
		float stbCenterY = m_slimToolBtnA.m_bmp.getHeight() / 2f;
		m_slimToolBtnA.m_matrix.reset();
		m_slimToolBtnA.m_matrix.postScale(-1,1,stbCenterX,stbCenterY); // 镜像
		m_slimToolBtnA.m_matrix.postRotate(m_slimTool.m_degree,stbCenterX,stbCenterY);
		m_slimToolBtnA.m_matrix.postTranslate(temp_slim_tool_dst[0] - stbCenterX, temp_slim_tool_dst[1] - stbCenterY);

		// slimToolBtnB 的初始位置
		stbCenterX = m_slimToolBtnB.m_bmp.getWidth() / 2f;
		stbCenterY = m_slimToolBtnB.m_bmp.getHeight() / 2f;
		m_slimToolBtnB.m_matrix.reset();
		m_slimToolBtnB.m_matrix.postRotate(m_slimTool.m_degree,stbCenterX,stbCenterY);
		m_slimToolBtnB.m_matrix.postTranslate(temp_slim_tool_dst[2] - stbCenterX, temp_slim_tool_dst[3] - stbCenterY);

		// slimToolBtnR 的初始位置
		stbCenterX = m_slimToolBtnR.m_bmp.getWidth() / 2f;
		stbCenterY = m_slimToolBtnR.m_bmp.getHeight() / 2f;
		m_slimToolBtnR.m_matrix.reset();
		m_slimToolBtnR.m_matrix.postTranslate(temp_slim_tool_dst[4] - stbCenterX, temp_slim_tool_dst[5] - stbCenterY);
	}

	/**
	 * @return 当前的瘦身工具中心点坐标 -- 逻辑坐标系
	 */
	private PointF GetSlimToolCenterPos()
	{
		float[] src = new float[] {m_slimTool.m_x + m_slimTool.m_centerX, m_slimTool.m_y + m_slimTool.m_centerY};
		Matrix[] matrices = new Matrix[]{global.m_matrix, img.m_matrix};
		mixMatrixCount(src, src, matrices);

		return new PointF(src[0], src[1]);
	}

	protected void GetSlimToolData(float[] dst, float[] src)
	{
		PointF slimToolCenter = GetSlimToolCenterPos();//逻辑坐标系

		Matrix temp_matrix = new Matrix();
		temp_matrix.postRotate(m_slimTool.m_degree);
		temp_matrix.postTranslate(slimToolCenter.x, slimToolCenter.y);
		temp_matrix.mapPoints(dst, src);
	}

	/**
	 * 辅助瘦脸工具
	 * @param canvas
	 * @param color  颜色
	 * @param defR   默认半径
	 * @param x      中心点坐标
	 * @param y      中心点坐标
	 * @param r      半径
	 * @param degree 角度
	 */
	protected void DrawSlimTool(Canvas canvas, int color, float defR, float x, float y, float r, float degree)
	{
		canvas.save();
		canvas.translate(mCanvasX,mCanvasY);
		canvas.translate(x, y);
		canvas.rotate(degree);

		mPaint.reset();
		mPaint.setColor(color);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(def_stroke_width);
		mPaint.setStrokeJoin(Paint.Join.MITER);
		mPaint.setStrokeMiter(def_stroke_width * 2);

		//画圆
		canvas.drawCircle(0, 0, r, mPaint);

		//画直线
		canvas.drawLine(0, -r, 0, r, mPaint);

		//画左V型
		canvas.drawLine(-defR / 3f, 0, -defR / 7f, -defR / 8f, mPaint);
		canvas.drawLine(-defR / 3f, 0, -defR / 7f, defR / 8f, mPaint);

		//画右V型
		canvas.drawLine(defR / 3f, 0, defR / 7f, -defR / 8f, mPaint);
		canvas.drawLine(defR / 3f, 0, defR / 7f, defR / 8f, mPaint);

		//实心圆
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(0, 0, ShareData.PxToDpi_xhdpi(4), mPaint);

		canvas.restore();
	}

	protected void DrawButton(Canvas canvas, BaseView.Shape item)
	{
		if (item != null) {
			canvas.save();
			canvas.translate(mCanvasX, mCanvasY);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			canvas.drawBitmap(item.m_bmp,item.m_matrix,mPaint);
			canvas.restore();
		}
	}

	/**
	 * 获得白色圆当前状态的矩阵
	 * @param matrix
	 * @param item
	 */
	protected void GetSlimToolShowMatrix(Matrix matrix, ShapeEx item)
	{
		PointF slimToolCenter = GetSlimToolCenterPos();// 逻辑坐标

		matrix.reset();
		matrix.postTranslate(slimToolCenter.x - item.m_centerX, slimToolCenter.y - item.m_centerY);
		matrix.postScale(item.m_scaleX, item.m_scaleY, slimToolCenter.x, slimToolCenter.y);
		matrix.postRotate(item.m_degree, slimToolCenter.x, slimToolCenter.y);
	}

	/**
	 * @return true == 触摸到白色圆区域
	 */
	protected boolean IsClickSlimTool(float x, float y)
	{
		boolean out = false;
		float[] values = new float[9];
		Matrix matrix = new Matrix();
		GetSlimToolShowMatrix(matrix, m_slimTool);
		matrix.getValues(values);

		PointF touchPoint = new PointF(x,y);
		getLogicPos(touchPoint, touchPoint);

		if (ProcessorV2.IsSelectTarget(values, m_slimTool.m_w, m_slimTool.m_h, touchPoint.x, touchPoint.y)) {
			out = true;
		}
		return out;
	}

	private BaseView.Shape isClickResBtn(BaseView.Shape item, float[] pts)
	{
		if(item != null)
		{
			float[] dst = new float[pts.length];
			Matrix temp = new Matrix();
			item.m_matrix.invert(temp);
			temp.mapPoints(dst,pts);
			if(0 <= dst[0] && dst[0] <= item.m_bmp.getWidth())
			{
				if(0 <= dst[1] && dst[1] <= item.m_bmp.getHeight())
				{
					return item;
				}
			}
		}
		return mInit;
	}

	/**
	 * slimTool 初始化移动
	 * @param x
	 * @param y
	 */
	protected void Init_ST_M_Data(ShapeEx target, float x, float y)
	{
		m_gammaX = x;
		m_gammaY = y;
		m_oldX = target.m_x;
		m_oldY = target.m_y;
	}

	protected void Run_ST_M(ShapeEx target, float x, float y)
	{
		float[] src = new float[]{x, y, m_gammaX, m_gammaY};
		// 因为画图时是基于 global、img 两个矩阵，所以要先逆计算一次
		Matrix[] matrices = new Matrix[]{global.m_matrix, img.m_matrix};
		invMatrixCount(src, src, matrices);

		target.m_x = src[0] - src[2] + m_oldX;
		target.m_y = src[1] - src[3] + m_oldY;
	}

	/**
	 * slimTool的初始化旋转放大
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	protected void Init_ST_RZ_Data(ShapeEx target, float x1, float y1, float x2, float y2)
	{
		Init_ST_R_Data(target, x1, y1, x2, y2);
		Init_ST_Z_Data(target, x1, y1, x2, y2);
	}

	protected void Run_ST_RZ(ShapeEx target, float x1, float y1, float x2, float y2)
	{
		Run_ST_R(target, x1, y1, x2, y2);
		Run_ST_Z(target, x1, y1, x2, y2);
	}

	/**
	 * slimTool的初始化旋转
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	protected void Init_ST_R_Data(ShapeEx target, float x1, float y1, float x2, float y2)
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
		m_oldDegree = target.m_degree;
	}

	protected void Run_ST_R(ShapeEx target, float x1, float y1, float x2, float y2)
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
		target.m_degree = m_oldDegree + tempAngle - mBeta;
	}

	/**
	 * slimTool的初始化缩放
	 * (引起误差的原因是按钮不是在交点的上,放大的时候空隙也放大了)
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	protected void Init_ST_Z_Data(ShapeEx target, float x1, float y1, float x2, float y2)
	{
		m_delta = ImageUtils.Spacing(x1 - x2, y1 - y2);
		m_oldScaleX = target.m_scaleX;
		m_oldScaleY = target.m_scaleY;
	}

	protected void Run_ST_Z(ShapeEx target, float x1, float y1, float x2, float y2)
	{
		float tempDist = ImageUtils.Spacing(x1 - x2, y1 - y2);
		if(tempDist > 10)
		{
			float scale = tempDist / m_delta;
			float scaleX = m_oldScaleX * scale;
			float scaleY = m_oldScaleY * scale;
			if(scaleX > target.MAX_SCALE)
			{
				scaleX = target.MAX_SCALE;
				scaleY = scaleX / m_oldScaleX * m_oldScaleY;
			}
			if(scaleY > target.MAX_SCALE)
			{
				scaleY = target.MAX_SCALE;
				scaleX = scaleY / m_oldScaleY * m_oldScaleX;
			}
			if(scaleX < target.MIN_SCALE)
			{
				scaleX = target.MIN_SCALE;
				scaleY = scaleX / m_oldScaleX * m_oldScaleY;
			}
			if(scaleY < target.MIN_SCALE)
			{
				scaleY = target.MIN_SCALE;
				scaleX = scaleY / m_oldScaleY * m_oldScaleX;
			}
			target.SetScaleXY(scaleX, scaleY);
		}
	}

	/* ------------------------------------- 自动模式 ------------------------------------------------- */

	/* ---------------------- other -------------------------- */
	public interface ControlCallback extends BeautyCommonViewEx.ControlCallback
	{
		void UpdateSonWin(Bitmap bmp, int x, int y);

		/**
		 * @param fingerCount 触发Down事件时的手指数量
		 */
		void OnFingerDown(int mode, int fingerCount);

		/**
		 * @param fingerCount 触发Up事件时的手指数量
		 */
		void OnFingerUp(int mode, int fingerCount);

		void OnViewSizeChange();

		void OnClickSlimTool(float x1, float y1, float x2, float y2, float rw);

		void OnResetSlimTool(float rw);

		void OnDragSlim(float x1, float y1, float x2, float y2, float rw);
	}

	public static class ShapeEx extends BaseView.Shape
	{
		float MAX_SCALE = 2f;
		float MIN_SCALE = 0.5f;

		int m_w;
		int m_h;
		float m_centerX;
		float m_centerY;

		float m_x = 0;
		float m_y = 0;
		float m_degree = 0f;
		float m_scaleX = 1f;
		float m_scaleY = 1f;

		void SetScaleXY(float scaleX, float scaleY)
		{
			if(scaleX > MAX_SCALE)
			{
				m_scaleX = MAX_SCALE;
			}
			else if(scaleX < MIN_SCALE)
			{
				m_scaleX = MIN_SCALE;
			}
			else
			{
				m_scaleX = scaleX;
			}

			if(scaleY > MAX_SCALE)
			{
				m_scaleY = MAX_SCALE;
			}
			else if(scaleY < MIN_SCALE)
			{
				m_scaleY = MIN_SCALE;
			}
			else
			{
				m_scaleY = scaleY;
			}
		}
	}
}