package com.luckydraw.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.luckydraw.dao.DBAccess;
import com.luckydraw.dao.DBAccessImpl;

/**
 * 操作Excel表格的功能类
 * 
 * @author：hnylj
 * @version 1.0
 */
public class ExcelReader
{
    private POIFSFileSystem fs;

    private HSSFWorkbook wb;

    private HSSFSheet sheet;

    private HSSFRow row;

    /**
     * 读取Excel表格表头的内容
     * 
     * @param InputStream
     * @return String 表头内容的数组
     */
    public String[] readExcelTitle( InputStream is )
    {
        try
        {
            fs = new POIFSFileSystem( is );
            wb = new HSSFWorkbook( fs );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        sheet = wb.getSheetAt( 0 );
        row = sheet.getRow( 0 );
        //标题总列数   
        int colNum = row.getPhysicalNumberOfCells();
        String[] title = new String[ colNum ];
        for( int i = 0; i < colNum; i++ )
        {
            title[i] = getStringCellValue( row.getCell( ( short ) i ) );
        }
        return title;
    }

    /**
     * 读取Excel数据内容
     * 
     * @param InputStream
     * @return Map 包含单元格数据内容的Map对象
     */
    public Map<Integer, String> readExcelContent( InputStream is )
    {
        Map<Integer, String> content = new HashMap<Integer, String>();
        try
        {
            fs = new POIFSFileSystem( is );
            wb = new HSSFWorkbook( fs );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        sheet = wb.getSheetAt( 0 );
        //得到总行数   
        int rowNum = sheet.getLastRowNum();
        row = sheet.getRow( 0 );
        //正文内容应该从第二行开始,第一行为表头的标题   
        for( int i = 0; i <= rowNum; i++ )
        {
            row = sheet.getRow( i );
            Integer nsnid = ( int ) row.getCell( 0 ).getNumericCellValue();
            String ename = row.getCell( 1 ).getStringCellValue();
            content.put( nsnid, ename );
        }
        return content;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     * 
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private String getStringCellValue( HSSFCell cell )
    {
        String strCell = "";
        switch( cell.getCellType() )
        {
            case HSSFCell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                strCell = String.valueOf( cell.getNumericCellValue() );
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf( cell.getBooleanCellValue() );
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if( strCell.equals( "" ) || strCell == null )
        {
            return "";
        }
        if( cell == null )
        {
            return "";
        }
        return strCell;
    }

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        try
        {
            InputStream is = new FileInputStream( "J:\\cc.xls" );
            ExcelReader reader = new ExcelReader();
            Map<Integer, String> output = reader.readExcelContent( is );
            DBAccess dbAccess = new DBAccessImpl();
            int i = 1;
            for( Integer integer : output.keySet() )
            {
                String username = output.get( integer );
                String sql =
                    "insert into employees(id,nsnid,ename,chosen,class) values(" + i++ + "," + integer + ",'" +
                        username + "', 0,-1)";
                dbAccess.executeUpdate( sql );
//                System.out.println( integer + ename );
            }
//            dbAccess.executeUpdate( "delete from candidates where chosen=false" );
        }
        catch( FileNotFoundException e )
        {

            e.printStackTrace();
        }

    }
}
