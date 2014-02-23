package com.github.rmannibucau.jbatch.reader;

import com.github.rmannibucau.jbatch.diff.Line;
import com.github.rmannibucau.jbatch.excel.ExcelBase;
import org.apache.poi.ss.usermodel.Row;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Logger;

public class ForQuartReader extends ExcelBase implements ItemReader {
    private static final Logger LOGGER = Logger.getLogger(ForQuartReader.class.getName());

    private ClosableIterator iterator;

    @Inject
    @BatchProperty
    private String filter;
    private String[] filters = null;

    @Override
    public void open(final Serializable serializable) throws Exception {
        iterator = newIterator();
        if (filter != null) {
            final File filterFile = new File(filter);
            if (filterFile.isFile()) {
                final Collection<String> f = new LinkedList<>();
                try (final BufferedReader reader = new BufferedReader(new FileReader(filterFile))) {
                    String s;
                    while ((s = reader.readLine()) != null) {
                        f.add(s);
                    }
                }
                filters = f.toArray(new String[f.size()]);
            } else {
                filters = filter.split(",");
            }
        }
    }

    @Override
    public void close() throws Exception {
        iterator.close();
    }

    @Override
    public Object readItem() throws Exception {
        while (iterator.hasNext()) {
            final Row row = iterator.next();

            final String status = row.getCell(9).getStringCellValue().toLowerCase(Locale.ENGLISH).trim();
            if (!"en prévision".equals(status) && !"inscript. réalisée".equals(status)) {
                continue;
            }

            final String name = forceString(row.getCell(2));
            boolean filtered = false;
            if (filters != null) {
                for (final String filter : filters) {
                    if (name.contains(filter)) {
                        filtered = true;
                    }
                }
                if (!filtered) {
                    continue;
                }
            }

            try {
                return new Line(
                    forceString(row.getCell(0)),
                    forceString(row.getCell(1)).trim().toLowerCase(Locale.ENGLISH),
                        name,
                    row.getCell(3).getDateCellValue(),
                    row.getCell(4).getDateCellValue());
            } catch (final IllegalStateException ise) {
                LOGGER.severe("Can't parse line: " + row);
                throw ise;
            }
        }
        return null;
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
