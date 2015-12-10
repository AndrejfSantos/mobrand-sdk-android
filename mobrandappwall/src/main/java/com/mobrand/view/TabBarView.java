package com.mobrand.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.mobrand.appwall.A.R;
import com.mobrand.view.TabView;

/**
 * Created by rmateus on 04-04-2015.
 */
public class TabBarView extends LinearLayout {

    private static final int STRIP_HEIGHT = 6;

    public final Paint paint;

    private int stripHeight;
    private float offset;
    private int selectedTab = -1;

    private OnTabClickedListener onTabClickedListener;

    public interface OnTabClickedListener{
        void onTabClicked(int index);
    }

    public TabBarView(Context context) {
        this(context, null);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        paint = new Paint();

        paint.setColor(Color.WHITE);
        stripHeight = (int) (STRIP_HEIGHT * getResources().getDisplayMetrics().density + .5f);
        setAttributes(context, attrs);
    }

    private void setAttributes(Context context, AttributeSet attrs){
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.TabBarView, 0, 0);
            if (typedArray != null) {

                int n = typedArray.getIndexCount();

                for (int i = 0; i < n; i++){
                    int attr = typedArray.getIndex(i);

                    if (attr == R.styleable.TabBarView_tabBarColor) {
                        int color = typedArray.getColor(i, Color.WHITE);
                        paint.setColor(color);

                    } else if (attr == R.styleable.TabBarView_tabBarSize) {
                        int size = typedArray.getDimensionPixelSize(i, (int) (STRIP_HEIGHT * getResources().getDisplayMetrics().density + .5f));
                        stripHeight = size;

                    }
                }
                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setupTabViews();
    }

    private void setupTabViews() {
        if (getChildCount() > 0){
            for (int i = 0; i < getChildCount(); i++){
                TabView child = (TabView) getChildAt(i);
                if (child != null) {
                    child.setTag(i);
                    child.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = (Integer) v.getTag();
                            if (onTabClickedListener != null) {
                                onTabClickedListener.onTabClicked(index);
                            }
                        }
                    });
                }
            }
        }
    }

    public void setStripColor(int color) {
        if (paint.getColor() != color) {
            paint.setColor(color);
            invalidate();
        }
    }

    public void setStripHeight(int height) {
        if (stripHeight != height) {
            stripHeight = height;
            invalidate();
        }
    }

    public void setSelectedTab(int tabIndex) {
        if (tabIndex < 0) {
            tabIndex = 0;
        }
        final int childCount = getChildCount();
        if (tabIndex >= childCount) {
            tabIndex = childCount - 1;
        }
        if (selectedTab != tabIndex) {
            selectedTab = tabIndex;
            invalidate();
        }
    }

    public void setOffset(float offset) {
        if (this.offset != offset) {
            this.offset = offset;
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // Draw the strip manually
        final TabView child = (TabView) getChildAt(selectedTab);
        if (child != null) {
            int left = child.getLeft();
            int right = child.getRight();
            if (offset > 0) {
                final TabView nextChild = (TabView) getChildAt(selectedTab + 1);
                if (nextChild != null) {
                    left = (int) (child.getLeft() + offset * (nextChild.getLeft() - child.getLeft()));
                    right = (int) (child.getRight() + offset * (nextChild.getRight() - child.getRight()));
                }
            }
            canvas.drawRect(left, getHeight() - stripHeight, right, getHeight(), paint);
        }
    }

    public void setOnTabClickedListener(OnTabClickedListener onTabClickedListener) {
        this.onTabClickedListener = onTabClickedListener;
    }

    public TabView getTab(int position) {
        return (TabView) getChildAt(position);
    }

}
