package cn.poco.view.beauty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

import cn.poco.face.FaceDataV2;
import cn.poco.resource.MakeupType;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;
import cn.poco.utils.PhotoMark;
import my.beautyCamera.R;

public class MakeUpViewEx1 extends MakeUpViewEx
{
	public static final int POS_THREE = 0x0001;
	public static final int POS_EYE = 0x0002;
	public static final int POS_EYEBROW = 0x0004;
	public static final int POS_LIP = 0x0008;
	public static final int POS_CHEEK = 0x0010;
	public static final int POS_NOSE = 0x1 << 5;
	private final boolean mHadData;

	public int m_showPosFlag; //显示哪些定位点
	public int m_touchPosFlag; //哪些定点可调整

	protected boolean m_3Modify = false; //是否修改过3点定点
	protected boolean m_allModify = false; //是否修改过多点定点
	public boolean m_moveAllFacePos = false;
	public boolean m_isMovePoint = false;// 判断放大镜画点

	public boolean m_updatePoint = true;

	protected Canvas mSonWinCanvas;
	protected Bitmap m_sonWinBmp;
	protected Canvas m_temSonWinCanvas;
	protected Bitmap m_tempSonWinBmp;
	protected int m_sonWinRadius;
	protected int m_sonWinX;
	protected int m_sonWinY;
	private int m_sonWinOffset;
	private int m_sonWinBorder;
	protected PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	protected PorterDuffColorFilter temp_color_filter;

	public ArrayList<ShapeEx> m_targetArr = new ArrayList<>();

	public int def_face_pos_touch_size;

	public int def_stroke_width = 2;

	public int[] def_fix_face_res; //顺时针
	public int[] def_fix_eyebrow_res; //顺时针
	public int[] def_fix_eye_res; //顺时针
	public int[] def_fix_cheek_res; //左-右
	public int[] def_fix_lip_res; //左-上上-右-上中-下中-下下
	public int[] def_fix_nose_res;

	protected ArrayList<ShapeEx> m_facePos = new ArrayList<>();
	protected ArrayList<ShapeEx> m_leyebrowPos = new ArrayList<>();
	protected ArrayList<ShapeEx> m_reyebrowPos = new ArrayList<>();
	protected ArrayList<ShapeEx> m_leyePos = new ArrayList<>();
	protected ArrayList<ShapeEx> m_reyePos = new ArrayList<>();
	protected ArrayList<ShapeEx> m_lipPos = new ArrayList<>();
	protected ArrayList<ShapeEx> m_cheekPos = new ArrayList<>();
	protected ArrayList<ShapeEx> m_nosePos = new ArrayList<>();

	protected Shape mWaterMark;
	protected RectF mWaterRect;
	protected RectF mTemWaterRect;
	protected boolean mDrawWaterMark = false;
	protected boolean mIsNonItem = true;

	private float mWaterMarkAlpha = 1f;

	private boolean mDoingAlphaAnim = false;

	private ValueAnimator mWaterMarkAlphaAnim = null;

	public MakeUpViewEx1(Context context, ControlCallback cb)
	{
		super(context, cb);
		mHadData = SettingInfoMgr.GetSettingInfo(context).GetAddDateState();
	}

	@Override
	protected void InitData()
	{
		super.InitData();

		def_img_max_scale = 15f;
		def_img_min_scale = 0.5f;

		def_face_pos_touch_size = ShareData.PxToDpi_hdpi(35);

		m_touchPosFlag = POS_EYE | POS_EYEBROW | POS_LIP | POS_THREE | POS_CHEEK | POS_NOSE ;

		m_sonWinRadius = (int)(ShareData.m_screenWidth * 0.145f);

		m_sonWinOffset = ShareData.PxToDpi_xhdpi(10);
		m_sonWinBorder = ShareData.PxToDpi_xhdpi(5);

		//版本兼容
		if(android.os.Build.VERSION.SDK_INT >= 17)
		{
			temp_color_filter = new PorterDuffColorFilter(mSkinColor, PorterDuff.Mode.SRC_IN);
		}
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();

		cancelWaterMarkAlphaAnim();

		if(mSonWinCanvas != null)
		{
			mSonWinCanvas.setBitmap(null);
			mSonWinCanvas = null;
		}

		if(m_temSonWinCanvas != null)
		{
			m_temSonWinCanvas.setBitmap(null);
			m_temSonWinCanvas = null;
		}

		if(m_sonWinBmp != null && !m_sonWinBmp.isRecycled())
		{
			m_sonWinBmp.recycle();
			m_sonWinBmp = null;
		}

		if(m_tempSonWinBmp != null && !m_tempSonWinBmp.isRecycled())
		{
			m_tempSonWinBmp.recycle();
			m_tempSonWinBmp = null;
		}

		if(m_targetArr != null && m_targetArr.size()>0)
		{
			m_targetArr.clear();
			m_targetArr = null;
		}
		ReleaseFaceResMem();
	}

	protected void ReleaseFaceResMem()
	{
		if(def_fix_face_res != null)
		{
			def_fix_face_res = null;
		}

		if(def_fix_eyebrow_res != null)
		{
			def_fix_eyebrow_res = null;
		}

		if(def_fix_eye_res != null)
		{
			def_fix_eye_res = null;
		}

		if(def_fix_cheek_res != null)
		{
			def_fix_cheek_res = null;
		}

		if(def_fix_lip_res != null)
		{
			def_fix_lip_res = null;
		}

		if(m_facePos != null && m_facePos.size()>0)
		{
			for(ShapeEx item: m_facePos)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
			}
		}

