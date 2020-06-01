package cn.poco.featuremenu.model;

import com.adnonstop.missionhall.utils.gz_Iutil.TellHasClick;

import my.beautyCamera.R;

/**
 * Created by Shine on 2017/9/6.
 */

public class MenuFeature extends AppFeature{
    private static int[] sIconDrawableId = new int[] {R.drawable.featuremenu_welfare, R.drawable.featuremenu_taskhall,
            R.drawable.featuremenu_wallet, R.drawable.featuremenu_comment_icon,
            R.drawable.featuremenu_cloudalbum_icon, R.drawable.featuremenu_photoprinted_icon,
            R.drawable.featuremenu_chat_icon, R.drawable.featuremenu_live_show};


    public boolean mShowBadge = false;
    private BadgeTip mBadgeTip;
    private boolean mIsPlaceHolder;
    private int mFeatureIconResId;

    public MenuFeature(String id) {
        super(id);
    }

    public MenuFeature(FeatureType featureType) {
        super(featureType);
    }


    public BadgeTip getBadgeTip() {
        return mBadgeTip;
    }

    public void setBadgeTip(BadgeTip badgeTip) {
        this.mBadgeTip = badgeTip;
    }

    public void setIsPlaceHolder(boolean yes) {
        this.mIsPlaceHolder = yes;
    }

    public boolean isPlaceHolder() {
        return mIsPlaceHolder;
    }

    public int getFeatureIconResId() {
        return mFeatureIconResId;
    }

    public void setFeatureIconResId(int mFeatureIconResId) {
        this.mFeatureIconResId = mFeatureIconResId;
    }

    @Override
    public void actionToSpecificType(int index) {
        this.setFeatureIconResId(sIconDrawableId[index]);
    }

    public static class BadgeTip {
        public String badgeContent;
        public void clickAction(String userId) {

        };
    }

    public static class TextBadge extends BadgeTip {

    }

    public static class NumberBadge extends BadgeTip {

    }

    public static class RedDotBadge extends BadgeTip {

    }

    public static class RewardBadge extends TextBadge {
        @Override
        public void clickAction(String userId) {
            TellHasClick tellHasClick = new TellHasClick();
            tellHasClick.hasRedRight(userId, "1", "MISSIONHALL", "beauty_camera");
        }
    }

    public static class TaskBadge extends TextBadge {
        @Override
        public void clickAction(String userId) {

        }
    }



}
