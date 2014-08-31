package jp.egg.android.util;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chikara on 2014/08/30.
 */
public class AUtil {

//    private void makeAboutSpannable(SpannableStringBuilder span, String str_link, String replace, final Runnable on_click){
//        Pattern pattern = Pattern.compile(str_link);
//        Matcher matcher = pattern.matcher(span);
//        ForegroundColorSpan color_theme = new ForegroundColorSpan(Color.parseColor("#53b7bb"));
//        if(matcher.find()) {
//            span.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    if(on_click!=null) on_click.run();
//                }
//            }, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            span.setSpan(color_theme,
//                    matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            if(replace!=null) span.replace(matcher.start(), matcher.end(), replace);
//        }
//    }

    private void makeClickableTagsSpannable(Context context, SpannableStringBuilder span, String word, int textAppearance, final Runnable on_click){
        //ForegroundColorSpan color_theme = new ForegroundColorSpan(color);
        TextAppearanceSpan appearanceSpan = new TextAppearanceSpan(context, textAppearance);
        int start = span.length();
        span.append(word);
        int end = span.length();
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if(on_click!=null) on_click.run();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                //ds.setColor(ds.linkColor);
                //ds.setUnderlineText(false);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(appearanceSpan,
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

}
