package cn.poco.makeup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;


public class SeekBarTipsView extends FrameLayout {

    private CicleView m_cicle;
    private TextView m_text;
    public SeekBarTipsView(Context context) {
        super(context);
        initUI();
    }

    public SeekBarTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public SeekBarTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void  initUI()
    {
        m_cicle = new CicleView(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.CENTER;
        m_cicle.setLayoutParams(fl);
        this.addView(m_cicle);

        m_text = new TextView(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        m_text.setLayoutParams(fl);
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13f);
        m_text.setTextColor(Color.BLACK);
        this.addView(m_text);
    }

    public void setText(String text)
    {
        m_text.setText(text);
    }


    private class CicleView extends View
    {

        public CicleView(Context context) {
            super(context);
        }

        public CicleView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CicleView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            int cx = (int) (this.getWidth()/2f);
            int cy = (int) (this.getHeight()/2f);
            int radio = ShareData.PxToDpi_xhdpi(110)/2;
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setShadowLayer(ShareData.PxToDpi_xhdpi(3), 0, ShareData.PxToDpi_xhdpi(3),0x88888888);
            canvas.drawCircle(cx,cy,radio,paint);
        }
    }

}
