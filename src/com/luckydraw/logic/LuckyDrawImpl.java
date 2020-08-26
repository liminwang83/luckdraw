package com.luckydraw.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.luckydraw.dao.DBAccess;
import com.luckydraw.dao.DBAccessImpl;
import com.luckydraw.dao.FileAccess;
import com.luckydraw.dao.FileAccessImpl;

public class LuckyDrawImpl implements LuckyDraw
{
    private DBAccess dbAccess;

    private FileAccess fileAccess;

    private Map<Integer, String> available;

    public LuckyDrawImpl()
    {
        dbAccess = new DBAccessImpl();
        fileAccess = new FileAccessImpl( "J:\\RI.xls" );
        reset();
    }

    public void generateExcel()
    {
        Map<Integer, String> class4 = dbAccess.queryData( "select * from employees where class=4" );
        Map<Integer, String> class3 = dbAccess.queryData( "select * from employees where class=3" );
        Map<Integer, String> class2 = dbAccess.queryData( "select * from employees where class=2" );
        Map<Integer, String> class1 = dbAccess.queryData( "select * from employees where class=1" );
        Map<Integer, String> class0 = dbAccess.queryData( "select * from employees where class=0" );
        Map<Integer, String> class7 = dbAccess.queryData( "select * from employees where class=7" );

        if( class4.size() > 0 )
        {
            fileAccess.writeToExcel( "四等奖", class4, 0 );
        }
        if( class3.size() > 0 )
        {
            fileAccess.writeToExcel( "三等奖", class3, 3 );
        }
        if( class2.size() > 0 )
        {
            fileAccess.writeToExcel( "二等奖", class2, 6 );
        }
        if( class1.size() > 0 )
        {
            fileAccess.writeToExcel( "一等奖", class1, 9 );
        }
        if( class0.size() > 0 )
        {
            fileAccess.writeToExcel( "特等奖", class0, 12 );
        }
        if( class7.size() > 0 )
        {
            fileAccess.writeToExcel( "安慰奖", class7, 15 );
        }
    }

    //四等奖
    public void saveClass4( int suffixNum1, int suffixNum2 )
    {
        String sql =
            "update employees set chosen=true, class=4 where nsnid % 10=" + suffixNum1 + " or nsnid % 10=" + suffixNum2;
        //output to database
        dbAccess.executeUpdate( sql );
    }

    //三等奖
    public void saveClass3( int suffixNum )
    {
        String sql = "update employees set chosen=true, class=3 where nsnid % 10=" + suffixNum;
        //output to database
        dbAccess.executeUpdate( sql );
    }

    public void saveClass2( Set<Integer> results )
    {
        updateDb( 2, results );
    }

    public void saveClass1( Set<Integer> results )
    {
        updateDb( 1, results );
    }

    public void saveSpecialAward( Set<Integer> results )
    {
        updateDb( 0, results );
    }

    /**
     * 安慰奖，级别设置为7
     * 
     * @param results
     */
    public void saveConsolationPrize( Set<Integer> results )
    {
        updateDb( 7, results );
    }

    /**
     * 补抽而中奖的,级别统一标记为10
     * 
     * @param results
     */
    public void saveReplenishPrize( int level, Set<Integer> results )
    {
        updateDb( level, results );
    }

    public void reset()
    {
        available = null;
        dbAccess.executeUpdate( "update employees set chosen=false, class=-1" );
        fileAccess.deleteOutputFile();
    }

    public void cacheData()
    {
        available = dbAccess.queryData( "select * from employees where chosen=false" );
    }

    public Map<Integer, String> randomSelectFromEmployees( int count )
    {
        Map<Integer, String> selected = new HashMap<Integer, String>();
        Random random = new Random();
        int tatal = available.size();
        List<Integer> ids = new ArrayList<Integer>();
        ids.addAll( available.keySet() );
        while( selected.size() < count )
        {
            int randomIndex = random.nextInt( tatal );
            Integer select = ids.get( randomIndex );
            if( selected.get( select ) == null )
            {
                selected.put( select, available.get( select ) );
            }
        }
        return selected;
    }

    private void updateDb( int level, Set<Integer> results )
    {
        StringBuilder sqlBuilder = new StringBuilder( "update employees set chosen=true, class=" );
        sqlBuilder.append( level ).append( " where nsnid in(" );
        for( Integer nsnid : results )
        {
            sqlBuilder.append( nsnid ).append( "," );
        }
        String sql = sqlBuilder.toString();
        int index = sql.lastIndexOf( "," );
        sql = sql.substring( 0, index );
        sql = sql + ")";
        dbAccess.executeUpdate( sql );
        available = null;
    }
}
