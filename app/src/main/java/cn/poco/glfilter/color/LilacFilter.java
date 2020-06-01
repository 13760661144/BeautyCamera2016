package cn.poco.glfilter.color;

import android.content.Context;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by Jdlin on 2016/7/8.
 */
public class LilacFilter extends DefaultFilter {

    public LilacFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadStikerLilacProgram();
    }

    @Override
    public boolean isNeedFlipTexture() {
        return true;
    }
}
