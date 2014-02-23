package com.github.rmannibucau.jbatch;

import org.apache.batchee.util.Batches;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import java.util.Properties;

public class Main {
    public static void main(final String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage:\n\njava -jar exceldiff.jar forquart.xls icrh.xlsx");
            return;
        }

        final JobOperator operator = BatchRuntime.getJobOperator();
        final long execution = operator.start("diff", new Properties() {{
            setProperty("forquart", args[0]);
            setProperty("icrh", args[1]);
            if (args.length > 2) setProperty("output", args[2]);
            if (args.length > 3) setProperty("filter", args[3]);
        }});

        Batches.waitForEnd(operator, execution);
    }

    private Main() {
        // no-op
    }
}
