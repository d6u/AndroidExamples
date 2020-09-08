package com.daiwei.exampleandroid;

import com.daiwei.exampleandroid.reactiveprogramming.ReactiveProgrammingExampleOne;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ReactiveProgrammingExampleOneUnitTest {

  private TestReactiveProgrammingExampleOne mReactiveProgrammingExampleOne;
  private TestScheduler mTestScheduler = new TestScheduler();

  @Before
  public void before() {
    RxJavaPlugins.setComputationSchedulerHandler(ignored -> mTestScheduler);
    mReactiveProgrammingExampleOne = Mockito.spy(new TestReactiveProgrammingExampleOne());
  }

  @After
  public void after() {
    mReactiveProgrammingExampleOne.destroy();
    RxJavaPlugins.reset();
  }

  @Test
  public void testDoSomethingOnceSucceedWithValueHello() {
    TestObserver<String> observer =
        mReactiveProgrammingExampleOne.doSomethingOnce().test();
    mTestScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    observer.assertValue("Hello").dispose();
  }

  @Test
  public void testDoSomethingOnceExecuteWithoutSubscriber() {
    mReactiveProgrammingExampleOne.doSomethingOnce();
    Mockito.verify(mReactiveProgrammingExampleOne, Mockito.times(1)).createInnerSingle();
  }

  @Test
  public void testDoSomethingOnceMultipleSubscribersDoesNotExecuteMoreThanOnce() {
    Single<String> single = mReactiveProgrammingExampleOne.doSomethingOnce();
    TestObserver<String> observer1 = single.test();
    TestObserver<String> observer2 = single.test();
    Mockito.verify(mReactiveProgrammingExampleOne, Mockito.times(1)).createInnerSingle();
    observer1.dispose();
    observer2.dispose();
  }

  @Test
  public void testDoSomethingOnceSubscribersAfterSuccessDoesNotExecuteAgain() {
    Single<String> single = mReactiveProgrammingExampleOne.doSomethingOnce();
    TestObserver<String> observer1 = single.test();
    mTestScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    observer1.assertValue("Hello").dispose();
    Mockito.verify(mReactiveProgrammingExampleOne, Mockito.times(1)).createInnerSingle();

    single.test().assertValue("Hello").dispose();
    Mockito.verify(mReactiveProgrammingExampleOne, Mockito.times(1)).createInnerSingle();
  }

  @Test
  public void testDoSomethingOnceErrorWhenDestroyingInstance() {
    TestObserver<String> observer = mReactiveProgrammingExampleOne.doSomethingOnce().test();
    mTestScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    mReactiveProgrammingExampleOne.destroy();
    observer.assertError(NoSuchElementException.class).dispose();
  }

  private static class TestReactiveProgrammingExampleOne extends ReactiveProgrammingExampleOne {

    @Override
    protected SingleSource<String> createInnerSingle() {
      return super.createInnerSingle();
    }
  }
}
