package cn.poco.beautify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.display.RelativeView;
import cn.poco.face.FaceDataV2;
import cn.poco.graphics.Shape;
import cn.poco.graphics.ShapeEx;
import cn.poco.imagecore.ProcessorV2;
import cn.poco.resource.MakeupType;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

public class BeautifyView extends RelativeView
{
	public int def_rotation_res = 0;
	public int def_zoom_res = 0;
	public int def_delete_res = 0;

	protected int def_click_size; //判断为click状态的最大size
	public int def_face_pos_touch_size;
	public float def_limit_sacle = 0.25f; //图片移出显示区的限制

	public int[] def_fix_face_res; //顺时针
	public int[] def_fix_eyebrow_res; //顺时针
	public int[] def_fix_eye_res; //顺时针
	public int[] def_fix_cheek_res; //左-右
	public int[] def_fix_lip_res; //左-上上-右-上中-下中-下下

	public int def_anim_time = 400; //动画持续时间
	public int def_anim_type = TweenLite.EASING_BACK | TweenLite.EASE_OUT; //动画类型
	protected TweenLite m_tween = new TweenLite();
	protected float anim_old_scale;
	protected float anim_old_x;
	protected float anim_old_y;
	protected float anim_ds;
	protected float anim_dx;
	protected float anim_dy;

	public int def_face_anim_time = 400; //定点专用,动画持续时间
	public int def_face_anim_type = TweenLite.EASING_EXPO | TweenLite.EASE_OUT;//定点专用,动画类型

	protected ArrayList<ShapeEx> m_facePos = new ArrayList<ShapeEx>();
	protected ArrayList<ShapeEx> m_leyebrowPos = new ArrayList<ShapeEx>();
	protected ArrayList<ShapeEx> m_reyebrowPos = new ArrayList<ShapeEx>();
	protected ArrayList<ShapeEx> m_leyePos = new ArrayList<ShapeEx>();
	protected ArrayList<ShapeEx> m_reyePos = new ArrayList<ShapeEx>();
	protected ArrayList<ShapeEx> m_lipPos = new ArrayList<ShapeEx>();
	protected ArrayList<ShapeEx> m_cheekPos = new ArrayList<ShapeEx>();

	protected ArrayList<MakeupShape> m_otherMakeup = new ArrayList<MakeupShape>();
	protected int m_otherMakeupSel = -1;

	public static final int POS_THREE = 0x0001;
	public static final int POS_EYE = 0x0002;
	public static final int POS_EYEBROW = 0x0004;
	public static final int POS_LIP = 0x0008;
	public static final int POS_CHEEK = 0x0010;
	public int m_showPosFlag; //显示哪些定位点
	public int m_touchPosFlag; //哪些定点可调整
	public int m_faceIndex; //多人脸选择哪个
	public boolean m_showSelFaceRect = true;
	public boolean m_moveAllFacePos = false;

	public ShapeEx m_img = null;
	public ArrayList<ShapeEx> m_targetArr = new ArrayList<ShapeEx>();

	public static final int MODE_ALL = 0x0001;
	public static final int MODE_FACE = 0x0002;
	public static final int MODE_MAKEUP = 0x0004;
	public static final int MODE_SEL_FACE = 0x0008;

	public static final int CTRL_R_Z = 0x0001;
	public static final int CTRL_NZ = 0x0002;
	public static final int CTRL_DEL = 0x0004;

	protected boolean m_isTouch = false;
	protected boolean m_isClick = false;
	protected boolean m_isOddCtrl = false; //单手操作
	protected int m_oddCtrlType; //单手操作类型

	protected int m_sonWinRadius;
	protected int m_sonWinX;
	protected int m_sonWinY;
	protected Bitmap m_sonWinBmp;
	protected Canvas m_sonWinCanvas;
	protected Bitmap m_tempSonWinBmp;
	protected Canvas m_tempSonWinCanvas;

	protected ShapeEx m_rotationBtn;
	protected ShapeEx m_nzoomBtn; //只能固定在右上角
	protected ShapeEx m_deleteBtn; //固定左上角
	protected boolean m_hasRotationBtn = true;
	protected boolean m_hasZoomBtn = true;

	protected boolean m_drawable = false; //控制是否可画
	protected int m_operateMode = MODE_ALL;

	protected boolean m_3Modify = false; //是否修改过3点定点
	protected boolean m_allModify = false; //是否修改过多点定点

	protected long m_showRectFlagTime;
	public int m_showRectFlagInterval = 300;
	protected ArrayList<RectF> m_rectFlags = new ArrayList<RectF>();

	//双击触发
	protected long m_doubleClickTime = 0;
	protected float m_doubleClickX = 0;
	protected float m_doubleClickY = 0;

	protected ControlCallback m_cb;

	protected PorterDuffColorFilter temp_color_filter;
	protected int mSkinColor;

	public BeautifyView(Context context, int frW, int frH)
	{
		super(context, frW, frH);

		m_origin.MAX_SCALE = 5f;
		m_origin.MIN_SCALE = 0.3f;

		ShareData.InitData((Activity)context);

		m_sonWinRadius = (int)(ShareData.m_screenWidth * 0.145f);

		def_click_size = ShareData.PxToDpi_xhdpi(20);
		def_face_pos_touch_size = ShareData.PxToDpi_hdpi(35);
		m_showPosFlag = 0;
		m_touchPosFlag = POS_EYE | POS_EYEBROW | POS_LIP | POS_THREE | POS_CHEEK;

		mSkinColor = cn.poco.advanced.ImageUtils.GetSkinColor(0xffe75988);

		//版本兼容
		if(android.os.Build.VERSION.SDK_INT >= 17)
		{
			temp_color_filter = new PorterDuffColorFilter(mSkinColor, PorterDuff.Mode.SRC_IN);
		}
	}

