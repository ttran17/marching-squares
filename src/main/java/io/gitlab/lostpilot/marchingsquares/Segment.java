package io.gitlab.lostpilot.marchingsquares;

public class Segment
{
    public static final Segment[] EMPTY = new Segment[0];

    protected final Point start;
    protected final Point end;

    protected Segment( Point start, Point end )
    {
        this.start = start;
        this.end = end;
    }

    public Point getStart( )
    {
        return start;
    }

    public Point getEnd( )
    {
        return end;
    }
}
