package com.mobrand.appwall.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobrand.appwall.classic.R;


/**
 * Created by rmateus on 04-04-2015.
 */
public class TabView extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.button);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.mb_tab_view_merge, this);
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text);
        setAttributes(context, attrs);
        setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.app_bar_height));
        setGravity(Gravity.CENTER);
        int eightDp = getResources().getDimensionPixelSize(R.dimen.eight_dp);
        setPadding(eightDp, 0, eightDp, 0);
    }

    private void setAttributes(Context context, AttributeSet attrs){
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            TypedArray typedArray = theme.obtainStyledAttributes(attrs,R.styleable.tab_view, 0, 0);
            if (typedArray != null) {

                int n = typedArray.getIndexCount();

                for (int i = 0; i < n; i++){
                    int attr = typedArray.getIndex(i);

                    if (attr == R.styleable.tab_view_mb_src) {
                        int src = typedArray.getResourceId(i, 0);
                        setIcon(src);

                    } else if (attr == R.styleable.tab_view_mb_text) {
                        CharSequence text = typedArray.getText(attr);
                        setText(text);

                    }
                }
                typedArray.recycle();
            }
        }
    }

    public void setIcon(int resId) {
        setIcon(getContext().getResources().getDrawable(resId));
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(icon);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    public void setText(int resId) {
        setText(getContext().getString(resId));
    }

    public void setText(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            if (textView != null) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(text);
            }
        } else {
            if (textView != null) {
                textView.setVisibility(View.GONE);
            }
        }
        updateHint();
    }

    public ImageView getImageView(){
        return imageView;
    }

    @Override
    public void setContentDescription(CharSequence contentDescription) {
        super.setContentDescription(contentDescription);
        updateHint();
    }

    private void updateHint() {
        boolean needHint = false;
        if (textView == null || textView.getVisibility() == View.GONE) {
            if (!TextUtils.isEmpty(getContentDescription())) {
                needHint = true;
            } else {
                needHint = false;
            }
        }

        if (needHint) {
            setOnLongClickListener(mOnLongClickListener);
        } else {
            setOnLongClickListener(null);
            setLongClickable(false);
        }
    }

    private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            getLocationOnScreen(screenPos);

            final Context context = getContext();
            final int width = getWidth();
            final int height = getHeight();
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

            Toast cheatSheet = Toast.makeText(context, getContentDescription(), Toast.LENGTH_SHORT);

            // Show under the tab
            cheatSheet.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, (screenPos[0] + width / 2) - screenWidth / 2,
                    height);

            cheatSheet.show();
            Log.d("TabView", "onLongClick");
            return true;
        }
    };

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            if (textView != null) {
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                textView.setMaxWidth(R.dimen.tab_view_text_width_landscape);
                textView.setLayoutParams(params);
            }
        }else{
            if (textView != null) {
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                params.width = (int) getResources().getDimension(R.dimen.tab_view_text_width_portrait);
                textView.setMaxWidth(R.dimen.tab_view_text_width_portrait);
                textView.setLayoutParams(params);
            }
        }
    }
}
