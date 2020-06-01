package cn.poco.camera3.info;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;

import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.mgr.TypeMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.LimitRes;
import cn.poco.resource.LimitResMgr;
import cn.poco.resource.LiveVideoStickerGroupRes;
import cn.poco.resource.LiveVideoStickerGroupResRedDotMrg2;
import cn.poco.resource.LiveVideoStickerRes;
import cn.poco.resource.LiveVideoStickerResRedDotMgr2;
import cn.poco.resource.LockRes;
import cn.poco.resource.LockResMgr2;
import cn.poco.resource.VideoStickerGroupRes;
import cn.poco.resource.VideoStickerGroupResRedDotMrg2;
import cn.poco.resource.VideoStickerRes;
import cn.poco.resource.VideoStickerResRedDotMgr2;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;

/**
 * 方便构建实体类对象
 * Created by Gxx on 2017/10/25.
 */

public class InfoBuilder
{
    public static StickerInfo BuildStickerInfo(Context context, VideoStickerRes res, boolean isOnlyBuilt_In)
    {
        if (res != null)
        {
            StickerInfo info = new StickerInfo();
            info.id = res.m_id;
            info.mName = res.m_name;
            info.mHasMusic = res.m_has_music;
            info.mShowType = res.m_show_type;
            info.mIsFace = res.m_is_shape_compose;
            info.mRes = res;
            if (res.m_thumb != null)
            {
                info.mThumb = res.m_thumb;
            }
            else if (!TextUtils.isEmpty(res.url_thumb))
            {
                info.mThumb = res.url_thumb;
            }

            if (isOnlyBuilt_In)
            {
                info.mStatus = StatusMgr.Type.BUILT_IN;//内置
                return info;
            }

            // 下载状态
            if (res.m_type == BaseRes.TYPE_NETWORK_URL)
            {
                info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
            }
            else if (res.m_type == BaseRes.TYPE_LOCAL_PATH)
            {
                if (!TextUtils.isEmpty(res.m_res_path) && new File(res.m_res_path).exists())
                {
                    info.mStatus = StatusMgr.Type.LOCAL;
                }
                else if (TextUtils.isEmpty(res.m_res_path) || TextUtils.isEmpty(res.m_res_name))//zip未下载
                {
                    info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                }
                else
                {
                    info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                }
            }
            else
            {
                info.mStatus = StatusMgr.Type.BUILT_IN;//内置
            }

            // 是否new
            boolean isNew = VideoStickerResRedDotMgr2.getInstance().hasMarkFlag(context, info.id);

            // 是否上锁
            boolean isLock = false;
            ArrayList<LockRes> lockResList = LockResMgr2.getInstance().getVideoFaceLockArr();
            if (LockResMgr2.getInstance().getVideoFaceLockArr() != null)
            {
                for (LockRes lockRes : lockResList)
                {
                    if (lockRes != null)
                    {
                        if (lockRes.m_id == info.id
                                && info.mStatus == StatusMgr.Type.NEED_DOWN_LOAD
                                && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE
                                && TagMgr.CheckTag(context, Tags.CAMERA_VIDEO_FACE_UNLOCK_ID_FLAG + lockRes.m_id))
                        {
                            info.mLockRes = lockRes;
                            isLock = true;
                        }

                        if (!TextUtils.isEmpty(lockRes.m_showContent) && lockRes.m_showContent.contains("%"))
                        {
                            try
                            {
                                lockRes.m_showContent = URLDecoder.decode(lockRes.m_showContent, "UTF-8");
                            }
                            catch (Throwable ignored)
                            {
                            }
                        }
                    }
                }
            }

            // 是否限时
            boolean isLimit = false;
            if (LimitResMgr.m_videoFaceLimitArr != null)
            {
                for (LimitRes limitRes : LimitResMgr.m_videoFaceLimitArr)
                {
                    if (limitRes != null && limitRes.m_id == info.id && limitRes.isLimit
                            && info.mStatus == StatusMgr.Type.NEED_DOWN_LOAD
                            && TagMgr.CheckTag(context, Tags.CAMERA_VIDEO_FACE_UNLOCK_ID_FLAG + limitRes.m_id))
                    {
                        info.mLimitRes = limitRes;
                        isLimit = true;
                    }
                }
            }

            if (isLimit)
            {
                info.mStatus = StatusMgr.Type.LIMIT;
            }
            else if (isLock)
            {
                info.mStatus = StatusMgr.Type.LOCK;
                info.mHasLock = true;
            }

            if (isNew)
            {
                info.mStatus = StatusMgr.Type.NEW;
            }

            return info;
        }
        return null;
    }

    public static LabelInfo BuildLabelInfo(Context context, VideoStickerGroupRes res)
    {
        LabelInfo info = new LabelInfo();
        info.ID = res.m_id;
        info.mLabelName = res.m_name;
        info.mStickerIDArr = res.m_stickerIDArr;
        info.isHide = res.m_isHide;
        info.isShowRedPoint = VideoStickerGroupResRedDotMrg2.getInstance().hasMarkFlag(context, res.m_id);

        if (info.mLabelName != null && info.mLabelName.toUpperCase().equals("HOT"))
        {
            info.mType = TypeMgr.StickerLabelType.HOT;
        }
        else if (info.mLabelName != null && info.mLabelName.equals("脸型"))
        {
            info.mType = TypeMgr.StickerLabelType.FACE;
        }
        else
        {
            info.mType = TypeMgr.StickerLabelType.TEXT;
        }
        return info;
    }

