package cn.poco.acne.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.display.RelativeView;
import cn.poco.graphics.ShapeEx;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

/**
 * Created by: fwc
 * Date: 2016/11/30
 */
public class AcneView extends RelativeView {

	public static final int def_anim_type = TweenLite.EASING_BACK | TweenLite.EASE_OUT; //动画类型

	public ShapeEx m_img = null;
	public ArrayList<ShapeEx> m_targetArr = new ArrayList<>();

	protected TweenLite m_tween = new TweenLite();
	protected float anim_old_scale;
	protected float anim_old_x;
	protected float anim_old_y;
	protected float anim_ds;
	protected float anim_dx;
	protected float anim_dy;

	protected static final long DISPLAY_DURATION = 500;
	protected long m_displayTime; //调整大小时显示圆圈的结束时刻

	protected float m_acneToolDefR;
	protected float m_acneToolR; //大小为实际屏幕显示尺寸
	protected ShapeEx m_acneTool;

	protected boolean m_isTouch = false;
	protected boolean m_isClick = false;
	protected boolean m_isOddCtrl = false; //单手操作

	protected int m_sonWinRadius;
	protected int m_sonWinX;
	protected int m_sonWinY;
	protected Bitmap m_sonWinBmp;
	protected Canvas m_sonWinCanvas;
	protected Bitmap m_tempSonWinBmp;
	protected Canvas m_tempSonWinCanvas;

	protected boolean m_drawable = false;
	protected boolean mShowSonWin = false;

	protected PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	protected Paint temp_paint = new Paint();

	protected ControlCallback m_cb;

	public int def_color = 0xff2effd2;
	public int def_stroke_width = 2;

	public int def_anim_time = 400; //动画持续时间

	public AcneView(Context context, int frW, int frH) {
		super(context, frW, frH);

		m_origin.MAX_SCALE = 5f;
		m_origin.MIN_SCALE = 0.3f;

		m_sonWinRadius = (int)(ShareData.m_screenWidth * 0.145f);
	}

	public void InitData(ControlCallback cb) {
		m_acneToolDefR = (int)(m_origin.m_w * 0.013f + 0.5f);

		Bitmap bmp;
		int w = m_origin.m_w / 6;
		bmp = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		m_acneTool = new ShapeEx();
		m_acneTool.m_bmp = bmp;
		m_acneTool.m_w = bmp.getWidth();
		m_acneTool.m_h = bmp.getHeight();
		m_acneTool.m_centerX = m_acneTool.m_w / 2f;
		m_acneTool.m_centerY = m_acneTool.m_h / 2f;
		m_acneToolR = m_acneToolDefR;
		DrawAcneTool();

		m_cb = cb;
	}

	public void SetImg(Object info, Bitmap bmp) {
		m_img = new ShapeEx();
		if (bmp != null) {
			m_img.m_bmp = bmp;
		} else {
			m_img.m_bmp = m_cb.MakeShowImg(info, m_origin.m_w, m_origin.m_h);
		}
		m_img.m_w = m_img.m_bmp.getWidth();
		m_img.m_h = m_img.m_bmp.getHeight();
		m_img.m_centerX = (float)m_img.m_w / 2f;
		m_img.m_centerY = (float)m_img.m_h / 2f;
		m_img.m_x = m_origin.m_centerX - m_img.m_centerX;
		m_img.m_y = m_origin.m_centerY - m_img.m_centerY;
		{
			float scale1 = (float)m_origin.m_w / (float)m_img.m_w;
			float scale2 = (float)m_origin.m_h / (float)m_img.m_h;
			m_img.m_scaleX = (scale1 > scale2) ? scale2 : scale1;
			m_img.m_scaleY = m_img.m_scaleX;
		}
		m_img.m_ex = info;

		//控制缩放比例
		m_img.DEF_SCALE = m_img.m_scaleX;
		{
			float scale1 = (float)m_origin.m_w * 2f / (float)m_img.m_w;
			float scale2 = (float)m_origin.m_h * 2f / (float)m_img.m_h;
			m_img.MAX_SCALE = (scale1 > scale2) ? scale2 : scale1;

			scale1 = (float)m_origin.m_w * 0.3f / (float)m_img.m_w;
			scale2 = (float)m_origin.m_h * 0.3f / (float)m_img.m_h;
			m_img.MIN_SCALE = (scale1 > scale2) ? scale2 : scale1;
		}

		UpdateViewport();
	}

