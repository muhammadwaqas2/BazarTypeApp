package com.app.bizlinked.helpers.common;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.widget.TextView;

/**
 * Created by muhammadhumzakhan on 4/28/2017.
 */

public class TextUtilities {

    private static TextUtilities textUtilities;

    public static TextUtilities getInstance() {
        if(textUtilities == null)
            textUtilities = new TextUtilities();

        return textUtilities;
    }

    public static String getCapsSentences(String tagName) {
        String[] splits = tagName.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String eachWord = splits[i];
            if (i > 0 && eachWord.length() > 0) {
                sb.append(" ");
            }
            String cap = eachWord.substring(0, 1).toUpperCase()
                    + eachWord.substring(1);
            sb.append(cap);
        }
        return sb.toString();
    }

    public void setTextWithSpan(TextView textView, String text, String spanText, CharacterStyle style) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        int start = text.indexOf(spanText);
        int end = start + spanText.length();
        sb.setSpan(style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(sb);
    }

    public void setTextWithSpan(TextView textView, String text, String spanText1, String spanText2 ) {

        SpannableString wordtoSpan = new SpannableString(text);
        int start = text.indexOf(spanText1);
        int end = start + spanText1.length();
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4D4D")), start, end, 0);
        int start2 = text.indexOf(spanText2);
        int end2 = start2 + spanText2.length();
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4D4D")), start2, end2, 0);

        textView.setText(wordtoSpan , TextView.BufferType.SPANNABLE);
    }

    public void setTextWithSpan(TextView textView, String text, String spanText1, String spanText2, String spanText3, int color ) {
        SpannableString wordtoSpan = new SpannableString(text);
        int start = text.indexOf(spanText1);
        int end = start + spanText1.length();
        wordtoSpan.setSpan(new ForegroundColorSpan(color), start, end, 0);
        int start2 = text.indexOf(spanText2);
        int end2 = start2 + spanText2.length();
        wordtoSpan.setSpan(new ForegroundColorSpan(color), start2, end2, 0);
        int start3 = text.indexOf(spanText3);
        int end3 = start3 + spanText3.length();
        wordtoSpan.setSpan(new ForegroundColorSpan(color), start3, end3, 0);
        textView.setText(wordtoSpan, TextView.BufferType.SPANNABLE);
    }

    public void setTextWithFirstWordSpan(TextView textView, String text, MetricAffectingSpan style) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        int start = text.indexOf(" ");
        int end = text.length();
        sb.setSpan(style, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sb);
    }

    public void setTextWithWordSpan(TextView textView, String text, String spanText , CharacterStyle style) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        int start = text.indexOf(spanText);
        int end = start + spanText.length();
        sb.setSpan(style, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sb);
    }

    public void setTextWithSecondWordSpan(TextView textView, String text, MetricAffectingSpan style) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        int start = text.lastIndexOf(" ");
        int end = text.length();
        sb.setSpan(style, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sb);
    }
}
