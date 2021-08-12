package com.app.bizlinked.activities;

import android.animation.Animator;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import butterknife.BindView;

public class SplashActivity extends BaseActivity {

//    @BindView(R.id.tvAppTitle)
//    TextView tvAppTitle;
//
//    @BindView(R.id.tvAppDesc)
//    TextView tvAppDesc;

    @BindView(R.id.ivLogo)
    ImageView ivLogo;

    @Override
    public int getMainLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public int getFragmentFrameLayoutId() {
        return 0;
    }

    @Override
    protected void onViewReady() {
        glowAnimation();
    }

    @Override
    public void fragmentBackStackChangeListener(BaseFragment fragment) {

    }


    private void glowAnimation() {

        int animationSpeed = 800;

//        AnimationHelpers.animateWithCompletionListener(Techniques.FadeIn, animationSpeed, 0, ivLogo, null);
//        AnimationHelpers.animateWithCompletionListener(Techniques.FadeIn, animationSpeed * 2, 0, tvAppTitle, null);
//        AnimationHelpers.animateWithCompletionListener(Techniques.FadeIn, animationSpeed * 3, 0, tvAppDesc, new YoYo.AnimatorCallback() {
        AnimationHelpers.animateWithCompletionListener(Techniques.FadeIn, animationSpeed, 0, ivLogo, new YoYo.AnimatorCallback() {
        @Override
            public void call(Animator animator) {
                changeActivity(MainActivity.class, true);
            }
        });
    }

    public TitleBar getTitleBar() {
        return null;
    }


}
