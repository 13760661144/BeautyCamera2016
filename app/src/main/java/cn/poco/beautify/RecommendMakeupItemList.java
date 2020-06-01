package cn.poco.beautify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.advanced.RecommendItemList;
import cn.poco.resource.ResourceUtils;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;
import cn.poco.tsv.AsynImgLoader;
import cn.poco.tsv100.FastHSVCore100;
import cn.poco.tsv100.FastItemList100;
import my.beautyCamera.R;

public class RecommendMakeupItemList extends FastItemList100
{
	protected RecommendMakeupItemConfig m_itemConfig;
	protected AsynImgLoader m_loader;

	//public int m_currentSel = -1; //索引(添加删除项目时需要更新)
	public int m_currentSubSel = -1; //子索引

	protected TweenLite m_scrollToCenterTween = new TweenLite();
	protected boolean m_hasAnim = false;

	public RecommendMakeupItemList(Context context)
	{
		super(context);
	}

	public RecommendMakeupItemList(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RecommendMakeupItemList(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public void InitData(RecommendMakeupItemConfig config)
	{
		m_itemConfig = config;
		m_itemConfig.InitData();

		m_loader = new AsynImgLoader(new AsynImgLoader.ControlCallback()
		{
			@Override
			public void PopImg(int id)
			{
				ItemInfo info = GetItemInfoById(id);
				RecommendMakeupItemConfig config = m_itemConfig;
				if(config != null)
				{
					info.m_animTime = System.currentTimeMillis() + config.def_anim_time;
				}
				RecommendMakeupItemList.this.invalidate();
			}

			@Override
			public Bitmap MakeBmp(AsynImgLoader.Item item)
			{
				Bitmap out = null;

				RecommendMakeupItemConfig config = m_itemConfig;
				if(item != null && config != null)
				{
					LoadItem res = (LoadItem)item.m_res;
					Bitmap temp = null;
					if(res.m_res instanceof Integer)
					{
						temp = BitmapFactory.decodeResource(getResources(), (Integer)res.m_res);
					}
					else if(res.m_res instanceof String)
					{
						temp = BitmapFactory.decodeFile((String)res.m_res);
					}
					if(res.m_flag)
					{
						//六边形
						if(temp != null && temp.getWidth() > 0 && temp.getHeight() > 0)
						{
							//System.out.println("w:" + temp.getWidth() + " h:" + temp.getHeight());
							out = Bitmap.createBitmap(config.def_sub_img_w, config.def_sub_img_h, Config.ARGB_8888);
							Canvas canvas = new Canvas(out);
							Paint p = new Paint();
							p.reset();
							p.setAntiAlias(true);
							p.setFilterBitmap(true);
							Matrix m = new Matrix();
							float s1 = (float)out.getWidth() / (float)temp.getWidth();
							float s2 = (float)out.getHeight() / (float)temp.getHeight();
							float s = s1 > s1 ? s1 : s2;
							m.postScale(s, s, temp.getWidth() / 2f, temp.getHeight() / 2f);
							m.postTranslate((out.getWidth() - temp.getWidth()) / 2f, (out.getHeight() - temp.getHeight()) / 2f);
							canvas.drawBitmap(temp, m, p);
							temp.recycle();
							temp = null;

							p.reset();
							p.setAntiAlias(true);
							p.setFilterBitmap(true);
							p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
							m.reset();
							canvas.drawBitmap(config.m_maskBmp, m, p);
						}
					}
					else
					{
						//正方形
						if(temp != null)
						{
							out = ImageUtils.MakeRoundBmp(temp, config.def_img_w, config.def_img_h, 0);
							if(out != temp)
							{
								temp.recycle();
								temp = null;
							}
						}
					}
				}

				return out;
			}
		});
		m_loader.SetQueueSize(GetShowNum(ShareData.m_screenWidth, m_itemConfig.def_item_w));
	}

	protected int GetShowNum(int viewW, int itemW)
	{
		if(itemW > 0)
		{
			return (int)Math.ceil((float)(viewW + itemW) / itemW);
		}
		else
		{
			return 0;
		}
	}

	@Override
	public void SetData(ArrayList<?> infoList, FastHSVCore100.ControlCallback cb)
	{
		m_currentSel = -1;
		m_currentSubSel = -1;

		super.SetData(infoList, cb);
	}

	@Override
	public void AutoMeasureSize()
	{
		super.AutoMeasureSize();
		if(m_frW > 0)
		{
			m_frW += m_itemConfig.def_item_l;
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if(m_hasAnim)
		{
			this.invalidate();
			AutoMeasureSize();
			m_hasAnim = false;

			if(m_parentCb != null && !m_scrollToCenterTween.M1IsFinish() && m_infoList != null && m_infoList.size() > open_index && open_index >= 0)
			{
				ItemInfo info = (ItemInfo)m_infoList.get(open_index);
				int[] ps = info.GetCurrentSubPos(-1);
				m_parentCb.OnScrollToCenter(this, (int)((GetScrollX(info, ps[0], ps[1]) - start_scroll_x) * m_scrollToCenterTween.M1GetPos() + start_scroll_x + 0.5f), false);
			}
		}
	}

	protected int GetScrollX(ItemInfo info, int x, int w)
	{
		int out = 0;

		if(info != null)
		{
			out = x + info.m_x + (w - m_showW) / 2;
		}

		return out;
	}

	public void ScrollToCenter(int index, int subIndex, boolean hasAnim)
	{
		if(m_parentCb != null && index >= 0 && index < m_infoList.size())
		{
			m_scrollToCenterTween.M1End();

			ItemInfo info = (ItemInfo)m_infoList.get(index);
			int[] ps = info.GetCurrentSubPos(subIndex);
			//System.out.println(ps[0] + GetIndexX(index) + " " + ps[1]);
			m_parentCb.OnScrollToCenter(this, GetScrollX(info, ps[0], ps[1]), hasAnim);
		}
	}

	/**
	 * 和展开动画配合使用
	 */
	public void InitScrollToCenterAnim()
	{
		m_scrollToCenterTween.M1End();
		m_scrollToCenterTween.Init(0, 1, ((RecommendMakeupItemConfig)m_itemConfig).def_anim_time);
		m_scrollToCenterTween.M1Start(((RecommendMakeupItemConfig)m_itemConfig).def_anim_type);
	}

	@Override
	protected void OnDown(MotionEvent event)
	{
		super.OnDown(event);

		m_scrollToCenterTween.M1End();
	}

	@Override
	protected void OnItemDown(int index, float x, float y)
	{
		super.OnItemDown(index, x, y);

		if(m_cb != null)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(index);
			((ControlCallback)m_cb).OnItemDown(this, info, index, info.GetTouchSubIndex(x));
		}
	}

	@Override
	protected void OnItemUp(int index, float x, float y)
	{
		super.OnItemUp(index, x, y);

		if(m_cb != null)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(index);
			((ControlCallback)m_cb).OnItemUp(this, info, index, info.GetTouchSubIndex(x));
		}
	}

	@Override
	protected void OnItemClick(int index, float x, float y)
	{
		super.OnItemClick(index, x, y);

		if(m_cb != null)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(index);
//			if(info instanceof DownloadAndRecommendItemInfo)
//			{
//				if(((DownloadAndRecommendItemInfo)info).IsRecommendItem(x, y))
//				{
//					((ControlCallback)m_cb).OnRecommend(this, info, index, info.GetTouchSubIndex(x));
//					return;
//				}
//			}
//			else
//			{
			((ControlCallback)m_cb).OnItemClick(this, info, index, info.GetTouchSubIndex(x));
//			}
		}
	}

