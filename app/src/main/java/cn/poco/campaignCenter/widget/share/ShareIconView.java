package cn.poco.campaignCenter.widget.share;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/8/22.
 */

public class ShareIconView extends FrameLayout{
    private int mColumnCount, mRowCount;
    private int mItemVertialMargin;
    private int mBottomMargin;
    private int mTextSize = -1, mTextColor = -1;

    private List<ShareIconInfo> mShareInfoList = new ArrayList<>();
    private List<ShareImageCell> mShareImageCellList = new ArrayList<>();

    final String [] names = new String [] {getResources().getString(R.string.friends_circle), getResources().getString(R.string.wechat_friends), getResources().getString(R.string.sina_weibo),
            getResources().getString(R.string.QQZone), getResources().getString(R.string.QQ), getResources().getString(R.string.Facebook), getResources().getString(R.string.Twitter)};

    final int [] resId = new int [] {R.drawable.share_weibo_wechat_friend_normal, R.drawable.share_weibo_wechat_normal, R.drawable.share_weibo_sina_normal,
            R.drawable.share_weibo_qzone_normal, R.drawable.share_weibo_qq_normal, R.drawable.share_weibo_facebook_normal, R.drawable.share_weibo_twitter_normal};

    private OnClickListener mItemClickListener;

    private ShareIconView(Context context, ShareIconViewBuilder builder) {
        super(context);
        this.mColumnCount = builder.mColumnCount;
        this.mItemVertialMargin = builder.mItemVertialMargin;
        this.mBottomMargin = builder.mBottomMargin;
        if (builder.mShareInfoList == null) {
            initDefaultShareInfo();
        } else {
            this.mShareInfoList = builder.mShareInfoList;
        }
        if (builder.mTextSize != -1) {
            this.mTextSize = builder.mTextSize;
        }

        if (builder.mTextColor != -1) {
            this.mTextColor = builder.mTextColor;
        }
        this.mRowCount = (int)Math.ceil(mShareInfoList.size() / (mColumnCount * 1.0f));
        this.mItemClickListener = builder.mClickListener;
        initView(context);
    }

    private void initDefaultShareInfo() {
        ShareIconInfo wechatFriendCircle = new ShareIconInfo();
        wechatFriendCircle.name = names[0];
        wechatFriendCircle.resId = resId[0];
        wechatFriendCircle.mShareType = ShareInfoType.FRIEND_CIRCLE;
        mShareInfoList.add(wechatFriendCircle);

        ShareIconInfo wechatFriends = new ShareIconInfo();
        wechatFriends.name = names[1];
        wechatFriends.resId = resId[1];
        wechatFriends.mShareType = ShareInfoType.WECHAT_FRIENDS;
        mShareInfoList.add(wechatFriends);

        ShareIconInfo sinaWeibo = new ShareIconInfo();
        sinaWeibo.name = names[2];
        sinaWeibo.resId = resId[2];
        sinaWeibo.mShareType = ShareInfoType.SINA_WEIBO;
        mShareInfoList.add(sinaWeibo);

        ShareIconInfo qqZone = new ShareIconInfo();
        qqZone.name = names[3];
        qqZone.resId = resId[3];
        qqZone.mShareType = ShareInfoType.Q_ZONE;
        mShareInfoList.add(qqZone);

        ShareIconInfo qq = new ShareIconInfo();
        qq.name = names[4];
        qq.resId = resId[4];
        qq.mShareType = ShareInfoType.QQ;
        mShareInfoList.add(qq);

        ShareIconInfo facebook = new ShareIconInfo();
        facebook.name = names[5];
        facebook.resId = resId[5];
        facebook.mShareType = ShareInfoType.FACE_BOOK;
        mShareInfoList.add(facebook);

        ShareIconInfo twitter = new ShareIconInfo();
        twitter.name = names[6];
        twitter.resId = resId[6];
        twitter.mShareType = ShareInfoType.TWITTER;
        mShareInfoList.add(twitter);
    }

