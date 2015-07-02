package com.cmw;

public class FlatSpot {
    int start;
    int end;

    public FlatSpot() {
    }

    public FlatSpot(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean contains(int i) {
        if ( i < start ) return false;
        if ( i > end ) return false;
        return true;
    }

    public int getSize() {
        return end-start;
    }
}
