package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.util.ArrayList;

import cn.poco.filterManage.FilterItem;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.resource.ResType;
import cn.poco.resource.ThemeRes;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 下载更多和管理和主题详情页面
 */
public class BaseListAdapter extends BaseAdapter
{
	public static final int THEME_INTRO = 0;
	public static final int OTHER = 1;
	private ArrayList<BaseItemInfo> m_ItemInfos;
	private Context m_context;
	private BaseItem.OnBaseItemCallback m_cb;

	private int m_type = OTHER;
	private boolean m_canDrag = false;
	private boolean m_showLock = false;
	private boolean m_showCheck = false;
	private boolean m_canclickItem = false;
	private boolean m_canClickDownload = true;
	private int m_parentWidth;
	private int m_topHeight;
	private MyImageLoader m_loader;
	private MgrUtils.MyDownloadCB m_downloadThemeCB = null;

	private FilterItem.OnFilterItemClick mOnFilterItemClick;

	public BaseListAdapter(Context context, int parentWidth, int topheight)
	{
		m_context = context;
		m_parentWidth = parentWidth;
		m_topHeight = topheight;

		m_loader = new MyImageLoader();
		m_loader.SetMaxLoadCount(50);
	}

	public void setDatas(ArrayList<BaseItemInfo> itemInfos)
	{
		m_ItemInfos = itemInfos;
	}

	public void setType(int type)
	{
		m_type = type;
	}

	public void setOnBaseItemCallback(BaseItem.OnBaseItemCallback cb)
	{
		m_cb = cb;
	}

	public void showCheckBox(boolean show)
	{
		m_showCheck = show;
	}

	public void canDrag(boolean can)
	{
		m_canDrag = can;
	}

	public void showLock(boolean show)
	{
		m_showLock = show;
	}

	public void canClickItem(boolean show)
	{
		m_canclickItem = show;
	}

	public void canClickDownload(boolean can)
	{
		m_canClickDownload = can;
	}

