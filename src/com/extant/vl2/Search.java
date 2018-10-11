/*
 * Search.java
 *
 * Created on April 22, 2003, 12:15 AM
 */

package com.extant.vl2;

import com.extant.utilities.*;
//import com.extant.utilities.TextDialog;
//import com.extant.utilities.XProperties;
//import com.extant.utilities.UsefulFile;
//import com.extant.utilities.Strings;
//import com.extant.utilities.Julian;
//import com.extant.utilities.VLException;
import java.io.IOException;
import javax.swing.JRadioButton;

/**
 *
 * @author jms
 */
@SuppressWarnings("serial")
public class Search extends javax.swing.JFrame
{
	public Search(VL2Config vl2Config)
	{
		initComponents();
		lineBreak = System.lineSeparator();
		glFilename = vl2Config.getGLFile();
		this.vl2Config = vl2Config;
		report = new StringBuffer(1000);
		super.setVisible(true);
		setup();
	}

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		txtString = new javax.swing.JTextField();
		rbIncludeAll = new javax.swing.JRadioButton();
		rbDates = new javax.swing.JRadioButton();
		btnSearch = new javax.swing.JButton();
		btnClose = new javax.swing.JButton();
		txtStartDate = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		txtEndDate = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		lblStatusBar = new javax.swing.JLabel();
		lblTitle = new javax.swing.JLabel();

