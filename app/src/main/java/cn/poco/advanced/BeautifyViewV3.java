package cn.poco.advanced;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;

import cn.poco.display.CoreViewV3;
import cn.poco.graphics.Shape.Flip;
import cn.poco.graphics.ShapeEx;
import cn.poco.tianutils.ImageUtils;

public class BeautifyViewV3 extends CoreViewV3
{
	public int def_delete_res;
	public int def_flip_res;
	protected ShapeEx m_deleteBtn;
	protected ShapeEx m_flipBtn;

	public BeautifyViewV3(Context context, int frW, int frH)
	{
		super(context, frW, frH);
	}

	@Override
	public void InitData(CoreViewV3.ControlCallback cb)
	{
		super.InitData(cb);
		Bitmap bmp;
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

		if(def_flip_res != 0)
		{
			m_flipBtn = new ShapeEx();
			bmp = BitmapFactory.decodeResource(getResources(), def_flip_res);
			m_flipBtn.m_bmp = bmp;
			m_flipBtn.m_w = bmp.getWidth();
			m_flipBtn.m_h = bmp.getHeight();
			m_flipBtn.m_centerX = (float)m_flipBtn.m_w / 2f;
			m_flipBtn.m_centerY = (float)m_flipBtn.m_h / 2f;
		}
	}

	@Override
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
					else if((m_nzoomBtn == null || !m_hasZoomBtn) && m_flipBtn == null && dst[2] > a && dst[2] < c && dst[3] > b && dst[3] < d)
					{
						temp_point_src[0] = dst[2];
						temp_point_src[1] = dst[3];
					}
					else
					{
						float d1 = 0;
						if(m_deleteBtn != null)
						{
							d1 = 999999f;
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
						else if(m_flipBtn != null)
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

			if(m_flipBtn != null)
			{
				temp_src[0] = temp_dst[0] - m_flipBtn.m_centerX;
				temp_src[1] = temp_dst[1] - m_flipBtn.m_centerY;
				temp_src[2] = temp_dst[2] + m_flipBtn.m_centerX;
				temp_src[3] = temp_dst[3] - m_flipBtn.m_centerY;
				temp_src[4] = temp_dst[4] + m_flipBtn.m_centerX;
				temp_src[5] = temp_dst[5] + m_flipBtn.m_centerY;
				temp_src[6] = temp_dst[6] - m_flipBtn.m_centerX;
				temp_src[7] = temp_dst[7] + m_flipBtn.m_centerY;

				temp_matrix.reset();
				temp_matrix.postRotate(item.m_degree, cxy[0], cxy[1]);
				temp_matrix.mapPoints(dst, temp_src);

				temp_point_src[0] = dst[2];
				temp_point_src[1] = dst[3];
				GetLogicPos(temp_point_dst, temp_point_src);
				m_flipBtn.m_x = temp_point_dst[0] - m_flipBtn.m_centerX;
				m_flipBtn.m_y = temp_point_dst[1] - m_flipBtn.m_centerY;

				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				GetShowMatrixNoScale(temp_matrix, m_flipBtn);
				canvas.drawBitmap(m_flipBtn.m_bmp, temp_matrix, temp_paint);
			}

			if(m_deleteBtn != null)
			{
				temp_src[0] = temp_dst[0] - m_deleteBtn.m_centerX;
				temp_src[1] = temp_dst[1] - m_deleteBtn.m_centerY;
				temp_src[2] = temp_dst[2] + m_deleteBtn.m_centerX;
				temp_src[3] = temp_dst[3] - m_deleteBtn.m_centerY;
				temp_src[4] = temp_dst[4] + m_deleteBtn.m_centerX;
				temp_src[5] = temp_dst[5] + m_deleteBtn.m_centerY;
				temp_src[6] = temp_dst[6] - m_deleteBtn.m_centerX;
				temp_src[7] = temp_dst[7] + m_deleteBtn.m_centerY;

				temp_matrix.reset();
				temp_matrix.postRotate(item.m_degree, cxy[0], cxy[1]);
				temp_matrix.mapPoints(dst, temp_src);

				temp_point_src[0] = dst[0];
				temp_point_src[1] = dst[1];
				GetLogicPos(temp_point_dst, temp_point_src);
				m_deleteBtn.m_x = temp_point_dst[0];
				m_deleteBtn.m_y = temp_point_dst[1];

				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				GetShowMatrixNoScale(temp_matrix, m_deleteBtn);
				canvas.drawBitmap(m_deleteBtn.m_bmp, temp_matrix, temp_paint);
			}
		}
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		m_isTouch = true;

		if(m_operateMode == MODE_FRAME)
		{
			if(m_frame == null)
			{
				m_target = null;
				return;
			}
		}
		if(m_operateMode == MODE_PENDANT)
		{
			if(m_pendantCurSel >= 0)
			{
				//判断是否选中删除按钮
				if(m_deleteBtn != null && IsClickBtn(m_deleteBtn, m_downX, m_downY))
				{
					m_target = m_deleteBtn;
					if(m_cb != null)
					{
						((ControlCallback)m_cb).DeletePendant(m_pendantArr.get(m_pendantCurSel));
					}
					return;
				}

				//判断是否选中翻转按钮
				if(m_flipBtn != null && IsClickBtn(m_flipBtn, m_downX, m_downY))
				{
					m_target = m_flipBtn;
					ShapeEx temp = m_pendantArr.get(m_pendantCurSel);
					if(temp.m_flip == Flip.NONE)
					{
						temp.m_flip = Flip.HORIZONTAL;
					}
					else if(temp.m_flip == Flip.HORIZONTAL)
					{
						temp.m_flip = Flip.NONE;
					}
					this.invalidate();
					return;
				}
			}
		}

		super.OddDown(event);

		if(m_operateMode == MODE_IMAGE)
		{
			((ControlCallback)m_cb).TouchImage(true);
		}
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		super.OddUp(event);

		if(m_operateMode == MODE_IMAGE)
		{
			((ControlCallback)m_cb).TouchImage(false);
		}
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		m_isTouch = true;
		m_isOddCtrl = false;

		if(m_operateMode == MODE_FRAME)
		{
			if(m_frame == null)
			{
				m_target = null;
				return;
			}
		}

		super.EvenDown(event);
	}