	/**
	 * 需更新所有索引
	 *
	 * @param index
	 * @param info
	 */
	public void AddGroupItem(int index, ItemInfo info)
	{
		if(m_infoList != null && info != null && GetGroupItemInfoByUri(info.m_uris[0]) == null)
		{
			if(index <= 0)
			{
				m_infoList.add(0, info);
				if(m_currentSel >= 0)
				{
					m_currentSel++;
				}
			}
			else if(index < m_infoList.size())
			{
				m_infoList.add(index, info);
				if(m_currentSel >= index)
				{
					m_currentSel++;
				}
			}
			else
			{
				m_infoList.add(info);
			}

			AutoMeasureSize();
		}
	}

	@Override
	public void UpdateOrder(int[] uris)
	{
		if(m_infoList != null && uris != null && uris.length > 0)
		{
			ArrayList<?> temp = m_infoList;
			ArrayList<ItemInfo> list = (ArrayList<ItemInfo>)temp;

			Integer selUri = null;
			if(m_currentSel > -1)
			{
				selUri = list.get(m_currentSel).m_uris[0];
			}

			int[] is;
			ArrayList<ItemInfo> tempList = new ArrayList<ItemInfo>();
			for(int i = 0; i < uris.length; i++)
			{
				is = GetSubIndexByUri(list, uris[i]);
				if(is[0] >= 0)
				{
					tempList.add(list.remove(is[0]));
				}
			}
			list.addAll(0, tempList);

			if(selUri != null)
			{
				is = GetSubIndexByUri(list, selUri);
				m_currentSel = is[0];
			}

			AutoMeasureSize();
		}
	}

	public void AutoOpenOrCloseByIndex(int index, boolean hasAnim)
	{
		if(m_infoList != null && m_infoList.size() > index && index >= 0)
		{
			ItemInfo temp = (ItemInfo)m_infoList.get(index);

			int w;
			if(!temp.m_tween.M1IsFinish())
			{
				w = (int)temp.m_tween.GetEnd();
			}
			else
			{
				w = temp.m_w;
			}

			boolean open = false;
			if(w == temp.m_minW)
			{
				open = true;
			}
			OpenOrCloseByIndex(index, open, hasAnim);
		}
	}

	public void AutoOpenOrCloseByUri(int uri, boolean hasAnim)
	{
		AutoOpenOrCloseByIndex(GetGroupIndex(m_infoList, uri), hasAnim);
	}

	public void OpenOrCloseItem(ItemInfo item, boolean open, boolean hasAnim)
	{
		if(item != null)
		{
			int w;
			if(open)
			{
				w = item.m_maxW;
			}
			else
			{
				w = item.m_minW;
			}
			if(hasAnim && item.m_w != w)
			{
				if(!item.m_tween.M1IsFinish() && w == item.m_tween.GetEnd())
				{
					return;
				}
				item.m_tween.M1End();
				item.m_tween.Init(item.m_w, w, ((RecommendMakeupItemConfig)m_itemConfig).def_anim_time);
				item.m_tween.M1Start(((RecommendMakeupItemConfig)m_itemConfig).def_anim_type);
			}
			else
			{
				item.m_tween.M1End();
				item.m_w = w;
			}

			m_hasAnim = true;
			this.requestLayout();
			this.invalidate();
		}
	}

	public void CloseOtherIndex(int index, boolean hasAnim)
	{
		if(m_infoList != null)
		{
			int len = m_infoList.size();
			ItemInfo temp;
			for(int i = 0; i < len; i++)
			{
				if(i != index)
				{
					temp = (ItemInfo)m_infoList.get(i);
					int w = temp.m_minW;
					if(hasAnim && temp.m_w != w)
					{
						temp.m_tween.M1End();
						temp.m_tween.Init(temp.m_w, w, ((RecommendMakeupItemConfig)m_itemConfig).def_anim_time);
						temp.m_tween.M1Start(((RecommendMakeupItemConfig)m_itemConfig).def_anim_type);
					}
					else
					{
						temp.m_tween.M1End();
						temp.m_w = w;
					}
				}
			}

			m_hasAnim = true;
			this.requestLayout();
			this.invalidate();
		}
	}

