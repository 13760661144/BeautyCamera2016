package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.poco.resource.BaseRes;
import cn.poco.resource.ThemeRes;
import cn.poco.tianutils.ShareData;

/**
 * Created by admin on 2016/5/23.
 */
public class ThemeListAdapter extends BaseAdapter {
    private ArrayList<BaseItemInfo> m_infos;
    private Context m_context;
    private int m_topHeight;
    private MyImageLoader m_loader;
    private int mViewWidth;
    private int mViewHeight;

    public ThemeListAdapter(Context context) {
        m_context = context;

        m_topHeight = ShareData.PxToDpi_xhdpi(20);
        m_loader = new MyImageLoader();
        mViewWidth = ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(40);
        mViewHeight = (int) (mViewWidth * 271f / 678f);
        if (ShareData.m_screenHeight < 856) {
            float scale = 678f / 720f;

            mViewWidth = (int) (ShareData.m_screenWidth * scale);
            mViewHeight = (int) (mViewWidth * 271f / 678f);
        }
        int count = ShareData.m_screenHeight / mViewHeight + 1;
        mViewHeight = (int)(mViewWidth * 251 / 678f + 0.5f);
        m_loader.SetMaxLoadCount(count);
    }

    public void setDatas(ArrayList<BaseItemInfo> infos) {
        m_infos = infos;
    }

    @Override
    public int getCount() {
        if (m_infos != null) {
            return m_infos.size() + 1;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (m_infos != null && position != 0) {
            return m_infos.get(position - 1);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            if (null == convertView) {
                AbsListView.LayoutParams al = new AbsListView.LayoutParams(ShareData.m_screenWidth, m_topHeight);
                convertView = new ImageView(m_context);
                convertView.setLayoutParams(al);
            }
        } else {
            if (convertView == null) {
                convertView = new ThemeItem(m_context);
            }
            BaseItemInfo info = m_infos.get(position - 1);
            convertView.setTag(info.m_themeRes.m_id);
            ((ThemeItem) convertView).setItemInfo(info);
            ((ThemeItem) convertView).updateSkin();
            loadThumb(info.m_themeRes, convertView);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void loadThumb(BaseRes res, final View view) {
        if (view == null)
            return;
        if (res == null)
            return;
        if(m_loader == null)
            return;
        String key = "theme:" + res.m_id;
        MyImageLoader.LoadItem item = new MyImageLoader.LoadItem(key, res);
        Bitmap bmp = m_loader.loadThemeBmp(item, new MyImageLoader.ImageLoadCallback() {
            @Override
            public void onLoadFinished(Bitmap bmp, Object res) {
                if ((Integer) view.getTag() == ((BaseRes) res).m_id) {
                    if (bmp == null || !bmp.isRecycled()) {
                        ((ThemeItem) view).setThemeBmp(bmp);
                    }
                }
            }

            @Override
            public Bitmap makeBmp(Object res) {
                if (res != null) {
                    return MyImageLoader.MakeBmp(m_context, ((ThemeRes) res).m_thumb, mViewWidth, mViewHeight, ShareData.PxToDpi_xhdpi(10));
                }
                return null;
            }
        });
        ((ThemeItem) view).setThemeBmp(bmp);
    }

    public void releaseMem() {
       if(m_loader != null){
           m_loader.releaseMem(false);
       }
    }

    public void ClearAll()
    {
        m_loader.releaseMem(true);
        m_loader = null;
    }
}
