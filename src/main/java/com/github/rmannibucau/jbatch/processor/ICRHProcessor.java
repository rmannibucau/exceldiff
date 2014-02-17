package com.github.rmannibucau.jbatch.processor;

import com.github.rmannibucau.jbatch.diff.Line;
import com.github.rmannibucau.jbatch.excel.ExcelBase;
import org.apache.poi.ss.usermodel.Row;

import javax.batch.api.chunk.ItemProcessor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import static java.lang.Math.max;

public class ICRHProcessor extends ExcelBase implements ItemProcessor {
    public static class Diff {
        private final Line forQuartz;
        private final Line ichr;

        public Diff(final Line forQuartz, final Line ichr) {
            this.forQuartz = forQuartz;
            this.ichr = ichr;
        }

        public Line getForQuartz() {
            return forQuartz;
        }

        public Line getIchr() {
            return ichr;
        }
    }

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Map<String, Collection<Line>> sheet = null;

    private Object doProcess(final Line row) {
        final Collection<Line> lines = sheet.get(row.getNni());
        if (lines != null) {
            for (final Line line : lines) {
                if (line.getName().contains(row.getName())
                        && (!line.getBeginning().equals(row.getBeginning())
                            ||  !line.getEnd().equals(row.getEnd()))) {
                    return new Diff(row, line);
                }
            }
        }
        return null;
    }

    private void loadInMemory() throws ParseException {
        sheet = new HashMap<>(10000);

        try (final ClosableIterator iterator = newIterator()) {
            while (iterator.hasNext()) {
                final Row row = iterator.next();

                if ("inscrit".equals(row.getCell(8).getStringCellValue().trim().toLowerCase(Locale.ENGLISH))) {
                    continue;
                }

                final String nni = row.getCell(1).getStringCellValue();
                final Line line = new Line(
                        nni.substring(0, max(0, nni.length() - 2)),
                        row.getCell(0).getStringCellValue().trim().toLowerCase(Locale.ENGLISH),
                        row.getCell(2).getStringCellValue(),
                        simpleDateFormat.parse(row.getCell(4).getStringCellValue()),
                        simpleDateFormat.parse(row.getCell(5).getStringCellValue())); // not standard date format
                Collection<Line> lines = sheet.get(line.getNni());
                if (lines == null) {
                    lines = new LinkedList<>();
                    sheet.put(line.getNni(), lines);
                }
                lines.add(line);
            }
        } catch (final IOException e) {
            throw new ExcelProcessorException(e);
        }
    }

    @Override
    public Object processItem(final Object o) throws Exception {
        if (sheet == null) {
            loadInMemory();
        }
        return doProcess(Line.class.cast(o));
    }

    private static class ExcelProcessorException extends RuntimeException {
        public ExcelProcessorException(final IOException e) {
            super(e);
        }
    }
}
