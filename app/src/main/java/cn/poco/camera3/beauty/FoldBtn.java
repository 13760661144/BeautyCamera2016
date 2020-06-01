package cn.poco.camera3.beauty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2018-02-01.
 */

public class FoldBtn extends FrameLayout
{
    public FoldBtn(@NonNull Context context)
    {
        super(context);
        initUI();
    }

    private void  initUI()
    {
        ImageView icon = new ImageView(getContext());
        icon.setImageResource(R.drawable.ic_shape_fold_arrow);
        LayoutParams params  = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        this.addView(icon, params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.setAlpha(0.4f);
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            this.setAlpha(1f);
        }
        return super.onTouchEvent(event);
    }
}
