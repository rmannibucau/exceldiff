package com.github.rmannibucau.jbatch.reader;

import com.github.rmannibucau.jbatch.diff.Line;
import com.github.rmannibucau.jbatch.excel.ExcelBase;
import org.apache.poi.ss.usermodel.Row;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Logger;

public class ForQuartReader extends ExcelBase implements ItemReader {
    private static final Logger LOGGER = Logger.getLogger(ForQuartReader.class.getName());

    private ClosableIterator iterator;

    @Override
    public void open(final Serializable serializable) throws Exception {
        iterator = newIterator();
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

            try {
                return new Line(
                    forceString(row.getCell(0)),
                    forceString(row.getCell(1)).trim().toLowerCase(Locale.ENGLISH),
                    forceString(row.getCell(2)),
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
