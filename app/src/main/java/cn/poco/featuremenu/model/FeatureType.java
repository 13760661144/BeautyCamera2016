package cn.poco.featuremenu.model;

/**
 * Created by Shine on 2017/9/11.
 */

public enum FeatureType {
    NONE(-1),
    TASKHALL(0),
    WALLET(1),
    BEAUTYMALL(2),
    CHAT(3),
    COMMENT(4),
    CLOUDALBUM(5),
    PHOTOPRINTED(6),
    CUSTOMBEAUTY(7),

    WECHAT_LOGIN(8),
    SINA_LOGIN(9),
    HOMEPAGE_RIGHT_TEXT(10),
    HOMEPAGE_LEFT_TEXT(11),
    REGISTER_LOGIN_TIPS(12),

    CHANGETHEMESKIN(13),
    GENERALSETTING(14),
    SHAREWITHFRIENDS(15),
    APPRECOMENDATION(16),
    RATEUS(17),
    HELPANDFEEDBACK(18),
    ABOUT(19),
    MYCREDIT(20),
    LIVESHOW(21);



    private int mIndex;
    FeatureType(int index) {
        mIndex = index;
    }


}
