package cn.poco.beauty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.display.RelativeView;
import cn.poco.graphics.ShapeEx;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

/**
 * Created by: fwc
 * Date: 2016/12/21
 */
public class BeautyView extends RelativeView {

	protected boolean m_isTouch = false;
	protected boolean m_isClick = false;

	public int def_anim_time = 400; //动画持续时间
	public int def_anim_type = TweenLite.EASING_BACK | TweenLite.EASE_OUT; //动画类型
	public int def_face_anim_type = TweenLite.EASING_EXPO | TweenLite.EASE_OUT; //动画类型
	protected TweenLite m_tween = new TweenLite();
	protected float anim_old_scale;
	protected float anim_old_x;
	protected float anim_old_y;
	protected float anim_ds;
	protected float anim_dx;
	protected float anim_dy;

	protected int def_click_size; //判断为click状态的最大size

	public ShapeEx m_img = null;
	public ArrayList<ShapeEx> m_targetArr = new ArrayList<>();

	//双击触发
	protected long m_doubleClickTime = 0;
	protected float m_doubleClickX = 0;
	protected float m_doubleClickY = 0;

	protected PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	protected Paint temp_paint = new Paint();

	private ControlCallback mCb;

	protected boolean m_drawable = false; //控制是否可画

	public BeautyView(Context context, int frW, int frH) {
		super(context, frW, frH);

		m_origin.MAX_SCALE = 5f;
		m_origin.MIN_SCALE = 0.3f;

		def_click_size = ShareData.PxToDpi_xhdpi(20);
	}

	public void InitData(ControlCallback cb) {
		mCb = cb;
	}

