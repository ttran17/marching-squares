package com.github.ttran17.marchingsquares;

public class Grid
{
    protected final Cell[][] cells;

    protected Grid( Point[][] points )
    {
        int nVertexRows = points.length;
        int nVertexCols = points[0].length;

        // Build grid cells
        int nGridRows = nVertexRows - 1;
        int nGridCols = nVertexCols - 1;
        this.cells = new Cell[nGridRows][nGridCols];
        for ( int gridRow = 0; gridRow < nGridRows; gridRow++ )
        {
            for ( int gridCol = 0; gridCol < nGridCols; gridCol++ )
            {
                Point v0 = points[gridRow][gridCol];
                Point v1 = points[gridRow][gridCol + 1];
                Point v2 = points[gridRow + 1][gridCol + 1];
                Point v3 = points[gridRow + 1][gridCol];

                this.cells[gridRow][gridCol] = new Cell( v0, v1, v2, v3 );
            }
        }
    }

}
