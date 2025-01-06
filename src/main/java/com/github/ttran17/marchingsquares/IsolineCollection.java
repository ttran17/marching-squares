package com.github.ttran17.marchingsquares;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of isolines. This implementation uses a list as its underlying collection list.
 */
public class IsolineCollection<T>
{
    protected final List<Isoline<T>> isolines;

    public IsolineCollection( )
    {
        this.isolines = new ArrayList<>( );
    }

    public boolean add( Isoline<T> isoline )
    {
        return this.isolines.add( isoline );
    }

    public Isoline<T> get( int index )
    {
        return this.isolines.get( index );
    }

    public List<Isoline<T>> getIsolines( )
    {
        return this.isolines;
    }

    public int size( )
    {
        return isolines.size( );
    }
}