    public static StickerInfo BuildLiveStickerInfo(Context context, LiveVideoStickerRes res, boolean isOnlyBuilt_In)
    {
        if (res != null)
        {
            StickerInfo info = new StickerInfo();
            info.id = res.m_id;
            info.mName = res.m_name;
            info.mHasMusic = res.m_has_music;
            info.mIsFace = res.m_is_shape_compose;
            info.mRes = res;
            if (res.m_thumb != null)
            {
                info.mThumb = res.m_thumb;
            }
            else if (!TextUtils.isEmpty(res.url_thumb))
            {
                info.mThumb = res.url_thumb;
            }

            if (isOnlyBuilt_In)
            {
                info.mStatus = StatusMgr.Type.BUILT_IN;//内置
                return info;
            }

            // 下载状态
            if (res.m_type == BaseRes.TYPE_NETWORK_URL)
            {
                info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
            }
            else if (res.m_type == BaseRes.TYPE_LOCAL_PATH)
            {
                if (!TextUtils.isEmpty(res.m_res_path) && new File(res.m_res_path).exists())
                {
                    info.mStatus = StatusMgr.Type.LOCAL;
                }
                else if (TextUtils.isEmpty(res.m_res_path) || TextUtils.isEmpty(res.m_res_name))//zip未下载
                {
                    info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                }
                else
                {
                    info.mStatus = StatusMgr.Type.NEED_DOWN_LOAD;
                }
            }
            else
            {
                info.mStatus = StatusMgr.Type.BUILT_IN;//内置
            }

            // 是否new
            boolean isNew = LiveVideoStickerResRedDotMgr2.getInstance().hasMarkFlag(context, info.id);

            // 是否上锁
            boolean isLock = false;
            ArrayList<LockRes> lockResList = LockResMgr2.getInstance().getVideoFaceLockArr();
            if (LockResMgr2.getInstance().getVideoFaceLockArr() != null)
            {
                for (LockRes lockRes : lockResList)
                {
                    if (lockRes != null)
                    {
                        if (lockRes.m_id == info.id
                                && info.mStatus == StatusMgr.Type.NEED_DOWN_LOAD
                                && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE
                                && TagMgr.CheckTag(context, Tags.CAMERA_VIDEO_FACE_UNLOCK_ID_FLAG + lockRes.m_id))
                        {
                            info.mLockRes = lockRes;
                            isLock = true;
                        }

                        if (!TextUtils.isEmpty(lockRes.m_showContent) && lockRes.m_showContent.contains("%"))
                        {
                            try
                            {
                                lockRes.m_showContent = URLDecoder.decode(lockRes.m_showContent, "UTF-8");
                            }
                            catch (Throwable ignored)
                            {
                            }
                        }
                    }
                }
            }

            // 是否限时
            boolean isLimit = false;
            if (LimitResMgr.m_videoFaceLimitArr != null)
            {
                for (LimitRes limitRes : LimitResMgr.m_videoFaceLimitArr)
                {
                    if (limitRes != null && limitRes.m_id == info.id && limitRes.isLimit
                            && info.mStatus == StatusMgr.Type.NEED_DOWN_LOAD
                            && TagMgr.CheckTag(context, Tags.CAMERA_VIDEO_FACE_UNLOCK_ID_FLAG + limitRes.m_id))
                    {
                        info.mLimitRes = limitRes;
                        isLimit = true;
                    }
                }
            }

            if (isLimit)
            {
                info.mStatus = StatusMgr.Type.LIMIT;
            }
            else if (isLock)
            {
                info.mStatus = StatusMgr.Type.LOCK;
                info.mHasLock = true;
            }

            if (isNew)
            {
                info.mStatus = StatusMgr.Type.NEW;
            }

            return info;
        }
        return null;
    }

    public static LabelInfo BuildLiveLabelInfo(Context context, LiveVideoStickerGroupRes res)
    {
        LabelInfo info = new LabelInfo();
        info.ID = res.m_id;
        info.mLabelName = res.m_name;
        info.mStickerIDArr = res.m_stickerIDArr;
        info.isHide = res.m_isHide;
        info.isShowRedPoint = LiveVideoStickerGroupResRedDotMrg2.getInstance().hasMarkFlag(context, res.m_id);

        if (info.mLabelName != null && info.mLabelName.toUpperCase().equals("HOT"))
        {
            info.mType = TypeMgr.StickerLabelType.HOT;
        }
        else if (info.mLabelName != null && info.mLabelName.equals("脸型"))
        {
            info.mType = TypeMgr.StickerLabelType.FACE;
        }
        else
        {
            info.mType = TypeMgr.StickerLabelType.TEXT;
        }
        return info;
    }

    public static ArrayList<StickerInfo> BuildEmptyStickerInfoList(LabelInfo info)
    {
        ArrayList<StickerInfo> out = new ArrayList<>();

        if (info != null)
        {
            int[] ids = info.mStickerIDArr;
            if (ids != null)
            {
                int len = ids.length;
                while (len > 0)
                {
                    out.add(new StickerInfo());
                    len -= 1;
                }
            }
        }
        return out;
    }

    public static ArrayList<StickerInfo> BuildEmptyStickerInfoList(int[] ids)
    {
        ArrayList<StickerInfo> out = new ArrayList<>();
        if (ids != null)
        {
            int len = ids.length;
            while (len > 0)
            {
                out.add(new StickerInfo());
                len -= 1;
            }
        }
        return out;
    }
}
