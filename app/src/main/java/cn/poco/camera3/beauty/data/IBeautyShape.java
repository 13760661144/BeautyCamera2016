package cn.poco.camera3.beauty.data;

/**
 * @author lmx
 * Created by lmx on 2018-01-19.
 */

import cn.poco.pocointerfacelibs.IPOCO;

/**
 * 个人设置脸型数据api
 */
public interface IBeautyShape extends IPOCO
{
    String GetBeautyShapeApi();

    String SaveBeautyShapeApi();
}