		if(m_leyebrowPos != null && m_leyebrowPos.size()>0)
		{
			for(ShapeEx item: m_leyebrowPos)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
			}
		}

		if(m_reyebrowPos != null && m_reyebrowPos.size()>0)
		{
			for(ShapeEx item: m_reyebrowPos)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
			}
		}

		if(m_leyePos != null && m_leyePos.size()>0)
		{
			for(ShapeEx item: m_leyePos)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
			}
		}

		if(m_reyePos != null && m_reyePos.size()>0)
		{
			for(ShapeEx item: m_reyePos)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
			}
		}

		if(m_lipPos != null && m_lipPos.size()>0)
		{
			for(ShapeEx item: m_lipPos)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
			}
		}

		if(m_cheekPos != null && m_cheekPos.size()>0)
		{
			for(ShapeEx item: m_cheekPos)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if(m_operateMode == MODE_MAKEUP || m_operateMode == MODE_FACE)
		{
			canvas.save();
			canvas.translate(mCanvasX, mCanvasY);
			if(m_updatePoint)
			{
				Data2UI();
			}
			//画定点
			DrawPoint(canvas);
			canvas.restore();

			if(!m_moveAllFacePos)
			{
				// 放大镜
				drawMagnify(mTarget);
			}
		}

		DrawWaterMark(canvas);
	}

	protected void DrawWaterMark(Canvas canvas)
	{
		if(mDrawWaterMark && mWaterMark != null && mWaterMark.m_bmp != null)
		{
			canvas.save();
			mWaterMark.m_matrix.reset();
			mTemWaterRect.setEmpty();

			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setColor(0x80ffffff);

			RectF curImgShowRect = getCurImgShowRect();
			float s = Math.min(curImgShowRect.width(), curImgShowRect.height());
			float x = curImgShowRect.left + PhotoMark.getLogoRight(s);
			float dy = PhotoMark.getLogoBottom(s, mHadData);
//			float x = curImgShowRect.width() - dx - mWaterRect.width() - ShareData.PxToDpi_xhdpi(6);
			float y = curImgShowRect.top + curImgShowRect.height() - dy - mWaterRect.height() - ShareData.PxToDpi_xhdpi(4);

			if(!mIsNonItem)
			{
				float watermarkW = PhotoMark.getLogoW(s);
				float scale = watermarkW / mWaterRect.width();
				mWaterMark.m_matrix.postScale(scale,scale);
				mWaterMark.m_matrix.mapRect(mTemWaterRect, mWaterRect);
//				x = curImgLogicRect.width() - dx - mTemWaterRect.width();
				y = curImgShowRect.top + curImgShowRect.height() - dy - mTemWaterRect.height();
				mPaint.setColor(0xffffffff);
			}

			mPaint.setAlpha((int) (255 * mWaterMarkAlpha));

			mWaterMark.m_matrix.postTranslate(x, y);
			canvas.drawBitmap(mWaterMark.m_bmp, mWaterMark.m_matrix, mPaint);
			canvas.restore();
		}
	}

	public void setDrawWaterMark(boolean drawWaterMask)
	{
		mDrawWaterMark = drawWaterMask;
	}

	public boolean AddWaterMark(Bitmap waterMask, boolean isNonItem)
	{
		cancelWaterMarkAlphaAnim();
		syncScaling();
		//无水印素材下，50% 透明度
		mWaterMarkAlpha = isNonItem ? 0.5f : 1f;
		mIsNonItem = isNonItem;
		return AddWaterMark(waterMask);
	}

	protected boolean AddWaterMark(Bitmap waterMask)
	{
		if(waterMask != null && !waterMask.isRecycled())
		{
			if(mWaterMark == null)
			{
				mWaterMark = new Shape();
			}

			if(mWaterMark.m_bmp != null && !mWaterMark.m_bmp.isRecycled())
			{
				mWaterMark.m_bmp.recycle();
				mWaterMark.m_bmp = null;
			}
			mWaterMark.m_bmp = waterMask;

			if(mTemWaterRect == null)
			{
				mTemWaterRect = new RectF();
			}
			if(mWaterRect == null)
			{
				mWaterRect = new RectF();
			}
			mWaterRect.setEmpty();
			mWaterRect.set(0, 0, mWaterMark.m_bmp.getWidth(), mWaterMark.m_bmp.getHeight());
			ResetImage();
			return true;
		}
		return false;
	}

	public void cancelWaterMarkAlphaAnim()
	{
		if (mWaterMarkAlphaAnim != null)
		{
			mWaterMarkAlphaAnim.removeAllListeners();
			mWaterMarkAlphaAnim.removeAllUpdateListeners();
			mWaterMarkAlphaAnim.cancel();
			mWaterMarkAlphaAnim = null;
			mWaterMarkAlpha = 1f;
			mDoingAlphaAnim = false;
			invalidate();
		}
	}

	public boolean isDoingWaterMarkAlphaAnim()
	{
		return mDoingAlphaAnim;
	}

	public void AddWaterMarkWithAnim(Bitmap waterMask, boolean isNonItem)
	{
		AddWaterMark(waterMask, isNonItem);
		mDoingAlphaAnim = true;
		//无水印素材下，50% 透明度
		if (isNonItem) {
			mWaterMarkAlphaAnim = ValueAnimator.ofFloat(0.5f, 0.2f, 0.5f, 0.2f, 0.5f);
		} else {
			mWaterMarkAlphaAnim = ValueAnimator.ofFloat(1f, 0.2f, 1f, 0.2f, 1f);
		}
		mWaterMarkAlphaAnim.setInterpolator(new LinearInterpolator());
		mWaterMarkAlphaAnim.setDuration(1200);
		mWaterMarkAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				mWaterMarkAlpha = (float) animation.getAnimatedValue();
				invalidate();
			}
		});
		mWaterMarkAlphaAnim.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				mDoingAlphaAnim = false;
			}
		});
		mWaterMarkAlphaAnim.start();
	}

	private void drawMagnify(Shape target)
	{
		if(target != null && target instanceof ShapeEx)
		{
			ShapeEx item = (ShapeEx)target;
			int size = m_sonWinRadius * 2;
			m_isMovePoint = true;

			if (m_sonWinBmp == null)
			{
				m_sonWinBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
				mSonWinCanvas = new Canvas(m_sonWinBmp);
				mSonWinCanvas.setDrawFilter(temp_filter);
			}
			else
			{
				mSonWinCanvas.setBitmap(m_sonWinBmp);
			}

			if(m_tempSonWinBmp == null)
			{
				m_tempSonWinBmp = m_sonWinBmp.copy(Bitmap.Config.ARGB_8888, true);
				m_temSonWinCanvas = new Canvas(m_tempSonWinBmp);
				m_temSonWinCanvas.setDrawFilter(temp_filter);
			}
			else
			{
				m_temSonWinCanvas.setBitmap(m_tempSonWinBmp);
			}

			//清理
			m_temSonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
			mSonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

			// 画临时
			m_temSonWinCanvas.drawColor(0xffffffff, PorterDuff.Mode.SRC);
			m_temSonWinCanvas.save();
			m_temSonWinCanvas.translate(mCanvasX,mCanvasY);

			// 临时画布的中心点是屏幕坐标，要先转换成逻辑坐标才能变换
			PointF canvasCen = new PointF(size/2f , size/2f);
			getLogicPos(canvasCen, canvasCen);
			PointF zoomPoint = new PointF(item.m_x + item.m_centerX, item.m_y + item.m_centerY);
			m_temSonWinCanvas.translate(canvasCen.x - zoomPoint.x, canvasCen.y - zoomPoint.y);

			m_temSonWinCanvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);

			m_temSonWinCanvas.drawBitmap(img.m_bmp, img.m_matrix, mPaint);
			m_temSonWinCanvas.restore();

			m_temSonWinCanvas.save();
			m_temSonWinCanvas.translate(mCanvasX,mCanvasY);
			m_temSonWinCanvas.translate(canvasCen.x - zoomPoint.x, canvasCen.y - zoomPoint.y);
			DrawPoint(m_temSonWinCanvas);
			m_temSonWinCanvas.restore();

			//draw mask
			mPaint.reset();
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(0xffffffff);
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mSonWinCanvas.drawRoundRect(new RectF(m_sonWinOffset, m_sonWinOffset, size - m_sonWinOffset, size - m_sonWinOffset), m_sonWinBorder << 1, m_sonWinBorder << 1, mPaint);

			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			mSonWinCanvas.drawBitmap(m_tempSonWinBmp, 0,0, mPaint);

			//画中间标记
			mPaint.reset();
			mPaint.setFilterBitmap(true);
			Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_changepoint_sonwindow_center_icon);
			int startX = (size - ShareData.PxToDpi_xhdpi(62))/2;
			int startY = (size - ShareData.PxToDpi_xhdpi(62))/2;
			RectF rectF = new RectF(startX, startY, startX + ShareData.PxToDpi_xhdpi(62), startY + ShareData.PxToDpi_xhdpi(62));
			mSonWinCanvas.drawBitmap(temp,null,rectF,mPaint);

			if(((ControlCallback)mCallBack) != null)
			{
				((ControlCallback)mCallBack).UpdateSonWin(m_sonWinBmp, m_sonWinX, m_sonWinY);
			}

			mSonWinCanvas.setBitmap(null);
			m_temSonWinCanvas.setBitmap(null);
		}
	}

	protected void DrawPoint(Canvas canvas)
	{
		if((m_showPosFlag & POS_THREE) != 0)
		{
			if(m_facePos != null)
			{
				int len = m_facePos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_facePos.get(i));
				}
			}
		}
		if((m_showPosFlag & POS_EYEBROW) != 0)
		{
			if(m_leyebrowPos != null)
			{
				int len = m_leyebrowPos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_leyebrowPos.get(i));
				}
			}
			if(m_reyebrowPos != null)
			{
				int len = m_reyebrowPos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_reyebrowPos.get(i));
				}
			}
		}
		if((m_showPosFlag & POS_EYE) != 0)
		{
			if(m_leyePos != null)
			{
				int len = m_leyePos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_leyePos.get(i));
				}
			}
			if(m_reyePos != null)
			{
				int len = m_reyePos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_reyePos.get(i));
				}
			}
		}
		if((m_showPosFlag & POS_CHEEK) != 0)
		{
			if(m_cheekPos != null)
			{
				int len = m_cheekPos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_cheekPos.get(i));
				}
			}
		}
		if((m_showPosFlag & POS_LIP) != 0)
		{
			if(m_lipPos != null)
			{
				int len = m_lipPos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_lipPos.get(i));
				}
			}
		}

		if((m_showPosFlag & POS_NOSE) != 0)
		{
			if(m_nosePos != null)
			{
				int len = m_nosePos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton(canvas, m_nosePos.get(i));
				}
			}
		}
	}

	/**
	 * 有选中filter
	 * @param canvas
	 * @param item
	 */
	protected void DrawButton(Canvas canvas, ShapeEx item)
	{
		if(item == mTarget && m_isMovePoint && canvas == m_temSonWinCanvas)
		{
			return;
		}
		if(item != null)
		{
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setStrokeWidth(10);
//			if((m_moveAllFacePos || mTarget == item) && temp_color_filter != null)
//			{
//				mPaint.setColorFilter(temp_color_filter);
//			}
			item.m_matrix.reset();
			item.m_matrix.postTranslate(item.m_x, item.m_y);
			item.m_matrix.postRotate(item.m_degree, item.m_x+item.m_centerX, item.m_y+item.m_centerY);
			canvas.drawBitmap(item.m_bmp, item.m_matrix, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (mDoingAlphaAnim) return true;

		return super.onTouchEvent(event);
	}

	protected int m_tempFlag;
	protected boolean m_isDownWatermark;
	@Override
	protected void OddDown(MotionEvent event)
	{
		StopAnim();
		m_isClick = true;
		// 点击水印

		switch(m_operateMode)
		{
			case MODE_FACE:
			{
				int index = -1;
				if((m_touchPosFlag & POS_THREE) != 0)
				{
					index = IsClickFacePos(m_facePos, mDownX, mDownY, def_face_pos_touch_size);
				}
				if(index >= 0)
				{
					m_3Modify = true;
					mTarget = m_facePos.get(index);
					m_updatePoint = false;
					RefreshSonWinPos(mDownX, mDownY);
					this.invalidate();
				}
				else
				{
					mTarget = img;
					m_updatePoint = true;
				}

				Init_M_Data(mTarget, mDownX, mDownY);
				break;
			}
			case MODE_MAKEUP:
			{
				//移动全部点
				if(m_moveAllFacePos && (m_touchPosFlag & (POS_EYEBROW | POS_EYE | POS_LIP)) != 0)
				{
					MakeupType type = GetClickFacePos(mDownX, mDownY, null, false);
					if(type != null && type != MakeupType.CHEEK_L && type != MakeupType.CHEEK_R)
					{
						mTarget = mInit;
						m_targetArr.clear();
						m_updatePoint = false;
						switch(type)
						{
							case EYEBROW_L:
								m_tempFlag = POS_EYEBROW;
								m_targetArr.addAll(m_leyebrowPos);
								break;

							case EYEBROW_R:
								m_tempFlag = POS_EYEBROW;
								m_targetArr.addAll(m_reyebrowPos);
								break;

							case EYE_L:
								m_tempFlag = POS_EYE;
								m_targetArr.addAll(m_leyePos);
								break;

							case EYE_R:
								m_tempFlag = POS_EYE;
								m_targetArr.addAll(m_reyePos);
								break;

							case LIP:
								m_tempFlag = POS_LIP;
								m_targetArr.addAll(m_lipPos);
								break;

							default:
								m_tempFlag = -1;
								break;
						}

						m_allModify = true;
						if(m_tempFlag != m_showPosFlag)
						{
							m_allModify = false;
							mTarget = img;
							m_updatePoint = true;
							m_targetArr.clear();
						}
						Init_M_Data(mTarget, mDownX, mDownY);
						this.invalidate();
						break;
					}
				}
				ArrayList<ShapeEx> arr = new ArrayList<>();
				if((m_touchPosFlag & POS_EYEBROW) != 0)
				{
					arr.addAll(m_leyebrowPos);
					arr.addAll(m_reyebrowPos);
				}
				if((m_touchPosFlag & POS_EYE) != 0)
				{
					arr.addAll(m_leyePos);
					arr.addAll(m_reyePos);
				}
				if((m_touchPosFlag & POS_CHEEK) != 0)
				{
					arr.addAll(m_cheekPos);
				}
				if((m_touchPosFlag & POS_LIP) != 0)
				{
					arr.addAll(m_lipPos);
				}
				if((m_touchPosFlag & POS_NOSE) != 0)
				{
					arr.addAll(m_nosePos);
				}


				int index = IsClickFacePos(arr, mDownX, mDownY, def_face_pos_touch_size);
				if(index >= 0)
				{
					if(m_moveAllFacePos)
					{
						mTarget = mInit;
						m_targetArr.clear();
						m_updatePoint = false;
						switch(GetMakeupType(arr.get(index)))
						{
							case EYEBROW_L:
								m_tempFlag = POS_EYEBROW;
								m_targetArr.addAll(m_leyebrowPos);
								break;

							case EYEBROW_R:
								m_tempFlag = POS_EYEBROW;
								m_targetArr.addAll(m_reyebrowPos);
								break;

							case EYE_L:
								m_tempFlag = POS_EYE;
								m_targetArr.addAll(m_leyePos);
								break;

							case EYE_R:
								m_tempFlag = POS_EYE;
								m_targetArr.addAll(m_reyePos);
								break;

							case LIP:
								m_tempFlag = POS_LIP;
								m_targetArr.addAll(m_lipPos);
								break;

							case CHEEK_L:
							case CHEEK_R:
								m_tempFlag = POS_CHEEK;
								m_targetArr.addAll(m_cheekPos);
								break;
							case NOSE_l:
								m_tempFlag = POS_NOSE;
								m_targetArr.addAll(m_nosePos);
								break;
							default:
						}
						m_allModify = true;
						if(m_tempFlag != m_showPosFlag)
						{
							m_allModify = false;
							mTarget = img;
							m_updatePoint = true;
							m_targetArr.clear();
						}
					}
					else
					{
						m_allModify = true;
						mTarget = arr.get(index);
						m_updatePoint = false;
						RefreshSonWinPos(mDownX, mDownY);
					}
				}
				else
				{
					mTarget = img;
					m_updatePoint = true;
				}

				Init_M_Data(mTarget, mDownX, mDownY);
				this.invalidate();
				break;
			}

			default:
				super.OddDown(event);
		}

		if(mDrawWaterMark && mTemWaterRect != null)
		{
			mTemWaterRect.setEmpty();
			mWaterMark.m_matrix.mapRect(mTemWaterRect, mWaterRect);
//			getShowPos(mTemWaterRect, mTemWaterRect);
			mTemWaterRect.left -= 1;
			mTemWaterRect.top -= 1;
			mTemWaterRect.right += 1;
			mTemWaterRect.bottom += 1;
			if(mTemWaterRect.contains(mDownX, mDownY) && mCallBack != null)
			{
				m_isDownWatermark = true;
				((ControlCallback)mCallBack).onTouchWatermark();
				mTarget = mInit;
			}
		}
	}

	protected MakeupType GetMakeupType(ShapeEx item)
	{
		MakeupType out = null;

		if(m_leyebrowPos.indexOf(item) > -1)
		{
			out = MakeupType.EYEBROW_L;
		}
		else if(m_reyebrowPos.indexOf(item) > -1)
		{
			out = MakeupType.EYEBROW_R;
		}
		else if(m_leyePos.indexOf(item) > -1)
		{
			out = MakeupType.EYE_L;
		}
		else if(m_reyePos.indexOf(item) > -1)
		{
			out = MakeupType.EYE_R;
		}
		else if(m_lipPos.indexOf(item) > -1)
		{
			out = MakeupType.LIP;
		}
		else if(m_nosePos.indexOf(item) > -1)
		{
			out = MakeupType.NOSE_l;
		}
		else
		{
			int index = m_cheekPos.indexOf(item);
			if(index == 0 || index == 2)
			{
				out = MakeupType.CHEEK_L;
			}
			else if(index == 1)
			{
				out = MakeupType.CHEEK_R;
			}

		}

		return out;
	}

	protected int IsClickFacePos(ArrayList<ShapeEx> arr, float x, float y, float clickSize)
	{
		int out = -1;

		if(arr != null)
		{
			float minSize = 0;
			float[] src = new float[2];
			float[] dst = new float[2];
			ShapeEx temp;
			float size = 0;
			for(int i = 0; i < arr.size(); i++)
			{
				temp = arr.get(i);
				src[0] = temp.m_x + temp.m_centerX;
				src[1] = temp.m_y + temp.m_centerY;
				getShowPos(dst, src);
				size = ImageUtils.Spacing(dst[0] - x, dst[1] - y);

				if(i == 0)
				{
					minSize = size;
					out = i;
				}
				else
				{
					if(minSize > size)
					{
						minSize = size;
						out = i;
					}
				}
			}

			if(minSize > clickSize)
			{
				out = -1;
			}
		}

		return out;
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

	@Override
	protected void OddMove(MotionEvent event)
	{
		switch(m_operateMode)
		{
			case MODE_FACE:
			case MODE_MAKEUP:
				if(mTarget != mInit)
				{
					if(mTarget == img)
					{
						Run_M(mTarget, event.getX(), event.getY());
						this.invalidate();
						break;
					}

					Run_M_Pos(mTarget, event.getX(), event.getY());
					RefreshSonWinPos(event.getX(), event.getY());

					//限制移动区域, mTarget 为面部校对点
					RectF curImgShowRect = getCurImgShowRect();
					if(mTarget instanceof ShapeEx)
					{
						ShapeEx item = (ShapeEx)mTarget;
						// item 的左上角是逻辑坐标，先转换成屏幕坐标
						PointF pointF = new PointF(item.m_x, item.m_y);
						getShowPos(pointF, pointF);
						// item 的中心点
						pointF.x += item.m_centerX;
						pointF.y += item.m_centerY;

						if(pointF.x < curImgShowRect.left)
						{
							pointF.x = curImgShowRect.left;
						}
						else if(pointF.x > curImgShowRect.right)
						{
							pointF.x = curImgShowRect.right;
						}

						if(pointF.y < curImgShowRect.top)
						{
							pointF.y = curImgShowRect.top;
						}
						else if(pointF.y > curImgShowRect.bottom)
						{
							pointF.y = curImgShowRect.bottom;
						}
						// item 的左上角
						pointF.x -= item.m_centerX;
						pointF.y -= item.m_centerY;
						// item 左上角屏幕坐标 转换成 逻辑坐标
						getLogicPos(pointF, pointF);

						item.m_x = pointF.x;
						item.m_y = pointF.y;
						mTarget = item;
					}

					UpdatePosRotation();
				}
				else if(m_moveAllFacePos && m_targetArr.size() > 0)
				{
					Run_M_All(m_targetArr, event.getX(), event.getY());
				}
				else if(m_moveAllFacePos && mTarget == img)
				{
					Run_M(mTarget, event.getX(), event.getY());
					this.invalidate();
					break;
				}

				this.invalidate();

				if(ImageUtils.Spacing(mDownX - event.getX(), mDownY - event.getY()) > def_click_size)
				{
					m_isClick = false;
				}
				break;
			default:
				super.OddMove(event);
		}
	}

	protected void Run_M_Pos(Shape target, float x, float y)
	{
		if(target instanceof ShapeEx)
		{
			float dx = x - mGammaX;
			float dy = y - mGammaY;
			((ShapeEx)target).m_x += dx;
			((ShapeEx)target).m_y += dy;
			mGammaX = x;
			mGammaY = y;
		}
	}

	protected void Run_M_All(ArrayList<? extends ShapeEx> target, float x, float y)
	{
		int len = target.size();
		ShapeEx temp;
		float dx = x - mGammaX;
		float dy = y - mGammaY;
		for(int i = 0; i < len; i++)
		{
			temp = target.get(i);
			temp.m_x += dx;
			temp.m_y += dy;
		}
		mGammaX = x;
		mGammaY = y;
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		if(m_isDownWatermark)
		{
			m_isDownWatermark = false;
			return;
		}
		if((m_operateMode == MODE_MAKEUP || m_operateMode == MODE_FACE) && (m_allModify || m_3Modify))
		{
			UI2Data();
			FaceDataV2.Ripe2Raw();
		}
		m_updatePoint = true;
		super.OddUp(event);

		if(mCallBack != null)
		{
			if(m_3Modify)
			{
				((ControlCallback)mCallBack).On3PosModify();
				m_3Modify = false;
			}
			if(m_allModify)
			{
				((ControlCallback)mCallBack).OnAllPosModify();
				m_allModify = false;
			}
			((ControlCallback)mCallBack).UpdateSonWin(null,0,0);
		}
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		if((m_operateMode == MODE_MAKEUP || m_operateMode == MODE_FACE) && (m_allModify || m_3Modify))
		{
			UI2Data();
			FaceDataV2.Ripe2Raw();
		}
		m_updatePoint = true;
		super.EvenDown(event);
		if(mCallBack != null)
		{
			((ControlCallback)mCallBack).UpdateSonWin(null,0,0);
		}
	}

	/* ---------------------- 人脸定点 ------------------- */

	public void InitFaceRes()
	{
		if(def_fix_face_res != null)
		{
			for(int i = 0; i < def_fix_face_res.length; i++)
			{
				m_facePos.add(MakePosItem(def_fix_face_res[i]));
			}
		}

		if(def_fix_eyebrow_res != null)
		{
			ShapeEx item;
			for(int i = 0; i < def_fix_eyebrow_res.length; i++)
			{
				item = MakePosItem(def_fix_eyebrow_res[i]);
				m_leyebrowPos.add(item);

				m_reyebrowPos.add((ShapeEx)item.Clone());
			}
		}

		if(def_fix_eye_res != null)
		{
			ShapeEx item;
			for(int i = 0; i < def_fix_eye_res.length; i++)
			{
				item = MakePosItem(def_fix_eye_res[i]);
				m_leyePos.add(item);

				m_reyePos.add((ShapeEx)item.Clone());
			}
		}

		if(def_fix_cheek_res != null)
		{
			for(int i = 0; i < def_fix_cheek_res.length; i++)
			{
				m_cheekPos.add(MakePosItem(def_fix_cheek_res[i]));
			}
		}

		if(def_fix_lip_res != null)
		{
			for(int i = 0; i < def_fix_lip_res.length; i++)
			{
				m_lipPos.add(MakePosItem(def_fix_lip_res[i]));
			}
		}

		if(def_fix_nose_res != null)
		{
			for(int i = 0; i < def_fix_nose_res.length; i++)
			{
				m_nosePos.add(MakePosItem(def_fix_nose_res[i]));
			}
		}

		UpdatePosRotation();
	}

	protected ShapeEx MakePosItem(int res)
	{
		ShapeEx out = new ShapeEx();

		out.m_bmp = BitmapFactory.decodeResource(getResources(), res);
		out.m_w = out.m_bmp.getWidth();
		out.m_h = out.m_bmp.getHeight();
		out.m_centerX = out.m_w / 2f;
		out.m_centerY = out.m_h / 2f;

		return out;
	}

	/**
	 * 图片比例坐标转换为显示逻辑坐标(注意:非显示的实际坐标)
	 *
	 * @param dst
	 * @param src
	 * @return 成功ture
	 */
	public boolean GetFaceShowPos(float[] dst, float[] src)
	{
		boolean out = false;

		if(img != null)
		{
			int len = src.length / 2 * 2;
			for(int i = 0; i < len; i += 2)
			{
				RectF curImgRect = getCurImgLogicRect();
				dst[i] = curImgRect.left + curImgRect.width() * src[i];
				dst[i + 1] = curImgRect.top + curImgRect.height() * src[i + 1];
			}

			out = true;
		}

		return out;
	}

	protected void Data2UI(float[] datas, int startIndex, ArrayList<ShapeEx> arr)
	{
		float[] src = new float[2];
		float[] dst = new float[2];
		int len = arr.size();
		int index;
		ShapeEx item;
		for(int i = 0; i < len; i++)
		{
			index = startIndex + i * 2;
			src[0] = datas[index];
			src[1] = datas[index + 1];
			GetFaceShowPos(dst, src);// 逻辑坐标
			item = arr.get(i);
			item.m_x = dst[0] - item.m_centerX;
			item.m_y = dst[1] - item.m_centerY;
		}
	}


	//增加了下巴数据
    protected void Data2UIForCheek(float[] datas, float[] chinDatas,int startIndex, ArrayList<ShapeEx> arr)
    {
        float[] src = new float[2];
        float[] dst = new float[2];
        int len = arr.size();
        int index;
        ShapeEx item;
        //脸颊数据
        for(int i = 0; i < 2; i++)
        {
            index = startIndex + i * 2;
            src[0] = datas[index];
            src[1] = datas[index + 1];
            GetFaceShowPos(dst, src);// 逻辑坐标
            item = arr.get(i);
            item.m_x = dst[0] - item.m_centerX;
            item.m_y = dst[1] - item.m_centerY;
        }

        //下巴数据
        if(arr.size() == 3)
        {
            index = 0;
            src[0] = chinDatas[index];
            src[1] = chinDatas[index + 1];
            GetFaceShowPos(dst, src);// 逻辑坐标
            item = arr.get(2);
            item.m_x = dst[0] - item.m_centerX;
            item.m_y = dst[1] - item.m_centerY;
        }
    }

	public void Data2UI()
	{
		if(FaceDataV2.FACE_POS_MULTI != null && m_facePos != null)
		{
			Data2UI(FaceDataV2.FACE_POS_MULTI[m_faceIndex], 0, m_facePos);
		}
		if(FaceDataV2.EYEBROW_POS_MULTI != null)
		{
			if(m_leyebrowPos != null)
			{
				Data2UI(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, m_leyebrowPos);
			}

			if(m_reyebrowPos != null)
			{
				Data2UI(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, m_reyebrowPos);
			}
		}
		if(FaceDataV2.EYE_POS_MULTI != null)
		{
			if(m_leyePos != null)
			{
				Data2UI(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, m_leyePos);
			}
			if(m_reyePos != null)
			{
				Data2UI(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, m_reyePos);
			}
		}
		if(FaceDataV2.CHEEK_POS_MULTI != null)
		{
			if(m_cheekPos != null)
			{
//				Data2UI(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 0, m_cheekPos);
                Data2UIForCheek(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex],FaceDataV2.CHIN_POS_MULTI[m_faceIndex],0,m_cheekPos);
			}
		}
		if(FaceDataV2.LIP_POS_MULTI != null)
		{
			if(m_lipPos != null)
			{
				Data2UI(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, m_lipPos);
			}
		}
		if(FaceDataV2.NOSE_POS_MULTI != null)
		{
			if(m_nosePos != null)
			{
				Data2UI(FaceDataV2.NOSE_POS_MULTI[m_faceIndex], 0, m_nosePos);
			}
		}
		UpdatePosRotation();
	}

	protected void UI2Data(ArrayList<ShapeEx> arr, float[] datas, int startIndex)
	{
		float[] src = new float[2];
		float[] dst = new float[2];
		int len = arr.size();
		int index;
		ShapeEx item;
		for(int i = 0; i < len; i++)
		{
			item = arr.get(i);
			src[0] = item.m_x + item.m_centerX;
			src[1] = item.m_y + item.m_centerY;
			getShowPos(src, src);
			GetFaceLogicPos(dst, src);
			index = startIndex + i * 2;
			datas[index] = dst[0];
			datas[index + 1] = dst[1];
		}
	}


	//加了下巴的数据
    protected void UI2DataForCheek(ArrayList<ShapeEx> arr, float[] datas, float[] chinDatas, int startIndex)
    {
        float[] src = new float[2];
        float[] dst = new float[2];
        int len = arr.size();
        int index;
        ShapeEx item;
        if(arr.size() == 3)
        {
            for(int i = 0; i < 2; i++)
            {
                item = arr.get(i);
                src[0] = item.m_x + item.m_centerX;
                src[1] = item.m_y + item.m_centerY;
                getShowPos(src, src);
                GetFaceLogicPos(dst, src);
                index = startIndex + i * 2;
                datas[index] = dst[0];
                datas[index + 1] = dst[1];
            }

            item = arr.get(2);
            src[0] = item.m_x + item.m_centerX;
            src[1] = item.m_y + item.m_centerY;
            getShowPos(src, src);
            GetFaceLogicPos(dst, src);
            index = 0;
            chinDatas[index] = dst[0];
            chinDatas[index + 1] = dst[1];
        }
    }

	public void UI2Data()
	{
		if(FaceDataV2.FACE_POS_MULTI != null && m_facePos != null)
		{
			UI2Data(m_facePos, FaceDataV2.FACE_POS_MULTI[m_faceIndex], 0);
		}
		if(FaceDataV2.EYEBROW_POS_MULTI != null)
		{
			if(m_leyebrowPos != null)
			{
				UI2Data(m_leyebrowPos, FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0);
			}

			if(m_reyebrowPos != null)
			{
				UI2Data(m_reyebrowPos, FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8);
			}
		}
		if(FaceDataV2.EYE_POS_MULTI != null)
		{
			if(m_leyePos != null)
			{
				UI2Data(m_leyePos, FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0);
			}
			if(m_reyePos != null)
			{
				UI2Data(m_reyePos, FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10);
			}
		}
		if(FaceDataV2.CHEEK_POS_MULTI != null)
		{
			if(m_cheekPos != null)
			{
//				UI2Data(m_cheekPos, FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 0);
				if(m_cheekPos.size() == 2)
				{
					UI2Data(m_cheekPos, FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 0);
				}
				else if(m_cheekPos.size() == 3)
				{
					UI2DataForCheek(m_cheekPos,FaceDataV2.CHEEK_POS_MULTI[m_faceIndex],FaceDataV2.CHIN_POS_MULTI[m_faceIndex],0);
				}
			}
		}
		if(FaceDataV2.LIP_POS_MULTI != null)
		{
			if(m_lipPos != null)
			{
				UI2Data(m_lipPos, FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0);
			}
		}

		if(FaceDataV2.NOSE_POS_MULTI != null)
		{
			if(m_nosePos != null)
			{
				UI2Data(m_nosePos, FaceDataV2.NOSE_POS_MULTI[m_faceIndex], 0);
			}
		}
	}

	private float[] m_face_pos;
	private float[] m_face_eyebrow_pos;
	private float[] m_eye_pos;
	private float[] m_cheek_pos;
	private float[] m_lip_pos;
	private float[] m_raw_face;
	private float[] m_raw_all;

	//开始定点时复制一份脸部数据
	public void copyFaceData()
	{
		if (m_faceIndex == -1) return;

		if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI[m_faceIndex] != null)
		{
			m_face_pos = FaceDataV2.FACE_POS_MULTI[m_faceIndex].clone();
		}
		if(FaceDataV2.EYEBROW_POS_MULTI != null && FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex] != null)
		{
			m_face_eyebrow_pos = FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex].clone();
		}
		if(FaceDataV2.EYE_POS_MULTI != null && FaceDataV2.EYE_POS_MULTI[m_faceIndex] != null)
		{
			m_eye_pos = FaceDataV2.EYE_POS_MULTI[m_faceIndex].clone();
		}
		if(FaceDataV2.CHEEK_POS_MULTI != null && FaceDataV2.CHEEK_POS_MULTI[m_faceIndex] != null)
		{
			m_cheek_pos = FaceDataV2.CHEEK_POS_MULTI[m_faceIndex].clone();
		}
		if(FaceDataV2.LIP_POS_MULTI != null && FaceDataV2.LIP_POS_MULTI[m_faceIndex] != null)
		{
			m_lip_pos = FaceDataV2.LIP_POS_MULTI[m_faceIndex].clone();
		}

		if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI[m_faceIndex] != null)
		{
			m_raw_face = FaceDataV2.RAW_POS_MULTI[m_faceIndex].getFaceRect().clone();
		}

		if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI[m_faceIndex] != null)
		{
			m_raw_all = FaceDataV2.RAW_POS_MULTI[m_faceIndex].getFaceFeaturesMakeUp().clone();
		}
	}

	//定点取消时恢复数据
	public void reSetFaceData()
	{
		if (m_faceIndex == -1) return;

		if(m_face_pos != null)
		{
			FaceDataV2.FACE_POS_MULTI[m_faceIndex] = m_face_pos;
		}
		if(m_face_eyebrow_pos != null)
		{
			FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex] = m_face_eyebrow_pos;
		}
		if(m_eye_pos != null)
		{
			FaceDataV2.EYE_POS_MULTI[m_faceIndex] = m_eye_pos;
		}
		if(m_cheek_pos != null)
		{
			FaceDataV2.CHEEK_POS_MULTI[m_faceIndex] = m_cheek_pos;
		}
		if(m_lip_pos != null)
		{
			FaceDataV2.LIP_POS_MULTI[m_faceIndex] = m_lip_pos;
		}

		if(m_raw_face != null)
		{
			FaceDataV2.RAW_POS_MULTI[m_faceIndex].setFaceRect(m_raw_face);
		}

		if(FaceDataV2.RAW_POS_MULTI != null)
		{
			FaceDataV2.RAW_POS_MULTI[m_faceIndex].setMakeUpFeatures(m_raw_all);
		}
	}

	private float[] temp_pos = new float[8];

	/**
	 * 更新定点按钮的旋转角度
	 */
	protected void UpdatePosRotation()
	{
		if(m_leyePos != null && m_leyePos.size() >= 4)
		{
			ArrayList<ShapeEx> arr = m_leyePos;
			ShapeEx temp;

			temp = arr.get(0);
			temp_pos[0] = temp.m_x + temp.m_centerX;
			temp_pos[1] = temp.m_y + temp.m_centerY;
			temp = arr.get(2);
			temp_pos[2] = temp.m_x + temp.m_centerX;
			temp_pos[3] = temp.m_y + temp.m_centerY;
			temp = arr.get(1);
			temp_pos[4] = temp.m_x + temp.m_centerX;
			temp_pos[5] = temp.m_y + temp.m_centerY;
			temp = arr.get(3);
			temp_pos[6] = temp.m_x + temp.m_centerX;
			temp_pos[7] = temp.m_y + temp.m_centerY;

			float degree = GetPosRotation(0, temp_pos);
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				arr.get(i).m_degree = degree;
			}
		}
		if(m_reyePos != null && m_reyePos.size() >= 4)
		{
			ArrayList<ShapeEx> arr = m_reyePos;
			ShapeEx temp;

			temp = arr.get(0);
			temp_pos[0] = temp.m_x + temp.m_centerX;
			temp_pos[1] = temp.m_y + temp.m_centerY;
			temp = arr.get(2);
			temp_pos[2] = temp.m_x + temp.m_centerX;
			temp_pos[3] = temp.m_y + temp.m_centerY;
			temp = arr.get(1);
			temp_pos[4] = temp.m_x + temp.m_centerX;
			temp_pos[5] = temp.m_y + temp.m_centerY;
			temp = arr.get(3);
			temp_pos[6] = temp.m_x + temp.m_centerX;
			temp_pos[7] = temp.m_y + temp.m_centerY;

			float degree = GetPosRotation(0, temp_pos);
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				arr.get(i).m_degree = degree;
			}
		}
		if(m_lipPos != null && m_lipPos.size() == 6)
		{
			ArrayList<ShapeEx> arr = m_lipPos;
			ShapeEx temp;

			temp = arr.get(0);
			temp_pos[0] = temp.m_x + temp.m_centerX;
			temp_pos[1] = temp.m_y + temp.m_centerY;
			temp = arr.get(6);
			temp_pos[2] = temp.m_x + temp.m_centerX;
			temp_pos[3] = temp.m_y + temp.m_centerY;
			temp = arr.get(3);
			temp_pos[4] = temp.m_x + temp.m_centerX;
			temp_pos[5] = temp.m_y + temp.m_centerY;
			temp = arr.get(8);
			temp_pos[6] = temp.m_x + temp.m_centerX;
			temp_pos[7] = temp.m_y + temp.m_centerY;

			float degree = GetPosRotation(0, temp_pos);
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				arr.get(i).m_degree = degree;
			}
		}
	}

	/**
	 *
	 * @param defDegree
	 * @param pos
	 *            左右上下
	 * @return
	 */
	protected float GetPosRotation(float defDegree, float... pos)
	{
		float out = 0;

		if(pos != null && pos.length >= 4)
		{
			int len = pos.length & 0xFFFFFFFE;
			if(pos[1] == pos[3]) //y相等
			{
				out = 0;
				if(len >= 6)
				{
					if(pos[5] < pos[1])
					{
						out = 0;
					}
					else if(pos[5] > pos[1])
					{
						out = -180;
					}
					else
					{
						if(len >= 8)
						{
							if(pos[7] >= pos[1])
							{
								out = 0;
							}
							else
							{
								out = -180;
							}
						}
					}
				}
			}
			else if(pos[0] == pos[2]) //x相等
			{
				out = 90;
				if(len >= 6)
				{
					if(pos[4] > pos[0])
					{
						out = 90;
					}
					else if(pos[4] < pos[0])
					{
						out = -90;
					}
					else
					{
						if(len >= 8)
						{
							if(pos[6] <= pos[0])
							{
								out = 90;
							}
							else
							{
								out = -90;
							}
						}
					}
				}
			}
			else
			{
				out = (float)Math.toDegrees(Math.atan(((double)(pos[1] - pos[3])) / (pos[0] - pos[2])));
				if(pos[0] > pos[2])
				{
					out += 180;
				}
			}
			out -= defDegree;
		}
		return out;
	}

	/* ---------------------- 人脸定点的动画 -------------------- */

	public void DoFixedPointAnim()
	{
		RectF showRect = null;
		m_updatePoint = true;
		switch(m_showPosFlag)
		{
			case MakeUpViewEx1.POS_CHEEK:
				// 脸颊
				float[] pos = new float[10];
				for(int i = 0; i < 6; i++)
				{
					pos[i] = FaceDataV2.FACE_POS_MULTI[m_faceIndex][i];
				}
				for(int i = 0; i < 4; i++)
				{
					pos[i + 6] = FaceDataV2.CHEEK_POS_MULTI[m_faceIndex][i];
				}
				RectF rect = GetMinRect(pos, 0, 10);
				ZoomRect(rect, 2.5f);
				showRect = ChangeRatioToShow(rect);
				break;
			case MakeUpViewEx1.POS_EYE:
				// 左眼
				rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 20);
				ZoomRect(rect, 1.2f);
				showRect = ChangeRatioToShow(rect);
				break;
			case MakeUpViewEx1.POS_EYEBROW:
				// 左眉毛
				rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 16);
				ZoomRect(rect, 1.2f);
				showRect = ChangeRatioToShow(rect);
				break;
			case MakeUpViewEx1.POS_LIP:
				// 嘴唇
				rect = GetMinRect(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, 32);
				ZoomRect(rect, 1.2f);
				showRect = ChangeRatioToShow(rect);
				break;
			case POS_THREE:
				rect = GetMinRect(FaceDataV2.FACE_POS_MULTI[m_faceIndex], 0, 6);
				ZoomRect(rect, 2.0f);
				showRect = ChangeRatioToShow(rect);
				break;
			case POS_NOSE:
				pos = new float[10];
				for(int i = 0; i < 6; i++)
				{
					pos[i] = FaceDataV2.FACE_POS_MULTI[m_faceIndex][i];
				}
				for(int i = 0; i < 4; i++)
				{
					pos[i + 6] = FaceDataV2.NOSE_POS_MULTI[m_faceIndex][i];
				}
				rect = GetMinRect(pos, 0, 10);
				ZoomRect(rect, 2.5f);
				showRect = ChangeRatioToShow(rect);
				break;
