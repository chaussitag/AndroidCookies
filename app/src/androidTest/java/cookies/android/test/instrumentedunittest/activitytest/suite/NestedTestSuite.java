package cookies.android.test.instrumentedunittest.activitytest.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cookies.android.test.instrumentedunittest.activitytest.SimpleCalculatorTest;

/**
 * Created by daiguozhou on 2015/12/4.
 * A test suite group another test suite and a test class.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({JUnit3AndJUnit4MixedTestSuite.class, SimpleCalculatorTest.class})
public class NestedTestSuite {
}
