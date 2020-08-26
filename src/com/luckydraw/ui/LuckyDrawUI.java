package com.luckydraw.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import com.luckydraw.logic.LuckyDraw;
import com.luckydraw.logic.LuckyDrawImpl;

public class LuckyDrawUI extends JFrame implements ActionListener
{
    private JButton draw4thButton;

    private JButton draw3rdButton;

    private JButton draw2ndButton;

    private JButton draw1stButton;

    private JButton drawSpecialButton;

    private JButton drawConsolationButton;

    private JButton drawReplenishButton;

    private JButton stopDraw4thButton;

    private JButton stopDraw3rdButton;

    private JButton stopDraw2ndButton;

    private JButton stopDraw1stButton;

    private JButton stopDrawSpecialButton;

    private JButton stopDrawConsolationButton;

    private JButton stopDrawReplenishButton;

    private JButton resetButton;

    private JButton provisionButton;

    private JPanel contentPanel;

    private JLabel contentLabel;

    private JTextField numField;

    private JComboBox awardLevel;

    private LuckyDraw luckyDrawAction;

    private int random1;

    private int random2;

    private int random3;

    private Timer timer;

    //cache data
    private Map<Integer, String> results;

    //cache data
    private Map<Integer, String> firstLevelCache = new HashMap<Integer, String>();

    private List<Integer> nsnIdCache = new ArrayList<Integer>();

    private final int LABEL_REFRESH_PERIOD = 40;

    private final int TABLE_REFRESH_PERIOD = 90;

    //为安慰奖预备
    private static Map<Integer, Boolean> awardItem = new HashMap<Integer, Boolean>( 65 );

    static
    {
        for( int i = 0; i < 65; i++ )
        {
            awardItem.put( i, Boolean.FALSE );
        }
    }

    public LuckyDrawUI()
    {
        init();
    }

