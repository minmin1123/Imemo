package com.minmin.imemo.view.tickview;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2018/01/22
 * desc:打钩动画执行速率枚举配置
 * version:1.0
 */

enum TickRateEnum {

    SLOW(800, 480, 720),
    NORMAL(500, 300, 450),
    FAST(300, 180, 270);

    public static final int RATE_MODE_SLOW = 0;
    public static final int RATE_MODE_NORMAL = 1;
    public static final int RATE_MODE_FAST = 2;

    private int mRingAnimatorDuration;
    private int mCircleAnimatorDuration;
    private int mScaleAnimatorDuration;

    TickRateEnum(int mRingAnimatorDuration, int mCircleAnimatorDuration, int mScaleAnimatorDuration) {
        this.mRingAnimatorDuration = mRingAnimatorDuration;
        this.mCircleAnimatorDuration = mCircleAnimatorDuration;
        this.mScaleAnimatorDuration = mScaleAnimatorDuration;
    }

    public int getmRingAnimatorDuration() {
        return mRingAnimatorDuration;
    }

    public TickRateEnum setmRingAnimatorDuration(int mRingAnimatorDuration) {
        this.mRingAnimatorDuration = mRingAnimatorDuration;
        return this;
    }

    public int getmCircleAnimatorDuration() {
        return mCircleAnimatorDuration;
    }

    public TickRateEnum setmCircleAnimatorDuration(int mCircleAnimatorDuration) {
        this.mCircleAnimatorDuration = mCircleAnimatorDuration;
        return this;
    }

    public int getmScaleAnimatorDuration() {
        return mScaleAnimatorDuration;
    }

    public TickRateEnum setmScaleAnimatorDuration(int mScaleAnimatorDuration) {
        this.mScaleAnimatorDuration = mScaleAnimatorDuration;
        return this;
    }

    public static TickRateEnum getRateEnum(int rateMode) {
        TickRateEnum tickRateEnum;
        switch (rateMode) {
            case RATE_MODE_SLOW:
                tickRateEnum = TickRateEnum.SLOW;
                break;
            case RATE_MODE_NORMAL:
                tickRateEnum = TickRateEnum.NORMAL;
                break;
            case RATE_MODE_FAST:
                tickRateEnum = TickRateEnum.FAST;
                break;
            default:
                tickRateEnum = TickRateEnum.NORMAL;
                break;
        }
        return tickRateEnum;
    }
}
