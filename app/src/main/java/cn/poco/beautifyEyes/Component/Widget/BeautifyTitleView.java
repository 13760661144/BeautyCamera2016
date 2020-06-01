package cn.poco.beautifyEyes.Component.Widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.poco.filterPendant.MyStatusButton;
import cn.poco.tianutils.ShareData;

/**
 * Created by Shine on 2017/2/8.
 */

public class BeautifyTitleView extends LinearLayout {
    public static class TitleInfo {
        public String mName;
        public int resId;

        public TitleInfo(String name, int res) {
            this.mName = name;
            this.resId = res;
        }
    }

    public interface BeautifyTitleViewDelegate {
        void onArrowClick(boolean downAnimation);

        void onBeautifyModeChange(int index);

        // 0为左边，1为右边
        void onSwipeDirection(int direction, int srcIndex, int dstIndex);
    }


    private int mIndex;
    private MyStatusButton mCurrentSelectBtn;
    private int mItemLeftMargin;
    private List<MyStatusButton> mStatusBtnList = new ArrayList<>();
    private BeautifyTitleViewDelegate mDelegate;

    public BeautifyTitleView(Context context, int index) {
        super(context);
        mIndex = index;
        initData();
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);
    }

    private void initData() {
        mItemLeftMargin = ShareData.PxToDpi_xhdpi(16);
    }

    public void setItem(List<TitleInfo> itemList) {
        for (TitleInfo item : itemList) {
            MyStatusButton statusButton = new MyStatusButton(this.getContext());
            statusButton.setData(item.resId, item.mName);
            boolean isSelect = mIndex == itemList.indexOf(item);
            statusButton.setTag(itemList.indexOf(item));
            statusButton.setBtnStatus(isSelect, false);
            mCurrentSelectBtn = isSelect ? statusButton : mCurrentSelectBtn;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(160), ViewGroup.LayoutParams.MATCH_PARENT);
            params.leftMargin = mItemLeftMargin;
            statusButton.setLayoutParams(params);
            this.addView(statusButton);
            mStatusBtnList.add(statusButton);
            statusButton.setOnClickListener(onClickListener);
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            MyStatusButton statusButton = (MyStatusButton) v;
            boolean currentArrowState;
            if (mCurrentSelectBtn == statusButton) {
                currentArrowState = mCurrentSelectBtn.isDown() ? false :true;
                for (MyStatusButton btn : mStatusBtnList) {
                    if (mCurrentSelectBtn == btn) {
                        btn.setBtnStatus(true, currentArrowState);
                    } else {
                        btn.setBtnStatus(false, currentArrowState);
                    }
                }

                if (mDelegate != null) {
                    mDelegate.onArrowClick(currentArrowState);
                }

            } else {
                int oldIndex = (Integer) mCurrentSelectBtn.getTag();
                mCurrentSelectBtn.setBtnStatus(false, mCurrentSelectBtn.isDown());
                mCurrentSelectBtn = statusButton;

                boolean oldState = mCurrentSelectBtn.isDown();
                currentArrowState = oldState ? !oldState : oldState;

                mIndex = (Integer) statusButton.getTag();

                for (MyStatusButton btn : mStatusBtnList) {
                    if (mCurrentSelectBtn == btn) {
                        btn.setBtnStatus(true, currentArrowState);
                    } else {
                        btn.setBtnStatus(false, currentArrowState);
                    }
                }

                if (oldState) {
                    if (mDelegate != null) {
                        boolean animation = !oldState;
                        mDelegate.onArrowClick(animation);
                    }
                }

                if (mDelegate != null) {
                    mDelegate.onBeautifyModeChange((Integer) statusButton.getTag());
                    if (mIndex > oldIndex) {
                        mDelegate.onSwipeDirection(0, oldIndex, mIndex);
                    } else {
                        mDelegate.onSwipeDirection(1, oldIndex, mIndex);
                    }
                }

            }
        }
    };

    public void setCurrentSelectBtn(int index) {
        boolean isValidIndex = index >= 0 && index < mStatusBtnList.size() - 1;
        if (isValidIndex) {
            mCurrentSelectBtn.setBtnStatus(false, false);
            MyStatusButton targetStatusBtn = mStatusBtnList.get(index);
            targetStatusBtn.setBtnStatus(true, false);
            mCurrentSelectBtn = targetStatusBtn;
        }

    }


    public void setDelegate(BeautifyTitleViewDelegate delegate) {
        this.mDelegate = delegate;
    }


}
