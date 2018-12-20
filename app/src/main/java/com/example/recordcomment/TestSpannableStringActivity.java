package com.example.recordcomment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordcomment.widget.MovementMethod;
import com.example.recordcomment.widget.SpannableClickable;


/**
 *  测试 SpannableStringBuilder
 */
public class TestSpannableStringActivity extends AppCompatActivity {

    private TextView mContentTv = null;
    private TextView mContent1Tv = null;
    SpannableStringBuilder mSpannableStringBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spannable_string);
        mContentTv = findViewById(R.id.tv_content);
        mContent1Tv = findViewById(R.id.tv_content1);

        mContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("debug", "content onClick");
                Toast.makeText(getApplicationContext(), mContentTv.getText(),Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * 不考虑背景色，可以直接重写mContentTv的onTouch方法
         */
//        mContentTv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//
//                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
//                    int x = (int) event.getX();
//                    int y = (int) event.getY();
//
//                    TextView widget = (TextView) v;
//                    x -= widget.getTotalPaddingLeft();
//                    y -= widget.getTotalPaddingTop();
//
//                    x += widget.getScrollX();
//                    y += widget.getScrollY();
//
//                    Layout layout = widget.getLayout();
//                    int line = layout.getLineForVertical(y);
//                    int off = layout.getOffsetForHorizontal(line, x);
//
//                    ClickableSpan[] links = mSpannableStringBuilder.getSpans(off, off, ClickableSpan.class);
//
//                    if (links.length != 0) {
//                        if (action == MotionEvent.ACTION_UP) {
//                            links[0].onClick(widget);
//                        } else if (action == MotionEvent.ACTION_DOWN) {
//                            Selection.setSelection(mSpannableStringBuilder,
//                                    mSpannableStringBuilder.getSpanStart(links[0]),
//                                    mSpannableStringBuilder.getSpanEnd(links[0]));
//
//                            BackgroundColorSpan bgSpan = new BackgroundColorSpan(getResources().getColor(R.color.gray));
//                            mSpannableStringBuilder.setSpan(bgSpan, mSpannableStringBuilder.getSpanStart(links[0]), mSpannableStringBuilder.getSpanEnd(links[0]), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//
//                        }
//                        return true;
//                    } else {
//                        Selection.removeSelection(mSpannableStringBuilder);
//                    }
//                }
//
//                return false;
//            }
//        });

        test();
//        test1();
//        addClicl();
    }

    /**
     * 这是方式2：在动态的字符串中，设置setPan()
     * 后面会应用于评论
     */
    private void test(){
        SpannableString spannableString;
        mSpannableStringBuilder = new SpannableStringBuilder();

        spannableString = new SpannableString("小哥哥");
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSpannableStringBuilder.append(spannableString);

        mSpannableStringBuilder.append("回复");

        spannableString = new SpannableString("小姐姐 : ");
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSpannableStringBuilder.append(spannableString);

        mSpannableStringBuilder.append("在我心中，你最美~");
        mContentTv.setText(mSpannableStringBuilder);
    }

    /**
     * 这是方式2：在指定的字符串中，设置setPan()
     *
     * ！！！ 用一个CharacterStyle对象，不能直接被复用
     */
    private void test1(){
        String content = "小姐姐回复小哥哥：在我心中，你最帅~";
        mSpannableStringBuilder = new SpannableStringBuilder(content);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        mSpannableStringBuilder.setSpan(foregroundColorSpan,0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSpannableStringBuilder.setSpan(foregroundColorSpan,5, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        mSpannableStringBuilder.setSpan(CharacterStyle.wrap(foregroundColorSpan),0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        mSpannableStringBuilder.setSpan(CharacterStyle.wrap(foregroundColorSpan),5, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mContent1Tv.setText(mSpannableStringBuilder);
    }

    /**
     * 再回复的评论，添加点击事件
     */
    private void addClicl(){
//        //添加字体颜色
//        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        final SpannableString spannableString;
        mSpannableStringBuilder = new SpannableStringBuilder();

        spannableString = new SpannableString("小哥哥");
        /**
         * 添加监听事件
         */
//        spannableString.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                Toast.makeText(getApplicationContext(), spannableString.toString(),Toast.LENGTH_SHORT).show();
//            }
//        },0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

        /**
         * 添加自定义的监听事件
         */
        spannableString.setSpan(new SpannableClickable(getApplicationContext()) {
            @Override
            public void onClick(View widget) {
                Log.e("debug", "span onClick");
                Toast.makeText(getApplicationContext(), spannableString.toString(),Toast.LENGTH_SHORT).show();
            }
        },0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        mSpannableStringBuilder.append(spannableString);
        mSpannableStringBuilder.append("回复");


        final SpannableString spannableString1;
        spannableString1 = new SpannableString("小姐姐: ");
//        /**
//         *  添加监听事件
//         */
//        spannableString1.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                Toast.makeText(getApplicationContext(), spannableString1.toString(),Toast.LENGTH_SHORT).show();
//            }
//        },0, spannableString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

        /**
         * 添加自定义的监听事件
         */
        spannableString1.setSpan(new SpannableClickable(getApplicationContext()) {
            @Override
            public void onClick(View widget) {
                Log.e("debug", "span onClick");
                Toast.makeText(getApplicationContext(), spannableString1.toString(),Toast.LENGTH_SHORT).show();
            }
        },0, spannableString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mSpannableStringBuilder.append(spannableString1);

        mSpannableStringBuilder.append("在我心中，你最美~");
        mContentTv.setText(mSpannableStringBuilder);
//        mContentTv.setMovementMethod(LinkMovementMethod.getInstance());
        mContentTv.setMovementMethod(new MovementMethod(getResources().getColor(R.color.gray), getResources().getColor(R.color.gray)));


        ClickableSpan[] spans = mSpannableStringBuilder.getSpans(0, 3, ClickableSpan.class);
        ClickableSpan span = spans[0];

    }

}
