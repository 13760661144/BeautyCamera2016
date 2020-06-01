package cn.poco.campaignCenter.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.ui.cells.AutoDisplayCell;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.tianutils.ShareData;

/**
 * Created by admin on 2016/11/17.
 */

public class AutoSlideViewPager extends FrameLayout{

    private static class PageContent {
        private CampaignInfo mCampaignInfo;
        private int mIndex;
        private AutoDisplayCell mCampaignCell;

        public PageContent (int index, CampaignInfo info, AutoDisplayCell campaignCell) {
            this.mIndex = index;
            this.mCampaignInfo = info;
            this.mCampaignCell = campaignCell;
        }

        public int setIndexToLeft(int index, int max) {
            int temp;
            if (index == 0) {
                temp = max;
            } else {
                temp = index - 1;
            }
            return temp;
        }

        public int setIndexToRight(int index, int max) {
            int temp;
            if (index == max) {
                temp = 0;
            } else {
                temp = index + 1;
            }
            return temp;
        }
    }

    private static final int PAGE_LEFT = 0;
    private static final int PAGE_MIDDEL = 1;
    private static final int PAGE_RIGHT = 2;

    private int mSelectedPageIndex = 1;
    private PageContent[] pageContentArray = new PageContent[3] ;
    public ViewPager mViewPager;
    private SlidePagerAdapter mPagerAdapter;
    private ViewPagerIndicator mIndicator;
    private List<IDataChange> mDataChangeListenerList = new ArrayList<>();

    private Context mContext;
    private long mDuration = 2000;

    private ScrollHandler mHander;
    private static final int SCROLL_MSG = 1000;
    private boolean mEnoughPageToScroll;
    private boolean mIsScrolling;
    private boolean mIsStopByTouch;

    private List<CampaignInfo> mCampaignInfoList;
    private int infoListSize;

    private boolean mInitialize = true;

    private OnClickListener mDelegate;

    public AutoSlideViewPager(Context context, OnClickListener delegate) {
        super(context);
        mContext = context;
        mDelegate = delegate;
        initView(context);
    }

