package com.extant.vl2;

//import com.extant.utilities.XProperties;
import com.extant.utilities.UsefulFile;
import com.extant.utilities.Strings;
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.UtilitiesException;
import com.extant.utilities.ViewFile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
//import java.io.*;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.*;
import java.io.IOException;

/**
 *
 * @author jms
 */
public class EnterTransactionPanel extends javax.swing.JPanel {
	/**
	 * Creates new form EnterTransactionPanel
	 */

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

		txtDate = new javax.swing.JTextField();
		txtAmount = new javax.swing.JTextField();
		comboAccount = new javax.swing.JComboBox<>();
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
		txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtDateFocusLost(evt);
			}
		});
		add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 85, -1));

		txtAmount.setBorder(javax.swing.BorderFactory.createTitledBorder("Amount"));
		txtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtAmountFocusLost(evt);
			}
		});
		add(txtAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 85, 50));

		comboAccount.setBorder(javax.swing.BorderFactory.createTitledBorder("Account"));
		comboAccount.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				comboAccountFocusLost(evt);
			}
		});
		comboAccount.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				comboAccountKeyTyped(evt);
			}
		});
		add(comboAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 410, -1));

		txtDescr.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
		txtDescr.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtDescrFocusLost(evt);
			}
		});
		txtDescr.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				txtDescrKeyTyped(evt);
			}
		});
		add(txtDescr, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 502, 40));

		Enter.setText("Enter");
		Enter.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				EnterActionPerformed(evt);
			}
		});
		add(Enter, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 240, 99, -1));

		btnView.setText("View Entries");
		btnView.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnViewActionPerformed(evt);
			}
		});
		add(btnView, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, 110, -1));

		Post.setText("Post Entries");
		Post.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				PostActionPerformed(evt);
			}
		});
		add(Post, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 240, -1, -1));

		Close.setText("Close");
		Close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				CloseActionPerformed(evt);
			}
		});
		add(Close, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 100, -1));

		btnClear.setText("Clear Form");
		btnClear.addActionListener(new java.awt.event.ActionListener() {
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
		Account selectedAccount = (Account) comboAccount.getSelectedItem();
		String accountNo = selectedAccount.getAccountNo();
		// txtDescr.setText(txtDescr.getText().toUpperCase());
		logger.logDebug("enter trans: accountNo=" + accountNo);
		GLEntry glEntry = new GLEntry(jRef, GSNMan.getGSN(), "E", drcr, "100", accountNo, amount, descr,
				transDate.toString("yymmdd"));
		logger.logDebug(glEntry.toString());
		imbalanceAmount += Strings.parsePennies(amount);
		unpostedEntries.add(glEntry);
		manageStatusBar();
		clearForm();
		txtDate.setEnabled(false);
		txtAmount.requestFocus();
	}// GEN-LAST:event_EnterActionPerformed

	private void CloseActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_CloseActionPerformed
		if (!unpostedEntries.isEmpty()) {
			int ans = JOptionPane.showConfirmDialog(new JFrame(),
					"There are unposted entries.\nDo you want to discard them?", "Unposted Entries",
					JOptionPane.YES_NO_OPTION);
			if (ans == 0) // zero means "yes"
			{
				unpostedEntries.clear();
				this.setVisible(false);
			}
		} else
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
		logger.logDebug("GLFilename=" + vl2Config.getGLFile());

		try { /***** See VL2\Test Data\Notes.txt *****/

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
			for (int i = 0; i < unpostedEntries.size(); ++i) {
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
			for (int i = 0; i < unpostedEntries.size(); ++i) {
				GLFile.writeLine(unpostedEntries.get(i).toString());
			}

			// No problems detected, increment GSN and clear unpostedEntries
			new GSNMan(vl2Config, logger).incrementGSN();
			unpostedEntries.clear();
			imbalanceAmount = 0;
			clearForm();
		} catch (VLException vlx) { // Undo everything, inform user, and get out leaving GSN unchanged
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
		try {
			String tempFilename = "C:\\Temp\\ViewEntries.txt";
			UsefulFile tempFile = new UsefulFile(tempFilename, "w");
			for (int i = 0; i < unpostedEntries.size(); ++i) {
				String glEntry = unpostedEntries.get(i).toString();
				tempFile.writeLine(glEntry);
			}
			tempFile.close();
			new ViewFile(tempFilename, logger, false).setVisible(true);
		} catch (IOException iox) {
			logger.log("File Error: " + iox.getMessage());
		} catch (UtilitiesException ux) {
			logger.log("Util Error: " + ux.getMessage());
		}
	}// GEN-LAST:event_btnViewActionPerformed

	private void txtDateFocusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_txtDateFocusLost
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
		String descr = txtDescr.getText();
		txtDescr.setText(descr.toUpperCase());
		manageButtons(); // To eliminate losing focus requirement to get "Enter" button enabled
	}// GEN-LAST:event_txtDescrKeyTyped

	public EnterTransactionPanel(Chart chart, LogFile logger, String transType, VL2Config vl2Config) throws IOException
	{
		initComponents();
		this.logger = logger;
		this.transType = transType;
		this.vl2Config = vl2Config;
		String entityLongName = vl2Config.getEntityLongName();
		// this.setTitle(entityLongName);
		lblCoName.setText(entityLongName);
		cashAcctNo = vl2Config.getCashAcctNo();
		logger.logDebug("CashAcctNo=" + cashAcctNo);
		lblCashAcctDescr.setText("Cash Account No: " + cashAcctNo);
		lblTransType.setText("Cash " + transType);

		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("EntityLongName=" + vl2Config.getEntityLongName());
		logger.logDebug("CashAcctNo=" + vl2Config.getCashAcctNo());

		this.setVisible(true);
		txtDate.setInputVerifier(new DateVerifier());
		txtAmount.setInputVerifier(new AmountVerifier());
		accountFinder = new AccountFinder(chart, logger, comboAccount);
		imbalanceAmount = 0L;
		clearForm();
		String GLFilename = vl2Config.getGLFile();
		logger.logDebug("GLFilename=" + GLFilename);
		GLFile = new UsefulFile(GLFilename, "rw+");
		txtDate.requestFocus();
	}

	private void clearForm()
	{
		logger.logDebug("Enter clearForm");
		if (unpostedEntries.isEmpty()) {
			txtDate.setText(new Julian().toString("mm-dd-yyyy"));
			txtDate.setEnabled(true);
		}
		txtDescr.setText("");
		txtAmount.setText("");
		accountFinder.reset();
		txtDate.requestFocusInWindow();
		manageStatusBar();
		manageButtons();
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
			// return Pattern.matches( datePattern, tf.getText());

			// or use Julian
			transDate = new Julian(tf.getText());
			return transDate.isValid();
		}

		String datePattern = "\\d{1,2}[-/]\\d{1,2}[-/]\\d\\d";
	}

	public void appendToGL(GLEntry glEntry)
	{
		GLFile.println(glEntry.toString());
	}

	public void manageButtons()
	{
		// boolean OKdate = new Julian(txtDate.getText()).isValid();
		// boolean OKaccount = comboAccount.getSelectedIndex() >= 0;
		// boolean OKamount = Strings.regexMatch("\\d+\\.\\d\\d", txtAmount.getText());
		// boolean OKdescr = !txtDescr.getText().isEmpty();
		// if (!OKdate) System.out.print("OKdate ");
		// if (!OKaccount) System.out.print("OKaccount ");
		// if (!OKamount) System.out.print("OKamount ");
		// if (!OKdescr) System.out.print("OKdescr");

		boolean OKtoEnter = new Julian(txtDate.getText()).isValid() && comboAccount.getSelectedIndex() >= 0
				&& Strings.regexMatch("\\d+\\.\\d\\d", txtAmount.getText()) && !txtDescr.getText().isEmpty();
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
	VL2Config vl2Config;
	String transType;
	Julian transDate;
	String drcr;
	String amount;
	String descr;
	String cashAcctNo;
	Color defaultBackground;
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
	private javax.swing.JComboBox<String> comboAccount;
	private javax.swing.JLabel lblCashAcctDescr;
	private javax.swing.JLabel lblCoName;
	private javax.swing.JLabel lblTransType;
	private javax.swing.JLabel statusBar;
	private javax.swing.JTextField txtAmount;
	private javax.swing.JTextField txtDate;
	private javax.swing.JTextField txtDescr;
	// End of variables declaration//GEN-END:variables
}