//            case POS_CHIN:
//                pos = new float[10];
//                for(int i = 0; i < 6; i++)
//                {
//                    pos[i] = FaceDataV2.FACE_POS_MULTI[m_faceIndex][i];
//                }
//                for(int i = 0; i < 4; i++)
//                {
//                    pos[i + 6] = FaceDataV2.NOSE_POS_MULTI[m_faceIndex][i];
//                }
//                rect = GetMinRect(pos, 0, 10);
//                ZoomRect(rect, 2.5f);
//                showRect = ChangeRatioToShow(rect);
//                break;
		}
		if(showRect != null && showRect.width() > 0 && showRect.height() >0)
		{
			InitFixedPointAnim(showRect);
		}
	}

	protected void InitFixedPointAnim(RectF ratioRectF)
	{
		removeCallbacks(mMagnifyAnim);
		removeCallbacks(mShrinkAnim);

		RectF curImgRect = getCurImgLogicRect();
		RectF orgImgShowRect = getOrgImgShowRect();

		// 当前缩放比例
		float curScaleX = curImgRect.width() / orgImgShowRect.width();

		// 缩放区域的宽高
		float zoomRectWidth = ratioRectF.width();
		float zoomRectHeight = ratioRectF.height();

		int viewWidth = getWidth();
		int viewHeight = getHeight();

		// 缩放区域要缩放到全屏的比例
		float scaleX = 1.0f * viewWidth / zoomRectWidth;
		float scaleY = 1.0f * viewHeight / zoomRectHeight;
		float tempScale = Math.min(scaleX, scaleY);

		Matrix temp_matrix = new Matrix();
		temp_matrix.set(img.m_matrix);

		if(mTween == null)
			mTween = new TweenLite();

		// 放大比例
		mTweenScale = tempScale;
		// 模拟图片放大缩放
		temp_matrix.postScale(mTweenScale, mTweenScale);
		// 求出模拟缩放后，图片的位置 -- 逻辑坐标
		RectF tempRect = new RectF(0, 0, img.m_bmp.getWidth(), img.m_bmp.getHeight());
		Matrix[] matrices = new Matrix[]{global.m_matrix, temp_matrix};
		mixMatrixCount(tempRect, tempRect, matrices);

		// 求出缩放后，跟原始对比的比例
		float temp_scale_X = tempRect.width() / orgImgShowRect.width();

		if(temp_scale_X >= def_img_max_scale)
		{
			mTweenScale = def_img_max_scale / curScaleX;
		}
		else if(temp_scale_X <= def_img_min_scale)
		{
			mTweenScale = def_img_min_scale / curScaleX;
		}

		InitTweenAnim(img, 0, 1, 500, mAnimType);

		PointF pointF = new PointF(ratioRectF.centerX(), ratioRectF.centerY());
		getLogicPos(pointF, pointF);

		// 记录放大时的缩放点
		mMagnifyPosX = pointF.x;
		mMagnifyPosY = pointF.y;
		// 修复放大后的位置偏移问题
		repairOffset(mMagnifyPosX, mMagnifyPosY);
		doMagnifyAnim(mMagnifyPosX,mMagnifyPosY);
	}

	public void ResetAnim()
	{
		removeCallbacks(mMagnifyAnim);
		RectF curImgShowRect = getCurImgShowRect();
		RectF orgImgShowRect = getOrgImgShowRect();

		// 缩放比例
		mTweenScale = orgImgShowRect.width() / curImgShowRect.width();
		InitTweenAnim(img, 0, 1, 500, mAnimType);
		doShrinkAnim();
	}
}
