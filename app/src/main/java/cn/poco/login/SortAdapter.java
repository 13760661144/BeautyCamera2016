package cn.poco.login;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
    private List<SortModel> mSortModeList = null;
    private Context mContext;

    public SortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.mSortModeList = list;
    }

    public void updateListView(List<SortModel> list){
        this.mSortModeList = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mSortModeList.size();
    }

    public Object getItem(int position) {
        return mSortModeList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = mSortModeList.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = new ChoooseCountryItem(mContext);
            viewHolder.mdivTop1=((ChoooseCountryItem)view).mdivTop1;
            viewHolder.mCountry = ((ChoooseCountryItem)view).mCountry;
            viewHolder.mChoooseCountryTag =((ChoooseCountryItem)view).mChoooseCountryTag;
            viewHolder.mAreaCode =((ChoooseCountryItem)view).mPhone;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int section=0;
        if (position > 3){
             section = getSectionForPosition(position);
        }

        if(position == getPositionForSection(section)||position==0){
            ((RelativeLayout)viewHolder.mChoooseCountryTag.getParent()).setVisibility(View.VISIBLE);
            viewHolder.mChoooseCountryTag.setText(mContent.getSortLetters());
            viewHolder.mdivTop1.setVisibility(View.GONE);//屏蔽第一条分割线
        }else{
            ((RelativeLayout)viewHolder.mChoooseCountryTag.getParent()).setVisibility(View.GONE);
            viewHolder.mdivTop1.setVisibility(View.VISIBLE);
        }

        viewHolder.mCountry.setText(this.mSortModeList.get(position).getName());
        viewHolder.mAreaCode.setText("+" + this.mSortModeList.get(position).getAreaCode());

        return view;
    }



    final static class ViewHolder {
        View mdivTop1;
        TextView mChoooseCountryTag;
        TextView mCountry;
        TextView mAreaCode;
    }

    public int getSectionForPosition(int position) {
        return mSortModeList.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 4; i < getCount(); i++) {
            String sortStr = mSortModeList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}