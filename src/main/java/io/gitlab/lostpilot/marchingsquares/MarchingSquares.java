package io.gitlab.lostpilot.marchingsquares;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verbatim from <a href="https://en.wikipedia.org/wiki/Marching_squares">Wikipedia: marching squares</a> is an algorithm that generates contours for a two-dimensional scalar field
 * (rectangular array of individual numerical values).
 */
public class MarchingSquares
{
    protected final Grid grid;
    protected final double isovalue;

    protected final Point[][] horizontalEdgePoints;
    protected final Point[][] verticalEdgePoints;

    protected final Segment[][][] segments;

    protected int[][] binary;

    protected MarchingSquares( Grid grid, double isovalue )
    {
        this.grid = grid;
        this.isovalue = isovalue;

        int nGridRows = grid.cells.length;
        int nGridCols = grid.cells[0].length;

        this.horizontalEdgePoints = new Point[nGridRows + 1][nGridRows];
        this.verticalEdgePoints = new Point[nGridRows][nGridCols + 1];

        this.segments = new Segment[nGridRows][nGridCols][];

        this.binary = new int[nGridRows][nGridCols];
    }

    protected IsolineCollection<Point> computeIsoLines( )
    {
        computeInterpolationPoints( );
        computeSegments( );
        return computeContours( );
    }

    protected void computeInterpolationPoints( )
    {
        Cell[][] cells = grid.cells;

        int nGridRows = cells.length;
        int nGridCols = cells[0].length;

        // Loop through grid and for each cell just handle left and bottom edges
        for ( int gridRow = 0; gridRow < nGridRows; gridRow++ )
        {
            for ( int gridCol = 0; gridCol < nGridCols; gridCol++ )
            {
                Cell cell = cells[gridRow][gridCol];

                Point v0 = cell.v0;
                Point v1 = cell.v1;
                Point v3 = cell.v3;

                int b0 = v0.getZ( ) < isovalue ? 0 : 1;
                int b1 = v1.getZ( ) < isovalue ? 0 : 1;
                int b3 = v3.getZ( ) < isovalue ? 0 : 1;

                if ( b0 != b1 )
                {
                    double x01 = lerp( v0.getX( ), v0.getZ( ), v1.getX( ), v1.getZ( ), isovalue );
                    double y01 = v0.getY( );
                    horizontalEdgePoints[gridRow][gridCol] = new Point( x01, y01, isovalue );
                }

                if ( b3 != b0 )
                {
                    double x30 = v3.getX( );
                    double y30 = lerp( v3.getY( ), v3.getZ( ), v0.getY( ), v0.getZ( ), isovalue );
                    verticalEdgePoints[gridRow][gridCol] = new Point( x30, y30, isovalue );
                }
            }
        }

        // Now handle the remaining top edge
        {
            int gridRow = nGridRows - 1;
            for ( int gridCol = 0; gridCol < nGridCols; gridCol++ )
            {
                Cell cell = cells[gridRow][gridCol];

                Point v2 = cell.v2;
                Point v3 = cell.v3;

                int b2 = v2.getZ( ) < isovalue ? 0 : 1;
                int b3 = v3.getZ( ) < isovalue ? 0 : 1;

                if ( b2 != b3 )
                {
                    double x23 = lerp( v2.getX( ), v2.getZ( ), v3.getX( ), v3.getZ( ), isovalue );
                    double y23 = v2.getY( );
                    horizontalEdgePoints[nGridRows][gridCol] = new Point( x23, y23, isovalue );
                }
            }
        }

        // And handle the remaining right edge
        {
            int gridCol = nGridCols - 1;
            for ( int gridRow = 0; gridRow < nGridRows; gridRow++ )
            {
                Cell cell = cells[gridRow][gridCol];

                Point v1 = cell.v1;
                Point v2 = cell.v2;

                int b1 = v1.getZ( ) < isovalue ? 0 : 1;
                int b2 = v2.getZ( ) < isovalue ? 0 : 1;

                if ( b1 != b2 )
                {
                    double x12 = v1.getX( );
                    double y12 = lerp( v1.getY( ), v1.getZ( ), v2.getY( ), v2.getZ( ), isovalue );
                    verticalEdgePoints[gridRow][nGridCols] = new Point( x12, y12, isovalue );
                }
            }
        }
    }

