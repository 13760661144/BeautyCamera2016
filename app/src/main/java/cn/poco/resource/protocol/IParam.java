package cn.poco.resource.protocol;

import android.support.annotation.NonNull;

/**
 * 素材平台请求 具体的数据内容，key-value形式
 *
 * @author lmx
 *         Created by lmx on 2017/7/21.
 */

public interface IParam
{
    /**
     * 测试数据代表切去88.8获取的数据，正式数据代表用户能看到的数据
     *
     * @return 1：测试数据 2：正式数据
     */
    public int IsBeta(boolean isBeta);

    /**
     * 素材请求分类
     *
     * @param pageType {@link PageType}
     * @return 1：bgm素材 2：动态贴纸标签分类 3：动态贴纸素材
     */
    public int GetPageType(@NonNull PageType pageType);


    /**
     * 分组，获取下载区与内置的作品列表
     *
     * @param resourceGroup {@link ResourceGroup}
     * @return -2下载区 -1引导下载前 0内置 1引导下载后 可复选分组用逗号分割 PS:-2,0 （默认值: -2）
     */
    public String GetResourceGroup(ResourceGroup[] resourceGroup);
}
