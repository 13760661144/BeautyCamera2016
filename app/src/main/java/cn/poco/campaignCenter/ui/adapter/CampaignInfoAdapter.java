package cn.poco.campaignCenter.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cn.poco.campaignCenter.manager.FileManager;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.ui.cells.CampaignBgView;
import cn.poco.campaignCenter.ui.cells.CampaignCell;
import cn.poco.campaignCenter.widget.AutoSlideViewPager;
import cn.poco.campaignCenter.widget.PullDownRefreshRecyclerView.IViewHolder;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;

/**
 * Created by admin on 2016/10/17.
 */

public class CampaignInfoAdapter extends RecyclerView.Adapter<IViewHolder>{
    private Context mContext;
    private Map<Integer, List<CampaignInfo>> mCampaignInfoMap;
    private View.OnClickListener mDelegate;

    public static final int AUTO_SLIDE_VIEWPAGER = 0;
    public static final int DISPLAY_VIEW = 1;
    public static final int CAMPAINGN_ITEM_CELL = 2;

    public static final int DATA_TYPE_COUNT_SPECIAL = 0;

    private OnAnimationClickListener mOnTouchListener;


    public CampaignInfoAdapter(Context context, Map map) {
        this.mContext = context;
        this.mCampaignInfoMap = map;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.mDelegate = listener;
    }

    public void setOnAnimationClickListener(OnAnimationClickListener listener) {
        this.mOnTouchListener = listener;
    }

    public void clear() {
        this.mDelegate = null;
        this.mOnTouchListener = null;
    }

