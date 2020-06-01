package cn.poco.makeup.makeup2;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.recycle.RecommendExAdapter;
import my.beautyCamera.R;

public class Makeup2SubItem extends BaseItem {

    private AbsExConfig m_config;
    private MakeupSubItemView m_view;
    private boolean m_isSelect;
    public Makeup2SubItem(@NonNull Context context,AbsExConfig config) {
        super(context);
        m_config = config;
        init();
    }

    private void init()
    {
        m_view = new MakeupSubItemView(getContext(),m_config);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_view.setLayoutParams(fl);
        this.addView(m_view);
    }

    @Override
    public void onSelected() {
        if(m_view != null)
        {
            m_isSelect = true;
            m_view.setSelect(true);
        }
    }

    @Override
    public void onUnSelected() {
        if(m_view != null)
        {
            m_isSelect = false;
            m_view.setSelect(false);
        }
    }

    @Override
    public void onClick() {

    }

    public void onAlphaMode()
    {
        if(m_view != null)
        {
            m_view.setAlphaMode(true);
        }
    }

    public void closeAlphaMode()
    {
        if(m_view != null)
        {
            m_view.setAlphaMode(false);
        }
    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        if(m_view != null)
        {
            RecommendExAdapter.ItemInfo data = (RecommendExAdapter.ItemInfo) info;
            m_view.SetBmpRes(data.m_logos[index]);
            m_view.SetName(data.m_names[index]);
            m_view.invalidate();
            if(((RecommendExAdapter.ItemInfo) info).m_ex instanceof MakeupSPage.Makeup2ResGroup)
            {
                MakeupSPage.Makeup2ResGroup temp = (MakeupSPage.Makeup2ResGroup) ((RecommendExAdapter.ItemInfo) info).m_ex;
                m_view.SetMaskColor(temp.m_maskColor);
            }
        }
    }

    public boolean getIsSelect()
    {
        return m_isSelect;
    }

    private class MakeupSubItemView extends View {
        private Bitmap m_bmp;
        private int m_maskColor;
        private Object m_res;
        private Makeup2ListConfig m_config;
        private String m_name;
        public boolean m_isSelect = false;

        public boolean m_isAlphaMode = false;


        public MakeupSubItemView(Context context,AbsExConfig config) {
            super(context);
            m_config = (Makeup2ListConfig) config;
        }

        public void SetBmpRes(Object res)
        {
            m_res = res;
        }

        public void SetName(String name)
        {
            m_name = name;
        }

        public void SetMaskColor(int color)
        {
            m_maskColor = color;
        }

        public void setSelect(boolean isSelect)
        {
            m_isSelect = isSelect;
            invalidate();
        }

        public void setAlphaMode(boolean flag)
        {
            m_isAlphaMode = flag;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(m_bmp == null)
            {
                m_bmp = decodeBmp(m_res);
            }
            if(m_bmp != null && m_bmp.getWidth() > 0 && m_bmp.getHeight() > 0)
            {
                //System.out.println("w:" + temp.getWidth() + " h:" + temp.getHeight());
                Bitmap mainBmp = makeHexxagonBmp();
                if(mainBmp != null)
                {
                    int x = (int) ((this.getWidth() - mainBmp.getWidth())/2f);
                    int y = (int) ((this.getHeight() - mainBmp.getHeight())/2f);
                    canvas.drawBitmap(mainBmp,x,y,null);
                }

                if(m_isSelect)
                {
                    Bitmap temp = drawMaskColorBmp();
                    if(temp != null)
                    {
                        int x = (int) ((this.getWidth() - temp.getWidth())/2f);
                        int y = (int) ((this.getHeight() - temp.getHeight())/2f);
                        canvas.drawBitmap(temp,x,y,null);
                    }


                    if(m_isAlphaMode)
                    {
                        if(m_config.m_alphaSelectBmp == null)
                        {
                            m_config.m_alphaSelectBmp = BitmapFactory.decodeResource(getResources(),R.drawable.makeup2_alpha_back_bmp);
                        }

                        if(m_config.m_alphaSelectBmp != null)
                        {
                            Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setFilterBitmap(true);
                            int tempW = ShareData.PxToDpi_xhdpi(50);
                            int tempH = ShareData.PxToDpi_xhdpi(50);
                            int x = (int) ((this.getWidth() - tempW)/2f);
//                            int y = (int) ((this.getHeight() - tempH)/2f) - ShareData.PxToDpi_xhdpi(10);
                            int y = ShareData.PxToDpi_xhdpi(30);
                            Rect rect = new Rect(x,y,x + tempW,y + tempH);
                            canvas.drawBitmap(m_config.m_alphaSelectBmp,null,rect,paint);
                        }
                    }
                    else
                    {
                        if(m_config.m_subItemSelBmp == null)
                        {
                            m_config.m_subItemSelBmp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_makeup2_sel_bmp);
                        }
                        if(m_config.m_subItemSelBmp != null)
                        {
                            Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setFilterBitmap(true);
                            int x = (int) ((this.getWidth() - m_config.m_subItemSelBmp.getWidth())/2f);
                            int y = (int) ((this.getHeight() - m_config.m_subItemSelBmp.getHeight())/2f) - ShareData.PxToDpi_xhdpi(8);
                            Rect rect = new Rect(x,y,x + m_config.m_subItemSelBmp.getWidth(),y + m_config.m_subItemSelBmp.getHeight());
                            canvas.drawBitmap(m_config.m_subItemSelBmp,null,rect,paint);
                        }
                    }

                    if(m_name != null && m_name.length() > 0)
                    {
                        Paint paint = new Paint();
                        paint.reset();
                        paint.setAntiAlias(true);
                        paint.setTypeface(Typeface.DEFAULT_BOLD);
                        paint.setTextSize(m_config.def_sub_title_sel_size);
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setColor(m_config.def_title_color_out);
                        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
                        int y = (this.getHeight() - (fmi.bottom - fmi.top)) / 2 - fmi.top + ShareData.PxToDpi_xhdpi(26);
//                        if(m_isAlphaMode)
//                        {
//                            y = ShareData.PxToDpi_xhdpi(76) - fmi.top;
//                        }
                        canvas.drawText(m_name, this.getWidth() / 2f, y, paint);
                    }
                }
                else
                {
//                    if(m_name != null && m_name.length() > 0)
//                    {
//                        Paint paint = new Paint();
//                        paint.reset();
//                        paint.setAntiAlias(true);
//                        paint.setTypeface(Typeface.DEFAULT_BOLD);
////                        m_config.def_sub_title_size
//                        paint.setTextSize(ShareData.PxToDpi_xhdpi(m_config.def_sub_title_size));
//                        paint.setTextAlign(Paint.Align.CENTER);
//                        paint.setColor(m_config.def_title_color_out);
//                        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
//                        canvas.drawText(m_name, this.getWidth() / 2f, (this.getHeight() - (fmi.bottom - fmi.top)) / 2 - fmi.top, paint);
//                    }
                }
            }
        }


