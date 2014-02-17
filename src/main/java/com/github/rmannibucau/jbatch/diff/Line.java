package com.github.rmannibucau.jbatch.diff;

import java.util.Date;

public class Line {
    // key
    private final String nni;
    private final String resource;
    private final String name;
    private final Date beginning;
    private final Date end;

    public Line(final String nni, final String resource, final String name, final Date beginning, final Date end) {
        this.nni = nni;
        this.resource = resource;
        this.name = name;
        this.beginning = beginning;
        this.end = end;
    }

    public String getNni() {
        return nni;
    }

    public String getName() {
        return name;
    }

    public Date getBeginning() {
        return beginning;
    }

    public Date getEnd() {
        return end;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Line line = Line.class.cast(o);
        return nni.equals(line.nni);

    }

    @Override
    public String toString() {
        return "Line{" +
                "nni='" + nni + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