	public void UpdateViewport() {
		if (m_img != null) {
			m_viewport.m_w = m_img.m_w;
			m_viewport.m_h = m_img.m_h;
			m_viewport.m_centerX = m_img.m_centerX;
			m_viewport.m_centerY = m_img.m_centerY;
			m_viewport.m_x = (m_origin.m_w - m_viewport.m_w) / 2f;
			m_viewport.m_y = (m_origin.m_h - m_viewport.m_h) / 2f;
			{
				float scale1 = (float)m_origin.m_w / (float)m_viewport.m_w;
				float scale2 = (float)m_origin.m_h / (float)m_viewport.m_h;
				m_viewport.m_scaleX = (scale1 > scale2) ? scale2 : scale1;
				m_viewport.m_scaleY = m_viewport.m_scaleX;
			}
		} else {
			m_viewport.m_w = m_origin.m_w;
			m_viewport.m_h = m_origin.m_h;
			m_viewport.m_centerX = m_origin.m_centerX;
			m_viewport.m_centerY = m_origin.m_centerY;
			m_viewport.m_x = 0;
			m_viewport.m_y = 0;
			m_viewport.m_scaleX = 1;
			m_viewport.m_scaleY = 1;
		}
	}

	public void UpdateUI() {
		this.invalidate();
	}

	public void CreateViewBuffer() {
		ClearViewBuffer();

		m_drawable = true;
	}

	public void ClearViewBuffer() {
		m_drawable = false;
	}

	protected void DrawAcneTool() {
		if (m_acneTool != null && m_acneTool.m_bmp != null) {
			Canvas canvas = new Canvas(m_acneTool.m_bmp);
			//清理
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setStyle(Paint.Style.STROKE);
			temp_paint.setColor(def_color);
			temp_paint.setStrokeWidth(def_stroke_width);
			canvas.drawCircle(m_acneTool.m_centerX, m_acneTool.m_centerY, m_acneToolR, temp_paint);
		}
	}

	@Override
	protected void OddDown(MotionEvent event) {

		mShowSonWin = true;

		if (m_cb != null) {
			m_cb.OnTouchDown();
		}

		if (m_tween.M1IsFinish()) {
			m_isTouch = true;
			m_isClick = true;

			m_displayTime = System.currentTimeMillis();

			m_target = m_acneTool;
			float[] src = new float[] {m_downX, m_downY};
			float[] dst = new float[2];
			GetLogicPos(dst, src);
			m_target.m_x = dst[0] - m_target.m_centerX;
			m_target.m_y = dst[1] - m_target.m_centerY;
			RefreshSonWinPos(m_downX, m_downY);
			this.invalidate();

			Init_M_Data(m_target, m_downX, m_downY);
		}

	}

	protected void RefreshSonWinPos(float x, float y) {
		int size = m_sonWinRadius * 2;
		if (x < size && y < size) {
			m_sonWinX = m_origin.m_w - size;
			m_sonWinY = 0;
		} else if (x > m_origin.m_w - size && y < size) {
			m_sonWinX = 0;
			m_sonWinY = 0;
		}
	}

	@Override
	protected void OddMove(MotionEvent event) {
		if (m_target == m_acneTool) {
			Run_M(m_target, event.getX(), event.getY());
			RefreshSonWinPos(event.getX(), event.getY());

			//限制移动区域
			float[] points = new float[] {m_target.m_x, m_target.m_y};
			FixPoint(points, m_target.m_centerX, m_target.m_centerY);
			m_target.m_x = points[0];
			m_target.m_y = points[1];

			this.invalidate();
		}
	}

	protected void FixPoint(float[] points, float centerX, float centerY) {
		if (points != null && m_img != null) {
			float a = m_img.m_x + m_img.m_centerX - m_img.m_centerX * m_img.m_scaleX - centerX;
			float b = m_img.m_y + m_img.m_centerY - m_img.m_centerY * m_img.m_scaleY - centerY;
			float c = m_img.m_x + m_img.m_centerX + m_img.m_centerX * m_img.m_scaleX - centerX;
			float d = m_img.m_y + m_img.m_centerY + m_img.m_centerY * m_img.m_scaleY - centerY;

			int len = points.length / 2 * 2;
			for (int i = 0; i < len; i += 2) {
				if (points[i] < a) {
					points[i] = a;
				} else if (points[i] > c) {
					points[i] = c;
				}
				if (points[i + 1] < b) {
					points[i + 1] = b;
				} else if (points[i + 1] > d) {
					points[i + 1] = d;
				}
			}
		}
	}

