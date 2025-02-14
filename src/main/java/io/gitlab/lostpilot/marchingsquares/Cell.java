package io.gitlab.lostpilot.marchingsquares;

/**
 * Auxiliary class to lighten the mental book-keeping.
 */
public class Cell
{
    // Counter-clockwise from lower left
    protected final Point v0, v1, v2, v3;

    protected Cell( Point v0, Point v1, Point v2, Point v3 )
    {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }
}
