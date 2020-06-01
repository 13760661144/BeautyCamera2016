package cn.poco.login;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import cn.poco.tianutils.ShareData;

public class DashView extends View{
    protected Paint dashLinePaint;
    protected PathEffect dashLineEffect;
    private int dashLineWidth = ShareData.PxToDpi_xhdpi(3);
    private int dashLineColor = Color.BLACK;
    public DashView(Context context) {
        super(context);
        initilizeView();
    }

    public DashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initilizeView();
    }

    public DashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initilizeView();
    }

    private void initilizeView(){
        dashLinePaint = new Paint();
        dashLinePaint.setAntiAlias(true);
        dashLinePaint.setStyle(Paint.Style.STROKE);
        dashLinePaint.setStrokeWidth(dashLineWidth);
        dashLinePaint.setColor(dashLineColor);
        dashLineEffect = new DashPathEffect(new float[]{8,8},1);
        dashLinePaint.setPathEffect(dashLineEffect);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), dashLinePaint);
    }
}
