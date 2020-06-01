package cn.poco.slim.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.display.RelativeView;
import cn.poco.face.FaceDataV2;
import cn.poco.graphics.ShapeEx;
import cn.poco.imagecore.ProcessorV2;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

import static cn.poco.beautify.BeautifyView.GetOutsideRect;

/**
 * Created by: fwc
 * Date: 2016/12/23
 */
public class SlimView extends RelativeView {

	public static final int MODE_MANUAL = 0x1;
	public static final int MODE_AUTO = 0x1 << 1;
	public static final int MODE_TOOL = 0x1 << 2;
	public static final int MODE_FACE = 0x1 << 3;

	private int mMode = MODE_MANUAL;

	protected boolean m_isTouch = false;
	protected boolean m_isClick = false;
	protected int def_click_size; //判断为click状态的最大size

	//双击触发
	protected long m_doubleClickTime = 0;
	protected float m_doubleClickX = 0;
	protected float m_doubleClickY = 0;

	public ShapeEx m_img = null;
	public ArrayList<ShapeEx> m_targetArr = new ArrayList<>();

	public int def_anim_time = 400; //动画持续时间
	public int def_face_anim_time = 400;
	public int def_anim_type = TweenLite.EASING_BACK | TweenLite.EASE_OUT; //动画类型
	public int def_face_anim_type = TweenLite.EASING_EXPO | TweenLite.EASE_OUT; //动画类型
	protected TweenLite m_tween = new TweenLite();
	protected float anim_old_scale;
	protected float anim_old_x;
	protected float anim_old_y;
	protected float anim_ds;
	protected float anim_dx;
	protected float anim_dy;

	protected static final long DISPLAY_DURATION = 500;
	protected long m_displayTime; //调整大小时显示圆圈的结束时刻

	protected float m_dragToolX1;
	protected float m_dragToolY1;
	protected float m_dragToolX2;
	protected float m_dragToolY2;
	protected float m_dragToolDefR; //默认大小
	protected float m_dragToolR; //大小为实际屏幕显示尺寸

	protected float m_slimToolDefR;
	protected ShapeEx m_slimTool; //瘦身工具,大小为实际屏幕显示尺寸
	protected ShapeEx m_slimToolBtnA; //瘦身工具-A按钮
	protected ShapeEx m_slimToolBtnB; //瘦身工具-B按钮
	protected ShapeEx m_slimToolBtnR; //瘦身工具-旋转按钮

	public int m_faceIndex; //多人脸选择哪个
	public boolean m_showSelFaceRect = true;

	protected Matrix temp_matrix = new Matrix();
	protected float[] temp_dst = new float[8];
	protected float[] temp_src = new float[8];
	protected Path temp_path = new Path();
	protected float[] temp_dst3; //选择人脸用
	protected float[] temp_src3;
	protected PathEffect temp_effect = new DashPathEffect(new float[] {12, 6}, 1);
	protected ArrayList<RectF> temp_rect_arr = new ArrayList<RectF>(); //GPU加速画矩形

	protected int m_sonWinRadius;
	protected int m_sonWinX;
	protected int m_sonWinY;
	protected Bitmap m_sonWinBmp;
	protected Canvas m_sonWinCanvas;
	protected Bitmap m_tempSonWinBmp;
	protected Canvas m_tempSonWinCanvas;
	protected boolean mShowSonWin = false;

	protected ControlCallback m_cb;

	private float temp_showCX;
	private float temp_showCY;

	protected PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	protected Paint temp_paint = new Paint();

	protected boolean m_drawable = false; //控制是否可画

	public int def_color = 0xff2effd2;
	public int def_stroke_width = 2;
	public int def_slim_tool_ab_btn_res;
	public int def_slim_tool_r_btn_res;

	private Path mLinePath = new Path();