	@Override
	public int getCount()
	{
		if(m_ItemInfos != null)
		{
			return m_ItemInfos.size() + 1;
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if(m_ItemInfos != null)
		{
			return m_ItemInfos.get(position - 1);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position - 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(getItemViewType(position) == 0)
		{
			if(m_type == THEME_INTRO)
			{
				if(null == convertView)
				{
					convertView = new TitleView(m_context);
				}
				if(m_ItemInfos != null && m_ItemInfos.size() > 0)
				{
					convertView.setTag("title");
					((TitleView)convertView).setThemeInfo(m_ItemInfos.get(0).m_themeRes);
				}

			}
			else
			{
				if(null == convertView)
				{
					convertView = new ImageView(m_context);
					convertView.setMinimumHeight(ShareData.PxToDpi_xhdpi(96));
					convertView.setTag("top");
				}
			}
		}
		else
		{
			if (m_ItemInfos.get(position-1).m_type == ResType.FILTER) {
				BaseItemInfo itemInfo = m_ItemInfos.get(position - 1);
				FilterItem filterItem = new FilterItem(m_context);
				filterItem.setData(itemInfo, m_showLock, m_cb);
				filterItem.setDownloadBtnState(itemInfo.m_state, itemInfo.m_progress);
				filterItem.setOnFilterItemClick(mOnFilterItemClick);
				itemInfo.m_uri = position - 1;
				filterItem.setTag(position - 1);
				return filterItem;
			}

			if(null == convertView)
			{
				convertView = new BaseItem(m_context, m_parentWidth, m_topHeight);
			}
			if(null != m_ItemInfos)
			{
				if(m_type == OTHER)
				{
					convertView.setBackgroundResource(R.drawable.mgr_list_bk);
				}
				((BaseItem)convertView).setOnBaseItemCallback(m_cb);
				((BaseItem)convertView).showLock(m_showLock);
				((BaseItem)convertView).canClickItem(m_canclickItem);
				((BaseItem)convertView).showCheckBox(m_showCheck);
				((BaseItem)convertView).canDrag(m_canDrag);
				((BaseItem)convertView).SetLoader(m_loader);
				((BaseItem)convertView).canClickDownload(m_canClickDownload);
				BaseItemInfo info = m_ItemInfos.get(position - 1);
				info.m_uri = position - 1;
				convertView.setTag(position - 1);
				((BaseItem)convertView).SetData(info);
			}
		}
		return convertView;
	}

	@Override
	public int getItemViewType(int position)
	{
		if(position == 0)
			return 0;
		else
			return 1;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	public void ReleaseMem()
	{
		if(m_downloadThemeCB != null)
		{
			m_downloadThemeCB.ClearAll();
			m_downloadThemeCB = null;
		}
		if(m_loader != null)
		{
			m_loader.releaseMem(true);
			m_loader = null;
		}
		m_cb = null;
	}

	public void setOnFilterItemClick(FilterItem.OnFilterItemClick onFilterItemClick) {
		mOnFilterItemClick = onFilterItemClick;
	}

	class TitleView extends FrameLayout
	{
		ImageView m_topicIcon; //主题icon
		TextView m_topicDetail; //主题说明
		ProgressBar m_dlg;
		private int m_leftMargin;
		private int m_detailViewHeight;
		private int m_frH;
		private int m_frW;

		public TitleView(Context context)
		{
			super(context);
			ShareData.InitData(context);
			setPadding(0, 0, 0, ShareData.PxToDpi_xhdpi(10));

			m_leftMargin = (int)(28 / 720f * ShareData.m_screenWidth);
			m_detailViewHeight = (int)(98 / 1280f * ShareData.m_screenHeight);
			m_frW = ShareData.m_screenWidth;

			AbsListView.LayoutParams vl = new AbsListView.LayoutParams(ShareData.m_screenWidth, AbsListView.LayoutParams.WRAP_CONTENT);
			this.setLayoutParams(vl);
//			this.setBackgroundColor(0x99ffffff);
			FrameLayout.LayoutParams fl;

			m_topicIcon = new ImageView(getContext());
			m_topicIcon.setBackgroundColor(0xff000000);
			m_topicIcon.setScaleType(ImageView.ScaleType.FIT_START);
			fl = new FrameLayout.LayoutParams(m_frW, FrameLayout.LayoutParams.WRAP_CONTENT);
			m_topicIcon.setLayoutParams(fl);
			this.addView(m_topicIcon);

			m_topicDetail = new TextView(getContext());
			m_topicDetail.setGravity(Gravity.CENTER_VERTICAL);
			m_topicDetail.setPadding(m_leftMargin, 0, 0, 0);
			m_topicDetail.setTextColor(0xd8000000);
			m_topicDetail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth, m_detailViewHeight);
			fl.gravity = Gravity.BOTTOM;
			m_topicDetail.setLayoutParams(fl);
			this.addView(m_topicDetail);

			m_dlg = new ProgressBar(getContext());
			m_dlg.setVisibility(View.GONE);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			m_dlg.setLayoutParams(fl);
			this.addView(m_dlg);
		}

		public void setThemeInfo(ThemeRes res)
		{
			if(res == null)
				return;
			if(res.m_type == BaseRes.TYPE_LOCAL_RES && res.m_pic != null)
			{
				Bitmap thumb = BitmapFactory.decodeResource(getResources(), (Integer)res.m_pic);
				setIconImage(thumb);
			}
			if(res.m_type == BaseRes.TYPE_LOCAL_PATH && res.m_pic != null)
			{
				Bitmap thumb = BitmapFactory.decodeFile((String)res.m_pic);
				setIconImage(thumb);
			}
			if(res.m_type == BaseRes.TYPE_NETWORK_URL)
			{
				m_dlg.setVisibility(View.VISIBLE);
				downloadTheme(res);
			}
			m_topicDetail.setText(res.m_detail);
		}

		public void downloadTheme(ThemeRes res)
		{
			if(m_downloadThemeCB == null)
			{
				m_downloadThemeCB = new MgrUtils.MyDownloadCB(new MgrUtils.MyCB()
				{

					@Override
					public void OnProgress(int downloadId, IDownload[] resArr, int progress)
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void OnGroupComplete(int downloadId, IDownload[] resArr)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void OnFail(int downloadId, IDownload res)
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void OnGroupFailed(int downloadId, IDownload[] resArr)
					{

					}

					@Override
					public void OnComplete(int downloadId, IDownload res)
					{
						FileInputStream fis = null;
						try
						{
							fis = new FileInputStream((String)((ThemeRes)res).m_pic);
							Bitmap m_bmp = BitmapFactory.decodeStream(fis);
							fis.close();
							setIconImage(m_bmp);
						}
						catch(Exception e)
						{
							if(fis != null)
							{
								try
								{
									fis.close();
								}catch(Exception e1){}
							}
						}
						if(m_dlg != null)
						{
							m_dlg.setVisibility(View.GONE);
							TitleView.this.removeView(m_dlg);
							m_dlg = null;
						}
					}
				});
			}
			DownloadMgr.getInstance().DownloadRes(res, false, m_downloadThemeCB);
		}

		private void setIconImage(Bitmap thumb)
		{
			if(thumb != null)
			{
				float scale = thumb.getHeight() / (float)thumb.getWidth();
				m_frH = (int)(scale * m_frW);
				if(m_frW <= thumb.getWidth())
				{
					FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(m_frW, m_frH);
					m_topicIcon.setLayoutParams(fl);
					m_topicIcon.setImageBitmap(thumb);
				}
				else
				{
					Bitmap bmp = MakeBmpV2.CreateFixBitmapV2(thumb, 0, 0, MakeBmpV2.POS_BOTTOM | MakeBmpV2.POS_LEFT, m_frW, m_frH, Bitmap.Config.ARGB_8888);
					if(thumb != null)
					{
						thumb.recycle();
						thumb = null;
					}
					m_topicIcon.setImageBitmap(bmp);
				}
			}
		}
	}
}