	@Override
	public void SetFrame(Object info, Bitmap bmp)
	{
		super.SetFrame(info, bmp);

		if(m_img != null && m_frame != null)
		{
			m_img.m_x = m_origin.m_centerX - m_img.m_centerX;
			m_img.m_y = m_origin.m_centerY - m_img.m_centerY;
			{
				float scale1 = m_viewport.m_w * m_viewport.m_scaleX / m_img.m_w;
				float scale2 = m_viewport.m_h * m_viewport.m_scaleY / m_img.m_h;
				m_img.m_scaleX = (scale1 > scale2) ? scale1 : scale2;
				m_img.m_scaleY = m_img.m_scaleX;
			}
			m_img.m_degree = 0;
		}
	}

	/**
	 * 不重置位置
	 */
	public void SetFrame3(Object info, Bitmap bmp)
	{
		super.SetFrame(info, bmp);
	}

	@Override
	public void SetFrame2(ShapeEx item)
	{
		super.SetFrame2(item);

		if(m_img != null)
		{
			//重置图片位置
			if(item == null)
			{
				m_img.m_x = m_viewport.m_x;
				m_img.m_y = m_viewport.m_y;
				m_img.m_scaleX = m_img.DEF_SCALE;
				m_img.m_scaleY = m_img.DEF_SCALE;
				m_img.m_degree = 0;
			}
			else
			{
				m_img.m_x = m_origin.m_centerX - m_img.m_centerX;
				m_img.m_y = m_origin.m_centerY - m_img.m_centerY;
				{
					float scale1 = m_viewport.m_w * m_viewport.m_scaleX / m_img.m_w;
					float scale2 = m_viewport.m_h * m_viewport.m_scaleY / m_img.m_h;
					m_img.m_scaleX = (scale1 > scale2) ? scale1 : scale2;
					m_img.m_scaleY = m_img.m_scaleX;
				}
				m_img.m_degree = 0;
			}
		}
	}

	public static interface ControlCallback extends CoreViewV3.ControlCallback
	{
		public void TouchImage(boolean isTouch);

		public void DeletePendant(ShapeEx pendant);
	}

