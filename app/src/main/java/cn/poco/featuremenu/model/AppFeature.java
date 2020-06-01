package cn.poco.featuremenu.model;


/**
 * Created by Shine on 2017/9/12.
 */

public abstract class AppFeature {
    private static final String[] sFeatureId = new String[] {"welfare", "mission", "wallet", "message", "cloud",
            "photo", "chat", "credit","wechat", "weibo",
            "homepage_right", "homepage_left", "log_tips", "recommend", "liveShow"};

    private FeatureType mFeature;
    private String mId;
    private String mTitle;
    private String mDescribe;
    private String mTime;
    private String mUnlock;


    public AppFeature(String id) {
        this.mFeature = mapIdToCorrectType(id);
    }

    public AppFeature(FeatureType menuFeatureType) {
        this.mFeature = menuFeatureType;
    }

    public FeatureType getFeature() {
        return mFeature;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDescribe() {
        return mDescribe;
    }

    public void setDescribe(String describe) {
        this.mDescribe = describe;
    }


    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String isUnlock() {
        return mUnlock;
    }

    public void setUnlock(String unlock) {
        this.mUnlock = unlock;
    }


    public FeatureType mapIdToCorrectType(String id) {
        FeatureType featureType = null;
        if (id.equals(sFeatureId[0])) {
            featureType = FeatureType.BEAUTYMALL;
            actionToSpecificType(0);
        } else if (id.equals(sFeatureId[1])) {
            featureType = FeatureType.TASKHALL;
            actionToSpecificType(1);
        } else if (id.equals(sFeatureId[2])) {
            featureType = FeatureType.WALLET;
            actionToSpecificType(2);
        } else if (id.equals(sFeatureId[3])) {
            featureType = FeatureType.COMMENT;
            actionToSpecificType(3);
        } else if (id.equals(sFeatureId[4])) {
            featureType = FeatureType.CLOUDALBUM;
            actionToSpecificType(4);
        } else if (id.equals(sFeatureId[5])) {
            featureType = FeatureType.PHOTOPRINTED;
            actionToSpecificType(5);
        } else if (id.equals(sFeatureId[6])) {
            featureType = FeatureType.CHAT;
            actionToSpecificType(6);
        } else if (id.equals(sFeatureId[7])) {
            featureType = FeatureType.MYCREDIT;
        } else if (id.equals(sFeatureId[8])) {
            featureType = FeatureType.WECHAT_LOGIN;
        } else if (id.equals(sFeatureId[9])) {
            featureType = FeatureType.SINA_LOGIN;
        } else if (id.equals(sFeatureId[10])) {
            featureType = FeatureType.HOMEPAGE_RIGHT_TEXT;
        } else if (id.equals(sFeatureId[11])) {
            featureType = FeatureType.HOMEPAGE_LEFT_TEXT;
        } else if (id.equals(sFeatureId[12])) {
            featureType = FeatureType.REGISTER_LOGIN_TIPS;
        } else if (id.equals(sFeatureId[13])) {
            featureType = FeatureType.APPRECOMENDATION;
        } else if (id.equals(sFeatureId[14])) {
            featureType = FeatureType.LIVESHOW;
            actionToSpecificType(7);
        }
        return featureType;
    }

    public abstract void actionToSpecificType(int index);

}
