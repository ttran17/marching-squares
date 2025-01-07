package com.github.ttran17.marchingsquares;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMarchingSquares
{
    @Test
    public void testBitShift( )
    {
        Assertions.assertEquals( 2, 1 << 1 );
        Assertions.assertEquals( 4, 1 << 2 );
        Assertions.assertEquals( 8, 1 << 3 );

        Assertions.assertEquals( 9, 1 | 1 << 3 );
    }

    @Test
    public void testWikipediaExample( )
    {
        double[][] testData = new double[][] {
                { 1, 1, 1, 1, 1 },
                { 1, 2, 3, 2, 1 },
                { 1, 3, 3, 3, 1 },
                { 1, 2, 3, 2, 1 },
                { 1, 1, 1, 1, 1 }
        };

        Point[][] points = new Point[testData.length][testData[0].length];
        for ( int i = 0; i < testData.length; i++ )
        {
            for ( int j = 0; j < testData[0].length; j++ )
            {
                points[i][j] = new Point( i, j, testData[i][j] );
            }
        }

        double isovalue = 1.1;
        IsolineMap<Point> isolineMap = MarchingSquares.computeIsoLines( points, new double[] { isovalue } );
        Assertions.assertEquals( 1, isolineMap.get( isovalue ).size( ) );

        Isoline<Point> isoline = isolineMap.get( isovalue ).getIsolines( ).get( 0 );
        Assertions.assertEquals( 13, isoline.size( ) );
        Assertions.assertEquals( isoline.get( 0 ), isoline.get( isoline.size( ) - 1 ) );
    }

    @Test
    public void testAnotherExample( )
    {
        double[][] testData = new double[][] {
                { 1, 0, 0, 0, 0, 1 },
                { 0, 1, 0, 0, 1, 0 },
                { 0, 0, 1, 1, 0, 0 },
                { 0, 0, 1, 1, 1, 0 },
                { 0, 1, 0, 1, 0, 1 },
                { 1, 0, 0, 0, 0, 1 },
                { 0, 0, 1, 0, 1, 1 },
        };

        Point[][] points = new Point[testData.length][testData[0].length];
        for ( int i = 0; i < testData.length; i++ )
        {
            for ( int j = 0; j < testData[0].length; j++ )
            {
                // remember to flip i and j for x and y
                points[i][j] = new Point( j, i, testData[i][j] );
            }
        }

        double isovalue = 0.5;
        IsolineMap<Point> isolineMap = MarchingSquares.computeIsoLines( points, new double[] { isovalue } );

        IsolineCollection<Point> isolineCollection = isolineMap.get( isovalue );
        Assertions.assertEquals( 5, isolineCollection.size( ) );

        List<Isoline<Point>> list = new ArrayList<>( isolineCollection.getIsolines( ) );
        list.sort( Comparator.comparingInt( Isoline::size ) );
        Assertions.assertEquals( 3, list.get( 0 ).size( ) );
        Assertions.assertEquals( 7, list.get( 1 ).size( ) );
        Assertions.assertEquals( 8, list.get( 2 ).size( ) );
        Assertions.assertEquals( 8, list.get( 3 ).size( ) );
        Assertions.assertEquals( 13, list.get( 4 ).size( ) );
    }

    /**
     * <strong>NOTA BENE</strong>:
     * Assertions in this test are highly dependent on the ordering (arbitrarily) enforced in:
     * {@link MarchingSquares#resolveAmbiguity(int, Point, Point, Point, Point, Cell) MarchingSquares.resolveAmbiguity()}
     * and the stack created in {@link MarchingSquares#computeContours()}.
     */
    @Test
    public void testAsymptoticDecider5( )
    {
        // wikipedia case 5 (remember that our mental model has array with row 0 at the bottom)
        double[][] testData = new double[][] {
                { 1, 0 },
                { 0, 1 },
        };

        Point[][] points = new Point[testData.length][testData[0].length];
        for ( int i = 0; i < testData.length; i++ )
        {
            for ( int j = 0; j < testData[0].length; j++ )
            {
                // remember to flip i and j for x and y
                points[i][j] = new Point( j, i, testData[i][j] );
            }
        }

        IsolineMap<Point> isolineMap = MarchingSquares.computeIsoLines( points, new double[] { 0.7, 0.3 } );

        {
            IsolineCollection<Point> isolineCollection = isolineMap.get( 0.7 );
            Assertions.assertEquals( 2, isolineCollection.size( ) );

            Isoline<Point> isoline0 = isolineCollection.get( 0 );
            Assertions.assertEquals( 2, isoline0.size( ) );
            Assertions.assertEquals( 0.0, isoline0.get( 0 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 0 ).getY( ) );
            Assertions.assertEquals( 0.0, isoline0.get( 1 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 1 ).getX( ) );

            Isoline<Point> isoline1 = isolineCollection.get( 1 );
            Assertions.assertEquals( 2, isoline1.size( ) );
            Assertions.assertEquals( 1.0, isoline1.get( 0 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 0 ).getY( ) );
            Assertions.assertEquals( 1.0, isoline1.get( 1 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 1 ).getX( ) );
        }

        {
            IsolineCollection<Point> isolineCollection = isolineMap.get( 0.3 );
            Assertions.assertEquals( 2, isolineCollection.size( ) );

            Isoline<Point> isoline0 = isolineCollection.get( 0 );
            Assertions.assertEquals( 2, isoline0.size( ) );
            Assertions.assertEquals( 0.0, isoline0.get( 0 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 0 ).getY( ) );
            Assertions.assertEquals( 1.0, isoline0.get( 1 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 1 ).getX( ) );

            Isoline<Point> isoline1 = isolineCollection.get( 1 );
            Assertions.assertEquals( 2, isoline1.size( ) );
            Assertions.assertEquals( 1.0, isoline1.get( 0 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 0 ).getY( ) );
            Assertions.assertEquals( 0.0, isoline1.get( 1 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 1 ).getX( ) );
        }
    }

    /**
     * <strong>NOTA BENE</strong>:
     * Assertions in this test are highly dependent on the ordering (arbitrarily) enforced in:
     * {@link MarchingSquares#resolveAmbiguity(int, Point, Point, Point, Point, Cell) MarchingSquares.resolveAmbiguity()}
     * and the stack created in {@link MarchingSquares#computeContours()}.
     */
    @Test
    public void testAsymptoticDecider10( )
    {
        // wikipedia case 10 (remember that our mental model has array with row 0 at the bottom)
        double[][] testData = new double[][] {
                { 0, 1 },
                { 1, 0 },
        };

        Point[][] points = new Point[testData.length][testData[0].length];
        for ( int i = 0; i < testData.length; i++ )
        {
            for ( int j = 0; j < testData[0].length; j++ )
            {
                // remember to flip i and j for x and y
                points[i][j] = new Point( j, i, testData[i][j] );
            }
        }

        IsolineMap<Point> isolineMap = MarchingSquares.computeIsoLines( points, new double[] { 0.7, 0.3 } );

        {
            IsolineCollection<Point> isolineCollection = isolineMap.get( 0.7 );
            Assertions.assertEquals( 2, isolineCollection.size( ) );

            Isoline<Point> isoline0 = isolineCollection.get( 0 );
            Assertions.assertEquals( 2, isoline0.size( ) );
            Assertions.assertEquals( 1.0, isoline0.get( 0 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 0 ).getX( ) );
            Assertions.assertEquals( 0.0, isoline0.get( 1 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 1 ).getY( ) );

            Isoline<Point> isoline1 = isolineCollection.get( 1 );
            Assertions.assertEquals( 2, isoline1.size( ) );
            Assertions.assertEquals( 0.0, isoline1.get( 0 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 0 ).getX( ) );
            Assertions.assertEquals( 1.0, isoline1.get( 1 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 1 ).getY( ) );
        }

        {
            IsolineCollection<Point> isolineCollection = isolineMap.get( 0.3 );
            Assertions.assertEquals( 2, isolineCollection.size( ) );

            Isoline<Point> isoline0 = isolineCollection.get( 0 );
            Assertions.assertEquals( 2, isoline0.size( ) );
            Assertions.assertEquals( 0.0, isoline0.get( 0 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 0 ).getX( ) );
            Assertions.assertEquals( 0.0, isoline0.get( 1 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline0.get( 1 ).getY( ) );

            Isoline<Point> isoline1 = isolineCollection.get( 1 );
            Assertions.assertEquals( 2, isoline1.size( ) );
            Assertions.assertEquals( 1.0, isoline1.get( 0 ).getY( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 0 ).getX( ) );
            Assertions.assertEquals( 1.0, isoline1.get( 1 ).getX( ) );
            Assertions.assertTrue( 0.0 < isoline1.get( 1 ).getX( ) );
        }
    }
}