	@Override
	public Bitmap GetOutputBmp(int size)
	{
		return super.GetOutputBmp2(size);
		/*float whscale = (float)m_viewport.m_w / (float)m_viewport.m_h;
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

		float tempW;
		float tempH;
		Bitmap imgBmp = null;
		if(m_img != null)
		{
			tempW = m_origin.m_scaleX * m_img.m_scaleX * m_img.m_w;
			tempH = m_origin.m_scaleY * m_img.m_scaleY * m_img.m_h;
			imgBmp = m_cb.MakeOutputImg(m_img.m_ex, (int)(tempW + 0.5), (int)(tempH + 0.5));
			if(imgBmp != null && imgBmp.getWidth() > 0 && imgBmp.getHeight() > 0)
			{
				if(Math.max(tempW, tempH) > Math.max(imgBmp.getWidth(), imgBmp.getHeight()))
				{
					//修正(图片小于输出size)
					m_origin.m_scaleX = (float)imgBmp.getWidth() / (m_img.m_scaleX * m_img.m_w);
					m_origin.m_scaleY = m_origin.m_scaleX;
					outW = m_origin.m_scaleX * (float)m_viewport.m_w * m_viewport.m_scaleX;
					outH = outW / whscale;
					m_origin.m_x = (int)outW / 2f - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
					m_origin.m_y = (int)outH / 2f - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;
				}
			}
			else
			{
				imgBmp = null;
			}
		}

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
			tempBmp = m_cb.MakeOutputBK(m_bk.m_ex, (int)outW, (int)outH);
			if(tempBmp != null)
			{
				BitmapShader shader = new BitmapShader(tempBmp, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT);
				temp_paint.setShader(shader);
				canvas.drawRect(0, 0, outW, outH, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		if(m_img != null && imgBmp != null)
		{
			tempBmp = imgBmp;
			imgBmp = null;
			if(tempBmp != null)
			{
				GetOutputMatrix(temp_matrix, m_img, tempBmp);
				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		if(m_frame != null)
		{
			tempW = m_origin.m_scaleX * m_frame.m_scaleX * m_frame.m_w;
			tempH = m_origin.m_scaleY * m_frame.m_scaleY * m_frame.m_h;
			tempBmp = m_cb.MakeOutputFrame(m_frame.m_ex, (int)(tempW + 0.5), (int)(tempH + 0.5));
			if(tempBmp != null)
			{
				GetOutputMatrix(temp_matrix, m_frame, tempBmp);
				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		int len = m_pendantArr.size();
		ShapeEx temp;
		for(int i = 0; i < len; i++)
		{
			temp = m_pendantArr.get(i);
			tempW = m_origin.m_scaleX * temp.m_scaleX * temp.m_w;
			tempH = m_origin.m_scaleY * temp.m_scaleY * temp.m_h;
			tempBmp = m_cb.MakeOutputPendant(temp.m_ex, (int)(tempW + 0.5), (int)(tempH + 0.5));
			if(tempBmp != null)
			{
				GetOutputMatrix(temp_matrix, temp, tempBmp);
				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		m_origin.Set(backup);

		return outBmp;*/
	}

	/*public Bitmap GetOutputBmp()
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
			if(tempBmp != null)
			{
				BitmapShader shader = new BitmapShader(tempBmp, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT);
				temp_paint.setShader(shader);
				canvas.drawRect(0, 0, outW, outH, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		if(m_img != null)
		{
			tempBmp = m_img.m_bmp;
			if(tempBmp != null)
			{
				GetOutputMatrix(temp_matrix, m_img, tempBmp);
				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		if(m_frame != null)
		{
			tempBmp = m_frame.m_bmp;
			if(tempBmp != null)
			{
				GetOutputMatrix(temp_matrix, m_frame, tempBmp);
				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		int len = m_pendantArr.size();
		ShapeEx temp;
		for(int i = 0; i < len; i++)
		{
			temp = m_pendantArr.get(i);
			tempBmp = temp.m_bmp;
			if(tempBmp != null)
			{
				GetOutputMatrix(temp_matrix, temp, tempBmp);
				temp_paint.reset();
				temp_paint.setAntiAlias(true);
				temp_paint.setFilterBitmap(true);
				canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		m_origin.Set(backup);

		return outBmp;
	}*/

	@Override
	public int GetPendantMaxNum()
	{
		long mem = Runtime.getRuntime().maxMemory() / 1048576;
		int max;
		if(mem >= 96)
		{
			max = 32;
		}
		else if(mem >= 64)
		{
			max = 24;
		}
		else if(mem >= 32)
		{
			max = 12;
		}
		else if(mem >= 24)
		{
			max = 8;
		}
		else
		{
			max = 6;
		}

		return max;
	}
}
