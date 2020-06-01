package cn.poco.beautify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.PorterDuff;
import android.view.MotionEvent;

import cn.poco.graphics.ShapeEx;
import cn.poco.imagecore.ProcessorV2;
import my.beautyCamera.R;

public class BeautifyView2 extends BeautifyView
{
	public static final int MODE_ACNE = 0x0100;
	public static final int MODE_ADV_SLIM_TOOL = 0x0200;
	public static final int MODE_ADV_SLIM_DRAG = 0x0400;

	public int def_color = 0xff2effd2;
	public int def_stroke_width = 2;
	public int def_slim_tool_ab_btn_res = R.drawable.liquefaction_view_path_out;
	public int def_slim_tool_r_btn_res = R.drawable.liquefaction_view_rotate_out;

	protected static final long DISPLAY_DURATION = 500;
	protected long m_displayTime; //调整大小时显示圆圈的结束时刻

	protected float m_acneToolDefR;
	protected float m_acneToolR; //大小为实际屏幕显示尺寸
	protected ShapeEx m_acneTool;

	protected float m_slimToolDefR;
	protected ShapeEx m_slimTool; //瘦身工具,大小为实际屏幕显示尺寸
	protected ShapeEx m_slimToolBtnA; //瘦身工具-A按钮
	protected ShapeEx m_slimToolBtnB; //瘦身工具-B按钮
	protected ShapeEx m_slimToolBtnR; //瘦身工具-旋转按钮

	protected float m_dragToolX1;
	protected float m_dragToolY1;
	protected float m_dragToolX2;
	protected float m_dragToolY2;
	protected float m_dragToolDefR; //默认大小
	protected float m_dragToolR; //大小为实际屏幕显示尺寸

	public BeautifyView2(Context context, int frW, int frH)
	{
		super(context, frW, frH);
	}

	@Override
	public void InitData(BeautifyView.ControlCallback cb)
	{
		m_acneToolDefR = (int)(m_origin.m_w * 0.013f + 0.5f);
		m_slimToolDefR = (int)(m_origin.m_w * 0.15f + 0.5f);
		m_dragToolDefR = (int)(m_origin.m_w * 0.08f + 0.5f);

		Bitmap bmp;
		int w = m_origin.m_w / 6;
		bmp = Bitmap.createBitmap(w, w, Config.ARGB_8888);
		m_acneTool = new ShapeEx();
		m_acneTool.m_bmp = bmp;
		m_acneTool.m_w = bmp.getWidth();
		m_acneTool.m_h = bmp.getHeight();
		m_acneTool.m_centerX = m_acneTool.m_w / 2f;
		m_acneTool.m_centerY = m_acneTool.m_h / 2f;
		m_acneToolR = m_acneToolDefR;
		DrawAcneTool();

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

		super.InitData(cb);
	}

