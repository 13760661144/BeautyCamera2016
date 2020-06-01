package cn.poco.nose.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.display.RelativeView;
import cn.poco.face.FaceDataV2;
import cn.poco.graphics.ShapeEx;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

import static cn.poco.beautify.BeautifyView.DivideRect;
import static cn.poco.beautify.BeautifyView.RectCross;

/**
 * Created by: fwc
 * Date: 2016/12/1
 */
public class ShapeView extends RelativeView {

	protected boolean m_isTouch = false;
	protected boolean m_isClick = false;

	protected PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	protected Paint temp_paint = new Paint();

	protected int def_click_size; //判断为click状态的最大size

	public ShapeEx m_img = null;
	public ArrayList<ShapeEx> m_targetArr = new ArrayList<>();

	protected ControlCallback m_cb;

	//双击触发
	protected long m_doubleClickTime = 0;
	protected float m_doubleClickX = 0;
	protected float m_doubleClickY = 0;

	public int def_anim_time = 400; //动画持续时间
	public int def_anim_type = TweenLite.EASING_BACK | TweenLite.EASE_OUT; //动画类型
	protected TweenLite m_tween = new TweenLite();
	protected float anim_old_scale;
	protected float anim_old_x;
	protected float anim_old_y;
	protected float anim_ds;
	protected float anim_dx;
	protected float anim_dy;

	public int def_face_anim_time = 400;
	public int def_face_anim_type = TweenLite.EASING_EXPO | TweenLite.EASE_OUT;

	protected boolean m_drawable = false; //控制是否可画

	private boolean mSelectFace = false;

	protected Matrix temp_matrix = new Matrix();
	protected float[] temp_dst = new float[8];
	protected float[] temp_src = new float[8];
	protected Path temp_path = new Path();
	protected float[] temp_dst3; //选择人脸用
	protected float[] temp_src3;
	protected PathEffect temp_effect = new DashPathEffect(new float[]{12, 6}, 1);
	protected ArrayList<RectF> temp_rect_arr = new ArrayList<>(); //GPU加速画矩形

	public int m_faceIndex; //多人脸选择哪个
	public boolean m_showSelFaceRect = true;

	public ShapeView(Context context, int frW, int frH) {
		super(context, frW, frH);

		m_origin.MAX_SCALE = 5f;
		m_origin.MIN_SCALE = 0.3f;

		def_click_size = ShareData.PxToDpi_xhdpi(20);
	}

	public void InitData(ControlCallback cb)
	{
		m_cb = cb;
	}

