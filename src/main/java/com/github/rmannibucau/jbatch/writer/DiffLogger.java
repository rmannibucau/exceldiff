package com.github.rmannibucau.jbatch.writer;

import com.github.rmannibucau.jbatch.diff.Line;
import com.github.rmannibucau.jbatch.processor.ICRHProcessor;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class DiffLogger extends AbstractItemWriter {
    private static final String SEP = ";";

    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @Inject
    private JobContext ctx;

    @Inject
    @BatchProperty
    private String file;

    @Override
    public void writeItems(final List<Object> objects) throws Exception {
        final PrintStream ps;
        if (file == null) {
            ps = new PrintStream(System.out) {
                @Override
                public void close() {
                    // no-op
                }
            };
        } else {
            ps = new PrintStream(new FileOutputStream(file));
        }

        try {
            ps.println("Intersection size: " + objects.size());
            ps.println();
            ps.println("nni" + SEP + "resource" + SEP + "name(forquartz)" + SEP + "start(forquartz)" + SEP + "end(forquartz)"
                    + SEP + "name(ichr)" + SEP + "start(ichr)" + SEP + "end(forquartz)");
            for (final Object o : objects) {
                final ICRHProcessor.Diff diff = ICRHProcessor.Diff.class.cast(o);
                final Line forQuartz = diff.getForQuartz();
                final Line ichr = diff.getIchr();
                ps.println(forQuartz.getNni() + SEP + forQuartz.getResource()
                        + SEP + forQuartz.getName() + SEP + format.format(forQuartz.getBeginning()) + SEP + format.format(forQuartz.getEnd())
                        + SEP + ichr.getName() + SEP + format.format(ichr.getBeginning()) + SEP + format.format(ichr.getEnd()));
            }
        } finally {
            ps.close();
        }

        ctx.setExitStatus(objects.size() + " differences");
    }
}