	public void InitData(ControlCallback cb)
	{
		m_cb = cb;

		Bitmap bmp;
		if(def_rotation_res != 0)
		{
			m_rotationBtn = new ShapeEx();
			bmp = BitmapFactory.decodeResource(getResources(), def_rotation_res);
			m_rotationBtn.m_bmp = bmp;
			m_rotationBtn.m_w = bmp.getWidth();
			m_rotationBtn.m_h = bmp.getHeight();
			m_rotationBtn.m_centerX = m_rotationBtn.m_w / 2f;
			m_rotationBtn.m_centerY = m_rotationBtn.m_h / 2f;
		}

		if(def_zoom_res != 0)
		{
			m_nzoomBtn = new ShapeEx();
			bmp = BitmapFactory.decodeResource(getResources(), def_zoom_res);
			m_nzoomBtn.m_bmp = bmp;
			m_nzoomBtn.m_w = bmp.getWidth();
			m_nzoomBtn.m_h = bmp.getHeight();
			m_nzoomBtn.m_centerX = (float)m_nzoomBtn.m_w / 2f;
			m_nzoomBtn.m_centerY = (float)m_nzoomBtn.m_h / 2f;
		}

		if(def_delete_res != 0)
		{
			m_deleteBtn = new ShapeEx();
			bmp = BitmapFactory.decodeResource(getResources(), def_delete_res);
			m_deleteBtn.m_bmp = bmp;
			m_deleteBtn.m_w = bmp.getWidth();
			m_deleteBtn.m_h = bmp.getHeight();
			m_deleteBtn.m_centerX = (float)m_deleteBtn.m_w / 2f;
			m_deleteBtn.m_centerY = (float)m_deleteBtn.m_h / 2f;
		}

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

		UpdatePosRotation();
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
			GetFaceShowPos(dst, src);
			item = arr.get(i);
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
				Data2UI(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 0, m_cheekPos);
			}
		}
		if(FaceDataV2.LIP_POS_MULTI != null)
		{
			if(m_lipPos != null)
			{
				Data2UI(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, m_lipPos);
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
			GetFaceLogicPos(dst, src);
			index = startIndex + i * 2;
			datas[index] = dst[0];
			datas[index + 1] = dst[1];
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
				UI2Data(m_cheekPos, FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 0);
			}
		}
		if(FaceDataV2.LIP_POS_MULTI != null)
		{
			if(m_lipPos != null)
			{
				UI2Data(m_lipPos, FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0);
			}
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

	protected PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	protected Paint temp_paint = new Paint();
	protected boolean m_isFacePos; //是否点击了脸部定点点

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(m_drawable && m_viewport.m_w > 0 && m_viewport.m_h > 0)
		{
			DrawToCanvas(canvas, m_operateMode);

			if((m_operateMode == MODE_FACE || m_operateMode == MODE_MAKEUP) && m_isTouch && m_isFacePos)
			{
				DrawToSonWin2(m_target);

				if(m_cb != null)
				{
					m_cb.UpdateSonWin(m_sonWinBmp, m_sonWinX, m_sonWinY);
				}
			}
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

	/**
	 * 判断点击的是不是脸部定点点
	 *
	 * @param item
	 * @return
	 */
	protected boolean IsFacePos(ShapeEx item)
	{
		boolean out = false;

		if(item != null && item != m_origin && item != m_rotationBtn && item != m_nzoomBtn && item != m_deleteBtn)
		{
			int len = m_otherMakeup.size();
			for(int i = 0; i < len; i++)
			{
				if(item == m_otherMakeup.get(i))
				{
					return false;
				}
			}
			out = true;
		}

		return out;
	}

	protected Matrix temp_matrix = new Matrix();
	protected float[] temp_dst = new float[8];
	protected float[] temp_src = new float[8];
	protected Path temp_path = new Path();
	protected float[] temp_dst3; //选择人脸用
	protected float[] temp_src3;
	protected PathEffect temp_effect = new DashPathEffect(new float[]{12, 6}, 1);
	protected ArrayList<RectF> temp_rect_arr = new ArrayList<RectF>(); //GPU加速画矩形

	protected void DrawToCanvas(Canvas canvas, int mode)
	{
		canvas.save();

		canvas.setDrawFilter(temp_filter);

		//控制渲染矩形
		ClipStage(canvas);

		//画图片
		DrawItem(canvas, m_img);

		//其他彩妆
		{
			int len = m_otherMakeup.size();
			for(int i = 0; i < len; i++)
			{
				DrawItem(canvas, m_otherMakeup.get(i));
			}

			//画选中框和按钮
			if(m_otherMakeupSel >= 0 && m_otherMakeupSel < m_otherMakeup.size())
			{
				ShapeEx temp = m_otherMakeup.get(m_otherMakeupSel);
				//画选中框
				DrawRect(canvas, temp);

				//显示单手旋转放大按钮
				if(!m_isTouch)
				{
					DrawButtons(canvas, temp);
				}
			}
		}

		//画定点
		DrawPoint(canvas);

		DrawRectFlag(canvas);

		//选择人脸模式
		if(mode == MODE_SEL_FACE && m_img != null)
		{
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
			temp_path.setFillType(FillType.WINDING);
			int rectLen = arr.size();
			for(int i = 0; i < rectLen; i++)
			{
				temp_path.addRect(arr.get(i), Path.Direction.CW);
			}
			//temp_path.addRect(temp_dst3[0], temp_dst3[1], temp_dst3[2], temp_dst3[3], Path.Direction.CW);
			//if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			//{
			//	int index;
			//	for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
			//	{
			//		index = 4 + (i << 2);
			//		temp_path.addRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], Path.Direction.CW);
			//	}
			//}
			temp_paint.reset();
			temp_paint.setColor(0x8c000000);
			temp_paint.setStyle(Style.FILL);
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawPath(temp_path, temp_paint);

			//画矩形边框
			if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			{
				temp_paint.reset();
				temp_paint.setColor(0xFFFFFFFF);
				temp_paint.setStyle(Style.STROKE);
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
					temp_paint.setColor(mSkinColor);
					canvas.drawRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], temp_paint);
				}
			}
		}

		canvas.restore();
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
					DrawButton2(canvas, m_facePos.get(i));
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
					DrawButton2(canvas, m_leyebrowPos.get(i));
				}
			}
			if(m_reyebrowPos != null)
			{
				int len = m_reyebrowPos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton2(canvas, m_reyebrowPos.get(i));
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
					DrawButton2(canvas, m_leyePos.get(i));
				}
			}
			if(m_reyePos != null)
			{
				int len = m_reyePos.size();
				for(int i = 0; i < len; i++)
				{
					DrawButton2(canvas, m_reyePos.get(i));
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
					DrawButton2(canvas, m_cheekPos.get(i));
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
					DrawButton2(canvas, m_lipPos.get(i));
				}
			}
		}
	}

	protected void DrawRect(Canvas canvas, ShapeEx item)
	{
		if(item != null)
		{
			GetShowMatrix(temp_matrix, item);

			//画选中框
			temp_src[0] = 0;
			temp_src[1] = 0;
			temp_src[2] = item.m_w;
			temp_src[3] = 0;
			temp_src[4] = item.m_w;
			temp_src[5] = item.m_h;
			temp_src[6] = 0;
			temp_src[7] = item.m_h;
			temp_matrix.mapPoints(temp_dst, temp_src);
			temp_paint.reset();
			temp_paint.setStyle(Style.STROKE);
			temp_paint.setColor(0xA0FFFFFF);
			temp_paint.setStrokeCap(Paint.Cap.SQUARE);
			temp_paint.setStrokeJoin(Paint.Join.MITER);
			temp_paint.setStrokeWidth(2);
			canvas.drawLine(temp_dst[0], temp_dst[1], temp_dst[2], temp_dst[3], temp_paint);
			canvas.drawLine(temp_dst[2], temp_dst[3], temp_dst[4], temp_dst[5], temp_paint);
			canvas.drawLine(temp_dst[4], temp_dst[5], temp_dst[6], temp_dst[7], temp_paint);
			canvas.drawLine(temp_dst[6], temp_dst[7], temp_dst[0], temp_dst[1], temp_paint);
			//GPU兼容问题
			//temp_path.reset();
			//temp_path.moveTo(temp_dst[0], temp_dst[1]);
			//temp_path.lineTo(temp_dst[2], temp_dst[3]);
			//temp_path.lineTo(temp_dst[4], temp_dst[5]);
			//temp_path.lineTo(temp_dst[6], temp_dst[7]);
			//temp_path.close();
			//canvas.drawPath(temp_path, temp_paint);
		}
	}

	protected float[] temp_point_src = {0, 0};
	protected float[] temp_point_dst = {0, 0};

	protected void DrawButtons(Canvas canvas, ShapeEx item)
	{
		if(item != null)
		{
			//移动到正确位置
			temp_matrix.reset();
			temp_point_src[0] = item.m_x + item.m_centerX;
			temp_point_src[1] = item.m_y + item.m_centerY;
			float[] cxy = new float[2];
			GetShowPos(cxy, temp_point_src);
			temp_matrix.postTranslate(cxy[0] - item.m_centerX, cxy[1] - item.m_centerY);
			temp_matrix.postScale(item.m_scaleX * m_origin.m_scaleX, item.m_scaleY * m_origin.m_scaleY, cxy[0], cxy[1]);
			temp_src[0] = 0;
			temp_src[1] = 0;
			temp_src[2] = item.m_w;
			temp_src[3] = 0;
			temp_src[4] = item.m_w;
			temp_src[5] = item.m_h;
			temp_src[6] = 0;
			temp_src[7] = item.m_h;
			temp_matrix.mapPoints(temp_dst, temp_src);

			float[] dst = new float[8];
			float[] dst2 = new float[8];
			if(m_rotationBtn != null && m_hasRotationBtn)
			{
				//计算按钮的位置
				temp_src[0] = temp_dst[0] - m_rotationBtn.m_centerX;
				temp_src[1] = temp_dst[1] - m_rotationBtn.m_centerY;
				temp_src[2] = temp_dst[2] + m_rotationBtn.m_centerX;
				temp_src[3] = temp_dst[3] - m_rotationBtn.m_centerY;
				temp_src[4] = temp_dst[4] + m_rotationBtn.m_centerX;
				temp_src[5] = temp_dst[5] + m_rotationBtn.m_centerY;
				temp_src[6] = temp_dst[6] - m_rotationBtn.m_centerX;
				temp_src[7] = temp_dst[7] + m_rotationBtn.m_centerY;
				Matrix matrix = new Matrix();
				matrix.postRotate(item.m_degree, cxy[0], cxy[1]);
				matrix.mapPoints(dst, temp_src);

				//测试用
				//temp_path.reset();
				//temp_path.moveTo(dst[0], dst[1]);
				//temp_path.lineTo(dst[2], dst[3]);
				//temp_path.lineTo(dst[4], dst[5]);
				//temp_path.lineTo(dst[6], dst[7]);
				//temp_path.close();
				//temp_paint.setColor(0xFF00FF00);
				//canvas.drawPath(temp_path, temp_paint);

				temp_src[0] = 0;
				temp_src[1] = 0;
				temp_src[2] = m_viewport.m_w;
				temp_src[3] = 0;
				temp_src[4] = m_viewport.m_w;
				temp_src[5] = m_viewport.m_h;
				temp_src[6] = 0;
				temp_src[7] = m_viewport.m_h;
				GetShowMatrix(matrix, m_viewport);
				matrix.mapPoints(dst2, temp_src);

				if(dst2[0] < m_origin.m_w && dst2[1] < m_origin.m_h && dst2[4] > 0 && dst2[5] > 0)
				{
					//边界 左上角(a,b) 右下角(c,d)
					float a = ((dst2[0] < 0) ? 0 : dst2[0]);
					float b = ((dst2[1] < 0) ? 0 : dst2[1]);
					float c = ((dst2[4] > m_origin.m_w) ? m_origin.m_w : dst2[4]);
					float d = ((dst2[5] > m_origin.m_h) ? m_origin.m_h : dst2[5]);
					if(c - a > m_rotationBtn.m_w)
					{
						a += m_rotationBtn.m_centerX;
						c -= m_rotationBtn.m_centerX;
					}
					if(d - b > m_rotationBtn.m_h)
					{
						b += m_rotationBtn.m_centerY;
						d -= m_rotationBtn.m_centerY;
					}

					//测试用
					//temp_path.reset();
					//temp_path.moveTo(a, b);
					//temp_path.lineTo(c, b);
					//temp_path.lineTo(c, d);
					//temp_path.lineTo(a, d);
					//temp_path.close();
					//temp_paint.setColor(0xFF0000FF);
					//canvas.drawPath(temp_path, temp_paint);

					float p0x = (a + c) / 2f;
					float p0y = (b + d) / 2f;

					//(dst[4], dst[5])右下角坐标
					if(dst[4] > a && dst[4] < c && dst[5] > b && dst[5] < d)
					{
						temp_point_src[0] = dst[4];
						temp_point_src[1] = dst[5];
					}
					//(dst[6], dst[7])左下角坐标
					else if(dst[6] > a && dst[6] < c && dst[7] > b && dst[7] < d)
					{
						temp_point_src[0] = dst[6];
						temp_point_src[1] = dst[7];
					}
					//(dst[0], dst[1])左上角坐标
					else if(m_deleteBtn == null && dst[0] > a && dst[0] < c && dst[1] > b && dst[1] < d)
					{
						temp_point_src[0] = dst[0];
						temp_point_src[1] = dst[1];
					}
					//(dst[2], dst[3])右上角坐标,注意和其他按钮冲突
					else if((m_nzoomBtn == null || !m_hasZoomBtn) && dst[2] > a && dst[2] < c && dst[3] > b && dst[3] < d)
					{
						temp_point_src[0] = dst[2];
						temp_point_src[1] = dst[3];
					}
					else
					{
						float d1 = 0;
						if(m_deleteBtn != null)
						{
							d1 = 999999f; //极大值
						}
						else
						{
							d1 = ImageUtils.Spacing(p0x - dst[0], p0y - dst[1]);
						}
						float d2 = 0;
						if(m_nzoomBtn != null && m_hasZoomBtn)
						{
							d2 = 999999f; //极大值
						}
						else
						{
							d2 = ImageUtils.Spacing(p0x - dst[2], p0y - dst[3]);
						}
						float d3 = ImageUtils.Spacing(p0x - dst[4], p0y - dst[5]);
						float d4 = ImageUtils.Spacing(p0x - dst[6], p0y - dst[7]);

						float min = Math.min(Math.min(Math.min(d1, d2), d3), d4);
						if(min == d3)
						{
							temp_point_src[0] = dst[4];
							temp_point_src[1] = dst[5];
						}
						else if(min == d2)
						{
							temp_point_src[0] = dst[2];
							temp_point_src[1] = dst[3];
						}
						else if(min == d4)
						{
							temp_point_src[0] = dst[6];
							temp_point_src[1] = dst[7];
						}
						else
						{
							temp_point_src[0] = dst[0];
							temp_point_src[1] = dst[1];
						}
					}

					GetLogicPos(temp_point_dst, temp_point_src);
					m_rotationBtn.m_x = temp_point_dst[0] - m_rotationBtn.m_centerX;
					m_rotationBtn.m_y = temp_point_dst[1] - m_rotationBtn.m_centerY;

					temp_paint.reset();
					temp_paint.setAntiAlias(true);
					temp_paint.setFilterBitmap(true);
					GetShowMatrixNoScale(temp_matrix, m_rotationBtn);
					canvas.drawBitmap(m_rotationBtn.m_bmp, temp_matrix, temp_paint);
				}
			}

			//上下顺序不能调换
			if(m_nzoomBtn != null && m_hasZoomBtn)
			{
				temp_src[0] = temp_dst[0] - m_nzoomBtn.m_centerX;
				temp_src[1] = temp_dst[1] - m_nzoomBtn.m_centerY;
				temp_src[2] = temp_dst[2] + m_nzoomBtn.m_centerX;
				temp_src[3] = temp_dst[3] - m_nzoomBtn.m_centerY;
				temp_src[4] = temp_dst[4] + m_nzoomBtn.m_centerX;
				temp_src[5] = temp_dst[5] + m_nzoomBtn.m_centerY;
				temp_src[6] = temp_dst[6] - m_nzoomBtn.m_centerX;
				temp_src[7] = temp_dst[7] + m_nzoomBtn.m_centerY;

				temp_matrix.reset();
				temp_matrix.postRotate(item.m_degree, cxy[0], cxy[1]);
				temp_matrix.mapPoints(dst, temp_src);

				//测试用
				//temp_path.reset();
				//temp_path.moveTo(temp_dst[0], temp_dst[1]);
				//temp_path.lineTo(temp_dst[2], temp_dst[3]);
				//temp_path.lineTo(temp_dst[4], temp_dst[5]);
				//temp_path.lineTo(temp_dst[6], temp_dst[7]);
				//temp_path.close();
				//temp_paint.setColor(0xFFFF0000);
				//canvas.drawPath(temp_path, temp_paint);

				temp_point_src[0] = dst[2];
				temp_point_src[1] = dst[3];
				GetLogicPos(temp_point_dst, temp_point_src);
				m_nzoomBtn.m_x = temp_point_dst[0] - m_nzoomBtn.m_centerX;
				m_nzoomBtn.m_y = temp_point_dst[1] - m_nzoomBtn.m_centerY;

				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				GetShowMatrixNoScale(temp_matrix, m_nzoomBtn);
				canvas.drawBitmap(m_nzoomBtn.m_bmp, temp_matrix, temp_paint);
			}
			if(m_deleteBtn != null)
			{
				temp_src[0] = temp_dst[0] - m_deleteBtn.m_centerX / 2;
				temp_src[1] = temp_dst[1] - m_deleteBtn.m_centerY / 2;
				temp_src[2] = temp_dst[2] + m_deleteBtn.m_centerX / 2;
				temp_src[3] = temp_dst[3] - m_deleteBtn.m_centerY / 2;
				temp_src[4] = temp_dst[4] + m_deleteBtn.m_centerX / 2;
				temp_src[5] = temp_dst[5] + m_deleteBtn.m_centerY / 2;
				temp_src[6] = temp_dst[6] - m_deleteBtn.m_centerX / 2;
				temp_src[7] = temp_dst[7] + m_deleteBtn.m_centerY / 2;

				temp_matrix.reset();
				temp_matrix.postRotate(item.m_degree, cxy[0], cxy[1]);
				temp_matrix.mapPoints(dst, temp_src);

				temp_point_src[0] = dst[0];
				temp_point_src[1] = dst[1];
				GetLogicPos(temp_point_dst, temp_point_src);
				m_deleteBtn.m_x = temp_point_dst[0] - m_deleteBtn.m_centerX;
				m_deleteBtn.m_y = temp_point_dst[1] - m_deleteBtn.m_centerY;

				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				GetShowMatrixNoScale(temp_matrix, m_deleteBtn);
				canvas.drawBitmap(m_deleteBtn.m_bmp, temp_matrix, temp_paint);
			}
		}
	}

	protected RectF temp_rect = new RectF();

	/**
	 * 画五官位置矩形框
	 *
	 * @param canvas
	 */
	protected void DrawRectFlag(Canvas canvas)
	{
		long ctime = System.currentTimeMillis();
		if(m_showRectFlagTime > ctime)
		{
			if(((m_showRectFlagTime - ctime) / m_showRectFlagInterval) % 2 == 0)
			{
				temp_paint.reset();
				temp_paint.setStyle(Style.STROKE);
				temp_paint.setColor(0xFFFFFFFF);
				temp_paint.setStrokeCap(Paint.Cap.SQUARE);
				temp_paint.setStrokeJoin(Paint.Join.MITER);

				int len = m_rectFlags.size();
				if(len > 0)
				{
					temp_rect.set(m_rectFlags.get(0));
					ComputeShowRect(temp_rect);
					float r = (temp_rect.width() > temp_rect.height() ? temp_rect.width() : temp_rect.height()) / 8f;
					float r1 = temp_rect.width() / 3f;
					float r2 = temp_rect.height() / 3f;
					if(r > r1)
					{
						r = r1;
					}
					if(r > r2)
					{
						r = r2;
					}
					if(r < 1)
					{
						r = 1;
					}
					float rw = r;
					float rh = r;
					float bw = r / 10f;
					if(bw < 1)
					{
						bw = 1;
					}
					temp_paint.setStrokeWidth(bw);

					for(int i = 0; i < len; i++)
					{
						temp_rect.set(m_rectFlags.get(i));
						ComputeShowRect(temp_rect);

						canvas.drawLine(temp_rect.left, temp_rect.top, temp_rect.left + rw, temp_rect.top, temp_paint);
						canvas.drawLine(temp_rect.left, temp_rect.top, temp_rect.left, temp_rect.top + rh, temp_paint);
						canvas.drawLine(temp_rect.right, temp_rect.top, temp_rect.right - rw, temp_rect.top, temp_paint);
						canvas.drawLine(temp_rect.right, temp_rect.top, temp_rect.right, temp_rect.top + rh, temp_paint);
						canvas.drawLine(temp_rect.right, temp_rect.bottom, temp_rect.right - rw, temp_rect.bottom, temp_paint);
						canvas.drawLine(temp_rect.right, temp_rect.bottom, temp_rect.right, temp_rect.bottom - rh, temp_paint);
						canvas.drawLine(temp_rect.left, temp_rect.bottom, temp_rect.left + rw, temp_rect.bottom, temp_paint);
						canvas.drawLine(temp_rect.left, temp_rect.bottom, temp_rect.left, temp_rect.bottom - rh, temp_paint);
					}
				}
			}

			this.invalidate();
		}
	}

	public int AddOtherMakeup(MakeupShape item)
	{
		m_otherMakeup.add(item);

		return m_otherMakeup.size() - 1;
	}

	public void AddOtherMakeup(ArrayList<MakeupShape> arr)
	{
		if(arr != null)
		{
			m_otherMakeup.addAll(arr);
		}
	}

	/**
	 *
	 * @param faceID
	 *            -1:清理全部
	 */
	public void ClearOtherMakeup(int faceID)
	{
		if(faceID == -1)
		{
			m_otherMakeup.clear();
		}
		else
		{
			int len = m_otherMakeup.size();
			for(int i = 0; i < len; i++)
			{
				if(m_otherMakeup.get(i).m_faceID == faceID)
				{
					m_otherMakeup.remove(i);
					len--;
					i--;
				}
			}
		}
	}

	public void SetShowRectFlag()
	{
		m_showRectFlagTime = System.currentTimeMillis() + m_showRectFlagInterval * 3;
		this.invalidate();
	}

	/**
	 * 获取眉毛最小矩形
	 *
	 * @param in_out
	 * @param l
	 *            可以为null,为null时会重新获取
	 * @param r
	 *            可以为null,为null时会重新获取
	 * @param isShow
	 */
	protected void AddEyebrowRectFlag(ArrayList<RectF> in_out, RectF l, RectF r, boolean isShow)
	{
		if(in_out != null && FaceDataV2.EYEBROW_POS_MULTI != null)
		{
			RectF temp;
			if(l != null)
			{
				temp = l;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);

			if(r != null)
			{
				temp = r;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 获取眼睛最小矩形
	 *
	 * @param in_out
	 * @param l
	 *            可以为null,为null时会重新获取
	 * @param r
	 *            可以为null,为null时会重新获取
	 * @param isShow
	 */
	protected void AddEyeRectFlag(ArrayList<RectF> in_out, RectF l, RectF r, boolean isShow)
	{
		if(in_out != null && FaceDataV2.EYE_POS_MULTI != null)
		{
			RectF temp;
			if(l != null)
			{
				temp = l;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 10);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);

			if(r != null)
			{
				temp = r;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, 10);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 获取腮红最小矩形
	 *
	 * @param in_out
	 * @param l
	 *            可以为null,为null时会重新获取
	 * @param r
	 *            可以为null,为null时会重新获取
	 * @param isShow
	 */
	protected void AddCheckRectFlag(ArrayList<RectF> in_out, RectF l, RectF r, boolean isShow)
	{
		if(in_out != null && FaceDataV2.CHEEK_POS_MULTI != null)
		{
			RectF temp;
			if(l != null)
			{
				temp = l;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 4, 4);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);

			if(r != null)
			{
				temp = r;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 8, 4);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);
		}
	}

	protected void AddFoundationRectFlag(ArrayList<RectF> in_out, RectF foundation, boolean isShow)
	{
		if(in_out != null && FaceDataV2.FACE_POS_MULTI != null)
		{
			RectF temp;
			if(foundation != null)
			{
				temp = foundation;
			}
			else
			{
				float[] pos = FaceDataV2.FACE_POS_MULTI[m_faceIndex];
				temp = new RectF(pos[6], pos[7], pos[6] + pos[8], pos[7] + pos[9]);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 获取嘴唇最小矩形
	 *
	 * @param in_out
	 * @param lip
	 *            可以为null,为null时会重新获取
	 * @param isShow
	 */
	protected void AddLipRectFlag(ArrayList<RectF> in_out, RectF lip, boolean isShow)
	{
		if(in_out != null && FaceDataV2.LIP_POS_MULTI != null)
		{
			RectF temp;
			if(lip != null)
			{
				temp = lip;
			}
			else
			{
				temp = GetMinRect(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, 32);
			}
			if(isShow)
			{
				ComputeShowRect(temp);
			}
			in_out.add(temp);
		}
	}

	/**
	 * 比例矩形转换为显示矩形
	 *
	 * @param in_out
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

	protected void DrawToSonWin(ShapeEx item)
	{
		if(item != null && m_img != null && m_img.m_bmp != null && !m_img.m_bmp.isRecycled())
		{
			int size = m_sonWinRadius * 2;
			int offset = ShareData.PxToDpi_xhdpi(10);
			int border = ShareData.PxToDpi_xhdpi(5);

			if(m_sonWinBmp == null || m_sonWinCanvas == null)
			{
				if(m_sonWinBmp != null)
				{
					m_sonWinBmp.recycle();
					m_sonWinBmp = null;
				}
				m_sonWinBmp = Bitmap.createBitmap(size, size, Config.ARGB_8888);
				m_sonWinCanvas = new Canvas(m_sonWinBmp);
				m_sonWinCanvas.setDrawFilter(temp_filter);
			}

			//清理
			m_sonWinCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

			//draw mask
			temp_paint.reset();
			temp_paint.setStyle(Style.FILL);
			temp_paint.setColor(0xFFFFFFFF);
			temp_paint.setAntiAlias(true);
			m_sonWinCanvas.drawRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, temp_paint);

			//画图
			float[] src = {item.m_x + item.m_centerX, item.m_y + item.m_centerY};
			float[] dst = new float[2];
			GetFaceLogicPos(dst, src);
			dst[0] *= m_img.m_w;
			dst[1] *= m_img.m_h;
			temp_matrix.reset();
			temp_matrix.postTranslate(m_sonWinRadius - dst[0], m_sonWinRadius - dst[1]);
			temp_matrix.postScale(m_origin.m_scaleX * m_img.m_scaleX, m_origin.m_scaleY * m_img.m_scaleY, m_sonWinRadius, m_sonWinRadius);
			temp_paint.reset();
			temp_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			temp_paint.setFilterBitmap(true);
			m_sonWinCanvas.drawBitmap(m_img.m_bmp, temp_matrix, temp_paint);

			//画定点图标
			temp_matrix.reset();
			temp_matrix.postTranslate((m_sonWinBmp.getWidth() - item.m_w) / 2f, (m_sonWinBmp.getHeight() - item.m_h) / 2f);
			temp_matrix.postRotate(item.m_degree, m_sonWinBmp.getWidth() / 2f, m_sonWinBmp.getHeight() / 2f);
			temp_paint.reset();
			if(temp_color_filter != null)
			{
				temp_paint.setColorFilter(temp_color_filter);
			}
			m_sonWinCanvas.drawBitmap(item.m_bmp, temp_matrix, temp_paint);

			//画白边
			temp_paint.reset();
			temp_paint.setStyle(Style.FILL);
			temp_paint.setColor(0xA0FFFFFF);
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			temp_path.reset();
			temp_path.setFillType(FillType.EVEN_ODD);
			temp_path.addRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, Path.Direction.CW);
			temp_path.addRoundRect(new RectF(offset + border, offset + border, size - offset - border, size - offset - border), border << 1, border << 1, Path.Direction.CW);
			m_sonWinCanvas.drawPath(temp_path, temp_paint);
		}
	}

	protected void DrawToSonWin2(ShapeEx item)
	{
		if(item != null && m_img != null && m_img.m_bmp != null && !m_img.m_bmp.isRecycled())
		{
			int size = m_sonWinRadius * 2;
			int offset = ShareData.PxToDpi_xhdpi(10);
			int border = ShareData.PxToDpi_xhdpi(5);

			if(m_sonWinBmp == null || m_sonWinCanvas == null)
			{
				if(m_sonWinBmp != null)
				{
					m_sonWinBmp.recycle();
					m_sonWinBmp = null;
				}
				m_sonWinBmp = Bitmap.createBitmap(size, size, Config.ARGB_8888);
				m_sonWinCanvas = new Canvas(m_sonWinBmp);
				m_sonWinCanvas.setDrawFilter(temp_filter);
			}
			if(m_tempSonWinBmp == null)
			{
				m_tempSonWinBmp = m_sonWinBmp.copy(Config.ARGB_8888, true);
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
			DrawToCanvas(m_tempSonWinCanvas, m_operateMode);
			m_tempSonWinCanvas.restore();

			//draw mask
			temp_paint.reset();
			temp_paint.setStyle(Style.FILL);
			temp_paint.setColor(0xFFFFFFFF);
			temp_paint.setAntiAlias(true);
			m_sonWinCanvas.drawRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, temp_paint);

			//临时画到sonWin
			temp_paint.reset();
			temp_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			temp_paint.setFilterBitmap(true);
			m_sonWinCanvas.drawBitmap(m_tempSonWinBmp, 0, 0, temp_paint);

			//画白边
			temp_paint.reset();
			temp_paint.setStyle(Style.FILL);
			temp_paint.setColor(0xA0FFFFFF);
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			temp_path.reset();
			temp_path.setFillType(FillType.EVEN_ODD);
			temp_path.addRoundRect(new RectF(offset, offset, size - offset, size - offset), border << 1, border << 1, Path.Direction.CW);
			temp_path.addRoundRect(new RectF(offset + border, offset + border, size - offset - border, size - offset - border), border << 1, border << 1, Path.Direction.CW);
			m_sonWinCanvas.drawPath(temp_path, temp_paint);
		}
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
		//temp_paint.reset();
		//temp_paint.setStyle(Paint.Style.FILL);
		//temp_paint.setAntiAlias(true);
		//temp_paint.setFilterBitmap(true);
		//temp_paint.setColor(0xFFFFFFFF);
		//temp_paint.setXfermode(temp_xfermode);
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

	protected void DrawButton(Canvas canvas, ShapeEx item)
	{
		if(item != null)
		{
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			GetShowMatrixNoScale(temp_matrix, item);
			canvas.drawBitmap(item.m_bmp, temp_matrix, temp_paint);
		}
	}

	/**
	 * 有选中filter
	 *
	 * @param canvas
	 * @param item
	 */
	protected void DrawButton2(Canvas canvas, ShapeEx item)
	{
		if(item != null)
		{
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			if((m_moveAllFacePos || m_target == item) && temp_color_filter != null)
			{
				temp_paint.setColorFilter(temp_color_filter);
			}
			GetShowMatrixNoScale(temp_matrix, item);
			canvas.drawBitmap(item.m_bmp, temp_matrix, temp_paint);
		}
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

	/**
	 * 显示的逻辑转为图片比例坐标
	 *
	 * @param dst
	 * @param src
	 * @return
	 */
	protected boolean GetFaceLogicPos(float[] dst, float[] src)
	{
		boolean out = false;

		if(m_img != null)
		{
			int len = src.length / 2 * 2;
			for(int i = 0; i < len; i += 2)
			{
				dst[i] = (src[i] - m_img.m_x - m_img.m_centerX) / (m_img.m_w * m_img.m_scaleX) + 1f / 2f;
				dst[i + 1] = (src[i + 1] - m_img.m_y - m_img.m_centerY) / (m_img.m_h * m_img.m_scaleY) + 1f / 2f;
			}

			out = true;
		}

		return out;
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
		else
		{
			int index = m_cheekPos.indexOf(item);
			if(index == 0)
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
				GetShowPos(dst, src);
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

	public void SetOperateMode(int mode)
	{
		m_otherMakeupSel = -1;
		m_moveAllFacePos = false;

		switch(mode)
		{
			case MODE_FACE:
				m_operateMode = MODE_FACE;
				break;

			case MODE_MAKEUP:
				m_operateMode = MODE_MAKEUP;
				break;

			case MODE_SEL_FACE:
				m_operateMode = MODE_SEL_FACE;
				break;

			default:
				m_operateMode = MODE_ALL;
				break;
		}
	}

	protected void RefreshSonWinPos(float x, float y)
	{
		int size = m_sonWinRadius * 2;
		if(x < size && y < size)
		{
			m_sonWinX = m_origin.m_w - size;
			m_sonWinY = 0;
		}
		else if(x > m_origin.m_w - size && y < size)
		{
			m_sonWinX = 0;
			m_sonWinY = 0;
		}
	}

	public void UpdateUI()
	{
		this.invalidate();
	}

	public void CreateViewBuffer()
	{
		ClearViewBuffer();

		m_drawable = true;
	}

	public void ClearViewBuffer()
	{
		m_drawable = false;
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

		int len = m_otherMakeup.size();
		ShapeEx temp;
		for(int i = 0; i < len; i++)
		{
			temp = m_otherMakeup.get(i);
			if(temp.m_bmp != null)
			{
				temp.m_bmp.recycle();
				temp.m_bmp = null;
			}
		}
	}

	protected float temp_showCX;
	protected float temp_showCY;

	@Override
	protected void OddDown(MotionEvent event)
	{
		m_isTouch = true;
		m_isClick = true;
		m_tween.M1End();

		switch(m_operateMode)
		{
			case MODE_FACE:
			{
				int index = -1;
				if((m_touchPosFlag & POS_THREE) != 0)
				{
					index = IsClickFacePos(m_facePos, m_downX, m_downY, def_face_pos_touch_size);
				}
				if(index >= 0)
				{
					m_3Modify = true;
					m_target = m_facePos.get(index);
					RefreshSonWinPos(m_downX, m_downY);
					this.invalidate();
				}
				else
				{
					m_target = m_origin;
				}

				Init_M_Data(m_target, m_downX, m_downY);
				break;
			}

			case MODE_MAKEUP:
			{
				//移动全部点
				if(m_moveAllFacePos && (m_touchPosFlag & (POS_EYEBROW | POS_EYE | POS_LIP)) != 0)
				{
					MakeupType type = GetClickFacePos(m_downX, m_downY, null, false);
					if(type != null && type != MakeupType.CHEEK_L && type != MakeupType.CHEEK_R)
					{
						m_target = null;
						m_targetArr.clear();
						switch(type)
						{
							case EYEBROW_L:
								m_targetArr.addAll(m_leyebrowPos);
								break;

							case EYEBROW_R:
								m_targetArr.addAll(m_reyebrowPos);
								break;

							case EYE_L:
								m_targetArr.addAll(m_leyePos);
								break;

							case EYE_R:
								m_targetArr.addAll(m_reyePos);
								break;

							case LIP:
								m_targetArr.addAll(m_lipPos);
								break;

							default:
								break;
						}
						m_allModify = true;
						Init_M_All_Data(m_targetArr, m_downX, m_downY);
						this.invalidate();

						m_otherMakeupSel = -1;
						break;
					}
				}
				ArrayList<ShapeEx> arr = new ArrayList<ShapeEx>();
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
				int index = IsClickFacePos(arr, m_downX, m_downY, def_face_pos_touch_size);
				if(index >= 0)
				{
					if(m_moveAllFacePos)
					{
						m_target = null;
						m_targetArr.clear();
						switch(GetMakeupType(arr.get(index)))
						{
							case EYEBROW_L:
								m_targetArr.addAll(m_leyebrowPos);
								break;

							case EYEBROW_R:
								m_targetArr.addAll(m_reyebrowPos);
								break;

							case EYE_L:
								m_targetArr.addAll(m_leyePos);
								break;

							case EYE_R:
								m_targetArr.addAll(m_reyePos);
								break;

							case LIP:
								m_targetArr.addAll(m_lipPos);
								break;

							case CHEEK_L:
							case CHEEK_R:
								m_targetArr.addAll(m_cheekPos);
								break;

							default:
								break;
						}

						m_allModify = true;
						Init_M_All_Data(m_targetArr, m_downX, m_downY);
						this.invalidate();

						m_otherMakeupSel = -1;
						break;
					}
					else
					{
						m_allModify = true;
						m_target = arr.get(index);
						RefreshSonWinPos(m_downX, m_downY);
						this.invalidate();

						m_otherMakeupSel = -1;
					}
				}
				else
				{
					//判断是否选中其他彩妆
					if(m_otherMakeupSel >= 0)
					{
						//判断是否选中旋转放大按钮
						if(m_rotationBtn != null && IsClickBtn(m_rotationBtn, m_downX, m_downY))
						{
							m_target = m_otherMakeup.get(m_otherMakeupSel);
							m_isOddCtrl = true;
							m_oddCtrlType = CTRL_R_Z;
							float[] src = {m_target.m_x + m_target.m_centerX, m_target.m_y + m_target.m_centerY};
							float[] dst = new float[2];
							GetShowPos(dst, src);
							temp_showCX = dst[0];
							temp_showCY = dst[1];
							Init_RZ_Data(m_target, dst[0], dst[1], m_downX, m_downY);

							return;
						}

						//判断是否选中非比例缩放按钮
						if(m_nzoomBtn != null && IsClickBtn(m_nzoomBtn, m_downX, m_downY))
						{
							m_target = m_otherMakeup.get(m_otherMakeupSel);
							m_isOddCtrl = true;
							m_oddCtrlType = CTRL_NZ;
							Init_NZ_Data(m_target, m_downX, m_downY);

							return;
						}

						//判断是否按下删除按钮
						if(m_deleteBtn != null && IsClickBtn(m_deleteBtn, m_downX, m_downY))
						{
							m_target = m_otherMakeup.get(m_otherMakeupSel);
							m_isOddCtrl = true;
							m_oddCtrlType = CTRL_DEL;

							return;
						}
					}

					index = GetSelectIndex(m_otherMakeup, m_downX, m_downY);
					if(index >= 0)
					{
						m_target = m_otherMakeup.get(index);
						m_otherMakeup.remove(index);
						m_otherMakeup.add((MakeupShape)m_target);
						m_otherMakeupSel = m_otherMakeup.size() - 1;
						m_isOddCtrl = false;

						//更新界面
						this.invalidate();
					}
					else
					{
						m_otherMakeupSel = -1;
						m_target = m_origin;
					}

				}

				Init_M_Data(m_target, m_downX, m_downY);
				break;
			}

			default:
			{
				m_target = m_origin;
				Init_M_Data(m_target, m_downX, m_downY);
				break;
			}
		}

		m_isFacePos = IsFacePos(m_target);
	}

	protected ArrayList<ShapeEx> m_oldXYs = new ArrayList<ShapeEx>();

	protected void Init_M_All_Data(ArrayList<? extends ShapeEx> target, float x, float y)
	{
		m_gammaX = x;
		m_gammaY = y;
		m_oldXYs.clear();
		int len = target.size();
		for(int i = 0; i < len; i++)
		{
			m_oldXYs.add((ShapeEx)target.get(i).Clone());
		}
	}

	protected void Run_M_All(ArrayList<? extends ShapeEx> target, float x, float y)
	{
		int len = target.size();
		ShapeEx temp;
		ShapeEx temp2;
		float dx = (x - m_gammaX) / m_origin.m_scaleX;
		float dy = (y - m_gammaY) / m_origin.m_scaleY;
		for(int i = 0; i < len; i++)
		{
			temp = target.get(i);
			temp2 = m_oldXYs.get(i);
			temp.m_x = dx + temp2.m_x;
			temp.m_y = dy + temp2.m_y;
		}
	}

	protected boolean IsClickBtn(ShapeEx item, float x, float y)
	{
		boolean out = false;

		float[] values = new float[9];
		Matrix matrix = new Matrix();
		GetShowMatrixNoScale(matrix, item);
		matrix.getValues(values);
		if(ProcessorV2.IsSelectTarget(values, item.m_w, item.m_h, x, y))
		{
			out = true;
		}

		return out;
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		if(m_target != null)
		{
			switch(m_operateMode)
			{
				case MODE_MAKEUP:
					if(m_isOddCtrl)
					{
						if(m_oddCtrlType == CTRL_R_Z)
						{
							//使用临时中心点
							Run_RZ(m_target, temp_showCX, temp_showCY, event.getX(), event.getY());
						}
						else if(m_oddCtrlType == CTRL_NZ)
						{
							Run_NZ(m_target, event.getX(), event.getY());
						}
						this.invalidate();
						break;
					}
				case MODE_FACE:
				case MODE_ALL:
				default:
					if(m_target != m_origin)
					{
						Run_M(m_target, event.getX(), event.getY());
						RefreshSonWinPos(event.getX(), event.getY());

						//限制移动区域,m_target为面部校对点
						float a = m_img.m_x + m_img.m_centerX - m_img.m_centerX * m_img.m_scaleX - m_target.m_centerX;
						float b = m_img.m_y + m_img.m_centerY - m_img.m_centerY * m_img.m_scaleY - m_target.m_centerY;
						float c = m_img.m_x + m_img.m_centerX + m_img.m_centerX * m_img.m_scaleX - m_target.m_centerX;
						float d = m_img.m_y + m_img.m_centerY + m_img.m_centerY * m_img.m_scaleY - m_target.m_centerY;
						if(m_target.m_x < a)
						{
							m_target.m_x = a;
						}
						else if(m_target.m_x > c)
						{
							m_target.m_x = c;
						}
						if(m_target.m_y < b)
						{
							m_target.m_y = b;
						}
						else if(m_target.m_y > d)
						{
							m_target.m_y = d;
						}

						UpdatePosRotation();
					}
					else
					{
						Run_M2(m_target, event.getX(), event.getY());
					}
					this.invalidate();
					break;
			}
		}
		else
		{
			if(m_moveAllFacePos && m_targetArr.size() > 0)
			{
				Run_M_All(m_targetArr, event.getX(), event.getY());
				this.invalidate();
			}
		}

		if(m_isClick)
		{
			if(ImageUtils.Spacing(m_downX - event.getX(), m_downY - event.getY()) > def_click_size)
			{
				m_isClick = false;
			}
		}
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		m_isTouch = false;

		//双击判断
		boolean doubleClick = false;
		if(m_isClick)
		{
			long time = System.currentTimeMillis();
			if(Math.abs(time - m_doubleClickTime) < 900 && ImageUtils.Spacing(m_doubleClickX - event.getX(), m_doubleClickY - event.getY()) < def_click_size)
			{
				m_doubleClickTime = 0;

				//双击
				//System.out.println("double click");
				float[] src = new float[]{event.getX(), event.getY()};
				float[] dst = new float[2];
				GetLogicPos(dst, src);
				GetFaceLogicPos(src, dst);
				//System.out.println(src[0] + "," + src[1]);
				if(src[0] >= 0 && src[1] >= 0)
				{
					doubleClick = true;
					float scale = (m_origin.MAX_SCALE + m_origin.DEF_SCALE) * 0.4f;
					if(scale < m_origin.DEF_SCALE)
					{
						scale = (m_origin.MAX_SCALE + m_origin.DEF_SCALE) * 0.5f;
					}
					if(Math.abs(m_origin.m_scaleX - m_origin.DEF_SCALE) > Math.abs(m_origin.m_scaleX - scale))
					{
						DoAnim(new RectF(0, 0, 1, 1), def_face_anim_type, def_anim_time, true);
					}
					else
					{
						if(scale != 0)
						{
							scale = m_origin.DEF_SCALE / scale;
						}
						else
						{
							scale = 0.6f;
						}
						//System.err.println(scale);
						scale /= 2f;
						//System.err.println(new RectF(src[0] - scale, src[1] - scale, src[0] + scale, src[1] + scale));
						DoAnim(new RectF(src[0] - scale, src[1] - scale, src[0] + scale, src[1] + scale), def_face_anim_type, def_anim_time, true);
					}
				}
			}
			else
			{
				m_doubleClickTime = time;
				m_doubleClickX = m_downX;
				m_doubleClickY = m_downY;
			}
		}
		else
		{
			m_doubleClickTime = 0;
		}

		//回弹动画
		if(m_origin == m_target && !doubleClick)
		{
			//float limit;
			//if(m_origin.m_w > m_origin.m_h)
			//{
			//	limit = def_limit_sacle * m_origin.m_w;
			//}
			//else
			//{
			//	limit = def_limit_sacle * m_origin.m_h;
			//}
			//float w2 = m_origin.m_scaleX * m_viewport.m_centerX * m_viewport.m_scaleX;
			//float h2 = m_origin.m_scaleY * m_viewport.m_centerY * m_viewport.m_scaleY;
			//float left = limit - w2;
			//float top = limit - h2;
			//float right = m_origin.m_w - limit + w2;
			//float bottom = m_origin.m_h - limit + h2;
			//
			//float[] src = {m_viewport.m_x + m_viewport.m_centerX, m_viewport.m_y + m_viewport.m_centerY};
			//float[] dst = new float[2];
			//GetShowPos(dst, src);
			//
			//boolean flag = false;
			//if(dst[0] < left)
			//{
			//	dst[0] = left;
			//	flag = true;
			//}
			//else if(dst[0] > right)
			//{
			//	dst[0] = right;
			//	flag = true;
			//}
			//if(flag)
			//{
			//	m_origin.m_x = dst[0] - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
			//}
			//
			//flag = false;
			//if(dst[1] < top)
			//{
			//	dst[1] = top;
			//	flag = true;
			//}
			//else if(dst[1] > bottom)
			//{
			//	dst[1] = bottom;
			//	flag = true;
			//}
			//if(flag)
			//{
			//	m_origin.m_y = dst[1] - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;
			//}

			if(m_origin.m_scaleX < 1 || m_origin.m_scaleY < 1)
			{
				DoAnim(new RectF(0, 0, 1, 1), def_anim_type, def_anim_time, false);
			}
			else
			{
				float[] src = new float[]{m_img.m_x + m_img.m_centerX, m_img.m_y + m_img.m_centerY};
				float[] dst = new float[2];
				GetShowPos(dst, src);

				boolean doAnim = false;

				float imgW = m_origin.m_scaleX * m_img.m_scaleX * m_img.m_w;
				if(imgW > m_origin.m_w)
				{
					float min = m_origin.m_w - imgW / 2f;
					float max = imgW / 2f;
					if(dst[0] < min)
					{
						dst[0] = min;

						doAnim = true;
					}
					else if(dst[0] > max)
					{
						dst[0] = max;

						doAnim = true;
					}
				}
				else
				{
					dst[0] = m_origin.m_w / 2f;

					doAnim = true;
				}

				float imgH = m_origin.m_scaleY * m_img.m_scaleY * m_img.m_h;
				if(imgH > m_origin.m_h)
				{
					float min = m_origin.m_h - imgH / 2f;
					float max = imgH / 2f;
					if(dst[1] < min)
					{
						dst[1] = min;

						doAnim = true;
					}
					else if(dst[1] > max)
					{
						dst[1] = max;

						doAnim = true;
					}
				}
				else
				{
					dst[1] = m_origin.m_h / 2f;

					doAnim = true;
				}

				if(doAnim)
				{
					float l, t, r, b;
					l = dst[0] - imgW / 2f;
					if(l > 0)
					{
						l = 0;
					}
					else
					{
						l = -l / imgW;
					}
					r = dst[0] + imgW / 2f;
					if(r < m_origin.m_w)
					{
						r = 1;
					}
					else
					{
						r = (imgW - (r - m_origin.m_w)) / imgW;
					}
					t = dst[1] - imgH / 2f;
					if(t > 0)
					{
						t = 0;
					}
					else
					{
						t = -t / imgH;
					}
					b = dst[1] + imgH / 2f;
					if(b < m_origin.m_h)
					{
						b = 1;
					}
					else
					{
						b = (imgH - (b - m_origin.m_h)) / imgH;
					}

					DoAnim(new RectF(l, t, r, b), def_anim_type, def_anim_time, false);
				}
			}
		}

		switch(m_operateMode)
		{
			case MODE_FACE:
			case MODE_MAKEUP:
				break;

			case MODE_SEL_FACE:
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
				break;

			default:
			{
				//判断点中哪个部位
				if(m_isClick && m_cb != null)
				{
					if(FaceDataV2.RAW_POS_MULTI != null)
					{
						ArrayList<RectF> tempArr = new ArrayList<RectF>();
						MakeupType type = GetClickFacePos(event.getX(), event.getY(), tempArr, false);
						if(type != null)
						{
							switch(type)
							{
								case EYEBROW_L:
									m_rectFlags.clear();
									AddEyebrowRectFlag(m_rectFlags, tempArr.get(0), null, false);
									m_cb.OnTouchEyebrow(true);
									break;

								case EYEBROW_R:
									m_rectFlags.clear();
									AddEyebrowRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1), false);
									m_cb.OnTouchEyebrow(false);
									break;

								case EYE_L:
									m_rectFlags.clear();
									AddEyeRectFlag(m_rectFlags, tempArr.get(0), null, false);
									m_cb.OnTouchEye(true);
									break;

								case EYE_R:
									m_rectFlags.clear();
									AddEyeRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1), false);
									m_cb.OnTouchEye(false);
									break;

								case LIP:
									m_rectFlags.clear();
									AddLipRectFlag(m_rectFlags, tempArr.get(0), false);
									m_cb.OnTouchLip();
									break;

								case CHEEK_L:
									m_rectFlags.clear();
									AddCheckRectFlag(m_rectFlags, tempArr.get(0), null, false);
									m_cb.OnTouchCheek(true);
									break;

								case CHEEK_R:
									m_rectFlags.clear();
									AddCheckRectFlag(m_rectFlags, tempArr.get(0), tempArr.get(1), false);
									m_cb.OnTouchCheek(false);
									break;

								case FOUNDATION:
									m_rectFlags.clear();
									AddFoundationRectFlag(m_rectFlags, tempArr.get(0), false);
									m_cb.OnTouchFoundation();
									break;

								default:
									break;
							}
						}
					}
					else if(FaceDataV2.FACE_POS_MULTI != null)
					{
						int index = IsClickFacePos(m_facePos, event.getX(), event.getY(), def_face_pos_touch_size * 1.5f);
						switch(index)
						{
							case 0:
								m_cb.OnTouchEye(true);
								break;
							case 1:
								m_cb.OnTouchEye(false);
								break;
							case 2:
								m_cb.OnTouchLip();
								break;
							default:
								break;
						}
					}
				}
				break;
			}
		}

		if(m_allModify || m_3Modify)
		{
			UI2Data();
			FaceDataV2.Ripe2Raw();

			if(m_3Modify)
			{
				m_cb.On3PosModify();
			}
			if(m_allModify)
			{
				m_cb.OnAllPosModify();
			}

			m_3Modify = false;
			m_allModify = false;
		}

		m_sonWinX = 0;
		m_sonWinY = 0;
		m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);

		//删除
		if(m_isOddCtrl && m_oddCtrlType == CTRL_DEL && m_isClick)
		{
			int len = m_otherMakeup.size();
			for(int i = 0; i < len; i++)
			{
				if(m_otherMakeup.get(i) == m_target)
				{
					m_otherMakeup.remove(i);
					break;
				}
			}
		}

		m_isClick = false;
		m_isOddCtrl = false;
		m_target = null;
		m_targetArr.clear();
		this.invalidate();

		m_isFacePos = IsFacePos(m_target);
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		m_isTouch = true;
		m_isOddCtrl = false;
		m_isClick = false;
		m_tween.M1End();

		m_target = null;
		if(m_img != null)
		{
			ArrayList<ShapeEx> arr = new ArrayList<ShapeEx>();
			arr.add(m_img);
			if(GetSelectIndex(arr, (m_downX1 + m_downX2) / 2f, (m_downY1 + m_downY2) / 2f) > -1)
			{
				m_target = m_origin;
				Init_NCZM_Data(m_target, m_downX1, m_downY1, m_downX2, m_downY2);
			}
		}

		m_isFacePos = IsFacePos(m_target);
	}

	@Override
	protected void EvenMove(MotionEvent event)
	{
		if(m_target != null)
		{
			Run_NCZM(m_target, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
			this.invalidate();
		}
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		m_isClick = false;
		OddUp(event);
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param outArr
	 *            输出矩形,未必全,可能只有一边
	 * @param clickRect
	 *            点击矩形还是显示矩形
	 * @return
	 */
	protected MakeupType GetClickFacePos(float x, float y, ArrayList<RectF> outArr, boolean clickRect)
	{
		MakeupType out = null;

		if(FaceDataV2.RAW_POS_MULTI != null)
		{
			float[] src = new float[2];
			float[] dst = new float[2];
			src[0] = x;
			src[1] = y;
			GetLogicPos(dst, src);
			src[0] = dst[0];
			src[1] = dst[1];
			GetFaceLogicPos(dst, src);
			RectF rect0;
			RectF rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8);
			//眉毛加高
			rect.top -= rect.bottom - rect.top;
			if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
			{
				out = MakeupType.EYEBROW_L;
				if(outArr != null)
				{
					if(clickRect)
					{
						outArr.add(rect);
					}
					else
					{
						outArr.add(GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8));
					}
				}
			}
			else
			{
				rect0 = rect;
				rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8);
				//眉毛加高
				rect.top -= rect.bottom - rect.top;
				if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
				{
					out = MakeupType.EYEBROW_R;
					if(outArr != null)
					{
						if(clickRect)
						{
							outArr.add(rect0);
							outArr.add(rect);
						}
						else
						{
							outArr.add(GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8));
							outArr.add(GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8));
						}
					}
				}
				else
				{
					rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 10);
					if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
					{
						out = MakeupType.EYE_L;
						if(outArr != null)
						{
							outArr.add(rect);
						}
					}
					else
					{
						rect0 = rect;
						rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, 10);
						if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
						{
							out = MakeupType.EYE_R;
							if(outArr != null)
							{
								outArr.add(rect0);
								outArr.add(rect);
							}
						}
						else
						{
							rect = GetMinRect(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, 32);
							if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
							{
								out = MakeupType.LIP;
								if(outArr != null)
								{
									outArr.add(rect);
								}
							}
							else
							{
								rect = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 4, 4);
								if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
								{
									out = MakeupType.CHEEK_L;
									if(outArr != null)
									{
										outArr.add(rect);
									}
								}
								else
								{
									rect0 = rect;
									rect = GetMinRect(FaceDataV2.CHEEK_POS_MULTI[m_faceIndex], 8, 4);
									if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
									{
										out = MakeupType.CHEEK_R;
										if(outArr != null)
										{
											outArr.add(rect0);
											outArr.add(rect);
										}
									}
									else
									{
										float[] pos = FaceDataV2.FACE_POS_MULTI[m_faceIndex];
										rect = new RectF(pos[6], pos[7], pos[6] + pos[8], pos[7] + pos[9]);
										if(dst[0] >= rect.left && dst[0] <= rect.right && dst[1] >= rect.top && dst[1] <= rect.bottom)
										{
											out = MakeupType.FOUNDATION;
											if(outArr != null)
											{
												outArr.add(rect);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return out;
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

	public void DoAnim2Eyebrow(boolean isLeft)
	{
		if(FaceDataV2.EYEBROW_POS_MULTI != null)
		{
			RectF rect = null;
			if(isLeft)
			{
				rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 8);
			}
			else
			{
				rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 8, 8);
			}
			ZoomRect(rect, 1.3f);
			DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
		}
	}

	public void DoAnim2Eyebrow()
	{
		if(FaceDataV2.EYEBROW_POS_MULTI != null)
		{
			RectF rect = GetMinRect(FaceDataV2.EYEBROW_POS_MULTI[m_faceIndex], 0, 16);
			ZoomRect(rect, 1.3f);
			DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
		}
	}

	public void DoAnim2Eye(boolean isLeft)
	{
		if(FaceDataV2.EYE_POS_MULTI != null)
		{
			RectF rect = null;
			if(isLeft)
			{
				rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 10);
			}
			else
			{
				rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 10, 10);
			}
			ZoomRect(rect, 1.3f);
			DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
		}
	}

	public void DoAnim2Eye()
	{
		if(FaceDataV2.EYE_POS_MULTI != null)
		{
			RectF rect = GetMinRect(FaceDataV2.EYE_POS_MULTI[m_faceIndex], 0, 20);
			ZoomRect(rect, 1.2f);
			DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
		}
	}

	public void DoAnim2Mouth()
	{
		if(FaceDataV2.LIP_POS_MULTI != null)
		{
			RectF rect = GetMinRect(FaceDataV2.LIP_POS_MULTI[m_faceIndex], 0, 32);
			ZoomRect(rect, 1.3f);
			DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
		}
	}

	public void DoAnim2All()
	{
		if(FaceDataV2.FACE_POS_MULTI != null)
		{
			RectF rect = GetMinRect(FaceDataV2.FACE_POS_MULTI[m_faceIndex], 0, 6);
			ZoomRect(rect, 1.5f);
			DoAnim(rect, def_face_anim_type, def_face_anim_time, true);
		}
	}

	public void DoAnim2Cheek()
	{
		if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.CHEEK_POS_MULTI != null)
		{
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

	public void DoAnim(RectF rect, int animType, int animTime)
	{
		DoAnim(rect, animType, animTime, false);
	}

	/**
	 *
	 * @param rect
	 *            图片的比例矩形
	 */
	public void DoAnim(RectF rect, int animType, int animTime, boolean fixRectPos)
	{
		float l = rect.left;
		float t = rect.top;
		float r = rect.right;
		float b = rect.bottom;
		if(rect.left > rect.right)
		{
			l = rect.right;
			r = rect.left;
		}
		if(rect.top > rect.bottom)
		{
			t = rect.bottom;
			b = rect.top;
		}
		if(m_img != null)
		{
			float rectW = (r - l) * m_img.m_w * m_img.m_scaleX;
			if(rectW <= 0)
			{
				rectW = 1;
			}
			float rectH = (b - t) * m_img.m_h * m_img.m_scaleY;
			if(rectH <= 0)
			{
				rectH = 1;
			}
			float scale = m_origin.m_w / rectW;
			{
				float scale2 = m_origin.m_h / rectH;
				if(scale2 < scale)
				{
					scale = scale2;
				}
			}
			//限制最大最小缩放比例
			if(scale > m_origin.MAX_SCALE)
			{
				scale = m_origin.MAX_SCALE;
			}
			else if(scale < m_origin.DEF_SCALE)
			{
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
			if(imgShowW <= m_origin.m_w)
			{
				offsetX = m_origin.m_w / 2f - (imgShowL + imgShowW / 2f);
			}
			else
			{
				float imgShowR = imgShowL + imgShowW;
				if(imgShowL > 0)
				{
					offsetX = -imgShowL;
				}
				else if(imgShowR < m_origin.m_w)
				{
					offsetX = m_origin.m_w - imgShowR;
				}
			}
			float rectCY = (t + b) * imgShowH / 2f;
			float imgShowT = m_origin.m_h / 2f - rectCY;
			if(imgShowH <= m_origin.m_h)
			{
				offsetY = m_origin.m_h / 2f - (imgShowT + imgShowH / 2f);
			}
			else
			{
				float imgShowB = imgShowT + imgShowH;
				if(imgShowT > 0)
				{
					offsetY = -imgShowT;
				}
				else if(imgShowB < m_origin.m_h)
				{
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

	public static void FixRect(RectF rect)
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

	public static boolean RectCross(RectF rect1, RectF rect2)
	{
		boolean out = false;

		if(rect1 != null && rect2 != null)
		{
			out = true;
			if(rect1.left >= rect2.left && rect1.left >= rect2.right || rect1.right <= rect2.left && rect1.right <= rect2.right || rect1.top >= rect2.top && rect1.top >= rect2.bottom || rect1.bottom <= rect2.top && rect1.bottom <= rect2.bottom)
			{
				out = false;
			}
		}

		return out;
	}

	public static ArrayList<RectF> DivideRect(RectF src, ArrayList<RectF> dst)
	{
		ArrayList<RectF> out = new ArrayList<RectF>();

		if(src != null && dst != null)
		{
			ArrayList<Float> xs = new ArrayList<Float>();
			ArrayList<Float> ys = new ArrayList<Float>();
			InsertOrderItem(xs, src.left);
			InsertOrderItem(xs, src.right);
			InsertOrderItem(ys, src.top);
			InsertOrderItem(ys, src.bottom);

			int len = dst.size();
			RectF rect;
			for(int i = 0; i < len; i++)
			{
				rect = dst.get(i);
				if(src.left < rect.left && rect.left < src.right)
				{
					InsertOrderItem(xs, rect.left);
				}
				if(src.left < rect.right && rect.right < src.right)
				{
					InsertOrderItem(xs, rect.right);
				}
				if(src.top < rect.top && rect.top < src.bottom)
				{
					InsertOrderItem(ys, rect.top);
				}
				if(src.top < rect.bottom && rect.bottom < src.bottom)
				{
					InsertOrderItem(ys, rect.bottom);
				}
			}

			int xlen = xs.size() - 1;
			int ylen = ys.size() - 1;
			for(int i = 0; i < ylen; i++)
			{
				for(int j = 0; j < xlen; j++)
				{
					out.add(new RectF(xs.get(j), ys.get(i), xs.get(j + 1), ys.get(i + 1)));
				}
			}
		}

		return out;
	}

	public static boolean InsertOrderItem(ArrayList<Float> arr, float value)
	{
		boolean out = false;

		if(arr != null)
		{
			Float temp;
			int len = arr.size();
			boolean equal = false;
			for(int i = 0; i < len; i++)
			{
				equal = false;
				temp = arr.get(i);
				if(temp > value)
				{
					arr.add(i, value);
					out = true;
					break;
				}
				else if(temp == value)
				{
					equal = true;
					break;
				}
			}
			if(!out && !equal)
			{
				arr.add(value);
			}
		}

		return out;
	}

	public static ArrayList<RectF> GetOutsideRect(RectF src, ArrayList<RectF> dst)
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

	public Bitmap GetOutputBmp(int size)
	{
		Bitmap out = null;

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

		ShapeEx temp;
		float tempW;
		float tempH;
		Bitmap tempBmp;
		int len = m_otherMakeup.size();
		for(int i = 0; i < len; i++)
		{
			temp = m_otherMakeup.get(i);
			tempW = m_origin.m_scaleX * temp.m_scaleX * temp.m_w;
			tempH = m_origin.m_scaleY * temp.m_scaleY * temp.m_h;
			tempBmp = m_cb.MakeOutputPendant(temp.m_ex, (int)(tempW + 0.5), (int)(tempH + 0.5));
			GetOutputMatrix(temp_matrix, temp, tempBmp);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
			tempBmp.recycle();
			tempBmp = null;
		}

		m_origin.Set(backup);

		return out;
	}

	protected void GetOutputMatrix(Matrix matrix, ShapeEx item, Bitmap bmp)
	{
		float[] src = {item.m_x + item.m_centerX, item.m_y + item.m_centerY};
		float[] dst = new float[2];
		GetShowPos(dst, src);

		matrix.reset();
		if(item.m_flip == Shape.Flip.VERTICAL)
		{
			float[] values = {1, 0, 0, 0, -1, bmp.getHeight(), 0, 0, 1};
			matrix.setValues(values);
		}
		else if(item.m_flip == Shape.Flip.HORIZONTAL)
		{
			float[] values = {-1, 0, bmp.getWidth(), 0, 1, 0, 0, 0, 1};
			matrix.setValues(values);
		}

		matrix.postTranslate(dst[0] - bmp.getWidth() / 2f, dst[1] - bmp.getHeight() / 2f);
		matrix.postScale(m_origin.m_scaleX * item.m_scaleX * item.m_w / (float)bmp.getWidth(), m_origin.m_scaleY * item.m_scaleY * item.m_h / (float)bmp.getHeight(), dst[0], dst[1]);
		matrix.postRotate(item.m_degree, dst[0], dst[1]);
	}

	public static class MakeupShape extends ShapeEx
	{
		public int m_faceID;
	}

	public static interface ControlCallback
	{
		public Bitmap MakeShowImg(Object info, int frW, int frH);

		public Bitmap MakeOutputImg(Object info, int outW, int outH);

		public Bitmap MakeOutputPendant(Object info, int outW, int outH);

		public void OnTouchEyebrow(boolean isLeft);

		public void OnTouchEye(boolean isLeft);

		public void OnTouchCheek(boolean isLeft);

		public void OnTouchLip();

		public void OnTouchFoundation();

		public void On3PosModify();

		public void OnAllPosModify();

		public void UpdateSonWin(Bitmap bmp, int x, int y);

		public void OnSelFaceIndex(int index);
	}
}