	/**
	 *
	 * @param info
	 * @param bmp
	 *            可以为null,为null时调用默认回调生成图片
	 */
	public void SetImg(Object info, Bitmap bmp)
	{
		m_img = new ShapeEx();
		if(bmp != null)
		{
			m_img.m_bmp = bmp;
		}
		else
		{
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


	public void UpdateViewport()
	{
		if(m_img != null)
		{
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
		}
		else
		{
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

	public void restore() {
		DoAnim(new RectF(0, 0, 1, 1), def_face_anim_type, def_face_anim_time, false);
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
				//System.out.println("double click");
				float[] src = new float[] {event.getX(), event.getY()};
				float[] dst = new float[2];
				GetLogicPos(dst, src);
				GetFaceLogicPos(src, dst);
				//System.out.println(src[0] + "," + src[1]);
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

		if (mSelectFace) {
			//判断选中哪个人脸
			if(m_isClick && m_cb != null && m_img != null)
			{
				if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
				{
					int len = FaceDataV2.FACE_POS_MULTI.length << 2;
					if(temp_src3 == null || temp_src3.length < len)
					{
						temp_src3 = new float[len];
						temp_dst3 = new float[len];
					}
					int index;
					for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
					{
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
					for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
					{
						index = i << 2;
						if(temp_dst3[index] < x && x < temp_dst3[index + 2] && temp_dst3[index + 1] < y && y < temp_dst3[index + 3])
						{
							m_cb.OnSelFaceIndex(i);
							break;
						}
					}
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
		if(m_img != null)
		{
			ArrayList<ShapeEx> arr = new ArrayList<>();
			arr.add(m_img);
			if(GetSelectIndex(arr, (m_downX1 + m_downX2) / 2f, (m_downY1 + m_downY2) / 2f) > -1)
			{
				m_target = m_origin;
				Init_NCZM_Data(m_target, m_downX1, m_downY1, m_downX2, m_downY2);
			}
		}
	}

	@Override
	protected void EvenMove(MotionEvent event) {
		if(m_target != null)
		{
			Run_NCZM(m_target, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
			this.invalidate();
		}
	}

	@Override
	protected void EvenUp(MotionEvent event) {
		m_isClick = false;
		OddUp(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(m_drawable && m_viewport.m_w > 0 && m_viewport.m_h > 0)
		{
			DrawToCanvas(canvas);
		}

		if(!m_isTouch && !m_tween.M1IsFinish())
		{
			float s = m_tween.M1GetPos();
			m_origin.m_scaleX = anim_old_scale + anim_ds * s;
			m_origin.m_scaleY = m_origin.m_scaleX;
			m_origin.m_x = anim_old_x + anim_dx * s;
			m_origin.m_y = anim_old_y + anim_dy * s;
			this.invalidate();
		}
	}

	protected void DrawToCanvas(Canvas canvas)
	{
		canvas.save();

		canvas.setDrawFilter(temp_filter);

		//控制渲染矩形
		ClipStage(canvas);

		//画图片
		DrawItem(canvas, m_img);

		//选择人脸模式
		if(mSelectFace && m_img != null) {
			//计算全部矩形
			int len = 4;
			if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			{
				len += FaceDataV2.FACE_POS_MULTI.length << 2;
			}
			if(temp_src3 == null || temp_src3.length < len)
			{
				temp_src3 = new float[len];
				temp_dst3 = new float[len];
			}
			temp_src3[0] = 0;
			temp_src3[1] = 0;
			temp_src3[2] = m_img.m_w;
			temp_src3[3] = m_img.m_h;
			if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			{
				int index;
				for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
				{
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
			if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			{
				int index;
				for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
				{
					index = 4 + (i << 2);
					temp_rect_arr.add(new RectF(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3]));
				}
			}
			ArrayList<RectF> arr = GetOutsideRect(new RectF(temp_dst3[0], temp_dst3[1], temp_dst3[2], temp_dst3[3]), temp_rect_arr);
			temp_path.reset();
			temp_path.setFillType(Path.FillType.WINDING);
			int rectLen = arr.size();
			for(int i = 0; i < rectLen; i++)
			{
				temp_path.addRect(arr.get(i), Path.Direction.CW);
			}
			temp_paint.reset();
			temp_paint.setColor(0x8c000000);
			temp_paint.setStyle(Paint.Style.FILL);
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawPath(temp_path, temp_paint);

			//画矩形边框
			if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			{
				temp_paint.reset();
				temp_paint.setColor(0xFFFFFFFF);
				temp_paint.setStyle(Paint.Style.STROKE);
				temp_paint.setStrokeWidth(3);
				temp_paint.setAntiAlias(true);
				temp_paint.setPathEffect(temp_effect);

				int index;
				for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
				{
					index = 4 + (i << 2);
					if(i != m_faceIndex || !m_showSelFaceRect)
					{
						canvas.drawRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], temp_paint);
					}
				}
				if(m_showSelFaceRect && m_faceIndex >= 0 && m_faceIndex < FaceDataV2.FACE_POS_MULTI.length)
				{
					index = 4 + (m_faceIndex << 2);
					temp_paint.setColor(cn.poco.advanced.ImageUtils.GetSkinColor(0xffe75988));
					canvas.drawRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], temp_paint);
				}
			}
		}

		canvas.restore();
	}

	public ArrayList<RectF> GetOutsideRect(RectF src, ArrayList<RectF> dst)
	{
		ArrayList<RectF> out = new ArrayList<RectF>();

		if(src != null && dst != null)
		{
			FixRect(src);
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				FixRect(dst.get(i));
			}

			ArrayList<RectF> arr = DivideRect(src, dst);
			int len2 = arr.size();

			RectF temp;
			CROSS: for(int i = 0; i < len2; i++)
			{
				temp = arr.get(i);
				for(int j = 0; j < len; j++)
				{
					if(RectCross(temp, dst.get(j)))
					{
						continue CROSS;
					}
				}
				out.add(temp);
			}
		}

		return out;
	}

	public void FixRect(RectF rect)
	{
		if(rect != null)
		{
			float temp;
			if(rect.left > rect.right)
			{
				temp = rect.left;
				rect.left = rect.right;
				rect.right = temp;
			}
			if(rect.top > rect.bottom)
			{
				temp = rect.top;
				rect.top = rect.bottom;
				rect.bottom = temp;
			}
		}
	}

	protected RectF temp_rect = new RectF();

	/**
	 * 比例矩形转换为显示矩形
	 */
	protected void ComputeShowRect(RectF in_out)
	{
		if(in_out != null && m_img != null)
		{
			float[] src = new float[4];
			float[] dst = {in_out.left, in_out.top, in_out.right, in_out.bottom};
			GetFaceShowPos(src, dst);
			GetShowPos(dst, src);
			in_out.left = dst[0];
			in_out.top = dst[1];
			in_out.right = dst[2];
			in_out.bottom = dst[3];
		}
	}

	/**
	 * 图片比例坐标转换为显示逻辑坐标(注意:非显示的实际坐标)
	 */
	protected boolean GetFaceShowPos(float[] dst, float[] src)
	{
		boolean out = false;

		if(m_img != null)
		{
			int len = src.length / 2 * 2;
			for(int i = 0; i < len; i += 2)
			{
				dst[i] = m_img.m_w * (src[i] - 1f / 2f) * m_img.m_scaleX + m_img.m_x + m_img.m_centerX;
				dst[i + 1] = m_img.m_h * (src[i + 1] - 1f / 2f) * m_img.m_scaleY + m_img.m_y + m_img.m_centerY;
			}

			out = true;
		}

		return out;
	}

	protected void ClipStage(Canvas canvas)
	{
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
		if(temp_dst[0] < 0)
		{
			temp_dst[0] = 0;
		}
		else
		{
			if(temp_dst[0] != (int)temp_dst[0])
			{
				temp_dst[0] += 0.5f; //浮点误差补偿
			}
		}
		if(temp_dst[1] < 0)
		{
			temp_dst[1] = 0;
		}
		else
		{
			if(temp_dst[1] != (int)temp_dst[1])
			{
				temp_dst[1] += 0.5f; //浮点误差补偿
			}
		}
		temp_dst[4] = (int)temp_dst[4];
		temp_dst[5] = (int)temp_dst[5];
		if(temp_dst[4] > this.getWidth())
		{
			temp_dst[4] = this.getWidth();
		}
		if(temp_dst[5] > this.getHeight())
		{
			temp_dst[5] = this.getHeight();
		}
		//System.out.println(temp_dst[0] + "," + temp_dst[1] + "," + temp_dst[4] + "," + temp_dst[5]);
		canvas.clipRect(temp_dst[0], temp_dst[1], temp_dst[4], temp_dst[5]);
	}

	protected void DrawItem(Canvas canvas, ShapeEx item)
	{
		if(item != null)
		{
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

	public void setSelectFace(boolean selectFace) {
		mSelectFace = selectFace;
	}

	public void DoAnim2Cheek()
	{
		if (m_faceIndex < 0 || m_faceIndex >= FaceDataV2.FACE_POS_MULTI.length) {
			return;
		}

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
		ZoomRect(rect, 2.2f);
		DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
	}

	public void ZoomRect(RectF rect, float scale)
	{
		float w2 = rect.width() * scale / 2f;
		float h2 = rect.height() * scale / 2f;
		float cx = (rect.left + rect.right) / 2f;
		float cy = (rect.top + rect.bottom) / 2f;
		rect.left = cx - w2;
		rect.right = cx + w2;
		rect.top = cy - h2;
		rect.bottom = cy + h2;
		if(rect.left < 0)
		{
			rect.left = 0;
		}
		else if(rect.left > 1)
		{
			rect.left = 1;
		}
		if(rect.right < 0)
		{
			rect.right = 0;
		}
		else if(rect.right > 1)
		{
			rect.right = 1;
		}
		if(rect.top < 0)
		{
			rect.top = 0;
		}
		else if(rect.top > 1)
		{
			rect.top = 1;
		}
		if(rect.bottom < 0)
		{
			rect.bottom = 0;
		}
		else if(rect.bottom > 1)
		{
			rect.bottom = 1;
		}
	}

	public RectF GetMinRect(float[] pos, int startIndex, int len)
	{
		return GetMinRect(pos, startIndex, len, null);
	}

	/**
	 *
	 * @param pos
	 * @param startIndex
	 * @param len
	 * @param recycle
	 *            重复利用对象,可为null
	 * @return
	 */
	public RectF GetMinRect(float[] pos, int startIndex, int len, RectF recycle)
	{
		RectF out;
		if(recycle != null)
		{
			out = recycle;
			out.setEmpty();
		}
		else
		{
			out = new RectF();
		}

		if(pos != null && pos.length >= startIndex + len)
		{
			len = len / 2 * 2;
			for(int i = 0; i < len; i += 2)
			{
				if(i == 0)
				{
					out.left = pos[startIndex + i];
					out.right = pos[startIndex + i];
					out.top = pos[startIndex + i + 1];
					out.bottom = pos[startIndex + i + 1];
				}
				else
				{
					if(out.left > pos[startIndex + i])
					{
						out.left = pos[startIndex + i];
					}
					else if(out.right < pos[startIndex + i])
					{
						out.right = pos[startIndex + i];
					}
					if(out.top > pos[startIndex + i + 1])
					{
						out.top = pos[startIndex + i + 1];
					}
					else if(out.bottom < pos[startIndex + i + 1])
					{
						out.bottom = pos[startIndex + i + 1];
					}
				}
			}
		}

		return out;
	}

	public Bitmap GetOutputBmp(int size)
	{
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

	public interface ControlCallback {
		Bitmap MakeShowImg(Object info, int frW, int frH);

		Bitmap MakeOutputImg(Object info, int outW, int outH);

		void OnSelFaceIndex(int index);
	}
}
