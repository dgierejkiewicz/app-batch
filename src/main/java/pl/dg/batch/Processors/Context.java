package pl.dg.batch.Processors;

import pl.dg.batch.POJO.User;

import java.util.Arrays;
import java.util.List;

/**
 * Simple strategy context
 */
public class Context {

    private Processor strategy;

    public void set(Processor strategy) {
        this.strategy = strategy;
    }

    public void handle() {
        List<User> statements = strategy.process();
        if (null != statements) {
            System.out.println(Arrays.toString(statements.toArray()));
        }
    }
}
