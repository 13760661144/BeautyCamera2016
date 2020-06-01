package cn.poco.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;

/**
 * 话题按钮样式
 */
public class TopicButton extends FrameLayout
{
	public static final int TOPIC_BUTTON_MARGIN = 28;	//按钮两侧空隙，按xhdpi设计给为准

	private FrameLayout mBackground;
    private TextView mTopicName;
    private ImageView mTopicBmp;
    private TopicItem itemName;
    private String mUrl;
    
	public TopicButton(Context context) 
	{
		super(context);
		initItem(context);
	}
	
	private void initItem(Context context) 
	{		
		mBackground = new FrameLayout(context);		
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		this.addView(mBackground, fl);
		{
		    mTopicName = new TextView(context);
		    mTopicName.setTextColor(0xff777777);
		    mTopicName.setSingleLine();
		    mTopicName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		    mTopicName.setText("");
		    mBackground.addView(mTopicName, fl);
		    
		    mTopicBmp = new ImageView(context);
		    mBackground.addView(mTopicBmp, fl);
		}
	}
	
	/**
	 * 设置控件背景
	 * @param moreTopic 是否“更多”选项
	 */
	public void setButtonBG(int id, boolean moreTopic)
	{
		mBackground.setBackgroundResource(id);
		if(moreTopic)
		{
			mBackground.setPadding(ShareData.PxToDpi_xhdpi(32), 0, ShareData.PxToDpi_xhdpi(32), 0);
		}
		else
		{
			mBackground.setPadding(ShareData.PxToDpi_xhdpi(TOPIC_BUTTON_MARGIN), 0, ShareData.PxToDpi_xhdpi(TOPIC_BUTTON_MARGIN), 0);
		}
	}
	
	/**
	 * 设置控件文字
	 * @param item 显示的文字
	 */
	public void setText(TopicItem item)
	{
		if(item != null)
		{	
			itemName = item;
			mTopicName.setText(item.getTopic());
		}
	}
	
	/**
	 * 设置控件文字
	 * @param text 显示的文字
	 */
	public void setText(String text)
	{
		if(text != null && text.length() > 0)
		{	
			mTopicName.setText(text);
		}
	}
	
	public void setImage(Bitmap bmp)
	{
		if(bmp != null && !bmp.isRecycled())
		{
			mTopicBmp.setImageBitmap(bmp);
		}
	}
	
	/**
	 * 设置控件url地址
	 * @param url
	 */
	public void setUrl(String url)
	{
		if(url != null && url.length() > 0)
		{
			mUrl = url;
		}
	}
	
	/**
	 * 获取主题文字
	 * @return
	 */
	public String getText()
	{
		if(itemName != null)
		{	
			return itemName.getDisTopic();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 获取URL地址
	 * @return
	 */
	public String getUrl()
	{
		return mUrl;
	}
}
