package com.extant.vl2;

/*
 * Analyze.java
 *
 * Created on February 25, 2003, 3:37 PM
 */
import com.extant.utilities.TextDialog;
import com.extant.utilities.Sorts;
import com.extant.utilities.Strings;
import com.extant.utilities.UsefulFile;
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import static com.extant.vl2.ChartTree.tree;
import javax.swing.JTextField;

/**
 *
 * @author jms
 */
@SuppressWarnings("serial")
public class Analyze extends JDialog implements TreeSelectionListener
{
	VL2Config vl2Config;

	public Analyze(JFrame parent, boolean modal, Chart chart, ChartTree tree, VL2Config vl2Config)
	{
		super(parent, modal);
		initComponents();
		this.parent = parent;
		this.vl2Config = vl2Config;
		setup();
	}

	@Override
	public void setVisible(boolean bln)
	{
		super.setVisible(bln); // To change body of generated methods, choose Tools | Templates.
	}

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		comboAccounts = new JTextField();
		jLabel1 = new javax.swing.JLabel();
		btnAnalyze = new javax.swing.JButton();
		btnAnalyzeCancel = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();
		statusBar = new javax.swing.JLabel();
		ckboxSubtotal = new javax.swing.JCheckBox();
		jPanel2 = new javax.swing.JPanel();
		rbAllTrans = new javax.swing.JRadioButton();
		rbDates = new javax.swing.JRadioButton();
		txtStartDate = new javax.swing.JTextField();
		txtEndDate = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		rbSortByDescr = new javax.swing.JRadioButton();
		rbSortByDate = new javax.swing.JRadioButton();
		rbSortByAmount = new javax.swing.JRadioButton();

		setTitle("Analyze Account");
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				closeDialog(evt);
			}
		});
		getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		comboAccounts.setFont(new java.awt.Font("Dialog", 0, 12));
		getContentPane().add(comboAccounts, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 310, -1));

		jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		jLabel1.setText("Select Account:");
		getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));

		btnAnalyze.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		btnAnalyze.setText("Analyze");
		btnAnalyze.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		btnAnalyze.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnAnalyzeActionPerformed(evt);
			}
		});
		getContentPane().add(btnAnalyze, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, -1, -1));

		btnAnalyzeCancel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		btnAnalyzeCancel.setText("Cancel");
		btnAnalyzeCancel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		btnAnalyzeCancel.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnAnalyzeCancelActionPerformed(evt);
			}
		});
		getContentPane().add(btnAnalyzeCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 240, -1, -1));

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jPanel1.setLayout(new java.awt.BorderLayout());

		statusBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jPanel1.add(statusBar, java.awt.BorderLayout.SOUTH);

		getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 270, 320, 30));

		ckboxSubtotal.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		ckboxSubtotal.setText("Show Subtotals");
		getContentPane().add(ckboxSubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, -1, -1));

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Period to Analyze",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Arial", 0, 10))); // NOI18N
		jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		rbAllTrans.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbAllTrans.setText("Include All Transactions");
		rbAllTrans.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				manageDateButtons(evt);
			}
		});
		jPanel2.add(rbAllTrans, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, 20));

		rbDates.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbDates.setText("Specify  Dates");
		rbDates.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				manageDateButtons(evt);
			}
		});
		jPanel2.add(rbDates, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));
		jPanel2.add(txtStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 80, -1));
		jPanel2.add(txtEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 80, -1));

		jLabel5.setText("-");
		jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 10, -1));

		getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 310, 70));

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Desired Order",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Arial", 0, 10))); // NOI18N
		jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		rbSortByDescr.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbSortByDescr.setText("Description");
		rbSortByDescr.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				manageSortButtons(evt);
			}
		});
		jPanel3.add(rbSortByDescr, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, 20));

		rbSortByDate.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbSortByDate.setText("Date");
		rbSortByDate.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				manageSortButtons(evt);
			}
		});
		jPanel3.add(rbSortByDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, 20));

		rbSortByAmount.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbSortByAmount.setText("Amount");
		rbSortByAmount.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				manageSortButtons(evt);
			}
		});
		jPanel3.add(rbSortByAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, -1, 20));

		getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, 310, 50));

		setSize(new java.awt.Dimension(422, 373));
		setLocationRelativeTo(null);
	}// </editor-fold>//GEN-END:initComponents

	private void manageSortButtons(java.awt.event.ActionEvent evt)// GEN-FIRST:event_manageSortButtons
	{// GEN-HEADEREND:event_manageSortButtons
		JRadioButton button = null;
		if (evt == null)
			button = rbSortByDate; // Default
		else if (evt.getActionCommand().startsWith("Description"))
			button = rbSortByDescr;
		else if (evt.getActionCommand().startsWith("Date"))
			button = rbSortByDate;
		else if (evt.getActionCommand().startsWith("Amount"))
			button = rbSortByAmount;
		rbSortByDescr.setSelected(button == rbSortByDescr);
		rbSortByDate.setSelected(button == rbSortByDate);
		rbSortByAmount.setSelected(button == rbSortByAmount);
		ckboxSubtotal.setEnabled(rbSortByDescr.isSelected());
	}// GEN-LAST:event_manageSortButtons

	private void manageDateButtons(java.awt.event.ActionEvent evt)// GEN-FIRST:event_manageDateButtons
	{// GEN-HEADEREND:event_manageDateButtons
		JRadioButton button = null;
		if (evt == null)
			button = rbAllTrans; // Default
		else if (evt.getActionCommand().startsWith("Include"))
			button = rbAllTrans;
		else if (evt.getActionCommand().startsWith("Specify"))
			button = rbDates;
		rbAllTrans.setSelected(button == rbAllTrans);
		rbDates.setSelected(button == rbDates);
		txtStartDate.setVisible(rbDates.isSelected());
		txtEndDate.setVisible(rbDates.isSelected());
		// jLabel2.setVisible( rbDates.isSelected() );
		// jLabel3.setVisible( rbDates.isSelected() );
		if (rbDates.isSelected())
			txtStartDate.requestFocus();
	}// GEN-LAST:event_manageDateButtons

	private void btnAnalyzeActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnAnalyzeActionPerformed
	{// GEN-HEADEREND:event_btnAnalyzeActionPerformed
		if (rbDates.isSelected())
			analyze((Account) chart.findAcctByNo(comboAccounts.getText()), new Julian(txtStartDate.getText()),
					new Julian(txtEndDate.getText()));
		else
			analyze((Account) chart.findAcctByNo(comboAccounts.getText()), null, null);
	}// GEN-LAST:event_btnAnalyzeActionPerformed

	private void btnAnalyzeCancelActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnAnalyzeCancelActionPerformed
	{// GEN-HEADEREND:event_btnAnalyzeCancelActionPerformed
		closeDialog(null);
	}// GEN-LAST:event_btnAnalyzeCancelActionPerformed

