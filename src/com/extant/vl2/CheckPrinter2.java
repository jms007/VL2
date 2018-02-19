package com.extant.vl2;

import com.extant.utilities.XProperties;
import com.extant.utilities.Console;
import com.extant.utilities.Strings;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.Point;
import java.util.Hashtable;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.print.*;
import java.util.regex.Pattern;
//import static vl2.VL2.props;

/**
 *
 * @author jms
 */
public class CheckPrinter2 extends JFrame implements Printable {
	// @Override
	// public void pack() {
	// super.pack(); //To change body of generated methods, choose Tools |
	// Templates.
	// }

	// @Override
	// public void setLocation(Point point) {
	// super.setLocation(point); //To change body of generated methods, choose Tools
	// | Templates.
	// }

	int lineSpace = 12;
	int y;
	Hashtable<String, Point> points = new Hashtable<String, Point>();

	public CheckPrinter2(VL2Config vl2Config)
	{
		initComponents();
		// Frame size and location are set in VL2
		// this.setSize(800,600);
		// this.setLocation(600,200);
		// this.pack();
		// this.setVisible(true);
		txtDate.setInputVerifier(new DateVerifier());
		txtAmount.setInputVerifier(new AmountVerifier());
		btnApprove.setEnabled(false);
		resetForm();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.vl2FileMan = vl2Config;
	}

	public void resetForm()
	{
		stub0.setText("");
		stub1.setText("");
		stub2.setText("");
		stubamt0.setText("");
		stubamt1.setText("");
		stubamt2.setText("");
		txtDate.setText("");
		txtCheckNo.setText("");
		txtAmount.setText("");
		lblAmtWords.setText("");
		txtPayee0.setText("");
		txtPayee1.setText("");
		txtPayee2.setText("");
		txtPayee3.setText("");
	}

	public int print(Graphics g, PageFormat pf, int page) throws PrinterException
	{
		if (page != 0)
			return NO_SUCH_PAGE;
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		buildGraphics(g);
		return PAGE_EXISTS;
	}

	public void buildGraphics(Graphics g)
	{
		int lineSpace = 12;
		int y;
		int x;
		String s;
		initPoints();

		// Build the stub
		g.setFont(new Font("Monospaced", Font.BOLD, 10)); // 4-10-04
		x = coordFor("ST").x;
		y = coordFor("ST").y;
		// Stub text
		s = stub0.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);
		y += lineSpace;

