package com.github.ttran17.marchingsquares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IsolineList<T>
{
    protected final List<Isoline<T>> isolines;

    public IsolineList( )
    {
        this.isolines = new ArrayList<>( );
    }

    protected boolean add( Isoline<T> isoline )
    {
        return this.isolines.add( isoline );
    }

    protected Isoline<T> get( int index )
    {
        return this.isolines.get( index );
    }

    public List<Isoline<T>> getIsolines( )
    {
        return Collections.unmodifiableList( this.isolines );
    }

    public int size( )
    {
        return isolines.size( );
    }
}
