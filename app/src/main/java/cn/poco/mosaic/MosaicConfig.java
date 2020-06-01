package cn.poco.mosaic;

import cn.poco.tianutils.ShareData;
import cn.poco.widget.recycle.RecommendConfig;

/**
 * Created by lgd on 2017/7/25.
 */

public class MosaicConfig extends RecommendConfig {
    @Override
    public void InitData() {
        super.InitData();
        def_parent_center_x = ShareData.PxToDpi_xhdpi(104)+(ShareData.m_screenWidth -ShareData.PxToDpi_xhdpi(104)) / 2;
    }
}
