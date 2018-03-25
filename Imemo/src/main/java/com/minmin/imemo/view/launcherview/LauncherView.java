package com.minmin.imemo.view.launcherview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.minmin.imemo.R;
import com.minmin.imemo.activity.MainActivity;
import com.minmin.imemo.util.Utils;


/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2018/01/18
 *   desc:开场动画view
 *   version:1.0
 */

public class LauncherView extends RelativeLayout {
    private int mHeight;
    private int mWidth;
    private int dp80 = Utils.dp2px(getContext(), 80);
    private Activity context;

    public LauncherView(Context context) {
        super(context);
        this.context = (Activity) context;
    }

    public LauncherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
    }

    public LauncherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Activity) context;
    }

    ImageView yellow, orange, red, brown;

    //添加四个颜色的圆
    private void init() {

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(CENTER_HORIZONTAL, TRUE);//这里的TRUE 要注意 不是true
        lp.addRule(CENTER_VERTICAL, TRUE);
        lp.setMargins(0, 0, 0, dp80);

        orange = new ImageView(getContext());
        orange.setLayoutParams(lp);
        orange.setImageResource(R.drawable.shape_circle_orange);
        addView(orange);

        red = new ImageView(getContext());
        red.setLayoutParams(lp);
        red.setImageResource(R.drawable.shape_circle_red);
        addView(red);

        brown = new ImageView(getContext());
        brown.setLayoutParams(lp);
        brown.setImageResource(R.drawable.shape_circle_brown);
        addView(brown);

        yellow = new ImageView(getContext());
        yellow.setLayoutParams(lp);
        yellow.setImageResource(R.drawable.shape_circle_yellow);
        addView(yellow);

        setAnimation(yellow, yellowPath1);
        setAnimation(orange, orangePath1);
        setAnimation(red, redPath1);
        setAnimation(brown, brownPath1);

    }

    ViewPath yellowPath1, orangePath1, redPath1, brownPath1;

    //绘制四个圆圈的 Path
    private void initPath() {
        yellowPath1 = new ViewPath(); //偏移坐标
        yellowPath1.moveTo(0, 0);
        yellowPath1.lineTo(mWidth / 5 - mWidth / 2, 0);
        yellowPath1.curveTo(-700, -mHeight / 2, mWidth / 3 * 2, -mHeight / 3 * 2, 0, -dp80);

        orangePath1 = new ViewPath(); //偏移坐标
        orangePath1.moveTo(0, 0);
        orangePath1.lineTo(mWidth / 5 * 2 - mWidth / 2, 0);
        orangePath1.curveTo(-300, -mHeight / 2, mWidth, -mHeight / 9 * 5, 0, -dp80);

        redPath1 = new ViewPath(); //偏移坐标
        redPath1.moveTo(0, 0);
        redPath1.lineTo(mWidth / 5 * 3 - mWidth / 2, 0);
        redPath1.curveTo(300, mHeight, -mWidth, -mHeight / 9 * 5, 0, -dp80);

        brownPath1 = new ViewPath(); //偏移坐标
        brownPath1.moveTo(0, 0);
        brownPath1.lineTo(mWidth / 5 * 4 - mWidth / 2, 0);
        brownPath1.curveTo(700, mHeight / 3 * 2, -mWidth / 2, mHeight / 2, 0, -dp80);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        initPath();
    }


    public void start() {

        removeAllViews();
        init();
        yellowAll.start();
        redAll.start();
        orangeAll.start();
        brownAll.start();
        new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            showLogo();
        }
    }, 2400);
}

    //将 Path 转换成 ObjectAnimation
    private void setAnimation(final ImageView target, ViewPath path1) {
        //路径
        ObjectAnimator anim1 = ObjectAnimator.ofObject(new ViewObj(target), "fabLoc", new ViewPathEvaluator(), path1.getPoints().toArray());
        anim1.setInterpolator(new AccelerateDecelerateInterpolator());
        anim1.setDuration(2700);
        //组合添加缩放透明效果
        addAnimation(anim1, target);
    }


    AnimatorSet yellowAll, orangeAll, redAll, brownAll;

    //组合动画得到一个圆的完整运行轨迹
    private void addAnimation(ObjectAnimator animator1, final ImageView target) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 1000);
        valueAnimator.setDuration(1800);
        valueAnimator.setStartDelay(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float alpha = 1 - value / 2000;
                float scale = getScale(target) - 1;
                if (value <= 500) {
                    scale = 1 + (value / 500) * scale;
                } else {
                    scale = 1 + ((1000 - value) / 500) * scale;
                }
                target.setScaleX(scale);
                target.setScaleY(scale);
                target.setAlpha(alpha);
            }
        });
        valueAnimator.addListener(new AnimEndListener(target));
        if (target == yellow) {
            yellowAll = new AnimatorSet();
            yellowAll.playTogether(animator1, valueAnimator);
        }
        if (target == brown) {
            brownAll = new AnimatorSet();
            brownAll.playTogether(animator1, valueAnimator);
        }
        if (target == orange) {
            orangeAll = new AnimatorSet();
            orangeAll.playTogether(animator1, valueAnimator);
        }
        if (target == red) {
            redAll = new AnimatorSet();
            redAll.playTogether(animator1, valueAnimator);
        }

    }


    private float getScale(ImageView target) {
        if (target == yellow){
            return 3.0f;
        }

        if (target == orange){
            return 2.0f;
        }

        if (target == red){
            return 4.5f;
        }

        if (target == brown){
            return 3.5f;
        }

        return 2f;
    }

    //显示 logo 动画
    private void showLogo() {
        View view = View.inflate(getContext(), R.layout.launcher_logo, this);
        View logo = view.findViewById(R.id.logoIv);
        final View slogo = view.findViewById(R.id.slogoTv);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f);
        alpha.setDuration(800);
        alpha.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator alpha = ObjectAnimator.ofFloat(slogo, View.ALPHA, 0f, 1f);
                alpha.setDuration(500);
                alpha.start();
            }
        }, 300);


    }

    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            removeView((target));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    context.finish();
                }
            }, 100);

        }
    }


    public class ViewObj {
        private final ImageView yellow;

        public ViewObj(ImageView yellow) {
            this.yellow = yellow;
        }

        public void setFabLoc(ViewPoint newLoc) {
            yellow.setTranslationX(newLoc.x);
            yellow.setTranslationY(newLoc.y);
        }
    }


}