	protected int open_index;
	protected int start_scroll_x;
	protected int old_frW;

	public void OpenOrCloseByIndex(int index, boolean open, boolean hasAnim)
	{
		if(m_infoList != null && m_infoList.size() > index && index >= 0)
		{
			CloseOtherIndex(index, hasAnim);

			open_index = index;
			if(m_parentCb != null)
			{
				start_scroll_x = m_showOffsetX;
			}
			old_frW = m_frW;
			OpenOrCloseItem((ItemInfo)m_infoList.get(index), open, hasAnim);
		}
	}

	public int SetSelectByUri(int uri, int subIndex)
	{
		return SetSelectByIndex(GetGroupIndex(m_infoList, uri), subIndex);
	}

	public void OpenOrCloseByUri(int uri, boolean open, boolean hasAnim)
	{
		OpenOrCloseByIndex(GetGroupIndex(m_infoList, uri), open, hasAnim);
	}

	public ItemInfo GetItemInfoById(int id)
	{
		ItemInfo out = null;

		if(m_infoList != null)
		{
			ItemInfo temp;
			int len = m_infoList.size();
			for(int i = 0; i < len; i++)
			{
				temp = (ItemInfo)m_infoList.get(i);
				if(ResourceUtils.HasId(temp.m_ids, id) > -1)
				{
					out = temp;
					break;
				}
			}
		}

		return out;
	}

	public static int[] GetSubIndexByUri(ArrayList<? extends ItemInfo> arr, int uri)
	{
		int[] out = new int[]{-1, -1};

		if(arr != null)
		{
			ItemInfo temp;
			int len = arr.size();
			int index;
			for(int i = 0; i < len; i++)
			{
				temp = (ItemInfo)arr.get(i);
				if((index = ResourceUtils.HasId(temp.m_uris, uri)) > -1)
				{
					out[0] = i;
					if(index > 0)
					{
						out[1] = index;
					}
					break;
				}
			}
		}

		return out;
	}

	public ItemInfo GetGroupItemInfoByUri(int uri)
	{
		ItemInfo out = null;

		if(m_infoList != null)
		{
			ItemInfo temp;
			int len = m_infoList.size();
			for(int i = 0; i < len; i++)
			{
				temp = (ItemInfo)m_infoList.get(i);
				if(temp.m_uris[0] == uri)
				{
					out = temp;
					break;
				}
			}
		}

		return out;
	}

	@Override
	public int DeleteItemByUri(int uri)
	{
		ArrayList<?> temp = m_infoList;
		int[] is = GetSubIndexByUri((ArrayList<ItemInfo>)temp, uri);
		return DeleteItemByIndex(is[0]);
	}

	@Override
	public void CancelSelect()
	{
		m_currentSubSel = -1;

		super.CancelSelect();
	}

	public int SetSelectByIndex(int index, int subIndex)
	{
		int out = -1;

		if(m_infoList != null && m_infoList.size() > index && index >= 0)
		{
			m_currentSel = index;
			out = index;

			ItemInfo temp = (ItemInfo)m_infoList.get(m_currentSel);
			if(subIndex >= 0 && subIndex < temp.m_logos.length)
			{
				m_currentSubSel = subIndex;
			}

			//生成遮罩颜色
			temp.DrawSubItemColorBmp();

			this.invalidate();
		}

		return out;
	}

