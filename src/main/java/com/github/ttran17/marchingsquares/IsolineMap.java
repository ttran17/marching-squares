package com.github.ttran17.marchingsquares;

import it.unimi.dsi.fastutil.doubles.Double2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * A hash map of collections of isolines keyed by isovalue.
 */
public class IsolineMap<T>
{
    protected final Double2ObjectMap<IsolineCollection<T>> isolineMap;

    public IsolineMap( )
    {
        this.isolineMap = new Double2ObjectLinkedOpenHashMap<>( );
    }

    public IsolineCollection<T> put( double isovalue, IsolineCollection<T> isolineCollection )
    {
        return this.isolineMap.put( isovalue, isolineCollection );
    }

    public IsolineCollection<T> get( double isovalue )
    {
        return this.isolineMap.getOrDefault( isovalue, new IsolineCollection<>( ) );
    }

    /**
     * @return A type-specific view of the underlying entry set. See {@link Double2ObjectMap#double2ObjectEntrySet()}.
     */
    public ObjectSet<Double2ObjectMap.Entry<IsolineCollection<T>>> entrySet( )
    {
        return isolineMap.double2ObjectEntrySet( );
    }

    public boolean isEmpty( )
    {
        return this.isolineMap.isEmpty( );
    }

    public int size( )
    {
        return this.isolineMap.size( );
    }
}
