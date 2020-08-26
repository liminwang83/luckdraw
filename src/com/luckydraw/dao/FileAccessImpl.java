package com.luckydraw.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class FileAccessImpl implements FileAccess
{
    private WritableCellFormat timesBoldUnderline;

    private WritableCellFormat times;

    private String outputFile;

    public FileAccessImpl( String outputFile )
    {
        this.outputFile = outputFile;
    }

    public void writeToExcel( String title, Map<Integer, String> data, int column )
    {
        try
        {
            File file = new File( outputFile );
            if( file.exists() )
            {
                Workbook wb = Workbook.getWorkbook( new File( outputFile ) );
                WritableWorkbook wwb = Workbook.createWorkbook( new File( outputFile ), wb );
                WritableSheet excelSheet = wwb.getSheet( 0 );

                createLabel( excelSheet, title, column );
                createLabel( excelSheet, title, column + 1 );
                createContent( excelSheet, data, column );

                wwb.write();
                wb.close();
                wwb.close();
            }
            else
            {
                file.getParentFile().mkdirs();
                file.getParentFile().createNewFile();
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale( new Locale( "en", "EN" ) );
                WritableWorkbook workbook = Workbook.createWorkbook( file );

                workbook.createSheet( "Lucky Draw Results", 0 );
                WritableSheet excelSheet = workbook.getSheet( 0 );

                createLabel( excelSheet, title, column );
                createLabel( excelSheet, title, column + 1 );
                createContent( excelSheet, data, column );

                workbook.write();
                workbook.close();
            }
        }
        catch( BiffException e )
        {
            e.printStackTrace();
        }
        catch( WriteException e )
        {
            e.printStackTrace();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void deleteOutputFile()
    {
        File file = new File( outputFile );
        file.delete();
    }

    private void createLabel( WritableSheet sheet, String title, int column ) throws WriteException
    {
        // Lets create a times font
        WritableFont times10pt = new WritableFont( WritableFont.TIMES, 10 );
        // Define the cell format
        times = new WritableCellFormat( times10pt );
        // Lets automatically wrap the cells
        times.setWrap( true );

        // Create create a bold font with unterlines
        WritableFont times10ptBoldUnderline =
            new WritableFont( WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.SINGLE );
        timesBoldUnderline = new WritableCellFormat( times10ptBoldUnderline );
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap( true );

        CellView cv = new CellView();
        cv.setFormat( times );
        cv.setFormat( timesBoldUnderline );
        cv.setAutosize( true );

        // Write header
        addCaption( sheet, column, 0, title );

    }

    private void createContent( WritableSheet sheet, Map<Integer, String> data, int column ) throws WriteException,
        RowsExceededException
    {
        // Write data
        List<Integer> nsnids = new ArrayList<Integer>();
        nsnids.addAll( data.keySet() );
        for( int i = 1; i <= data.size(); i++ )
        {
            int nsnId = nsnids.get( i - 1 );
            addNumber( sheet, column, i, nsnId );
            sheet.addCell( new Label( column + 1, i, data.get( nsnId ) ) );
        }
    }

    private void addCaption( WritableSheet sheet, int column, int row, String s ) throws RowsExceededException,
        WriteException
    {
        Label label;
        label = new Label( column, row, s, timesBoldUnderline );
        sheet.addCell( label );
    }

    private void addNumber( WritableSheet sheet, int column, int row, Integer integer ) throws WriteException,
        RowsExceededException
    {
        Number number;
        number = new Number( column, row, integer, times );
        sheet.addCell( number );
    }
}
