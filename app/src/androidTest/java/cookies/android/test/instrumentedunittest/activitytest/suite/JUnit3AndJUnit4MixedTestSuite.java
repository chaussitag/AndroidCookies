package cookies.android.test.instrumentedunittest.activitytest.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cookies.android.test.instrumentedunittest.activitytest.SimpleCalculatorActivityJUnit3StyleInstrumentedTest;
import cookies.android.test.instrumentedunittest.activitytest.SimpleCalculatorActivityJUnit4StyleInstrumentedTest;

/**
 * Created by daiguozhou on 2015/12/4.
 * A test suite to run a junit3 testcase and a junit4 testcase.
 * With the new AndroidJUnit runner you can run both JUnit3 and JUnit4 tests in a single test.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({SimpleCalculatorActivityJUnit3StyleInstrumentedTest.class,
        SimpleCalculatorActivityJUnit4StyleInstrumentedTest.class})
public class JUnit3AndJUnit4MixedTestSuite {
}
