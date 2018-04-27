package com.raed.swe311project.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.raed.swe311project.R;

/**
 * Created by Raed on 21/03/2018.
 */

public class RatingView extends View {


    private Path mStarPath;
    private Paint mStarPaint;

    private boolean mHalfStarEnabled = false;

    private int mActiveColor;
    private int mInactiveColor;

    private int mNumOfStars;
    private float mRating;

    private float mMarginBetweenStars;

    private Paint mClearPaint;

    private boolean mChangeable;

    private OnRatingChangedListener mOnRatingChangedListener;

    interface OnRatingChangedListener{
        void onRatingChanged(float newRating);
    }

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mStarPath = new Path();

        mStarPaint = new Paint();
        mStarPaint.setAntiAlias(true);
        mStarPaint.setDither(true);
        mStarPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mClearPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RatingView,
                0, 0);
        try {
            mHalfStarEnabled = typedArray.getBoolean(R.styleable.RatingView_halfStar, false);
            mChangeable = typedArray.getBoolean(R.styleable.RatingView_changeable,true);
            int defaultColor;
            try {
                defaultColor = context.getResources().getColor(R.color.colorAccent);
            }catch (Exception ignore){
                defaultColor = 0xff212121;
            }
            mActiveColor = typedArray.getColor(R.styleable.RatingView_activeColor, defaultColor);
            mInactiveColor = typedArray.getColor(R.styleable.RatingView_inactiveColor, (0x00ffffff & defaultColor) | 0x77000000 );
            setNumOfStars(typedArray.getInteger(R.styleable.RatingView_numOfStars, 5));
            setRating(typedArray.getFloat(R.styleable.RatingView_rating, 2.5f));
        } finally {
            typedArray.recycle();
        }

        mMarginBetweenStars = context.getResources().getDimension(R.dimen.default_star_margin);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        updateStarPath();

        /*
        draw in the center
        test by adding seekbar to cotroll dime
        default color = accent color
        let the user the stroke border
         */
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float starDime = calcStarDime();

        int widthWithoutPadding = getWidth() - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = getHeight() - getPaddingTop() - getPaddingBottom();

        canvas.translate( //translate to draw in the center
                getPaddingLeft() + (widthWithoutPadding - (starDime * mNumOfStars + mMarginBetweenStars * (mNumOfStars - 1) ) ) / 2,
                getPaddingTop() + (heightWithoutPadding - starDime) / 2
        );

        canvas.translate(mMarginBetweenStars/2, 0);//translate to draw the next star
        for (int i = 0; i < mNumOfStars ; i++){
            if (i < (int) mRating){ // totally selected star

                mStarPaint.setColor(mActiveColor);
                canvas.drawPath(mStarPath, mStarPaint);

            }else if (i == (int) mRating){ //partially selected star

                float floatRating = mRating - (int) mRating;

                canvas.save();
                RectF rect = new RectF(0, 0, starDime * floatRating, starDime);
                canvas.clipRect(rect);
                mStarPaint.setColor(mActiveColor);
                canvas.drawPath(mStarPath, mStarPaint);
                canvas.restore();

                canvas.save();
                rect.set(new RectF(starDime * floatRating, 0, starDime, starDime));
                canvas.clipRect(rect);
                mStarPaint.setColor(mInactiveColor);
                canvas.drawPath(mStarPath, mStarPaint);
                canvas.restore();

            }else { // not selected star
                mStarPaint.setColor(mInactiveColor);
                canvas.drawPath(mStarPath, mStarPaint);
            }
            canvas.translate(starDime + mMarginBetweenStars, 0);//translate to draw the next star
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        float defStarDime = getResources() .getDimensionPixelOffset(R.dimen.default_star_dimen);
        int desiredWidth = (int) (defStarDime * mNumOfStars + (mNumOfStars - 1) * mMarginBetweenStars) + getPaddingLeft() + getPaddingRight();
        int desiredHeight = (int) defStarDime + getPaddingTop() + getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mChangeable)
            return false;
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                float starDime = calcStarDime();
                float rating;
                if (mHalfStarEnabled)
                    rating = (int)( event.getX() / ((mMarginBetweenStars + starDime)/2) ) / 2f;
                else
                    rating = (int)( event.getX() / ((mMarginBetweenStars + starDime)/2) ) / 2;
                setRating(Math.max(Math.min(mNumOfStars, rating), 0));

                if (event.getActionMasked() == MotionEvent.ACTION_UP)
                    if (mOnRatingChangedListener != null)
                        mOnRatingChangedListener.onRatingChanged(mRating);

                break;
        }
        return true;

        //todo firebase to capture bugs
    }

    public void setNumOfStars(int numOfStars) {
        if (numOfStars < 1)
            throw new IllegalArgumentException("Number of stars should be grater than 1");
        mNumOfStars = numOfStars;
        updateStarPath();
    }

    public void setActiveColor(int activeColor) {
        mActiveColor = activeColor;
    }

    public void setInactiveColor(int inactiveColor) {
        mInactiveColor = inactiveColor;
    }

    public void setMarginBetweenStars(float marginInPixels){
        mMarginBetweenStars = marginInPixels;
    }

    public boolean isChangeable() {
        return mChangeable;
    }

    public boolean isHalfStarEnabled() {
        return mHalfStarEnabled;
    }

    public float getRating() {
        return mRating;
    }

    public float getMarginBetweenStars() {
        return mMarginBetweenStars;
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public int getInactiveColor() {
        return mInactiveColor;
    }

    public int getNumOfStars() {
        return mNumOfStars;
    }


    /**
     * Passing true enable the user to fill a half star.
     */
    public void setHalfStarEnabled(boolean halfStarEnabled) {
        mHalfStarEnabled = halfStarEnabled;
    }

    public void setRating(float rating){
        if (rating > mNumOfStars)
            throw new IllegalArgumentException("Rating " + rating + " is grater than the number of stars " + mNumOfStars);
        if (rating < 0)
            throw new IllegalArgumentException("Negative rating is not allowed");
        mRating = rating;
        invalidate();
    }

    public void setOnRatingChangedListener(OnRatingChangedListener onRatingChangedListener) {
        mOnRatingChangedListener = onRatingChangedListener;
    }

    private void updateStarPath(){

        float starDimen = calcStarDime();

        mStarPath.reset();

        mStarPath.moveTo(0, 0.387f * starDimen);
        mStarPath.lineTo(0.360f * starDimen, 0.357f * starDimen);
        mStarPath.lineTo(0.500f * starDimen, 0.025f * starDimen);
        mStarPath.lineTo(0.640f * starDimen, 0.356f * starDimen);
        mStarPath.lineTo(starDimen, 0.387f * starDimen);
        mStarPath.lineTo(0.727f * starDimen, 0.623f * starDimen);
        mStarPath.lineTo(0.809f * starDimen, 0.975f * starDimen);
        mStarPath.lineTo(0.5f * starDimen, 0.788f * starDimen);
        mStarPath.lineTo(0.191f * starDimen, 0.975f * starDimen);
        mStarPath.lineTo(0.273f * starDimen, 0.623f * starDimen);
        mStarPath.close();
    }

    private float calcStarDime(){
        float starDimen = (getWidth() - (mNumOfStars + 1) * mMarginBetweenStars - getPaddingLeft() - getPaddingRight()) / mNumOfStars;
        if (starDimen > getHeight() - getPaddingTop() - getPaddingBottom())
            starDimen = (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.9f;
        return starDimen;
    }

}
