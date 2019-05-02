package com.mlrecommendation.gopi.gopimlrecommendation.utils;

import android.util.Log;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;


public class RxUtils {

    private static final String TAG = RxUtils.class.getSimpleName();

    public static CompletableObserver getCompletableObserver() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError : " + e.toString());
            }
        };
    }
}
