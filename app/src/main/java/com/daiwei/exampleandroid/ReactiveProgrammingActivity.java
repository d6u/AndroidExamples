package com.daiwei.exampleandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.daiwei.exampleandroid.reactiveprogramming.ReactiveProgrammingExampleOne;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ReactiveProgrammingActivity extends AppCompatActivity {

  public static final String LOG_TAG = ReactiveProgrammingActivity.class.getSimpleName();

  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private @Nullable ReactiveProgrammingExampleOne mTestClass;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reactive_programming);

    mTestClass = new ReactiveProgrammingExampleOne();
  }

  @Override
  protected void onStart() {
    super.onStart();

    assert mTestClass != null;
    Single<String> single = mTestClass.doSomethingOnce();

    mHandler.postDelayed(() -> {
      Disposable disposable = single.subscribe(
          v -> Log.d(LOG_TAG, "subscriber 1 got result: " + v),
          t -> Log.e(LOG_TAG, "subscriber 1 got error", t));
      mCompositeDisposable.add(disposable);
    }, 500);

    mHandler.postDelayed(() -> {
      Disposable disposable = single.subscribe(
          v -> Log.d(LOG_TAG, "subscriber 2 got result: " + v),
          t -> Log.e(LOG_TAG, "subscriber 2 got error", t));
      mCompositeDisposable.add(disposable);
    }, 500);

    mHandler.postDelayed(() -> {
      Disposable disposable = single.subscribe(
          v -> Log.d(LOG_TAG, "subscriber 3 got result: " + v),
          t -> Log.e(LOG_TAG, "subscriber 3 got error", t));
      mCompositeDisposable.add(disposable);
    }, 3000);
  }

  @Override
  protected void onDestroy() {
    mCompositeDisposable.clear();

    mHandler.removeCallbacksAndMessages(null);

    assert mTestClass != null;
    mTestClass.destroy();

    super.onDestroy();
  }

}
