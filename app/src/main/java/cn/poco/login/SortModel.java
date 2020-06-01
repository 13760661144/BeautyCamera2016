package cn.poco.login;

import java.util.ArrayList;

public class SortModel {

    private String mName;   //国家名称
    private String mAreaCode;//国际区号
    private String mSortLetters;  //国家第一字母
    private ArrayList<String> mEachChineseSpell;//国家每个汉字对应的拼音
    private String mEnName;

    public String getEnName() {
        return mEnName;
    }

    public void setEnName(String mEnName) {
        this.mEnName = mEnName;
    }

    public String getName() {return mName;}

    public String getAreaCode() {return mAreaCode;}

    public ArrayList<String> getEachChineseSpell() {return mEachChineseSpell;}

    public void setName(String name) {this.mName = name;}

    public void setNum(String areaCode) {
        this.mAreaCode = areaCode;
    }

    public String getSortLetters() {return mSortLetters;}

    public void setSortLetters(String sortLetters) {this.mSortLetters = sortLetters;}

    public void setEachChineseSpell(ArrayList<String> eachChineseSpell) {this.mEachChineseSpell = eachChineseSpell;}
}
