package com.app.bizlinked.helpers.validation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;


public class ValidationHelpers {

//    public static void setButtonTransparency(float transparency, Button btn){
//        btn.setAlpha(transparency);
//        if(transparency == 1.0f)
//            btn.setEnabled(true);
//        else
//            btn.setEnabled(false);
//    }
//
//
//    public static void resetAllErrors(ViewGroup v) {
//        ArrayList<CustomTextInputLayout> customInputLayout = traverseAllCustomInputLayout(v);
//        for (int layoutViewIndex  = 0; layoutViewIndex  < customInputLayout.size(); layoutViewIndex ++) {
//            CustomTextInputLayout customTextInputLayout = customInputLayout.get(layoutViewIndex);
//            customTextInputLayout.errorEnable(false);
//        }
//    }
//
//    public static  ArrayList<CustomTextInputLayout> traverseAllCustomInputLayout(ViewGroup v) {
//        ArrayList<CustomTextInputLayout> list = new ArrayList<>();
//        for (int i = 0; i < v.getChildCount(); i++) {
//            Object child = v.getChildAt(i);
//            if (child instanceof CustomTextInputLayout) {
//                list.add(((CustomTextInputLayout) child));
//            }
//        }
//        return list;
//    }

    public static EditText getEditText(ViewGroup mainView) {

        ArrayList<View> allViewsWithinMyTopView = getAllChildren(mainView);
        for (View child : allViewsWithinMyTopView) {
            if (child instanceof EditText) {
                return (EditText) child;
            }
        }
        return null;
    }


    private static ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

}