	@Override
	protected void OddUp(MotionEvent event) {

		mShowSonWin = false;

		if (m_cb != null) {
			m_cb.OnTouchUp();
		}

		if (m_target == m_origin) {

			m_isTouch = false;

			//回弹动画

			if (m_origin.m_scaleX < 1 || m_origin.m_scaleY < 1) {
				DoAnim(new RectF(0, 0, 1, 1), def_anim_type, def_anim_time, false);
			} else {
				float[] src = new float[] {m_img.m_x + m_img.m_centerX, m_img.m_y + m_img.m_centerY};
				float[] dst = new float[2];
				GetShowPos(dst, src);

				boolean doAnim = false;

				float imgW = m_origin.m_scaleX * m_img.m_scaleX * m_img.m_w;
				if (imgW > m_origin.m_w) {
					float min = m_origin.m_w - imgW / 2f;
					float max = imgW / 2f;
					if (dst[0] < min) {
						dst[0] = min;

						doAnim = true;
					} else if (dst[0] > max) {
						dst[0] = max;

						doAnim = true;
					}
				} else {
					dst[0] = m_origin.m_w / 2f;

					doAnim = true;
				}

				float imgH = m_origin.m_scaleY * m_img.m_scaleY * m_img.m_h;
				if (imgH > m_origin.m_h) {
					float min = m_origin.m_h - imgH / 2f;
					float max = imgH / 2f;
					if (dst[1] < min) {
						dst[1] = min;

						doAnim = true;
					} else if (dst[1] > max) {
						dst[1] = max;

						doAnim = true;
					}
				} else {
					dst[1] = m_origin.m_h / 2f;

					doAnim = true;
				}

				if (doAnim) {
					float l, t, r, b;
					l = dst[0] - imgW / 2f;
					if (l > 0) {
						l = 0;
					} else {
						l = -l / imgW;
					}
					r = dst[0] + imgW / 2f;
					if (r < m_origin.m_w) {
						r = 1;
					} else {
						r = (imgW - (r - m_origin.m_w)) / imgW;
					}
					t = dst[1] - imgH / 2f;
					if (t > 0) {
						t = 0;
					} else {
						t = -t / imgH;
					}
					b = dst[1] + imgH / 2f;
					if (b < m_origin.m_h) {
						b = 1;
					} else {
						b = (imgH - (b - m_origin.m_h)) / imgH;
					}

					DoAnim(new RectF(l, t, r, b), def_anim_type, def_anim_time, false);
				}
			}


			m_sonWinX = 0;
			m_sonWinY = 0;
			m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);

			m_isClick = false;
			m_isOddCtrl = false;
			m_target = null;
			m_targetArr.clear();
			this.invalidate();
		} else {
			m_isTouch = false;
			if (m_isClick && m_cb != null && m_img != null && m_origin != null) {
				float[] src = new float[] {m_target.m_x + m_target.m_centerX, m_target.m_y + m_target.m_centerY};
				float[] dst = new float[2];
				GetFaceLogicPos(dst, src);
				m_cb.OnTouchAcne(dst[0], dst[1], m_acneToolR / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
			}
			m_sonWinX = 0;
			m_sonWinY = 0;
			if (m_cb != null) {
				m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);
			}

			m_isClick = false;
			m_target = null;
			this.invalidate();
		}
	}

	/**
	 * @param rect 图片的比例矩形
	 */
	public void DoAnim(RectF rect, int animType, int animTime, boolean fixRectPos) {
		float l = rect.left;
		float t = rect.top;
		float r = rect.right;
		float b = rect.bottom;
		if (rect.left > rect.right) {
			l = rect.right;
			r = rect.left;
		}
		if (rect.top > rect.bottom) {
			t = rect.bottom;
			b = rect.top;
		}
		if (m_img != null) {
			float rectW = (r - l) * m_img.m_w * m_img.m_scaleX;
			if (rectW <= 0) {
				rectW = 1;
			}
			float rectH = (b - t) * m_img.m_h * m_img.m_scaleY;
			if (rectH <= 0) {
				rectH = 1;
			}
			float scale = m_origin.m_w / rectW;
			{
				float scale2 = m_origin.m_h / rectH;
				if (scale2 < scale) {
					scale = scale2;
				}
			}
			//限制最大最小缩放比例
			if (scale > m_origin.MAX_SCALE) {
				scale = m_origin.MAX_SCALE;
			} else if (scale < m_origin.DEF_SCALE) {
				scale = m_origin.DEF_SCALE;
			}

			//计算矩形框相对图片的位置
			rectW *= scale;
			rectH *= scale;
			float imgShowW = m_img.m_w * m_img.m_scaleX * scale;
			float imgShowH = m_img.m_h * m_img.m_scaleY * scale;
			float rectX = l * imgShowW;
			float rectY = t * imgShowH;
			//计算图片相对世界的位置
			float imgX = (m_img.m_x + m_img.m_centerX - m_img.m_centerX * m_img.m_scaleX) * scale;
			float imgY = (m_img.m_y + m_img.m_centerY - m_img.m_centerY * m_img.m_scaleY) * scale;

			float showX = (m_origin.m_w - rectW) / 2f;
			float showY = (m_origin.m_h - rectH) / 2f;

			float oX = showX - rectX - imgX;
			float oY = showY - rectY - imgY;

			//修正
			float offsetX = 0;
			float offsetY = 0;
			//计算图片中心点
			float rectCX = (l + r) * imgShowW / 2f;
			float imgShowL = m_origin.m_w / 2f - rectCX;
			if (imgShowW <= m_origin.m_w) {
				offsetX = m_origin.m_w / 2f - (imgShowL + imgShowW / 2f);
			} else {
				float imgShowR = imgShowL + imgShowW;
				if (imgShowL > 0) {
					offsetX = -imgShowL;
				} else if (imgShowR < m_origin.m_w) {
					offsetX = m_origin.m_w - imgShowR;
				}
			}
			float rectCY = (t + b) * imgShowH / 2f;
			float imgShowT = m_origin.m_h / 2f - rectCY;
			if (imgShowH <= m_origin.m_h) {
				offsetY = m_origin.m_h / 2f - (imgShowT + imgShowH / 2f);
			} else {
				float imgShowB = imgShowT + imgShowH;
				if (imgShowT > 0) {
					offsetY = -imgShowT;
				} else if (imgShowB < m_origin.m_h) {
					offsetY = m_origin.m_h - imgShowB;
				}
			}

			oX += offsetX;
			oY += offsetY;

			anim_old_scale = m_origin.m_scaleX;
			anim_old_x = m_origin.m_x;
			anim_old_y = m_origin.m_y;

			anim_ds = scale - anim_old_scale;
			anim_dx = oX + m_origin.m_centerX * scale - m_origin.m_centerX - anim_old_x;
			anim_dy = oY + m_origin.m_centerY * scale - m_origin.m_centerY - anim_old_y;

			m_tween.Init(0f, 1f, animTime);
			m_tween.M1Start(animType);
		}
	}

	/**
	 * 显示的逻辑转为图片比例坐标
	 *
	 * @param dst
	 * @param src
	 * @return
	 */
	protected boolean GetFaceLogicPos(float[] dst, float[] src) {
		boolean out = false;

		if (m_img != null) {
			int len = src.length / 2 * 2;
			for (int i = 0; i < len; i += 2) {
				dst[i] = (src[i] - m_img.m_x - m_img.m_centerX) / (m_img.m_w * m_img.m_scaleX) + 1f / 2f;
				dst[i + 1] = (src[i + 1] - m_img.m_y - m_img.m_centerY) / (m_img.m_h * m_img.m_scaleY) + 1f / 2f;
			}

			out = true;
		}

		return out;
	}


	@Override
	protected void EvenDown(MotionEvent event) {
		mShowSonWin = false;

		if (m_cb != null) {
			m_cb.OnTouchUp();
		}

		m_sonWinX = 0;
		m_sonWinY = 0;
		m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);

		m_isTouch = true;
		m_isOddCtrl = false;
		m_isClick = false;
		m_tween.M1End();

		m_target = null;
		if (m_img != null) {
			ArrayList<ShapeEx> arr = new ArrayList<ShapeEx>();
			arr.add(m_img);
			if (GetSelectIndex(arr, (m_downX1 + m_downX2) / 2f, (m_downY1 + m_downY2) / 2f) > -1) {
				m_target = m_origin;
				Init_NCZM_Data(m_target, m_downX1, m_downY1, m_downX2, m_downY2);
			}
		}
	}

	@Override
	protected void EvenMove(MotionEvent event) {
		if (m_target != null) {
			Run_NCZM(m_target, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
			this.invalidate();
		}
	}

	@Override
	protected void EvenUp(MotionEvent event) {
		m_isClick = false;
		OddUp(event);
	}

	public void SetAcneToolRScale(float scale) {
		m_acneToolR = scale * m_acneToolDefR;
		if (m_acneToolR < 1) {
			m_acneToolR = 1;
		}
		DrawAcneTool();
		m_displayTime = System.currentTimeMillis() + DISPLAY_DURATION;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (m_drawable && m_viewport.m_w > 0 && m_viewport.m_h > 0) {
			DrawToCanvas(canvas);

			if (m_isTouch) {
				DrawToSonWin2(m_target);

				if (m_cb != null && mShowSonWin) {
					m_cb.UpdateSonWin(m_sonWinBmp, m_sonWinX, m_sonWinY);
				}
			}
		}

		if (!m_isTouch && !m_tween.M1IsFinish()) {
			float s = m_tween.M1GetPos();
			m_origin.m_scaleX = anim_old_scale + anim_ds * s;
			m_origin.m_scaleY = m_origin.m_scaleX;
			m_origin.m_x = anim_old_x + anim_dx * s;
			m_origin.m_y = anim_old_y + anim_dy * s;
			this.invalidate();
		}
	}

	protected Matrix temp_matrix = new Matrix();
	protected float[] temp_dst = new float[8];
	protected float[] temp_src = new float[8];
	protected Path temp_path = new Path();

	protected void DrawToCanvas(Canvas canvas) {
		canvas.save();

		canvas.setDrawFilter(temp_filter);

		//控制渲染矩形
		ClipStage(canvas);

		//画图片
		DrawItem(canvas, m_img);

		canvas.restore();

		//显示调整大小时的圈
		if (m_displayTime > System.currentTimeMillis() && m_acneTool != null && m_acneTool.m_bmp != null) {
			temp_matrix.reset();
			temp_matrix.postTranslate(m_origin.m_centerX - m_acneTool.m_centerX, m_origin.m_centerY - m_acneTool.m_centerY);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			canvas.drawBitmap(m_acneTool.m_bmp, temp_matrix, temp_paint);

			this.invalidate();
		}

		if (m_isClick && m_acneTool != null) {
			DrawButton(canvas, m_acneTool);
		}
	}

	protected void DrawButton(Canvas canvas, ShapeEx item) {
		if (item != null) {
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			GetShowMatrixNoScale(temp_matrix, item);
			canvas.drawBitmap(item.m_bmp, temp_matrix, temp_paint);
		}
	}

	protected void ClipStage(Canvas canvas) {
		GetShowMatrix(temp_matrix, m_viewport);
		temp_src[0] = 0;
		temp_src[1] = 0;
		temp_src[2] = m_viewport.m_w;
		temp_src[3] = 0;
		temp_src[4] = m_viewport.m_w;
		temp_src[5] = m_viewport.m_h;
		temp_src[6] = 0;
		temp_src[7] = m_viewport.m_h;
		temp_matrix.mapPoints(temp_dst, temp_src);

		if (temp_dst[0] < 0) {
			temp_dst[0] = 0;
		} else {
			if (temp_dst[0] != (int)temp_dst[0]) {
				temp_dst[0] += 0.5f; //浮点误差补偿
			}
		}
		if (temp_dst[1] < 0) {
			temp_dst[1] = 0;
		} else {
			if (temp_dst[1] != (int)temp_dst[1]) {
				temp_dst[1] += 0.5f; //浮点误差补偿
			}
		}
		temp_dst[4] = (int)temp_dst[4];
		temp_dst[5] = (int)temp_dst[5];
		if (temp_dst[4] > this.getWidth()) {
			temp_dst[4] = this.getWidth();
		}
		if (temp_dst[5] > this.getHeight()) {
			temp_dst[5] = this.getHeight();
		}

		canvas.clipRect(temp_dst[0], temp_dst[1], temp_dst[4], temp_dst[5]);
	}

	protected void DrawItem(Canvas canvas, ShapeEx item) {
		if (item != null) {
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			GetShowMatrix(temp_matrix, item);
			canvas.drawBitmap(item.m_bmp, temp_matrix, temp_paint);
		}
	}

	protected void DrawToSonWin2(ShapeEx item) {
		if (item != null && m_img != null && m_img.m_bmp != null && !m_img.m_bmp.isRecycled()) {
			int size = m_sonWinRadius * 2;
			int offset = ShareData.PxToDpi_xhdpi(10);
			int border = ShareData.PxToDpi_xhdpi(5);

			if (m_sonWinBmp == null || m_sonWinCanvas == null) {
				if (m_sonWinBmp != null) {
					m_sonWinBmp.recycle();
					m_sonWinBmp = null;
				}
				m_sonWinBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
				m_sonWinCanvas = new Canvas(m_sonWinBmp);
				m_sonWinCanvas.setDrawFilter(temp_filter);
			}
			if (m_tempSonWinBmp == null) {
				m_tempSonWinBmp = m_sonWinBmp.copy(Bitmap.Config.ARGB_8888, true);
				m_tempSonWinCanvas = new Canvas(m_tempSonWinBmp);
				m_tempSonWinCanvas.setDrawFilter(temp_filter);
			}

			//清理
			m_sonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
			m_tempSonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

			//画图到临时
			float[] src = {item.m_x + item.m_centerX, item.m_y + item.m_centerY};
			float[] dst = new float[2];
			GetShowPos(dst, src);
			m_tempSonWinCanvas.save();
			m_tempSonWinCanvas.translate(-dst[0] + m_sonWinRadius, -dst[1] + m_sonWinRadius);
			m_tempSonWinCanvas.drawColor(0xFFFFFFFF);
			DrawToCanvas(m_tempSonWinCanvas);
			m_tempSonWinCanvas.restore();

			//draw mask
			temp_paint.reset();
			temp_paint.setStyle(Paint.Style.FILL);
			temp_paint.setColor(0xFFFFFFFF);
			temp_paint.setAntiAlias(true);
			m_sonWinCanvas.drawRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, temp_paint);

			//临时画到sonWin
			temp_paint.reset();
			temp_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			temp_paint.setFilterBitmap(true);
			m_sonWinCanvas.drawBitmap(m_tempSonWinBmp, 0, 0, temp_paint);

			//画白边
//			temp_paint.reset();
//			temp_paint.setStyle(Paint.Style.FILL);
//			temp_paint.setColor(0xA0FFFFFF);
//			temp_paint.setAntiAlias(true);
//			temp_paint.setFilterBitmap(true);
//			temp_path.reset();
//			temp_path.setFillType(Path.FillType.EVEN_ODD);
//			temp_path.addRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, Path.Direction.CW);
//			temp_path.addRoundRect(new RectF(offset + border, offset + border, size - offset - border, size - offset - border), border << 1, border << 1, Path.Direction.CW);
//			m_sonWinCanvas.drawPath(temp_path, temp_paint);
		}
	}

	public Bitmap GetOutputBmp(int size) {
		Bitmap out = m_cb.MakeOutputImg(m_img.m_ex, size, size);

		float outW = out.getWidth();
		float outH = out.getHeight();
		ShapeEx backup = (ShapeEx)m_origin.Clone();

		//设置输出位置
		m_origin.m_scaleX = outW / (float)m_viewport.m_w / m_viewport.m_scaleX;
		m_origin.m_scaleY = m_origin.m_scaleX;
		m_origin.m_x = (int)outW / 2f - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
		m_origin.m_y = (int)outH / 2f - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;

		Canvas canvas = new Canvas(out);
		canvas.setDrawFilter(temp_filter);

		m_origin.Set(backup);

		return out;
	}

	public void ReleaseMem()
	{
		ClearViewBuffer();

		if(m_sonWinBmp != null)
		{
			m_sonWinBmp.recycle();
			m_sonWinBmp = null;
		}
		if(m_tempSonWinBmp != null)
		{
			m_tempSonWinBmp.recycle();
			m_tempSonWinBmp = null;
		}
	}

	public interface ControlCallback {

		/**
		 * @param x  逻辑坐标
		 * @param y  逻辑坐标
		 * @param rw 圈的比例大小(r/w)
		 */
		void OnTouchAcne(float x, float y, float rw);

		Bitmap MakeShowImg(Object info, int frW, int frH);

		Bitmap MakeOutputImg(Object info, int outW, int outH);

		void UpdateSonWin(Bitmap bmp, int x, int y);

		void OnTouchDown();

		void OnTouchUp();
	}
}
