package com.daiwei.exampleandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.SingleSubject;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ReactiveProgrammingActivity extends AppCompatActivity {

  private static final String TAG = ReactiveProgrammingActivity.class.getSimpleName();

  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private @Nullable TestClass mTestClass;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reactive_programming);

    mTestClass = new TestClass();
  }

  @Override
  protected void onStart() {
    super.onStart();

    assert mTestClass != null;
    Single<String> single = mTestClass.doSomethingOnce();

    mHandler.postDelayed(() -> {
      Disposable disposable = single.subscribe(
          v -> Log.d(TAG, "subscriber 1 got result: " + v),
          t -> Log.e(TAG, "subscriber 1 got error", t));
      mCompositeDisposable.add(disposable);
    }, 500);

    mHandler.postDelayed(() -> {
      Disposable disposable = single.subscribe(
          v -> Log.d(TAG, "subscriber 2 got result: " + v),
          t -> Log.e(TAG, "subscriber 2 got error", t));
      mCompositeDisposable.add(disposable);
    }, 500);

    mHandler.postDelayed(() -> {
      Disposable disposable = single.subscribe(
          v -> Log.d(TAG, "subscriber 3 got result: " + v),
          t -> Log.e(TAG, "subscriber 3 got error", t));
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

  private static final class TestClass {

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final Single<Integer> mSingle;

    TestClass() {
      mSingle = Single.just(1).cache();
    }

    void destroy() {
      mCompositeDisposable.clear();
    }

    Single<String> doSomethingOnce() {
      SingleSubject<String> subject = SingleSubject.create();

      AtomicReference<Disposable> reference = new AtomicReference<>(null);

      mSingle.flatMap(ignored -> {
        Log.d(TAG, "Run hello logic");
        return Single.timer(2, TimeUnit.SECONDS).map(ignored2 -> "Hello");
      })
          .doOnEvent((String v, Throwable t) -> {
            Log.d(TAG, "Hello single event");
            mCompositeDisposable.delete(reference.get());
          })
          .doOnDispose(() -> {
            Log.d(TAG, "Hello single disposing");
            subject.onError(new NoSuchElementException());
          })
          .subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
              reference.set(d);
              mCompositeDisposable.add(d);
            }

            @Override
            public void onSuccess(String s) {
              subject.onSuccess(s);
            }

            @Override
            public void onError(Throwable e) {
              subject.onError(e);
            }
          });

      return subject;
    }
  }
}
