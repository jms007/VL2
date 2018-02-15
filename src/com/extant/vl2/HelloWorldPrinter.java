package com.extant.vl2; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.print.*;
import java.util.Hashtable;
 
public class HelloWorldPrinter
    implements Printable
{
    /***** Declarations Added *****/
    int lineSpace = 12;
    int y;
    Hashtable <String, Point> points = new Hashtable<String, Point>();
    JFrame jFrame = new JFrame();
    /*****/
 
    public void init()
    {
        jFrame.setSize(800, 600);
        jFrame.setLocation( 200,200);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.addWindowListener(new WindowAdapter()
        {
           public void windowClosing(WindowEvent e) {return;}
        });
    }

    public int print(Graphics g, PageFormat pf, int page)
        throws PrinterException
    {
        
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
 
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
 
        /* Now we perform our rendering */
        //g.drawString("Hello world!", 100, 100);
        /***** Inserted Code *****/
        initPoints();

        // Build the stub
        g.setFont( new Font( "Monospaced", Font.BOLD, 10 ) ); // 4-10-04
        y = coordFor("ST").y;

//        for (int i=0; i<4; ++i)
//        {
//            String image = txtStubs.get(i).getText();
//            if (!image.equals(""))
//                g.drawString( image, coordFor("ST").x, coordFor("ST").y );
//            y += lineSpace;
//        }
//        y = coordFor("ST").y;
//        for (int i=0; i<4; ++i)
//        {
//            g.drawString( Strings.colFormat( txtStubamts.get(i).getText(), 13 ), 475, y );
//            y += lineSpace;
//        }

        // Build the check, first the BOLD items
        g.setFont( new Font( "Arial", Font.BOLD, 12 ) );
        // Check Number
        g.drawString( "1234",  //txtCheckNo.getText(), 
            coordFor("CN").x, coordFor("CN").y );
        // Amount
        g.drawString( "123.45",   //txtAmount.getText(),
            coordFor("DA").x, coordFor("DA").y );
        // Switch to PLAIN font
        g.setFont( new Font( "Arial", Font.PLAIN, 10 ) );
        // Amount in words
        g.drawString( "Amount in Words",   //lblAmtWords.getText(),
            coordFor("DW").x, coordFor( "DW" ).y );
        // Date
        g.drawString( "10-11-17",   //txtDate.getText(),
            coordFor("DT").x, coordFor("DT").y );
        // Payee Data
        y = coordFor("PA").y;
        for (int i=0; i<4; ++i)
        {
            g.drawString( "Frida",   //payeeData[i],
                coordFor("PA").x, y );
            y += lineSpace;
        }
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
    
    public Point coordFor( String key )
    {
        double xReg = 0.25 * 72;
        double yReg = 0.1 * 72;
        Point p1;
        Point p2;
 
        p1 = points.get(key);
        int x2 = (int)((p1.x * 72.0) / 100.0 + xReg);
        int y2 = (int)((p1.y * 72.0) / 100.0 + yReg);
        p2 = new Point( x2, y2 );
        return p2;
    }

    public void initPoints()
    {   // Note these numbers are *100 so we can use integers
        // (I cannot create a Hashtable with Point2D.Double values)
        points.put("ST", new Point( 80,  80));  // (0) ST Stub Detail
        points.put("SA", new Point(715, 725));  // (1) SA Stub Amounts
        points.put("CN", new Point(715, 420));  // (2) Check Number
        points.put("DW", new Point( 25, 480));  // (3) Amount in words
        points.put("DT", new Point(525, 520));  // (4) Date
        points.put("DA", new Point(705, 520));  // (5) Amount in digits
        points.put("PA", new Point( 85, 550));  // (6) Payee Block
    }
    
    public void doit()
    {
         PrinterJob job = PrinterJob.getPrinterJob();
         job.setPrintable(this);
         boolean ok = job.printDialog();
         if (ok) {
             try {
                  job.print();
             } catch (PrinterException ex) {
              /* The job did not successfully complete */
             }
         }
    }

    public static void main(String args[])
    {
        HelloWorldPrinter helloWorldPrinter = new HelloWorldPrinter();
//        UIManager.put("swing.boldMetal", Boolean.FALSE);
//        JFrame f = new JFrame("Hello World Printer");
//        f.addWindowListener(new WindowAdapter() {
//           public void windowClosing(WindowEvent e) {System.exit(0);}
//        });
//        JButton printButton = new JButton("Print Hello World");
//        //printButton.addActionListener(new HelloWorldPrinter());
        helloWorldPrinter.doit();
//        f.add("Center", printButton);
//        f.pack();
//        f.setVisible(true);
    }
}
