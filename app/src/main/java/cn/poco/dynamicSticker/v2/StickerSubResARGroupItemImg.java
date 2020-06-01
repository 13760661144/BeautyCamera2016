package cn.poco.dynamicSticker.v2;

import java.util.ArrayList;

/**
 * Created by zwq on 2017/10/30 16:34.<br/><br/>
 */

public class StickerSubResARGroupItemImg {

    private int mItemIndex;
    private ArrayList<String> mImgPaths;

    public StickerSubResARGroupItemImg() {
    }

    public void setItemIndex(int itemIndex) {
        mItemIndex = itemIndex;
    }

    public int getItemIndex() {
        return mItemIndex;
    }

    public void addImg(String imgPath) {
        if (mImgPaths == null) {
            mImgPaths = new ArrayList<>();
        }
        mImgPaths.add(imgPath);
    }

    public ArrayList<String> getImgPaths() {
        return mImgPaths;
    }



}
