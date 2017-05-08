package com.luseen.luseenbottomnavigation.BottomNavigation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luseen.luseenbottomnavigation.R;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigationView extends RelativeLayout {

    private OnBottomNavigationItemClickListener onBottomNavigationItemClickListener;

    private final int NAVIGATION_HEIGHT = (int) getResources().getDimension(com.luseen.luseenbottomnavigation.R.dimen.bottom_navigation_height);
    private final int NAVIGATION_LINE_WIDTH = (int) getResources().getDimension(R.dimen.bottom_navigation_line_width);

    private static int currentItem = 0;

    private Context context;
    private float textActiveSize;
    private float textInactiveSize;

    private List<BottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();

    private int itemActiveColorWithoutColoredBackground = -1;

    private int navigationWidth;

    private int shadowHeight;

    private int itemInactiveColor;

    private int itemWidth;

    private int itemHeight;

    private boolean withText;

    private boolean coloredBackground;

    private boolean disableShadow;

    private boolean isTablet;

    private boolean viewPagerSlide;

    private boolean isCustomFont = false;

    private FrameLayout mainContainer;
    private LinearLayout itemsContainer;

    private View backgroundColorTemp;

    private ViewPager mViewPager;

    private Typeface font;

    public BottomNavigationView(Context context) {
        this(context, null);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            Resources res = getResources();

            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BottomNavigationView);
            withText = array.getBoolean(R.styleable.BottomNavigationView_bnv_with_text, true);
            coloredBackground = array.getBoolean(R.styleable.BottomNavigationView_bnv_colored_background, true);
            disableShadow = array.getBoolean(R.styleable.BottomNavigationView_bnv_shadow, false);
            isTablet = array.getBoolean(R.styleable.BottomNavigationView_bnv_tablet, false);
            viewPagerSlide = array.getBoolean(R.styleable.BottomNavigationView_bnv_viewpager_slide, true);
            itemActiveColorWithoutColoredBackground = array.getColor(R.styleable.BottomNavigationView_bnv_active_color, -1);
            itemInactiveColor = array.getColor(R.styleable.BottomNavigationView_bnv_inactive_color, -1);
            textActiveSize = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bnv_active_text_size, res.getDimensionPixelSize(R.dimen.bottom_navigation_text_size_active));
            textInactiveSize = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bnv_inactive_text_size, res.getDimensionPixelSize(R.dimen.bottom_navigation_text_size_inactive));

            array.recycle();
        }

        initLayoutRoot();
        setColoredBackgroundMode();
    }

    private void setColoredBackgroundMode() {
        if (coloredBackground) {
            itemActiveColorWithoutColoredBackground = ContextCompat.getColor(context, com.luseen.luseenbottomnavigation.R.color.colorActive);
            shadowHeight = (int) getResources().getDimension(com.luseen.luseenbottomnavigation.R.dimen.bottom_navigation_shadow_height);
        } else {
            if (itemActiveColorWithoutColoredBackground == -1)
                itemActiveColorWithoutColoredBackground = ContextCompat.getColor(context, com.luseen.luseenbottomnavigation.R.color.itemActiveColorWithoutColoredBackground);
            shadowHeight = (int) getResources().getDimension(com.luseen.luseenbottomnavigation.R.dimen.bottom_navigation_shadow_height_without_colored_background);
        }
    }

    private void initLayoutRoot() {
        navigationWidth = BottomNavigationUtils.getActionbarSize(context);

        LayoutParams containerParams, params, lineParams;
        backgroundColorTemp = new View(context);
        viewList.clear();

        mainContainer = new FrameLayout(context);
        View shadow = new View(context);
        View line = new View(context);
        itemsContainer = new LinearLayout(context);
        itemsContainer.setOrientation(isTablet ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        LayoutParams shadowParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, shadowHeight);
        if (isTablet) {
            line.setBackgroundColor(ContextCompat.getColor(context, R.color.colorInactive));
            containerParams = new LayoutParams(navigationWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            lineParams = new LayoutParams(NAVIGATION_LINE_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT);
            lineParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params = new LayoutParams(navigationWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            itemsContainer.setPadding(0, itemHeight / 2, 0, 0);
            addView(line, lineParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                LayoutParams backgroundLayoutParams = new LayoutParams(
                        navigationWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                backgroundLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                mainContainer.addView(backgroundColorTemp, backgroundLayoutParams);
            }
        } else {
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, NAVIGATION_HEIGHT);
            containerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, NAVIGATION_HEIGHT);
            shadowParams.addRule(RelativeLayout.ABOVE, mainContainer.getId());
            shadow.setBackgroundResource(com.luseen.luseenbottomnavigation.R.drawable.shadow);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                LayoutParams backgroundLayoutParams = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, NAVIGATION_HEIGHT);
                backgroundLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mainContainer.addView(backgroundColorTemp, backgroundLayoutParams);
            }
        }
        containerParams.addRule(isTablet ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(shadow, shadowParams);
        addView(mainContainer, containerParams);
        mainContainer.addView(itemsContainer, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup.LayoutParams params = getLayoutParams();

        if (isTablet) {
            params.width = navigationWidth + NAVIGATION_LINE_WIDTH;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = disableShadow ? NAVIGATION_HEIGHT : NAVIGATION_HEIGHT + shadowHeight;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(com.luseen.luseenbottomnavigation.R.dimen.bottom_navigation_elevation));
            }
        }
        setLayoutParams(params);
    }

    private void updateItemSize() {
        if (isTablet) {
            itemWidth = LayoutParams.MATCH_PARENT;
            itemHeight = navigationWidth;
        } else {
            itemWidth = LayoutParams.MATCH_PARENT;
            itemHeight = LayoutParams.MATCH_PARENT;
        }
    }

    private void addTabView(BottomNavigationItem item) {
        updateItemSize();

        int white = ContextCompat.getColor(context, com.luseen.luseenbottomnavigation.R.color.white);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final int index = bottomNavigationItems.size();
        bottomNavigationItems.add(item);

        if (!coloredBackground) {
            item.setColor(white);
        }

        int textActivePaddingTop = (int) context.getResources().getDimension(com.luseen.luseenbottomnavigation.R.dimen.bottom_navigation_padding_top_active);
        int viewInactivePaddingTop = (int) context.getResources().getDimension(com.luseen.luseenbottomnavigation.R.dimen.bottom_navigation_padding_top_inactive);
        int viewInactivePaddingTopWithoutText = (int) context.getResources().getDimension(com.luseen.luseenbottomnavigation.R.dimen.bottom_navigation_padding_top_inactive_without_text);
        final View view = inflater.inflate(com.luseen.luseenbottomnavigation.R.layout.bottom_navigation, this, false);
        ImageView icon = (ImageView) view.findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_item_icon);
        TextView title = (TextView) view.findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_item_title);
        View badge = view.findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_item_badge);

        if (isCustomFont) {
            title.setTypeface(font);
        }

        if (isTablet) {
            title.setVisibility(GONE);
        }

        title.setTextColor(itemInactiveColor);
        viewList.add(view);

        boolean selected = index == currentItem;

        if (item.getImageResourceActive() != 0) {
            if (selected) {
                icon.setImageResource(item.getImageResourceActive());
            } else {
                item.getImageResource();
            }
        } else {
            icon.setImageResource(item.getImageResource());
            icon.setColorFilter(selected ? itemActiveColorWithoutColoredBackground : itemInactiveColor);
        }

        if (item.showBadge()) {
            badge.setVisibility(VISIBLE);
        } else {
            badge.setVisibility(GONE);
        }

        if (selected) {
            mainContainer.setBackgroundColor(item.getColor());
            title.setTextColor(itemActiveColorWithoutColoredBackground);
        }


        int paddingFromText = withText ? viewInactivePaddingTop : viewInactivePaddingTopWithoutText;
        int paddingFromEdge = selected ? textActivePaddingTop : paddingFromText;

        if (isTablet) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), paddingFromEdge, view.getPaddingBottom());
        } else {
            view.setPadding(view.getPaddingLeft(), paddingFromEdge, view.getPaddingRight(), view.getPaddingBottom());
        }

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, selected ? textActiveSize : withText ? textInactiveSize : 0);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, textActiveSize);
        title.setText(item.getTitle());
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(itemWidth, itemHeight, 1);
        itemsContainer.addView(view, itemParams);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomNavigationItemClick(index);
            }
        });
    }

    private void onBottomNavigationItemClick(final int itemIndex) {
        for (int i = 0; i < viewList.size(); i++) {
            if (i == itemIndex) {
                View view = viewList.get(itemIndex).findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_container);
                final TextView title = (TextView) view.findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_item_title);
                final ImageView icon = (ImageView) view.findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_item_icon);
                BottomNavigationUtils.changeTextColor(title, itemInactiveColor, itemActiveColorWithoutColoredBackground);

                if (bottomNavigationItems.get(i).getImageResourceActive() != 0) {
                    icon.setImageResource((bottomNavigationItems.get(i).getImageResourceActive()));
                } else {
                    BottomNavigationUtils.changeImageColorFilter(icon, itemInactiveColor, itemActiveColorWithoutColoredBackground);
                }

                BottomNavigationUtils.changeViewBackgroundColor
                        (mainContainer, bottomNavigationItems.get(currentItem).getColor(), bottomNavigationItems.get(itemIndex).getColor());
            } else if (i == currentItem) {
                View view = viewList.get(i).findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_container);
                final TextView title = (TextView) view.findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_item_title);
                final ImageView icon = (ImageView) view.findViewById(com.luseen.luseenbottomnavigation.R.id.bottom_navigation_item_icon);

                if (bottomNavigationItems.get(i).getImageResourceActive() != 0) {
                    icon.setImageResource((bottomNavigationItems.get(i).getImageResource()));
                } else {
                    BottomNavigationUtils.changeImageColorFilter(icon, itemActiveColorWithoutColoredBackground, itemInactiveColor);
                }

                BottomNavigationUtils.changeTextColor(title, itemActiveColorWithoutColoredBackground, itemInactiveColor);
            }
        }

        if (mViewPager != null)
            mViewPager.setCurrentItem(itemIndex, viewPagerSlide);

        if (onBottomNavigationItemClickListener != null)
            onBottomNavigationItemClickListener.onNavigationItemClick(itemIndex);
        currentItem = itemIndex;
    }

    /**
     * Creates a connection between this navigation view and a ViewPager
     *
     * @param pager          pager to connect to
     * @param colorResources color resources for every item in the ViewPager adapter
     * @param imageResources images resources for every item in the ViewPager adapter
     */

    public void setUpWithViewPager(ViewPager pager, int[] colorResources, int[] imageResources) {
        this.mViewPager = pager;
        if (pager.getAdapter().getCount() != colorResources.length || pager.getAdapter().getCount() != imageResources.length)
            throw new IllegalArgumentException("colorResources and imageResources must be equal to the ViewPager items : " + pager.getAdapter().getCount());

        for (int i = 0; i < pager.getAdapter().getCount(); i++)
            addTab(new BottomNavigationItem(pager.getAdapter().getPageTitle(i).toString(), colorResources[i], imageResources[i]));
    }

    /**
     * Add item for BottomNavigation
     *
     * @param item item to add
     */
    public void addTab(BottomNavigationItem item) {
        addTabView(item);
    }

    /**
     * Activate BottomNavigation tablet mode
     */
    public void activateTabletMode() {
        isTablet = true;
    }

    /**
     * Change text visibility
     *
     * @param withText disable or enable item text
     */
    public void isWithText(boolean withText) {
        this.withText = withText;
    }

    /**
     * Item Active Color if isColoredBackground(false)
     *
     * @param itemActiveColorWithoutColoredBackground active item color
     */
    public void setItemActiveColorWithoutColoredBackground(int itemActiveColorWithoutColoredBackground) {
        this.itemActiveColorWithoutColoredBackground = itemActiveColorWithoutColoredBackground;
    }

    /**
     * With this BottomNavigation background will be white
     *
     * @param coloredBackground disable or enable background color
     */
    public void isColoredBackground(boolean coloredBackground) {
        this.coloredBackground = coloredBackground;
        setColoredBackgroundMode();
    }

    /**
     * Change tab programmatically
     *
     * @param position selected tab position
     */
    public void selectTab(int position) {
        onBottomNavigationItemClick(position);
        currentItem = position;
    }

    /**
     * Disable shadow of BottomNavigationView
     */
    public void disableShadow() {
        disableShadow = true;
    }

    /**
     * Disable slide animation when using ViewPager
     */
    public void disableViewPagerSlide() {
        viewPagerSlide = false;
    }

    /**
     * Change Active text size
     *
     * @param textActiveSize size
     */
    public void setTextActiveSize(float textActiveSize) {
        this.textActiveSize = textActiveSize;
    }

    /**
     * Change Inactive text size
     *
     * @param textInactiveSize size
     */
    public void setTextInactiveSize(float textInactiveSize) {
        this.textInactiveSize = textInactiveSize;
    }

    /**
     * Setup interface for item onClick
     */
    public void setOnBottomNavigationItemClickListener(OnBottomNavigationItemClickListener onBottomNavigationItemClickListener) {
        this.onBottomNavigationItemClickListener = onBottomNavigationItemClickListener;
    }

    /**
     * Returns the item that is currently selected
     *
     * @return Currently selected item
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * set custom font for item texts
     *
     * @param font custom font
     */
    public void setFont(Typeface font) {
        isCustomFont = true;
        this.font = font;
    }

    /**
     * get item text size on active status
     *
     * @return font size
     */
    public float getTextActiveSize() {
        return textActiveSize;
    }

    /**
     * get item text size on inactive status
     *
     * @return font size
     */
    public float getTextInactiveSize() {
        return textInactiveSize;
    }


    public BottomNavigationItem getItem(int position) {
        onBottomNavigationItemClick(position);
        return bottomNavigationItems.get(position);
    }

    public void showBadge(int position, boolean show) {
        BottomNavigationItem item = bottomNavigationItems.get(position);
        item.setShowBadge(show);
        for (int i = 0; i < viewList.size(); i++) {
            if (i == position) {
                View view = viewList.get(position).findViewById(R.id.bottom_navigation_item_badge);
                view.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }
}