	/**
	 * @param info
	 * @param bmp  可以为null,为null时调用默认回调生成图片
	 */
	public void SetImg(Object info, Bitmap bmp) {
		m_img = new ShapeEx();
		if (bmp != null) {
			m_img.m_bmp = bmp;
		} else {
			m_img.m_bmp = mCb.MakeShowImg(info, m_origin.m_w, m_origin.m_h);
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

	@Override
	protected void OddDown(MotionEvent event) {

		m_isTouch = true;
		m_isClick = true;
		m_tween.M1End();

		m_target = m_origin;
		Init_M_Data(m_target, m_downX, m_downY);
	}

	@Override
	protected void OddMove(MotionEvent event) {
		if (m_target != null) {
			if (m_target != m_origin) {
				Run_M(m_target, event.getX(), event.getY());

				//限制移动区域,m_target为面部校对点
				float a = m_img.m_x + m_img.m_centerX - m_img.m_centerX * m_img.m_scaleX - m_target.m_centerX;
				float b = m_img.m_y + m_img.m_centerY - m_img.m_centerY * m_img.m_scaleY - m_target.m_centerY;
				float c = m_img.m_x + m_img.m_centerX + m_img.m_centerX * m_img.m_scaleX - m_target.m_centerX;
				float d = m_img.m_y + m_img.m_centerY + m_img.m_centerY * m_img.m_scaleY - m_target.m_centerY;
				if (m_target.m_x < a) {
					m_target.m_x = a;
				} else if (m_target.m_x > c) {
					m_target.m_x = c;
				}
				if (m_target.m_y < b) {
					m_target.m_y = b;
				} else if (m_target.m_y > d) {
					m_target.m_y = d;
				}

			} else {
				Run_M2(m_target, event.getX(), event.getY());
			}
			this.invalidate();
		}

		if (m_isClick) {
			if (ImageUtils.Spacing(m_downX - event.getX(), m_downY - event.getY()) > def_click_size) {
				m_isClick = false;
			}
		}
	}

	@Override
	protected void OddUp(MotionEvent event) {

		m_isTouch = false;

		//双击判断
		boolean doubleClick = false;
		if (m_isClick) {
			long time = System.currentTimeMillis();
			if (Math.abs(time - m_doubleClickTime) < 900 && ImageUtils.Spacing(m_doubleClickX - event.getX(), m_doubleClickY - event.getY()) < def_click_size) {
				m_doubleClickTime = 0;

				//双击
				float[] src = new float[] {event.getX(), event.getY()};
				float[] dst = new float[2];
				GetLogicPos(dst, src);
				GetFaceLogicPos(src, dst);
				if (src[0] >= 0 && src[1] >= 0) {
					doubleClick = true;
					float scale = (m_origin.MAX_SCALE + m_origin.DEF_SCALE) * 0.4f;
					if (scale < m_origin.DEF_SCALE) {
						scale = (m_origin.MAX_SCALE + m_origin.DEF_SCALE) * 0.5f;
					}
					if (Math.abs(m_origin.m_scaleX - m_origin.DEF_SCALE) > Math.abs(m_origin.m_scaleX - scale)) {
						// 双击缩小
						DoAnim(new RectF(0, 0, 1, 1), def_face_anim_type, def_anim_time, true);
					} else {
						if (scale != 0) {
							scale = m_origin.DEF_SCALE / scale;
						} else {
							scale = 0.6f;
						}
						//System.err.println(scale);
						scale /= 2f;
						//System.err.println(new RectF(src[0] - scale, src[1] - scale, src[0] + scale, src[1] + scale));
						DoAnim(new RectF(src[0] - scale, src[1] - scale, src[0] + scale, src[1] + scale), def_face_anim_type, def_anim_time, true);
					}
				}
			} else {
				m_doubleClickTime = time;
				m_doubleClickX = m_downX;
				m_doubleClickY = m_downY;
			}
		} else {
			m_doubleClickTime = 0;
		}

		//回弹动画
		if (m_origin == m_target && !doubleClick) {
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
		}

		m_isClick = false;
		m_target = null;
		m_targetArr.clear();
		this.invalidate();
	}

	@Override
	protected void EvenDown(MotionEvent event) {
		m_isTouch = true;
		m_isClick = false;
		m_tween.M1End();

		m_target = null;
		if (m_img != null) {
			ArrayList<ShapeEx> arr = new ArrayList<>();
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

	public void CreateViewBuffer() {
		ClearViewBuffer();

		m_drawable = true;
	}

	public void ClearViewBuffer() {
		m_drawable = false;
	}

	public void UpdateUI() {
		this.invalidate();
	}

	public void ReleaseMem() {
		ClearViewBuffer();
	}

	protected Matrix temp_matrix = new Matrix();
	protected float[] temp_dst = new float[8];
	protected float[] temp_src = new float[8];

	@Override
	protected void onDraw(Canvas canvas) {
		if (m_drawable && m_viewport.m_w > 0 && m_viewport.m_h > 0) {
			DrawToCanvas(canvas);
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

	protected void DrawToCanvas(Canvas canvas) {
		canvas.save();

		canvas.setDrawFilter(temp_filter);

		//控制渲染矩形
		ClipStage(canvas);

		//画图片
		DrawItem(canvas, m_img);

		canvas.restore();
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
		//System.out.println(temp_dst[0] + "," + temp_dst[1] + "," + temp_dst[4] + "," + temp_dst[5]);
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

	/**
	 * 显示的逻辑转为图片比例坐标
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

	public Bitmap GetOutputBmp(int size) {
		Bitmap out;

		out = mCb.MakeOutputImg(m_img.m_ex, size, size);

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

	/**
	 * 复制当前对象,主要用于多线程处理时的UI数据分离
	 */
	public Object Clone() {
		RelativeView out = null;

		try {
			out = (RelativeView)this.clone();
			out.m_origin = (ShapeEx)this.m_origin.Clone();
			out.m_viewport = (ShapeEx)this.m_viewport.Clone();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return out;
	}

	public interface ControlCallback {

		Bitmap MakeShowImg(Object info, int frW, int frH);

		Bitmap MakeOutputImg(Object info, int outW, int outH);
	}
}