    private void initView(Context context) {
        mViewPager = new ViewPager(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(layoutParams);
        this.addView(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex = position;
            }


            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE  && mEnoughPageToScroll) {
                    updateIndex();
                    sychronizeData();
                }
            }
        });

        mIndicator = new ViewPagerIndicator(context);
        LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        layoutParams1.bottomMargin = ShareData.PxToDpi_xhdpi(12);
        mIndicator.setLayoutParams(layoutParams1);
        this.addView(mIndicator);
        mDataChangeListenerList.add(mIndicator);

        mHander = new ScrollHandler(this);
    }

    public void setData(List<CampaignInfo> dataList) {
        mCampaignInfoList = dataList;
        infoListSize = mCampaignInfoList.size();
        initialData();
        if (infoListSize > 0) {
            mIndicator.setData(infoListSize);
            if (!mInitialize) {
                updateIndex();
                sychronizeData();
            }
            mInitialize = false;
        }
    }

    private void initialData() {
        if (infoListSize > 0) {
            PageContent pageContent;
            for (int i = 0; i < infoListSize; i++) {
                PageContent temp = pageContentArray[i];
                if (i == 0) {
                    if (temp != null) {
                        pageContent = temp;
                    } else {
                        pageContent = new PageContent(mCampaignInfoList.size() - 1, mCampaignInfoList.get(mCampaignInfoList.size() - 1), new AutoDisplayCell(mContext));
                    }
                    pageContentArray[0] = pageContent;
                } else if (i == 1) {
                    if (temp != null) {
                        pageContent = temp;
                    } else {
                        pageContent = new PageContent(0, mCampaignInfoList.get(0), new AutoDisplayCell(mContext));
                    }
                    pageContentArray[1] = pageContent;
                } else if (i == 2) {
                    if (temp != null) {
                        pageContent = temp;
                    } else {
                        pageContent = new PageContent(1, mCampaignInfoList.get(1), new AutoDisplayCell(mContext));
                    }
                    pageContentArray[2] = pageContent;
                }
            }

            if (infoListSize >= 2) {
                mEnoughPageToScroll = true;

                if ( infoListSize == 2) {
                    if (pageContentArray[2] == null) {
                        pageContentArray[2] = new PageContent(1, mCampaignInfoList.get(1), new AutoDisplayCell(mContext));
                    } else {
                        pageContentArray[2] = new PageContent(1, mCampaignInfoList.get(1), pageContentArray[2].mCampaignCell);
                    }
                }
            }

            if (mPagerAdapter == null) {
                mPagerAdapter = new SlidePagerAdapter(mContext, mCampaignInfoList);
                mViewPager.setAdapter(mPagerAdapter);
            } else {
                mPagerAdapter.notifyDataSetChanged();
            }

            if (mEnoughPageToScroll) {
                mViewPager.setCurrentItem(PAGE_MIDDEL, false);
            }

        }
    }

    private void updateContent(int index) {
        PageContent currentPageContent = pageContentArray[index];
        currentPageContent.mCampaignInfo = mCampaignInfoList.get(currentPageContent.mIndex);
        if (currentPageContent.mCampaignCell.mBackground != null) {
            ImageLoaderUtil.displayImage(getContext(), currentPageContent.mCampaignInfo.getCoverUrl(), currentPageContent.mCampaignCell.mBackground, false, null);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN : {
                if (mIsScrolling) {
                    mIsStopByTouch = true;
                    stop();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP : {
                if (mIsStopByTouch) {
                    start();
                }
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void prepareAndStartScroll() {
        if (!mIsScrolling && mEnoughPageToScroll) {
            start();
        }
    }

    public boolean isScrolling() {
        return mIsScrolling;
    }

    private void scrollTo(int index) {
        mViewPager.setCurrentItem(index);
    }

    private void start() {
        if (mPagerAdapter.getCount() > 1 && !mIsScrolling) {
            mIsScrolling = true;
            scrollTo(1);
            scrollOnce(mDuration);
        }
    }

    public void stop() {
        mIsScrolling = false;
        mHander.removeMessages(SCROLL_MSG);
    }

    private void scrollOnce(long delay) {
        mHander.removeMessages(SCROLL_MSG);
        mHander.sendEmptyMessageDelayed(SCROLL_MSG, delay);
    }

    public void updateIndex() {
        PageContent left = pageContentArray[PAGE_LEFT];
        PageContent middle = pageContentArray[PAGE_MIDDEL];
        PageContent right = pageContentArray[PAGE_RIGHT];

        int oldLeftIndex = left.mIndex;
        int oldMiddleIndex = middle.mIndex;
        int oldRightIndex = right.mIndex;

        if (mSelectedPageIndex == PAGE_LEFT) {
            left.mIndex = left.setIndexToLeft(oldLeftIndex, infoListSize - 1);
            middle.mIndex = middle.setIndexToLeft(oldMiddleIndex, infoListSize - 1);
            right.mIndex = right.setIndexToLeft(oldRightIndex, infoListSize - 1);
        } else if (mSelectedPageIndex == PAGE_RIGHT) {
            left.mIndex = left.setIndexToRight(oldLeftIndex, infoListSize - 1);
            middle.mIndex = middle.setIndexToRight(oldMiddleIndex, infoListSize - 1);
            right.mIndex = right.setIndexToRight(oldRightIndex, infoListSize - 1);
        } else if (mSelectedPageIndex == PAGE_MIDDEL){
            middle.mIndex = pageContentArray[PAGE_MIDDEL].mIndex;
        }

        for (IDataChange item : mDataChangeListenerList) {
            item.onDataChange(middle.mIndex);
        }
    }

    public void sychronizeData() {
        updateContent(PAGE_MIDDEL);
        mViewPager.setCurrentItem(PAGE_MIDDEL, false);
        updateContent(PAGE_LEFT);
        updateContent(PAGE_RIGHT);
    }

    public View getActivePage() {
        if (pageContentArray[PAGE_MIDDEL] != null) {
            return pageContentArray[PAGE_MIDDEL].mCampaignCell;
        }
        return null;
    }


    private class SlidePagerAdapter extends PagerAdapter {
        private Context mContext;

        public SlidePagerAdapter(Context context, List<CampaignInfo> infoList) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mCampaignInfoList.size() > 1 ? 3 : mCampaignInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PageContent currentPage = pageContentArray[position];
            final CampaignInfo currentPaignInfo = currentPage.mCampaignInfo;
            AutoDisplayCell cell = new AutoDisplayCell(mContext);
            cell.setData(currentPaignInfo);
            currentPage.mCampaignCell = cell;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            cell.setLayoutParams(params);
            container.addView(cell);
            cell.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDelegate != null) {
                        mDelegate.onClick(v);
                    }
                }
            });
            return cell;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
            container.clearDisappearingChildren();
        }
    }

    private static class ScrollHandler extends Handler {
        private WeakReference<AutoSlideViewPager> mWeakReference;

        public ScrollHandler(AutoSlideViewPager viewPager) {
            mWeakReference = new WeakReference<>(viewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AutoSlideViewPager instance = mWeakReference.get();
            if (instance != null) {
                switch (msg.what) {
                    case SCROLL_MSG :
                        instance.mViewPager.setCurrentItem(instance.mViewPager.getCurrentItem() + 1);
                        instance.scrollOnce(instance.mDuration);
                        break;
                    default:
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(MeasureSpec.getSize(heightMeasureSpec)), MeasureSpec.EXACTLY));
    }


    public interface IDataChange {

        void onDataChange(int activeIndex);

    }

}
