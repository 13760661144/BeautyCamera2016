package cn.poco.resource.protocol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.poco.system.AppInterface;

/**
 * 素材平台请求params
 *
 * @author lmx
 *         Created by lmx on 2017/7/21.
 */
public class ParamsInterface implements IComeFrom, IParam
{
    protected static ParamsInterface mInstance;

    public static ParamsInterface GetInstance()
    {
        if (mInstance == null)
        {
            mInstance = new ParamsInterface();
        }
        return mInstance;
    }

    private ParamsInterface()
    {

    }

    @Override
    public String GetAppName(Context context)
    {
        return AppInterface.GetInstance(context).GetAppName();
    }

    @Override
    public String GetAppVersion(Context context)
    {
        return AppInterface.GetInstance(context).GetAppVer();
    }

    @Override
    public String GetProjectName()
    {
        return "beauty_camera";
    }

    @Override
    public int IsBeta(boolean isBeta)
    {
        if (isBeta)
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }

    @Override
    public int GetPageType(@NonNull PageType pageType)
    {
        return pageType.getType();
    }

    @Override
    public String GetResourceGroup(ResourceGroup[] resourceGroup)
    {
        if (resourceGroup == null || resourceGroup.length == 0)
        {
            resourceGroup = new ResourceGroup[]{ResourceGroup.DOWNLOAD};
        }

        StringBuilder builder = new StringBuilder();
        for (ResourceGroup group : resourceGroup)
        {
            builder.append(String.valueOf(group.getType())).append(",");
        }
        String sbStr = builder.toString();
        String group = "-2";
        if (!TextUtils.isEmpty(sbStr))
        {
            group = sbStr.substring(0, sbStr.length() - 1).trim();
        }
        //System.out.println("group:" + group);
        return group;
    }
}
