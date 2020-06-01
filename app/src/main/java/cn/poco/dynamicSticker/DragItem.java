package cn.poco.dynamicSticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by zwq on 2017/03/09 13:54.<br/><br/>
 * 拖动时显示的view
 */
public class DragItem {

    private View mDragView;

    public DragItem(Context context) {
        mDragView = new View(context);
    }

    public View getDragView() {
        return mDragView;
    }

    public void show() {
        mDragView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mDragView.setVisibility(View.GONE);
    }

    private void updateViewLocation(float x, float y) {
        mDragView.setX(x);
        mDragView.setY(y);
        mDragView.invalidate();
    }

    private void bindDragView(View srcView, View dragView) {
        Bitmap bitmap = Bitmap.createBitmap(srcView.getWidth(), srcView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        srcView.draw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            dragView.setBackground(new BitmapDrawable(srcView.getResources(), bitmap));
        } else {
            //noinspection deprecation
            dragView.setBackgroundDrawable(new BitmapDrawable(srcView.getResources(), bitmap));
        }
    }

    private void measureDragView(View srcView, View dragView) {
        dragView.setLayoutParams(new FrameLayout.LayoutParams(srcView.getMeasuredWidth(), srcView.getMeasuredHeight()));
        int widthSpec = View.MeasureSpec.makeMeasureSpec(srcView.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(srcView.getMeasuredHeight(), View.MeasureSpec.EXACTLY);
        dragView.measure(widthSpec, heightSpec);
    }

    public void startDrag(View srcView, float x, float y) {
        show();
        bindDragView(srcView, mDragView);
        measureDragView(srcView, mDragView);

        updateViewLocation(x, y);
    }

    public void dragging(float x, float y) {
        updateViewLocation(x, y);
    }

    public void stopDrag(float x, float y) {
        updateViewLocation(x, y);
        hide();
    }

    public void clearAll() {
        mDragView = null;
    }
}
