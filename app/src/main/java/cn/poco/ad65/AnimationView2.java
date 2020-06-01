package cn.poco.ad65;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;

import cn.poco.tianutils.AnimationView;
import cn.poco.tianutils.ShareData;

public class AnimationView2 extends AnimationView {
    public AnimationView2(Context context) {
        super(context);
    }


    private Matrix temp_matrix2 = new Matrix();
    @Override
    protected void onDraw(Canvas canvas) {
        //拉满宽度显示
        if(m_bmp != null && !m_bmp.isRecycled())
        {
            float scale = ShareData.m_screenWidth*1.0f/m_bmp.getWidth()*1.0f;
            temp_matrix2.reset();
			temp_matrix2.postScale(scale, scale);
			temp_matrix2.postTranslate(0, 0);
			canvas.drawBitmap(m_bmp, temp_matrix2, null);
        }
    }
}
