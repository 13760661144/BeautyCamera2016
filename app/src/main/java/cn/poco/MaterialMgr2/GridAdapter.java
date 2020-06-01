package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.poco.resource.BaseRes;
import cn.poco.resource.FrameExRes;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;

public class GridAdapter extends BaseAdapter
{
	private Context m_context;
	private ArrayList<? extends BaseRes> m_itemInfos;
	private boolean m_isAllShow = true;
	private int m_numColumns = 4;
	private int m_thumbSize = 0;
	private MyImageLoader m_loader;
	public GridAdapter(Context context, MyImageLoader loader)
	{
		m_context = context;

		m_loader = loader;
	}

	public void SetDatas(ArrayList<? extends BaseRes> infos)
	{
		m_itemInfos = infos;
	}

	public void SetIsAllShow(boolean isAllShow)
	{
		m_isAllShow = isAllShow;
	}

	public void setNumColumns(int numColumns)
	{
		m_numColumns = numColumns;
	}

	public void setThumbSize(int size)
	{
		m_thumbSize = size;
	}

	@Override
	public int getCount()
	{
		if(m_itemInfos == null)
		{
			return 0;
		}
		if(m_isAllShow)
		{
			return m_itemInfos.size();
		}
		else
		{
			if(m_itemInfos.size() < m_numColumns)
			{
				return m_itemInfos.size();
			}
			return m_numColumns;
		}
	}

	@Override
	public Object getItem(int position)
	{
		if(m_itemInfos == null)
			return null;
		return m_itemInfos.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(null == convertView)
		{
			convertView = new ImageView(m_context);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(m_thumbSize, m_thumbSize);
			convertView.setLayoutParams(params);
			((ImageView)convertView).setScaleType(ImageView.ScaleType.FIT_XY);
		}
		convertView.setTag(m_itemInfos.get(position).m_id);
		loadThumb(position, convertView);
		return convertView;
	}

	public void loadThumb(int position, final View view)
	{
		if(view == null)
			return;
		BaseRes res = m_itemInfos.get(position);
		if(res == null)
			return;
		if(m_loader == null)
			return;
		if(res instanceof FrameExRes)
		{
			view.setBackgroundDrawable(new BitmapDrawable(ImageUtils.MakeColorRoundBmp(0x14000000, m_thumbSize, m_thumbSize, ShareData.PxToDpi_xhdpi(10))));
//			view.setBackgroundColor(0x14000000);
		}
		else
		{
			view.setBackgroundColor(0);
		}
		String key = res.m_id + res.m_name;
		MyImageLoader.LoadItem item = new MyImageLoader.LoadItem(key, res);
		m_loader.SetMaxLoadCount(50);
		Bitmap bmp = m_loader.loadBmp(item, new MyImageLoader.ImageLoadCallback()
		{
			@Override
			public void onLoadFinished(Bitmap bmp, Object res)
			{
				if(bmp != null && res != null && (Integer)view.getTag() == ((BaseRes)res).m_id && !bmp.isRecycled())
				{
					((ImageView)view).setImageBitmap(bmp);
				}
			}

			@Override
			public Bitmap makeBmp(Object res)
			{
				if(res != null)
				{
					return MyImageLoader.MakeBmp(m_context, ((BaseRes)res).m_thumb, m_thumbSize, m_thumbSize, ShareData.PxToDpi_xhdpi(10));
				}
				return null;
			}
		});
		((ImageView)view).setImageBitmap(bmp);
	}

	public void releaseMem() {
		if(m_loader != null){
			m_loader.releaseMem(true);
		}
	}

	public void ClearAll()
	{
		if(m_itemInfos != null)
		{
			m_itemInfos.clear();
		}
		m_loader.releaseMem(true);
		m_loader = null;
	}
}
