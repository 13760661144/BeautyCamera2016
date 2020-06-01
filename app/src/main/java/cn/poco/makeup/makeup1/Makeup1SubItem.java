package cn.poco.makeup.makeup1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import cn.poco.makeup.MakeupUtil;
import cn.poco.makeup.makeup_abs.BaseItemWithAlphaFrMode;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;


public class Makeup1SubItem extends BaseItemWithAlphaFrMode {

    private SubItemView m_view;
    private Makeup1ListConfig m_config;
    public Makeup1SubItem(@NonNull Context context,Makeup1ListConfig config) {
        super(context);
        m_config = config;
        initUI();
    }

    private void initUI()
    {
        m_view = new SubItemView(getContext(),m_config);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_view.setLayoutParams(fl);
        this.addView(m_view);
    }

    @Override
    public void onOpenAlphaFr() {
        m_view.setIsAlphaMode(true);
    }

    @Override
    public void onCloseAlphaFr() {
        m_view.setIsAlphaMode(false);
    }

    @Override
    public void onSelected() {
        super.onSelected();
        m_view.setIsSelect(true);
    }

    @Override
    public void onUnSelected() {
        super.onUnSelected();
        m_view.setIsSelect(false);
    }


    @Override
    public void onClick() {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        if(m_view != null && info != null)
        {
            Makeup1ExAdapter.ItemInfo temp = (Makeup1ExAdapter.ItemInfo) info;
            m_view.setRes(temp.m_logos[index]);
            m_view.setMaskColor(temp.m_maskColor);
            m_view.setName(temp.m_names[index]);
        }
    }

    public static class SubItemView extends View
    {
        private int def_sub_title_size = ShareData.PxToDpi_xhdpi(30);
        private int def_title_color_out = 0xFFFFFFFF;//title文字的颜色
        private Bitmap m_hexagonBmp;
        private Bitmap m_maskBmp;
        private Object m_res;
        private boolean m_isSelect;
        private boolean m_isAlphaMode;
        private String m_name;
        private int m_maskColor;
        private Makeup1ListConfig m_config;
        public SubItemView(Context context,Makeup1ListConfig config) {
            super(context);
            m_config = config;
        }

        public void setRes(Object res)
        {
            m_res = res;
        }

        public void setName(String name)
        {
            m_name = name;
        }

        public void setMaskColor(int color)
        {
            m_maskColor = color;
        }

        public void setIsSelect(boolean isSelect)
        {
            m_isSelect = isSelect;
            invalidate();
        }

        public void setIsAlphaMode(boolean isAlphaMode)
        {
            m_isAlphaMode = isAlphaMode;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(m_hexagonBmp == null)
            {
                Bitmap temp = null;
                if(m_res instanceof Integer)
                {
                   temp = BitmapFactory.decodeResource(getResources(), (Integer) m_res);
                }
                else if(m_res instanceof String)
                {
                    temp = BitmapFactory.decodeFile((String) m_res);
                }
                if(temp != null)
                {
                    m_hexagonBmp = MakeupUtil.makeHexagonBmp(m_config.def_sub_w,m_config.def_sub_h,temp,getContext());
                }
            }

            if(m_hexagonBmp != null)
            {
                canvas.drawBitmap(m_hexagonBmp,0,0,null);
            }

            if(m_isSelect)
            {
                if(m_maskBmp == null)
                {
                    Bitmap bmp = Bitmap.createBitmap(m_config.def_sub_w,m_config.def_sub_h, Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(bmp);
                    canvas1.drawColor(m_maskColor);
                    m_maskBmp = MakeupUtil.makeHexagonBmp(m_config.def_sub_w,m_config.def_sub_h,bmp,getContext());
                    bmp = null;
                }
                canvas.drawBitmap(m_maskBmp,0,0,null);

                if(m_isAlphaMode)
                {
                    int centerX = (int) (m_config.def_sub_w/2f);
                    int centerY = (int) (m_config.def_sub_w/2f - ShareData.PxToDpi_xhdpi(8));

                    Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.filter_selected_back_icon);

                    canvas.drawBitmap(icon,centerX - ShareData.PxToDpi_xhdpi(25),centerY - ShareData.PxToDpi_xhdpi(25),null);

                    Paint paint = new Paint();
                    paint.reset();
                    paint.setAntiAlias(true);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                    paint.setTextSize(ShareData.PxToDpi_xhdpi(21));
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(def_title_color_out);
                    Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
                    canvas.drawText(m_name, m_config.def_sub_w/2f, (m_config.def_sub_h - (fmi.bottom - fmi.top)) - fmi.top - ShareData.PxToDpi_xhdpi(18), paint);
                }
                else
                {
                    //画勾
                   Bitmap subItemSelBmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_makeup_item_sub_sel);
                      Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setFilterBitmap(true);
                        canvas.drawBitmap(subItemSelBmp,(m_config.def_sub_w - subItemSelBmp.getWidth()) / 2, (m_config.def_sub_h - subItemSelBmp.getHeight()) / 2, paint);
                }
            }
            else
            {
                if(m_name != null && m_name.length() > 0)
                {
                    Paint paint = new Paint();
                    paint.reset();
                    paint.setAntiAlias(true);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                    paint.setTextSize(def_sub_title_size);
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(def_title_color_out);
                    Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
                    canvas.drawText(m_name, m_config.def_sub_w / 2, (m_config.def_sub_h - (fmi.bottom - fmi.top)) / 2 - fmi.top, paint);
                }
            }
        }
    }
}
