package cn.poco.makeup.makeup1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;


public class Makeup1NullItem extends BaseItem {
    private NullView m_view;
    private AbsConfig m_config;
    public Makeup1NullItem(Context context, AbsConfig config) {
        super(context);
        m_config = config;
        initUI();
    }

    protected void initUI()
    {
        m_view = new NullView(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_view.setLayoutParams(fl);
        this.addView(m_view);
    }


    @Override
    public void onSelected() {
        m_view.setIsSelect(true);
        m_view.invalidate();
    }

    @Override
    public void onUnSelected() {
        m_view.setIsSelect(false);
        m_view.invalidate();
    }

    @Override
    public void onClick() {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        Makeup1ExAdapter.NullItemInfo itemInfo = (Makeup1ExAdapter.NullItemInfo) info;
        m_view.setItemInfo(itemInfo);
        m_view.setBgColor(itemInfo.m_maskColor);
    }


    private class NullView extends View
    {
        private Makeup1ExAdapter.NullItemInfo m_itemInfo;
        private int m_bkColor;
        private boolean m_isSelect;
        public NullView(Context context) {
            super(context);
        }

        public void setItemInfo(Makeup1ExAdapter.NullItemInfo itemInfo)
        {
            m_itemInfo = itemInfo;
        }

        public void setBgColor(int bgColor)
        {
            m_bkColor = bgColor;
        }

        public void setIsSelect(boolean flag)
        {
            m_isSelect = flag;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final Paint paint = new Paint();
            final int item_w = m_config.def_item_w;
            final int item_h = m_config.def_item_h;
            final int item_l = m_config.def_item_l;

            //画背景
            DrawBk(canvas, m_bkColor);
            //画图片
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),(int)m_itemInfo.m_logos[0]);

            if(bmp != null)
            {
                paint.reset();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                int x = (int)((item_w - ShareData.PxToDpi_xhdpi(52)) / 2f);
                int y = (int)((item_h - ShareData.PxToDpi_xhdpi(52)) / 2f);
                RectF rectF = new RectF(x, y, x + ShareData.PxToDpi_xhdpi(52), y + ShareData.PxToDpi_xhdpi(52));
                canvas.drawBitmap(bmp, null, rectF, paint);
            }


            if(m_isSelect)
            {
                paint.reset();
                paint.setAntiAlias(true);
                paint.setColor(0xffe75988);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(ShareData.PxToDpi_xhdpi(4));
                RectF rectF = new RectF(ShareData.PxToDpi_xhdpi(2), ShareData.PxToDpi_xhdpi(2), item_w - ShareData.PxToDpi_xhdpi(2), item_h - ShareData.PxToDpi_xhdpi(2));

                Bitmap tempBmp = Bitmap.createBitmap(item_w, item_h, Bitmap.Config.ARGB_8888);
                Canvas tempCanvas = new Canvas(tempBmp);
                tempCanvas.drawRect(rectF, paint);
                cn.poco.advanced.ImageUtils.AddSkin(getContext(), tempBmp);
                canvas.drawBitmap(tempBmp, 0, 0, null);
                if(tempBmp != null)
                {
                    tempBmp.recycle();
                    tempBmp = null;
                }
            }
        }

        protected void DrawBk(Canvas canvas, int color)
        {
            final AbsConfig config = m_config;
            final Paint paint = new Paint();
            final int item_w = config.def_item_w;
            final int item_h = config.def_item_h;

            paint.reset();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawRect(0, 0, item_w, item_h, paint);
        }
    }
}
