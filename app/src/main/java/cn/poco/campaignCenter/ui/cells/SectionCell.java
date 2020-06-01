package cn.poco.campaignCenter.ui.cells;

import android.content.Context;
import android.widget.FrameLayout;

/**
 * Created by admin on 2016/10/13.
 */

public class SectionCell extends FrameLayout{

    public SectionCell(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    public void setSectionCellBackgroundColor(int color) {
        this.setBackgroundColor(color);
    }

}