	public SlimView(Context context, int frW, int frH) {
		super(context, frW, frH);

		m_origin.MAX_SCALE = 5f;
		m_origin.MIN_SCALE = 0.3f;

		m_sonWinRadius = (int)(ShareData.m_screenWidth * 0.145f);

		def_click_size = ShareData.PxToDpi_xhdpi(20);

//		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}

	public void setMode(int mode) {

		mMode = mode;
	}

	public void InitData(ControlCallback cb) {
		m_cb = cb;

		m_slimToolDefR = (int)(m_origin.m_w * 0.15f + 0.5f);
//		m_dragToolDefR = (int)(m_origin.m_w * 0.08f + 0.5f);
		m_dragToolDefR = ShareData.PxToDpi_xhdpi(47);

		Bitmap bmp;

		m_slimTool = new ShapeEx();
		m_slimTool.m_w = (int)(m_slimToolDefR * 2f);
		m_slimTool.m_h = m_slimTool.m_w;
		m_slimTool.m_centerX = m_slimTool.m_w / 2f;
		m_slimTool.m_centerY = m_slimTool.m_h / 2f;
		m_slimTool.m_degree = -45;
		m_slimTool.MIN_SCALE = (m_origin.m_w * 2f * 0.1f + 0.5f) / m_slimTool.m_w;
		m_slimTool.MAX_SCALE = (m_origin.m_w * 2f * 0.2f + 0.5f) / m_slimTool.m_w;
		m_slimTool.m_x = m_origin.m_centerX - m_slimTool.m_centerX;
		m_slimTool.m_y = m_origin.m_centerY - m_slimTool.m_centerY;
		bmp = BitmapFactory.decodeResource(getResources(), def_slim_tool_ab_btn_res);
		m_slimToolBtnA = new ShapeEx();
		m_slimToolBtnA.m_bmp = bmp;
		m_slimToolBtnA.m_w = bmp.getWidth();
		m_slimToolBtnA.m_h = bmp.getHeight();
		m_slimToolBtnA.m_centerX = m_slimToolBtnA.m_w / 2f;
		m_slimToolBtnA.m_centerY = m_slimToolBtnA.m_h / 2f;
		m_slimToolBtnB = new ShapeEx();
		m_slimToolBtnB.m_bmp = bmp;
		m_slimToolBtnB.m_w = bmp.getWidth();
		m_slimToolBtnB.m_h = bmp.getHeight();
		m_slimToolBtnB.m_centerX = m_slimToolBtnB.m_w / 2f;
		m_slimToolBtnB.m_centerY = m_slimToolBtnB.m_h / 2f;
		bmp = BitmapFactory.decodeResource(getResources(), def_slim_tool_r_btn_res);
		m_slimToolBtnR = new ShapeEx();
		m_slimToolBtnR.m_bmp = bmp;
		m_slimToolBtnR.m_w = bmp.getWidth();
		m_slimToolBtnR.m_h = bmp.getHeight();
		m_slimToolBtnR.m_centerX = m_slimToolBtnR.m_w / 2f;
		m_slimToolBtnR.m_centerY = m_slimToolBtnR.m_h / 2f;
		UpdateSlimTool();

		m_dragToolR = m_dragToolDefR;
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

	/**
	 * @param info
	 * @param bmp  可以为null,为null时调用默认回调生成图片
	 */
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

	@Override
	protected void OddDown(MotionEvent event) {
		m_isTouch = true;
		m_isClick = true;
		mShowSonWin = true;
		m_tween.M1End();

		switch (mMode) {
			case MODE_MANUAL: {
				m_displayTime = System.currentTimeMillis();

				m_target = null;

				float[] src = new float[] {m_downX, m_downY};
				float[] dst = new float[2];
				GetLogicPos(dst, src);
				FixPoint(dst, 0, 0);
				m_dragToolX1 = dst[0];
				m_dragToolY1 = dst[1];
				m_dragToolX2 = m_dragToolX1;
				m_dragToolY2 = m_dragToolY1;

				RefreshSonWinPos(m_downX, m_downY);

				if (m_cb != null) {
					m_cb.OnTouchDown(MODE_MANUAL);
				}

				this.invalidate();
				break;
			}
			case MODE_TOOL: {
				if (m_slimToolBtnA != null && IsClickBtn(m_slimToolBtnA, m_downX, m_downY)) {
					m_target = m_slimToolBtnA;
				} else if (m_slimToolBtnB != null && IsClickBtn(m_slimToolBtnB, m_downX, m_downY)) {
					m_target = m_slimToolBtnB;
				} else if (m_slimToolBtnR != null && IsClickBtn(m_slimToolBtnR, m_downX, m_downY) && m_slimTool != null) {
					m_target = m_slimToolBtnR;

					float[] src = {m_slimTool.m_x + m_slimTool.m_centerX, m_slimTool.m_y + m_slimTool.m_centerY};
					float[] dst = new float[2];
					GetShowPos(dst, src);
					temp_showCX = dst[0];
					temp_showCY = dst[1];
					Init_RZ_Data(m_slimTool, temp_showCX, temp_showCY, m_downX, m_downY);
				} else if (m_slimTool != null && IsClickBtn(m_slimTool, m_downX, m_downY)) {
					m_target = m_slimTool;

					Init_M_Data(m_target, m_downX, m_downY);
				} else {
					m_target = null;
				}

				if (m_cb != null) {
					m_cb.OnTouchDown(MODE_TOOL);
				}

				this.invalidate();
				break;
			}
			case MODE_AUTO:
			default: {
				m_target = m_origin;
				Init_M_Data(m_target, m_downX, m_downY);
				break;
			}
		}
	}

	@Override
	protected void OddMove(MotionEvent event) {
		switch (mMode) {
			case MODE_MANUAL:

				float[] src = new float[] {event.getX(), event.getY()};
				float[] dst = new float[2];
				GetLogicPos(dst, src);
				FixPoint(dst, 0, 0);
				m_dragToolX2 = dst[0];
				m_dragToolY2 = dst[1];

				RefreshSonWinPos(event.getX(), event.getY());

				this.invalidate();
				break;
			case MODE_TOOL: {
				if (m_target != null) {
					if (m_target == m_slimToolBtnA) {
						if (!IsClickBtn(m_slimToolBtnA, event.getX(), event.getY())) {
							m_target = null;
						}
					} else if (m_target == m_slimToolBtnB) {
						if (!IsClickBtn(m_slimToolBtnB, event.getX(), event.getY())) {
							m_target = null;
						}
					} else if (m_target == m_slimToolBtnR) {
						if (m_slimTool != null) {
							//使用临时中心点
							Run_RZ(m_slimTool, temp_showCX, temp_showCY, event.getX(), event.getY());

							UpdateSlimTool();
						}
					} else if (m_target == m_slimTool) {
						Run_M(m_target, event.getX(), event.getY());

						//限制移动区域
						float[] points = new float[] {m_target.m_x, m_target.m_y};
						FixPoint(points, m_target.m_centerX, m_target.m_centerY);
						m_target.m_x = points[0];
						m_target.m_y = points[1];

						UpdateSlimTool();
					}

					this.invalidate();
				}
				break;
			}
			case MODE_AUTO:
			default: {
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
					break;
				}

				if (m_isClick) {
					if (ImageUtils.Spacing(m_downX - event.getX(), m_downY - event.getY()) > def_click_size) {
						m_isClick = false;
					}
				}
				break;
			}

		}
	}

	@Override
	protected void OddUp(MotionEvent event) {

		m_isTouch = false;
		mShowSonWin = false;
		//双击判断
		boolean doubleClick = false;
		if (m_isClick && m_target == m_origin) {
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
						DoAnim(new RectF(0, 0, 1, 1), def_face_anim_type, def_anim_time, true);
					} else {
						if (scale != 0) {
							scale = m_origin.DEF_SCALE / scale;
						} else {
							scale = 0.6f;
						}
						scale /= 2f;
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

		if (mMode == MODE_MANUAL) {
			if (m_isClick && m_cb != null && m_img != null) {
				float[] src = new float[] {m_dragToolX1, m_dragToolY1, m_dragToolX2, m_dragToolY2};
				float[] dst = new float[4];
				GetFaceLogicPos(dst, src);
				m_cb.OnDragSlim(dst[0], dst[1], dst[2], dst[3], m_dragToolR / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
				m_cb.OnTouchUp(MODE_MANUAL);
			}
		} else if (mMode == MODE_TOOL) {
			if (m_target != null && m_target == m_slimToolBtnA && IsClickBtn(m_slimToolBtnA, event.getX(), event.getY()) && m_cb != null && m_slimTool != null) {
				float[] src = new float[4];
				float[] dst = new float[] {0, 0, -(m_slimTool.m_centerX * m_slimTool.m_scaleX) / 2f, 0};
				GetSlimToolData(src, dst);
				src[0] = m_slimTool.m_x + m_slimTool.m_centerX;
				src[1] = m_slimTool.m_y + m_slimTool.m_centerY;
				GetFaceLogicPos(dst, src);
				m_cb.OnClickSlimTool(dst[0], dst[1], dst[2], dst[3], m_slimTool.m_centerX * m_slimTool.m_scaleX / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
			} else if (m_target != null && m_target == m_slimToolBtnB && IsClickBtn(m_slimToolBtnB, event.getX(), event.getY()) && m_cb != null && m_slimTool != null) {
				float[] src = new float[4];
				float[] dst = new float[] {0, 0, (m_slimTool.m_centerX * m_slimTool.m_scaleX) / 2f, 0};
				GetSlimToolData(src, dst);
				src[0] = m_slimTool.m_x + m_slimTool.m_centerX;
				src[1] = m_slimTool.m_y + m_slimTool.m_centerY;
				GetFaceLogicPos(dst, src);
				m_cb.OnClickSlimTool(dst[0], dst[1], dst[2], dst[3], m_slimTool.m_centerX * m_slimTool.m_scaleX / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
			} else if (m_target != null && (m_target == m_slimToolBtnR || m_target == m_slimTool) && m_origin != null && m_img != null && m_cb != null && m_slimTool != null) {
				m_cb.OnResetSlimTool((m_slimTool.m_w * m_slimTool.m_scaleX) / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
			}
			if (m_cb != null) {
				m_cb.OnTouchUp(MODE_TOOL);
			}
		} else if (mMode == MODE_FACE) {
			//判断选中哪个人脸
			if (m_isClick && m_cb != null && m_img != null) {
				if (FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0) {
					int len = FaceDataV2.FACE_POS_MULTI.length << 2;
					if (temp_src3 == null || temp_src3.length < len) {
						temp_src3 = new float[len];
						temp_dst3 = new float[len];
					}
					int index;
					for (int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++) {
						index = i << 2;
						temp_src3[index] = m_img.m_w * FaceDataV2.FACE_POS_MULTI[i][6];
						temp_src3[index + 1] = m_img.m_h * FaceDataV2.FACE_POS_MULTI[i][7];
						temp_src3[index + 2] = temp_src3[index] + m_img.m_w * FaceDataV2.FACE_POS_MULTI[i][8];
						temp_src3[index + 3] = temp_src3[index + 1] + m_img.m_h * FaceDataV2.FACE_POS_MULTI[i][9];
					}

					GetShowMatrix(temp_matrix, m_img);
					temp_matrix.mapPoints(temp_dst3, temp_src3);

					float x = event.getX();
					float y = event.getY();
					for (int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++) {
						index = i << 2;
						if (temp_dst3[index] < x && x < temp_dst3[index + 2] && temp_dst3[index + 1] < y && y < temp_dst3[index + 3]) {
							m_cb.OnSelFaceIndex(i);
							break;
						}
					}
				}
			}
		}

		m_sonWinX = 0;
		m_sonWinY = 0;
		m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);

		m_isClick = false;
		m_target = null;
		m_targetArr.clear();
		this.invalidate();
	}

	@Override
	protected void EvenDown(MotionEvent event) {
		m_isTouch = true;
		m_isClick = false;

		mShowSonWin = false;
		m_sonWinX = 0;
		m_sonWinY = 0;
		m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);
		m_cb.OnTouchUp(mMode);

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
		switch (mMode) {
			case MODE_MANUAL:
				m_isClick = false;
				OddUp(event);
				break;
			case MODE_TOOL:
				if (m_target != null && (m_target == m_slimToolBtnR || m_target == m_slimTool) && m_origin != null && m_img != null && m_cb != null && m_slimTool != null) {
					m_cb.OnResetSlimTool((m_slimTool.m_w * m_slimTool.m_scaleX) / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
				}
				break;
			case MODE_AUTO:
				m_isClick = false;
				OddUp(event);
				break;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (m_drawable && m_viewport.m_w > 0 && m_viewport.m_h > 0) {
			DrawToCanvas(canvas);

			if (mShowSonWin && mMode == MODE_MANUAL) {
				DrawToSonWin2();

				if (m_cb != null) {
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

	protected void DrawToCanvas(Canvas canvas) {
		canvas.save();

		canvas.setDrawFilter(temp_filter);

		//控制渲染矩形
		ClipStage(canvas);

		//画图片
		DrawItem(canvas, m_img);

		//选择人脸模式
		if (mMode == MODE_FACE && m_img != null) {
			//计算全部矩形
			int len = 4;
			if (FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0) {
				len += FaceDataV2.FACE_POS_MULTI.length << 2;
			}
			if (temp_src3 == null || temp_src3.length < len) {
				temp_src3 = new float[len];
				temp_dst3 = new float[len];
			}
			temp_src3[0] = 0;
			temp_src3[1] = 0;
			temp_src3[2] = m_img.m_w;
			temp_src3[3] = m_img.m_h;
			if (FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0) {
				int index;
				for (int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++) {
					index = 4 + (i << 2);
					temp_src3[index] = m_img.m_w * FaceDataV2.FACE_POS_MULTI[i][6];
					temp_src3[index + 1] = m_img.m_h * FaceDataV2.FACE_POS_MULTI[i][7];
					temp_src3[index + 2] = temp_src3[index] + m_img.m_w * FaceDataV2.FACE_POS_MULTI[i][8];
					temp_src3[index + 3] = temp_src3[index + 1] + m_img.m_h * FaceDataV2.FACE_POS_MULTI[i][9];
				}
			}
			GetShowMatrix(temp_matrix, m_img);
			temp_matrix.mapPoints(temp_dst3, temp_src3);

			//画矩形填充
			temp_rect_arr.clear();
			if (FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0) {
				int index;
				for (int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++) {
					index = 4 + (i << 2);
					temp_rect_arr.add(new RectF(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3]));
				}
			}
			ArrayList<RectF> arr = GetOutsideRect(new RectF(temp_dst3[0], temp_dst3[1], temp_dst3[2], temp_dst3[3]), temp_rect_arr);
			temp_path.reset();
			temp_path.setFillType(Path.FillType.WINDING);
			int rectLen = arr.size();
			for (int i = 0; i < rectLen; i++) {
				temp_path.addRect(arr.get(i), Path.Direction.CW);
			}
			temp_paint.reset();
			temp_paint.setColor(0x8c000000);
			temp_paint.setStyle(Paint.Style.FILL);
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawPath(temp_path, temp_paint);

			//画矩形边框
			if (FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0) {
				temp_paint.reset();
				temp_paint.setColor(0xFFFFFFFF);
				temp_paint.setStyle(Paint.Style.STROKE);
				temp_paint.setStrokeWidth(3);
				temp_paint.setAntiAlias(true);
				temp_paint.setPathEffect(temp_effect);

				int index;
				for (int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++) {
					index = 4 + (i << 2);
					if (i != m_faceIndex || !m_showSelFaceRect) {
						canvas.drawRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], temp_paint);
					}
				}
				if (m_showSelFaceRect && m_faceIndex >= 0 && m_faceIndex < FaceDataV2.FACE_POS_MULTI.length) {
					index = 4 + (m_faceIndex << 2);
					temp_paint.setColor(cn.poco.advanced.ImageUtils.GetSkinColor(0xffe75988));
					canvas.drawRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], temp_paint);
				}
			}
		}

		canvas.restore();

		switch (mMode) {
			case MODE_TOOL: {
				UpdateSlimTool();

				if (m_slimTool != null) {
					float[] src = new float[] {m_slimTool.m_x + m_slimTool.m_centerX, m_slimTool.m_y + m_slimTool.m_centerY};
					float[] dst = new float[2];
					GetShowPos(dst, src);
					DrawSlimTool(canvas, def_color, m_slimToolDefR / 2, dst[0], dst[1], m_slimTool.m_centerX * m_slimTool.m_scaleX, m_slimTool.m_degree);
				}
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
			case MODE_MANUAL: {
				//显示调整大小时的圈
				if (m_displayTime > System.currentTimeMillis()) {
					temp_paint.reset();
					temp_paint.setAntiAlias(true);
					temp_paint.setStyle(Paint.Style.STROKE);
					temp_paint.setColor(def_color);
					temp_paint.setStrokeWidth(def_stroke_width);
					canvas.drawCircle(m_origin.m_centerX, m_origin.m_centerY, m_dragToolR, temp_paint);

					this.invalidate();
				}

				if (m_isClick) {
					float[] src = new float[] {m_dragToolX1, m_dragToolY1, m_dragToolX2, m_dragToolY2};
					float[] dst = new float[4];
					GetShowPos(dst, src);
					DrawSlimTool2(canvas, def_color, m_dragToolDefR, dst[0], dst[1], dst[2], dst[3], m_dragToolR);
				}
				break;
			}
			default:
				break;
		}
	}

	/**
	 * 辅助瘦脸工具
	 *
	 * @param canvas
	 * @param color  颜色
	 * @param defR   默认半径
	 * @param x      中心点坐标
	 * @param y      中心点坐标
	 * @param r      半径
	 * @param degree 角度
	 */
	protected void DrawSlimTool(Canvas canvas, int color, float defR, float x, float y, float r, float degree) {
		canvas.save();
		canvas.translate(x, y);
		canvas.rotate(degree);

		temp_paint.reset();
		temp_paint.setColor(color);
		temp_paint.setAntiAlias(true);
		temp_paint.setStyle(Paint.Style.STROKE);
		temp_paint.setStrokeWidth(def_stroke_width);
		temp_paint.setStrokeJoin(Paint.Join.MITER);
		temp_paint.setStrokeMiter(def_stroke_width * 2);

		//画圆
		canvas.drawCircle(0, 0, r, temp_paint);

		//画直线
		canvas.drawLine(0, -r, 0, r, temp_paint);

		//画左V型
		canvas.drawLine(-defR * 15 / 56f - 1, 1, -defR / 7f - 1, -defR / 8f + 1, temp_paint);
		canvas.drawLine(-defR * 15 / 56f - 1, -1, -defR / 7f - 1, defR / 8f - 1, temp_paint);

		//画右V型
		canvas.drawLine(defR * 15 / 56f + 1, 1, defR / 7f + 1, -defR / 8f + 1, temp_paint);
		canvas.drawLine(defR * 15 / 56f + 1, -1, defR / 7f + 1, defR / 8f - 1, temp_paint);

		//实心圆
		temp_paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(0, 0, ShareData.PxToDpi_xhdpi(4), temp_paint);

		canvas.restore();
	}

	/**
	 * @param canvas
	 * @param color  颜色
	 * @param defR   默认半径
	 * @param x1     中心点坐标
	 * @param y1     中心点坐标
	 * @param x2     中心点坐标
	 * @param y2     中心点坐标
	 * @param r      半径
	 */
	protected void DrawSlimTool2(Canvas canvas, int color, float defR, float x1, float y1, float x2, float y2, float r) {
		temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setStyle(Paint.Style.STROKE);
		temp_paint.setColor(color);
		temp_paint.setStrokeWidth(def_stroke_width);
		temp_paint.setStrokeJoin(Paint.Join.MITER);
		temp_paint.setStrokeMiter(def_stroke_width * 2);

		//画第一个圆
		canvas.drawCircle(x1, y1, r, temp_paint);
		//画第二个圆
		canvas.drawCircle(x2, y2, r, temp_paint);

		mLinePath.reset();
		mLinePath.moveTo(x1, y1);
		mLinePath.lineTo(x2, y2);

		temp_paint.setPathEffect(new DashPathEffect(new float[] {15, 8}, 0));
		//画直线
//		canvas.drawLine(x1, y1, x2, y2, temp_paint);
		canvas.drawPath(mLinePath, temp_paint);

		int smallRadius = ShareData.PxToDpi_xhdpi(3);
		temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setStyle(Paint.Style.FILL);
		temp_paint.setColor(color);
		canvas.drawCircle(x1, y1, smallRadius, temp_paint);
		canvas.drawCircle(x2, y2, smallRadius, temp_paint);

//		//画箭头
//		float[] src = {-defR / 4f, defR / 5f, -defR / 4f, -defR / 5f};
//		float[] dst = new float[4];
//		float tempAngle;
//		if (x1 - x2 == 0) {
//			if (y1 >= y2) {
//				tempAngle = 90;
//			} else {
//				tempAngle = -90;
//			}
//		} else if (y1 - y2 != 0) {
//			tempAngle = (float)Math.toDegrees(Math.atan(((double)(y1 - y2)) / (x1 - x2)));
//			if (x1 < x2) {
//				tempAngle += 180;
//			}
//		} else {
//			if (x1 >= x2) {
//				tempAngle = 0;
//			} else {
//				tempAngle = 180;
//			}
//		}
//		tempAngle += 180;
//		temp_matrix.reset();
//		temp_matrix.postRotate(tempAngle);
//		temp_matrix.postTranslate(x2, y2);
//		temp_matrix.mapPoints(dst, src);
//		canvas.drawLine(dst[0], dst[1], x2, y2, temp_paint);
//		canvas.drawLine(dst[2], dst[3], x2, y2, temp_paint);
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

	public void SetSlimDragRScale(float scale) {
		m_dragToolR = m_dragToolDefR * scale;
		if (m_dragToolR < 1) {
			m_dragToolR = 1;
		}
		m_displayTime = System.currentTimeMillis() + DISPLAY_DURATION;
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

	public void ReleaseMem() {
		ClearViewBuffer();

		if (m_sonWinBmp != null) {
			m_sonWinBmp.recycle();
			m_sonWinBmp = null;
		}
		if (m_tempSonWinBmp != null) {
			m_tempSonWinBmp.recycle();
			m_tempSonWinBmp = null;
		}
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

	public Bitmap GetOutputBmp(int size) {
		Bitmap out;

		out = m_cb.MakeOutputImg(m_img.m_ex, size, size);

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

	protected void DrawToSonWin2() {
		if (m_img != null && m_img.m_bmp != null && !m_img.m_bmp.isRecycled()) {
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
			float[] src = {m_dragToolX1, m_dragToolY1};
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

	protected boolean IsClickBtn(ShapeEx item, float x, float y) {
		boolean out = false;

		float[] values = new float[9];
		Matrix matrix = new Matrix();
		GetShowMatrixNoScale(matrix, item);
		matrix.getValues(values);
		if (ProcessorV2.IsSelectTarget(values, item.m_w, item.m_h, x, y)) {
			out = true;
		}

		return out;
	}

	public void restore() {
		DoAnim(new RectF(0, 0, 1, 1), def_face_anim_type, def_face_anim_time, false);
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
	 * 检查点是否在m_img里面,若不是则修正
	 *
	 * @param points
	 * @param centerX
	 * @param centerY
	 */
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

	protected float[] temp_slim_tool_dst = new float[6];
	protected float[] temp_slim_tool_src = new float[6];

	protected void UpdateSlimTool() {
		if (m_slimTool != null && m_slimToolBtnA != null && m_slimToolBtnB != null && m_slimToolBtnR != null) {
			float offsetA = m_slimTool.m_centerX * m_slimTool.m_scaleX + m_slimToolBtnA.m_centerX;
			float offsetR = m_slimTool.m_centerX * m_slimTool.m_scaleX + m_slimToolBtnR.m_centerX;
			temp_slim_tool_src[0] = -offsetA;
			temp_slim_tool_src[1] = 0;
			temp_slim_tool_src[2] = offsetA;
			temp_slim_tool_src[3] = 0;
			temp_slim_tool_src[4] = 0;
			temp_slim_tool_src[5] = offsetR;
			GetSlimToolData(temp_slim_tool_dst, temp_slim_tool_src);

			m_slimToolBtnA.m_x = temp_slim_tool_dst[0] - m_slimToolBtnA.m_centerX;
			m_slimToolBtnA.m_y = temp_slim_tool_dst[1] - m_slimToolBtnA.m_centerY;
			m_slimToolBtnA.m_degree = 180 + m_slimTool.m_degree;
			m_slimToolBtnB.m_x = temp_slim_tool_dst[2] - m_slimToolBtnB.m_centerX;
			m_slimToolBtnB.m_y = temp_slim_tool_dst[3] - m_slimToolBtnB.m_centerY;
			m_slimToolBtnB.m_degree = 0 + m_slimTool.m_degree;
			m_slimToolBtnR.m_x = temp_slim_tool_dst[4] - m_slimToolBtnR.m_centerX;
			m_slimToolBtnR.m_y = temp_slim_tool_dst[5] - m_slimToolBtnR.m_centerY;
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
	 * 原始坐标->逻辑坐标
	 *
	 * @param dst
	 * @param src
	 */
	protected void GetSlimToolData(float[] dst, float[] src) {
		float[] srcXY = new float[] {m_slimTool.m_x + m_slimTool.m_centerX, m_slimTool.m_y + m_slimTool.m_centerY};
		float[] dstXY = new float[2];
		GetShowPos(dstXY, srcXY);

		float[] temp = new float[src.length];
		temp_matrix.reset();
		temp_matrix.postRotate(m_slimTool.m_degree);
		temp_matrix.postTranslate(dstXY[0], dstXY[1]);
		temp_matrix.mapPoints(temp, src);

		GetLogicPos(dst, temp);
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

	public void DoAnim2Cheek() {
		if (m_faceIndex < 0 || m_faceIndex >= FaceDataV2.FACE_POS_MULTI.length) {
			return;
		}

		float[] pos = new float[10];
		for (int i = 0; i < 6; i++) {
			pos[i] = FaceDataV2.FACE_POS_MULTI[m_faceIndex][i];
		}
		for (int i = 0; i < 4; i++) {
			pos[i + 6] = FaceDataV2.CHEEK_POS_MULTI[m_faceIndex][i];
		}
		RectF rect = GetMinRect(pos, 0, 10);
		ZoomRect(rect, 2.2f);
		DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
	}

	public void ZoomRect(RectF rect, float scale) {
		float w2 = rect.width() * scale / 2f;
		float h2 = rect.height() * scale / 2f;
		float cx = (rect.left + rect.right) / 2f;
		float cy = (rect.top + rect.bottom) / 2f;
		rect.left = cx - w2;
		rect.right = cx + w2;
		rect.top = cy - h2;
		rect.bottom = cy + h2;
		if (rect.left < 0) {
			rect.left = 0;
		} else if (rect.left > 1) {
			rect.left = 1;
		}
		if (rect.right < 0) {
			rect.right = 0;
		} else if (rect.right > 1) {
			rect.right = 1;
		}
		if (rect.top < 0) {
			rect.top = 0;
		} else if (rect.top > 1) {
			rect.top = 1;
		}
		if (rect.bottom < 0) {
			rect.bottom = 0;
		} else if (rect.bottom > 1) {
			rect.bottom = 1;
		}
	}

	public RectF GetMinRect(float[] pos, int startIndex, int len) {
		return GetMinRect(pos, startIndex, len, null);
	}

	public int getMode() {
		return mMode;
	}

	/**
	 * @param pos
	 * @param startIndex
	 * @param len
	 * @param recycle    重复利用对象,可为null
	 * @return
	 */
	public RectF GetMinRect(float[] pos, int startIndex, int len, RectF recycle) {
		RectF out;
		if (recycle != null) {
			out = recycle;
			out.setEmpty();
		} else {
			out = new RectF();
		}

		if (pos != null && pos.length >= startIndex + len) {
			len = len / 2 * 2;
			for (int i = 0; i < len; i += 2) {
				if (i == 0) {
					out.left = pos[startIndex + i];
					out.right = pos[startIndex + i];
					out.top = pos[startIndex + i + 1];
					out.bottom = pos[startIndex + i + 1];
				} else {
					if (out.left > pos[startIndex + i]) {
						out.left = pos[startIndex + i];
					} else if (out.right < pos[startIndex + i]) {
						out.right = pos[startIndex + i];
					}
					if (out.top > pos[startIndex + i + 1]) {
						out.top = pos[startIndex + i + 1];
					} else if (out.bottom < pos[startIndex + i + 1]) {
						out.bottom = pos[startIndex + i + 1];
					}
				}
			}
		}

		return out;
	}

	public interface ControlCallback {
		Bitmap MakeShowImg(Object info, int frW, int frH);

		Bitmap MakeOutputImg(Object info, int outW, int outH);

		void UpdateSonWin(Bitmap bmp, int x, int y);

		void OnSelFaceIndex(int index);

		void OnClickSlimTool(float x1, float y1, float x2, float y2, float rw);

		void OnResetSlimTool(float rw);

		void OnDragSlim(float x1, float y1, float x2, float y2, float rw);

		void OnTouchDown(int mode);

		void OnTouchUp(int mode);
	}
}
