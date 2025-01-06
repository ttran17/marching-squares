package com.github.ttran17.marchingsquares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Isoline<T>
{
    protected final List<T> points;

    public Isoline( )
    {
        this.points = new ArrayList<>( );
    }

    protected boolean add( T point )
    {
        return this.points.add( point );
    }

    public T get( int index )
    {
        return this.points.get( index );
    }

    public List<T> getPoints( )
    {
        return Collections.unmodifiableList( this.points );
    }

    public int size( )
    {
        return this.points.size( );
    }
}
