package com.github.rmannibucau.jbatch.writer;

import com.github.rmannibucau.jbatch.diff.Line;
import com.github.rmannibucau.jbatch.processor.ICRHProcessor;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

public class DiffLogger extends AbstractItemWriter {
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @Inject
    private JobContext ctx;

    @Override
    public void writeItems(final List<Object> objects) throws Exception {
        System.out.println("Intersection size: " + objects.size());
        System.out.println();
        System.out.println("nni,resource,name(forquartz),start(forquartz),end(forquartz),name(ichr),start(ichr),end(forquartz)");
        for (final Object o : objects) {
            final ICRHProcessor.Diff diff = ICRHProcessor.Diff.class.cast(o);
            final Line forQuartz = diff.getForQuartz();
            final Line ichr = diff.getIchr();
            System.out.println(forQuartz.getNni() + "," + forQuartz.getResource()
                    + "," + forQuartz.getName() + "," + format.format(forQuartz.getBeginning()) + "," + format.format(forQuartz.getEnd())
                    + "," + ichr.getName() + "," + format.format(ichr.getBeginning()) + "," + format.format(ichr.getEnd()));
        }

        ctx.setExitStatus(objects.size() + " differences");
    }
}
