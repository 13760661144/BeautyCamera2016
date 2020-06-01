package cn.poco.widget;

import android.content.Context;
import android.graphics.Point;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.util.ArrayList;

public class NumberHighlightTextView extends android.support.v7.widget.AppCompatTextView
{

	private boolean isHightLightBold;

	public NumberHighlightTextView(Context context) {
		super(context);
	}

	private int color = -1;
	private int highlightColor = -1;
	private boolean priceTx;

	public void setText(String text) {
		if (color != -1) {
			super.setTextColor(color);
		}
		if (text == null) {
			super.setText("");
			return;
		}
		SpannableString sp = new SpannableString(text);
		if (highlightColor != -1) {
			for (int i = 0; i < text.length(); i++) {
				char s = text.charAt(i);
				if (s == '.') {
					if (i != text.length() - 1 && isNumber(text.charAt(i - 1)) && isNumber(text.charAt(i + 1))) {
						sp.setSpan(new ForegroundColorSpan(highlightColor), i, i + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						if(isHightLightBold){
							sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					} else {
						break;
					}
				} else if (isNumber(s)) {
					sp.setSpan(new ForegroundColorSpan(highlightColor), i, i + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					if(isHightLightBold){
						sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		setText(sp);
	}

	private int ￥ = '￥', ¥ = '¥', $ = '$', £ = '£', € = '€';
	private int max = '9', min = '0';

	private void setPriceText(String text) {
		if (color != -1) {
			super.setTextColor(color);
		}
		if (text == null) {
			super.setText("");
			return;
		}
		ArrayList<Point> indexs = new ArrayList<Point>();
		SpannableString sp = new SpannableString(text);
		if (highlightColor != -1) {
			for (int i = 0; i < text.length(); i++) {
				char s = text.charAt(i);
				if (s == ￥ || s == ¥ || s == $ || s == £ || s == €) {
					Point item = new Point();
					item.x = i;
					++i;
					for (; i < text.length(); i++) {
						char t = text.charAt(i);
						if (t == '.') {
							if (i != text.length() - 1 && isNumber(text.charAt(i - 1)) && isNumber(text.charAt(i + 1))) {
								item.y = i;
							} else {
								break;
							}
						} else if (isNumber(t)) {
							item.y = i;
						} else {
							break;
						}
					}
					indexs.add(item);
				}
			}
			for (Point item : indexs) {
				if (item.y - 1 > item.x) {
					sp.setSpan(new ForegroundColorSpan(highlightColor), item.x, item.y + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}
			}
		}
		setText(sp);
	}

	private boolean isNumber(char s) {
		return (min <= s && s <= max);
	}

	public void setPriceHightlightText(String text) {
		priceTx = true;
		if (text != null) {
			setPriceText(text.toString().trim());
			return;
		}
		super.setText(text);
	}

	public void setTexts(CharSequence text) {
		if (text != null) {
			setText(text.toString());
			return;
		}
		super.setText(text);
	}

	public void setHighlightTextColor(int highlightColor) {
		this.highlightColor = highlightColor;
		if (getText() != null) {
			if (priceTx) {
				setPriceText(getText().toString());
			} else {
				setText(getText().toString());
			}
		}
	}

	public void setTextColor(int color) {
		this.color = color;
		super.setTextColor(color);
		if (highlightColor != -1 && getText() != null) {
			if (priceTx) {
				setPriceText(getText().toString());
			} else {
				setText(getText().toString());
			}
		}
	}

	/**
	 * call it before setText
	 * */
	public void setHightLightBold(boolean isHightLightBold){
		this.isHightLightBold = isHightLightBold;
	}
}
