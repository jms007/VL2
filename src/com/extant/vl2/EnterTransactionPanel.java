package com.extant.vl2;

import com.extant.utilities.UsefulFile;
import com.extant.utilities.Strings;
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.UtilitiesException;
import com.extant.utilities.ViewFile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
//import java.util.regex.Pattern;
import javax.swing.*;
import java.io.IOException;

/**
 *
 * @author jms
 */
@SuppressWarnings("serial")
public class EnterTransactionPanel extends javax.swing.JPanel
{
	/**
	 * Creates new form EnterTransactionPanel for entry of cash transactions
	 */

	public EnterTransactionPanel(Chart chart, ChartTree chartTree, LogFile logger, String transType,
			VL2Config vl2Config) throws IOException
	{
		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		initComponents();
		this.logger = logger;
		this.transType = transType;
		this.vl2Config = vl2Config;
		this.chartTree = chartTree;
		String GLFilename = vl2Config.getGLFile();
		logger.logDebug("GLFilename=" + GLFilename);
		GLFile = new UsefulFile(GLFilename, "rw+");
		normalBG = txtAmount.getBackground();
		logger.logDebug("normalBG="+normalBG);

		String entityLongName = vl2Config.getEntityLongName();
		lblCoName.setText(entityLongName);
		cashAcctNo = vl2Config.getCashAcctNo();
		lblCashAcctDescr.setText("Cash Account No: " + cashAcctNo);
		lblTransType.setText("Cash " + transType);
		imbalanceAmount = 0L;
		//clearForm();
		manageButtons();

		accountFinder = new AccountFinder(chart, chartTree, comboAccount, logger);
		this.requestFocus();
		txtDate.requestFocusInWindow();
		this.setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// @SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		txtDate = new javax.swing.JTextField();
		txtAmount = new javax.swing.JTextField();
		comboAccount = new javax.swing.JComboBox<Account>();
		txtDescr = new javax.swing.JTextField();
		Enter = new javax.swing.JButton();
		btnView = new javax.swing.JButton();
		Post = new javax.swing.JButton();
		Close = new javax.swing.JButton();
		btnClear = new javax.swing.JButton();
		lblCashAcctDescr = new javax.swing.JLabel();
		lblTransType = new javax.swing.JLabel();
		lblCoName = new javax.swing.JLabel();
		statusBar = new javax.swing.JLabel();

		setMinimumSize(new java.awt.Dimension(700, 285));
		setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		txtDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));
		txtDate.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtDateFocusLost(evt);
			}
		});
		add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 85, -1));

		txtAmount.setBorder(javax.swing.BorderFactory.createTitledBorder("Amount"));
		txtAmount.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtAmountFocusLost(evt);
			}
		});
		add(txtAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 85, 50));

		comboAccount.setBorder(javax.swing.BorderFactory.createTitledBorder("Account"));
		comboAccount.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				comboAccountFocusLost(evt);
			}
		});
		comboAccount.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				comboAccountKeyTyped(evt);
			}
		});
		add(comboAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 410, -1));

		txtDescr.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
		txtDescr.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtDescrFocusLost(evt);
			}
		});
		txtDescr.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				txtDescrKeyTyped(evt);
			}
		});
		add(txtDescr, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 502, 40));

		Enter.setText("Enter");
		Enter.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				EnterActionPerformed(evt);
			}
		});
		add(Enter, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 240, 99, -1));

		btnView.setText("View Entries");
		btnView.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnViewActionPerformed(evt);
			}
		});
		add(btnView, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, 110, -1));

		Post.setText("Post Entries");
		Post.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				PostActionPerformed(evt);
			}
		});
		add(Post, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 240, -1, -1));

		Close.setText("Close");
		Close.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				CloseActionPerformed(evt);
			}
		});
		add(Close, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 100, -1));

		btnClear.setText("Clear Form");
		btnClear.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnClearActionPerformed(evt);
			}
		});
		add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 270, 110, -1));

		lblCashAcctDescr.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
		lblCashAcctDescr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(lblCashAcctDescr, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 330, 40));

		lblTransType.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
		lblTransType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(lblTransType, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 500, 20));

		lblCoName.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
		lblCoName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblCoName.setText("Entity Name");
		add(lblCoName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 520, 20));

		statusBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(statusBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 500, 20));
	}// </editor-fold>//GEN-END:initComponents

	private void txtDescrFocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtDescrFocusLost
		manageButtons();
		descr = txtDescr.getText().toUpperCase().trim();
		txtDescr.setText(descr);
	}// GEN-LAST:event_txtDescrFocusLost

	private void EnterActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_EnterActionPerformed
		// Build a GLEntry with this data and current GSN, add to
		// unpostedEntries list, add to imbalanceAmount, update status bar
		// and clear the form leaving GSN unchanged.
		transDate = new Julian(txtDate.getText());
		String jRef = "C" + transType.substring(0, 1) + transDate.toString("mm");
		logger.logDebug("jRef=" + jRef);
		if (transType.startsWith("R"))
			drcr = "C";
		else
			drcr = "D";
		amount = txtAmount.getText();
		// TODO Note the following statement clears everything in the comboBox except the selected item 
		Account selectedAccount = (Account) comboAccount.getSelectedItem();
		String accountNo = selectedAccount.getAccountNo();
		logger.logDebug("enter trans: accountNo=" + accountNo);
		// Rebuild comboBox
		accountFinder.setup();
		GLEntry glEntry = new GLEntry(jRef, GSNMan.getGSN(), "E", drcr, "100", accountNo, amount, descr,
				transDate.toString("yymmdd"));
		logger.logDebug(glEntry.toString());
		imbalanceAmount += Strings.parsePennies(amount);
		unpostedEntries.add(glEntry);
		manageStatusBar();
		txtAmount.setText("");
		comboAccount.setSelectedIndex(-1); // TODO
		txtDescr.setText("");
		manageButtons();
		txtDate.setEnabled(false);
		txtAmount.requestFocusInWindow();
	}// GEN-LAST:event_EnterActionPerformed

	private void CloseActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_CloseActionPerformed
		if (!unpostedEntries.isEmpty())
		{
			int ans = JOptionPane.showConfirmDialog(new JFrame(),
					"There are unposted entries.\nDo you want to discard them?", "Unposted Entries",
					JOptionPane.YES_NO_OPTION);
			if (ans == 0) // zero means "yes"
				unpostedEntries.clear();
		}
		clearForm();
		this.setVisible(false);
		manageButtons();
		manageStatusBar();
	}// GEN-LAST:event_CloseActionPerformed

	private void btnClearActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnClearActionPerformed
		clearForm();
	}// GEN-LAST:event_btnClearActionPerformed

	private void PostActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_PostActionPerformed
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("Enter PostActionPerformed");
		// logger.logDebug("GLFilename=" + vl2Config.getGLFile());

		try
		{ /***** See VL2\Test Data\Notes.txt *****/

			// Build balancing entry with GSN and status 'F' and add to unpostedEntries
			GLEntry balEntry = new GLEntry(unpostedEntries.get(0).toString());
			// Fields to change: DRCR, ACCOUNT, AMOUNT, DESCR
			if (balEntry.getField("DRCR").equals("C"))
				balEntry.setField("DRCR", "D");
			else
				balEntry.setField("DRCR", "C");
			balEntry.setField("JREF", unpostedEntries.get(0).getField("JREF"));
			balEntry.setField("ACCOUNT", cashAcctNo);
			balEntry.setField("AMOUNT", Strings.formatPennies(imbalanceAmount, "").trim());
			if (balEntry.getField("JREF").startsWith("CR"))
				balEntry.setField("DESCR", "Cash Receipt");
			else
				balEntry.setField("DESCR", "Cash Disbursement");

			unpostedEntries.add(balEntry);

			// Update unposted entries with current GSN and calculate total
			long transBal = 0L;
			for (int i = 0; i < unpostedEntries.size(); ++i)
			{
				balEntry = unpostedEntries.get(i);
				balEntry.setField("GSN", GSNMan.getGSN());
				balEntry.setField("STATUS", "F");
				transBal += balEntry.getSignedAmount();
				logger.logDebug(i + " " + balEntry.toString() + "   transBal=" + transBal);
			}
			// Verify the transaction is in balance
			if (transBal != 0)
				throw new VLException(VLException.OUT_OF_BALANCE, Long.toString(transBal));

			// No further checks to do ...
			// Append the unposted entries to GL file
			for (int i = 0; i < unpostedEntries.size(); ++i)
			{
				GLFile.println(unpostedEntries.get(i).toString());
			}

			// No problems detected, increment GSN and clear unpostedEntries
			new GSNMan(vl2Config, logger).incrementGSN();
			unpostedEntries.clear();
			imbalanceAmount = 0;
			clearForm();
		} catch (VLException vlx)
		{ // Undo everything, inform user, and get out leaving GSN unchanged
			logger.logFatal("Failed to move unposted transaction to GL0010.DAT (" + vlx.getMessage() + ")");
			return; // Get out
		}
	}// GEN-LAST:event_PostActionPerformed

	private void comboAccountKeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_comboAccountKeyTyped
		accountFinder.processKeyEvent(evt);
	}// GEN-LAST:event_comboAccountKeyTyped

	private void btnViewActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnViewActionPerformed
		try
		{
			String tempFilename = "C:\\Temp\\ViewEntries.txt";
			UsefulFile tempFile = new UsefulFile(tempFilename, "w");
			for (int i = 0; i < unpostedEntries.size(); ++i)
			{
				String glEntry = unpostedEntries.get(i).toString();
				tempFile.writeLine(glEntry);
			}
			tempFile.close();
			new ViewFile(tempFilename, logger, false).setVisible(true);
		} catch (IOException iox)
		{
			logger.log("File Error: " + iox.getMessage());
		} catch (UtilitiesException ux)
		{
			logger.log("Util Error: " + ux.getMessage());
		}
	}// GEN-LAST:event_btnViewActionPerformed

	private void txtDateFocusLost(java.awt.event.FocusEvent evt) //TODO
	{// GEN-FIRST:event_txtDateFocusLost
		logger.logDebug("txtDateFocusLost - date="+txtDate.getText());
		if (dateVerifier(txtDate.getText()))
			txtDate.setBackground(normalBG);
		else txtDate.setBackground(Color.yellow);
		manageButtons();
	}// GEN-LAST:event_txtDateFocusLost

	private void txtAmountFocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtAmountFocusLost
		manageButtons();
	}// GEN-LAST:event_txtAmountFocusLost

	private void comboAccountFocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_comboAccountFocusLost
		manageButtons();
	}// GEN-LAST:event_comboAccountFocusLost

	private void txtDescrKeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_txtDescrKeyTyped
		String descr = txtDescr.getText().toUpperCase();
		txtDescr.setText(descr);
		manageButtons(); // To eliminate losing focus requirement to get "Enter" button enabled
	}// GEN-LAST:event_txtDescrKeyTyped

	private void clearForm()
	{
		logger.logDebug("Enter clearForm");
//		Leave date unchanged		
//		if (unpostedEntries.isEmpty())
//		{
//			txtDate.setText(new Julian().toString("mm-dd-yy"));
//			txtDate.setEnabled(true);
//		}
		txtDate.setEnabled(true);
		txtDescr.setText("");
		txtAmount.setText("");
		txtDate.requestFocusInWindow();
		manageStatusBar();
		clearButtons();
	}

