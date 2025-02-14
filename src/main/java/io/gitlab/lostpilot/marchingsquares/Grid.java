package io.gitlab.lostpilot.marchingsquares;

/**
 * Auxiliary class to help with the book-keeping.
 */
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
                // Instead of saving references to these vertices in Cell[][],
                // we could choose to access the vertices via Point[][] array
                // and indexing as shown here. If saving space is paramount
                // to saving time, Cell[][] can be removed with very little work.
                Point v0 = points[gridRow][gridCol];
                Point v1 = points[gridRow][gridCol + 1];
                Point v2 = points[gridRow + 1][gridCol + 1];
                Point v3 = points[gridRow + 1][gridCol];

                this.cells[gridRow][gridCol] = new Cell( v0, v1, v2, v3 );
            }
        }
    }

}
