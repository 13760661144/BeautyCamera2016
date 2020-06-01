package cn.poco.beautifyEyes.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;

/**
 * Created by Shine on 2016/12/7.
 * 大眼、祛眼袋、亮眼继承的父类site,定义好基本操作接口
 */

public abstract class BeautyBaseSite extends BaseSite{

    public BeautyBaseSite(int id) {
        super(id);
    }

    @Override
    public IPage MakePage(Context context) {
        return null;
    }

    /**
     * @param params img:Bitmap
     */
    public abstract void onBack(Context context, HashMap<String, Object> params);

    public abstract void onSave(Context context, HashMap<String, Object> params);

    public abstract void pinFaceChat(Context context, HashMap<String, Object> params);

}
