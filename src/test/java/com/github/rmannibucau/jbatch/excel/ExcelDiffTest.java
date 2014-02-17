package com.github.rmannibucau.jbatch.excel;

import org.apache.batchee.test.JobLauncher;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ExcelDiffTest {
    @Test
    public void run() {
        final JobExecution execution = JobLauncher.start("diff", new Properties() {{
            setProperty("forquart", "src/test/data/FORQUART.xls");
            setProperty("icrh", "src/test/data/tr14_ICRH.xlsx");
        }});
        assertEquals(BatchStatus.COMPLETED, execution.getBatchStatus());
        assertEquals("143 differences", execution.getExitStatus());
    }
}
