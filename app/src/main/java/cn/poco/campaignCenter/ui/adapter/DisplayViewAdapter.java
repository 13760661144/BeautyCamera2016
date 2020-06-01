package cn.poco.campaignCenter.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.ui.cells.CampaignCell;


/**
 * Created by Shine on 2016/11/30.
 */

public class DisplayViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<CampaignInfo> mInfoList;
    private Context mContext;
    private View.OnClickListener mDelegate;

    public DisplayViewAdapter(Context context, List<CampaignInfo> list, View.OnClickListener delegate) {
        this.mContext = context;
        mInfoList = list;
        this.mDelegate = delegate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        CampaignCell iv = new CampaignCell(mContext);
        viewHolder = new ViewHolder(iv);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDelegate != null) {
                    if (v instanceof CampaignCell) {
                        CampaignCell cell = (CampaignCell) v;
                        mDelegate.onClick(cell);
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CampaignCell item = (CampaignCell) holder.itemView;
        item.setData(mInfoList.get(position), 1);
    }

    @Override
    public int getItemCount() {
        return mInfoList == null ? 0 : mInfoList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
