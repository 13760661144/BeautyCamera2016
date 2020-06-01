package cn.poco.cloudAlbum.adapter;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

import cn.poco.cloudalbumlibs.adapter.AbsAlbumFolderAdapter;
import cn.poco.cloudalbumlibs.model.FolderInfo;
import my.beautyCamera.R;

/**
 * Created by admin on 2016/9/13.
 */
public class CloudAlbumFolderAdapter extends AbsAlbumFolderAdapter {

    public CloudAlbumFolderAdapter(Context context, List<FolderInfo> list) {
        super(context, list);
    }

    @Override
    protected void displayImage(String url, ImageView viewHolder, int resDrawableID) {
        super.displayImage(url, viewHolder, R.drawable.beauty_cloudalbum_create_album);
    }
}
