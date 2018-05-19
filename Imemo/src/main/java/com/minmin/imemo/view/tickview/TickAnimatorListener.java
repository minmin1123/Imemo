package com.minmin.imemo.view.tickview;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2018/01/22
 * desc:打钩动画监听器
 * version:1.0
 */

public interface TickAnimatorListener {

    void onAnimationStart(TickView tickView);

    void onAnimationEnd(TickView tickView);

    abstract class TickAnimatorListenerAdapter implements TickAnimatorListener {
        @Override
        public void onAnimationStart(TickView tickView) {

        }

        @Override
        public void onAnimationEnd(TickView tickView) {

        }
    }
}
