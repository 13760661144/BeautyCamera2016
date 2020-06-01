package cn.poco.makeup.makeup1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;


public class Makeup1DownItem extends BaseItem {
    private ImageView m_img;
    private Makeup1ListConfig m_config;
    protected static final int BLUR_COLOR = 0x20000000;
    protected static final int BLUR_R = 6;
    protected static final int OFFSET = 4;
    protected static final int SPACE_W = 10;
    public Makeup1DownItem(@NonNull Context context, AbsConfig config) {
        super(context);
        m_config = (Makeup1ListConfig) config;
        init();
    }

    protected void init()
    {
        m_img = new ImageView(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_img.setLayoutParams(fl);
        this.addView(m_img);
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onUnSelected() {

    }

    @Override
    public void onClick() {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index) {
        Makeup1ExAdapter.DownloadItemInfo2 itemInfo2 = (Makeup1ExAdapter.DownloadItemInfo2) info;
        Bitmap bmp = makeBmp(itemInfo2);
        m_img.setImageBitmap(bmp);
    }

    private Bitmap makeBmp(Makeup1ExAdapter.DownloadItemInfo2 info)
    {
        Bitmap out = null;
        if(m_config != null)
        {
            int item_w = m_config.def_item_w;
            int item_h = m_config.def_item_h;
            out = Bitmap.createBitmap(m_config.def_item_w,m_config.def_item_h, Bitmap.Config.ARGB_8888);
            if(info.m_logos != null && info.m_logos.length > 1)
            {
                Canvas canvas = new Canvas(out);

                //画图
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0x33ffffff);
                canvas.drawRect(0, 0, item_w, item_w, paint);
                //int len = m_logos.length;
                int len = info.m_logos.length - 1;
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
                    DrawSubBmp(canvas, info.m_logos[i + 1], ex, ey, subW, subW);
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
                canvas.drawRect(0, item_w, item_w, item_h, paint);
                //下载更多
                if(info.m_num != 0)
                {
                    Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_makeup_store_icon);
                    paint.reset();
                    paint.setAntiAlias(true);
                    paint.setFilterBitmap(true);
                    int x = ShareData.PxToDpi_xhdpi(6);
                    //int y = (item_h - (img_h + 2) - temp.getHeight()) / 2 + img_h + 2;
                    int y = (item_h - item_w - temp.getHeight()) / 2 + item_w;
                    temp = cn.poco.advanced.ImageUtils.AddSkin(getContext(), temp);
                    canvas.drawBitmap(temp, x, y, paint);
                    paint.setColor(cn.poco.advanced.ImageUtils.GetSkinColor(0xffe75988));
                    paint.setTextSize(ShareData.PxToDpi_xhdpi(15));

                    //y = img_h + 2 + ShareData.PxToDpi_xhdpi(20);
                    y = item_w + ShareData.PxToDpi_xhdpi(20);
                    //Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
                    //y = (int) ((item_h - img_h - ShareData.PxToDpi_xhdpi(2) - (fmi.bottom - fmi.top))/2f - fmi.top) + img_h;
                    String text = getContext().getResources().getString(R.string.makeup1downItem_downmore);
                    int textWidth = (int) paint.measureText(text);
                    x = (int) (ShareData.PxToDpi_xhdpi(32) + (item_w - ShareData.PxToDpi_xhdpi(36) - textWidth)/2f);
                    canvas.drawText(text, x, y, paint);
                    //下载个数
                    x = ShareData.PxToDpi_xhdpi(18);
                    y = item_w - ShareData.PxToDpi_xhdpi(4);
                    paint.reset();
                    paint.setAntiAlias(true);
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_makeup_downmore_num_icon);
                    cn.poco.advanced.ImageUtils.AddSkin(getContext(), temp, 0xffe75988);
                    canvas.drawBitmap(temp, x, y, paint);
                    paint.reset();
                    paint.setAntiAlias(true);
                    paint.setFilterBitmap(true);
                    paint.setColor(0xffffffff);
                    paint.setTextSize(ShareData.PxToDpi_xhdpi(12));
                    paint.setTextAlign(Paint.Align.CENTER);
                    Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                    canvas.drawText(Integer.toString(info.m_num), x + temp.getWidth() / 2f, y + temp.getHeight() / 2f + Math.abs(fontMetrics.ascent * 0.65f) / 2f, paint);
                }
            }
            else
            {
                //就画一个下载更多
                Canvas canvas = new Canvas(out);
                Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_download_logo);
                temp = cn.poco.advanced.ImageUtils.AddSkin(getContext(), temp);
                float x = (item_w - temp.getWidth()) / 2f;
                float y = (item_h - temp.getHeight()) / 2f;
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                canvas.drawBitmap(temp, x, y, paint);

                if(info.m_num != 0)
                {
//					if(config.m_downloadMoreBkBmp == null)
//					{
                    Bitmap downNumBmp = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_download_num_bk);
//					}
                    if(downNumBmp != null)
                    {
                        x += temp.getWidth() - downNumBmp.getWidth() * 1.15f;
                        downNumBmp = cn.poco.advanced.ImageUtils.AddSkin(getContext(), downNumBmp);
                        canvas.drawBitmap(downNumBmp, x, y, paint);
                        paint.reset();
                        paint.setAntiAlias(true);
                        paint.setFilterBitmap(true);
                        paint.setColor(0xFFFFFFFF);
                        paint.setTextSize(ShareData.PxToDpi_xhdpi(18));
                        paint.setTextAlign(Paint.Align.CENTER);
                        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                        canvas.drawText(Integer.toString(info.m_num), x + downNumBmp.getWidth() / 2f, y + downNumBmp.getHeight() / 2f + Math.abs(fontMetrics.ascent * 0.8f) / 2f, paint);
                    }
                }
            }
        }
        return out;
    }


    private void DrawSubBmp(Canvas canvas, Object res, float x, float y, int w, int h)
    {
        Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(this.getContext(), res, 0, -1, -1, -1);
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
}
