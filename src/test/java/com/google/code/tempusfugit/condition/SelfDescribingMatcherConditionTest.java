package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.concurrency.Callable;
import com.google.code.tempusfugit.temporal.Condition;
import com.google.code.tempusfugit.temporal.ProbeFor;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.condition.SelfDescribingMatcherCondition.probe;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SelfDescribingMatcherConditionTest {

    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void exampleUsage() throws TimeoutException, InterruptedException {
        waitOrTimeout(probe(stateOfTheEconomy(), is("Out of recession!")), timeout(millis(250)));
    }

    @Test
    public void isSatisfied() {
        Condition condition = new SelfDescribingMatcherCondition<String>(lambda("the best"), is("the best"));
        assertThat(condition.isSatisfied(), is(true));
    }

    @Test
    public void isNotSatisfied() {
        Condition condition = new SelfDescribingMatcherCondition<String>(lambda("the worst"), is("the best"));
        assertThat(condition.isSatisfied(), is(false));
    }

    private Callable<String, RuntimeException> lambda(final String value) {
        return new Callable<String, RuntimeException>() {
            @Override
            public String call() throws RuntimeException {
                return value;
            }
        };
    }

    private static ProbeFor<String> stateOfTheEconomy() {
        return new ProbeFor<String>() {
            @Override
            public String call() throws RuntimeException {
                return "Out of recession!"; // eg, "probe" the chancellor of the exchequer for an updated report
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("the state of the economy");
            }
        };
    }

}
