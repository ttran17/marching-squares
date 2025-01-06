package com.github.ttran17.marchingsquares;

import it.unimi.dsi.fastutil.doubles.Double2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;

public class IsolineMap<T>
{
    protected final Double2ObjectMap<IsolineList<T>> isolineMap;

    public IsolineMap( )
    {
        this.isolineMap = new Double2ObjectLinkedOpenHashMap<>( );
    }

    protected IsolineList<T> put( double isovalue, IsolineList<T> isolineList )
    {
        return this.isolineMap.put( isovalue, isolineList );
    }

    public IsolineList<T> get( double isovalue )
    {
        return this.isolineMap.getOrDefault( isovalue, new IsolineList<>( ) );
    }
}
