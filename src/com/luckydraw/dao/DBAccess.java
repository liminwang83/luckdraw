package com.luckydraw.dao;

import java.util.Map;

public interface DBAccess
{
    Map<Integer, String> queryData( String sql );

    Integer querySingleRow( String sql );

    int queryCount( String sql );

    int executeUpdate( String sql );
}
