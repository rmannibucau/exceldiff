package com.github.rmannibucau.jbatch.reader;

import com.github.rmannibucau.jbatch.diff.Line;
import com.github.rmannibucau.jbatch.excel.ExcelBase;
import org.apache.poi.ss.usermodel.Row;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;
import java.util.Locale;

public class ForQuartReader extends ExcelBase implements ItemReader {
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

            return new Line(
                    row.getCell(0).getStringCellValue(),
                    row.getCell(1).getStringCellValue().trim().toLowerCase(Locale.ENGLISH),
                    row.getCell(2).getStringCellValue(),
                    row.getCell(3).getDateCellValue(),
                    row.getCell(4).getDateCellValue());
        }
        return null;
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