    @Override
    public IViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View campaignItem = null;
        if (viewType == AUTO_SLIDE_VIEWPAGER) {
            campaignItem = new AutoSlideViewPager(mContext, mDelegate);
            campaignItem.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(220)));
        } else if (viewType == DISPLAY_VIEW) {
            campaignItem = new RecyclerView(mContext);
            RecyclerView recyclerView = (RecyclerView) campaignItem;
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(0)));
            campaignItem.setVisibility(View.GONE);
        } else if (viewType == CAMPAINGN_ITEM_CELL) {
            campaignItem = new CampaignCell(mContext);
            CampaignCell cell = (CampaignCell) campaignItem;
            float rate = (374 * 1.0f / 720);
            int viewHeight = (int) (ShareData.m_screenWidth * rate);
            cell.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight));
            cell.setOnNavigationItemTouchListener(mOnTouchListener);
        }

        ViewHolder holder = new ViewHolder(campaignItem);
        if (mDelegate != null) {
            campaignItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof  CampaignCell) {
                        CampaignCell cell = (CampaignCell) v;
                        mDelegate.onClick(cell);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(IViewHolder holder, int position) {
        if (holder != null) {
        View campaignItem = holder.itemView;
        int viewType = getItemViewType(position);
        List<CampaignInfo> currentCampaignTypeList = mCampaignInfoMap.get(viewType);
        if (currentCampaignTypeList.size() > 0) {
            if (viewType == AUTO_SLIDE_VIEWPAGER) {
                AutoSlideViewPager viewPager = (AutoSlideViewPager) campaignItem;
                viewPager.setData(currentCampaignTypeList);
            } else if (viewType == DISPLAY_VIEW) {
                RecyclerView recyclerView = (RecyclerView) campaignItem;
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter == null) {
                    DisplayViewAdapter displayViewAdapter = new DisplayViewAdapter(mContext, currentCampaignTypeList, mDelegate);
                    recyclerView.setAdapter(displayViewAdapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            } else if (viewType == CAMPAINGN_ITEM_CELL) {
                CampaignInfo currentCampaignInfo = currentCampaignTypeList.get(position);
                CampaignCell currentCampaignCell = (CampaignCell) campaignItem;
                currentCampaignCell.mBackground.mPosition = position;
                if (position == 0) {
                    currentCampaignCell.mBackground.setType(CampaignBgView.HEAD);
                    currentCampaignCell.setData(currentCampaignInfo, 0);
                } else {
                    currentCampaignCell.mBackground.setType(CampaignBgView.NORMAL);
                    currentCampaignCell.setData(currentCampaignInfo, 1);
                }
            }
        }
        }
    }

    @Override
    public int getItemCount() {
        return mCampaignInfoMap.get(CAMPAINGN_ITEM_CELL) != null ? mCampaignInfoMap.get(CAMPAINGN_ITEM_CELL).size() + DATA_TYPE_COUNT_SPECIAL : DATA_TYPE_COUNT_SPECIAL;
    }

//    public boolean isAdapterRealEmpty () {
//        int totalCount = 0;
//        for (Map.Entry<Integer, List<CampaignInfo>> entry : mCampaignInfoMap.entrySet()) {
//            List<CampaignInfo> value = entry.getValue();
//            totalCount += value.size();
//        }
//        return totalCount == 0 ? true : false;
//    }

    public boolean isAdapterRealEmpty () {
        int totalCount =  mCampaignInfoMap.get(CAMPAINGN_ITEM_CELL).size();
        return totalCount == 0 ? true : false;
    }


    private static class ViewHolder extends IViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void cacheInfoToFile(Map<Integer, List<CampaignInfo>> cacheMap) {
        if (cacheMap != null) {
            JSONObject jsonObject = new JSONObject();
//            JSONArray autoSlideJson = new JSONArray();
//            JSONArray displayJson = new JSONArray();
            JSONArray listItemJson = new JSONArray();

            for (Map.Entry<Integer, List<CampaignInfo>> entry : cacheMap.entrySet()) {
                int key = entry.getKey();
                List<CampaignInfo> value = entry.getValue();
                if (key == AUTO_SLIDE_VIEWPAGER) {
//                    fillDataToJsonArray(autoSlideJson, value);
                } else if (key == DISPLAY_VIEW) {
//                    fillDataToJsonArray(displayJson, value);
                } else if (key == CAMPAINGN_ITEM_CELL) {
                    fillDataToJsonArray(listItemJson, value);
                }
            }
            try {
//                if (autoSlideJson.length() > 0) {
//                    jsonObject.put("position_1", autoSlideJson);
//                }
//
//                if (displayJson.length() > 0) {
//                    jsonObject.put("position_2", displayJson);
//                }

                if (listItemJson.length() > 0) {
                    jsonObject.put("position_3", listItemJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 避免用空的数据覆盖了有效的缓存数据
            if (!TextUtils.isEmpty(jsonObject.toString())) {
                FileManager.getInstacne().saveDataToFile(FileManager.FILE_NAME, jsonObject.toString());
            }
        }
    }


    /**
     *
     * @param jsonArray 存放list里面数据的JsonArray;
     * @param list 要存储的list
     */

    private void fillDataToJsonArray(JSONArray jsonArray, List<CampaignInfo> list) {
        for (CampaignInfo item : list) {
            try {
                JSONObject temp = new JSONObject();
                temp.put("id", item.getId());
                temp.put("img_url", item.getCoverUrl());
                temp.put("position", item.getPosition());
                temp.put("sort", item.getSort());
                temp.put("url", item.getOpenUrl());
                temp.put("title", item.getTitle());
                temp.put("type", item.getCampaignType().getServerType());
                temp.put("tj_id", item.getStatisticId());
                temp.put("try_url", item.getTryUrl());
                temp.put("share_link", item.getShareLink());
                temp.put("share_title", item.getShareTitle());
                temp.put("share_desc", item.getShareDescription());
                temp.put("share_img", item.getShareImg());
                temp.put("try_tj_id", item.getTryNowId());
                temp.put("share_tj_id", item.getShareIconId());
                jsonArray.put(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
//        // 3种类型
//        if (position == AUTO_SLIDE_VIEWPAGER) {
//            return AUTO_SLIDE_VIEWPAGER;
//        } else if (position == DISPLAY_VIEW) {
//            return DISPLAY_VIEW;
//        } else {
//            return CAMPAINGN_ITEM_CELL;
//        }

//         2种类型
//        if (position == AUTO_SLIDE_VIEWPAGER) {
//            return AUTO_SLIDE_VIEWPAGER;
//        } else {
//            return CAMPAINGN_ITEM_CELL;
//        }

        return CAMPAINGN_ITEM_CELL;
    }

}
