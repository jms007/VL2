package com.extant.vl2;

import com.extant.utilities.UsefulFile;
import com.extant.utilities.Strings;
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author jms
 */
@SuppressWarnings("serial")
public class EnterJournalTransaction extends javax.swing.JPanel
{
	/**
	 * Creates new form EnterJournalTransaction
	 */
	public EnterJournalTransaction(Chart chart, ChartTree tree, VL2Config vl2Config, LogFile logger)
	{
		this.logger = logger;
		initComponents();
		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		this.vl2Config = vl2Config;
		txtDate.setInputVerifier(new DateVerifier());
		txtDebitAmount.setInputVerifier(new AmountVerifier());
		txtCreditAmount.setInputVerifier(new AmountVerifier());
		lblEntityName.setText(vl2Config.getEntityLongName());
		debitAccountFinder = new AccountFinder(chart, tree, comboDebitAccount, logger);
		creditAccountFinder = new AccountFinder(chart, tree, comboCreditAccount, logger);
		imbalanceAmount = 0;
		clearForm();
		manageStatusBar();
		manageButtons();
		logger.logDebug("EnterJournal... Initialization Complete");
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		lblEntityName = new javax.swing.JLabel();
		jLabel1 = new javax.swing.JLabel();
		txtDate = new javax.swing.JTextField();
		comboCreditAccount = new javax.swing.JComboBox<Account>();
		comboDebitAccount = new javax.swing.JComboBox<Account>();
		txtDescr = new javax.swing.JTextField();
		View = new javax.swing.JButton();
		Clear = new javax.swing.JButton();
		Enter = new javax.swing.JButton();
		Post = new javax.swing.JButton();
		Close = new javax.swing.JButton();
		txtCreditAmount = new javax.swing.JTextField();
		txtDebitAmount = new javax.swing.JTextField();
		Status = new javax.swing.JLabel();

		setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		lblEntityName.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
		lblEntityName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblEntityName.setText("Entity Name");
		add(lblEntityName, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, 520, -1));

		jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("Journal Entry");
		add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 520, -1));

		txtDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));
		add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 85, -1));

		comboCreditAccount.setBorder(javax.swing.BorderFactory.createTitledBorder("Credit Account"));
		comboCreditAccount.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				comboCreditAccountKeyTyped(evt);
			}
		});
		add(comboCreditAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 300, -1));

		comboDebitAccount.setBorder(javax.swing.BorderFactory.createTitledBorder("Debit Account"));
		comboDebitAccount.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				comboDebitAccountKeyTyped(evt);
			}
		});
		add(comboDebitAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 300, -1));

		txtDescr.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
		// txtDescr.addFocusListener(new java.awt.event.FocusAdapter()
		// {
		// public void focusLost(java.awt.event.FocusEvent evt)
		// {
		// txtDescrFocusLost(evt);
		// }
		// });

		txtDescr.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				txtDescrActionPerformed(evt);
			}
		});
		txtDescr.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				txtDescrKeyTyped(evt);
			}
		});
		add(txtDescr, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 520, 37));

		View.setText("View Entries");

		View.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnViewActionPerformed(evt);
			}
		});
		add(View, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 110, -1));

		Clear.setText("Clear Form");
		Clear.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnClearActionPerformed(evt);
			}
		});
		add(Clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, 110, -1));

		Enter.setText("Enter");
		Enter.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				EnterActionPerformed(evt);
			}
		});
		add(Enter, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 240, 99, -1));

		Post.setText("Post Entries");
		Post.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				PostActionPerformed(evt);
			}
		});
		add(Post, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 240, -1, -1));

		Close.setText("Close");
		Close.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				CloseActionPerformed(evt);
			}
		});
		add(Close, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 280, 100, -1));

		txtCreditAmount.setBorder(javax.swing.BorderFactory.createTitledBorder("Credit Amount"));
		add(txtCreditAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 120, 120, 45));

		txtDebitAmount.setBorder(javax.swing.BorderFactory.createTitledBorder("Debit Amount"));
		add(txtDebitAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 70, 120, -1));

		Status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(Status, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 510, 20));
	}// </editor-fold>//GEN-END:initComponents

	private void comboCreditAccountKeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_comboCreditAccountKeyTyped
		creditAccountFinder.processKeyEvent(evt);
	}// GEN-LAST:event_comboCreditAccountKeyTyped

	private void comboDebitAccountKeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_comboDebitAccountKeyTyped
		debitAccountFinder.processKeyEvent(evt);
	}// GEN-LAST:event_comboDebitAccountKeyTyped

	// private void txtDescrFocusLost(java.awt.event.FocusEvent evt)
	// {// GEN-FIRST:event_txtDescrFocusLost
	// descr = txtDescr.getText().toUpperCase();
	// txtDescr.setText(descr);
	// manageButtons();
	// }// GEN-LAST:event_txtDescrFocusLost
	//
	private void btnViewActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnViewActionPerformed
		viewEntries();
	}// GEN-LAST:event_btnViewActionPerformed

	private void btnClearActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnClearActionPerformed
		clearForm();
	}// GEN-LAST:event_btnClearActionPerformed

	private void EnterActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_EnterActionPerformed
		// Build one or two GLEntry's with this data, add to unpostedEntries list,
		// update the imbalanceAmount, update the status bar, update the buttons,
		// and clear the form, leaving date field locked.
		logger.logDebug("EnterActionPerformed");
		if (txtDate.getText().isEmpty())
		{
			Status.setText("Date is not entered.");
			return;
		}

		transDate = new Julian(txtDate.getText());
		String jRef = "JE" + transDate.toString("mm");

		// Build the debit entry (if there is one)
		debitAmount = txtDebitAmount.getText();
		if (!debitAmount.isEmpty())
		{
			if (comboDebitAccount.getSelectedIndex() < 0)
				return;
			Account selectedDebitAccount = (Account) comboDebitAccount.getSelectedItem();
			debitAccountNo = Strings.trim(selectedDebitAccount.getFullAccountNo(), "/");
			GLEntry glEntryDebit = new GLEntry(jRef, GSNMan.getGSN(), "E", "D", "100", debitAccountNo, debitAmount,
					descr, transDate.toString("yymmdd"));
			// logger.logDebug(glEntryDebit.toString());
			imbalanceAmount += Strings.parsePennies(debitAmount);
			unpostedEntries.add(glEntryDebit);
		}

		// Build the Credit entry (if there is one)
		creditAmount = txtCreditAmount.getText();
		if (!creditAmount.isEmpty())
		{
			if (comboCreditAccount.getSelectedIndex() < 0)
				return;
			creditAmount = txtCreditAmount.getText();
			Account selectedCreditAccount = (Account) comboCreditAccount.getSelectedItem();
			creditAccountNo = Strings.trim(selectedCreditAccount.getFullAccountNo(), "/");
			GLEntry glEntryCredit = new GLEntry(jRef, GSNMan.getGSN(), "E", "C", "100", creditAccountNo, creditAmount,
					descr, transDate.toString("yymmdd"));
			// logger.logDebug(glEntryCredit.toString());
			imbalanceAmount -= Strings.parsePennies(creditAmount);
			unpostedEntries.add(glEntryCredit);
		}
		clearForm();
	}// GEN-LAST:event_EnterActionPerformed

	private void PostActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_PostActionPerformed
		logger.logDebug("Enter PostActionPerformed");
		try
		{
			UsefulFile GLFile = new UsefulFile(vl2Config.getGLFile(), "w+");
			// Change status to "F" and append the unposted entries to GL file
			for (int i = 0; i < unpostedEntries.size(); ++i)
			{
				GLEntry glEntry = unpostedEntries.get(i);
				glEntry.setField("STATUS", "F");
				GLFile.writeLine(glEntry.toString());
			}
			GLFile.close();
			// Clear upostedEntries
			unpostedEntries.clear();
			imbalanceAmount = 0;
			clearForm();
			// this.setVisible(false);
		} catch (Exception x)
		{ // Undo everything, inform user, and get out leaving GSN unchanged
			int nUnposted = unpostedEntries.size();
			logger.log("Failed to move " + nUnposted + " entries to GLFile");
			logger.logFatal(x.getMessage());
			System.exit(2); // Get out
		}

		// No problems detected, increment GSN and clear unpostedEntries
		unpostedEntries.clear();
		new GSNMan(vl2Config, logger).incrementGSN();
		clearForm();
	}// GEN-LAST:event_PostActionPerformed

	private void CloseActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_CloseActionPerformed
		logger.logDebug("CloseActionPerformed");
		if (!unpostedEntries.isEmpty())
			unpostedEntries.clear();
		// if (!unpostedEntries.isEmpty())
		// {
		// JFrame jFrame = new JFrame();
		// MsgBox msgBox = new MsgBox(jFrame, "Unposted Entries",
		// "There are unposted entries!\nDo you want to discard them?", MsgBox.YES_NO);
		// String command = msgBox.getCommand(); // Assume "No"
		// if (command != null)
		// if (command.equals("Yes"))
		// unpostedEntries.clear();
		// jFrame.dispose();
		// java.awt.Frame[] VL2Frames = VL2.getFrames();
		// java.awt.Frame frame;
		// for (int i = 0; i < VL2Frames.length; ++i)
		// {
		// frame = VL2Frames[i];
		// java.awt.MenuBar menuBar = frame.getMenuBar();
		// logger.log("menuBar: " + menuBar.toString());
		// }
		// }
		manageButtons();
		manageStatusBar();
		this.setVisible(false);
	}// GEN-LAST:event_CloseActionPerformed

	private void txtDescrActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_txtDescrActionPerformed
		descr = txtDescr.getText().toUpperCase();
		txtDescr.setText(descr);
		manageButtons();
	}// GEN-LAST:event_txtDescrActionPerformed

	private void txtDescrKeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_txtDescrKeyTyped
		// To eliminate the requirement to lose focus in otder to get "Enter" button
		// enabled
		manageButtons();
		descr = txtDescr.getText().toUpperCase();
		txtDescr.setText(descr);

	}// GEN-LAST:event_txtDescrKeyTyped

	private void viewEntries()
	{ // Display the unposted entries (if any)
		logger.logInfo("Unposted Entries:");
		if (!unpostedEntries.isEmpty())
		{
			for (int i = 0; i < unpostedEntries.size(); ++i)
				logger.logInfo(unpostedEntries.get(i).toString());
		} else
		{
			Status.setText("There are no unposted entries");
			logger.logInfo("There are no unposted entries");
		}
	}

	private void clearForm()
	{
		// Clear entry fields (DO NOT remove unposted transactions)
		logger.logDebug("EnterJournalTransaction/clearForm");
		if (unpostedEntries.isEmpty())
		{
			txtDate.setText("");
			txtDate.setEnabled(true);
		} else
			txtDate.setEnabled(false);
		txtDescr.setText("");
		txtDebitAmount.setText("");
		txtCreditAmount.setText("");
		debitAccountFinder.reset();
		creditAccountFinder.reset();
		debitAccountNo = null;
		creditAccountNo = null;
		manageButtons();
		manageStatusBar();
	}

	public void manageButtons()
	{
		txtDate.setEnabled(unpostedEntries.isEmpty());
		Enter.setEnabled(!txtDescr.getText().isEmpty()); // && debitAccountNo != null && creditAccountNo != null);
		View.setEnabled(!unpostedEntries.isEmpty());
		Post.setEnabled(!unpostedEntries.isEmpty() && imbalanceAmount == 0);
		Clear.setEnabled(true);
		Close.setEnabled(true);
	}

	public void manageStatusBar()
	{
		String statusMsg;
		if (unpostedEntries.isEmpty())
			statusMsg = "No unposted entries.";
		else
			statusMsg = Strings.plurals("unnposted entry", unpostedEntries.size());
		statusMsg += "   Amount to balance = " + Strings.formatPennies(imbalanceAmount);
		Status.setText(statusMsg);
	}

	class AmountVerifier extends InputVerifier
	{
		String amountPattern = "\\d*\\.\\d\\d";

		public boolean verify(JComponent jc)
		{
			JTextField tf = (JTextField) jc;
			return Pattern.matches(amountPattern, tf.getText());
		}
	}

	class DateVerifier extends InputVerifier
	{
		public boolean verify(JComponent jc)
		{
			JTextField tf = (JTextField) jc;
			// Use regex
			// String datePattern = "\\d{1,2}[-/]\\d{1,2}[-/]\\d\\d";
			// return Pattern.matches( datePattern, tf.getText());

			// or use Julian
			transDate = new Julian(tf.getText());
			//logger.log("DateVerifier: transDate=" + transDate);
			if (transDate.toString("yy").equals(vl2Config.currentYY))
				return true;
			return false;
		}
	}

	// Local Variables
	VL2Config vl2Config;
	LogFile logger;
	Julian transDate;
	String drcr;
	String debitAmount;
	String creditAmount;
	String descr;
	AccountFinder debitAccountFinder;
	AccountFinder creditAccountFinder;
	UsefulFile GLFile;
	List<GLEntry> unpostedEntries = new ArrayList<GLEntry>();
	long imbalanceAmount;
	// UsefulFile GL0010;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton Close;
	private javax.swing.JButton Enter;
	private javax.swing.JButton Post;
	private javax.swing.JLabel Status;
	private javax.swing.JButton View;
	private javax.swing.JButton Clear;
	private javax.swing.JComboBox<Account> comboCreditAccount;
	private javax.swing.JComboBox<Account> comboDebitAccount;
	private String debitAccountNo;
	private String creditAccountNo;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel lblEntityName;
	private javax.swing.JTextField txtCreditAmount;
	private javax.swing.JTextField txtDate;
	private javax.swing.JTextField txtDebitAmount;
	private javax.swing.JTextField txtDescr;
	// End of variables declaration//GEN-END:variables
}
