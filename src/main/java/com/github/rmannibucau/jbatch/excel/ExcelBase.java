package com.github.rmannibucau.jbatch.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.batch.api.BatchProperty;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public abstract class ExcelBase {
    @Inject
    @BatchProperty
    private String path;

    protected ClosableIterator newIterator() throws IOException {
        if (path == null) {
            throw new NullPointerException("path is null");
        }

        final File file = new File(path);
        if (!file.isFile()) {
            throw new IllegalArgumentException("'" + path + "' path doesn't exist");
        }

        final FileInputStream inputStream = new FileInputStream(file);
        final Workbook workbook;
        if (file.getName().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            workbook = new HSSFWorkbook(inputStream);
        }

        final Sheet sheet = workbook.getSheetAt(0);
        final ClosableIterator closableIterator = new ClosableIterator(sheet.iterator(), inputStream);

        // ignore headers
        if (closableIterator.hasNext()) {
            closableIterator.next();
        }

        return closableIterator;
    }

    protected static String forceString(final Cell cell) {
        try {
            return cell.getStringCellValue();
        } catch (final IllegalStateException ise) {
            return Double.toString(cell.getNumericCellValue());
        }
    }

    public static class ClosableIterator implements Iterator<Row>, AutoCloseable {
        private final Iterator<Row> delegate;
        private final FileInputStream stream;

        public ClosableIterator(final Iterator<Row> delegate, final FileInputStream inputStream) {
            this.delegate = delegate;
            this.stream = inputStream;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public Row next() {
            return delegate.next();
        }

        @Override
        public void remove() {
            delegate.remove();
        }

        @Override
        public void close() {
            try {
                stream.close();
            } catch (final IOException e) {
                // no-op
            }
        }
    }
}