//	class AmountVerifier extends InputVerifier
//	{
//		String amountPattern = "\\d*\\.\\d\\d";
//
//		public boolean verify(JComponent jc)
//		{
//			JTextField tf = (JTextField) jc;
//			return Pattern.matches(amountPattern, tf.getText());
//		}
//	}
//
	// Use our own code to verify dates
	private boolean dateVerifier(String text)
	{
		boolean valid;
		Julian date = new Julian(txtDate.getText());
		valid = date.isValid() && date.toString("yy").equals(vl2Config.getCurrentYY());
		return valid;
	}

//	class DateVerifier extends InputVerifier
//	{
//		public boolean verify(JComponent jc)
//		{
//			JTextField tf = (JTextField) jc;
//			// Use regex
//			// return Pattern.matches( datePattern, tf.getText());
//
//			// or use Julian
//			transDate = new Julian(tf.getText());
//			String yy = transDate.toString("yy");
//			String currentYear = vl2Config.getCurrentYear();
//			logger.log("yy=" + yy + " currentYear=" + currentYear);
//			boolean valid = transDate.isValid() && yy.equals(vl2Config.getCurrentYear());
//			if (valid) txtDate.setBackground(normalBG);
//			else txtDate.setBackground(Color.YELLOW);
//			return valid;
//		}
//
//		String datePattern = "\\d{1,2}[-/]\\d{1,2}[-/]\\d\\d";
//	}
//
	public void appendToGL(GLEntry glEntry)
	{
		GLFile.println(glEntry.toString());
	}

	public void clearButtons()
	{
		//txtDate.setText("");
		txtDate.setBackground(normalBG);
		txtAmount.setText("");
		txtAmount.setBackground(normalBG);
		//comboAccount.setSelectedIndex(-1);
		
		comboAccount.setBackground(normalBG);
		txtDescr.setText("");
		txtDescr.setBackground(normalBG);
		txtDate.requestFocusInWindow();
	}
	
	public void manageButtons()
	{
		boolean dateIsValid;
		Julian date = new Julian(txtDate.getText());
		dateIsValid = date.isValid() && date.toString("yy").equals(vl2Config.getCurrentYY());
		if (!dateIsValid)
			txtDate.setBackground(Color.yellow);
		else txtDate.setBackground(normalBG);

		boolean amountIsValid;
		amountIsValid = Strings.regexMatch("\\d+\\.\\d\\d", txtAmount.getText());
		if (amountIsValid) txtAmount.setBackground(normalBG);
		else txtAmount.setBackground(Color.yellow);
		
		boolean accountIsValid;
		accountIsValid = comboAccount.getSelectedIndex() >= 0;
		if (!accountIsValid) comboAccount.setBackground(Color.yellow);
		else comboAccount.setBackground(normalBG);
		
		boolean descrIsValid = !txtDescr.getText().isEmpty();
		if (!descrIsValid) txtDescr.setBackground(Color.yellow);
		else txtDescr.setBackground(normalBG);
		
		boolean OKtoEnter = dateIsValid && amountIsValid && accountIsValid && descrIsValid;
		Enter.setEnabled(OKtoEnter);
		txtDate.setEnabled(unpostedEntries.isEmpty());
		btnView.setEnabled(!unpostedEntries.isEmpty());
		Post.setEnabled(!unpostedEntries.isEmpty());
	}

	public void manageStatusBar()
	{
		String statusMsg = "";
		if (unpostedEntries.isEmpty())
			statusMsg = "No unposted entries.";
		else
			statusMsg = Strings.plurals("unposted entry", unpostedEntries.size());
		statusMsg += "   Amount to balance = " + Strings.formatPennies(imbalanceAmount);
		statusBar.setText(statusMsg);
	}

	// Local Variables
	LogFile logger;
	ChartTree chartTree;
	VL2Config vl2Config;
	String transType;
	int currentYear;
	Julian transDate;
	String drcr;
	String amount;
	String descr;
	String cashAcctNo;
	Color normalBG;
	AccountFinder accountFinder;
	List<GLEntry> unpostedEntries = new ArrayList<GLEntry>();
	long imbalanceAmount;
	UsefulFile GLFile = null;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton Close;
	private javax.swing.JButton Enter;
	private javax.swing.JButton Post;
	private javax.swing.JButton btnClear;
	private javax.swing.JButton btnView;
	private javax.swing.JComboBox<Account> comboAccount;
	private javax.swing.JLabel lblCashAcctDescr;
	private javax.swing.JLabel lblCoName;
	private javax.swing.JLabel lblTransType;
	private javax.swing.JLabel statusBar;
	private javax.swing.JTextField txtAmount;
	private javax.swing.JTextField txtDate;
	private javax.swing.JTextField txtDescr;
	// End of variables declaration//GEN-END:variables
}