		rbIncludeAll.setSelected(true); // default
		setTitle("Search");
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				exitForm(evt);
			}
		});
		getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		getContentPane().add(txtString, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 300, -1));

		rbIncludeAll.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbIncludeAll.setMnemonic('I');
		rbIncludeAll.setText("Include All Transactions");
		rbIncludeAll.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				manageGUI(evt);
			}
		});
		getContentPane().add(rbIncludeAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, -1, -1));

		rbDates.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		rbDates.setMnemonic('D');
		rbDates.setText("Specify Dates");
		rbDates.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				manageGUI(evt);
			}
		});
		getContentPane().add(rbDates, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, -1, -1));

		btnSearch.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		btnSearch.setMnemonic('O');
		btnSearch.setText("Search");
		btnSearch.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnSearchActionPerformed(evt);
			}
		});
		getContentPane().add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 190, -1, -1));

		btnClose.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		btnClose.setMnemonic('C');
		btnClose.setText("Close");
		btnClose.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnCloseActionPerformed(evt);
			}
		});
		getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 190, -1, -1));
		getContentPane().add(txtStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 140, 80, -1));

		jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		jLabel1.setText("String to match:");
		getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));
		getContentPane().add(txtEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, 80, -1));

		jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel2.setText("Start");
		getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 120, 80, -1));

		jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
		jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel3.setText("End");
		getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 120, 80, -1));

		lblStatusBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		getContentPane().add(lblStatusBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 470, 30));

		lblTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
		lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblTitle.setText("Search");
		getContentPane().add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 470, -1));

		setSize(new java.awt.Dimension(509, 345));
		setLocationRelativeTo(null);
	}// </editor-fold>//GEN-END:initComponents

	private void btnSearchActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnSearchActionPerformed
	{// GEN-HEADEREND:event_btnSearchActionPerformed
		try
		{
			if (report.length() > 0)
				report.delete(0, report.length() - 1);
			txtString.setText(txtString.getText().toUpperCase());
			String report = doit();
			if (nEntries > 0)
			{
				TextDialog textDialog = new TextDialog(this, true);
				textDialog.setTitle("Search Results");
				String defaultOutFile = vl2Config.getWorkingDirectory() + "SearchMatches.txt";
				textDialog.setOutfileName(defaultOutFile);
				textDialog.setText(report);
				textDialog.setVisible(true);
			}
			// there are no matches - don't show the report at all
			else
				lblStatusBar.setText("No matches were found.");
		} catch (IOException iox)
		{
			lblStatusBar.setText(iox.getMessage());
		}
	}// GEN-LAST:event_btnSearchActionPerformed

	private void btnCloseActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnCloseActionPerformed
	{// GEN-HEADEREND:event_btnCloseActionPerformed
		exitForm(null);
	}// GEN-LAST:event_btnCloseActionPerformed

	private void manageGUI(java.awt.event.ActionEvent evt)// GEN-FIRST:event_manageGUI
	{// GEN-HEADEREND:event_manageGUI
		JRadioButton button = null;
		if (evt == null)
		{ // Initialize
			button = rbIncludeAll;
			rbIncludeAll.setSelected(true);
			rbDates.setSelected(!rbIncludeAll.isSelected());
		} else
		{
			if (evt.getActionCommand().equals(rbDates.getText()))
			{
				button = rbDates;
				rbIncludeAll.setSelected(!rbDates.isSelected());

			} else if (evt.getActionCommand().equals(rbIncludeAll.getText()))
			{
				button = rbIncludeAll;
				rbDates.setSelected(!rbIncludeAll.isSelected());
			}
		}
		txtStartDate.setVisible(rbDates.isSelected());
		txtEndDate.setVisible(rbDates.isSelected());
		jLabel2.setVisible(rbDates.isSelected());
		jLabel3.setVisible(rbDates.isSelected());
		if ((button == rbDates) && txtStartDate.isVisible())
			txtStartDate.requestFocus();
	}// GEN-LAST:event_manageGUI

	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt)
	{// GEN-FIRST:event_exitForm
		dispose();
	}// GEN-LAST:event_exitForm

	private void setup()
	{
		nEntries = 0;
		total = 0L;
		lblStatusBar.setText("");
	}

	private String doit() throws IOException
	{
		setup();
		needle = txtString.getText();
		if (rbDates.isSelected())
		{
			if (!Julian.isValid(txtStartDate.getText()))
			{
				lblStatusBar.setText("Start Date is not valid");
				txtStartDate.requestFocus();
				return "";
			}
			if (!Julian.isValid(txtEndDate.getText()))
			{
				lblStatusBar.setText("End Date is not valid");
				txtEndDate.requestFocus();
				return "";
			}
			startDate = new Julian(txtStartDate.getText());
			endDate = new Julian(txtEndDate.getText());
		} else
		{
			startDate = null;
			endDate = null;
		}

		grep(needle);
		String msg = Strings.plurals("matching entry", nEntries) + " found." + lineBreak;
		report.append(msg + lineBreak);
		report.append("Total Amount: " + Strings.formatPennies(total, ",") + lineBreak);
		this.setVisible(true);
		return report.toString();
	}

	private void grep(String needle) throws IOException
	{
		String image;
		// //!! Apply '?' wildcard to match any character
		// needle = needle.replace( '?', ' ' );
		UsefulFile file = new UsefulFile(glFilename, "r");
		lineNo = 0;
		while (!file.EOF())
		{
			image = file.readLine(UsefulFile.ALL_WHITE).toUpperCase();
			++lineNo;
			// Match on needle anywhere in the record
			if (image.contains(needle))
				addEntry("found in line " + lineNo.toString() + ": " + image);
		}
		file.close();
	}

	private void addEntry(String image)
	{
		GLEntry glEntry;
		try
		{
			glEntry = new GLEntry(image);
		} catch (VLException vlx)
		{
			report.append("error in record " + lineNo + ": " + image + lineBreak);
			return;
		}

		boolean include = true;
		if (startDate != null)
			include &= glEntry.getJulianDate().getDayNumber() >= startDate.getDayNumber();
		if (endDate != null)
			include &= glEntry.getJulianDate().getDayNumber() <= endDate.getDayNumber();
		if (include)
		{
			report.append(image + lineBreak);
			total += glEntry.getSignedAmount();
			++nEntries;
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	// public static void main(String args[])
	// {
	// try
	// {
	// Clip clip = new Clip(args);
	// new Search();
	// } catch (UtilitiesException ux)
	// {
	// Console.println(ux.getMessage());
	// }
	// }

	String glFilename;
	String lineBreak;
	String needle;
	Julian startDate = null;
	Julian endDate = null;
	StringBuffer report;
	Integer lineNo;
	int nEntries;
	long total;
	// StringBuffer comboAcctBuffer;
	VL2Config vl2Config;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnClose;
	private javax.swing.JButton btnSearch;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel lblStatusBar;
	private javax.swing.JLabel lblTitle;
	private javax.swing.JRadioButton rbDates;
	private javax.swing.JRadioButton rbIncludeAll;
	private javax.swing.JTextField txtEndDate;
	private javax.swing.JTextField txtStartDate;
	private javax.swing.JTextField txtString;
	// End of variables declaration//GEN-END:variables
}
