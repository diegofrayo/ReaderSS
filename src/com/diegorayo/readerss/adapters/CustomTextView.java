package com.diegorayo.readerss.adapters;

import com.diegorayo.readerss.context.ApplicationContext;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Clase utilizada para crear un textview con una fuente personalizada
 */
public class CustomTextView extends TextView {

	public CustomTextView(Context context) {
		super(context);
		setCustomFont();
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont();
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont();
	}

	public void setCustomFont() {

		Typeface tf = Typeface
				.createFromAsset(ApplicationContext.getAssetsResource(),
						"fonts/MyriadBold.ttf");
		setTypeface(tf);
	}

}
