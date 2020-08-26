package com.luckydraw.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.luckydraw.util.Config;

public class DBAccessImpl implements DBAccess
{
    private DBConnectionPool dbConnectionPool;

    public DBAccessImpl()
    {
        String url = Config.getProperties().getProperty( "database.url" );
        String user = Config.getProperties().getProperty( "database.username" );
        String password = Config.getProperties().getProperty( "database.password" );
        final int maxConn = 100;
        dbConnectionPool = new DBConnectionPool( "ConnectionPool", url, user, password, maxConn );
    }

    public int executeUpdate( String sql )
    {
        Connection conn = dbConnectionPool.getConnection();
        try
        {
            Statement statement = conn.createStatement();
            return statement.executeUpdate( sql );
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            dbConnectionPool.freeConnection( conn );
        }

        return -1;
    }

    public int queryCount( String sql )
    {
        Connection conn = dbConnectionPool.getConnection();
        try
        {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery( sql );
            resultSet.next();
            int rowCount = resultSet.getInt( "rowCount" );
            return rowCount;
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            dbConnectionPool.freeConnection( conn );
        }
        return 0;
    }

    public Map<Integer, String> queryData( String sql )
    {
        Connection conn = dbConnectionPool.getConnection();
        Map<Integer, String> resultMap = new HashMap<Integer, String>();
        try
        {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery( sql );
            while( rs.next() )
            {
                int nsnId = rs.getInt( "nsnid" );
                String ename = rs.getString( "ename" );
                resultMap.put( nsnId, ename );
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            dbConnectionPool.freeConnection( conn );
        }

        return resultMap;
    }

    /**
     * the caller should ensure that the sql will only return single row.
     * 
     * @param sql
     * @return
     */
    public Integer querySingleRow( String sql )
    {
        Connection conn = dbConnectionPool.getConnection();
        Integer nsnId = null;
        try
        {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery( sql );
            if( rs.next() )
            {
                nsnId = rs.getInt( "nsnid" );
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            dbConnectionPool.freeConnection( conn );
        }
        return nsnId;
    }

}
