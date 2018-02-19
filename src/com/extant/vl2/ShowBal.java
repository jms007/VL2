/*
 * ShowBal.java
 *
 * Created on November 8, 2002, 4:38 PM
 *
 *        //Create a chart tree that allows one node selection at a time.
 *            chartTree.getSelectionModel().setSelectionMode
 *                (TreeSelectionModel.SINGLE_TREE_SELECTION);
 *
 *         // In the "user" classes:
 *        // Add implements TreeSelectionListener
 *        // Listen for a change of selected node
 *        // (This listener must be in each of the classes that use the 
 *        //  ChartTree to select an account, instead of a comboBox.)
 *        // It DOES NOT belong here in VL2!
 *        chartTree.addTreeSelectionListener(this);
 *       
 *        chartTree.addTreeSelectionListener(new TreeSelectionListener()
 *            {
 *                public void valueChanged(TreeSelectionEvent e)
 *                {
 *                    DefaultMutableTreeNode node =
 *                        (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
 *                } 
 *           )
 *
 */

package com.extant.vl2;

import com.extant.utilities.*;
import static com.extant.vl2.ChartTree.tree;
//import com.extant.utilities.Strings;
//import com.extant.utilities.Julian;
//import com.extant.utilities.UsefulFile;
//import com.extant.utilities.LogFile;
//import com.extant.utilities.VLException;
import java.io.IOException;
//import java.util.Vector;
//import java.util.StringTokenizer;
import javax.swing.JRadioButton;
//import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.event.*;
//import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.KeyEvent;

/**
 *
 * @author jms
 */
public class ShowBal extends javax.swing.JFrame implements TreeSelectionListener {
	public ShowBal(Chart chart, ChartTree tree, VL2FileMan vl2FileMan)
	{
		logger = VL2.logger;
		// For debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		this.chart = chart;
		glFileName = vl2FileMan.getGLFile();
		initComponents();
		setup();
	}

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		jLabel1 = new javax.swing.JLabel();
		rbAllTrans = new javax.swing.JRadioButton();
		rbCutoffDate = new javax.swing.JRadioButton();
		txtEffDate = new javax.swing.JTextField();
		lblAnswer1 = new javax.swing.JLabel();
		lblAnswer2 = new javax.swing.JLabel();
		btnCompute = new javax.swing.JButton();
		btnClose = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();
		statusBar = new javax.swing.JLabel();
		txtAccountNo = new javax.swing.JTextField();

