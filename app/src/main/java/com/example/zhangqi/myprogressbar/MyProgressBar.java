package com.example.zhangqi.myprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class MyProgressBar extends ProgressBar {


    //默认值
    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0xFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp


    // 实际值
    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mUnReachColor = DEFAULT_COLOR_UNREACH;
    private int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    private int mReachColor = DEFAULT_COLOR_REACH;
    private int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    private Paint mPaint = new Paint();

    private int mRealWidth;

    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainStyledAttrs(attrs);
    }


    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        //设置当前组件的大小
        setMeasuredDimension(width, height);
        //getMeasuredWidth 获取到View的原始大小。
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 画
     * @param canvas
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //保存当前矩阵，并且剪辑到私有的堆栈中。
        canvas.save();
        // 移动  初始点 默认是 （0，0），现在移动到 （getPaddingLeft()，getHeight() / 2）
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean noNeedUnReach = false;

        //获取到当前ProgressBar进度。
        String text = getProgress() + "%";

        //返回文本的宽度
        int textWidth = (int) mPaint.measureText(text);

        //getMax 返回的是当前ProgressBar进度条的最大范围 ，radio是 进度百分比
        float radio = getProgress() * 1.0f / getMax();

        //实际进度
        float progressX = radio * mRealWidth;

        if (progressX+textWidth>mRealWidth){
            progressX = mRealWidth-textWidth;
            noNeedUnReach = true;
        }


        float endX = progressX-mTextOffset/2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            //为画笔设置宽度  stroke(划)
            mPaint.setStrokeWidth(mReachHeight);
            //划线
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        //draw text

        //设置画笔颜色
        mPaint.setColor(mTextColor);
        // descent(是基线之下到字符最低处的距离)  ascent（是基线之上到字符最高处的距离）。
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);

        canvas.drawText(text, progressX, y, mPaint);

        // draw unreach bar

        if (!noNeedUnReach) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }


        canvas.restore();
    }

    /**
     * 测量绘制的高度
     * Mode : UNSPECIFIED    EXACTLY（指定了确切的大小,match_parent或者具体的值）     AT_MOST（指定了一个最大尺寸wrap_content）
     *
     *
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {

        int result = 0;

        //模式
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        //高度的值  获取到控件的高度值
        int heightVal = MeasureSpec.getSize(heightMeasureSpec);


        if (mode == MeasureSpec.EXACTLY) {
            result = heightVal;
        } else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            Log.d("textHeight = ", String.valueOf(textHeight));
            // Math.abs()返回的是绝对值  这是在绘制的线与字体中找出最大值
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mReachHeight, mUnReachHeight), Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, heightVal);
            }
        }


        return result;
    }

    /**
     * 获取自定义属性
     *
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs) {

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MyProgressBar);

        mTextSize = (int) ta.getDimension(R.styleable.MyProgressBar_progress_text_size, mTextSize);
        mTextColor = ta.getColor(R.styleable.MyProgressBar_progress_text_color, mTextColor);

        mUnReachColor = ta.getColor(R.styleable.MyProgressBar_progress_unreach_color, mUnReachColor);
        mUnReachHeight = (int) ta.getDimension(R.styleable.MyProgressBar_progress_unreach_size, mUnReachHeight);

        mReachColor = ta.getColor(R.styleable.MyProgressBar_progress_reach_color, mReachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.MyProgressBar_progress_reach_size, mReachHeight);

        mTextOffset = (int) ta.getDimension(R.styleable.MyProgressBar_progress_text_offset, mTextOffset);

        //回收
        ta.recycle();

        mPaint.setTextSize(mTextSize);

    }


    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    private int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }


}
