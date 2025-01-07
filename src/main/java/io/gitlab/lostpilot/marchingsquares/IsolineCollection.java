package io.gitlab.lostpilot.marchingsquares;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of isolines. This implementation uses a list as its underlying collection list.
 * <br><br>
 * The alternative to this class is to pass around a list of lists of Points:
 * List&lt;List&lt;Point&gt;&gt;.
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

    public boolean isEmpty( )
    {
        return this.isolines.isEmpty( );
    }

    public int size( )
    {
        return isolines.size( );
    }
}
