package com.app.bizlinked.customViews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.bizlinked.R;

public class TitleBar extends FrameLayout {

//    @BindView(R.id.llBackLayout)
    LinearLayout llBackLayout;
//    @BindView(R.id.ivLeftBackIcon)
    ImageView ivLeftBackIcon;
//    @BindView(R.id.tvLeftText)
    TextView tvLeftText;
//    @BindView(R.id.tvRightTextBtn)
    TextView tvRightTextBtn;
//    @BindView(R.id.tvTitle)
    TextView tvTitle;
//    @BindView(R.id.ivRightIconSearch)
    ImageView ivRightIconSearch;
//    @BindView(R.id.ivRightIcon1)
    ImageView ivRightIcon1;
//    @BindView(R.id.ivRightIcon2)
    ImageView ivRightIcon2;
//    @BindView(R.id.ivRightIconShare)
    ImageView ivRightIconShare;
//    @BindView(R.id.ivRightIconHeart)
    ImageView ivRightIconHeart;
//    @BindView(R.id.headerLayout)
    FrameLayout headerLayout;

    LinearLayout llAppLogoSection;

//    LinearLayout llSearchContainer;
//    EditText etSearch;
//    ImageView ivSearchCross;

    //    @BindView(R.id.stepsProfileProgressBar)
//    ProgressBar stepsProfileProgressBar;

    private Context context;

    public TitleBar(Context context) {
        super(context);
        this.context = context;
        initLayout(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
        if (attrs != null)
            initAttrs(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
        if (attrs != null)
            initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
    }

    private void initLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.titlebar, this);


        bindViews();
        resetViews();

    }


    public void resetViews() {

        headerLayout.setVisibility(GONE);

//        stepsProfileProgressBar.setVisibility(GONE);
//        stepsProfileProgressBar.setProgress(0);
//
//
//        llSearchContainer.setVisibility(GONE);

        llBackLayout.setVisibility(GONE);
        ivLeftBackIcon.setVisibility(GONE);
        tvLeftText.setVisibility(GONE);
        tvTitle.setVisibility(GONE);
        tvRightTextBtn.setVisibility(GONE);

        llAppLogoSection.setVisibility(GONE);
        ivRightIconSearch.setVisibility(GONE);
        ivRightIcon2.setVisibility(GONE);
        ivRightIcon1.setVisibility(GONE);
        ivRightIconShare.setVisibility(GONE);
        ivRightIconHeart.setVisibility(GONE);

        llBackLayout.setOnClickListener(null);
        tvTitle.setOnClickListener(null);
        llAppLogoSection.setOnClickListener(null);
        ivRightIconSearch.setOnClickListener(null);
        ivRightIcon2.setOnClickListener(null);
        ivRightIcon1.setOnClickListener(null);
        ivRightIconShare.setOnClickListener(null);
        ivRightIconHeart.setOnClickListener(null);

    }

    private void bindViews() {

        headerLayout = (FrameLayout) this.findViewById(R.id.headerLayout);

//        llSearchContainer = (LinearLayout) this.findViewById(R.id.llSearchContainer);
//        etSearch = (EditText) this.findViewById(R.id.etSearch);
//        ivSearchCross = (ImageView) this.findViewById(R.id.ivSearchCross);

        llBackLayout = (LinearLayout) this.findViewById(R.id.llBackLayout);

        tvRightTextBtn = (TextView) this.findViewById(R.id.tvRightTextBtn);
        tvTitle = (TextView) this.findViewById(R.id.tvTitle);
        llAppLogoSection = (LinearLayout) this.findViewById(R.id.llAppLogoSection);
        tvLeftText = (TextView) this.findViewById(R.id.tvLeftText);

        ivLeftBackIcon = (ImageView) this.findViewById(R.id.ivLeftBackIcon);
        ivRightIconSearch = (ImageView) this.findViewById(R.id.ivRightIconSearch);
        ivRightIcon2 = (ImageView) this.findViewById(R.id.ivRightIcon2);
        ivRightIcon1 = (ImageView) this.findViewById(R.id.ivRightIcon1);
        ivRightIconShare = (ImageView) this.findViewById(R.id.ivRightIconShare);
        ivRightIconHeart = (ImageView) this.findViewById(R.id.ivRightIconHeart);


//        stepsProfileProgressBar = (ProgressBar) this.findViewById(R.id.stepsProfileProgressBar);

    }


    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** ProgressBar Starts ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */


//    public void showProgressBar(){
//        stepsProfileProgressBar.setVisibility(View.VISIBLE);
//    }
//
//    public void setProgressBarMax(int max){
//        stepsProfileProgressBar.setMax(max);
//    }
//
//
//    public void setProgressBarProgress(int progress){
//        stepsProfileProgressBar.setProgress(progress);
//    }
//
//    public void hideProgressBar(){
//        stepsProfileProgressBar.setVisibility(View.GONE);
//        stepsProfileProgressBar.setMax(0);
//        stepsProfileProgressBar.setProgress(0);
//    }
//

    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** ProgressBar Ends ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */



    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** Header Layout Starts ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */


    public void showHeaderView(){
        resetViews();
        showShadowBottomFromTitleBar();
        headerLayout.setVisibility(View.VISIBLE);
    }

    public void hideHeaderView(){
        headerLayout.setVisibility(View.GONE);
    }

    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** Header Layout Ends ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */




    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** Back Layout Starts ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */

    public void showBackMenuView(){
        llBackLayout.setVisibility(View.VISIBLE);
    }