    private void init()
    {
        luckyDrawAction = new LuckyDrawImpl();

        resetButton = new JButton( "初始化数据" );
        draw4thButton = new JButton( "抽取四等奖(0/2)" );
        draw3rdButton = new JButton( "抽取三等奖" );
        draw2ndButton = new JButton( "抽取二等奖(0/15)" );
        draw1stButton = new JButton( "抽取一等奖(0/4)" );
        drawSpecialButton = new JButton( "抽取特等奖(0/5)" );
        drawConsolationButton = new JButton( "抽取安慰奖(0/13)" );
        drawReplenishButton = new JButton( "补抽" );
        awardLevel = new JComboBox( new String[]{ "二等奖", "一等奖", "特等奖", "安慰奖" } );
        numField = new JTextField( 4 );
        provisionButton = new JButton( "生成 Excel" );

        stopDraw4thButton = new JButton( "停止滚动" );
        stopDraw3rdButton = new JButton( "停止滚动" );
        stopDraw2ndButton = new JButton( "停止滚动" );
        stopDraw1stButton = new JButton( "停止滚动" );
        stopDrawSpecialButton = new JButton( "停止滚动" );
        stopDrawConsolationButton = new JButton( "停止滚动" );
        stopDrawReplenishButton = new JButton( "停止滚动" );

        resetButton.addActionListener( this );
        draw4thButton.addActionListener( this );
        draw3rdButton.addActionListener( this );
        draw2ndButton.addActionListener( this );
        draw1stButton.addActionListener( this );
        drawSpecialButton.addActionListener( this );
        drawConsolationButton.addActionListener( this );
        drawReplenishButton.addActionListener( this );
        provisionButton.addActionListener( this );

        stopDraw4thButton.addActionListener( this );
        stopDraw3rdButton.addActionListener( this );
        stopDraw2ndButton.addActionListener( this );
        stopDraw1stButton.addActionListener( this );
        stopDrawSpecialButton.addActionListener( this );
        stopDrawConsolationButton.addActionListener( this );
        stopDrawReplenishButton.addActionListener( this );

        resetButton.setEnabled( true );
        draw4thButton.setEnabled( false );
        draw3rdButton.setEnabled( false );
        draw2ndButton.setEnabled( false );
        draw1stButton.setEnabled( true );
        drawSpecialButton.setEnabled( true );
        drawConsolationButton.setEnabled( false );
        drawReplenishButton.setEnabled( false );
        numField.setEnabled( false );
        provisionButton.setEnabled( true );

        stopDraw4thButton.setEnabled( false );
        stopDraw3rdButton.setEnabled( false );
        stopDraw2ndButton.setEnabled( false );
        stopDraw1stButton.setEnabled( false );
        stopDrawSpecialButton.setEnabled( false );
        stopDrawConsolationButton.setEnabled( false );
        stopDrawReplenishButton.setEnabled( false );

        JPanel buttonPanel = new JPanel( new MigLayout( "", "12[]12[]12[]12[]12[]12[]12[]30[]25[]12[]" ) );
        buttonPanel.add( resetButton, "span 1 2, growy" );
        buttonPanel.add( draw4thButton );
        buttonPanel.add( draw3rdButton );
        buttonPanel.add( draw2ndButton );
        buttonPanel.add( draw1stButton );
        buttonPanel.add( drawSpecialButton );
        buttonPanel.add( drawConsolationButton );
        buttonPanel.add( drawReplenishButton, "split 2" );
        buttonPanel.add( awardLevel, "growx" );
        buttonPanel.add( provisionButton, "span 1 2, growy, wrap" );
        buttonPanel.add( stopDraw4thButton, "growx" );
        buttonPanel.add( stopDraw3rdButton, "growx" );
        buttonPanel.add( stopDraw2ndButton, "growx" );
        buttonPanel.add( stopDraw1stButton, "growx" );
        buttonPanel.add( stopDrawSpecialButton, "growx" );
        buttonPanel.add( stopDrawConsolationButton, "growx" );
        buttonPanel.add( numField, "split 2, growy" );
        buttonPanel.add( stopDrawReplenishButton );

        contentPanel = new JPanel( new BorderLayout() );
        String description =
            "<html><font color=blue size=36><b>  抽奖规则</b><br><ul><li>四等奖抽2次，每次一个尾号，中奖率约20%</li><li>三等奖抽1次，一个尾号，中奖率约10%</li>"
                + "<li>二等奖150名，抽15次，每次10名</li><li>一等奖20名，抽4次，每次5名</li>"
                + "<li>特等奖5名，抽5次，每次1名</li><li>安慰奖65名，抽13次，每次5名</li><li>未出席者的获奖名额由补抽代替</li></ul></font></html>";
        JLabel label = new JLabel( description );
        contentPanel.add( label );

        contentLabel = new JLabel();
        contentLabel.setForeground( Color.BLUE );
        contentLabel.setFont( new Font( "Dialog", 1, 250 ) );

        setExtendedState( JFrame.MAXIMIZED_BOTH );
        setLayout( new BorderLayout() );
        add( buttonPanel, BorderLayout.NORTH );
        add( new JScrollPane( contentPanel ), BorderLayout.CENTER );
        setTitle( "NSN 2013新年团拜会" );
        setMinimumSize( new Dimension( 800, 600 ) );
        //改为点X不做任何操作，避免中途误关程序
//        setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
    }