	protected void DrawAcneTool()
	{
		if(m_acneTool != null && m_acneTool.m_bmp != null)
		{
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
	protected void onDraw(Canvas canvas)
	{
		if(m_drawable && m_viewport.m_w > 0 && m_viewport.m_h > 0)
		{
			DrawToCanvas(canvas, m_operateMode);

			if((m_operateMode == MODE_ACNE && m_isTouch) || ((m_operateMode == MODE_FACE || m_operateMode == MODE_MAKEUP) && m_isTouch && m_isFacePos))
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

	@Override
	protected void DrawToCanvas(Canvas canvas, int mode)
	{
		super.DrawToCanvas(canvas, mode);

		switch(mode)
		{
			case MODE_ACNE:
			{
				//显示调整大小时的圈
				if(m_displayTime > System.currentTimeMillis() && m_acneTool != null && m_acneTool.m_bmp != null)
				{
					temp_matrix.reset();
					temp_matrix.postTranslate(m_origin.m_centerX - m_acneTool.m_centerX, m_origin.m_centerY - m_acneTool.m_centerY);
					temp_paint.reset();
					temp_paint.setAntiAlias(true);
					canvas.drawBitmap(m_acneTool.m_bmp, temp_matrix, temp_paint);

					this.invalidate();
				}

				if(m_isClick && m_acneTool != null)
				{
					DrawButton(canvas, m_acneTool);
				}
				break;
			}
			case MODE_ADV_SLIM_TOOL:
			{
				UpdateSlimTool();

				if(m_slimTool != null)
				{
					float[] src = new float[]{m_slimTool.m_x + m_slimTool.m_centerX, m_slimTool.m_y + m_slimTool.m_centerY};
					float[] dst = new float[2];
					GetShowPos(dst, src);
					DrawSlimTool(canvas, def_color, m_slimToolDefR, dst[0], dst[1], m_slimTool.m_centerX * m_slimTool.m_scaleX, m_slimTool.m_degree);
				}
				if(m_slimToolBtnA != null)
				{
					DrawButton(canvas, m_slimToolBtnA);
				}
				if(m_slimToolBtnB != null)
				{
					DrawButton(canvas, m_slimToolBtnB);
				}
				if(m_slimToolBtnR != null)
				{
					DrawButton(canvas, m_slimToolBtnR);
				}

				break;
			}
			case MODE_ADV_SLIM_DRAG:
			{
				//显示调整大小时的圈
				if(m_displayTime > System.currentTimeMillis())
				{
					temp_paint.reset();
					temp_paint.setAntiAlias(true);
					temp_paint.setStyle(Paint.Style.STROKE);
					temp_paint.setColor(def_color);
					temp_paint.setStrokeWidth(def_stroke_width);
					canvas.drawCircle(m_origin.m_centerX, m_origin.m_centerY, m_dragToolR, temp_paint);

					this.invalidate();
				}

				if(m_isClick)
				{
					float[] src = new float[]{m_dragToolX1, m_dragToolY1, m_dragToolX2, m_dragToolY2};
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
	 * @param color
	 *            颜色
	 * @param defR
	 *            默认半径
	 * @param x
	 *            中心点坐标
	 * @param y
	 *            中心点坐标
	 * @param r
	 *            半径
	 * @param degree
	 *            角度
	 */
	protected void DrawSlimTool(Canvas canvas, int color, float defR, float x, float y, float r, float degree)
	{
		canvas.save();
		canvas.translate(x, y);
		canvas.rotate(degree);

		temp_paint.reset();
		temp_paint.setColor(color);
		temp_paint.setAntiAlias(true);
		temp_paint.setStyle(Paint.Style.STROKE);
		temp_paint.setStrokeWidth(def_stroke_width);
		temp_paint.setStrokeJoin(Join.MITER);
		temp_paint.setStrokeMiter(def_stroke_width * 2);

		//画圆
		canvas.drawCircle(0, 0, r, temp_paint);

		//画直线
		canvas.drawLine(0, -r, 0, r, temp_paint);

		//画左V型
		canvas.drawLine(-defR / 3f, 0, -defR / 7f, -defR / 8f, temp_paint);
		canvas.drawLine(-defR / 3f, 0, -defR / 7f, defR / 8f, temp_paint);

		//画右V型
		canvas.drawLine(defR / 3f, 0, defR / 7f, -defR / 8f, temp_paint);
		canvas.drawLine(defR / 3f, 0, defR / 7f, defR / 8f, temp_paint);

		//实心圆
		temp_paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(0, 0, defR / 20f, temp_paint);

		canvas.restore();
	}

	/**
	 * 
	 * @param canvas
	 * @param color
	 *            颜色
	 * @param defR
	 *            默认半径
	 * @param x1
	 *            中心点坐标
	 * @param y1
	 *            中心点坐标
	 * @param x2
	 *            中心点坐标
	 * @param y2
	 *            中心点坐标
	 * @param r
	 *            半径
	 */
	protected void DrawSlimTool2(Canvas canvas, int color, float defR, float x1, float y1, float x2, float y2, float r)
	{
		temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setStyle(Paint.Style.STROKE);
		temp_paint.setColor(color);
		temp_paint.setStrokeWidth(def_stroke_width);
		temp_paint.setStrokeJoin(Join.MITER);
		temp_paint.setStrokeMiter(def_stroke_width * 2);

		//画第一个圆
		canvas.drawCircle(x1, y1, r, temp_paint);
		//画第二个圆
		canvas.drawCircle(x2, y2, r, temp_paint);
		//画直线
		canvas.drawLine(x1, y1, x2, y2, temp_paint);
		//画箭头
		float[] src = {-defR / 4f, defR / 5f, -defR / 4f, -defR / 5f};
		float[] dst = new float[4];
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
		tempAngle += 180;
		temp_matrix.reset();
		temp_matrix.postRotate(tempAngle);
		temp_matrix.postTranslate(x2, y2);
		temp_matrix.mapPoints(dst, src);
		canvas.drawLine(dst[0], dst[1], x2, y2, temp_paint);
		canvas.drawLine(dst[2], dst[3], x2, y2, temp_paint);
	}

	/**
	 * 原始坐标->逻辑坐标
	 * 
	 * @param dst
	 * @param src
	 */
	protected void GetSlimToolData(float[] dst, float[] src)
	{
		float[] srcXY = new float[]{m_slimTool.m_x + m_slimTool.m_centerX, m_slimTool.m_y + m_slimTool.m_centerY};
		float[] dstXY = new float[2];
		GetShowPos(dstXY, srcXY);

		float[] temp = new float[src.length];
		temp_matrix.reset();
		temp_matrix.postRotate(m_slimTool.m_degree);
		temp_matrix.postTranslate(dstXY[0], dstXY[1]);
		temp_matrix.mapPoints(temp, src);

		GetLogicPos(dst, temp);
	}

	protected float[] temp_slim_tool_dst = new float[6];
	protected float[] temp_slim_tool_src = new float[6];

	protected void UpdateSlimTool()
	{
		if(m_slimTool != null && m_slimToolBtnA != null && m_slimToolBtnB != null && m_slimToolBtnR != null)
		{
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

	private float temp_showCX;
	private float temp_showCY;

	@Override
	protected void OddDown(MotionEvent event)
	{
		switch(m_operateMode)
		{
			case MODE_ACNE:
			case MODE_ADV_SLIM_TOOL:
			case MODE_ADV_SLIM_DRAG:
			{
				if(m_tween.M1IsFinish())
				{
					m_isTouch = true;
					m_isClick = true;

					switch(m_operateMode)
					{
						case MODE_ACNE:
						{
							m_displayTime = System.currentTimeMillis();

							m_target = m_acneTool;
							float[] src = new float[]{m_downX, m_downY};
							float[] dst = new float[2];
							GetLogicPos(dst, src);
							m_target.m_x = dst[0] - m_target.m_centerX;
							m_target.m_y = dst[1] - m_target.m_centerY;
							RefreshSonWinPos(m_downX, m_downY);
							this.invalidate();

							Init_M_Data(m_target, m_downX, m_downY);
							break;
						}
						case MODE_ADV_SLIM_TOOL:
						{
							if(m_slimToolBtnA != null && IsClickBtn(m_slimToolBtnA, m_downX, m_downY))
							{
								m_target = m_slimToolBtnA;
							}
							else if(m_slimToolBtnB != null && IsClickBtn(m_slimToolBtnB, m_downX, m_downY))
							{
								m_target = m_slimToolBtnB;
							}
							else if(m_slimToolBtnR != null && IsClickBtn(m_slimToolBtnR, m_downX, m_downY) && m_slimTool != null)
							{
								m_target = m_slimToolBtnR;

								float[] src = {m_slimTool.m_x + m_slimTool.m_centerX, m_slimTool.m_y + m_slimTool.m_centerY};
								float[] dst = new float[2];
								GetShowPos(dst, src);
								temp_showCX = dst[0];
								temp_showCY = dst[1];
								Init_RZ_Data(m_slimTool, temp_showCX, temp_showCY, m_downX, m_downY);
							}
							else if(m_slimTool != null && IsClickBtn(m_slimTool, m_downX, m_downY))
							{
								m_target = m_slimTool;

								Init_M_Data(m_target, m_downX, m_downY);
							}
							else
							{
								m_target = null;
							}

							this.invalidate();
							break;
						}
						case MODE_ADV_SLIM_DRAG:
						{
							m_displayTime = System.currentTimeMillis();

							m_target = null;

							float[] src = new float[]{m_downX, m_downY};
							float[] dst = new float[2];
							GetLogicPos(dst, src);
							FixPoint(dst, 0, 0);
							m_dragToolX1 = dst[0];
							m_dragToolY1 = dst[1];
							m_dragToolX2 = m_dragToolX1;
							m_dragToolY2 = m_dragToolY1;

							this.invalidate();
							break;
						}
						default:
							break;
					}
				}
				break;
			}
			default:
				super.OddDown(event);
				break;
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

	/**
	 * 检查点是否在m_img里面,若不是则修正
	 * 
	 * @param points
	 * @param centerX
	 * @param centerY
	 */
	protected void FixPoint(float[] points, float centerX, float centerY)
	{
		if(points != null && m_img != null)
		{
			float a = m_img.m_x + m_img.m_centerX - m_img.m_centerX * m_img.m_scaleX - centerX;
			float b = m_img.m_y + m_img.m_centerY - m_img.m_centerY * m_img.m_scaleY - centerY;
			float c = m_img.m_x + m_img.m_centerX + m_img.m_centerX * m_img.m_scaleX - centerX;
			float d = m_img.m_y + m_img.m_centerY + m_img.m_centerY * m_img.m_scaleY - centerY;

			int len = points.length / 2 * 2;
			for(int i = 0; i < len; i += 2)
			{
				if(points[i] < a)
				{
					points[i] = a;
				}
				else if(points[i] > c)
				{
					points[i] = c;
				}
				if(points[i + 1] < b)
				{
					points[i + 1] = b;
				}
				else if(points[i + 1] > d)
				{
					points[i + 1] = d;
				}
			}
		}
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		switch(m_operateMode)
		{
			case MODE_ACNE:
			case MODE_ADV_SLIM_TOOL:
			case MODE_ADV_SLIM_DRAG:
			{
				switch(m_operateMode)
				{
					case MODE_ACNE:
					{
						if(m_target == m_acneTool)
						{
							Run_M(m_target, event.getX(), event.getY());
							RefreshSonWinPos(event.getX(), event.getY());

							//限制移动区域
							float[] points = new float[]{m_target.m_x, m_target.m_y};
							FixPoint(points, m_target.m_centerX, m_target.m_centerY);
							m_target.m_x = points[0];
							m_target.m_y = points[1];

							this.invalidate();
						}
						break;
					}
					case MODE_ADV_SLIM_TOOL:
					{
						if(m_target != null)
						{
							if(m_target == m_slimToolBtnA)
							{
								if(!IsClickBtn(m_slimToolBtnA, event.getX(), event.getY()))
								{
									m_target = null;
								}
							}
							else if(m_target == m_slimToolBtnB)
							{
								if(!IsClickBtn(m_slimToolBtnB, event.getX(), event.getY()))
								{
									m_target = null;
								}
							}
							else if(m_target == m_slimToolBtnR)
							{
								if(m_slimTool != null)
								{
									//使用临时中心点
									Run_RZ(m_slimTool, temp_showCX, temp_showCY, event.getX(), event.getY());

									UpdateSlimTool();
								}
							}
							else if(m_target == m_slimTool)
							{
								Run_M(m_target, event.getX(), event.getY());

								//限制移动区域
								float[] points = new float[]{m_target.m_x, m_target.m_y};
								FixPoint(points, m_target.m_centerX, m_target.m_centerY);
								m_target.m_x = points[0];
								m_target.m_y = points[1];

								UpdateSlimTool();
							}

							this.invalidate();
						}
						break;
					}
					case MODE_ADV_SLIM_DRAG:
					{
						float[] src = new float[]{event.getX(), event.getY()};
						float[] dst = new float[2];
						GetLogicPos(dst, src);
						FixPoint(dst, 0, 0);
						m_dragToolX2 = dst[0];
						m_dragToolY2 = dst[1];

						this.invalidate();
						break;
					}
					default:
						break;
				}
				break;
			}
			default:
				super.OddMove(event);
				break;
		}
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		if(m_target == m_origin)
		{
			super.OddUp(event);
		}
		else
		{
			switch(m_operateMode)
			{
				case MODE_ACNE:
				case MODE_ADV_SLIM_TOOL:
				case MODE_ADV_SLIM_DRAG:
				{
					m_isTouch = false;

					switch(m_operateMode)
					{
						case MODE_ACNE:
						{
							if(m_isClick && m_cb != null && m_img != null && m_origin != null)
							{
								float[] src = new float[]{m_target.m_x + m_target.m_centerX, m_target.m_y + m_target.m_centerY};
								float[] dst = new float[2];
								GetFaceLogicPos(dst, src);
								((ControlCallback)m_cb).OnTouchAcne(dst[0], dst[1], m_acneToolR / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
							}
							break;
						}
						case MODE_ADV_SLIM_TOOL:
						{
							if(m_target != null && m_target == m_slimToolBtnA && IsClickBtn(m_slimToolBtnA, event.getX(), event.getY()) && m_cb != null && m_slimTool != null)
							{
								float[] src = new float[4];
								float[] dst = new float[]{0, 0, -(m_slimTool.m_centerX * m_slimTool.m_scaleX) / 2f, 0};
								GetSlimToolData(src, dst);
								src[0] = m_slimTool.m_x + m_slimTool.m_centerX;
								src[1] = m_slimTool.m_y + m_slimTool.m_centerY;
								GetFaceLogicPos(dst, src);
								((ControlCallback)m_cb).OnClickSlimTool(dst[0], dst[1], dst[2], dst[3], m_slimTool.m_centerX * m_slimTool.m_scaleX / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
							}
							else if(m_target != null && m_target == m_slimToolBtnB && IsClickBtn(m_slimToolBtnB, event.getX(), event.getY()) && m_cb != null && m_slimTool != null)
							{
								float[] src = new float[4];
								float[] dst = new float[]{0, 0, (m_slimTool.m_centerX * m_slimTool.m_scaleX) / 2f, 0};
								GetSlimToolData(src, dst);
								src[0] = m_slimTool.m_x + m_slimTool.m_centerX;
								src[1] = m_slimTool.m_y + m_slimTool.m_centerY;
								GetFaceLogicPos(dst, src);
								((ControlCallback)m_cb).OnClickSlimTool(dst[0], dst[1], dst[2], dst[3], m_slimTool.m_centerX * m_slimTool.m_scaleX / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
							}
							else if(m_target != null && (m_target == m_slimToolBtnR || m_target == m_slimTool) && m_origin != null && m_img != null && m_cb != null && m_slimTool != null)
							{
								((ControlCallback)m_cb).OnResetSlimTool((m_slimTool.m_w * m_slimTool.m_scaleX) / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
							}
							break;
						}
						case MODE_ADV_SLIM_DRAG:
						{
							if(m_isClick && m_cb != null && m_img != null)
							{
								float[] src = new float[]{m_dragToolX1, m_dragToolY1, m_dragToolX2, m_dragToolY2};
								float[] dst = new float[4];
								GetFaceLogicPos(dst, src);
								((ControlCallback)m_cb).OnDragSlim(dst[0], dst[1], dst[2], dst[3], m_dragToolR / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
							}
							break;
						}
						default:
							break;
					}

					m_sonWinX = 0;
					m_sonWinY = 0;
					m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);

					m_isClick = false;
					m_target = null;
					this.invalidate();
					break;
				}
				default:
					super.OddUp(event);
					break;
			}
		}
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		switch(m_operateMode)
		{
			case MODE_ACNE:
			{
				m_sonWinX = 0;
				m_sonWinY = 0;
				m_cb.UpdateSonWin(null, m_sonWinX, m_sonWinY);
				break;
			}
			default:
				break;
		}

		super.EvenDown(event);
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		switch(m_operateMode)
		{
			case MODE_ACNE:
			case MODE_ADV_SLIM_TOOL:
			case MODE_ADV_SLIM_DRAG:
			{
				switch(m_operateMode)
				{
					case MODE_ACNE:
						break;
					case MODE_ADV_SLIM_TOOL:
						if(m_target != null && (m_target == m_slimToolBtnR || m_target == m_slimTool) && m_origin != null && m_img != null && m_cb != null && m_slimTool != null)
						{
							((ControlCallback)m_cb).OnResetSlimTool((m_slimTool.m_w * m_slimTool.m_scaleX) / (m_img.m_w * m_img.m_scaleX * m_origin.m_scaleX));
						}
						break;
					case MODE_ADV_SLIM_DRAG:
						break;
					default:
						break;
				}
				break;
			}
			default:
				super.EvenUp(event);
				break;
		}
	}

	@Override
	public void SetOperateMode(int mode)
	{
		switch(mode)
		{
			case MODE_ACNE:
				m_operateMode = MODE_ACNE;
				break;

			case MODE_ADV_SLIM_TOOL:
				m_operateMode = MODE_ADV_SLIM_TOOL;
				break;

			case MODE_ADV_SLIM_DRAG:
				m_operateMode = MODE_ADV_SLIM_DRAG;
				break;

			default:
				super.SetOperateMode(mode);
				break;
		}
	}

	public void SetAcneToolRScale(float scale)
	{
		m_acneToolR = scale * m_acneToolDefR;
		if(m_acneToolR < 1)
		{
			m_acneToolR = 1;
		}
		DrawAcneTool();
		m_displayTime = System.currentTimeMillis() + DISPLAY_DURATION;
	}

	public void SetSlimToolRScale(float scale)
	{
		m_slimTool.m_scaleX = scale;
		if(m_slimTool.m_scaleX < m_slimTool.MIN_SCALE)
		{
			m_slimTool.m_scaleX = m_slimTool.MIN_SCALE;
		}
		else if(m_slimTool.m_scaleX > m_slimTool.MAX_SCALE)
		{
			m_slimTool.m_scaleX = m_slimTool.MAX_SCALE;
		}
		m_slimTool.m_scaleY = m_slimTool.m_scaleX;
	}

	public void SetSlimDragRScale(float scale)
	{
		m_dragToolR = m_dragToolDefR * scale;
		if(m_dragToolR < 1)
		{
			m_dragToolR = 1;
		}
		m_displayTime = System.currentTimeMillis() + DISPLAY_DURATION;
	}

	public static interface ControlCallback extends BeautifyView.ControlCallback
	{
		/**
		 * 
		 * @param x
		 *            逻辑坐标
		 * @param y
		 *            逻辑坐标
		 * @param rw
		 *            圈的比例大小(r/w)
		 */
		public void OnTouchAcne(float x, float y, float rw);

		/**
		 * 
		 * @param x1
		 *            逻辑坐标
		 * @param y1
		 *            逻辑坐标
		 * @param x2
		 *            逻辑坐标
		 * @param y2
		 *            逻辑坐标
		 * @param rw
		 *            圈的比例大小(r/w)
		 */
		public void OnClickSlimTool(float x1, float y1, float x2, float y2, float rw);

		/**
		 * 
		 * @param rw
		 */
		public void OnResetSlimTool(float rw);

		/**
		 * 
		 * @param x1
		 *            逻辑坐标
		 * @param y1
		 *            逻辑坐标
		 * @param x2
		 *            逻辑坐标
		 * @param y2
		 *            逻辑坐标
		 * @param rw
		 *            圈的比例大小(r/w)
		 */
		public void OnDragSlim(float x1, float y1, float x2, float y2, float rw);
	}
}