    private void initView(Context context) {
        ShareImageCell shareImageCell;
        for (int i = 0; i < mShareInfoList.size(); i++) {
            ShareIconInfo currentInfo = mShareInfoList.get(i);
            shareImageCell = new ShareImageCell(context);
            shareImageCell.setImageIcon(currentInfo.resId);

            if (mTextSize != -1 && mTextColor != -1) {
                shareImageCell.setName(currentInfo.name, mTextSize, mTextColor);
            } else if (mTextSize != -1){
                shareImageCell.setNameAndTextSize(currentInfo.name, mTextSize);
            } else if (mTextColor != -1) {
                shareImageCell.setNameAndTextColor(currentInfo.name, mTextColor);
            } else {
                shareImageCell.setName(currentInfo.name);
            }
            shareImageCell.setTag(currentInfo.mShareType);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            shareImageCell.setLayoutParams(params);
            this.addView(shareImageCell);
            shareImageCell.setOnClickListener(mItemClickListener);
            mShareImageCellList.add(shareImageCell);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int shareItemHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ShareImageCell) {
                shareItemHeight = child.getMeasuredHeight();
                break;
            }
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (shareItemHeight * mRowCount) + mBottomMargin + (mItemVertialMargin * (mRowCount - 1)));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int mTotalWidth = this.getMeasuredWidth();
        int addtionalPadding = 0;
        for (int i = 0; i < mShareImageCellList.size(); i++) {
            View child = mShareImageCellList.get(i);
            int mItemPaddingHorizontal = ((mTotalWidth - (child.getMeasuredWidth() * mColumnCount)) / (mColumnCount - 1));
            ShareImageCell currentView = (ShareImageCell) child;
            int x = (i % mColumnCount) * (currentView.getMeasuredWidth() + mItemPaddingHorizontal);
            int lines = i / mColumnCount;

            if (lines == 0) {
                addtionalPadding = ShareData.PxToDpi_xhdpi(8);
            }
            int y = (currentView.getMeasuredHeight() + mItemVertialMargin + addtionalPadding) * (i / mColumnCount);
            addtionalPadding = 0;
            currentView.layout(x, y, x + currentView.getMeasuredWidth(), y + currentView.getMeasuredHeight());
        }
    }

    public void clear() {
        this.mItemClickListener = null;
    }


    public static class ShareIconViewBuilder {
        private Context mContext;
        private int mColumnCount;
        private int mItemVertialMargin = ShareData.PxToDpi_xhdpi(40);
        private int mBottomMargin = ShareData.PxToDpi_xhdpi(60);
        private List<ShareIconInfo> mShareInfoList;
        private OnClickListener mClickListener;
        private int mTextSize = -1, mTextColor = -1;

        public ShareIconViewBuilder(Context context, int count) {
            this.mContext = context;
            this.mColumnCount = count;
        }


        public ShareIconViewBuilder verticalMargin(int verticalMargin) {
            this.mItemVertialMargin = verticalMargin;
            return this;
        }

        public ShareIconViewBuilder bottomMargin(int bottomMargin) {
            this.mBottomMargin = bottomMargin;
            return this;
        }

        public ShareIconViewBuilder shareInfoList(List<ShareIconInfo> list) {
            this.mShareInfoList = list;
            return this;
        }

        public ShareIconViewBuilder itemOnClickListener(OnClickListener listener) {
            this.mClickListener = listener;
            return this;
        }

        public ShareIconViewBuilder iconTextSizeAndColor(int textSize, int textColor) {
            this.mTextSize = textSize;
            this.mTextColor =textColor;
            return this;
        }


        public ShareIconView create() {
            return new ShareIconView(mContext, this);
        }
    }

    public static class ShareIconInfo {
        public String name;
        public int resId;
        public int mShareType;
    }

    private static class ShareImageCell extends LinearLayout {
        private ImageView mIconView;
        private TextView mName;

        private int mNameToIconMargins;

        public ShareImageCell(Context context) {
            super(context);
            initLayoutData();
            initView(context);
        }

        private void initLayoutData() {
            mNameToIconMargins = ShareData.PxToDpi_xhdpi(10);
        }

        private void initView(Context context) {
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER_HORIZONTAL);
            mIconView = new ImageView(context);
            mIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mIconView.setLayoutParams(params);
            this.addView(mIconView);

            mName = new TextView(context);
            mName.setGravity(Gravity.CENTER);
            mName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            mName.setTextColor(0x80000000);

            LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.topMargin = mNameToIconMargins;
            mName.setLayoutParams(params1);
            this.addView(mName);
        }

        public void setImageIcon(int resId) {
            mIconView.setImageResource(resId);
        }

        public void setName(String name) {
            setNameAndTextSize(name, -1);
        }

        public void setNameAndTextSize(String name, int textSize) {
           setName(name, textSize, -1);
        }

        public void setNameAndTextColor(String name, int textColor) {
            setName(name, -1, textColor);
        }

        public void setName(String name, int textSize, int textColor) {
            mName.setText(name);
            if (textSize != -1) {
                mName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            }

            if (textColor != -1) {
                mName.setTextColor(textColor);
            }

        }

    }


}