//	private void comboAccountsKeyTyped(java.awt.event.KeyEvent evt)// GEN-FIRST:event_comboAccountsKeyTyped
//	{// GEN-HEADEREND:event_comboAccountsKeyTyped
//		if (evt.getKeyChar() == java.awt.event.KeyEvent.VK_ENTER)
//			btnAnalyzeActionPerformed(null);
//		accountFinder.processKeyEvent(evt);
//	}// GEN-LAST:event_comboAccountsKeyTyped
//
	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)
	{// GEN-FIRST:event_closeDialog
		dispose();
	}// GEN-LAST:event_closeDialog

	private void setup()
	{
		// accountFinder = new AccountFinder( VL2.chart, logger, comboAccounts );
		manageDateButtons(null);
		manageSortButtons(null);
		setVisible(true);
		tree.addTreeSelectionListener(this);
	}

	private void analyze(Account acctEntry, Julian startDate, Julian endDate)
	{
		String acctNo = acctEntry.getAccountNo();
		StringBuffer results = new StringBuffer();
		GLEntry glEntry;
		Vector<GLEntry> entries = new Vector<GLEntry>(100, 100);
		String dateFormat = "mm-dd-yyyy";
		int dateSpace = dateFormat.length() + 1;
		try
		{
			UsefulFile glFile = new UsefulFile(vl2Config.getGLFile(), "r");
			String image;
			while (!glFile.EOF())
			{
				image = glFile.readLine(UsefulFile.ALL_WHITE);
				if (image.length() < 3)
					continue; // ignore blank lines
				glEntry = new GLEntry(image);
				if (Chart.match(acctNo, glEntry, startDate, endDate))
					entries.addElement(glEntry);
			}
			glFile.close();
			Vector <GLEntry> sortFields = new Vector<GLEntry>(entries.size());
			for (int i = 0; i < entries.size(); ++i)
				if (rbSortByDescr.isSelected())
					sortFields.addElement(((GLEntry) entries.elementAt(i)));
				else if (rbSortByDate.isSelected())
					sortFields.addElement(((GLEntry) entries.elementAt(i)));
				else if (rbSortByAmount.isSelected())
					sortFields.addElement(((GLEntry) entries.elementAt(i)));
			int[] p = Sorts.sort(sortFields);

			long total = 0L;
			if (rbSortByDate.isSelected() || rbSortByAmount.isSelected())
			{
				for (int i = 0; i < p.length; ++i)
				{
					GLEntry thisEntry = (GLEntry) entries.elementAt(p[i]);
					total += thisEntry.getSignedAmount();
					results.append(thisEntry.getJulianDate().toString(dateFormat) + " " + thisEntry.getField("JREF")
							+ " " + thisEntry.getField("DRCR") + " " + thisEntry.getNormalizedAccountNo() + " "
							+ thisEntry.getField("DESCR") + " " + thisEntry.getField("AMOUNT") + " " + "\n");

				}
			} else if (rbSortByDescr.isSelected())
			{
				int descrWidth = (sortFields.elementAt(0).toString()).length();
				String thisPayer = "";
				String lastPayer = "";
				String thisDescr = "";
				String lastDescr = "";
				long payerSubtotal = 0L;
				long subtotal = 0L;
				for (int i = 0; i < p.length; ++i)
				{
					GLEntry thisEntry = (GLEntry) entries.elementAt(p[i]);
					thisDescr = thisEntry.getField("DESCR");
					if (ckboxSubtotal.isSelected())
					{
						int pSlash = thisDescr.indexOf("/");
						if (pSlash >= 0)
						{
							thisPayer = thisDescr.substring(0, pSlash);
							thisDescr = thisDescr.substring(pSlash + 1);
						} else
							thisPayer = "";

						if (!thisDescr.equals(lastDescr))
						{
							if (!lastDescr.equals(""))
							{
								results.append(Strings.leftJustify(" ", dateSpace) + "SubTotal "
										+ Strings.leftJustify(lastDescr, descrWidth)
										+ Strings.rightJustify(Strings.formatPennies(subtotal, ""), 26 - dateSpace)
										+ "*\n \n");
								subtotal = 0L;
							}
							lastDescr = thisDescr;
						}
						if (!thisPayer.equals(lastPayer))
						{
							if (!lastPayer.equals(""))
							{
								results.append(Strings.leftJustify(" ", dateSpace) + "Payer Total "
										+ Strings.leftJustify(lastPayer, 10)
										+ Strings.rightJustify(Strings.formatPennies(payerSubtotal, ""), 42 - dateSpace)
										+ "*\n \n");
								payerSubtotal = 0L;
							}
							lastPayer = thisPayer;
						}
						subtotal += thisEntry.getSignedAmount();
						if (!lastPayer.equals(""))
							payerSubtotal += thisEntry.getSignedAmount();
					}
					total += thisEntry.getSignedAmount();
					results.append(thisEntry.getJulianDate().toString(dateFormat) + " " + thisEntry.getField("JREF")
							+ " " + thisEntry.getField("DRCR") + " " + thisEntry.getNormalizedAccountNo() + " "
							+ thisEntry.getField("DESCR") + " " + thisEntry.getField("AMOUNT") + " " + "\n");
				}
				if (ckboxSubtotal.isSelected())
				{
					results.append(Strings.leftJustify(" ", dateSpace) + "SubTotal "
							+ Strings.leftJustify(lastDescr, descrWidth)
							+ Strings.rightJustify(Strings.formatPennies(subtotal, ""), 26 - dateSpace) + "*\n \n");
					if (!lastPayer.equals(""))
						results.append(Strings.leftJustify(" ", dateSpace) + "Payer Total "
								+ Strings.leftJustify(lastPayer, 10)
								+ Strings.rightJustify(Strings.formatPennies(payerSubtotal, ""), 42 - dateSpace)
								+ "*\n \n");
				}
			}
			results.append(Strings.leftJustify(" ", dateSpace) + "Total"
					+ Strings.rightJustify(Strings.formatPennies(total, ""), 59 - dateSpace) + "**\n");
			showText("Account Analysis", results.toString());
		} catch (IOException | VLException x)
		{
			statusBar.setText(x.getMessage());
		}
	}

	void showText(String title, String text)
	{
		TextDialog textDialog = new TextDialog(parent, true);
		textDialog.setText(text);
		textDialog.setVisible(true);
	}

	// /**
	// * @param args the command line arguments
	// */
	// public static void main(String args[])
	// {
	// new Analyze(new javax.swing.JFrame(), true).setVisible( true );
	// }
	//
	LogFile logger = VL2.logger;
	AccountFinder accountFinder;
	javax.swing.JFrame parent;
	Chart chart = VL2.chart;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnAnalyze;
	private javax.swing.JButton btnAnalyzeCancel;
	private javax.swing.JCheckBox ckboxSubtotal;
	private JTextField comboAccounts;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JRadioButton rbAllTrans;
	private javax.swing.JRadioButton rbDates;
	private javax.swing.JRadioButton rbSortByAmount;
	private javax.swing.JRadioButton rbSortByDate;
	private javax.swing.JRadioButton rbSortByDescr;
	private javax.swing.JLabel statusBar;
	private javax.swing.JTextField txtEndDate;
	private javax.swing.JTextField txtStartDate;

	// End of variables declaration//GEN-END:variables
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		TreePath selectedPath = e.getNewLeadSelectionPath();
		DefaultMutableTreeNode selectedNode = ((DefaultMutableTreeNode) selectedPath.getLastPathComponent());
		logger.logDebug("ShowBal: selectedNode=" + selectedNode.toString());
		String nodeString = selectedNode.toString();
		if (nodeString.contains("[]"))
			return; // This is not an account node
		String[] split = nodeString.split("\\[*\\]");
		String selectedAccount = split[0];
		logger.logDebug("selectedAccount=" + selectedAccount);
		String selectedAccountNo = selectedAccount.substring(1);
		logger.logDebug("selectedAccountNo=" + selectedAccountNo);
		comboAccounts.setText(selectedAccountNo);
	}
}
