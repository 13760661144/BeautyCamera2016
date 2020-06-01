package cn.poco.view.material;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;

import java.util.List;

import cn.poco.image.filter;
import cn.poco.resource.GlassRes;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ImageUtils;
import cn.poco.view.RelativeView;
import my.beautyCamera.R;

/**
 * 毛玻璃
 * Created by admin on 2017/1/20.
 */

public class FilterPendantViewEx extends RelativeView
{
	public static final int TYPE_SHAPE = 0;
	public static final int TYPE_PENDANT = 1;

	public float def_pendant_max_scale = 1.5f; //pendant_size / view_size 最大比例
	public float def_pendant_min_scale = 0.05f; //pendant_size / view_size 最小比例

	// 删除按钮
	protected Shape def_delete_res; // 删除 --> 左上角
	protected boolean m_clickDelBtn = false; // 判断是否点击到delete按钮
	protected boolean isDrawDeleteBtn = true;

	// 非等比例按钮
	protected Shape[] m_nZoom4Btn;
	protected int m_zoomBtnIndex = -1; // 判断是否触摸4个非等比例zoom按钮
	protected boolean isDrawZoomBtn = true;
	private float mMNZGamma, mMNZOffset;// 初始化4个非等比例zoom按钮

	protected int m_pendantCurSel = -1; //装饰当前选中index
	protected boolean isDrawRect = true;
	private boolean isReverseMode = false;

	public Bitmap m_glassBmp1;// 第一层毛玻璃
	public Bitmap m_glassBmp2;// 第二层毛玻璃
	public int m_color1;
	public int m_color2;

	private PorterDuffXfermode clear_mode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	private PorterDuffXfermode src_over_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
	private PorterDuffXfermode dst_over_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
	private PorterDuffXfermode dst_in_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
	private PorterDuffXfermode dst_out_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
	private PorterDuffXfermode src_in_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
	private PorterDuffXfermode src_out_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

	private ControlCallback mCB;

	private boolean isCancelGPU = false;

	public FilterPendantViewEx(Context context)
	{
		super(context);
		InitData();
	}

