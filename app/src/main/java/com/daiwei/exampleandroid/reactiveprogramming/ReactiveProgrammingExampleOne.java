package com.daiwei.exampleandroid.reactiveprogramming;

import android.util.Log;
import com.daiwei.exampleandroid.ReactiveProgrammingActivity;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.SingleSubject;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ReactiveProgrammingExampleOne {

  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private final Single<Integer> mSingle;

  public ReactiveProgrammingExampleOne() {
    mSingle = Single.just(1).cache();
  }

  public void destroy() {
    mCompositeDisposable.clear();
  }

  public Single<String> doSomethingOnce() {
    SingleSubject<String> subject = SingleSubject.create();

    AtomicReference<Disposable> reference = new AtomicReference<>(null);

    mSingle.flatMap(ignored -> {
      Log.d(ReactiveProgrammingActivity.LOG_TAG, "Run hello logic");
      return createInnerSingle();
    })
        .doOnEvent((String v, Throwable t) -> {
          Log.d(ReactiveProgrammingActivity.LOG_TAG, "Hello single event");
          mCompositeDisposable.delete(reference.get());
        })
        .doOnDispose(() -> {
          Log.d(ReactiveProgrammingActivity.LOG_TAG, "Hello single disposing");
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

  protected SingleSource<String> createInnerSingle() {
    return Single.timer(2, TimeUnit.SECONDS).map(ignored2 -> "Hello");
  }
}
