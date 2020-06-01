package cn.poco.advanced;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.RelativeLayout.LayoutParams;

import cn.poco.tianutils.ShareData;
public class BlessEditDialog extends Dialog{
	public BlessEditLayout mContentView;//布局对象
	public BlessEditDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initialize(context);
		
	}

	public BlessEditDialog(Context context, int theme) {
		super(context, theme);
		initialize(context);
	
	}
	
   public BlessEditDialog(Context context) {
		super(context);
		initialize(context);

	}
	
	/**
	 * 构造函数时,初始化弹出框
	 * @param context
	 */
	protected void initialize(Context context)
	{
		float scale = (float)ShareData.m_screenWidth / ShareData.m_resScale / 360f;
		if(scale>1){
			scale=1;
		}
		int dialog_w = (int) (ShareData.PxToDpi_xhdpi(653) * scale);
		int dialog_h = (int) (ShareData.PxToDpi_xhdpi(443) * scale);
		LayoutParams params = new LayoutParams(dialog_w, dialog_h);
		mContentView = new BlessEditLayout(context);
		mContentView.mDialog=this;
		this.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(mEditOkListener!=null)
				{
					mEditOkListener.onClose();
				}
				BlessEditDialog.this.dismiss();
			
			}
		});
	    setContentView(mContentView, params);
	}

	/**
	 * 回调接口
	 */
	public interface OnEditOkListener
	{
		void onEditOk(String strText, String strNickName);
		void onClose();
	}
	public OnEditOkListener mEditOkListener = null;
	public void setOnLoginOkListener(OnEditOkListener listener)
	{
		mEditOkListener = listener;
	}
	
	/**
	 * 外部调用设置文本
	 * @param strEdtText 输入文本
	 */
	public void setLayoutInputText(String strEdtText)
	{
		if(mContentView!=null)
		{
			mContentView.setInputText(strEdtText);
		}
	}
	/**
	 * 关闭按钮,事件
	 */
	public void onClose() {
		// TODO Auto-generated method stub
		if(mEditOkListener!=null)
		{
		mEditOkListener.onClose();
		}
		this.dismiss();
		
	}
   /**
    * 确定按钮,事件
    * @param strText     输入文本
    * @param strNickName 输入昵称
    */
	public void onEditTextOk(String strText,String strNickName) {
		// TODO Auto-generated method stub
		if(mEditOkListener!=null)
		{
			mEditOkListener.onEditOk(strText,strNickName);
		}
		this.dismiss();
	}
	
		@Override
	public void cancel() {
		// TODO Auto-generated method stub
		if(mContentView!=null)
		{
			mContentView.removeAllListener();
			mContentView.mDialog=null;
			mContentView=null;
		}
		super.cancel();
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		if(mContentView!=null)
		{
			mContentView.removeAllListener();
			mContentView.mDialog=null;
			mContentView=null;
		}
		super.dismiss();
	}
	
}