    /**
     * Given an isovalue alpha, segments are computed so that values lower than alpha are on the left as we walk along an edge.
     */
    protected Segment[] computeSegments( int lookupIndex, Point p01, Point p12, Point p23, Point p30, Cell cell )
    {
        return switch ( lookupIndex )
        {
            case 0, 15 -> Segment.EMPTY;
            case 1 -> new Segment[] { new Segment( p30, p01 ) };
            case 2 -> new Segment[] { new Segment( p01, p12 ) };
            case 3 -> new Segment[] { new Segment( p30, p12 ) };
            case 4 -> new Segment[] { new Segment( p12, p23 ) };
            case 6 -> new Segment[] { new Segment( p01, p23 ) };
            case 7 -> new Segment[] { new Segment( p30, p23 ) };
            case 8 -> new Segment[] { new Segment( p23, p30 ) };
            case 9 -> new Segment[] { new Segment( p23, p01 ) };
            case 11 -> new Segment[] { new Segment( p23, p12 ) };
            case 12 -> new Segment[] { new Segment( p12, p30 ) };
            case 13 -> new Segment[] { new Segment( p12, p01 ) };
            case 14 -> new Segment[] { new Segment( p01, p30 ) };
            case 5, 10 -> resolveAmbiguity( lookupIndex, p01, p12, p23, p30, cell );
            default -> throw new IllegalArgumentException( "Illegal lookupIndex: " + lookupIndex );
        };
    }

    /**
     * Resolve ambiguity in case lookupIndex == 5 or lookupIndex == 10 using
     * <a href=" https://people.eecs.berkeley.edu/~jrs/meshpapers/NielsonHamann.pdf">asymptotic decider</a>.
     * <br><br>
     * Note that computation of bilinear interpolant (bi) at intersection of asymptotes is incorrect (wrong sign) in this paper.
     */
    protected Segment[] resolveAmbiguity( int lookupIndex, Point p01, Point p12, Point p23, Point p30, Cell cell )
    {
        double b00 = cell.v0.getZ( );
        double b10 = cell.v1.getZ( );
        double b11 = cell.v2.getZ( );
        double b01 = cell.v3.getZ( );

        // Paper incorrectly has numerator as b00 * b11 + b10 * b01
        double bi = ( b00 * b11 - b10 * b01 ) / ( b00 + b11 - b01 - b10 );

        if ( lookupIndex == 5 )
        {
            if ( bi < isovalue )
            {
                return new Segment[] {
                        new Segment( p12, p23 ),
                        new Segment( p30, p01 )
                };
            }
            else
            {
                return new Segment[] {
                        new Segment( p12, p01 ),
                        new Segment( p30, p23 )
                };
            }
        }
        else // lookupIndex == 10
        {
            if ( bi < isovalue )
            {
                return new Segment[] {
                        new Segment( p01, p12 ),
                        new Segment( p23, p30 )
                };
            }
            else
            {
                return new Segment[] {
                        new Segment( p23, p12 ),
                        new Segment( p01, p30 )
                };
            }
        }
    }

    protected void computeSegments( )
    {
        Cell[][] cells = grid.cells;

        int nGridRows = cells.length;
        int nGridCols = cells[0].length;

        // Finally, loop through edge points and assign them to cells
        for ( int gridRow = 0; gridRow < nGridRows; gridRow++ )
        {
            for ( int gridCol = 0; gridCol < nGridCols; gridCol++ )
            {
                Cell cell = cells[gridRow][gridCol];

                Point v0 = cell.v0;
                Point v1 = cell.v1;
                Point v2 = cell.v2;
                Point v3 = cell.v3;

                int b0 = v0.getZ( ) < isovalue ? 0 : 1;
                int b1 = v1.getZ( ) < isovalue ? 0 : 1;
                int b2 = v2.getZ( ) < isovalue ? 0 : 1;
                int b3 = v3.getZ( ) < isovalue ? 0 : 1;

                int lookupIndex = b0 | ( b1 << 1 ) | ( b2 << 2 ) | ( b3 << 3 );
                this.binary[gridRow][gridCol] = lookupIndex;

                Point p01 = horizontalEdgePoints[gridRow][gridCol];
                Point p12 = verticalEdgePoints[gridRow][gridCol + 1];
                Point p23 = horizontalEdgePoints[gridRow + 1][gridCol];
                Point p30 = verticalEdgePoints[gridRow][gridCol];

                segments[gridRow][gridCol] = computeSegments( lookupIndex, p01, p12, p23, p30, cell );
            }
        }
    }

