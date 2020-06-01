package cn.poco.filterPendant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.MotionEvent;

import cn.poco.display.CoreViewV3;
import cn.poco.graphics.ShapeEx;
import cn.poco.image.filter;
import cn.poco.resource.GlassRes;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class PendantViewEx extends CoreViewV3
{
	public static final int CTRL_NZ_1 = 0x0010;
	public static final int CTRL_NZ_2 = 0x0020;
	public static final int CTRL_NZ_3 = 0x0040;
	public static final int CTRL_NZ_4 = 0x0080;

	//protected boolean m_invalidatePendant = true;

	protected ShapeEx[] m_nzoom4Btn;
	public int def_btn_touch_size;

	/**
	 * 0 : 图
	 * 1 : 形状
	 * 2 : 装饰
	 */
	public Bitmap m_glassBmp1;
	public Bitmap m_glassBmp2;
	public int m_color1;
	public int m_color2;

	public PendantViewEx(Context context, int frW, int frH)
	{
		super(context, frW, frH);
	}

	@Override
	protected void Init()
	{
		super.Init();

		m_operateMode = MODE_PENDANT;
		CommonUtils.CancelViewGPU(this);

		def_btn_touch_size = ShareData.PxToDpi_xhdpi(32);
	}

	@Override
	public void InitData(ControlCallback cb)
	{
		super.InitData(cb);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_pendant_nzoom4);
		m_nzoom4Btn = new ShapeEx[4];
		m_nzoom4Btn[0] = new ShapeEx();
		m_nzoom4Btn[0].m_bmp = bmp;
		m_nzoom4Btn[0].m_w = bmp.getWidth();
		m_nzoom4Btn[0].m_h = bmp.getHeight();
		m_nzoom4Btn[0].m_centerX = m_nzoom4Btn[0].m_w / 2f;
		m_nzoom4Btn[0].m_centerY = m_nzoom4Btn[0].m_h / 2f;
		m_nzoom4Btn[1] = new ShapeEx();
		m_nzoom4Btn[1].m_bmp = bmp;
		m_nzoom4Btn[1].m_w = bmp.getWidth();
		m_nzoom4Btn[1].m_h = bmp.getHeight();
		m_nzoom4Btn[1].m_centerX = m_nzoom4Btn[1].m_w / 2f;
		m_nzoom4Btn[1].m_centerY = m_nzoom4Btn[1].m_h / 2f;
		m_nzoom4Btn[2] = new ShapeEx();
		m_nzoom4Btn[2].m_bmp = bmp;
		m_nzoom4Btn[2].m_w = bmp.getWidth();
		m_nzoom4Btn[2].m_h = bmp.getHeight();
		m_nzoom4Btn[2].m_centerX = m_nzoom4Btn[2].m_w / 2f;
		m_nzoom4Btn[2].m_centerY = m_nzoom4Btn[2].m_h / 2f;
		m_nzoom4Btn[3] = new ShapeEx();
		m_nzoom4Btn[3].m_bmp = bmp;
		m_nzoom4Btn[3].m_w = bmp.getWidth();
		m_nzoom4Btn[3].m_h = bmp.getHeight();
		m_nzoom4Btn[3].m_centerX = m_nzoom4Btn[3].m_w / 2f;
		m_nzoom4Btn[3].m_centerY = m_nzoom4Btn[3].m_h / 2f;

		if(def_del_btn != 0)
		{
			bmp = BitmapFactory.decodeResource(getResources(), def_del_btn);
			mDeleteBtn = new ShapeEx();
			mDeleteBtn.m_bmp = bmp;
			mDeleteBtn.m_w = bmp.getWidth();
			mDeleteBtn.m_h = bmp.getHeight();
			mDeleteBtn.m_centerX = mDeleteBtn.m_w / 2f;
			mDeleteBtn.m_centerY = mDeleteBtn.m_h / 2f;
		}
	}

	protected float[] refix_src = new float[2];
	protected float[] refix_dst = new float[2];

	@Override
	protected void DrawToCanvas(Canvas canvas, int mode)
	{
		//if(m_invalidatePendant)
		//{
		//	//装饰位置已改变
		//	ShapeEx temp;
		//	int len = m_pendantArr.size();
		//	for(int i = 0; i < len; i++)
		//	{
		//		temp = m_pendantArr.get(i);
		//		refix_src[0] = temp.m_x + temp.m_centerX;
		//		refix_src[1] = temp.m_y + temp.m_centerY;
		//		GetFaceLogicPos(refix_dst, refix_src);
		//		((PendantViewEx.ControlCallback)m_cb).RefixPendant(temp, refix_dst[0], refix_dst[1], (temp.m_w * temp.m_scaleX) / (m_img.m_w * m_img.m_scaleX), temp.m_degree);
		//	}
		//
		//	m_invalidatePendant = false;
		//}
		//super.DrawToCanvas(canvas, mode);

		canvas.save();

		canvas.setDrawFilter(temp_filter);

		//控制渲染矩形
		ClipStage(canvas);

		//画背景
		DrawBK(canvas, m_bk, m_bkColor);

		//画图片
		//DrawItem(canvas, m_img);
		drawImage2(canvas);
		//画装饰
		//int len = m_pendantArr.size();
		//for(int i = 0; i < len; i++)
		//{
		//	DrawItem(canvas, m_pendantArr.get(i));
		//}

		//画选中框和按钮
		if(m_pendantCurSel >= 0 && m_pendantCurSel < m_pendantArr.size())
		{
			ShapeEx temp = m_pendantArr.get(m_pendantCurSel);
			//画选中框
			DrawRect(canvas, temp);

			//显示单手旋转放大按钮
			if(!m_isTouch)
			{
				DrawButtons(canvas, temp);

			}
			DrawDeleteBtn(canvas, temp);

			if(temp != null && temp instanceof BlurShapeEx && ((BlurShapeEx)temp).m_freeScale)
			{
				//画4边自由拉伸的按钮
				DrawButtons2(canvas, temp);
			}
		}

		canvas.restore();
	}

	public int def_del_btn = R.drawable.photofactory_pendant_rotation;
	public ShapeEx mDeleteBtn;

	protected void DrawDeleteBtn(Canvas canvas, ShapeEx item)
	{
		if(mDeleteBtn != null)
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

			//计算按钮的位置
			float[] dst = new float[8];
			temp_src[0] = temp_dst[0] - mDeleteBtn.m_centerX / 2f;
			temp_src[1] = temp_dst[1] - mDeleteBtn.m_centerY / 2f;
			temp_src[2] = temp_dst[2] + mDeleteBtn.m_centerX / 2f;
			temp_src[3] = temp_dst[3] - mDeleteBtn.m_centerY / 2f;
			temp_src[4] = temp_dst[4] + mDeleteBtn.m_centerX / 2f;
			temp_src[5] = temp_dst[5] + mDeleteBtn.m_centerY / 2f;
			temp_src[6] = temp_dst[6] - mDeleteBtn.m_centerX / 2f;
			temp_src[7] = temp_dst[7] + mDeleteBtn.m_centerY / 2f;
			Matrix matrix = new Matrix();
			matrix.postRotate(item.m_degree, cxy[0], cxy[1]);
			matrix.mapPoints(dst, temp_src);

			float[] dst2 = new float[8];
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
				if(c - a > mDeleteBtn.m_w)
				{
					a += mDeleteBtn.m_centerX;
					c -= mDeleteBtn.m_centerX;
				}
				if(d - b > mDeleteBtn.m_h)
				{
					b += mDeleteBtn.m_centerY;
					d -= mDeleteBtn.m_centerY;
				}

				//测试用
				float p0x = (a + c) / 2f;
				float p0y = (b + d) / 2f;

				//(dst[0], dst[1])左上角坐标
				if(dst[0] > a && dst[0] < c && dst[1] > b && dst[1] < d)
				{
					temp_point_src[0] = dst[0];
					temp_point_src[1] = dst[1];
				}
				//(dst[4], dst[5])右下角坐标
				else if(dst[4] > a && dst[4] < c && dst[5] > b && dst[5] < d)
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

				//(dst[2], dst[3])右上角坐标,注意和其他按钮冲突
				else if((m_nzoomBtn == null || !m_hasZoomBtn) && dst[2] > a && dst[2] < c && dst[3] > b && dst[3] < d)
				{
					temp_point_src[0] = dst[2];
					temp_point_src[1] = dst[3];
				}
				else
				{
					float d1 = ImageUtils.Spacing(p0x - dst[0], p0y - dst[1]);
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
				mDeleteBtn.m_x = temp_point_dst[0] - mDeleteBtn.m_centerX;
				mDeleteBtn.m_y = temp_point_dst[1] - mDeleteBtn.m_centerY;

				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				GetShowMatrixNoScale(temp_matrix, mDeleteBtn);
				canvas.drawBitmap(mDeleteBtn.m_bmp, temp_matrix, temp_paint);
			}
		}
	}

	protected void DrawButtons2(Canvas canvas, ShapeEx item)
	{
		if(item != null && m_nzoom4Btn != null)
		{
			//移动到正确位置
			temp_matrix.reset();
			temp_matrix.postTranslate(item.m_x, item.m_y);
			temp_matrix.postScale(item.m_scaleX, item.m_scaleY, item.m_x + item.m_centerX, item.m_y + item.m_centerY);
			temp_matrix.postRotate(item.m_degree, item.m_x + item.m_centerX, item.m_y + item.m_centerY);
			temp_src[0] = 0;
			temp_src[1] = item.m_h / 2f;
			temp_src[2] = item.m_w / 2f;
			temp_src[3] = 0;
			temp_src[4] = item.m_w;
			temp_src[5] = item.m_h / 2f;
			temp_src[6] = item.m_w / 2f;
			temp_src[7] = item.m_h;
			temp_matrix.mapPoints(temp_dst, temp_src);

			ShapeEx temp;
			for(int i = 0; i < m_nzoom4Btn.length; i++)
			{
				temp = m_nzoom4Btn[i];
				temp.m_x = temp_dst[i << 1] - temp.m_centerX;
				temp.m_y = temp_dst[(i << 1) + 1] - temp.m_centerY;

				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				GetShowMatrixNoScale(temp_matrix, temp);
				canvas.drawBitmap(temp.m_bmp, temp_matrix, temp_paint);
			}
		}
	}

	@Override
	protected void OddUp(MotionEvent event)
	{

		//判断主图片位置，并且移动到合适位置
		if(m_isTouch)
		{
			switch(m_operateMode)
			{
				case MODE_ALL:
					break;

				case MODE_FRAME:
					if(m_img != null && m_img == m_target)
					{
						float limit;
						if(m_viewport.m_w > m_viewport.m_h)
						{
							limit = def_limit_sacle * m_viewport.m_w * m_viewport.m_scaleX;
						}
						else
						{
							limit = def_limit_sacle * m_viewport.m_h * m_viewport.m_scaleY;
						}
						float w2 = m_viewport.m_centerX * m_viewport.m_scaleX;
						float h2 = m_viewport.m_centerY * m_viewport.m_scaleY;
						float left = m_viewport.m_x + m_viewport.m_centerX - w2;
						float top = m_viewport.m_y + m_viewport.m_centerY - h2;
						float right = m_viewport.m_x + m_viewport.m_centerX + w2;
						float bottom = m_viewport.m_y + m_viewport.m_centerY + h2;
						float imgw2 = m_img.m_centerX * m_img.m_scaleX;
						float imgh2 = m_img.m_centerY * m_img.m_scaleY;

						if(imgw2 > limit)
						{
							left -= imgw2 - limit;
							right += imgw2 - limit;
						}

						if(imgh2 > limit)
						{
							top -= imgh2 - limit;
							bottom += imgh2 - limit;
						}

						float cx = m_img.m_x + m_img.m_centerX;
						float cy = m_img.m_y + m_img.m_centerY;

						if(cx < left)
						{
							m_img.m_x = left - m_img.m_centerX;
						}
						else if(cx > right)
						{
							m_img.m_x = right - m_img.m_centerX;
						}
						if(cy < top)
						{
							m_img.m_y = top - m_img.m_centerY;
						}
						else if(cy > bottom)
						{
							m_img.m_y = bottom - m_img.m_centerY;
						}
					}
					break;

				case MODE_PENDANT:
				case MODE_IMAGE:
				default:
					break;
			}
		}

		//自动调节角度
		if(m_target != null && m_target != m_origin && m_target != m_img && m_rotationBtn != null && IsClickBtn(m_rotationBtn, event.getX(), event.getY()))
		{
			float degreeLack = m_target.m_degree % 90;
			Log.d("debugtag", "degreeLack:" + degreeLack);
			if(degreeLack >= -10 && degreeLack <= 10)
			{
				m_target.m_degree += 0 - degreeLack;
			}
			if(degreeLack >= 80 && degreeLack <= 90)
			{
				m_target.m_degree += 90 - degreeLack;
			}
			if(degreeLack <= -80 && degreeLack >= -90)
			{
				m_target.m_degree += -90 - degreeLack;
			}
			//Log.d("debugtag", "m_target.m_degree:" + m_target.m_degree);
		}

		m_isTouch = false;
		m_isOddCtrl = false;
		m_target = null;
		if(m_pendantArr != null && m_pendantArr.size() > 1)
		{
			createPendantGlassBmp();
		}
		UpdateUI();
	}

	@Override
	protected void EvenMove(MotionEvent event)
	{
		// TODO Auto-generated method stub
		super.EvenMove(event);
		//自动调节角度
		if(m_target != null && m_target != m_origin && m_target != m_img)
		{
			float degreeLack = m_target.m_degree % 90;
			Log.d("debugtag", "degreeLack:" + degreeLack);
			if(degreeLack >= -3 && degreeLack <= 3)
			{
				m_target.m_degree += 0 - degreeLack;
			}
			if(degreeLack >= 87 && degreeLack <= 90)
			{
				m_target.m_degree += 90 - degreeLack;
			}
			if(degreeLack <= -87 && degreeLack >= -90)
			{
				m_target.m_degree += -90 - degreeLack;
			}
			//Log.d("debugtag", "m_target.m_degree:" + m_target.m_degree);
		}
		UpdateUI();
	}

	private float temp_showCX;
	private float temp_showCY;

	@Override
	protected void OddDown(MotionEvent event)
	{
		m_isTouch = true;

		switch(m_operateMode)
		{
			case MODE_ALL:
				m_target = m_origin;
				Init_M_Data(m_target, m_downX, m_downY);
				break;

			case MODE_FRAME:
				m_target = m_img;
				Init_M_Data(m_target, m_downX, m_downY);
				break;

			case MODE_PENDANT:
			{
				if(m_pendantCurSel >= 0)
				{
					//判断是否选中旋转放大按钮
					if(m_rotationBtn != null && IsClickBtn(m_rotationBtn, m_downX, m_downY))
					{
						m_target = m_pendantArr.get(m_pendantCurSel);
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
						m_target = m_pendantArr.get(m_pendantCurSel);
						m_isOddCtrl = true;
						m_oddCtrlType = CTRL_NZ;
						Init_NZ_Data(m_target, m_downX, m_downY);

						return;
					}
					m_target = m_pendantArr.get(m_pendantCurSel);
					if(m_target != null && m_target instanceof BlurShapeEx && ((BlurShapeEx)m_target).m_freeScale)
					{
						//4点非等比例拉伸按钮
						int index;
						if(m_nzoom4Btn != null && (index = IsClickBtn(m_nzoom4Btn, m_downX, m_downY, def_btn_touch_size)) >= 0)
						{
							m_target = m_pendantArr.get(m_pendantCurSel);
							m_isOddCtrl = true;
							switch(index)
							{
								case 0:
									m_oddCtrlType = CTRL_NZ_1;
									break;
								case 1:
									m_oddCtrlType = CTRL_NZ_2;
									break;
								case 2:
									m_oddCtrlType = CTRL_NZ_3;
									break;
								case 3:
									m_oddCtrlType = CTRL_NZ_4;
									break;
								default:
									break;
							}
							Init_MNZ4_Data(m_target, m_downX, m_downY);

							return;
						}
					}

					//判断是否选中删除按钮
					if(mDeleteBtn != null && IsClickBtn(mDeleteBtn, m_downX, m_downY))
					{
						int type = ((BlurShapeEx)this.DelPendant()).m_type;
						if(type == TYPE_SHAPE)
						{
							if(m_glassBmp2 != null && !m_glassBmp2.isRecycled())
							{
								m_glassBmp2.recycle();
								m_glassBmp2 = null;
							}
							m_glassBmp2 = filter.fakeGlassBeauty(m_img.m_bmp.copy(Config.ARGB_8888, true), m_color2);
						}
						if(m_cb != null)
						{
							((MyFilterPendantViewControlCallback)m_cb).deletePendantType(type);
						}
						//更新界面
						this.invalidate();
						return;
					}
				}

				int index = GetSelectIndex(m_pendantArr, m_downX, m_downY);
				if(index >= 0)
				{
					m_target = m_pendantArr.get(index);
					m_pendantCurSel = index;
					//m_pendantArr.remove(index);
					//m_pendantArr.add(m_target);
					//m_pendantCurSel = m_pendantArr.size() - 1;
					m_isOddCtrl = false;
					Init_M_Data(m_target, m_downX, m_downY);
					//通知主界面选中信息
					m_cb.SelectPendant(m_pendantCurSel);

					//更新界面
					this.invalidate();
				}
				else
				{
					if(m_pendantCurSel >= 0)
					{
						m_pendantCurSel = -1;
						//通知主界面选中信息
						m_cb.SelectPendant(m_pendantCurSel);

						//更新界面
						this.invalidate();
					}
					m_isOddCtrl = false;
					m_target = null;
				}
				break;
			}

			case MODE_IMAGE:
			default:
				m_target = null;
				break;
		}

		if(m_cb != null)
		{
			((MyFilterPendantViewControlCallback)m_cb).fingerDown();
		}
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		m_isTouch = true;
		m_isOddCtrl = false;

		switch(m_operateMode)
		{
			case MODE_ALL:
				m_target = m_origin;
				Init_ZM_Data(m_target, m_downX1, m_downY1, m_downX2, m_downY2);
				break;

			case MODE_FRAME:
				m_target = m_img;
				Init_MRZ_Data(m_target, m_downX1, m_downY1, m_downX2, m_downY2);
				break;

			case MODE_PENDANT:
			{
				int index = GetSelectIndex(m_pendantArr, (m_downX1 + m_downX2) / 2f, (m_downY1 + m_downY2) / 2f);
				if(index >= 0)
				{
					m_target = m_pendantArr.get(index);
					m_pendantCurSel = index;
					//m_pendantArr.remove(index);
					//m_pendantArr.add(m_target);
					//m_pendantCurSel = m_pendantArr.size() - 1;
					Init_MRZ_Data(m_target, m_downX1, m_downY1, m_downX2, m_downY2);
					//通知主界面选中信息
					m_cb.SelectPendant(m_pendantCurSel);
					//更新界面
					this.invalidate();
				}
				else
				{
					if(m_pendantCurSel >= 0)
					{
						if(m_pendantCurSel >= 0)
						{
							m_pendantCurSel = -1;
							//通知主界面选中信息
							m_cb.SelectPendant(m_pendantCurSel);
							//更新界面
							this.invalidate();
						}
						m_target = null;
					}
				}
				break;
			}

			case MODE_IMAGE:
			default:
				m_target = null;
				break;
		}
	}

	protected void Init_MNZ4_Data(ShapeEx target, float x1, float y1)
	{
		m_oldX = target.m_x;
		m_oldY = target.m_y;

		Matrix matrix = new Matrix(); //不能包含反转参数
		matrix.postTranslate(target.m_x, target.m_y);
		matrix.postScale(target.m_scaleX, target.m_scaleY, target.m_x + target.m_centerX, target.m_y + target.m_centerY);
		matrix.postRotate(target.m_degree, target.m_x + target.m_centerX, target.m_y + target.m_centerY);
		switch(m_oddCtrlType)
		{
			case CTRL_NZ_1:
				temp_src2[0] = 0;
				temp_src2[1] = target.m_h / 2f;
				break;
			case CTRL_NZ_2:
				temp_src2[0] = target.m_w / 2f;
				temp_src2[1] = 0;
				break;
			case CTRL_NZ_3:
				temp_src2[0] = target.m_w;
				temp_src2[1] = target.m_h / 2f;
				break;
			case CTRL_NZ_4:
				temp_src2[0] = target.m_w / 2f;
				temp_src2[1] = target.m_h;
				break;
			default:
				break;
		}
		matrix.mapPoints(temp_dst2, temp_src2);

		m_gammaX = temp_dst2[0] - x1; //偏移量
		m_gammaY = temp_dst2[1] - y1;
		if(temp_old_matrix == null)
		{
			temp_old_matrix = new Matrix();
		}
		temp_old_matrix.reset();
		matrix.invert(temp_old_matrix);
		temp_old_matrix.postScale(target.m_scaleX, target.m_scaleY, target.m_centerX, target.m_centerY);
		m_oldScaleX = target.m_scaleX;
		m_oldScaleY = target.m_scaleY;
	}

	protected void Run_MNZ4(ShapeEx target, float x, float y)
	{
		temp_src2[0] = x + m_gammaX;
		temp_src2[1] = y + m_gammaY;
		temp_old_matrix.mapPoints(temp_dst2, temp_src2);
		float newScaleX = m_oldScaleX;
		float newScaleY = m_oldScaleY;
		//float offsetX = 0;
		//float offsetY = 0;
		float temp;
		switch(m_oddCtrlType)
		{
			case CTRL_NZ_1:
				if(temp_dst2[0] > target.m_centerX)
				{
					temp_dst2[0] = target.m_centerX;
				}
				temp = (target.m_centerX - temp_dst2[0]) * 2f / (float)target.m_w;
				//temp = (temp - m_oldScaleX) / 2f;
				//newScaleX = temp + m_oldScaleX;
				newScaleX = temp;
				//offsetX = -temp * target.m_centerX;
				break;

			case CTRL_NZ_2:
				if(temp_dst2[1] > target.m_centerY)
				{
					temp_dst2[1] = target.m_centerY;
				}
				temp = (target.m_centerY - temp_dst2[1]) * 2f / (float)target.m_h;
				//temp = (temp - m_oldScaleY) / 2f;
				//newScaleY = temp + m_oldScaleY;
				newScaleY = temp;
				//offsetY = -temp * target.m_centerY;
				break;

			case CTRL_NZ_3:
				if(temp_dst2[0] < target.m_centerX)
				{
					temp_dst2[0] = target.m_centerX;
				}
				temp = (temp_dst2[0] - target.m_centerX) * 2f / (float)target.m_w;
				//temp = (temp - m_oldScaleX) / 2f;
				//newScaleX = temp + m_oldScaleX;
				newScaleX = temp;
				//offsetX = temp * target.m_centerX;
				break;

			case CTRL_NZ_4:
				if(temp_dst2[1] < target.m_centerY)
				{
					temp_dst2[1] = target.m_centerY;
				}
				temp = (temp_dst2[1] - target.m_centerY) * 2f / (float)target.m_h;
				//temp = (temp - m_oldScaleY) / 2f;
				//newScaleY = temp + m_oldScaleY;
				newScaleY = temp;
				//offsetY = temp * target.m_centerY;
				break;

			default:
				break;
		}
		target.SetScaleXY(newScaleX, newScaleY);

		//移动到中心
		//target.m_x = offsetX + m_oldX;
		//target.m_y = offsetY + m_oldY;
	}

	protected void OddMove(MotionEvent event)
	{
		if(m_isTouch && m_target != null)
		{
			switch(m_operateMode)
			{
				case MODE_ALL:
					Run_M2(m_target, event.getX(), event.getY());
					//更新界面
					this.invalidate();
					break;

				case MODE_FRAME:
					Run_M(m_target, event.getX(), event.getY());
					//更新界面
					this.invalidate();
					break;

				case MODE_PENDANT:
				{
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
						else if(m_oddCtrlType == CTRL_NZ_1 || m_oddCtrlType == CTRL_NZ_2 || m_oddCtrlType == CTRL_NZ_3 || m_oddCtrlType == CTRL_NZ_4)
						{
							Run_MNZ4(m_target, event.getX(), event.getY());
						}
					}
					else
					{
						Run_M(m_target, event.getX(), event.getY());
					}
					//更新界面
					this.invalidate();
					break;
				}

				case MODE_IMAGE:
				default:
					break;
			}
		}
	}

	protected int IsClickBtn(ShapeEx[] arr, float x, float y, float clickSize)
	{
		int out = -1;

		if(arr != null)
		{
			float minSize = 0;
			float[] src = new float[2];
			float[] dst = new float[2];
			ShapeEx temp;
			float size = 0;
			for(int i = 0; i < arr.length; i++)
			{
				temp = arr[i];
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

	//public void mySetImage(Bitmap blurBmp, Bitmap orgBmp)
	//{
	//	if(orgBmp == null)
	//	{
	//		return;
	//	}
	//	SetImg(blurBmp, orgBmp);
	//}

	public int myAddPendant(BlurShapeEx item)
	{
		int out = -1;

		if(item == null)
		{
			return -1;
		}

		//删除相同类型
		DelGlassItem(item.m_type);
		//添加
		switch(item.m_type)
		{
			case TYPE_SHAPE:
				m_pendantArr.add(0, item);
				out = 0;
				break;
			case TYPE_PENDANT:
				m_pendantArr.add(item);
				out = m_pendantArr.size() - 1;
				break;
			default:
				break;
		}
		//如果有2层则构造第二层毛玻璃
		if(m_pendantArr.size() > 1 || m_glassBmp2 == null)
		{
			//用m_color2
			//					m_glassBmp2 = filter.fakeGlass(m_glassBmp1.copy(Config.ARGB_8888, true), m_color2);
			createPendantGlassBmp();
		}
		return out;
	}

	public int myAddPendant(GlassRes pendantInfo)
	{
		if(pendantInfo == null)
		{
			return -1;
		}
		return myAddPendant(changeToShapeEx(pendantInfo));
	}

	public BlurShapeEx changeToShapeEx(GlassRes info)
	{
		BlurShapeEx item = new BlurShapeEx();
		if(info.m_glassType == GlassRes.GLASS_TYPE_PENDANT)
		{
			item.m_type = TYPE_PENDANT;
		}
		else
		{
			item.m_type = TYPE_SHAPE;
		}
		item.m_bmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), info.m_mask, 0, -1, -1, -1);
		//if(tempBmp != null)
		//{
		//	item.m_bmp = MakeBmp.CreateBitmap(tempBmp, this.m_img.m_w, this.m_img.m_h, -1, 0, Config.ARGB_8888);
		//	tempBmp.recycle();
		//	tempBmp = null;
		//}
		if(item.m_bmp == null)
		{
			return null;
		}
		item.m_freeScale = info.m_canFreedomZoom;
		item.m_w = item.m_bmp.getWidth();
		item.m_h = item.m_bmp.getHeight();
		item.m_centerX = (float)item.m_w / 2f;
		item.m_centerY = (float)item.m_h / 2f;
		float scale = 1f;
		if(info.h_fill_parent > 0 || info.v_fill_parent > 0)
		{
			if(info.v_fill_parent > 0 && info.h_fill_parent > 0)
			{
				item.m_scaleY = 1f * m_img.m_h * m_img.m_scaleY / item.m_h * info.v_fill_parent / 100f;
				item.m_scaleX = 1f * m_img.m_w * m_img.m_scaleX / item.m_w * info.h_fill_parent / 100f;
			}
			else
			{
				if(info.v_fill_parent > 0)
				{
					item.m_scaleY = 1f * m_img.m_h * m_img.m_scaleY / item.m_h * info.v_fill_parent / 100f;
					item.m_scaleX = item.m_scaleY;
				}

				if(info.h_fill_parent > 0)
				{
					item.m_scaleX = 1f * m_img.m_w * m_img.m_scaleX / item.m_w * info.h_fill_parent / 100f;
					item.m_scaleY = item.m_scaleX;
				}
			}
		}
		else
		{
			if(info.m_glassType == GlassRes.GLASS_TYPE_PENDANT)
			{
				//按1024适配
				if(m_img.m_w < m_img.m_h)
				{
					scale = 1f * m_img.m_w * m_img.m_scaleX / 1024;
				}
				else
				{
					scale = 1f * m_img.m_h * m_img.m_scaleY / 1024;
				}
			}
			else
			{
				//按图片大小适配
				if(m_img.m_w == m_img.m_h && item.m_w > item.m_h)
				{
					scale = 1f * m_img.m_w * m_img.m_scaleX / item.m_w;
				}
				else if(m_img.m_w < m_img.m_h)
				{
					scale = 1f * m_img.m_w * m_img.m_scaleX / item.m_w;
				}
				else
				{
					scale = 1f * m_img.m_h * m_img.m_scaleY / item.m_h;
				}
			}

			item.m_scaleX = scale * info.m_scale / 100f;
			item.m_scaleY = item.m_scaleX;
		}
		if(info.horizontal_pos == GlassRes.POS_START)
		{
			float realW = this.m_img.m_w * this.m_img.m_scaleX;
			//float imgLeftX = (this.m_img.m_x + this.m_img.m_centerX) - this.m_img.m_centerX * this.m_img.m_scaleX;
			float imgLeftX = this.m_origin.m_centerX - this.m_img.m_centerX * this.m_img.m_scaleX;
			//相对图片的位置移动
			item.m_x = imgLeftX + info.horizontal_value / 100f * realW + item.m_centerX * item.m_scaleX - item.m_centerX;
			//相对自身的位置移动
			item.m_x += info.self_offset_x / 100f * item.m_w * item.m_scaleX;
		}
		else if(info.horizontal_pos == GlassRes.POS_END)
		{
			float realW = this.m_img.m_w * this.m_img.m_scaleX;
			//float imgRightX = (this.m_img.m_x + this.m_img.m_centerX) - this.m_img.m_centerX * this.m_img.m_scaleX + realW;
			float imgRightX = this.m_origin.m_centerX - this.m_img.m_centerX * this.m_img.m_scaleX + realW;
			//相对图片的位置移动
			item.m_x = imgRightX - item.m_w * item.m_scaleX - info.horizontal_value / 100f * realW + item.m_centerX * item.m_scaleX - item.m_centerX;
			//相对自身的位置移动
			item.m_x -= info.self_offset_x / 100f * item.m_w * item.m_scaleX;
		}
		else
		{
			item.m_x = (float)this.m_origin.m_w / 2f - item.m_centerX;
		}
		if(info.vertical_pos == GlassRes.POS_START)
		{
			float realH = this.m_img.m_h * this.m_img.m_scaleY;
			//float imgTopY = (this.m_img.m_y + this.m_img.m_centerY) - this.m_img.m_centerY * this.m_img.m_scaleY;
			float imgTopY = this.m_origin.m_centerY - this.m_img.m_centerY * this.m_img.m_scaleY;
			//相对图片的位置移动
			item.m_y = imgTopY + info.vertical_value / 100f * realH + item.m_centerY * item.m_scaleY - item.m_centerY;
			//相对自身的位置移动
			item.m_y += info.self_offset_y / 100f * item.m_h * item.m_scaleY;
		}
		else if(info.vertical_pos == GlassRes.POS_END)
		{
			float realH = this.m_img.m_h * this.m_img.m_scaleY;
			//float imgBottomY = (this.m_img.m_y + this.m_img.m_centerY) - this.m_img.m_centerY * this.m_img.m_scaleY + realH;
			float imgBottomY = this.m_origin.m_centerY - this.m_img.m_centerY * this.m_img.m_scaleY + realH;
			//相对图片的位置移动
			item.m_y = imgBottomY - item.m_h * item.m_scaleY - info.vertical_value / 100f * realH + item.m_centerY * item.m_scaleY - item.m_centerY;
			//相对自身的位置移动
			item.m_y -= info.self_offset_y / 100f * item.m_h * item.m_scaleY;
		}
		else
		{
			item.m_y = (float)this.m_origin.m_h / 2f - item.m_centerY;
		}

		item.m_ex = cn.poco.imagecore.Utils.DecodeImage(getContext(), info.m_img, 0, -1, -1, -1);
		//if(tempBmp != null)
		//{
		//	item.m_ex = MakeBmp.CreateBitmap(tempBmp, this.m_img.m_w, this.m_img.m_h, -1, 0, Config.ARGB_8888);
		//	tempBmp.recycle();
		//	tempBmp = null;
		//}

		/*//控制缩放比例
		item.DEF_SCALE = item.m_scaleX;
		{
			float scale1 = (float)mBeautifyView.m_origin.m_w * mBeautifyView.def_pendant_max_scale / (float)item.m_w;
			float scale2 = (float)mBeautifyView.m_origin.m_h * mBeautifyView.def_pendant_max_scale / (float)item.m_h;
			item.MAX_SCALE = (scale1 > scale2) ? scale2 : scale1;
		
			scale1 = (float)mBeautifyView.m_origin.m_w * mBeautifyView.def_pendant_min_scale / (float)item.m_w;
			scale2 = (float)mBeautifyView.m_origin.m_h * mBeautifyView.def_pendant_min_scale / (float)item.m_h;
			item.MIN_SCALE = (scale1 > scale2) ? scale2 : scale1;
		}*/

		////控制缩放比例
		//item.DEF_SCALE = item.m_scaleX;
		//{
		//	//float scale1 = (float)m_origin.m_w * 2f / (float)item.m_w;
		////float scale2 = (float)m_origin.m_h * 2f / (float)item.m_h;
		////item.MAX_SCALE = (scale1 > scale2) ? scale2 : scale1;
		//	item.MAX_SCALE = 1.5f;
		//
		//	float scale1 = 10f / (float)item.m_w;
		//	float scale2 = 10f / (float)item.m_h;
		//	item.MIN_SCALE = (scale1 > scale2) ? scale2 : scale1;
		//}

		//控制缩放比例
		item.DEF_SCALE = item.m_scaleX > item.m_scaleY ? item.m_scaleX : item.m_scaleY;
		{
			float scale1 = (float)m_origin.m_w * def_pendant_max_scale / (float)item.m_w;
			float scale2 = (float)m_origin.m_h * def_pendant_max_scale / (float)item.m_h;
			item.MAX_SCALE = (scale1 > scale2) ? scale2 : scale1;
			item.MAX_SCALE = item.MAX_SCALE < item.DEF_SCALE ? item.DEF_SCALE * def_pendant_max_scale : item.MAX_SCALE;

			scale1 = (float)m_origin.m_w * def_pendant_min_scale / (float)item.m_w;
			scale2 = (float)m_origin.m_h * def_pendant_min_scale / (float)item.m_h;
			item.MIN_SCALE = (scale1 > scale2) ? scale2 : scale1;
		}

		return item;
	}

	private PorterDuffXfermode clear_mode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	private PorterDuffXfermode src_over_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
	private PorterDuffXfermode dst_over_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
	private PorterDuffXfermode dst_in_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
	private PorterDuffXfermode dst_out_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
	private PorterDuffXfermode src_in_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
	private PorterDuffXfermode src_out_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

	public static final int TYPE_SHAPE = 0;
	public static final int TYPE_PENDANT = 1;
	public static int mType = TYPE_PENDANT;

	/**
	 * @param type {@link PendantViewEx#TYPE_PENDANT}/
	 *             {@link PendantViewEx#TYPE_SHAPE}
	 */
	public void setType(int type)
	{
		mType = type;
		//合并
	}

	private boolean isReverseMode = false;

	public void setReverseMode(boolean isReverse)
	{
		isReverseMode = isReverse;
		if(m_pendantArr != null && m_pendantArr.size() > 1)
		{
			createPendantGlassBmp();
		}
	}

	//		private boolean mFreeScale = false;
	//
	//	public void setFreeScale(boolean freeScale)
	//	{
	//		mFreeScale = freeScale;
	//	}

	public void SetGlassColor(int shapeColor, int pendantColor)
	{
		if(m_glassBmp1 == null || m_color1 != shapeColor)
		{
			//生成第一层毛玻璃
			if(m_glassBmp1 != null)
			{
				m_glassBmp1.recycle();
				m_glassBmp1 = null;
			}
			m_glassBmp1 = filter.fakeGlassBeauty(m_img.m_bmp.copy(Config.ARGB_8888, true), shapeColor);
		}
		if(m_pendantArr != null && m_pendantArr.size() > 1 && (m_color2 != pendantColor || m_color1 != shapeColor))
		{
			//生成第二层毛玻璃
			//			if(m_glassBmp2 != null)
			//			{
			//				m_glassBmp2.recycle();
			//				m_glassBmp2 = null;
			//			}
			//			m_glassBmp2 = filter.fakeGlass(m_glassBmp1.copy(Config.ARGB_8888, true), pendantColor);
			m_color2 = pendantColor;
			createPendantGlassBmp();
		}
		else if(m_color2 != pendantColor)
		{
			m_color2 = pendantColor;
			if(m_glassBmp2 != null)
			{
				m_glassBmp2.recycle();
				m_glassBmp2 = null;
			}
			m_glassBmp2 = filter.fakeGlassBeauty(m_img.m_bmp.copy(Config.ARGB_8888, true), pendantColor);
		}
		m_color1 = shapeColor;
	}

	private void drawImage(Canvas canvas)
	{
		int len = m_pendantArr.size();
		if(len > 0)
		{
			//myDrawItem(canvas, m_img, m_img.m_bmp, src_over_mode);
			//
			//for(int i = 0; i < len; i++)
			//{
			//	myDrawItem(canvas, m_pendantArr.get(i), m_pendantArr.get(i).m_bmp, isPendantType ? dst_out_mode : dst_in_mode);
			//	if(m_pendantArr.get(i).m_ex != null && m_pendantArr.get(i).m_ex instanceof Bitmap)
			//	{
			//		myDrawItem(canvas, m_pendantArr.get(i), (Bitmap)m_pendantArr.get(i).m_ex, src_over_mode);
			//	}
			//}
			//if(m_img.m_ex != null && m_img.m_ex instanceof Bitmap)
			//{
			//	myDrawItem(canvas, m_img, (Bitmap)m_img.m_ex, dst_over_mode);
			//}
			//Log.d("debugtag", "pendant degree:" + m_pendantArr.get(0).m_degree);
			for(int i = 0; i < len; i++)
			{
				myDrawShowItem(canvas, m_pendantArr.get(i), m_pendantArr.get(i).m_bmp, src_over_mode);
			}
			PorterDuffXfermode tempMode = src_out_mode;
			if(mType == TYPE_PENDANT)
			{
				if(isReverseMode)
				{
					tempMode = src_in_mode;
				}
				else
				{
					tempMode = src_out_mode;
				}
			}
			else if(mType == TYPE_SHAPE)
			{
				if(isReverseMode)
				{
					tempMode = src_out_mode;
				}
				else
				{
					tempMode = src_in_mode;
				}
			}
			myDrawShowItem(canvas, m_img, m_img.m_bmp, tempMode);
			for(int i = 0; i < len; i++)
			{
				if(m_pendantArr.get(i).m_ex != null && m_pendantArr.get(i).m_ex instanceof Bitmap)
				{
					myDrawShowItem(canvas, m_pendantArr.get(i), (Bitmap)m_pendantArr.get(i).m_ex, src_over_mode);
				}
			}
			if(m_img.m_ex != null && m_img.m_ex instanceof Bitmap)
			{
				myDrawShowItem(canvas, m_img, (Bitmap)m_img.m_ex, dst_over_mode);
			}
		}
		else
		{
			DrawItem(canvas, m_img);
		}
	}

	private void drawImage2(Canvas canvas)
	{
		int len = m_pendantArr.size();
		if(len > 0)
		{
			if(((BlurShapeEx)(m_pendantArr.get(0))).m_type == TYPE_PENDANT)
			{
				ShapeEx m_pendant = m_pendantArr.get(0);
				myDrawShowItem(canvas, m_img, m_img.m_bmp, src_out_mode);
				myDrawShowItem(canvas, m_pendant, m_pendant.m_bmp, dst_out_mode);
				myDrawShowItem(canvas, m_img, m_glassBmp2, dst_over_mode);
				if(m_pendant.m_ex != null && m_pendant.m_ex instanceof Bitmap)
				{
					myDrawShowItem(canvas, m_pendant, (Bitmap)m_pendant.m_ex, src_over_mode);
				}
			}
			else
			{
				for(int i = 0; i < len; i++)
				{
					ShapeEx m_pendant = m_pendantArr.get(i);
					if(((BlurShapeEx)(m_pendant)).m_type == TYPE_SHAPE)
					{
						myDrawShowItem(canvas, m_pendant, m_pendant.m_bmp, src_over_mode);
						PorterDuffXfermode tempMode = src_in_mode;
						if(isReverseMode)
						{
							tempMode = src_out_mode;
						}
						else
						{
							tempMode = src_in_mode;
						}
						myDrawShowItem(canvas, m_img, m_img.m_bmp, tempMode);
						myDrawShowItem(canvas, m_img, m_glassBmp1, dst_over_mode);
						//						createShapeBmpWithGlass();
						//						myDrawShowItem(canvas, m_img, mShapeLayer, src_over_mode);
					}
					else
					{
						myDrawShowItem(canvas, m_pendant, m_pendant.m_bmp, dst_out_mode);
						myDrawShowItem(canvas, m_img, m_glassBmp2, dst_over_mode);
						if(m_pendant.m_ex != null && m_pendant.m_ex instanceof Bitmap)
						{
							myDrawShowItem(canvas, m_pendant, (Bitmap)m_pendant.m_ex, src_over_mode);
						}
					}
				}
			}
		}
		else
		{
			DrawItem(canvas, m_img);
		}
	}

	//private Bitmap mShapeLayer;
	//private Canvas mShapeLayerCanvas;

	public void createPendantGlassBmp()
	{
		int size = m_img.m_w > m_img.m_h ? m_img.m_w : m_img.m_h;

		float whscale = (float)m_viewport.m_w / (float)m_viewport.m_h;
		float outW = size;
		float outH = outW / whscale;
		if(outH > size)
		{
			outH = size;
			outW = outH * whscale;
		}

		ShapeEx backup = (ShapeEx)m_origin.Clone();

		//设置输出位置
		m_origin.m_scaleX = outW / (float)m_viewport.m_w / m_viewport.m_scaleX;
		m_origin.m_scaleY = m_origin.m_scaleX;
		m_origin.m_x = (int)outW / 2f - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
		m_origin.m_y = (int)outH / 2f - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;

		//		if(mShapeLayer == null)
		//		{
		//			mShapeLayer = Bitmap.createBitmap((int)outW, (int)outH, Config.ARGB_8888);
		//			//mShapeLayer = m_img.m_bmp.copy(Config.ARGB_8888, true);
		//			mShapeLayerCanvas = new Canvas(mShapeLayer);
		//			mShapeLayerCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
		//		}

		Bitmap pendantGlassBmp = Bitmap.createBitmap((int)outW, (int)outH, Config.ARGB_8888);
		Canvas pendantGlasCanvas = new Canvas(pendantGlassBmp);
		pendantGlasCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));

		//		temp_paint.reset();
		//		temp_paint.setAntiAlias(true);
		//		temp_paint.setFilterBitmap(true);
		//		temp_paint.setXfermode(clear_mode);
		//		pendantGlasCanvas.drawPaint(temp_paint);
		myDrawOutPutItem(pendantGlasCanvas, m_pendantArr.get(0), m_pendantArr.get(0).m_bmp, src_over_mode);
		PorterDuffXfermode tempMode = src_in_mode;
		if(isReverseMode)
		{
			tempMode = src_out_mode;
		}
		else
		{
			tempMode = src_in_mode;
		}
		myDrawOutPutItem(pendantGlasCanvas, m_img, m_img.m_bmp, tempMode);
		myDrawOutPutItem(pendantGlasCanvas, m_img, m_glassBmp1, dst_over_mode);

		if(m_glassBmp2 != null)
		{
			m_glassBmp2.recycle();
			m_glassBmp2 = null;
		}
		m_glassBmp2 = filter.fakeGlassBeauty(pendantGlassBmp, m_color2);

		m_origin.Set(backup);

		pendantGlasCanvas.setBitmap(null);
	}

	private void myDrawShowItem(Canvas canvas, ShapeEx item, Bitmap bmp, PorterDuffXfermode mode)
	{
		if(bmp != null)
		{
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			temp_paint.setXfermode(mode);
			GetShowMatrix(temp_matrix, item);
			canvas.drawBitmap(bmp, temp_matrix, temp_paint);
		}
	}

	private void myDrawOutPutItem(Canvas canvas, ShapeEx item, Bitmap bmp, PorterDuffXfermode mode)
	{
		if(bmp != null)
		{
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			temp_paint.setXfermode(mode);
			GetOutputMatrix(temp_matrix, item, bmp);
			canvas.drawBitmap(bmp, temp_matrix, temp_paint);
		}
	}

	//@Override
	//protected void OddMove(MotionEvent event)
	//{
	//	if(m_isTouch && m_target != null && m_target != m_img && m_target != m_origin)
	//	{
	//		m_invalidatePendant = true;
	//	}
	//
	//	super.OddMove(event);
	//}
	//
	//@Override
	//protected void EvenMove(MotionEvent event)
	//{
	//	if(m_isTouch && m_target != null && m_target != m_img && m_target != m_origin)
	//	{
	//		m_invalidatePendant = true;
	//	}
	//
	//	super.EvenMove(event);
	//}
	//
	//@Override
	//public int AddPendant(Object info, Bitmap bmp)
	//{
	//	m_invalidatePendant = true;
	//
	//	return super.AddPendant(info, bmp);
	//}
	//
	//@Override
	//public int AddPendant2(ShapeEx item)
	//{
	//	m_invalidatePendant = true;
	//
	//	return super.AddPendant2(item);
	//}
	//
	//@Override
	//public Bitmap GetOutputBmp(int size)
	//{
	//	float whscale = (float)m_viewport.m_w / (float)m_viewport.m_h;
	//	float outW = size;
	//	float outH = outW / whscale;
	//	if(outH > size)
	//	{
	//		outH = size;
	//		outW = outH * whscale;
	//	}
	//	ShapeEx backup = (ShapeEx)m_origin.Clone();
	//
	//	//设置输出位置
	//	m_origin.m_scaleX = outW / (float)m_viewport.m_w / m_viewport.m_scaleX;
	//	m_origin.m_scaleY = m_origin.m_scaleX;
	//	m_origin.m_x = (int)outW / 2f - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
	//	m_origin.m_y = (int)outH / 2f - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;
	//
	//	Bitmap outBmp = Bitmap.createBitmap((int)outW, (int)outH, Config.ARGB_8888);
	//	Canvas canvas = new Canvas(outBmp);
	//	canvas.setDrawFilter(temp_filter);
	//
	//	Bitmap tempBmp;
	//	canvas.drawColor(m_bkColor);
	//
	//	float tempW;
	//	float tempH;
	//	if(m_img != null)
	//	{
	//		tempW = m_origin.m_scaleX * m_img.m_scaleX * m_img.m_w;
	//		tempH = m_origin.m_scaleY * m_img.m_scaleY * m_img.m_h;
	//		tempBmp = m_cb.MakeOutputImg(m_img.m_ex, (int)(tempW + 0.5), (int)(tempH + 0.5));
	//		GetOutputMatrix(temp_matrix, m_img, tempBmp);
	//		temp_paint.reset();
	//		temp_paint.setAntiAlias(true);
	//		temp_paint.setFilterBitmap(true);
	//		canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
	//		tempBmp.recycle();
	//		tempBmp = null;
	//	}
	//
	//	int len = m_pendantArr.size();
	//	ShapeEx temp;
	//	float[] src = new float[2];
	//	float[] dst = new float[2];
	//	for(int i = 0; i < len; i++)
	//	{
	//		temp = m_pendantArr.get(i);
	//		tempW = m_origin.m_scaleX * temp.m_scaleX * temp.m_w;
	//		tempH = m_origin.m_scaleY * temp.m_scaleY * temp.m_h;
	//		src[0] = temp.m_x + temp.m_centerX;
	//		src[1] = temp.m_y + temp.m_centerY;
	//		GetFaceLogicPos(dst, src);
	//		tempBmp = ((PendantViewEx.ControlCallback)m_cb).MakeOutputPendant(temp.m_ex, (int)(tempW + 0.5), (int)(tempH + 0.5), dst[0], dst[1], (temp.m_w * temp.m_scaleX) / (m_img.m_w * m_img.m_scaleX), temp.m_degree);
	//		GetOutputMatrix(temp_matrix, temp, tempBmp);
	//		temp_paint.reset();
	//		temp_paint.setAntiAlias(true);
	//		temp_paint.setFilterBitmap(true);
	//		canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
	//		tempBmp.recycle();
	//		tempBmp = null;
	//	}
	//
	//	m_origin.Set(backup);
	//
	//	return outBmp;
	//}

	public Bitmap GetOutputBmp()
	{
		int size = m_img.m_w > m_img.m_h ? m_img.m_w : m_img.m_h;

		float whscale = (float)m_viewport.m_w / (float)m_viewport.m_h;
		float outW = size;
		float outH = outW / whscale;
		if(outH > size)
		{
			outH = size;
			outW = outH * whscale;
		}
		ShapeEx backup = (ShapeEx)m_origin.Clone();

		//设置输出位置
		m_origin.m_scaleX = outW / (float)m_viewport.m_w / m_viewport.m_scaleX;
		m_origin.m_scaleY = m_origin.m_scaleX;
		m_origin.m_x = (int)outW / 2f - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
		m_origin.m_y = (int)outH / 2f - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;

		Bitmap outBmp = Bitmap.createBitmap((int)outW, (int)outH, Config.ARGB_8888);
		Canvas canvas = new Canvas(outBmp);
		canvas.setDrawFilter(temp_filter);

		Bitmap tempBmp;
		canvas.drawColor(m_bkColor);
		if(m_bk != null)
		{
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			tempBmp = m_bk.m_bmp;
			BitmapShader shader = new BitmapShader(tempBmp, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT);
			temp_paint.setShader(shader);
			canvas.drawRect(0, 0, outW, outH, temp_paint);
			tempBmp.recycle();
			tempBmp = null;
		}

		if(m_img != null)
		{
			//tempBmp = m_img.m_bmp;
			//GetOutputMatrix(temp_matrix, m_img, tempBmp);
			//temp_paint.reset();
			//temp_paint.setAntiAlias(true);
			//temp_paint.setFilterBitmap(true);
			//canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
			//tempBmp.recycle();
			//tempBmp = null;
			//毛玻璃保存用
			int len = m_pendantArr.size();
			if(len > 0)
			{
				//				for(int i = 0; i < len; i++)
				//				{
				//					myDrawOutPutItem(canvas, m_pendantArr.get(i), m_pendantArr.get(i).m_bmp, src_over_mode);
				//				}
				//				PorterDuffXfermode tempMode = src_out_mode;
				//				if(mType == TYPE_PENDANT)
				//				{
				//					if(isReverseMode)
				//					{
				//						tempMode = src_in_mode;
				//					}
				//					else
				//					{
				//						tempMode = src_out_mode;
				//					}
				//				}
				//				else if(mType == TYPE_SHAPE)
				//				{
				//					if(isReverseMode)
				//					{
				//						tempMode = src_out_mode;
				//					}
				//					else
				//					{
				//						tempMode = src_in_mode;
				//					}
				//				}
				//				myDrawOutPutItem(canvas, m_img, m_img.m_bmp, tempMode);
				//				for(int i = 0; i < len; i++)
				//				{
				//					if(m_pendantArr.get(i).m_ex != null && m_pendantArr.get(i).m_ex instanceof Bitmap)
				//					{
				//						myDrawOutPutItem(canvas, m_pendantArr.get(i), (Bitmap)m_pendantArr.get(i).m_ex, src_over_mode);
				//					}
				//				}
				//
				//				if(m_img.m_ex != null && m_img.m_ex instanceof Bitmap)
				//				{
				//					myDrawOutPutItem(canvas, m_img, (Bitmap)m_img.m_ex, dst_over_mode);
				//				}

				if(((BlurShapeEx)(m_pendantArr.get(0))).m_type == TYPE_PENDANT)
				{
					ShapeEx temp = m_pendantArr.get(0);
					myDrawShowItem(canvas, m_img, m_img.m_bmp, src_out_mode);
					myDrawShowItem(canvas, temp, temp.m_bmp, dst_out_mode);
					myDrawShowItem(canvas, m_img, m_glassBmp2, dst_over_mode);
					if(temp.m_ex != null && temp.m_ex instanceof Bitmap)
					{
						myDrawOutPutItem(canvas, temp, (Bitmap)temp.m_ex, src_over_mode);
					}
				}
				else
				{
					for(int i = 0; i < len; i++)
					{
						ShapeEx temp = m_pendantArr.get(i);
						if(((BlurShapeEx)(temp)).m_type == TYPE_SHAPE)
						{
							myDrawOutPutItem(canvas, temp, temp.m_bmp, src_over_mode);
							PorterDuffXfermode tempMode = src_in_mode;
							if(isReverseMode)
							{
								tempMode = src_out_mode;
							}
							else
							{
								tempMode = src_in_mode;
							}
							myDrawOutPutItem(canvas, m_img, m_img.m_bmp, tempMode);
							myDrawOutPutItem(canvas, m_img, m_glassBmp1, dst_over_mode);
						}
						else
						{
							myDrawOutPutItem(canvas, temp, temp.m_bmp, dst_out_mode);
							myDrawOutPutItem(canvas, m_img, m_glassBmp2, dst_over_mode);
							if(temp.m_ex != null && temp.m_ex instanceof Bitmap)
							{
								myDrawOutPutItem(canvas, temp, (Bitmap)m_pendantArr.get(i).m_ex, src_over_mode);
							}
						}
					}
				}
			}
			else
			{
				myDrawOutPutItem(canvas, m_img, m_img.m_bmp, src_out_mode);
			}
		}

		//if(m_frame != null)
		//{
		//	tempBmp = m_frame.m_bmp;
		//	GetOutputMatrix(temp_matrix, m_frame, tempBmp);
		//	temp_paint.reset();
		//	temp_paint.setAntiAlias(true);
		//	temp_paint.setFilterBitmap(true);
		//	canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
		//	tempBmp.recycle();
		//	tempBmp = null;
		//}
		//
		//int len = m_pendantArr.size();
		//ShapeEx temp;
		//for(int i = 0; i < len; i++)
		//{
		//	temp = m_pendantArr.get(i);
		//	tempBmp = temp.m_bmp;
		//	GetOutputMatrix(temp_matrix, temp, tempBmp);
		//	temp_paint.reset();
		//	temp_paint.setAntiAlias(true);
		//	temp_paint.setFilterBitmap(true);
		//	canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
		//	tempBmp.recycle();
		//	tempBmp = null;
		//}

		m_origin.Set(backup);
		canvas.setBitmap(null);

		return outBmp;
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

	//public static interface ControlCallback extends CoreViewV3.ControlCallback
	//{
	//	public void RefixPendant(ShapeEx item, float scx, float scy, float sw, float degree);
	//
	//	public Bitmap MakeOutputPendant(Object info, int outW, int outH, float scx, float scy, float sw, float degree);
	//}
	public interface MyFilterPendantViewControlCallback extends ControlCallback
	{
		void fingerDown();

		void deletePendantType(int type);
	}

	@Override
	public void ReleaseMem()
	{
		ClearViewBuffer();

		if(m_img != null)
		{
			if(m_img.m_bmp != null)
			{
				m_img.m_bmp.recycle();
				m_img.m_bmp = null;
			}
			if(m_img.m_ex != null && m_img.m_ex instanceof Bitmap)
			{
				((Bitmap)m_img.m_ex).recycle();
				m_img.m_ex = null;
			}
		}

		if(m_frame != null && m_frame.m_bmp != null && !m_frame.m_bmp.isRecycled())
		{
			m_frame.m_bmp.recycle();
			m_frame.m_bmp = null;
		}

		for (ShapeEx temp : m_pendantArr) {
			if(temp.m_bmp != null && !temp.m_bmp.isRecycled())
			{
				temp.m_bmp.recycle();
				temp.m_bmp = null;
			}
			if(temp.m_ex != null && temp.m_ex instanceof Bitmap)
			{
				((Bitmap)temp.m_ex).recycle();
				temp.m_ex = null;
			}
		}

		if(m_glassBmp1 != null && !m_glassBmp1.isRecycled())
		{
			m_glassBmp1.recycle();
			m_glassBmp1 = null;
		}
		if(m_glassBmp2 != null && !m_glassBmp2.isRecycled())
		{
			m_glassBmp2.recycle();
			m_glassBmp2 = null;
		}
	}

	public void DelGlassItem(int type)
	{
		m_pendantCurSel = -1;
		int len = m_pendantArr.size();
		for(int i = 0; i < len; i++)
		{
			if(((BlurShapeEx)m_pendantArr.get(i)).m_type == type)
			{
				m_pendantArr.remove(i);
				i--;
				len--;
			}
		}
	}

	public static class BlurShapeEx extends ShapeEx
	{
		public int m_type = TYPE_PENDANT;
		public boolean m_freeScale = false;
	}
}
