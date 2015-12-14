package cookies.android.test.instrumentedunittest.activitytest;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by daiguozhou on 2015/12/4.
 * JUnit4 unit tests for the calculator logic.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SimpleCalculatorTest {
    private SimpleCalculator mSimpleCalculator;
    
    @Before
    public void setUp() {
        mSimpleCalculator = new SimpleCalculator();
    }
    
    @Test
    public void addTwoNumbers() {
        double result = mSimpleCalculator.add(1d, 1d);
        assertThat(result, is(equalTo(2d)));
    }
    
    @Test
    public void subTwoNumbers() {
        double result = mSimpleCalculator.sub(1d, 1d);
        assertThat(result, is(equalTo(0d)));
    }
    
    @Test
    public void subWorksWithNegativeResult() {
        double result = mSimpleCalculator.sub(1d, 17d);
        assertThat(result, is(equalTo(-16d)));
    }
    
    @Test
    public void divTwoNumbers() {
        double result = mSimpleCalculator.div(32d, 2d);
        assertThat(result, is(equalTo(16d)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void divideByZeroThrows() {
        mSimpleCalculator.div(32d, 0d);
    }
    
    @Test
    public void mulTwoNumbers() {
        double result = mSimpleCalculator.mul(32d, 2d);
        assertThat(result, is(equalTo(64d)));
    }
}