    protected IsolineCollection<Point> computeContours( )
    {
        IsolineCollection<Point> contours = new IsolineCollection<>( );

        Cell[][] cells = grid.cells;

        int nGridRows = cells.length;
        int nGridCols = cells[0].length;

        // Create mapping from starting points to segments
        Map<Point, Segment> point2Segment = new LinkedHashMap<>( );
        Map<Point, Boolean> noLoopStartPoints = new LinkedHashMap<>( );
        for ( int gridRow = 0; gridRow < nGridRows; gridRow++ )
        {
            for ( int gridCol = 0; gridCol < nGridCols; gridCol++ )
            {
                for ( Segment segment : segments[gridRow][gridCol] )
                {
                    point2Segment.put( segment.getStart( ), segment );
                    noLoopStartPoints.putIfAbsent( segment.getStart( ), Boolean.TRUE );
                    noLoopStartPoints.put( segment.getEnd( ), Boolean.FALSE );
                }
            }
        }

        // Handle non-closed contours
        Deque<Point> stack = new LinkedList<>( );
        for ( Point point : point2Segment.keySet( ) )
        {
            if ( noLoopStartPoints.get( point ) == Boolean.TRUE )
            {
                stack.push( point );
            }
        }
        while ( !stack.isEmpty( ) )
        {
            Point startingPoint = stack.pop( );
            computeContour( startingPoint, point2Segment, contours );
        }

        // Handle closed contours
        while ( !point2Segment.isEmpty( ) )
        {
            Point startingPoint = point2Segment.keySet( ).stream( ).findAny( ).get( );
            computeContour( startingPoint, point2Segment, contours );
        }

        return contours;
    }

    protected void computeContour( Point startingPoint, Map<Point, Segment> point2Segment, IsolineCollection<Point> contours )
    {
        Queue<Point> queue = new LinkedList<>( );
        queue.offer( startingPoint );

        // Follow contour from starting point by way of point2Segment map
        Isoline<Point> contour = new Isoline<>( );
        while ( !queue.isEmpty( ) )
        {
            Point point = queue.poll( );
            contour.add( point );
            Segment segment = point2Segment.remove( point );
            if ( segment != null )
            {
                queue.add( segment.getEnd( ) );
            }
            else
            {
                contours.add( contour );
            }
        }
    }

    /**
     * Given (u0,v0) and (u1,v1) finds u in (u,isovalue) via standard linear interpolation.
     */
    protected static double lerp( double u0, double v0, double u1, double v1, double isovalue )
    {
        return u0 + ( isovalue - v0 ) * ( u1 - u0 ) / ( v1 - v0 );
    }

    /**
     * Generates <a href="https://en.wikipedia.org/wiki/Marching_squares">isolines</a>
     * (lines following a single data level, or {@code isovalue}) for a two-dimensional scalar field {@code Point[][]}.
     *
     * @param points A scalar field of points (x,y,z). Assumes that z = f(x,y) where f:R^2 --> R is continuous.
     * @param isovalues Array of isovalues.
     * @return A hash map of collections of isolines keyed by isovalue.
     */
    public static IsolineMap<Point> computeIsoLines( Point[][] points, double[] isovalues )
    {
        IsolineMap<Point> isolineMap = new IsolineMap<>( );

        Grid grid = new Grid( points );

        for ( double isovalue : isovalues )
        {
            MarchingSquares marchingSquares = new MarchingSquares( grid, isovalue );
            IsolineCollection<Point> isolineCollection = marchingSquares.computeIsoLines( );
            isolineMap.put( isovalue, isolineCollection );
        }

        return isolineMap;
    }

    /**
     * Same as {@link #computeIsoLines()} except that this method parallelizes the computation over the array of isovalues.
     * Note that the computation for any single isovalue is <em>not</em> parallelized! Therefore, this method is only useful
     * for large arrays of isovalues.
     */
    public static IsolineMap<Point> parallelComputeIsoLines( Point[][] points, double[] isovalues )
    {
        Map<Double, IsolineCollection<Point>> concurrentIsolineMap = new ConcurrentHashMap<>( );

        Grid grid = new Grid( points );

        List<Double> isovalueList = new ArrayList<>( );
        for ( double isovalue : isovalues )
        {
            isovalueList.add( isovalue );
        }

        isovalueList.parallelStream( ).forEach( ( isovalue ) -> {
            MarchingSquares marchingSquares = new MarchingSquares( grid, isovalue );
            IsolineCollection<Point> isolineCollection = marchingSquares.computeIsoLines( );
            concurrentIsolineMap.put( isovalue, isolineCollection );
        } );

        // Guarantees insertion order to be the same as in non-parallel case
        IsolineMap<Point> isolineMap = new IsolineMap<>( );
        for ( double isovalue : isovalueList )
        {
            isolineMap.put( isovalue, concurrentIsolineMap.get( isovalue ) );
        }

        return isolineMap;
    }
}
