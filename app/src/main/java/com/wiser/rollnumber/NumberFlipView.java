package com.wiser.rollnumber;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NumberFlipView extends View {

   private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
   private int mFlipNumber = 1990;
   private int mOutterFlipNumber = mFlipNumber;
   private Rect textRect = new Rect();
   private final float mMaxMoveHeight;
   private float mCurrentMoveHeight;
   private float mOutterMoveHeight;
   private float mCurrentAlphaValue;

   private List<String> flipNumbers = new ArrayList<>();
   private List<String> flipOutterNumbers = new ArrayList<>();

   public NumberFlipView(Context context, @Nullable AttributeSet attrs) {
       super(context, attrs);

       paint.setColor(Color.BLACK);

       int fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 54, context.getResources().getDisplayMetrics());
       paint.setTextSize(fontSize);

       paint.setStyle(Paint.Style.STROKE);

       mMaxMoveHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());
   }

   @Override
   protected void onDraw(Canvas canvas) {
       super.onDraw(canvas);

       flipNumbers.clear();
       flipOutterNumbers.clear();


       String flipNumberString = String.valueOf(mFlipNumber);
       for (int i = 0; i < flipNumberString.length(); i++) {
           flipNumbers.add(String.valueOf(flipNumberString.charAt(i)));
       }

       String flipOutterNumberString = String.valueOf(mOutterFlipNumber);
       for (int i = 0; i < flipNumberString.length(); i++) {
           flipOutterNumbers.add(String.valueOf(flipOutterNumberString.charAt(i)));
       }

       paint.getTextBounds(String.valueOf(mFlipNumber), 0, String.valueOf(mFlipNumber).length(), textRect);
       final int textWidth = textRect.width() + 80;

       float curTextWidth = 0;
       for (int i = 0; i < flipNumbers.size(); i++) {

           paint.getTextBounds(flipNumbers.get(i), 0, flipNumbers.get(i).length(), textRect);
           final int numWidth = textRect.width();

           if (flipNumbers.get(i).equals(flipOutterNumbers.get(i))) {

               paint.setAlpha(255);
               canvas.drawText(flipNumbers.get(i), getWidth() / 2 - textWidth / 2 + curTextWidth, getHeight() / 2 + textRect.height() / 2, paint);
           } else {

               paint.setAlpha((int) (255 * (1 - mCurrentAlphaValue)));
               canvas.drawText(flipOutterNumbers.get(i), getWidth() / 2 - textWidth / 2 + curTextWidth, mOutterMoveHeight + getHeight() / 2 + textRect.height() / 2, paint);
               paint.setAlpha((int) (255 * mCurrentAlphaValue));
               canvas.drawText(flipNumbers.get(i), getWidth() / 2 - textWidth / 2 + curTextWidth, mCurrentMoveHeight + getHeight() / 2 + textRect.height() / 2, paint);
           }

           curTextWidth += (numWidth + 20);
       }
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
       if (event.getAction() == MotionEvent.ACTION_DOWN) {
           jumpNumber();
       }
       return super.onTouchEvent(event);
   }

   public void jumpNumber() {
       mOutterFlipNumber = mFlipNumber;
       mFlipNumber++;

       ValueAnimator animator = ValueAnimator.ofFloat(mMaxMoveHeight, 0);
       animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
           public void onAnimationUpdate(ValueAnimator animation) {
               mCurrentMoveHeight = (float) animation.getAnimatedValue();
               invalidate();
           }
       });
       animator.setDuration(1000);
       animator.start();

       ValueAnimator animator1 = ValueAnimator.ofFloat(0, 1);
       animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
           public void onAnimationUpdate(ValueAnimator animation) {
               mCurrentAlphaValue = (float) animation.getAnimatedValue();
               invalidate();
           }
       });
       animator1.setDuration(1000);
       animator1.start();

       ValueAnimator animator2 = ValueAnimator.ofFloat(0, -mMaxMoveHeight);
       animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
           public void onAnimationUpdate(ValueAnimator animation) {
               mOutterMoveHeight = (float) animation.getAnimatedValue();
               invalidate();
           }
       });
       animator2.setDuration(1000);
       animator2.start();
   }
}