		s = stub1.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);
		y += lineSpace;

		s = stub2.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);
		y += lineSpace;

		s = stub3.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);

		// Stub amounts
		x = 475;
		y = coordFor("ST").y;
		s = stubamt0.getText();
		if (!s.isEmpty())
			g.drawString(Strings.colFormat(s, 13), x, y);
		y += lineSpace;

		s = stubamt1.getText();
		if (!s.isEmpty())
			g.drawString(Strings.colFormat(s, 13), x, y);
		y += lineSpace;

		s = stubamt2.getText();
		if (!s.isEmpty())
			g.drawString(Strings.colFormat(s, 13), x, y);
		y += lineSpace;

		s = stubamt3.getText();
		if (!s.isEmpty())
			g.drawString(Strings.colFormat(s, 13), x, y);

		// Build the check, first the BOLD items
		g.setFont(new Font("Arial", Font.BOLD, 12));
		// Check Number
		g.drawString(txtCheckNo.getText(), coordFor("CN").x, coordFor("CN").y);
		// Amount
		g.drawString(txtAmount.getText(), coordFor("DA").x, coordFor("DA").y);
		// Switch to PLAIN font
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		// Amount in words
		g.drawString(lblAmtWords.getText(), coordFor("DW").x, coordFor("DW").y);
		// Date
		g.drawString(txtDate.getText(), coordFor("DT").x, coordFor("DT").y);
		// Payee Data
		x = coordFor("PA").x;
		y = coordFor("PA").y;

		s = txtPayee0.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);
		y += lineSpace;

		s = txtPayee1.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);
		y += lineSpace;

		s = txtPayee2.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);
		y += lineSpace;

		s = txtPayee3.getText();
		if (!s.isEmpty())
			g.drawString(s, x, y);
	}

	public Point coordFor(String key)
	{
		double xReg = 0.25 * 72;
		double yReg = 0.1 * 72;
		Point p1;
		Point p2;

		p1 = points.get(key);
		int x2 = (int) ((p1.x * 72.0) / 100.0 + xReg);
		int y2 = (int) ((p1.y * 72.0) / 100.0 + yReg);
		p2 = new Point(x2, y2);
		return p2;
	}

	class AmountVerifier extends InputVerifier {
		String amountPattern = "\\d*\\.\\d\\d";

		public boolean verify(JComponent jc)
		{
			JTextField tf = (JTextField) jc;
			return Pattern.matches(amountPattern, tf.getText());
		}
	}

	class DateVerifier extends InputVerifier {
		public boolean verify(JComponent jc)
		{
			JTextField tf = (JTextField) jc;
			// Use regex
			return Pattern.matches(datePattern, tf.getText());

			// or use Julian
			// transDate = new Julian(tf.getText());
			// return transDate.isValid();
		}

		String datePattern = "\\d{1,2}[-/]\\d{1,2}[-/]\\d\\d\\d\\d";
	}

	// The following points are (x, y) coordinates for the left-most
	// character to be printed, except for Stub Amounts for which the x
	// value is the RIGHT-most digit. X values are measured from the left
	// edge of the paper to the right, and Y values are measured from the
	// top of the paper down. Where the data is more than one line, the
	// coordinates are for the top line, and subsequent lines
	// are lineSpace (72) points below (higher values of y).

	public void initPoints()
	{ // Note these numbers are *100 so we can use integers
		// (I was not able to create a Hashtable with Point2D.Double values.)
		points.put("ST", new Point(80, 80)); // (0) ST Stub Detail
		points.put("SA", new Point(715, 725)); // (1) SA Stub Amounts (right-justified)
		points.put("CN", new Point(700, 365)); // (2) Check Number
		points.put("DW", new Point(25, 490)); // (3) Amount in words
		points.put("DT", new Point(489, 445)); // (4) Date
		points.put("DA", new Point(640, 445)); // (5) Amount in digits
		points.put("PA", new Point(85, 550)); // (6) Payee Block
	}

	// public void setStubs()
	// {
	// // No need initialize the stub fields, they are all initially blank
	// ArrayList <JTextField> txtStubs = new ArrayList <JTextField>();
	// txtStubs.add(stub0);
	// txtStubs.add(stub1);
	// txtStubs.add(stub2);
	// txtStubs.add(stub3);
	//
	// ArrayList <JTextField> txtStubamts = new ArrayList <JTextField>();
	// txtStubamts.add(stubamt0);
	// txtStubamts.add(stubamt1);
	// txtStubamts.add(stubamt2);
	// txtStubamts.add(stubamt3);
	// }
	//
	// public void setDate(String date)
	// {
	// this.date = date;
	// txtDate.setText(date);
	// }
	//
	// public void setCheckNo( String checkNo )
	// {
	// this.checkNo = checkNo;
	// txtCheckNo.setText(checkNo);
	// }
	//
	// public void setAmount( long amount )
	// {
	// txtAmount.setText(Strings.formatPennies(amount));
	// lblAmtWords.setText(Strings.pennies2Text(amount));
	// }
	//
	public void setPayeeData(String[] payeeData)
	{
		txtPayee0.setText(payeeData[0]);
		txtPayee1.setText(payeeData[1]);
		txtPayee2.setText(payeeData[2]);
		txtPayee3.setText(payeeData[3]);
	}

	private int finalCheck()
	{
		String s;
		int error = 0;
		// The commented tests are now handled by the InputVerifiers
		// s = txtCheckNo.getText();
		// if (!Strings.isValidInt(s)) error = 1; // Check No
		// s = txtAmount.getText();
		// while (s.startsWith("*")) s = s.substring(1); // Amount
		// if (!Strings.regexMatch("\\d+\\.\\d\\d", s)) error += 2;
		// txtAmount.setText("***" + s); // Restore the original text
		// if (!new Julian(s).isValid()) error += 4; // Date
		// If the first line of payee is empty, error 8
		if (txtPayee0.getText().isEmpty())
			error += 8;
		btnApprove.setEnabled(error == 0);
		// Console.println("finalCheck error="+error);
		return error;
	}

	// public void (String[] payeeData)
	// {
	// for (int i=0; i<4; ++i)
	// {
	// txtPayee0.setText(payeeData[0]);
	// txtPayee1.setText(payeeData[1]);
	// txtPayee2.setText(payeeData[2]);
	// txtPayee3.setText(payeeData[3]);
	// }

	// public static void main(String args[])
	// {
	// CheckPrinter2 checkPrinter2 = new CheckPrinter2();
	// checkPrinter2.setVisible(true);
	// }
	//
	// private String abort(int error)
	// {
	// String report = "";
	// if ((error & 1) == 1) report += "Check No. is not valid. ";
	// if ((error & 2) == 2) report += "Amount is not valid. ";
	// if ((error & 4) == 4) report += "Date is not valid. ";
	// if ((error & 8) == 8) report += "Payee Data is all blank.";
	// if (error != 0)
	// {
	// Console.println(report);
	// this.dispose();
	// }
	// return report;
	// }

	VL2Config vl2FileMan;
	String[] payeeData = new String[4];
	// String[] stubs;
	// ArrayList <JTextField> txtStubs = new ArrayList <JTextField>();
	// String[] stubAmts;
	// ArrayList <JTextField> txtStubamts = new ArrayList <JTextField>();
	// String[] payeeData;
	// ArrayList <JTextField> txtPayeeData = new ArrayList <JTextField>();
	String date;
	String checkNo;

	// The following code is copied from the Oracle printing tutorial
	// https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/2d/printing/examples/HelloWorldPrinter.java
	// import java.awt.*;
	// import java.awt.event.*;
	// import javax.swing.*;
	// import java.awt.print.*;
	//
	// public class HelloWorldPrinter implements Printable, ActionListener {
	//
	//
	// public int print(Graphics g, PageFormat pf, int page) throws
	// PrinterException {
	//
	// if (page > 0) { /* We have only one page, and 'page' is zero-based */
	// return NO_SUCH_PAGE;
	// }
	//
	// /* User (0,0) is typically outside the imageable area, so we must
	// * translate by the X and Y values in the PageFormat to avoid clipping
	// */
	// Graphics2D g2d = (Graphics2D)g;
	// g2d.translate(pf.getImageableX(), pf.getImageableY());
	//
	// /* Now we perform our rendering */
	// g.drawString("Hello world!", 100, 100);
	//
	// /* tell the caller that this page is part of the printed document */
	// return PAGE_EXISTS;
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	// PrinterJob job = PrinterJob.getPrinterJob();
	// job.setPrintable(this);
	// boolean ok = job.printDialog();
	// if (ok) {
	// try {
	// job.print();
	// } catch (PrinterException ex) {
	// /* The job did not successfully complete */
	// }
	// }
	// }

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		jPanel2 = new javax.swing.JPanel();
		panel1 = new java.awt.Panel();
		lblOwnerName = new java.awt.Label();
		lblAccount = new java.awt.Label();
		lblAmtWords = new javax.swing.JLabel();
		txtDate = new javax.swing.JTextField();
		txtCheckNo = new javax.swing.JTextField();
		txtAmount = new javax.swing.JTextField();
		txtPayee0 = new javax.swing.JTextField();
		txtPayee1 = new javax.swing.JTextField();
		txtPayee2 = new javax.swing.JTextField();
		txtPayee3 = new javax.swing.JTextField();
		btnApprove = new javax.swing.JButton();
		btnSelFromVndrLst = new javax.swing.JButton();
		stub0 = new javax.swing.JTextField();
		stub1 = new javax.swing.JTextField();
		stub2 = new javax.swing.JTextField();
		stub3 = new javax.swing.JTextField();
		stubamt0 = new javax.swing.JTextField();
		stubamt1 = new javax.swing.JTextField();
		stubamt2 = new javax.swing.JTextField();
		stubamt3 = new javax.swing.JTextField();

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 100, Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 100, Short.MAX_VALUE));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		panel1.setBackground(new java.awt.Color(0, 204, 255));

		lblOwnerName.setAlignment(java.awt.Label.CENTER);
		lblOwnerName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
		lblOwnerName.setText("JAN M. SMITH");

		lblAccount.setAlignment(java.awt.Label.CENTER);
		lblAccount.setText("Personal Account");

		lblAmtWords.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

		txtDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));
		txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtDateFocusLost(evt);
			}
		});

		txtCheckNo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
		txtCheckNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Check No"));
		txtCheckNo.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtCheckNoFocusLost(evt);
			}
		});

		txtAmount.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
		txtAmount.setBorder(javax.swing.BorderFactory.createTitledBorder("Amount"));
		txtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtAmountFocusLost(evt);
			}
		});
		txtAmount.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				txtAmountActionPerformed(evt);
			}
		});

		txtPayee0.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtPayee0FocusLost(evt);
			}
		});

		txtPayee1.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtPayee1FocusLost(evt);
			}
		});

		txtPayee2.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtPayee2FocusLost(evt);
			}
		});

		txtPayee3.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtPayee3FocusLost(evt);
			}
		});

		btnApprove.setBackground(new java.awt.Color(0, 240, 0));
		btnApprove.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
		btnApprove.setText("APPROVE");
		btnApprove.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnApproveActionPerformed(evt);
			}
		});

		btnSelFromVndrLst.setText("Select Payee from Vendor List");
		btnSelFromVndrLst.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnSelFromVndrLstActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
		panel1.setLayout(panel1Layout);
		panel1Layout.setHorizontalGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
						.addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addGroup(panel1Layout.createSequentialGroup().addGap(65, 65, 65).addGroup(panel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(panel1Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
												.addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 115,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(panel1Layout.createSequentialGroup().addGroup(panel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(btnSelFromVndrLst,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(txtPayee3, javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
												.addComponent(txtPayee2, javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(txtPayee1, javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(txtPayee0, javax.swing.GroupLayout.Alignment.LEADING))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(btnApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 112,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(48, 48, 48))))
								.addGroup(panel1Layout.createSequentialGroup().addContainerGap()
										.addGroup(panel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(lblOwnerName, javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(lblAccount, javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.PREFERRED_SIZE, 410,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(50, 50, 50)
										.addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 104,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(txtCheckNo, javax.swing.GroupLayout.PREFERRED_SIZE, 82,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(panel1Layout.createSequentialGroup().addContainerGap(25, Short.MAX_VALUE)
										.addComponent(lblAmtWords, javax.swing.GroupLayout.PREFERRED_SIZE, 722,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(95, 95, 95)));
		panel1Layout.setVerticalGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel1Layout.createSequentialGroup().addGap(2, 2, 2)
						.addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
								.addComponent(lblOwnerName, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtDate).addComponent(txtCheckNo))
						.addGap(1, 1, 1)
						.addComponent(lblAccount, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblAmtWords, javax.swing.GroupLayout.PREFERRED_SIZE, 22,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(panel1Layout.createSequentialGroup().addGap(115, 115, 115).addComponent(
										btnApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnSelFromVndrLst)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtPayee0, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtPayee1, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtPayee2, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtPayee3, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(1384, 1384, 1384)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(stub0, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
										.addComponent(stub1).addComponent(stub2).addComponent(stub3))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(stubamt0).addComponent(stubamt1).addComponent(stubamt2)
										.addComponent(stubamt3, javax.swing.GroupLayout.DEFAULT_SIZE, 86,
												Short.MAX_VALUE)))
						.addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stub0, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stubamt0, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stub1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stubamt1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stub2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stubamt2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(stub3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(stubamt3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(panel1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(103, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void txtAmountActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_txtAmountActionPerformed
		String amtWords = Strings.pennies2Text(Long.parseLong(txtAmount.getText()));
		lblAmtWords.setText(amtWords);
	}// GEN-LAST:event_txtAmountActionPerformed

	private void btnApproveActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnApproveActionPerformed
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(this);
		// Check for required fields
		if (finalCheck() != 0) { // Unreachable because Approve is not enabled with errors
			this.dispose();
		}
		boolean ok = job.printDialog();
		if (ok) {
			try {
				job.print();
			} catch (PrinterException ex) { /* The print job did not successfully complete */
				Console.println("Print job failed: " + ex.getMessage());
				// this.dispose();
			}
			resetForm();
		}
	}// GEN-LAST:event_btnApproveActionPerformed

	private void txtAmountFocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtAmountFocusLost
		String amt = txtAmount.getText();
		while (amt.startsWith("$") || amt.startsWith("*"))
			amt = amt.substring(1);
		long pennies = Strings.parsePennies(txtAmount.getText());
		lblAmtWords.setText(Strings.pennies2Text(pennies));
		txtAmount.setText("$***" + amt);
		finalCheck();
	}// GEN-LAST:event_txtAmountFocusLost

	private void txtDateFocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtDateFocusLost
		finalCheck();
	}// GEN-LAST:event_txtDateFocusLost

	private void txtCheckNoFocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtCheckNoFocusLost
		finalCheck();
	}// GEN-LAST:event_txtCheckNoFocusLost

	private void txtPayee0FocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtPayee0FocusLost
		finalCheck();
	}// GEN-LAST:event_txtPayee0FocusLost

	private void txtPayee1FocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtPayee1FocusLost
		finalCheck();
	}// GEN-LAST:event_txtPayee1FocusLost

	private void txtPayee2FocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtPayee2FocusLost
		finalCheck();
	}// GEN-LAST:event_txtPayee2FocusLost

	private void txtPayee3FocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtPayee3FocusLost
		finalCheck();
	}// GEN-LAST:event_txtPayee3FocusLost

	private void btnSelFromVndrLstActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnSelFromVndrLstActionPerformed
		// //ListMan4 listMan4 = new ListMan4(props,
		// props.getString("WorkDir")+"VENDOR.LST", true);
		// String workDir = props.getString("WorkDir");
		// Container contentPane;
		// contentPane = this.getContentPane();
		// contentPane.setPreferredSize(new Dimension(500, 450));
		// contentPane = new ListMan4(props, workDir+"CUSTOMER.LST", true);
		// //VL2MenuFrame.pack();
		// contentPane.setVisible(true);
		// //VL2MenuFrame.contentPane.pack();
		// //VL2MenuFrame.contentPane.setVisible(true);
		// String listFile = props.getString("WorkDir") + "VENDOR.LST";
		// SelectFromList selFromList = new SelectFromList(this, true, listFile);
	}// GEN-LAST:event_btnSelFromVndrLstActionPerformed

	// </editor-fold>
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnApprove;
	private javax.swing.JButton btnSelFromVndrLst;
	private javax.swing.JPanel jPanel2;
	private java.awt.Label lblAccount;
	private javax.swing.JLabel lblAmtWords;
	private java.awt.Label lblOwnerName;
	private java.awt.Panel panel1;
	private javax.swing.JTextField stub0;
	private javax.swing.JTextField stub1;
	private javax.swing.JTextField stub2;
	private javax.swing.JTextField stub3;
	private javax.swing.JTextField stubamt0;
	private javax.swing.JTextField stubamt1;
	private javax.swing.JTextField stubamt2;
	private javax.swing.JTextField stubamt3;
	private javax.swing.JTextField txtAmount;
	private javax.swing.JTextField txtCheckNo;
	private javax.swing.JTextField txtDate;
	private javax.swing.JTextField txtPayee0;
	private javax.swing.JTextField txtPayee1;
	private javax.swing.JTextField txtPayee2;
	private javax.swing.JTextField txtPayee3;
	// End of variables declaration//GEN-END:variables
}
