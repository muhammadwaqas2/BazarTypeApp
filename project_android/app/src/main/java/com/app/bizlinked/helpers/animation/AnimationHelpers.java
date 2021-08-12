package com.app.bizlinked.helpers.animation;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class AnimationHelpers {


    public static void animate(Techniques techniques, int duration, final View view) {

        if(view != null){
            YoYo.with(techniques)
                    .onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            if (view.getVisibility() == View.INVISIBLE || view.getVisibility() == View.GONE)
                                view.setVisibility(View.VISIBLE);
                            else {
                                view.setVisibility(View.VISIBLE);
                            }

                        }
                    })
                    .duration(duration)
                    .repeat(0)
                    .playOn(view);
        }

    }

    public static void animate(Techniques techniques, int duration, final View view, int visibility) {

        if(view != null){
            YoYo.with(techniques)
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            view.setVisibility(visibility);

                        }
                    })
                    .duration(duration)
                    .repeat(0)
                    .playOn(view);
        }

    }

    public static void simpleAnimate(Techniques techniques, int duration, final View view) {

        if(view != null){
            YoYo.with(techniques)
                    .duration(duration)
                    .repeat(0)
                    .playOn(view);
        }

    }

    public static void animate(Techniques techniques, int duration, int repeat, final View view) {
        //view.clearAnimation();
        // view.clearFocus();

        if(view != null) {
            YoYo.with(techniques)
                    .onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            if (view.getVisibility() == View.INVISIBLE || view.getVisibility() == View.GONE)
                                view.setVisibility(View.VISIBLE);

                        }
                    }).duration(duration)
                    .repeat(repeat)
                    .playOn(view);
            view.clearFocus();
            view.clearAnimation();
        }
    }


    public static void animateWithCompletionListener(Techniques techniques, int duration, int delay, final View view, YoYo.AnimatorCallback animatorEndCallback) {

        if(view != null) {
            YoYo.AnimationComposer animationComposer = YoYo.with(techniques);
            animationComposer.onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    if (view.getVisibility() == View.INVISIBLE || view.getVisibility() == View.GONE)
                        view.setVisibility(View.VISIBLE);
                    else {
                        view.setVisibility(View.VISIBLE);
                    }

                }
            });

            if (animatorEndCallback != null)
                animationComposer.onEnd(animatorEndCallback);

            animationComposer.duration(duration);
            animationComposer.delay(delay);
            animationComposer.repeat(0);
            animationComposer.playOn(view);
        }

    }

    public static void simpleAnimateWithCompletionListener(Techniques techniques, int duration, final View view, YoYo.AnimatorCallback animatorEndCallback) {

        if(view != null) {
            YoYo.AnimationComposer animationComposer = YoYo.with(techniques);
//            animationComposer.onStart(new YoYo.AnimatorCallback() {
//                @Override
//                public void call(Animator animator) {
//                    if (view.getVisibility() == View.INVISIBLE || view.getVisibility() == View.GONE)
//                        view.setVisibility(View.VISIBLE);
//                    else {
//                        view.setVisibility(View.VISIBLE);
//                    }
//
//                }
//            });

            if (animatorEndCallback != null)
                animationComposer.onEnd(animatorEndCallback);

            animationComposer.duration(duration);
            //animationComposer.delay(delay);
            animationComposer.repeat(0);
            animationComposer.playOn(view);
        }

    }



    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {
        if(textview != null) {

            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
            valueAnimator.setDuration(1500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    textview.setText(valueAnimator.getAnimatedValue().toString());
                }
            });
            valueAnimator.start();
        }
    }


    public static void animateViewHeight(int initialValue, int finalValue, final View viewToIncreaseHeight) {
        if(viewToIncreaseHeight != null) {

            ValueAnimator anim = ValueAnimator.ofInt(initialValue, finalValue);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = viewToIncreaseHeight.getLayoutParams();
                    layoutParams.height = val;
                    viewToIncreaseHeight.setLayoutParams(layoutParams);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewToIncreaseHeight.setVisibility(View.VISIBLE);
                        }
                    }, 100);

                }
            });
            anim.setDuration(1500);
            anim.start();
        }
    }


    public static void animateTextViewWithAppendString(int initialValue, int finalValue, final TextView textview,final String appendString) {

        if(textview != null) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
            valueAnimator.setDuration(1500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    textview.setText(String.format("%s%s", valueAnimator.getAnimatedValue().toString(), appendString));
                }
            });
            valueAnimator.start();
        }
    }


    public static void animateTextViewWithAppendString(int colorFrom, int colorTo, final View view) {

        if(view != null) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(250); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    view.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
    }


}