    public void showLeftIconAndListener(OnClickListener onClickListener) {

        showBackMenuView();

        ivLeftBackIcon.setVisibility(View.VISIBLE);

//        ivLeftBackIcon.setOnClickListener(onClickListener);
//        if(tvLeftText != null)
//            tvLeftText.setOnClickListener(onClickListener);

        if(llBackLayout != null){
            llBackLayout.setOnClickListener(onClickListener);
        }
    }


    public void showLeftIconAndListener(int iconResId, OnClickListener onClickListener) {
        ivLeftBackIcon.setVisibility(View.VISIBLE);
        ivLeftBackIcon.setImageResource(iconResId);

//        ivLeftBackIcon.setOnClickListener(onClickListener);
//        if(tvLeftText != null)
//            tvLeftText.setOnClickListener(onClickListener);

        if(llBackLayout != null){
            llBackLayout.setOnClickListener(onClickListener);
        }


    }


    public void setLeftTitleText(String text) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        @ColorInt int color;
        if(text.equals(getContext().getString(R.string.bizlinked)))
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        else
            theme.resolveAttribute(R.attr.secondaryTextColor, typedValue, true);

        color = typedValue.data;

        tvLeftText.setVisibility(View.VISIBLE);
        tvLeftText.setText(text);
        tvLeftText.setTextColor(color);
    }


    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** Back Layout Ends ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */



    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** Header Title Starts ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */
    public void showHeaderTitle() {
        llAppLogoSection.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void showHeaderTitle(int gravity) {
        llAppLogoSection.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        llAppLogoSection.setGravity(gravity);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        if(gravity != Gravity.CENTER)
            params.setMarginStart(35);
        else
            params.setMarginStart(0);

        params.gravity = gravity;
        llAppLogoSection.setLayoutParams(params);
    }


    public void showHeaderTitle(String text) {
        llAppLogoSection.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(text);
        //AnimationHelpers.animate(Techniques.FadeIn, 500, tvTitle);
    }

    public void setHeaderClickListener(OnClickListener onClickListener) {
        tvTitle.setOnClickListener(onClickListener);
        //AnimationHelpers.animate(Techniques.FadeIn, 500, tvTitle);
    }


    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** Header Title Ends ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */



    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** All Right Icons Starts ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */

//    public EditText getSearchEditext() {
//        return etSearch;
//    }
//
//    public ImageView getSearchCrossIcon() {
//        return ivSearchCross;
//    }
//
//    public LinearLayout getSearchContainer() {
//        return llSearchContainer;
//    }

    public void showRightSearchIconAndSetListener(OnClickListener onClickListener) {
        ivRightIconSearch.setVisibility(View.VISIBLE);
        ivRightIconSearch.setOnClickListener(onClickListener);

//        etSearch.addTextChangedListener(null);
//        etSearch.setOnEditorActionListener(null);

        //etSearch.setText("");


    //        AnimationHelpers.animate(Techniques.BounceInLeft, 500, ivLeftBackIcon);
    }





    public void showRightIcon2AndSetListener(int visibility, int imgResource, int color ,OnClickListener onClickListener) {
        ivRightIcon2.setImageResource(imgResource);
        ivRightIcon2.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        ivRightIcon2.setVisibility(visibility);
        ivRightIcon2.setOnClickListener(onClickListener);
    }


    public void showRightIcon1AndSetListener(int visibility, int imgResource, int color ,OnClickListener onClickListener) {
        ivRightIcon1.setImageResource(imgResource);
        ivRightIcon1.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        ivRightIcon1.setVisibility(visibility);
        ivRightIcon1.setOnClickListener(onClickListener);
    }


    public void showRightShareIconAndSetListener(OnClickListener onClickListener) {
        ivRightIconShare.setVisibility(View.VISIBLE);
        ivRightIconShare.setOnClickListener(onClickListener);
//        AnimationHelpers.animate(Techniques.BounceInLeft, 500, ivLeftBackIcon);
    }


    public void showRightHeartIconAndSetListener(OnClickListener onClickListener) {
        ivRightIconHeart.setVisibility(View.VISIBLE);
        ivRightIconHeart.setOnClickListener(onClickListener);
//        AnimationHelpers.animate(Techniques.BounceInLeft, 500, ivLeftBackIcon);
    }


    public void showRightHeartIconAndSetListener(int iconResId, OnClickListener onClickListener) {
        ivRightIconHeart.setVisibility(View.VISIBLE);
        ivRightIconHeart.setImageResource(iconResId);
        ivRightIconHeart.setOnClickListener(onClickListener);
//        AnimationHelpers.animate(Techniques.BounceInLeft, 500, ivLeftBackIcon);
    }

    public void showRightTextAndSetListener(String text, OnClickListener onClickListener) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

        tvRightTextBtn.setTextColor(color);
        tvRightTextBtn.setVisibility(View.VISIBLE);
        tvRightTextBtn.setText(text);
        tvRightTextBtn.setOnClickListener(onClickListener);
    }

    public void showRightTextAndColorAndClickListener(int visibility,String text, int color,  OnClickListener onClickListener) {

        tvRightTextBtn.setTextColor(color);
        tvRightTextBtn.setVisibility(visibility);
        tvRightTextBtn.setText(text);
        tvRightTextBtn.setOnClickListener(onClickListener);
    }


    /* ***************************************************************************************
     ******************************************************************************************
     * ****************************** All Right Icons Ends ***************************************
     * ******************************************************************************************
     * ****************************************************************************************** */


    public void showShadowBottomFromTitleBar(){
        headerLayout.setBackgroundResource(R.drawable.shadow_bottom);
    }
    public void removeShadowBottomFromTitleBar(){
        headerLayout.setBackground(null);
    }

}

