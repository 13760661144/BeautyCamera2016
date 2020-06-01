package cn.poco.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;


/**
 * 彩妆用到的SeekBar
 */
public class MySeekBar extends View {
    private int m_width = 0;
    private int m_height = 0;
    private int m_ciclePaintWidth = ShareData.PxToDpi_xhdpi(2);
    private int m_radio = ShareData.PxToDpi_xhdpi(25);
    private Paint m_paint;
    private int m_maxProgress = 100;
    private float m_ratio = 1f;
    private int m_maxMoveDistan = 0;
    private int m_paintWidth = 0;
    private int m_backgroundColor = 0x57000000;
    private OnProgressChangeListener m_listener;
    private boolean m_uiEnabled = true;
    private int m_curProgress = 100;
    public MySeekBar(Context context) {
        super(context);
        initData();
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData()
    {
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        m_paintWidth = ShareData.PxToDpi_xhdpi(2);
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener)
    {
        this.m_listener = listener;
    }

    private boolean m_once;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
       if(changed && !m_once)
       {
           m_once = true;
           m_width = this.getWidth();
           m_height = this.getHeight();
       }
    }


    //设置圆的半径
    public void setCicleRadio(int radio)
    {
        this.m_radio = radio;
    }

    //当前圆的位置
    public int getCurCiclePos()
    {
        return (int) (getMaxDistans()*m_ratio) + m_radio;
    }



    public void setBackgroundColor(int color)
    {
        m_backgroundColor = color;
    }


    public void setUiEnable(boolean flag)
    {
        m_uiEnabled = flag;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        m_maxMoveDistan = m_width - m_radio*2;
        m_paint.setColor(0xffe75988);
        m_paint.setStyle(Paint.Style.STROKE);
        m_paint.setStrokeWidth(m_ciclePaintWidth);

        Bitmap cicleBmp = Bitmap.createBitmap(m_radio*2,m_radio*2, Bitmap.Config.ARGB_8888);
        Canvas cicleCanvas = new Canvas(cicleBmp);
        cicleCanvas.drawCircle(m_radio,m_radio,m_radio - m_ciclePaintWidth,m_paint);
        ImageUtils.AddSkin(getContext(),cicleBmp);

        int templeft = (int) Math.ceil(m_maxMoveDistan*m_ratio);
        int temptop = (m_height - m_radio*2)/2;
        canvas.drawBitmap(cicleBmp,templeft,temptop,null);
        if(cicleBmp != null)
        {
            cicleBmp.recycle();
            cicleBmp = null;
        }

        m_paint.setStyle(Paint.Style.FILL);
        m_paint.setStrokeWidth(ShareData.PxToDpi_xhdpi(4));
        if(templeft > 0)
        {
            Bitmap lineBmp = Bitmap.createBitmap(templeft,ShareData.PxToDpi_xhdpi(4), Bitmap.Config.ARGB_8888);
            Canvas lineCanvas = new Canvas(lineBmp);
            lineCanvas.drawRect(0,0,templeft,ShareData.PxToDpi_xhdpi(4),m_paint);
            ImageUtils.AddSkin(getContext(),lineBmp);
            canvas.drawBitmap(lineBmp,0,(m_height - ShareData.PxToDpi_xhdpi(4))/2f,null);
            if(lineBmp != null)
            {
                lineBmp.recycle();
                lineBmp = null;
            }
        }
        if(m_ratio != 1)
        {
            m_paint.setColor(m_backgroundColor);
            m_paint.setStrokeWidth(ShareData.PxToDpi_xhdpi(2));
            canvas.drawLine(templeft + m_radio*2,m_height/2,m_width,m_height/2,m_paint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(m_uiEnabled)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    m_ratio = getRetio((int) event.getX());
                    m_curProgress = (int) (m_ratio*m_maxProgress);
                    if(m_listener != null)
                    {
                        m_listener.onStartTrackingTouch(this);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    m_ratio = getRetio((int) event.getX());
                    m_curProgress = (int) (m_ratio*m_maxProgress);
                    if(m_listener != null)
                    {
                        m_listener.onProgressChanged(this,getProgress());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(m_listener != null)
                    {
                        m_listener.onStopTrackingTouch(this);
                    }
                    break;
            }

            this.invalidate();
        }

        return true;
    }

    private float getRetio(int x)
    {
        float out = 0f;
        out = (x - m_radio)/(m_maxMoveDistan + 0.0f);
        if(out < 0)
        {
            out = 0f;
        }
        else if(out > 1)
        {
            out = 1f;
        }
        return out;
    }

    public void setMax(int max)
    {
        m_maxProgress = max;
    }

    public void setProgress(int progress)
    {
        if(m_curProgress != progress)
        {
            m_curProgress = progress;
            float temp = 0f;
            temp = progress/(m_maxProgress + 0.0f);
            m_ratio = temp;
            this.invalidate();
        }
    }


    public int getProgress()
    {
        int out = 100;
        out = (int) (m_maxProgress*m_ratio);
        return out;
    }

    public int getMaxDistans()
    {
        return m_maxMoveDistan;
    }


    public interface OnProgressChangeListener {

        void onProgressChanged(MySeekBar seekBar, int progress);

        void onStartTrackingTouch(MySeekBar seekBar);

        void onStopTrackingTouch(MySeekBar seekBar);
    }


}