	public int SetItemStyleByIndex(int index, ItemInfo.Style style)
	{
		int out = -1;
		if(m_infoList != null && m_infoList.size() > index)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(index);
			info.m_style = style;
			out = index;
			this.invalidate();
		}
		return out;
	}

	public int SetItemStyleByUri(int uri, ItemInfo.Style style)
	{
		int out = GetGroupIndex(m_infoList, uri);
		if(out >= 0)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(out);
			info.m_style = style;
			this.invalidate();
		}

		return out;
	}

	public void Lock2(int uri)
	{
		ItemInfo info = GetGroupItemInfoByUri(uri);
		if(info != null)
		{
			info.m_isLock2 = true;
		}
		this.invalidate();
	}

	public void Unlock2(int uri)
	{
		ItemInfo info = GetGroupItemInfoByUri(uri);
		if(info != null)
		{
			info.m_isLock2 = false;
		}
		this.invalidate();
	}

	public <T extends FastHSVCore100.ItemInfo> int GetGroupIndex(ArrayList<T> arr, int uri)
	{
		int out = -1;

		if(arr != null)
		{
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				if(((ItemInfo)arr.get(i)).m_uris[0] == uri)
				{
					out = i;
					break;
				}
			}
		}

		return out;
	}

	@Override
	public void ClearAll()
	{
		super.ClearAll();

		if(m_itemConfig != null)
		{
			m_itemConfig.ClearAll();
			m_itemConfig = null;
		}
		if(m_loader != null)
		{
			m_loader.ClearAll();
			m_loader = null;
		}
		if(m_infoList != null)
		{
			int len = m_infoList.size();
			ItemInfo info;
			for(int i = 0; i < len; i++)
			{
				info = (ItemInfo)m_infoList.get(i);
				info.ClearAll();
			}
			m_infoList.clear();
		}
	}

	public static class LoadItem
	{
		public boolean m_flag; //true的时候需要做六边形
		public Object m_res;

		public LoadItem(boolean flag, Object res)
		{
			m_flag = flag;
			m_res = res;
		}
	}

	public static class ItemInfo extends FastItemList100.ItemInfo
	{
		public static int MY_ID = 1;

		public enum Style
		{
			//正常
			NORMAL(0),
			//需要下载
			NEED_DOWNLOAD(1),
			//下载中
			LOADING(2),
			//等待下载
			WAIT(3),
			//下载失败
			FAIL(4),
			//新下载
			NEW(5);

			private final int m_value;

			Style(int value)
			{
				m_value = value;
			}

			public int GetValue()
			{
				return m_value;
			}
		}

		public long m_animTime = 0;

		public int[] m_uris;
		public Object[] m_logos;
		public String[] m_names;
		public int[] m_ids;
		public int m_bkColor;

		public Object m_ex;

		public int m_minW;
		public int m_maxW;
		public int m_w = -1;
		public int m_h = -1;

		public Style m_style = Style.NORMAL;
		public boolean m_isLock2 = false;

		protected TweenLite m_tween = new TweenLite();

		public ItemInfo(RecommendMakeupItemConfig config)
		{
			super(config);
		}

		public void SetData(int[] uris, Object[] logos, String[] names, int color, Object ex)
		{
			m_uris = uris;
			m_logos = logos;
			m_names = names;
			m_bkColor = color;
			m_ex = ex;

			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final int item_w = config.def_item_w;
			final int item_h = config.def_item_h;
			final int item_l = config.def_item_l;
			final int item_r = config.def_item_r;
			final int item_l2 = config.def_item_l2;
			final int sub_img_w = config.def_sub_img_w;

			if(m_logos != null)
			{
				m_ids = new int[m_logos.length];
				for(int i = 0; i < m_logos.length; i++)
				{
					m_ids[i] = MY_ID++;
				}

				if(m_logos.length > 1)
				{
					m_minW = item_l + item_w + item_r;
					m_maxW = item_l + item_w + (sub_img_w + item_l2) * (m_logos.length - 1) + item_l2 - item_r;
					m_w = m_minW;
					m_h = item_h;
				}
				else
				{
					m_minW = item_l + item_w + item_r;
					m_maxW = m_minW;
					m_w = m_minW;
					m_h = item_h;
				}
			}
			else
			{
				m_minW = item_l + item_w + item_r;
				m_maxW = m_minW;
				m_w = m_minW;
				m_h = item_h;
			}
		}

		protected void DrawSubItemColorBmp()
		{
			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final Paint paint = config.temp_paint;

			if(config.m_subItemColorBmp != null && config.m_maskBmp != null)
			{
				Canvas canvas = new Canvas(config.m_subItemColorBmp);
				canvas.drawColor(0, PorterDuff.Mode.CLEAR);
				canvas.drawColor((m_bkColor & 0x00FFFFFF) | 0xEE000000);
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
				config.temp_matrix.reset();
				canvas.drawBitmap(config.m_maskBmp, config.temp_matrix, paint);
			}
		}

		protected Bitmap GetImg(AsynImgLoader loader, int id, Object res, boolean flag)
		{
			Bitmap out = null;

			out = loader.GetImg(id, true);
			if(out == null)
			{
				loader.PushImg(id, new LoadItem(flag, res));
				if(flag)
				{
					out = ((RecommendMakeupItemConfig)m_config).m_defSubImgBmp;
				}
			}

			return out;
		}

		protected void DrawBk(Canvas canvas, int color)
		{
			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_w = config.def_item_w;
			final int item_h = config.def_item_h;
			final int item_l = config.def_item_l;

			paint.reset();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(color);
			canvas.drawRect(item_l, 0, item_l + item_w, item_h, paint);
		}

		protected Matrix temp_matrix = new Matrix();

		protected void DrawImg(Canvas canvas, Bitmap bmp)
		{
			if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0)
			{
				final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
				final Paint paint = config.temp_paint;
				final int item_l = config.def_item_l;
				final int item_w = config.def_img_w;
				final int item_h = config.def_img_h;
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);

				temp_matrix.reset();
				temp_matrix.postScale(item_w / (float)bmp.getWidth(), item_h / (float)bmp.getHeight());
				temp_matrix.postTranslate(item_l, 0);
				canvas.drawBitmap(bmp, temp_matrix, paint);
				//canvas.drawBitmap(bmp, item_l, 0, paint);
			}
		}

		protected void DrawName(Canvas canvas, int selIndex, int index, String name)
		{
			if(name != null && name.length() > 0)
			{
				final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
				final Paint paint = config.temp_paint;
				final int item_w = config.def_item_w;
				final int item_h = config.def_item_h;
				final int item_l = config.def_item_l;

				paint.reset();
				paint.setAntiAlias(true);
				paint.setTextSize(config.def_title_size);
				paint.setTextAlign(Align.CENTER);
				if(selIndex == index)
				{
					paint.setColor(config.def_title_color_over);
				}
				else
				{
					paint.setColor(config.def_title_color_out);
				}
				//float w = temp_paint.measureText(name);
				//canvas.drawText(name, def_item_l + (def_img_w - w) / 2, def_item_h - def_title_bottom_margin, temp_paint);
				canvas.drawText(name, item_l + item_w / 2, item_h - config.def_title_bottom_margin, paint);
			}
		}

		/**
		 * @param subIndex
		 * @return [0]:内部x [1]:w
		 */
		public int[] GetCurrentSubPos(int subIndex)
		{
			int[] out = new int[]{0, 0};

			if(subIndex <= 0 || m_logos == null || subIndex >= m_logos.length)
			{
				out[0] = 0;
				out[1] = m_w;
			}
			else
			{
				final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
				final int item_w = config.def_item_w;
				final int item_l = config.def_item_l;
				final int item_l2 = config.def_item_l2;
				final int sub_img_w = config.def_sub_img_w;

				out[0] = item_l + item_w + item_l2;
				out[1] = m_w - out[0];
				if(out[1] > sub_img_w)
				{
					out[1] = sub_img_w;
				}
				for(int i = 1; i < subIndex; i++)
				{
					out[1] = m_w - out[0];
					if(out[1] > sub_img_w)
					{
						out[1] = sub_img_w;
					}
					out[0] += sub_img_w + item_l2;
				}
			}

			return out;
		}

		public int GetTouchSubIndex(float x)
		{
			int out = -1;

			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final int item_w = config.def_item_w;
			final int item_l = config.def_item_l;
			final int item_l2 = config.def_item_l2;
			final int sub_img_w = config.def_sub_img_w;

			x -= item_l;
			if(x > 0)
			{
				if(x < item_w)
				{
					out = 0;
				}
				else
				{
					x -= item_w + item_l2;
					int i = 1;
					while(x > 0)
					{
						if(x < sub_img_w)
						{
							out = i;
							break;
						}
						x -= sub_img_w + item_l2;
						i++;
					}
				}
			}

			return out;
		}

		@Override
		public int GetCurrentW(FastHSVCore100 list)
		{
			return m_w;
		}

		@Override
		public int GetCurrentH(FastHSVCore100 list)
		{
			return m_h;
		}

		@Override
		public int GetFixW(FastHSVCore100 list)
		{
			if(m_tween.M1IsFinish())
			{
				return m_w;
			}
			else
			{
				return (int)(m_tween.GetEnd() + 0.5f);
			}
		}

		@Override
		public int GetFixH(FastHSVCore100 list)
		{
			return m_h;
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_w = config.def_item_w;
			final int item_h = config.def_item_h;
			final int item_l = config.def_item_l;
			final int item_l2 = config.def_item_l2;
			final int img_h = config.def_img_h;
			final int sub_img_w = config.def_sub_img_w;

			//画背景
			DrawBk(canvas, m_bkColor);
			//画图片
			Bitmap bmp = GetImg(((RecommendMakeupItemList)list).m_loader, m_ids[0], m_logos[0], false);
			DrawImg(canvas, bmp);
			if(index == ((RecommendMakeupItemList)list).m_currentSel)
			{
				paint.reset();
				paint.setStyle(Paint.Style.FILL);
				paint.setColor((m_bkColor & 0x00FFFFFF) | 0xCC000000);
				canvas.drawRect(item_l, 0, item_l + item_w, img_h, paint);
			}
			//画文字
			DrawName(canvas, ((RecommendMakeupItemList)list).m_currentSel, index, m_names[0]);
			//画子item
			if(m_logos.length > 1 && m_w > m_minW)
			{
				int x = item_l + item_w + item_l2;
				int len = m_logos.length;
				for(int i = 1; i < len; i++)
				{
					bmp = GetImg(((RecommendMakeupItemList)list).m_loader, m_ids[i], m_logos[i], true);
					if(bmp != null)
					{
						paint.reset();
						paint.setAntiAlias(true);
						paint.setFilterBitmap(true);
						canvas.drawBitmap(bmp, x, (item_h - config.def_sub_img_h) / 2, paint);
					}
					//画文字
					if(index == ((RecommendMakeupItemList)list).m_currentSel && i == ((RecommendMakeupItemList)list).m_currentSubSel)
					{
						bmp = config.m_subItemColorBmp;
						if(bmp != null)
						{
							paint.reset();
							paint.setAntiAlias(true);
							paint.setFilterBitmap(true);
							canvas.drawBitmap(bmp, x, (item_h - config.def_sub_img_h) / 2, paint);
						}
						//画勾
						if(config.m_subItemSelBmp == null)
						{
							config.m_subItemSelBmp = BitmapFactory.decodeResource(list.getResources(), R.drawable.photofactory_makeup_item_sub_sel);
						}
						if(config.m_subItemSelBmp != null)
						{
							paint.reset();
							paint.setAntiAlias(true);
							paint.setFilterBitmap(true);
							canvas.drawBitmap(config.m_subItemSelBmp, x + (sub_img_w - config.m_subItemSelBmp.getWidth()) / 2, (item_h - config.m_subItemSelBmp.getHeight()) / 2, paint);
						}
					}
					else
					{
						String name = m_names[i];
						if(name != null && name.length() > 0)
						{
							paint.reset();
							paint.setAntiAlias(true);
							paint.setTypeface(Typeface.DEFAULT_BOLD);
							paint.setTextSize(config.def_sub_title_size);
							paint.setTextAlign(Align.CENTER);
							paint.setColor(config.def_title_color_out);
							Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
							canvas.drawText(name, x + sub_img_w / 2, (item_h - (fmi.bottom - fmi.top)) / 2 - fmi.top, paint);
						}
					}

					x += sub_img_w + item_l2;
				}
			}

			//画下载状态
			switch(m_style)
			{
				case NEW:
					if(config.m_newBmp == null)
					{
						config.m_newBmp = BitmapFactory.decodeResource(list.getResources(), R.drawable.sticker_new);
					}
					if(config.m_newBmp != null)
					{
						canvas.drawBitmap(config.m_newBmp, item_l + item_w - config.m_newBmp.getWidth(), 0, null);
					}
					break;

				case NEED_DOWNLOAD:
					if(config.m_readyBmp == null)
					{
						config.m_readyBmp = BitmapFactory.decodeResource(list.getResources(), R.drawable.photofactory_makeup_item_ready);
					}
					if(config.m_readyBmp != null)
					{
						canvas.drawBitmap(config.m_readyBmp, item_l + item_w - config.m_readyBmp.getWidth(), 0, null);
					}
					break;

				case LOADING:
				case WAIT:
					if(config.m_loadingBmp == null)
					{
						config.m_loadingBmp = BitmapFactory.decodeResource(list.getResources(), R.drawable.photofactory_item_loading);
					}
					if(config.m_loadingBmp != null)
					{
						canvas.drawBitmap(config.m_loadingBmp, item_l + (item_w - config.m_loadingBmp.getWidth()) / 2, (img_h - config.m_loadingBmp.getHeight()) / 2, null);
					}
					break;

				default:
					break;
			}

			//画锁
			if(m_isLock2)
			{
				if(config.m_lockBmp == null)
				{
					config.m_lockBmp = BitmapFactory.decodeResource(list.getResources(), R.drawable.advance_decorate_lock_icon);
				}
				if(config.m_lockBmp != null)
				{
					canvas.drawBitmap(config.m_lockBmp, item_l + item_w - config.m_lockBmp.getWidth(), 0, null);
				}
			}

			//动画
			if(!m_tween.M1IsFinish())
			{
				m_w = (int)m_tween.M1GetPos();
				((RecommendMakeupItemList)list).m_hasAnim = true;
			}
		}

		@Override
		public boolean IsThouch(FastHSVCore100 list, float x, float y)
		{
			boolean out = false;

			float tx = x - m_x;
			if(tx > ((RecommendMakeupItemConfig)m_config).def_item_l)
			{
				out = true;
			}

			return out;
		}

		public void ClearAll()
		{
		}
	}

	public static class ItemInfo10 extends ItemInfo
	{

		public ItemInfo10(RecommendMakeupItemConfig config)
		{
			super(config);
		}

		@Override
		protected void DrawName(Canvas canvas, int selIndex, int index, String name)
		{
			if(name != null && name.length() > 0)
			{
				final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
				final Paint paint = config.temp_paint;
				final int item_w = config.def_item_w;
				final int item_h = config.def_item_h;
				final int item_l = config.def_item_l;

				paint.reset();
				paint.setAntiAlias(true);
				paint.setTextSize(config.def_title_size);
				paint.setTextAlign(Align.CENTER);
				if(selIndex == index)
				{
					paint.setColor(config.def_title_color_over);
				}
				else
				{
					paint.setColor(config.def_title_color_out);
				}
				//float w = temp_paint.measureText(name);
				//canvas.drawText(name, def_item_l + (def_img_w - w) / 2, def_item_h - def_title_bottom_margin, temp_paint);
				Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
				int x = item_l + item_w / 2;
				int y = item_w + ((item_h - item_w) - (fmi.bottom - fmi.top)) / 2 - fmi.top;

				canvas.drawText(name, x, y, paint);
			}
		}
	}

	public static class RecommendExItemInfo extends ItemInfo10
	{
		public static final int RECOM_ITEM_URI = 0xFFFFFFE1;

		protected Bitmap m_logoBmp;
		protected Bitmap m_recommendLogoBmp;

		public RecommendExItemInfo(RecommendMakeupItemConfig config)
		{
			super(config);

			m_uris = new int[]{RECOM_ITEM_URI};
			m_ids = new int[]{MY_ID++};
			m_uri = RECOM_ITEM_URI;
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;

			if(m_logos != null && m_logos.length > 0)
			{
				//画背景
				DrawBk(canvas, m_bkColor);
				//画图片
				if(m_logoBmp == null)
				{
					m_logoBmp = cn.poco.imagecore.Utils.DecodeImage(list.getContext(), m_logos[0], 0, -1, -1, -1);
				}
				if(m_logoBmp != null)
				{
					DrawImg(canvas, m_logoBmp);
				}
				//画文字
				DrawName(canvas, -1, 1, m_names[0]);
				//画推荐星
				if(m_recommendLogoBmp == null)
				{
					m_recommendLogoBmp = BitmapFactory.decodeResource(list.getContext().getResources(), ((RecommendMakeupItemConfig)m_config).def_recommend_res);
				}
				if(m_recommendLogoBmp != null)
				{
					canvas.drawBitmap(m_recommendLogoBmp, config.def_item_w + ((RecommendMakeupItemConfig)m_config).def_item_l - m_recommendLogoBmp.getWidth(), 0, null);
				}
			}
		}

		public void SetLogos(Object[] arr, String[] names, int color)
		{
			m_logos = arr;
			m_names = names;
			m_bkColor = color;
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}

		@Override
		public int GetCurrentW(FastHSVCore100 list)
		{
			return ((RecommendMakeupItemConfig)m_config).def_item_l + ((RecommendMakeupItemConfig)m_config).def_item_w + ((RecommendMakeupItemConfig)m_config).def_item_r;
		}

		@Override
		public int GetCurrentH(FastHSVCore100 list)
		{
			return ((RecommendMakeupItemConfig)m_config).def_item_h;
		}
	}

	public static class NullItemInfo extends ItemInfo
	{
		public static final int NULL_ITEM_URI = 0xFFFFFFF2;

		public Bitmap m_nullOverBmp;

		public NullItemInfo(RecommendMakeupItemConfig config)
		{
			super(config);

			SetData(new int[]{NULL_ITEM_URI}, new Object[]{R.drawable.photofactory_makeup_item_null_out}, new String[]{"无"}, 0xFFFFFFFF, null);
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_w = config.def_item_w;
			final int item_h = config.def_item_h;
			final int item_l = config.def_item_l;

			//画背景
			DrawBk(canvas, m_bkColor);
			//画图片
			Bitmap bmp = null;
			if(index == ((RecommendMakeupItemList)list).m_currentSel)
			{
				if(m_nullOverBmp == null)
				{
					m_nullOverBmp = BitmapFactory.decodeResource(list.getResources(), R.drawable.photofactory_makeup_item_null_over);
				}
				bmp = m_nullOverBmp;
			}
			else
			{
				bmp = GetImg(((RecommendMakeupItemList)list).m_loader, m_ids[0], m_logos[0], false);
			}
			DrawImg(canvas, bmp);
			//画文字
			paint.reset();
			paint.setAntiAlias(true);
			paint.setTextSize(config.def_title_size);
			paint.setTextAlign(Align.CENTER);
			paint.setColor(0xFF8C8C8C);
			canvas.drawText(m_names[0], item_l + item_w / 2, item_h - config.def_title_bottom_margin, paint);
		}

		@Override
		public int GetCurrentW(FastHSVCore100 list)
		{
			return m_minW;
		}

		public int GetCurrentH(FastHSVCore100 list)
		{
			return m_h;
		}

		public int GetFixW(FastHSVCore100 list)
		{
			return GetCurrentW(list);
		}

		@Override
		public int GetFixH(FastHSVCore100 list)
		{
			return GetCurrentH(list);
		}

		@Override
		public void ClearAll()
		{
			if(m_nullOverBmp != null)
			{
				m_nullOverBmp.recycle();
				m_nullOverBmp = null;
			}

			super.ClearAll();
		}
	}

	public static class NullItemInfo10 extends NullItemInfo
	{
		public NullItemInfo10(RecommendMakeupItemConfig config)
		{
			super(config);
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_w = config.def_item_w;
			final int item_h = config.def_item_h;
			final int item_l = config.def_item_l;

			//画背景
			DrawBk(canvas, m_bkColor);
			//画图片
			Bitmap bmp = null;

			bmp = GetImg(((RecommendMakeupItemList)list).m_loader, m_ids[0], m_logos[0], false);
			if(bmp != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				int x = (int)(item_l + (item_w - ShareData.PxToDpi_xhdpi(52)) / 2f);
				int y = (int)((item_h - ShareData.PxToDpi_xhdpi(52)) / 2f);
				RectF rectF = new RectF(x, y, x + ShareData.PxToDpi_xhdpi(52), y + ShareData.PxToDpi_xhdpi(52));
				canvas.drawBitmap(bmp, null, rectF, paint);
			}


			if(index == ((RecommendMakeupItemList)list).m_currentSel)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setColor(0xffe75988);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(ShareData.PxToDpi_xhdpi(4));
				RectF rectF = new RectF(ShareData.PxToDpi_xhdpi(2), ShareData.PxToDpi_xhdpi(2), item_w - ShareData.PxToDpi_xhdpi(2), item_h - ShareData.PxToDpi_xhdpi(2));

				Bitmap tempBmp = Bitmap.createBitmap(item_w, item_h, Config.ARGB_8888);
				Canvas tempCanvas = new Canvas(tempBmp);
				tempCanvas.drawRect(rectF, paint);
				cn.poco.advanced.ImageUtils.AddSkin(list.getContext(), tempBmp);
				canvas.drawBitmap(tempBmp, item_l, 0, null);
				if(tempBmp != null)
				{
					tempBmp.recycle();
					tempBmp = null;
				}
			}
			//画文字
			//paint.reset();
			//paint.setAntiAlias(true);
			//paint.setTextSize(config.def_title_size);
			//paint.setTextAlign(Align.CENTER);
			//paint.setColor(0xFF8C8C8C);
			//canvas.drawText(m_names[0], item_l + item_w / 2, item_h - config.def_title_bottom_margin, paint);
		}
	}


	public static class DownloadAndRecommendItemInfo extends ItemInfo
	{
		public static final int DOWNLOAD_RECOM_ITEM_URI = RecommendItemList.DownloadItemInfo.DOWNLOAD_ITEM_URI;

		protected static final int BLUR_COLOR = 0x20000000;
		protected static final int BLUR_R = 6;
		protected static final int OFFSET = 4;
		protected static final int SPACE_W = 10;

		protected Activity m_ac;
		protected Object[] m_logos;
		protected String[] m_names;
		protected Bitmap m_logoBmp;
		protected int m_num;

		public DownloadAndRecommendItemInfo(Activity ac, RecommendMakeupItemConfig config)
		{
			super(config);

			m_uris = new int[]{DOWNLOAD_RECOM_ITEM_URI};
			m_ids = new int[]{MY_ID++};
			m_ac = ac;
			m_uri = DOWNLOAD_RECOM_ITEM_URI;
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			//DrawBk(canvas,m_bkColor);
			if(m_logoBmp == null)
			{
				m_logoBmp = MakeLogoBmp();
			}
			if(m_logoBmp != null)
			{
				canvas.drawBitmap(m_logoBmp, ((RecommendMakeupItemConfig)m_config).def_item_l, 0, null);
			}
		}

		public void SetLogos(Object[] arr, String[] names, int color)
		{
			m_logos = arr;
			m_names = names;
			m_bkColor = color;
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}

		@Override
		public int GetCurrentW(FastHSVCore100 list)
		{
			return ((RecommendMakeupItemConfig)m_config).def_item_l + ((RecommendMakeupItemConfig)m_config).def_item_w + ((RecommendMakeupItemConfig)m_config).def_item_r;
		}

		@Override
		public int GetCurrentH(FastHSVCore100 list)
		{
			return ((RecommendMakeupItemConfig)m_config).def_item_h;
		}

		public void SetNum(int num)
		{
			m_num = num;
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}

		protected Bitmap MakeLogoBmp()
		{
			final RecommendMakeupItemConfig config = (RecommendMakeupItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_w = config.def_item_w;
			final int item_h = config.def_item_h;
			final int img_h = config.def_img_h;

			Bitmap out = Bitmap.createBitmap(item_w, item_h, Config.ARGB_8888);
			if(m_logos != null && m_logos.length > 1)
			{
				Canvas canvas = new Canvas(out);

				//画分割线
				/*paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(0xffffffff);
				paint.setStrokeWidth(3);
				PathEffect effects = new DashPathEffect(new float[]{ShareData.PxToDpi_xhdpi(16), ShareData.PxToDpi_xhdpi(6)}, 1);
				paint.setPathEffect(effects);
				canvas.drawLine(0, img_h, item_w, img_h, paint);*/
				//画图
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(0x33ffffff);
				canvas.drawRect(0, 0, item_w, img_h, paint);
				//int len = m_logos.length;
				int len = m_logos.length - 1;
				if(len > 3)
				{
					len = 3;
				}
				int subW = ShareData.PxToDpi_xhdpi(70);
				int allSubW = subW;
				int sw = ShareData.PxToDpi_xhdpi(SPACE_W);
				for(int i = 0; i < len - 1; i++)
				{
					allSubW += sw;
				}
				allSubW += BLUR_R;
				float ex = (item_w - allSubW) / 2f + (len - 1) * sw;
				float ey = ex;
				for(int i = len - 1; i > -1; i--)
				{
					//DrawSubBmp(canvas, m_logos[i], ex, ey, subW, subW);
					DrawSubBmp(canvas, m_logos[i + 1], ex, ey, subW, subW);
					ex -= sw;
					ey -= sw;
				}

				//画底下颜色矩形
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(0xffffffff);
				//canvas.drawRect(0, img_h + 2, item_w, item_h, paint);
				canvas.drawRect(0, img_h, item_w, item_h, paint);
				//下载更多
				if(m_num != 0)
				{
					Bitmap temp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.beautify_makeup_store_icon);
					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					int x = ShareData.PxToDpi_xhdpi(6);
					//int y = (item_h - (img_h + 2) - temp.getHeight()) / 2 + img_h + 2;
					int y = (item_h - img_h - temp.getHeight()) / 2 + img_h;
					temp = cn.poco.advanced.ImageUtils.AddSkin(m_ac, temp);
					canvas.drawBitmap(temp, x, y, paint);
					paint.setColor(cn.poco.advanced.ImageUtils.GetSkinColor(0xffe75988));
					paint.setTextSize(ShareData.PxToDpi_xhdpi(15));
					x = ShareData.PxToDpi_xhdpi(36);
					//y = img_h + 2 + ShareData.PxToDpi_xhdpi(20);
					y = img_h + ShareData.PxToDpi_xhdpi(20);
					//Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
					//y = (int) ((item_h - img_h - ShareData.PxToDpi_xhdpi(2) - (fmi.bottom - fmi.top))/2f - fmi.top) + img_h;
					canvas.drawText("下载更多", x, y, paint);
					//下载个数
					x = ShareData.PxToDpi_xhdpi(18);
					y = img_h - ShareData.PxToDpi_xhdpi(4);
					paint.reset();
					paint.setAntiAlias(true);
					temp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.beautify_makeup_downmore_num_icon);
					cn.poco.advanced.ImageUtils.AddSkin(m_ac, temp, 0xffe75988);
					canvas.drawBitmap(temp, x, y, paint);
					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					paint.setColor(0xffffffff);
					paint.setTextSize(ShareData.PxToDpi_xhdpi(12));
					paint.setTextAlign(Align.CENTER);
					Paint.FontMetrics fontMetrics = paint.getFontMetrics();
					canvas.drawText(Integer.toString(m_num), x + temp.getWidth() / 2f, y + temp.getHeight() / 2f + Math.abs(fontMetrics.ascent * 0.65f) / 2f, paint);
				}
			}
			else
			{
				//就画一个下载更多
				Canvas canvas = new Canvas(out);
				Bitmap temp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.photofactory_download_logo);
				temp = cn.poco.advanced.ImageUtils.AddSkin(m_ac, temp);
				float x = (item_w - temp.getWidth()) / 2f;
				float y = (item_h - temp.getHeight()) / 2f;
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(temp, x, y, paint);

				if(m_num != 0)
				{
//					if(config.m_downloadMoreBkBmp == null)
//					{
					Bitmap downNumBmp = BitmapFactory.decodeResource(m_ac.getResources(), config.def_download_more_num_res);
//					}
					if(downNumBmp != null)
					{
						x += temp.getWidth() - downNumBmp.getWidth() * 1.15f;
						downNumBmp = cn.poco.advanced.ImageUtils.AddSkin(m_ac, downNumBmp);
						canvas.drawBitmap(downNumBmp, x, y, paint);
						paint.reset();
						paint.setAntiAlias(true);
						paint.setFilterBitmap(true);
						paint.setColor(0xFFFFFFFF);
						paint.setTextSize(config.def_download_more_num_size);
						paint.setTextAlign(Align.CENTER);
						Paint.FontMetrics fontMetrics = paint.getFontMetrics();
						canvas.drawText(Integer.toString(m_num), x + downNumBmp.getWidth() / 2f, y + downNumBmp.getHeight() / 2f + Math.abs(fontMetrics.ascent * 0.8f) / 2f, paint);
					}
				}
			}

			return out;
		}

		private void DrawSubBmp(Canvas canvas, Object res, float x, float y, int w, int h)
		{
			Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(m_ac, res, 0, -1, -1, -1);
			if(bmp != null)
			{
				//阴影
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setColor(BLUR_COLOR);
				paint.setStyle(Paint.Style.FILL);
				paint.setShadowLayer(BLUR_R, ShareData.PxToDpi_xhdpi(OFFSET), ShareData.PxToDpi_xhdpi(OFFSET), BLUR_COLOR);
				canvas.drawRect(x, y, x + w, x + h, paint);

				//画图
				Matrix matrix = new Matrix();
				matrix.postScale((float)w / (float)bmp.getWidth(), (float)h / (float)bmp.getHeight());
				matrix.postTranslate(x, y);
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(bmp, matrix, paint);
			}
		}

		public boolean IsRecommendItem(float x, float y)
		{
			boolean out = false;

			if(m_logos != null && m_logos.length > 0)
			{
				if(y < ((RecommendMakeupItemConfig)m_config).def_img_h)
				{
					out = true;
				}
			}

			return out;
		}
	}

	public interface ControlCallback extends FastItemList100.ControlCallback
	{
		public void OnItemDown(FastHSVCore100 list, FastHSVCore100.ItemInfo info, int index, int subIndex);

		public void OnItemUp(FastHSVCore100 list, FastHSVCore100.ItemInfo info, int index, int subIndex);

		public void OnItemClick(FastHSVCore100 list, FastHSVCore100.ItemInfo info, int index, int subIndex);

		//public void OnRecommend(FastHSVCore100 list, FastHSVCore100.ItemInfo info, int index, int subIndex);
	}
}
