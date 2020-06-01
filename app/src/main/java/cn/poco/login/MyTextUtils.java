package cn.poco.login;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import my.beautyCamera.R;

public class MyTextUtils
{

	/**
	 * 限制EditText输入字数
	 * @param inputText 要限制的EditText
	 * @param context
	 * @param maxLength 最大的字节数
	 * @param showToast 是否提示toast
	 */
	public static void setupLengthFilter(EditText inputText, final Context context, final int maxLength , final boolean showToast)
	{
		InputFilter.LengthFilter filter = new InputFilter.LengthFilter(maxLength)
		{
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
			{
				//				Log.v("Userinfo", "start: " + start + "end: " + end + "dstart: " + dstart + "dend: " + dend);
				//				Log.v("Userinfo", "source: " + source + "dest: " + dest);
				if (source.equals(" ")) {
					Toast.makeText(context, context.getResources().getString(R.string.textutils_nicknametips), Toast.LENGTH_SHORT).show();
					return "";
				}
				else
				{
					StringBuilder totalText = new StringBuilder();
					totalText.append(dest);
					totalText.append(source);
					int size = totalText.length();
					int len = 0;
					int keepByte = maxLength;
					int lastIndex = 0;
					for(int i = 0; i < size; i ++)
					{
						String text = totalText.charAt(i) + "";
						if(text.getBytes().length >= 2)
						{
							len += 2;
						}
						else
						{
							len ++;
						}
						keepByte = maxLength - len;
						lastIndex ++;
						if (keepByte <= 0)
						{
							break;
						}
					}
					int keep = lastIndex - dest.length() + dend - dstart;
					//				Log.v("Userinfo", "lastIndex: " + lastIndex + "keep: " + keep);
					if(keep <= 0 && size > 0)
					{
						if(showToast)
						{
							Toast.makeText(context, context.getResources().getString(R.string.textutils_overlimit), Toast.LENGTH_SHORT).show();
						}
						return "";
					}
					else if (keep >= end - start)
					{
						return null; // keep original
					} else
					{
						keep += start;
						if (Character.isHighSurrogate(source.charAt(keep - 1)))
						{
							--keep;
							if (keep == start)
							{
								return "";
							}
						}
						return source.subSequence(start, keep);
					}
				}

			}
		};
		inputText.setFilters(new InputFilter[]{filter});
	}	
}
