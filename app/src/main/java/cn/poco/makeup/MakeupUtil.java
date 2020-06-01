package cn.poco.makeup;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import my.beautyCamera.R;

public class MakeupUtil {

    //彩妆页面把图片生成六边形形状
    public static Bitmap makeHexagonBmp(int outWidth, int outHeight, Bitmap bmp, Context context)
    {
        if(bmp != null)
        {
            Bitmap out = null;
            out = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(out);
            Paint p = new Paint();
            p.reset();
            p.setAntiAlias(true);
            p.setFilterBitmap(true);
            Matrix m = new Matrix();
            float s1 = (float)out.getWidth() / (float) bmp.getWidth();
            float s2 = (float)out.getHeight() / (float)bmp.getHeight();
            float s = s1 > s2 ? s1 : s2;
            m.postScale(s, s, bmp.getWidth() / 2f, bmp.getHeight() / 2f);
            m.postTranslate((out.getWidth() - bmp.getWidth()) / 2f, (out.getHeight() - bmp.getHeight()) / 2f);
            canvas.drawBitmap(bmp, m, p);
            bmp.recycle();
            bmp = null;

            p.reset();
            p.setAntiAlias(true);
            p.setFilterBitmap(true);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            m.reset();
            Bitmap m_maskBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.photofactory_makeup_item_sub_mask);
            float s10 = out.getWidth()*1.0f/m_maskBmp.getWidth()*1.0f;
            float s11 = out.getHeight()*1.0f/m_maskBmp.getHeight()*1.0f;
            float sfinal = s10 > s11? s10:s11;
            m.postTranslate((out.getWidth() - m_maskBmp.getWidth())/2f,(out.getHeight() - m_maskBmp.getHeight())/2f);
            m.postScale(sfinal,sfinal,out.getWidth()/2f,out.getHeight()/2f);
            canvas.drawBitmap(m_maskBmp, m, p);
            return out;
        }
        return null;
    }
}
