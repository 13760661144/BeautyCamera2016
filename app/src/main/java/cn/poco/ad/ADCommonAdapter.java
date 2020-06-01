package cn.poco.ad;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.tianutils.ShareData;

public class ADCommonAdapter extends RecyclerView.Adapter{

    private Context m_context;
    private ArrayList<ADBaseItemData> m_ress;
    private OnClickCB m_cb;
    public ADCommonAdapter(Context context, ArrayList<ADBaseItemData> ress,OnClickCB cb)
    {
        m_context = context;
        m_ress = ress;
        m_cb = cb;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case 0:
                ADItem1 item1 = new ADItem1(m_context);
                RecyclerView.LayoutParams rl = new RecyclerView.LayoutParams(ShareData.PxToDpi_xhdpi(148),ShareData.PxToDpi_xhdpi(148));
                item1.setLayoutParams(rl);
                item1.setOnClickListener(mOnclickListener);
                viewHolder = new ADItem1ViewHolder(item1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {
            case 0:
                ADItem1ViewHolder viewHolder = (ADItem1ViewHolder) holder;
                ADItem1 view = (ADItem1) viewHolder.itemView;
                ADBaseItemData data = m_ress.get(position);
                data.m_index = position;
                view.setTag(data);
                view.SetData(m_ress.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return m_ress.size();
    }

    @Override
    public int getItemViewType(int position) {
        int out = 0;
        switch (m_ress.get(position).m_type)
        {
            case 0:
                out = 0;
                break;
        }
        return out;
    }

    public void SetSelectByUri(int uri)
    {
        if(m_ress != null && m_ress.size() > 0)
        {
            for(int i = 0; i < m_ress.size(); i++)
            {
                ADBaseItemData data = m_ress.get(i);
                if(data.m_id == uri)
                {
                    data.m_isSelect = true;
                }
                else
                {
                    data.m_isSelect = false;
                }
            }
        }
    }


    View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(m_cb != null)
            {
                if(v.getTag() instanceof ADBaseItemData)
                {
                    ADBaseItemData data = (ADBaseItemData) v.getTag();
                    m_cb.clickItem(data,data.m_index);
                }
            }
        }
    };

    private class ADItem1ViewHolder extends RecyclerView.ViewHolder
    {

        public ADItem1ViewHolder(View itemView) {
            super(itemView);
        }
    }


    public interface OnClickCB
    {
        public void clickItem(ADCommonAdapter.ADBaseItemData data,int index);
    }

    public static class ADItem1 extends ItemBase
    {
        private ADItem1Data m_data;
        private MyView m_view;
        public ADItem1(Context context) {
            super(context);
            initUI();
        }

        private void initUI()
        {
            m_view = new MyView(getContext());
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            m_view.setLayoutParams(fl);
            this.addView(m_view);
        }

        @Override
        public void SetData(ADBaseItemData data) {
            m_data = (ADItem1Data) data;
            if(m_view != null)
            {
                m_view.invalidate();
            }
        }

        protected PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);


        private Bitmap decodeBitmap(Object res)
        {
            Bitmap out = null;
            if(res != null)
            {
                if(res instanceof String)
                {
                    out = BitmapFactory.decodeFile((String) res);
                }
                else if(res instanceof Integer)
                {
                    out = BitmapFactory.decodeResource(getResources(), (Integer) res);
                }
            }
            return out;
        }

        private class MyView extends View
        {

            public MyView(Context context) {
                super(context);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                int width = this.getWidth();
                int height = this.getHeight();
                canvas.save();
                canvas.setDrawFilter(temp_filter);
                if(m_data != null)
                {
                    if(m_data.m_isSelect == true && m_data.m_bkColor != 0)
                    {
                        canvas.drawColor(m_data.m_bkColor);
                    }

                    if(m_data.m_res != null)
                    {
                        Bitmap bmp = decodeBitmap(m_data.m_res);
                        if(bmp != null)
                        {
                            int x = (int) ((width - m_data.m_imgShowWidth)/2f);
                            int y = (int) ((height - m_data.m_imgShowHeight)/2f);
                            Rect rect = new Rect(x,y,x + m_data.m_imgShowWidth,y + m_data.m_imgShowHeight);
                            canvas.drawBitmap(bmp,null,rect,null);
                        }
                    }
                }
                canvas.restore();
            }
        }

        public static class ADItem1Data extends ADBaseItemData
        {
            int m_imgShowWidth;
            int m_imgShowHeight;
            int m_bkColor;
        }
    }


    private static abstract class ItemBase extends FrameLayout
    {
        public ItemBase(Context context) {
            super(context);
        }

        public abstract void SetData(ADBaseItemData data);

    }

    public static class ADBaseItemData
    {
        int m_id;
        boolean m_isSelect;
        String m_name;
        Object m_res;
        Object m_ex;
        int m_type;
        int m_index;
    }
}
