package com.luckydraw.dao;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 此类定义了一个连接池.它能够根据要求创建新连接,直到预定的最 大连接数为止.在返回连接给客户程序之前,它能够验证连接的有效性.
 */
public class DBConnectionPool
{
    private int checkedOut;

    private Vector freeConnections = new Vector();

    private int maxConn;

    private String name;

    private String password;

    private String URL;

    private String user;

    private PrintWriter log;

    /**
     * 创建新的连接池
     * 
     * @param name 连接池名字
     * @param URL 数据库的JDBC URL
     * @param user 数据库帐号,或 null
     * @param password 密码,或 null
     * @param maxConn 此连接池允许建立的最大连接数
     */
    public DBConnectionPool( String name, String URL, String user, String password, int maxConn )
    {
        this.name = name;
        this.URL = URL;
        this.user = user;
        this.password = password;
        this.maxConn = maxConn;
        this.log = new PrintWriter( System.err );
    }

    /**
     * 将不再使用的连接返回给连接池
     * 
     * @param con 客户程序释放的连接
     */
    public synchronized void freeConnection( Connection con )
    {
        // 将指定连接加入到向量末尾
        freeConnections.addElement( con );
        checkedOut--;

        System.out.println( "当前释放后的连接数：" + checkedOut );
        notifyAll();

    }

    /**
     * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接 数限制,则创建新连接.如原来登记为可用的连接不再有效,则从向量删除之, 然后递归调用自己以尝试新的可用连接.
     */
    public synchronized Connection getConnection()
    {
        Connection con = null;
        if( freeConnections.size() > 0 )
        {
            // 获取向量中第一个可用连接
            con = ( Connection ) freeConnections.firstElement();
            freeConnections.removeElementAt( 0 );
            try
            {
                if( con.isClosed() )
                {
                    log( "从连接池" + name + "删除一个无效连接" );
                    // 递归调用自己,尝试再次获取可用连接
                    con = getConnection();
                }
            }
            catch( SQLException e )
            {
                log( "从连接池" + name + "删除一个无效连接" );
                // 递归调用自己,尝试再次获取可用连接
                con = getConnection();
            }
        }
        else if( maxConn == 0 || checkedOut < maxConn )
        {
            con = newConnection();
        }
        if( con != null )
        {
            checkedOut++;
            System.out.println( "当前连接数：" + checkedOut );
        }
        return con;
    }

    /**
     * 从连接池获取可用连接.可以指定客户程序能够等待的最长时间 参见前一个getConnection()方法.
     * 
     * @param timeout 以毫秒计的等待时间限制
     */
    public synchronized Connection getConnection( long timeout )
    {
        long startTime = new Date().getTime();
        Connection con;
        while( ( con = getConnection() ) == null )
        {
            try
            {
                wait( timeout );
            }
            catch( InterruptedException e )
            {
            }
            if( ( new Date().getTime() - startTime ) >= timeout )
            {
                // wait()返回的原因是超时
                return null;
            }
        }
        return con;
    }

    /**
     * 关闭所有连接
     */
    public synchronized void release()
    {
        Enumeration allConnections = freeConnections.elements();
        while( allConnections.hasMoreElements() )
        {
            Connection con = ( Connection ) allConnections.nextElement();
            try
            {
                con.close();
                log( "关闭连接池" + name + "中的一个连接" );
            }
            catch( SQLException e )
            {
                log( e, "无法关闭连接池" + name + "中的连接" );
            }
        }
        freeConnections.removeAllElements();
    }

    /**
     * 创建新的连接
     */
    private Connection newConnection()
    {
        Connection con = null;
        try
        {
            if( user == null )
            {
                con = DriverManager.getConnection( URL );
            }
            else
            {
                con = DriverManager.getConnection( URL, user, password );
            }
            log( "连接池" + name + "创建一个新的连接" );
        }
        catch( SQLException e )
        {
            log( e, "无法创建下列URL的连接: " + URL );
            return null;
        }
        return con;
    }

    /**
     * 将文本信息写入日志文件
     */
    private void log( String msg )
    {
        log.println( new Date() + ": " + msg );
    }

    /**
     * 将文本信息与异常写入日志文件
     */
    private void log( Throwable e, String msg )
    {
        log.println( new Date() + ": " + msg );
        e.printStackTrace( log );
    }
}