	@Override
	protected void InitData()
	{
		super.InitData();

		def_img_max_scale = 3f;
		def_img_min_scale = 0.6f;

		def_delete_res = new Shape();
		def_delete_res.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_pendant_delete);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_pendant_nzoom4);

		m_nZoom4Btn = new Shape[4];

		m_nZoom4Btn[0] = new Shape();
		m_nZoom4Btn[0].m_bmp = bmp;

		m_nZoom4Btn[1] = new Shape();
		m_nZoom4Btn[1].m_bmp = bmp;

		m_nZoom4Btn[2] = new Shape();
		m_nZoom4Btn[2].m_bmp = bmp;

		m_nZoom4Btn[3] = new Shape();
		m_nZoom4Btn[3].m_bmp = bmp;
	}

	public void setControlCallback(ControlCallback callback)
	{
		mCB = callback;
	}

	@Override
	protected float getScaleByH(Shape target)
	{
		float[] curPos = getImgLogicPos(target);
		if(curPos != null)
		{
			float curImgH = ImageUtils.Spacing(curPos[0] - curPos[6], curPos[1] - curPos[7]);
			if(target instanceof BlurShapeEx)
			{
				updateItemDefScale((BlurShapeEx)target);// 更新当前画布长宽下，素材原始缩放比例
				float orgImgH = ((BlurShapeEx)target).m_h * ((BlurShapeEx)target).m_scaleY;
				return curImgH / orgImgH;
			}
		}
		return -1;
	}

	@Override
	protected float getScaleByW(Shape target)
	{
		float[] curPos = getImgLogicPos(target);
		if(curPos != null)
		{
			float curImgW = ImageUtils.Spacing(curPos[0] - curPos[2], curPos[1] - curPos[3]);
			if(target instanceof BlurShapeEx)
			{
				updateItemDefScale((BlurShapeEx)target);// 更新当前画布长宽下，素材原始缩放比例
				float orgImgW = ((BlurShapeEx)target).m_w * ((BlurShapeEx)target).m_scaleX;
				return curImgW / orgImgW;
			}
		}
		return -1;
	}

	@Override
	protected void updateContent(int width, int height)
	{
		super.updateContent(width, height);
		UpdateButtons();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// 背景色
		canvas.drawColor(Color.TRANSPARENT);

		// 限制图片显示区域
		canvas.clipRect(canvas_l,canvas_t,canvas_r,canvas_b);

		DrawGlassImage(canvas);

		//画选中框和按钮
		if(m_pendantCurSel >= 0 && m_pendantCurSel < mPendantArr.size())
		{
			Shape temp = mPendantArr.get(m_pendantCurSel);
			if(temp == mTarget)
			{
				//画选中框
				DrawRect(canvas, temp);

				DrawDeleteBtn(canvas, def_delete_res);

				if(temp != null && temp instanceof BlurShapeEx && ((BlurShapeEx)temp).m_freeScale)
				{
					//画4边自由拉伸的按钮
					DrawZoomBtn(canvas);
				}
			}
		}
	}

	public void setReverseMode(boolean isReverse)
	{
		isReverseMode = isReverse;
		if(mPendantArr != null && mPendantArr.size() > 1)
		{
			createPendantGlassBmp();
		}
		invalidate();
	}

	public void SetSelPendant(int index)
	{
		if(index >= 0 && index < mPendantArr.size())
		{
			m_pendantCurSel = index;
		}
		else
		{
			m_pendantCurSel = -1;
		}
		invalidate();
	}

	private void DrawGlassImage(Canvas canvas)
	{
		int len = mPendantArr.size();
		if(len > 0)
		{
			if(((BlurShapeEx)(mPendantArr.get(0))).m_type == TYPE_PENDANT)
			{
				myDrawShowItem(canvas, img, img.m_bmp, new PorterDuffXfermode(PorterDuff.Mode.SRC));
				myDrawShowItem(canvas, mPendantArr.get(0), mPendantArr.get(0).m_bmp, dst_out_mode);
				myDrawShowItem(canvas, img, m_glassBmp2, dst_over_mode);
				if(mPendantArr.get(0).m_ex != null && mPendantArr.get(0).m_ex instanceof Bitmap)
				{
					myDrawShowItem(canvas, mPendantArr.get(0), (Bitmap)mPendantArr.get(0).m_ex, src_over_mode);
				}
			}
			else
			{
				for(int i = 0; i < len; i++)
				{
					if(((BlurShapeEx)(mPendantArr.get(i))).m_type == TYPE_SHAPE)
					{
						myDrawShowItem(canvas, mPendantArr.get(i), mPendantArr.get(i).m_bmp, new PorterDuffXfermode(PorterDuff.Mode.SRC));
						PorterDuffXfermode tempMode = src_in_mode;
						if(isReverseMode)
						{
							tempMode = src_out_mode;
						}
						else
						{
							tempMode = src_in_mode;
						}

						myDrawShowItem(canvas, img, img.m_bmp, tempMode);
						myDrawShowItem(canvas, img, m_glassBmp1, dst_over_mode);
					}
					else
					{
						myDrawShowItem(canvas, mPendantArr.get(i), mPendantArr.get(i).m_bmp, dst_out_mode);
						myDrawShowItem(canvas, null, m_glassBmp2, dst_over_mode);

						if(mPendantArr.get(i).m_ex != null && mPendantArr.get(i).m_ex instanceof Bitmap)
						{
							myDrawShowItem(canvas, mPendantArr.get(i), (Bitmap)mPendantArr.get(i).m_ex, src_over_mode);
						}
					}
				}
			}
		}
		else
		{
			DrawImage(canvas, img);// 只画图片
		}
	}

	protected void DrawRect(Canvas canvas, Shape item)
	{
		if(isDrawRect && item != null)
		{
			canvas.save();
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setStrokeWidth(1);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.WHITE);
			canvas.translate(canvas_l, canvas_t);
			canvas.concat(global.m_matrix);
			canvas.concat(item.m_matrix);
			canvas.drawRect(0, 0, item.m_bmp.getWidth(), item.m_bmp.getHeight(), mPaint);
			canvas.restore();
		}
	}

	protected void DrawDeleteBtn(Canvas canvas, Shape item)
	{
		if(isDrawDeleteBtn && item != null)
		{
			canvas.save();
			canvas.translate(canvas_l, canvas_t);
			canvas.concat(global.m_matrix);
			canvas.drawBitmap(item.m_bmp, item.m_matrix, mPaint);
			canvas.restore();
		}
	}

	protected void DrawZoomBtn(Canvas canvas)
	{
		if(isDrawZoomBtn && m_nZoom4Btn != null)
		{
			for(Shape btn : m_nZoom4Btn)
			{
				canvas.save();
				canvas.translate(canvas_l, canvas_t);
				canvas.concat(global.m_matrix);
				canvas.drawBitmap(btn.m_bmp, btn.m_matrix, mPaint);
				canvas.restore();
			}
		}
	}

	protected void DrawImage(Canvas canvas, Shape item)
	{
		if(item != null && item.m_bmp != null)
		{
			canvas.save();
			canvas.translate(canvas_l,canvas_t);
			canvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			canvas.drawBitmap(img.m_bmp, img.m_matrix, mPaint);
			canvas.restore();
		}
	}

	private void myDrawShowItem(Canvas canvas, Shape item, Bitmap bmp, PorterDuffXfermode mode)
	{
		if(bmp != null)
		{
			canvas.save();
			canvas.translate(canvas_l,canvas_t);
			canvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setXfermode(mode);
			if(item == null)
			{
				canvas.drawBitmap(bmp, new Matrix(), mPaint);
			}
			else
			{
				canvas.drawBitmap(bmp, item.m_matrix, mPaint);
			}
			canvas.restore();
		}
	}

	private void myDrawOutPutItem(Canvas canvas, Shape item, Bitmap bmp, PorterDuffXfermode mode)
	{
		if(bmp != null)
		{
			canvas.save();
			canvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setXfermode(mode);
			if(item != null)
			{
				canvas.drawBitmap(bmp, item.m_matrix, mPaint);
			}
			else
			{
				canvas.drawBitmap(bmp, new Matrix(), mPaint);
			}
			canvas.restore();
		}
	}

	/**
	 * 添加毛玻璃素材
	 * @param item
	 * @return
	 */
	private int myAddPendant(BlurShapeEx item)
	{
		if (!isCancelGPU)
		{
			CommonUtils.CancelViewGPU(this);
			isCancelGPU = true;
		}

		syncScaling();

		int out = -1;

		if(item == null)
		{
			return out;
		}

		isDrawRect = false;
		isDrawDeleteBtn = false;
		isDrawZoomBtn = false;

		//删除相同类型
		DelGlassItem(item.m_type);
		//添加
		switch(item.m_type)
		{
			case TYPE_SHAPE:
				mPendantArr.add(0, item);
				out = 0;
				break;
			case TYPE_PENDANT:
				mPendantArr.add(item);
				out = mPendantArr.size() - 1;
				break;
			default:
				break;
		}
		//如果有2层则构造第二层毛玻璃
		if(mPendantArr.size() > 1 || m_glassBmp2 == null)
		{
			createPendantGlassBmp();
		}

		mTarget = item;
		m_pendantCurSel = out;

		updateDeleteBtn(mTarget);

		if(item.m_freeScale)
		{
			updateZoom4Btn(mTarget);
			isDrawZoomBtn = true;
		}

		isDrawRect = true;
		isDrawDeleteBtn = true;

		this.invalidate();
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

	/**
	 * 毛玻璃素材资源对象转换
	 * @param info
	 * @return
	 */
	private BlurShapeEx changeToShapeEx(GlassRes info)
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

		if(item.m_bmp == null)
		{
			return null;
		}
		item.m_freeScale = info.m_canFreedomZoom;

		item.m_w = item.m_bmp.getWidth();
		item.m_h = item.m_bmp.getHeight();
		item.m_centerX = (float)item.m_w / 2f;
		item.m_centerY = (float)item.m_h / 2f;
		item.m_ex = cn.poco.imagecore.Utils.DecodeImage(getContext(), info.m_img, 0, -1, -1, -1);
		item.m_info = info;

		updateItemDefScale(item);

		float[] imgPos = getImgLogicPos(img);
		if(info.horizontal_pos == GlassRes.POS_START)
		{
			float realW = canvas_r - canvas_l;
			float imgLeftX = imgPos[0];
			//相对图片的位置移动
			item.m_x = imgLeftX + info.horizontal_value / 100f * realW;
			//相对自身的位置移动
			item.m_x += info.self_offset_x / 100f * item.m_w * item.m_scaleX;
		}
		else if(info.horizontal_pos == GlassRes.POS_END)
		{
			float realW = canvas_r - canvas_l;
			float imgRightX = imgPos[4];
			//相对图片的位置移动
			item.m_x = imgRightX - item.m_w * item.m_scaleX - info.horizontal_value / 100f * realW;
			//相对自身的位置移动
			item.m_x -= info.self_offset_x / 100f * item.m_w * item.m_scaleX;
		}
		else
		{
			item.m_x = (canvas_r - canvas_l - item.m_w * item.m_scaleX)/2f;
		}

		if(info.vertical_pos == GlassRes.POS_START)
		{
			float realH = canvas_b - canvas_t;
			float imgTopY = imgPos[1];
			//相对图片的位置移动
			item.m_y = imgTopY + info.vertical_value / 100f * realH;
			//相对自身的位置移动
			item.m_y += info.self_offset_y / 100f * item.m_h * item.m_scaleY;
		}
		else if(info.vertical_pos == GlassRes.POS_END)
		{
			float realH = canvas_b - canvas_t;
			float imgBottomY = imgPos[5];
			//相对图片的位置移动
			item.m_y = imgBottomY - item.m_h * item.m_scaleY - info.vertical_value / 100f * realH;
			//相对自身的位置移动
			item.m_y -= info.self_offset_y / 100f * item.m_h * item.m_scaleY;
		}
		else
		{
			item.m_y = (canvas_b - canvas_t - item.m_h * item.m_scaleY)/2f;
		}

		item.m_matrix.postScale(item.m_scaleX,item.m_scaleY);
		item.m_matrix.postTranslate(item.m_x, item.m_y);

		float viewW = getWidth();
		float viewH = getHeight();
		item.DEF_SCALE = item.m_scaleX > item.m_scaleY ? item.m_scaleX : item.m_scaleY;
		{
			float scale1 = viewW * def_pendant_max_scale / (float)item.m_w;
			float scale2 = viewH * def_pendant_max_scale / (float)item.m_h;
			item.MAX_SCALE = (scale1 > scale2) ? scale2 : scale1;
			item.MAX_SCALE = item.MAX_SCALE < item.DEF_SCALE ? item.DEF_SCALE * def_pendant_max_scale : item.MAX_SCALE;

			scale1 = viewW * def_pendant_min_scale / (float)item.m_w;
			scale2 = viewH * def_pendant_min_scale / (float)item.m_h;
			item.MIN_SCALE = (scale1 > scale2) ? scale2 : scale1;
		}

		return item;
	}

	/**
	 * 根据当前画布的size，更新素材默认的缩放比例
	 * @param item
	 */
	private void updateItemDefScale(BlurShapeEx item)
	{
		float scale;
		if(((GlassRes)item.m_info).h_fill_parent > 0 || ((GlassRes)item.m_info).v_fill_parent > 0)
		{
			if(((GlassRes)item.m_info).v_fill_parent > 0 && ((GlassRes)item.m_info).h_fill_parent > 0)
			{
				item.m_scaleY = 1f * (canvas_b - canvas_t) / item.m_h * ((GlassRes)item.m_info).v_fill_parent / 100f;
				item.m_scaleX = 1f * (canvas_r - canvas_l) / item.m_w * ((GlassRes)item.m_info).h_fill_parent / 100f;
			}
			else
			{
				if(((GlassRes)item.m_info).v_fill_parent > 0)
				{
					item.m_scaleY = 1f * (canvas_b - canvas_t) / item.m_h * ((GlassRes)item.m_info).v_fill_parent / 100f;
					item.m_scaleX = item.m_scaleY;
				}

				if(((GlassRes)item.m_info).h_fill_parent > 0)
				{
					item.m_scaleX = 1f * (canvas_r - canvas_l) / item.m_w * ((GlassRes)item.m_info).h_fill_parent / 100f;
					item.m_scaleY = item.m_scaleX;
				}
			}
		}
		else
		{
			if(((GlassRes)item.m_info).m_glassType == GlassRes.GLASS_TYPE_PENDANT)
			{
				//按1024适配
				if(img.m_bmp.getWidth() < img.m_bmp.getHeight())
				{
					scale = 1f * (canvas_r - canvas_l) / 1024;
				}
				else
				{
					scale = 1f * (canvas_b - canvas_t) / 1024;
				}
			}
			else
			{
				//按图片大小适配
				if(img.m_bmp.getWidth() == img.m_bmp.getHeight() && item.m_w > item.m_h)
				{
					scale = 1f * (canvas_r - canvas_l) / item.m_w;
				}
				else if(img.m_bmp.getWidth() < img.m_bmp.getHeight())
				{
					scale = 1f * (canvas_r - canvas_l) / item.m_w;
				}
				else
				{
					scale = 1f * (canvas_b - canvas_t) / item.m_h;
				}
			}

			item.m_scaleX = scale * ((GlassRes)item.m_info).m_scale / 100f;
			item.m_scaleY = item.m_scaleX;
		}
	}

	public void SetGlassColor(int shapeColor, int pendantColor)
	{
		if(m_glassBmp1 == null || m_color1 != shapeColor)
		{
			//生成第一层毛玻璃
			if(m_glassBmp1 != null && !m_glassBmp1.isRecycled())
			{
				m_glassBmp1.recycle();
				m_glassBmp1 = null;
			}
			m_glassBmp1 = filter.fakeGlassBeauty(img.m_bmp.copy(Bitmap.Config.ARGB_8888, true), shapeColor);
		}
		if(mPendantArr != null && mPendantArr.size() > 1 && (m_color2 != pendantColor || m_color1 != shapeColor))
		{
			m_color2 = pendantColor;
			createPendantGlassBmp();
		}
		else if(m_color2 != pendantColor)
		{
			m_color2 = pendantColor;
			if(m_glassBmp2 != null && !m_glassBmp2.isRecycled())
			{
				m_glassBmp2.recycle();
				m_glassBmp2 = null;
			}
			m_glassBmp2 = filter.fakeGlassBeauty(img.m_bmp.copy(Bitmap.Config.ARGB_8888, true), pendantColor);
		}
		m_color1 = shapeColor;

		invalidate();
	}

	/**
	 * 创建第二层毛玻璃
	 */
	private void createPendantGlassBmp()
	{
//		if(img != null && !img.m_bmp.isRecycled())
//		{
//			Bitmap temp = filter.fakeGlassBeauty(img.m_bmp.copy(Bitmap.Config.ARGB_8888, true), m_color2);
//			m_glassBmp2 = filter.fakeGlassBeauty(temp,m_color2);
//		}

		Bitmap pendantGlassBmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas pendantGlassCanvas = new Canvas(pendantGlassBmp);
		pendantGlassCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));

		myDrawOutPutItem(pendantGlassCanvas, mPendantArr.get(0), mPendantArr.get(0).m_bmp, src_over_mode);
		PorterDuffXfermode tempMode = src_in_mode;
		if(isReverseMode)
		{
			tempMode = src_out_mode;
		}
		else
		{
			tempMode = src_in_mode;
		}
		myDrawOutPutItem(pendantGlassCanvas, img, img.m_bmp, tempMode);
		myDrawOutPutItem(pendantGlassCanvas, img, m_glassBmp1, dst_over_mode);

		if(m_glassBmp2 != null && !m_glassBmp2.isRecycled())
		{
			m_glassBmp2.recycle();
			m_glassBmp2 = null;
		}
		m_glassBmp2 = filter.fakeGlassBeauty(pendantGlassBmp, m_color2);
	}

	/**
	 * 删除毛玻璃素材
	 * @param type
	 */
	public void DelGlassItem(int type)
	{
		m_pendantCurSel = -1;
		int len = mPendantArr.size();
		for(int i = 0; i < len; i++)
		{
			if(((BlurShapeEx)mPendantArr.get(i)).m_type == type)
			{
				mPendantArr.remove(i);
				i--;
				len--;
			}
		}
	}

	private Shape DelPendant()
	{
		Shape out = null;

		if(m_pendantCurSel >= 0 && m_pendantCurSel < mPendantArr.size())
		{
			out = mPendantArr.remove(m_pendantCurSel);

			int tempNextSel = mPendantArr.size() - 1;
			if(tempNextSel >= 0 && tempNextSel < mPendantArr.size())
			{
				mTarget = mPendantArr.get(tempNextSel);
			}
			else
			{
				mTarget = mInit;
			}

			m_pendantCurSel = tempNextSel;
			if(mCB != null)
			{
				mCB.selectPendant(tempNextSel);
			}
		}

		// 重新创建第二层毛玻璃
		if(mPendantArr.size() == 1)
		{
			if(m_glassBmp2 != null && !m_glassBmp2.isRecycled())
			{
				m_glassBmp2.recycle();
				m_glassBmp2 = null;
			}
			m_glassBmp2 = filter.fakeGlassBeauty(img.m_bmp.copy(Bitmap.Config.ARGB_8888, true), m_color2);
		}

		return out;
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		mTarget = getShowMatrix(mDownX, mDownY);

		if(m_clickDelBtn)
		{
			if(mCB != null && mTarget instanceof BlurShapeEx)
			{
				mCB.deletePendantType(((BlurShapeEx)DelPendant()).m_type, m_pendantCurSel);
			}
		}
		else if(m_zoomBtnIndex == -1)
		{
			if(mCB != null)
			{
				mCB.selectPendant(m_pendantCurSel);
			}
		}
		else
		{
			Init_MNZ4_Data(mTarget,mDownX,mDownY);
		}

		UpdateButtons();

		Init_M_Data(mTarget, mDownX, mDownY);
		invalidate();

		if (mCB != null)
		{
			mCB.fingerDown();
		}
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		if(m_zoomBtnIndex == -1)
		{
			super.OddMove(event);
		}
		else
		{
			//4点非等比例拉伸按钮
			Run_MNZ4(mTarget,event.getX(),event.getY());
		}
		UpdateButtons();
		invalidate();
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		if(mTarget != mInit && mPendantArr != null && mPendantArr.size() > 1)
		{
			// 触摸4个zoom按钮，没有第二个手指触发evenDown事件的前提下
			createPendantGlassBmp();
			invalidate();
		}
		if(m_zoomBtnIndex == -1)
		{
			super.OddUp(event);
		}
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		if(m_zoomBtnIndex == -1) // 没有触摸非等比例缩放的按钮
		{
			int index = getSelectIndex(mPendantArr,(mDownX1 + mDownX2)/2f,(mDownY1 + mDownY2)/2f);

			if(index <0)
			{
				mTarget = mInit;
				m_pendantCurSel = -1;
			}
			else
			{
				m_pendantCurSel = index;
				mTarget = mPendantArr.get(index);
				UpdateButtons();
			}

			if(mCB != null)
			{
				mCB.selectPendant(m_pendantCurSel);
			}

			Init_MRZ_Data(mTarget, mDownX1, mDownY1, mDownX2, mDownY2);
		}
		else
		{
			mTarget = mInit;// cancel 后续事件
		}
		this.invalidate();
	}

	@Override
	protected void EvenMove(MotionEvent event)
	{
		super.EvenMove(event);
		UpdateButtons();
		this.invalidate();
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		if(mTarget == mInit && mPendantArr != null && mPendantArr.size() > 1)
		{
			createPendantGlassBmp();
		}
		super.EvenUp(event);
	}

	protected void UpdateButtons()
	{
		updateDeleteBtn(mTarget);
		if(mTarget instanceof BlurShapeEx && ((BlurShapeEx)mTarget).m_freeScale)
		{
			updateZoom4Btn(mTarget);
			if(!isDrawZoomBtn) isDrawZoomBtn = true;
		}

		if(!isDrawRect) isDrawRect = true;

		if(!isDrawDeleteBtn) isDrawDeleteBtn = true;
	}

	// 非等比例按钮缩放
	private void Init_MNZ4_Data(Shape item, float x, float y)
	{
		mOldMatrix.set(item.m_matrix);
		float[] imgPos = getImgShowPos(item);
		switch(m_zoomBtnIndex)
		{
			case 0:
				mMNZOffset = y - (imgPos[1] + imgPos[3])/2f;
				mMNZGamma = ImageUtils.Spacing(imgPos[0] - imgPos[6], imgPos[1] - imgPos[7]);
				break;
			case 2:
				mMNZOffset = y - (imgPos[5] + imgPos[7])/2f;
				mMNZGamma = ImageUtils.Spacing(imgPos[0] - imgPos[6], imgPos[1] - imgPos[7]);
				break;
			case 1:
				mMNZOffset = x - (imgPos[2] + imgPos[4])/2f;
				mMNZGamma = ImageUtils.Spacing(imgPos[0] - imgPos[2], imgPos[1] - imgPos[3]);
				break;
			case 3:
				mMNZOffset = x - (imgPos[0] + imgPos[6])/2f;
				mMNZGamma = ImageUtils.Spacing(imgPos[0] - imgPos[2], imgPos[1] - imgPos[3]);
		}
	}

	protected void Run_MNZ4(Shape item, float x, float y)
	{
		float temp = 1f,scale = 1f;

		if(item instanceof BlurShapeEx)
		{
			mTarget.m_matrix.set(mOldMatrix);
			Matrix record = new Matrix();
			record.set(item.m_matrix);

			// 计算down时图片的缩放比例
			float sw1 = getScaleByW(item);
			float sh1 = getScaleByH(item);

			float[] imgPos = getImgShowPos(item);

			float imgCenX = (imgPos[0] + imgPos[4])/2f;
			float imgCenY = (imgPos[1] + imgPos[5])/2f;

			switch(m_zoomBtnIndex)
			{
				case 0:
					y -= mMNZOffset;
					if(y > imgCenY)
					{
						y = imgCenY;
					}
					temp = (imgCenY - y)*2f / mMNZGamma;
					break;
				case 2:
					y -= mMNZOffset;
					if(y < imgCenY)
					{
						y = imgCenY;
					}
					temp = (y - imgCenY)*2f / mMNZGamma;
					break;
				case 1:
					x -= mMNZOffset;
					if(x < imgCenX)
					{
						x = imgCenX;
					}
					temp = (x - imgCenX)*2f / mMNZGamma;
					break;
				case 3:
					x -= mMNZOffset;
					if(x > imgCenX)
					{
						x = imgCenX;
					}
					temp = (imgCenX - x)*2f / mMNZGamma;
					break;
			}

			Run_MNZ4(item,temp);

			// 计算move后图片 width 缩放比例
			float sw2 = getScaleByW(item);
			// 计算move后图片 height 缩放比例
			float sh2 = getScaleByH(item);

			float def_img_min_scale = ((BlurShapeEx)item).MIN_SCALE;
			float def_img_max_scale = ((BlurShapeEx)item).MAX_SCALE;
			if(sw2 != -1 && sw1 != -1 && (m_zoomBtnIndex == 1 || m_zoomBtnIndex == 3))
			{
				// 限制图片 width 缩放比例
				if(sw2 <= def_img_min_scale)
				{
					sw2 = def_img_min_scale;
				}

				if(sw2 >= def_img_max_scale)
				{
					sw2 = def_img_max_scale;
				}
				scale = sw2 / sw1;
			}

			if(sh2 != -1 && sh1 != -1 && (m_zoomBtnIndex == 0 || m_zoomBtnIndex == 2))
			{
				// 限制图片 height 缩放比例
				if(sh2 <= def_img_min_scale)
				{
					sh2 = def_img_min_scale;
				}
				if(sh2 >= def_img_max_scale)
				{
					sh2 = def_img_max_scale;
				}
				scale = sh2 / sh1;
			}

			item.m_matrix.reset();
			item.m_matrix.set(record);
			Run_MNZ4(item,scale);
		}
	}

	private void Run_MNZ4(Shape target, float scale)
	{
		Matrix invert = new Matrix();
		Matrix temp = new Matrix();
		temp.set(target.m_matrix);
		target.m_matrix.invert(invert);
		// 图片原始位置
		mTarget.m_matrix.postConcat(invert);

		float[] imgPos = getImgLogicPos(target);
		Matrix[] matrices = new Matrix[]{global.m_matrix};
		inverseCount(imgPos, matrices);
		if(imgPos != null)
		{
			float lt_x = imgPos[0];
			float lt_y = imgPos[1];
			float rb_x = imgPos[4];
			float rb_y = imgPos[5];

			switch(m_zoomBtnIndex)
			{
				// y轴
				case 0:
				case 2:
					target.m_matrix.postScale(1, scale, (rb_x + lt_x) / 2f, (rb_y + lt_y) / 2f);
					break;
				// x轴
				case 1:
				case 3:
					target.m_matrix.postScale(scale, 1, (rb_x + lt_x) / 2f, (rb_y + lt_y) / 2f);
			}
			// 还原图片状态
			target.m_matrix.postConcat(temp);
		}
	}

	@Override
	protected void Run_Z(Shape item, float x1, float y1, float x2, float y2)
	{
		if(item instanceof BlurShapeEx)
		{
			float scale = 1f;
			Matrix record = new Matrix();
			record.set(item.m_matrix);
			// 计算down时图片的缩放比例
			float sw1 = getScaleByW(item);
			float sh1 = getScaleByH(item);

			float tempDist = ImageUtils.Spacing(x1 - x2, y1 - y2);
			if(tempDist > 10)
			{
				scale = tempDist / mDelta;
			}

			Run_Z(item,scale,scale);

			// 计算move后图片的缩放比例
			float sw2 = getScaleByW(item);
			float sh2 = getScaleByH(item);
			float newScaleX = 1f, newScaleY = 1f;

			if(sw2 != -1 && sw1 != -1)
			{
				// 限制图片缩放比例
				if(sw2 <= ((BlurShapeEx)item).MIN_SCALE)
				{
					sw2 = ((BlurShapeEx)item).MIN_SCALE;
				}

				if(sw2 >= ((BlurShapeEx)item).MAX_SCALE)
				{
					sw2 = ((BlurShapeEx)item).MAX_SCALE;
				}
				newScaleX = sw2 / sw1;
			}

			if(sh2 != -1 && sh1 != -1)
			if(sh2 != -1 && sh1 != -1)
			{
				// 限制图片缩放比例
				if(sh2 <= ((BlurShapeEx)item).MIN_SCALE)
				{
					sh2 = ((BlurShapeEx)item).MIN_SCALE;
				}

				if(sh2 >= ((BlurShapeEx)item).MAX_SCALE)
				{
					sh2 = ((BlurShapeEx)item).MAX_SCALE;
				}
				newScaleY = sh2 / sh1;
			}

			if(sw2 == ((BlurShapeEx)item).MIN_SCALE || sw2 == ((BlurShapeEx)item).MAX_SCALE)
			{
				newScaleY = newScaleX;
			}

			if(sh2 == ((BlurShapeEx)item).MIN_SCALE || sh2 == ((BlurShapeEx)item).MAX_SCALE)
			{
				newScaleX = newScaleY;
			}

			// 恢复缩放前的状态
			item.m_matrix.set(record);

			Run_Z(item,newScaleX,newScaleY);
		}
	}

	private void Run_Z(Shape target, float scaleX, float scaleY)
	{
		if(target == global) // 整体缩放
		{
			target.m_matrix.postScale(scaleX, scaleY, (mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
		}
		else
		{
			float[] src = new float[]{(mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f};
			float[] dst = new float[src.length];
			Matrix[] matrices = new Matrix[]{global.m_matrix};
			inverseCount(dst, src, matrices);
			target.m_matrix.postScale(scaleX, scaleY, dst[0],dst[1]);
		}
	}

	private int getSelectIndex(List<Shape> array, float x, float y)
	{
		float[] src = new float[]{x,y};
		float[] dst = new float[src.length];

		// 屏幕坐标转换为逻辑坐标
		getLogicPos(src);

		int count = array.size();
		int index = -1;
		for(int j=0;j<count;j++){
			Shape pendant = array.get(j);
			// 逆矩阵求点的坐标
			Matrix[] matrices = new Matrix[]{global.m_matrix,pendant.m_matrix};
			inverseCount(dst,src,matrices);

			if(0 <= dst[0] && dst[0] <= pendant.m_bmp.getWidth()){
				if(0 <= dst[1] && dst[1] <= pendant.m_bmp.getHeight()){
					index = j;
				}
			}
		}
		return index;
	}

	@Override
	protected Shape getShowMatrix(float... pts)
	{
		m_zoomBtnIndex = -1;
		m_clickDelBtn = false;
		Shape item = null;

		int count = pts.length;
		if(count %2 !=0) return mInit;

		float[] dst = new float[count];
		float[] src = new float[count];
		for(int i=0;i<count;i++) src[i] = pts[i];

		count = mPendantArr.size();
		// 屏幕坐标转换为逻辑坐标
		getLogicPos(src);

		if(m_pendantCurSel >= 0)
		{
			Shape temp = mPendantArr.get(m_pendantCurSel);
			if(temp != null && temp instanceof BlurShapeEx && ((BlurShapeEx)temp).m_freeScale)
			{
				// 判断zoom按钮
				int zoomBtnCount = m_nZoom4Btn.length;
				for(int i=0;i<zoomBtnCount;i++)
				{
					Shape btn = m_nZoom4Btn[i];
					Matrix[] matrices = new Matrix[]{global.m_matrix,btn.m_matrix};
					inverseCount(dst,src,matrices);
					if(0 <= dst[0] && dst[0] <= btn.m_bmp.getWidth()){
						if(0 <= dst[1] && dst[1] <= btn.m_bmp.getHeight()){
							m_zoomBtnIndex = i;
							return temp;
						}
					}
				}
			}

			Matrix[] matrices = new Matrix[]{global.m_matrix,def_delete_res.m_matrix};
			inverseCount(dst,src,matrices);
			// 判断delete按钮
			if(0 <= dst[0] && dst[0] <= def_delete_res.m_bmp.getWidth()){
				if(0 <= dst[1] && dst[1] <= def_delete_res.m_bmp.getHeight()){
					m_clickDelBtn = true;
					return mPendantArr.get(m_pendantCurSel);
				}
			}
		}

		for(int i=0;i<count;i++){
			Shape pendant = mPendantArr.get(i);
			// 逆矩阵求点的坐标
			Matrix[] matrices = new Matrix[]{global.m_matrix,pendant.m_matrix};
			inverseCount(dst,src,matrices);
			if(0 <= dst[0] && dst[0] <= pendant.m_bmp.getWidth()){
				if(0 <= dst[1] && dst[1] <= pendant.m_bmp.getHeight()){
					m_pendantCurSel = i;
					item = pendant;   // 保留层次比较靠上的矩阵
				}
			}
		}

		if(item == null)
		{
			m_pendantCurSel = -1;
			item = mInit;
		}

		return item;
	}

	/**
	 * 重置4个缩放按钮 -->> 顺时针
	 * @param item 毛玻璃素材
	 */
	private void updateZoom4Btn(Shape item)
	{
		if(item.m_bmp == null){
			return;
		}

		Shape[] zoom4Btn = m_nZoom4Btn;

		float[] imagePts = getImgLogicPos(item); // 逻辑坐标
		if(imagePts != null)
		{
			int count = zoom4Btn.length;
			for(int i=0; i <count; i++)
			{
				Shape btn = zoom4Btn[i];
				btn.m_matrix.reset();
				global.m_matrix.invert(btn.m_matrix);
				float[] btnPos = getImgLogicPos(btn);// 计算图标位置 -->> 逻辑坐标
				if(btnPos != null && btnPos.length == 8)
				{
					float btn_lt_x = btnPos[0];
					float btn_lt_y = btnPos[1];
					float btn_rb_x = btnPos[4];
					float btn_rb_y = btnPos[5];
					// res图标中心点
					float btn_cen_x = (btn_lt_x + btn_rb_x) / 2f;
					float btn_cen_y = (btn_lt_y + btn_rb_y) / 2f;

					float[] src = new float[4];
					src[0] = btn_cen_x;
					src[1] = btn_cen_y;
					Matrix[] matrices = new Matrix[]{global.m_matrix};
					switch(i)
					{
						case 0:
							src[2] = (imagePts[0] + imagePts[2])/2f;
							src[3] = (imagePts[1] + imagePts[3])/2f;
							break;
						case 1:
							src[2] = (imagePts[2] + imagePts[4])/2f;
							src[3] = (imagePts[3] + imagePts[5])/2f;
							break;
						case 2:
							src[2] = (imagePts[4] + imagePts[6])/2f;
							src[3] = (imagePts[5] + imagePts[7])/2f;
							break;
						case 3:
							src[2] = (imagePts[6] + imagePts[0])/2f;
							src[3] = (imagePts[7] + imagePts[1])/2f;
					}
					inverseCount(src, matrices);
					btn.m_matrix.postTranslate(src[2] - src[0], src[3] - src[1]);
				}
			}
		}
	}

	/**
	 * 重置delete按钮
	 * @param item 毛玻璃素材
	 */
	private void updateDeleteBtn(Shape item)
	{
		Shape res = def_delete_res;
		if(item.m_bmp == null){
			return;
		}
		float dis = res.m_bmp.getWidth()/4f;
		float[] imagePts = getImgLogicPos(item); // 逻辑坐标
		if(imagePts != null){
			float[] rectPos = getImgLogicRect(item);// 外切矩形的坐标
			float[] change = new float[rectPos.length];
			// 扩大外切矩形的范围
			change[0] = rectPos[0] - dis;
			change[1] = rectPos[1] - dis;
			change[2] = rectPos[2] + dis;
			change[3] = rectPos[3] + dis;

			// 求出扩大范围后与原来的宽高比例
			float wScale = (change[2] - change[0])/(rectPos[2] - rectPos[0]);
			float hScale = (change[3] - change[1])/(rectPos[3] - rectPos[1]);
			float scale = Math.min(wScale,hScale);
			// 用宽高比例将图片图片缩放，---> 在img外再计算一层隐藏矩形，用来放res图标
			Matrix matrix = new Matrix();
			matrix.set(item.m_matrix);

			float[] pts = new float[]{(imagePts[0] + imagePts[4])/2f,(imagePts[1]+imagePts[5])/2f};
			Matrix[] matrices = new Matrix[]{global.m_matrix};
			inverseCount(pts,matrices);

			item.m_matrix.postScale(scale,scale,pts[0],pts[1]);
			float[] hideLogicRect = getImgLogicPos(item);// 逻辑坐标
			item.m_matrix.set(matrix);

			// 隐藏矩形的坐标
			// left-top
			float hlr_lt_x = hideLogicRect[0];
			float hlr_lt_y = hideLogicRect[1];
			// right-top
			float hlr_rt_x = hideLogicRect[2];
			float hlr_rt_y = hideLogicRect[3];
			// right-bottom
			float hlr_rb_x = hideLogicRect[4];
			float hlr_rb_y = hideLogicRect[5];
			// left-bottom
			float hlr_lb_x = hideLogicRect[6];
			float hlr_lb_y = hideLogicRect[7];

			res.m_matrix.reset();
			global.m_matrix.invert(res.m_matrix);
			float[] resPts = getImgLogicPos(res);// 计算res图标位置 -->> 逻辑坐标
			if(resPts != null && resPts.length == 8){
				float res_lt_x = resPts[0];
				float res_lt_y = resPts[1];
				float res_rb_x = resPts[4];
				float res_rb_y = resPts[5];
				// res图标中心点
				float res_cen_x = (res_lt_x + res_rb_x)/2f;
				float res_cen_y = (res_lt_y + res_rb_y)/2f;

				if(res == def_delete_res)// 删除按钮
				{
					float[] src = new float[4];
					boolean isSetPos = false;

					float[] hideShowRect = new float[hideLogicRect.length];

					getShowPos(hideShowRect,hideLogicRect);

					// 隐藏矩形的坐标 --> 屏幕坐标
					// left-top
					float hsr_lt_x = hideShowRect[0];
					float hsr_lt_y = hideShowRect[1];
					// right-top
					float hsr_rt_x = hideShowRect[2];
					float hsr_rt_y = hideShowRect[3];
					// right-bottom
					float hsr_rb_x = hideShowRect[4];
					float hsr_rb_y = hideShowRect[5];
					// left-bottom
					float hsr_lb_x = hideShowRect[6];
					float hsr_lb_y = hideShowRect[7];

					if(hsr_lt_x > canvas_l + dis && hsr_lt_x < canvas_r - dis)
					{
						if(hsr_lt_y > canvas_t + dis && hsr_lt_y < canvas_b - dis)
						{
							src[0] = res_cen_x;
							src[1] = res_cen_y;
							src[2] = hlr_lt_x;
							src[3] = hlr_lt_y;
							inverseCount(src, matrices);
							res.m_matrix.postTranslate(src[2] - src[0], src[3] - src[1]);
							isSetPos = true;
						}
					}

					if(!isSetPos && hsr_rb_x > canvas_l + dis && hsr_rb_x < canvas_r - dis)
					{
						if(hsr_rb_y > canvas_t + dis && hsr_rb_y < canvas_b - dis)
						{
							src[0] = res_cen_x;
							src[1] = res_cen_y;
							src[2] = hlr_rb_x;
							src[3] = hlr_rb_y;
							inverseCount(src, matrices);
							res.m_matrix.postTranslate(src[2] - src[0], src[3] - src[1]);
							isSetPos = true;
						}
					}

					if(!isSetPos && hsr_lb_x > canvas_l + dis && hsr_lb_x < canvas_r - dis)
					{
						if(hsr_lb_y > canvas_t + dis && hsr_lb_y < canvas_b - dis)
						{
							src[0] = res_cen_x;
							src[1] = res_cen_y;
							src[2] = hlr_lb_x;
							src[3] = hlr_lb_y;
							inverseCount(src, matrices);
							res.m_matrix.postTranslate(src[2] - src[0], src[3] - src[1]);
							isSetPos = true;
						}
					}

					if(!isSetPos && hsr_rt_x > canvas_l + dis && hsr_rt_x < canvas_r - dis)
					{
						if(hsr_rt_y > canvas_t + dis && hsr_rt_y < canvas_b - dis)
						{
							src[0] = res_cen_x;
							src[1] = res_cen_y;
							src[2] = hlr_rt_x;
							src[3] = hlr_rt_y;
							inverseCount(src, matrices);
							res.m_matrix.postTranslate(src[2] - src[0], src[3] - src[1]);
							isSetPos = true;
						}
					}

					if(!isSetPos)
					{
						src[0] = res_cen_x;
						src[1] = res_cen_y;
						src[2] = hlr_lt_x;
						src[3] = hlr_lt_y;
						inverseCount(src, matrices);
						res.m_matrix.postTranslate(src[2] - src[0], src[3] - src[1]);
					}
				}
			}
		}
	}

	public Object getPendantArrByIndex(int index)
	{
		if(mPendantArr != null && mPendantArr.size() > 0)
		{
			return mPendantArr.get(index);
		}
		return null;
	}

	public List<Shape> getPendantArray()
	{
		return mPendantArr;
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();

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

	@Override
	public Bitmap getOutPutBmp()
	{
		int size = img.m_bmp.getWidth() > img.m_bmp.getHeight() ? img.m_bmp.getWidth() : img.m_bmp.getHeight();

		float whscale = (canvas_r - canvas_l) / (canvas_b - canvas_t);
		float outW = size;
		float outH = outW / whscale;
		if(outH > size)
		{
			outH = size;
			outW = outH * whscale;
		}

		float scaleX = outW / (canvas_r - canvas_l);
		float scaleY = outH / (canvas_b - canvas_t);
		float scale = Math.min(scaleX,scaleY);
		global.m_matrix.postScale(scale,scale);

		Bitmap outBmp = Bitmap.createBitmap((int)outW, (int)outH, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(outBmp);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

		int len = mPendantArr.size();
		if(len > 0)
		{
			if(((BlurShapeEx)(mPendantArr.get(0))).m_type == TYPE_PENDANT)
			{
				myDrawOutPutItem(canvas, img, img.m_bmp, new PorterDuffXfermode(PorterDuff.Mode.SRC));
				myDrawOutPutItem(canvas, mPendantArr.get(0), mPendantArr.get(0).m_bmp, dst_out_mode);
				myDrawOutPutItem(canvas, img, m_glassBmp2, dst_over_mode);
				if(mPendantArr.get(0).m_ex != null && mPendantArr.get(0).m_ex instanceof Bitmap)
				{
					myDrawOutPutItem(canvas, mPendantArr.get(0), (Bitmap)mPendantArr.get(0).m_ex, src_over_mode);
				}
			}
			else
			{
				for(int i = 0; i < len; i++)
				{
					if(((BlurShapeEx)(mPendantArr.get(i))).m_type == TYPE_SHAPE)
					{
						myDrawOutPutItem(canvas, mPendantArr.get(i), mPendantArr.get(i).m_bmp, new PorterDuffXfermode(PorterDuff.Mode.SRC));
						PorterDuffXfermode tempMode = src_in_mode;
						if(isReverseMode)
						{
							tempMode = src_out_mode;
						}
						else
						{
							tempMode = src_in_mode;
						}

						myDrawOutPutItem(canvas, img, img.m_bmp, tempMode);
						myDrawOutPutItem(canvas, img, m_glassBmp1, dst_over_mode);
					}
					else
					{
						myDrawOutPutItem(canvas, mPendantArr.get(i), mPendantArr.get(i).m_bmp, dst_out_mode);
						myDrawOutPutItem(canvas, null, m_glassBmp2, dst_over_mode);
						if(mPendantArr.get(i).m_ex != null && mPendantArr.get(i).m_ex instanceof Bitmap)
						{
							myDrawOutPutItem(canvas, mPendantArr.get(i), (Bitmap)mPendantArr.get(i).m_ex, src_over_mode);
						}
					}
				}
			}
		}
		else
		{
			myDrawOutPutItem(canvas, img, img.m_bmp, new PorterDuffXfermode(PorterDuff.Mode.SRC));
		}
		return outBmp;
	}

	public interface ControlCallback
	{
		void selectPendant(int index);

		void fingerDown();

		void deletePendantType(int type, int index);
	}

	public static class BlurShapeEx extends Shape
	{
		public int m_type = TYPE_PENDANT;
		public boolean m_freeScale = false;

		public float MAX_SCALE = 2f;
		public float DEF_SCALE = 1f;
		public float MIN_SCALE = 0.5f;

		public float m_x;
		public float m_y;
		public int m_w;
		public int m_h;
		public float m_scaleX;
		public float m_scaleY;
		public float m_centerX;
		public float m_centerY;
	}
}