        private Bitmap makeHexxagonBmp()
        {
            Bitmap out = Bitmap.createBitmap(m_config.def_sub_w, m_config.def_sub_h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(out);
            Paint p = new Paint();
            p.reset();
            p.setAntiAlias(true);
            p.setFilterBitmap(true);
            Matrix m = new Matrix();
            float s1 = (float)out.getWidth() / (float)m_bmp.getWidth();
            float s2 = (float)out.getHeight() / (float)m_bmp.getHeight();
            float s = s1 > s1 ? s1 : s2;
            m.postScale(s, s, m_bmp.getWidth() / 2f, m_bmp.getHeight() / 2f);
            m.postTranslate((out.getWidth() - m_bmp.getWidth()) / 2f, (out.getHeight() - m_bmp.getHeight()) / 2f);
            canvas.drawBitmap(m_bmp, m, p);
            m_bmp.recycle();
            m_bmp = null;

            p.reset();
            p.setAntiAlias(true);
            p.setFilterBitmap(true);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            m.reset();
            if(m_config.m_maskBmp != null && !m_config.m_maskBmp.isRecycled())
            {
//                int x = (int) ((m_config.def_sub_w - m_config.m_maskBmp.getWidth())/2f);
//                int y = (int) ((m_config.def_sub_h - m_config.m_maskBmp.getHeight())/2f);
//                int centerX = (int) (m_config.def_sub_w/2f);
//                int centerY = (int) (m_config.def_sub_h/2f);
//                float scaleX = m_config.m_maskBmp.getWidth()/m_config.def_sub_w*1.0f;
//                float scaleY = m_config.m_maskBmp.getHeight()/m_config.def_sub_h*1.0f;
//                canvas.drawBitmap(m_config.m_maskBmp, m, p);
                canvas.drawBitmap(m_config.m_maskBmp,null,new Rect(0,0,m_config.def_sub_w,m_config.def_sub_h),p);
            }
            return out;
        }


        private Bitmap drawMaskColorBmp()
        {
            if(m_config.m_subItemColorBmp != null && m_config.m_maskBmp != null)
            {
                Canvas canvas = new Canvas(m_config.m_subItemColorBmp);
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                canvas.drawColor((m_maskColor & 0x00FFFFFF) | 0xEF000000);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//                Matrix matrix = new Matrix();
//                int centerX = (int) (m_config.def_sub_w/2f);
//                int centerY = (int) (m_config.def_sub_h/2f);
//                int x = (int) ((m_config.def_sub_w - m_config.m_maskBmp.getWidth())/2f);
//                int y = (int) ((m_config.def_sub_h - m_config.m_maskBmp.getHeight())/2f);
//                float scaleX = m_config.def_sub_w*1f/m_config.m_maskBmp.getWidth()*1f;
//                float scaleY = m_config.def_sub_h*1f/m_config.m_maskBmp.getHeight()*1f;
//                matrix.postTranslate(x,y);
//                matrix.postScale(scaleX,scaleY,centerX,centerY);
//                canvas.drawBitmap(m_config.m_maskBmp,matrix, paint);

                RectF rectF = new RectF(0,0, m_config.def_sub_w,m_config.def_sub_h);
                canvas.drawBitmap(m_config.m_maskBmp,null,rectF,paint);
            }
            return  m_config.m_subItemColorBmp;
        }

        private Bitmap decodeBmp(Object res)
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
            return  out;
        }
    }
}