    public static void main( String[] args )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                new LuckyDrawUI().setVisible( true );
            }
        } );
    }

    public void actionPerformed( ActionEvent event )
    {
        //四等奖抽2次
        if( event.getSource() == draw4thButton )
        {
            refreshContentPanel();
            timer = new Timer( LABEL_REFRESH_PERIOD, new ActionListener()
            {
                public void actionPerformed( ActionEvent event )
                {
                    Random random = new Random();
                    String showString = null;
                    if( draw4thButton.getText().equals( "抽取四等奖(0/2)" ) )
                    {
                        random1 = random.nextInt( 10 );
                        showString = "尾号: " + random1;
                    }
                    else if( draw4thButton.getText().equals( "抽取四等奖(1/2)" ) )
                    {
                        random2 = random.nextInt( 10 );
                        while( random2 == random1 )
                        {
                            random2 = random.nextInt( 10 );
                        }
                        showString = "尾号: " + random2;
                    }
                    contentLabel.setText( showString );
                }
            } );
            timer.start();

            draw4thButton.setEnabled( false );
            stopDraw4thButton.setEnabled( true );
            getRootPane().setDefaultButton( stopDraw4thButton );
        }
        else if( event.getSource() == stopDraw4thButton )
        {
            timer.stop();
            stopDraw4thButton.setEnabled( false );
            if( draw4thButton.getText().equals( "抽取四等奖(0/2)" ) )
            {
                draw4thButton.setText( "抽取四等奖(1/2)" );
                draw4thButton.setEnabled( true );
            }
            else if( draw4thButton.getText().equals( "抽取四等奖(1/2)" ) )
            {
                draw4thButton.setText( "抽取四等奖(2/2)" );
                draw3rdButton.setEnabled( true );
            }
        }
        //三等奖抽取一次
        else if( event.getSource() == draw3rdButton )
        {
            refreshContentPanel();
            timer = new Timer( LABEL_REFRESH_PERIOD, new ActionListener()
            {
                public void actionPerformed( ActionEvent event )
                {
                    Random random = new Random();
                    random3 = random.nextInt( 10 );
                    while( random3 == random1 || random3 == random2 )
                    {
                        random3 = random.nextInt( 10 );
                    }
                    String showString = "尾号: " + random3;
                    contentLabel.setText( showString );
                }
            } );
            timer.start();
            draw3rdButton.setEnabled( false );
            stopDraw3rdButton.setEnabled( true );
            getRootPane().setDefaultButton( stopDraw3rdButton );
        }
        else if( event.getSource() == stopDraw3rdButton )
        {
            timer.stop();
            stopDraw3rdButton.setEnabled( false );
            draw2ndButton.setEnabled( true );
        }
        //二等奖抽15次,每次10名
        else if( event.getSource() == draw2ndButton )
        {
            luckyDrawAction.saveClass4( random1, random2 );
            luckyDrawAction.saveClass3( random3 );

            luckyDrawAction.cacheData();
            timer = new Timer( TABLE_REFRESH_PERIOD, new ActionListener()
            {
                public void actionPerformed( ActionEvent event )
                {
                    results = luckyDrawAction.randomSelectFromEmployees( 10 );
                    Object[][] data = buildTableDataFor2ndLevel( 10, 1 );
                    String[] columnNames = { "NSN ID", "Name" };
                    JTable table = new JTable( data, columnNames );
                    paintTableToUI( table );
                }
            } );
            timer.start();

            draw2ndButton.setEnabled( false );
            stopDraw2ndButton.setEnabled( true );
            drawReplenishButton.setEnabled( false );
            numField.setEnabled( false );
            provisionButton.setEnabled( true );
            getRootPane().setDefaultButton( stopDraw2ndButton );
        }
        else if( event.getSource() == stopDraw2ndButton )
        {
            timer.stop();
            int num = getNumber( draw2ndButton.getText() );
            num++;
            if( num < 15 )
            {
                draw2ndButton.setEnabled( true );
            }
            else
            {
                draw1stButton.setEnabled( true );
            }
            draw2ndButton.setText( "抽取二等奖(" + num + "/15)" );
            stopDraw2ndButton.setEnabled( false );
            drawReplenishButton.setEnabled( true );
            numField.setEnabled( true );
            luckyDrawAction.saveClass2( results.keySet() );
        }
        //一等奖抽4次,每次5名
        else if( event.getSource() == draw1stButton )
        {
            luckyDrawAction.cacheData();
            timer = new Timer( TABLE_REFRESH_PERIOD, new ActionListener()
            {
                public void actionPerformed( ActionEvent event )
                {
                    results = luckyDrawAction.randomSelectFromEmployees( 5 );
                    Object[][] data = buildTableDataFor1stLevel();
                    String[] columnNames = { "NSN ID", "Name", "NSN ID", "Name" };
                    JTable table = new JTable( data, columnNames );
                    paintTableToUI( table );
                }
            } );
            timer.start();

            draw1stButton.setEnabled( false );
            stopDraw1stButton.setEnabled( true );
            drawReplenishButton.setEnabled( false );
            numField.setEnabled( false );
            getRootPane().setDefaultButton( stopDraw1stButton );
        }
        else if( event.getSource() == stopDraw1stButton )
        {
            timer.stop();
            int num = getNumber( draw1stButton.getText() );
            num++;
            if( num < 4 )
            {
                draw1stButton.setEnabled( true );
            }
            else
            {
                drawSpecialButton.setEnabled( true );
            }
            draw1stButton.setText( "抽取一等奖(" + num + "/4)" );
            stopDraw1stButton.setEnabled( false );
            drawReplenishButton.setEnabled( true );
            numField.setEnabled( true );
            nsnIdCache.addAll( results.keySet() );
            for( Integer nsnid : results.keySet() )
            {
                firstLevelCache.put( nsnid, results.get( nsnid ) );
            }
            luckyDrawAction.saveClass1( results.keySet() );
        }
        //特等奖抽5次,每次1名
        else if( event.getSource() == drawSpecialButton )
        {
            refreshContentPanel();
            luckyDrawAction.cacheData();
            timer = new Timer( LABEL_REFRESH_PERIOD, new ActionListener()
            {
                public void actionPerformed( ActionEvent event )
                {
                    results = luckyDrawAction.randomSelectFromEmployees( 1 );
                    for( Integer nsnid : results.keySet() )
                    {
                        contentLabel.setFont( new Font( "Dialog", 1, 170 ) );
                        contentLabel.setText( "<html>" + nsnid + "<br>" + results.get( nsnid ) + "</html>" );
                    }
                }
            } );
            timer.start();

            drawSpecialButton.setEnabled( false );
            stopDrawSpecialButton.setEnabled( true );
            drawReplenishButton.setEnabled( false );
            numField.setEnabled( false );
            getRootPane().setDefaultButton( stopDrawSpecialButton );
        }
        else if( event.getSource() == stopDrawSpecialButton )
        {
            timer.stop();
            int num = getNumber( drawSpecialButton.getText() );
            num++;
            if( num < 5 )
            {
                drawSpecialButton.setEnabled( true );
            }
            else
            {
                drawConsolationButton.setEnabled( true );
            }
            drawSpecialButton.setText( "抽取特等奖(" + num + "/5)" );
            stopDrawSpecialButton.setEnabled( false );
            drawReplenishButton.setEnabled( true );
            numField.setEnabled( true );
            luckyDrawAction.saveSpecialAward( results.keySet() );
        }
        //安慰奖,抽取15次,每次5名
        else if( event.getSource() == drawConsolationButton )
        {
            luckyDrawAction.cacheData();
            timer = new Timer( TABLE_REFRESH_PERIOD, new ActionListener()
            {
                public void actionPerformed( ActionEvent event )
                {
                    results = luckyDrawAction.randomSelectFromEmployees( 5 );
                    String[] columnNames = { "NSN ID" };
                    Object[][] data = buildTableDataFor2ndLevel( 5, 1 );
                    JTable table = new JTable( data, columnNames );
                    paintTableToUI( table );
                }
            } );
            timer.start();

            drawConsolationButton.setEnabled( false );
            stopDrawConsolationButton.setEnabled( true );
            getRootPane().setDefaultButton( stopDrawConsolationButton );
        }
        else if( event.getSource() == stopDrawConsolationButton )
        {
            timer.stop();
            int num = getNumber( drawConsolationButton.getText() );
            num++;
            if( num < 13 )
            {
                drawConsolationButton.setEnabled( true );
            }
            drawConsolationButton.setText( "抽取安慰奖(" + num + "/13)" );
            stopDrawConsolationButton.setEnabled( false );
            drawReplenishButton.setEnabled( true );
            numField.setEnabled( true );
            luckyDrawAction.saveConsolationPrize( results.keySet() );
        }
        //补抽若干名
        else if( event.getSource() == drawReplenishButton )
        {
            try
            {
                final int x = Integer.valueOf( numField.getText().trim() );
                if( 10 < x || x < 1 )
                {
                    throw new NumberFormatException( "number should be bigger than 1 and smaller than 10" );
                }
                //number is OK, start timer
                luckyDrawAction.cacheData();
                timer = new Timer( TABLE_REFRESH_PERIOD, new ActionListener()
                {
                    public void actionPerformed( ActionEvent event )
                    {
                        results = luckyDrawAction.randomSelectFromEmployees( x );
                        String[] columnNames = { "NSN ID" };
                        Object[][] data = buildTableDataFor2ndLevel( x, 1 );
                        JTable table = new JTable( data, columnNames );
                        paintTableToUI( table );
                    }
                } );
                timer.start();
            }
            catch( NumberFormatException e )
            {
                return;
            }

            drawReplenishButton.setEnabled( false );
            numField.setEnabled( false );
            stopDrawReplenishButton.setEnabled( true );
            getRootPane().setDefaultButton( stopDrawReplenishButton );
        }
        else if( event.getSource() == stopDrawReplenishButton )
        {
            timer.stop();
            numField.setText( "" );
            drawReplenishButton.setEnabled( true );
            numField.setEnabled( true );
            stopDrawReplenishButton.setEnabled( false );
            luckyDrawAction.saveReplenishPrize( getReplenishLevel(), results.keySet() );
        }
        //初始化数据
        else if( event.getSource() == resetButton )
        {
            luckyDrawAction.reset();
            JLabel myLabel = new JLabel( " Good Luck!" );
            myLabel.setForeground( Color.BLUE );
            myLabel.setFont( new Font( "Dialog", 1, 150 ) );
            contentPanel.removeAll();
            contentPanel.add( myLabel, BorderLayout.CENTER );
            contentPanel.repaint();
            contentPanel.revalidate();

            draw4thButton.setEnabled( true );
            resetButton.setEnabled( false );
        }
        //创建excel文件
        else if( event.getSource() == provisionButton )
        {
            luckyDrawAction.generateExcel();
        }
    }

    private int getReplenishLevel()
    {
        String selectedItem = ( String ) awardLevel.getSelectedItem();
        if( "二等奖".equals( selectedItem ) )
        {
            return 2;
        }
        else if( "一等奖".equals( selectedItem ) )
        {
            return 1;
        }
        else if( "特等奖".equals( selectedItem ) )
        {
            return 0;
        }
        else if( "安慰奖".equals( selectedItem ) )
        {
            return 7;
        }
        throw new RuntimeException();
    }

    private void refreshContentPanel()
    {
        contentPanel.removeAll();
        contentPanel.add( contentLabel, BorderLayout.CENTER );
        contentPanel.repaint();
        contentPanel.revalidate();
    }

    private Object[][] buildTableDataFor2ndLevel( int rowCount, int columnCount )
    {
        Object[][] data = new Object[ rowCount ][ 2 ];
        int i = 0;
        for( Integer nsnid : results.keySet() )
        {
            data[i][0] = nsnid;
            data[i][1] = results.get( nsnid );
            i++;
        }
        return data;
    }

    private Object[][] buildTableDataFor1stLevel()
    {
        Object[][] data = new Object[ 10 ][ 4 ];
        List<Integer> nsnids = new ArrayList<Integer>();
        nsnids.addAll( nsnIdCache );
        nsnids.addAll( results.keySet() );
        int k = 0;
        for( int j : new int[]{ 0, 2 } )
        {
            for( int i = 0; i < 10; i++ )
            {
                if( k < firstLevelCache.size() )
                {
                    data[i][j] = nsnids.get( k++ );
                    data[i][j + 1] = firstLevelCache.get( data[i][j] );
                }
                else if( firstLevelCache.size() <= k && k < firstLevelCache.size() + results.size() )
                {
                    data[i][j] = nsnids.get( k++ );
                    data[i][j + 1] = results.get( data[i][j] );
                }
                else
                {
                    data[i][j] = "";
                    data[i][j + 1] = "";
                }
            }
        }
        return data;
    }

    private void paintTableToUI( JTable table )
    {
        table.setRowHeight( 60 );
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment( JLabel.CENTER );
        render.setForeground( Color.BLUE );
        table.setDefaultRenderer( Object.class, render );
        table.getTableHeader().setFont( new Font( "Dialog", Font.BOLD, 40 ) );
        table.setFont( new Font( "Dialog", Font.BOLD, 50 ) );
        table.enable( false );
        contentPanel.removeAll();
        contentPanel.add( table.getTableHeader(), BorderLayout.NORTH );
        contentPanel.add( table, BorderLayout.CENTER );
        contentPanel.repaint();
        contentPanel.revalidate();
    }

    private int getNumber( String string )
    {
        int index1 = string.indexOf( "(" );
        int index2 = string.indexOf( "/" );
        String numString = string.substring( index1 + 1, index2 );
        return Integer.valueOf( numString );
    }
}
