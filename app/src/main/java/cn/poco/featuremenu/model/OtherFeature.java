package cn.poco.featuremenu.model;

/**
 * Created by Shine on 2017/9/12.
 */

public class OtherFeature extends AppFeature{
    private String mTips;

    public OtherFeature(String id) {
        super(id);
    }

    @Override
    public void actionToSpecificType(int index) {

    }

    public void setFeatureTips(String tips) {
        this.mTips = tips;
    }

    public String getTips() {
        return mTips;
    }



}
