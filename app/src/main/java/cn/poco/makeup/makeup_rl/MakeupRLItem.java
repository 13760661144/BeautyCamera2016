package cn.poco.makeup.makeup_rl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.makeup.makeup_abs.BaseItemWithAlphaFrMode;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;


public class MakeupRLItem extends BaseItemWithAlphaFrMode {

    private MakeupRLItemView m_img;
    private MakeupRLListConfig m_config;
    private boolean m_isSelect;
    private MakeupRLAdapter.ItemInfo m_itemInfo;
    public float def_title_size = 9f;

    public int def_title_color_out = 0xFFFFFFFF;
    public int def_title_color_over = 0xFF19B593;
    public MakeupRLItem(@NonNull Context context, AbsConfig config) {
        super(context);
        m_config = (MakeupRLListConfig) config;
        initUI();
    }

    private void initUI()
    {
        m_img = new MakeupRLItemView(getContext());
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_img.setLayoutParams(fl);
        this.addView(m_img);
    }

    @Override
    public void onSelected() {
        super.onSelected();
        m_isSelect = true;
        m_img.setIsSelect(true);
    }

    @Override
    public void onUnSelected() {
        super.onUnSelected();
        m_isSelect = false;
        m_img.setIsSelect(false);
    }

    public boolean isSelect()
    {
        return m_isSelect;
    }

    @Override
    public void onClick() {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        m_itemInfo = (MakeupRLAdapter.ItemInfo)info;
    }

    @Override
    public void onOpenAlphaFr() {
        m_img.setIsAlphaMode(true);
    }

    @Override
    public void onCloseAlphaFr() {
        m_img.setIsAlphaMode(false);
    }



    private class MakeupRLItemView extends View {
        private boolean m_isSelected;
        private boolean m_isAlphaMode;

        public int def_title_color_over = 0xFF606060; //title文字的颜色
        public int def_title_size = ShareData.PxToDpi_xhdpi(20);
        public MakeupRLItemView(Context context) {
            super(context);
        }


        public void setIsSelect(boolean isSelect)
        {
            m_isSelected = isSelect;
            invalidate();
        }

        public void setIsAlphaMode(boolean isAlphaMode)
        {
            m_isAlphaMode = isAlphaMode;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            if(m_itemInfo != null)
            {
                int ViewWidth = getWidth();
                int ViewHeight = getHeight();
                String name = m_itemInfo.m_name;
                int res = (int) m_itemInfo.m_logo;
                int maskColor = m_itemInfo.m_maskColor;
                canvas.drawColor(Color.WHITE);
                if(!m_isSelected)
                {
                    if(res != 0)
                    {
                        Bitmap bmp = BitmapFactory.decodeResource(getResources(),res);
                        int width = this.getWidth();
                        Rect rect = new Rect(0,0,width,width);
                        canvas.drawBitmap(bmp,null,rect,null);
                    }
                }
                else
                {
                    if(maskColor != 0)
                    {
                        canvas.drawColor(maskColor);
                    }
                    else
                    {
                        canvas.drawColor(ImageUtils.GetSkinColor());
                    }

                    if(!m_isAlphaMode)
                    {
                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_makeup_select_icon);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        if(bmp != null)
                        {
                            canvas.drawBitmap(bmp, (ViewWidth - bmp.getWidth())/2, (ViewHeight - bmp.getHeight())/2f, paint);
                        }
                    }
                    else
                    {
                        int centerX = (int) (ViewWidth/2f);
                        int centerY = (int) (ViewHeight/4f + ShareData.PxToDpi_xhdpi(10));

                        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_makeup_normal_back_icon);
                        canvas.drawBitmap(bmp,centerX - (bmp.getWidth()/2f), centerY - bmp.getHeight()/2f,null);
                    }
                }

                if(name != null && name.length() > 0)
                {
                    if(!m_isAlphaMode)
                    {
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setTextSize(def_title_size);
                        if(m_isSelected)
                        {
                            paint.setColor(def_title_color_out);
                        }
                        else
                        {
                            paint.setColor(def_title_color_over);
                        }

                        float w = paint.measureText(name);

                        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
                        int y = ViewWidth + ((ViewHeight - ViewWidth) - (fmi.bottom - fmi.top))/2 - fmi.top;
                        canvas.drawText(name, (ViewWidth - w) / 2, y, paint);
                    }
                    else
                    {
                        Paint paint = new Paint();
                        paint.reset();
                        paint.setAntiAlias(true);
                        paint.setTypeface(Typeface.DEFAULT_BOLD);
                        paint.setTextSize(def_title_size);
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setColor(Color.WHITE);
                        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
                        canvas.drawText(name, ViewWidth/2f, (ViewHeight - (fmi.bottom - fmi.top)) - fmi.top - ShareData.PxToDpi_xhdpi(20), paint);
                    }
                }
            }
        }
    }

}