		setTitle("Compute Account Balance");
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				exitForm(evt);
			}
		});
		getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		jLabel1.setText("Account: No:");
		getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

		rbAllTrans.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbAllTrans.setText("Include All Transactions");
		rbAllTrans.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rbAllTransActionPerformed(evt);
			}
		});
		getContentPane().add(rbAllTrans, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, -1, -1));

		rbCutoffDate.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbCutoffDate.setText("Specify Cutoff Date ...");
		rbCutoffDate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rbCutoffDateActionPerformed(evt);
			}
		});
		getContentPane().add(rbCutoffDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, -1, -1));

		txtEffDate.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				txtEffDateKeyTyped(evt);
			}
		});
		getContentPane().add(txtEffDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 90, 80, -1));

		lblAnswer1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblAnswer1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		getContentPane().add(lblAnswer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 300, -1));

		lblAnswer2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblAnswer2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		getContentPane().add(lblAnswer2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 300, -1));

		btnCompute.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		btnCompute.setMnemonic('B');
		btnCompute.setText("Show Balance");
		btnCompute.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnComputeActionPerformed(evt);
			}
		});
		getContentPane().add(btnCompute, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, -1, -1));

		btnClose.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		btnClose.setMnemonic('C');
		btnClose.setText("Close");
		btnClose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnCloseActionPerformed(evt);
			}
		});
		getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 190, -1, -1));

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jPanel1.setLayout(new java.awt.BorderLayout());

		statusBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jPanel1.add(statusBar, java.awt.BorderLayout.NORTH);

		getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 430, 30));
		getContentPane().add(txtAccountNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 210, -1));

		setSize(new java.awt.Dimension(471, 356));
		setLocationRelativeTo(null);
	}// </editor-fold>//GEN-END:initComponents

	private void txtEffDateKeyTyped(java.awt.event.KeyEvent evt)// GEN-FIRST:event_txtEffDateKeyTyped
	{// GEN-HEADEREND:event_txtEffDateKeyTyped
		if (evt.getKeyChar() == KeyEvent.VK_ENTER)
			btnComputeActionPerformed(null);
	}// GEN-LAST:event_txtEffDateKeyTyped

	private void btnCloseActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnCloseActionPerformed
	{// GEN-HEADEREND:event_btnCloseActionPerformed
		exitForm(null);
	}// GEN-LAST:event_btnCloseActionPerformed

	private void btnComputeActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnComputeActionPerformed
	{// GEN-HEADEREND:event_btnComputeActionPerformed
		try {
			compute();
		} catch (VLException vlx) {
			statusBar.setText(vlx.getMessage());
		}
	}// GEN-LAST:event_btnComputeActionPerformed

	private void rbCutoffDateActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_rbCutoffDateActionPerformed
	{// GEN-HEADEREND:event_rbCutoffDateActionPerformed
		manageButtons(evt);
	}// GEN-LAST:event_rbCutoffDateActionPerformed

	private void rbAllTransActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_rbAllTransActionPerformed
	{// GEN-HEADEREND:event_rbAllTransActionPerformed
		manageButtons(evt);
	}// GEN-LAST:event_rbAllTransActionPerformed

	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt)
	{// GEN-FIRST:event_exitForm
		this.setVisible(false);
	}// GEN-LAST:event_exitForm

	void setup()
	{
		tree.addTreeSelectionListener(this);
		manageButtons(null);
		// accountFinder = new AccountFinder( chart, logger, comboAccounts );
		super.setVisible(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
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
		txtAccountNo.setText(selectedAccountNo);
	}

	void compute() throws VLException
	{
		UsefulFile glFile;
		String image;
		GLEntry glEntry;
		Julian cutoffDate;
		boolean include;
		if (rbCutoffDate.isSelected()) {
			if (!Julian.isValid(txtEffDate.getText())) {
				statusBar.setText("Cutoff Date is not valid");
				return;
			}
			cutoffDate = new Julian(txtEffDate.getText());
		} else
			cutoffDate = new Julian();
		statusBar.setText("");
		lblAnswer1.setText("");
		lblAnswer2.setText("");
		// String acctEntry = ((Account)comboAccounts.getSelectedItem()).getAccountNo();
		String acctEntry = (txtAccountNo.getText());
		long bal = 0L;
		Julian lastDate = new Julian("1-1-2900");
		boolean found = false;
		try {
			glFile = new UsefulFile(glFileName, "r");
			while (!glFile.EOF()) {
				image = glFile.readLine(UsefulFile.ALL_WHITE);
				glEntry = new GLEntry(image);
				include = Chart.match(acctEntry, glEntry, cutoffDate);
				if (include) {
					found = true;
					bal += glEntry.getSignedAmount();
					if (glEntry.getJulianDate().compareTo(lastDate) > 0)
						lastDate = glEntry.getJulianDate();
				}
			}
			glFile.close();
			if (found) {
				lblAnswer1.setText("Balance as of " + cutoffDate.toString("mm-dd-yyyy") + ":");
				lblAnswer2.setText(Strings.formatPennies(bal, ","));
			} else {
				lblAnswer1.setText("No Transactions found");
				lblAnswer2.setText("");
			}
		} catch (IOException iox) {
			statusBar.setText(iox.getMessage());
		}
	}

	void manageButtons(java.awt.event.ActionEvent evt)
	{
		JRadioButton button = rbAllTrans;
		if (evt == null)
			button = rbAllTrans;
		else if (evt.getActionCommand().startsWith("Include"))
			button = rbAllTrans;
		else if (evt.getActionCommand().startsWith("Specify"))
			button = rbCutoffDate;
		rbAllTrans.setSelected(button == rbAllTrans);
		rbCutoffDate.setSelected(button == rbCutoffDate);
		txtEffDate.setVisible(rbCutoffDate.isSelected());
		if (rbCutoffDate.isSelected())
			txtEffDate.requestFocus();
	}

	LogFile logger;
	Chart chart;
	String glFileName;
	AccountFinder accountFinder;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnClose;
	private javax.swing.JButton btnCompute;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JLabel lblAnswer1;
	private javax.swing.JLabel lblAnswer2;
	private javax.swing.JRadioButton rbAllTrans;
	private javax.swing.JRadioButton rbCutoffDate;
	private javax.swing.JLabel statusBar;
	private javax.swing.JTextField txtAccountNo;
	private javax.swing.JTextField txtEffDate;
	// End of variables declaration//GEN-END:variables

	/*****
	 * How to implement account selection from chart In VL2: add chartTree to the
	 * parameters In user class: add ChartTree tree in the parameters add implements
	 * [javax.swing.event.]TreeSelectionListener tree.addTreeSelectionListener(this)
	 * in the initialization code add method valueChanged(TreeSelectionEvent evt) to
	 * process the event
	 *****/
}
