package cn.poco.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;

public class ElasticHorizontalScrollView extends HorizontalScrollView {
    // 拖动的距离 size = 4 的意思 只允许拖动屏幕的1/4
    private final int size = 2;
    private View childView;
    private float dx;

    private Rect normal = new Rect();

    public ElasticHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ElasticHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ElasticHorizontalScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            childView = getChildAt(0);
        }
    }

    /**
     * 将scrollView包含的唯一的一个子布局的view添加进来
     */
    public void onFinishAddView(View view) {
        childView = view;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (childView == null) {
            return super.onTouchEvent(ev);
        } else {
            onChildTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    public void onChildTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                dx = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                final float preX = dx;
                float nowX = ev.getX();
                int deltaX = (int) (preX - nowX) / size;
                dx = nowX;
                if (isNeedMove()) {
                    if (normal.isEmpty()) {
                        normal.set(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
                        return;
                    }
                    int xx = childView.getLeft() - deltaX;
                    childView.layout(xx, childView.getTop(), childView.getRight() - deltaX, childView.getBottom());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isNeedAnimation()) {
                    TranAnimation();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 是否需要移动布局
     */
    public boolean isNeedMove() {
        int offset = childView.getMeasuredWidth() - getWidth();
        if (offset <= 0) {
            return false;
        }
        int scrollX = getScrollX();
        return scrollX == 0 || scrollX == offset;
    }

    /**
     * 是否需要开启动画
     */
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    /**
     * 开启动画移动
     */
    public void TranAnimation() {
        TranslateAnimation ta = new TranslateAnimation(childView.getLeft(), normal.left, 0, 0);
        ta.setDuration(200);

        childView.startAnimation(ta);
        // 设置回到正常的布局位置
        childView.layout(normal.left, normal.top, normal.right, normal.bottom);
        normal.setEmpty();
    }
}
