/*
 * $Id: ListMan4.java,v 1.2 2006/10/20 05:44:41 jms Exp $
 *
 * Dialog for using and changing lists.  Used by EnterTransaction (for VENDOR.LST)
 * and by Invoice (for CUSTOMER.LST)
 *
 * Provides Insert, Modify, and Delete functions, including upload of modified file
 * to remote server.
 *
 * Replaces SelectFromList - 10-17-06
 *
 * Created on October 16, 2006, 8:21 PM
 */

package com.extant.vl2;

import java.awt.Dimension;
//import java.sql.SQLException;
//import javax.swing.JFrame;
//import javax.swing.JDialog;
//import dbTools.RemoteFileMan;
//import com.extant.dbTools.DBException;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.extant.utilities.LogFile;
import com.extant.utilities.MsgBox;
import com.extant.utilities.Sorts;
import com.extant.utilities.Strings;
import com.extant.utilities.TextDialog;
import com.extant.utilities.UsefulFile;

/**
 *
 * @author jms
 */
public class ListMan4 extends javax.swing.JFrame {
	public ListMan4(VL2Config props, ListType listType)
	{
		this(props, listType, false);
	}

	public ListMan4(VL2Config vl2Config, ListType listType, boolean selectEntry)
	{
		try {
			logger = VL2.logger;
			// if (logger == null) logger = new LogFile();
			// For debugging:
			// logger.setLogLevel( LogFile.DEBUG_LOG_LEVEL );
			initComponents();
			selectEntry = true;
			this.vl2FileMan = vl2Config;
			this.selectEntry = selectEntry;
			this.checkPrinter2 = new CheckPrinter2(vl2Config);
			this.setTitle(vl2Config.getEntityName() + listType + " List");
			Dimension windowSize = new Dimension(450, 500);
			setSize(windowSize);
			java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
			setup(listType);
		} catch (IOException iox) {
			statusBar.setText("File " + fileName + " is not accessable");
			btnAdd.setEnabled(false);
			btnOK.setEnabled(true);
			btnDelete.setEnabled(false);
			btnSave.setEnabled(false);
			btnUpdate.setEnabled(false);
		}
	}

	public void setDelim(String delim)
	{
		this.delim = delim;
	}

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		northPanel = new javax.swing.JPanel();
		centerPanel = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		comboNames = new javax.swing.JComboBox();
		txtLine1 = new javax.swing.JTextField();
		txtLine2 = new javax.swing.JTextField();
		txtLine3 = new javax.swing.JTextField();
		txtLine4 = new javax.swing.JTextField();
		jPanel1 = new javax.swing.JPanel();
		btnAdd = new javax.swing.JButton();
		btnUpdate = new javax.swing.JButton();
		btnDelete = new javax.swing.JButton();
		btnSave = new javax.swing.JButton();
		statusBar = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		btnHelp = new javax.swing.JButton();
		btnOK = new javax.swing.JButton();
		southPanel = new javax.swing.JPanel();

		northPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		getContentPane().add(northPanel, java.awt.BorderLayout.NORTH);

		centerPanel.setPreferredSize(new java.awt.Dimension(380, 100));
		centerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		jPanel2.setMaximumSize(new java.awt.Dimension(32767, 10));
		centerPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, -1));

		comboNames.setActionCommand("");
		comboNames.setMaximumSize(new java.awt.Dimension(400, 20));
		comboNames.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				comboNamesItemStateChanged(evt);
			}
		});
		comboNames.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				comboNamesKeyTyped(evt);
			}
		});
		centerPanel.add(comboNames, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 350, -1));

		txtLine1.setMaximumSize(new java.awt.Dimension(400, 19));
		txtLine1.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				txtLine1KeyTyped(evt);
			}
		});
		centerPanel.add(txtLine1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 32, 350, -1));

		txtLine2.setMaximumSize(new java.awt.Dimension(400, 19));
		centerPanel.add(txtLine2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 54, 350, -1));

		txtLine3.setMaximumSize(new java.awt.Dimension(400, 19));
		centerPanel.add(txtLine3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 76, 350, -1));

		txtLine4.setMaximumSize(new java.awt.Dimension(400, 19));
		centerPanel.add(txtLine4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 98, 350, -1));

		jPanel1.setMinimumSize(new java.awt.Dimension(400, 20));
		jPanel1.setPreferredSize(new java.awt.Dimension(400, 20));

		btnAdd.setText("Add New");
		btnAdd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnAddActionPerformed(evt);
			}
		});
		jPanel1.add(btnAdd);

		btnUpdate.setText("Update");
		btnUpdate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnUpdateActionPerformed(evt);
			}
		});
		jPanel1.add(btnUpdate);

		btnDelete.setText("Delete");
		btnDelete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnDeleteActionPerformed(evt);
			}
		});
		jPanel1.add(btnDelete);

		btnSave.setText("Save to File");
		btnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnSaveActionPerformed(evt);
			}
		});
		jPanel1.add(btnSave);

		centerPanel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 380, 40));

		statusBar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		statusBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		statusBar.setText("Status Bar");
		statusBar.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
		statusBar.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		statusBar.setPreferredSize(new java.awt.Dimension(100, 16));
		statusBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		centerPanel.add(statusBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(171, 9, -1, -1));

		jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		btnHelp.setText("Help");
		btnHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnHelpActionPerformed(evt);
			}
		});
		jPanel3.add(btnHelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, -1, -1));

		btnOK.setText("OK");
		btnOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnOKActionPerformed(evt);
			}
		});
		jPanel3.add(btnOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 60, -1));

		centerPanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 156, 350, 90));

		getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

		southPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		southPanel.setLayout(new java.awt.BorderLayout());
		getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void btnHelpActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnHelpActionPerformed
		displayHelp();
	}// GEN-LAST:event_btnHelpActionPerformed

	private void txtLine1KeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_txtLine1KeyTyped
		manageButtons();
	}// GEN-LAST:event_txtLine1KeyTyped

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnSaveActionPerformed
		saveToFile();
	}// GEN-LAST:event_btnSaveActionPerformed

	private void btnAddActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnAddActionPerformed
		addNewEntry();
	}// GEN-LAST:event_btnAddActionPerformed

	private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnDeleteActionPerformed
		deleteEntry();
	}// GEN-LAST:event_btnDeleteActionPerformed

	private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnUpdateActionPerformed
		updateEntry();
	}// GEN-LAST:event_btnUpdateActionPerformed

	private void btnOKActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_btnOKActionPerformed
		// Close the editing window & return the selected entry (if any)
		if (dataHasChanged) {
			MsgBox msgBox = new MsgBox(this, "Confirm Close",
					"The changes you made will be lost.\nDo you want to close anyway?", MsgBox.YES_NO);
			if (msgBox.getCommand().equalsIgnoreCase("yes"))
				this.dispose();
		} else {
			if (selectEntry) {
				String[] payeeData = getSelectedEntry();
				checkPrinter2.setPayeeData(payeeData);
				this.dispose();
			}
		}
	}// GEN-LAST:event_btnOKActionPerformed

	private void comboNamesItemStateChanged(java.awt.event.ItemEvent evt)
	{// GEN-FIRST:event_comboNamesItemStateChanged
		// Console.println( "ItemStateChanged: " + (String)evt.getItem() + " " +
		// evt.getStateChange() );
		if (initializing)
			return;
		if (evt.getStateChange() == evt.SELECTED) {
			fillLines();
			currentIndex = comboNames.getSelectedIndex();
			manageButtons();
		}
	}// GEN-LAST:event_comboNamesItemStateChanged

	private void comboNamesKeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_comboNamesKeyTyped
		evt.consume();
		char c = evt.getKeyChar();
		logger.logDebug("typed: " + c);
		if (c == '\b') {
			if (comboBuffer.length() > 0)
				comboBuffer = comboBuffer.substring(0, comboBuffer.length() - 1);
			else
				return;
		} else if (c == evt.VK_ESCAPE)
			comboBuffer = "";
		else
			comboBuffer += c;
		logger.logDebug("comboBuffer=" + comboBuffer);
		if (comboBuffer.length() == 0) {
			comboNames.setSelectedIndex(-1);
			fillLines();
			txtLine1.requestFocus();
			return;
		}
		for (int i = 0; i < names.size(); ++i) {
			String name = ((String) names.elementAt(i)).toLowerCase();
			if (name.startsWith(comboBuffer.toLowerCase())) {
				comboNames.setSelectedIndex(i);
				break;
			}
		}
	}// GEN-LAST:event_comboNamesKeyTyped

	void setup(ListType listType) throws IOException
	{
		String fileName = null;
		switch (listType) {
		case CONTACT:
			fileName = vl2FileMan.getContactsList();
			break;
		case CUSTOMER:
			fileName = vl2FileMan.getCustomerList();
			break;
		case VENDOR:
			fileName = vl2FileMan.getVendorList();
			break;
		}
		String image;
		entries = new Vector<String[]>(50, 50);
		if (!new File(fileName).exists()) {
			String msg = "File " + fileName + " not accessible.";
			logger.log(msg);
			statusBar.setText(msg);
			return;
		}
		UsefulFile file = new UsefulFile(fileName, "r");
		while (!file.EOF()) {
			image = file.readLine(UsefulFile.ALL_WHITE);
			// if ( image.length() < 3 || image.startsWith( "*" ) ) continue;
			String linesIn[] = new String[4];
			String lines[] = new String[4];
			linesIn = Strings.split(image, delim);
			int j = 0;
			for (int i = 0; i < lines.length; ++i) {
				if (i >= linesIn.length)
					lines[j++] = "";
				else if (!Strings.isValidInt(linesIn[i].trim()))
					lines[j++] = linesIn[i].trim();
			}
			while (j < lines.length)
				lines[j++] = "";
			entries.addElement(lines);
		}
		logger.logDebug("# of initial entries=" + entries.size());
		file.close();
		dataHasChanged = false;
		entries = organize(entries);
		logger.logDebug("after organize # of entries=" + entries.size() + " # of names=" + names.size());
		comboNames.setSelectedIndex(-1);
		comboBuffer = "";
		statusBar.setText("");
		// VL2MenuFrame.getContentPane.add(ListMan4());
		// this.pack();
		// this.setVisible(true);

		manageButtons();
	}

	Vector<String[]> organize(Vector<String[]> items)
	{
		names = new Vector<String>(items.size());
		for (int i = 0; i < items.size(); ++i)
			names.addElement(((String[]) items.elementAt(i))[0]);
		int sortP[] = Sorts.sort(names);
		names.removeAllElements();
		Vector<String[]> newEntries = new Vector<String[]>(items.size());
		for (int i = 0; i < sortP.length; ++i) {
			String entry[] = new String[4];
			entry = (String[]) items.elementAt(sortP[i]);
			newEntries.addElement(entry);
			names.addElement(entry[0]);
		}
		// Update the combo box
		initializing = true;
		comboNames.removeAllItems();
		for (int i = 0; i < names.size(); ++i)
			comboNames.addItem((String) names.elementAt(i));
		comboNames.setSelectedIndex(-1); // to ensure an event when we select something
		initializing = false;
		return newEntries;
	}

	void saveToFile()
	{
		try {
			String entry[];
			// Do Backup
			String backupName = Strings.getDerivedFileName(fileName, "BAK");
			logger.logDebug("backupName=" + backupName);
			UsefulFile.copy(fileName, backupName);

			// Now save the new file
			// UsefulFile.delete( fileName ); // Get a fresh start
			UsefulFile newFile = new UsefulFile(fileName, "w");
			for (int i = 0; i < entries.size(); ++i) {
				String image = "";
				entry = (String[]) entries.elementAt(i);
				for (int j = 0; j < entry.length; ++j) {
					if (j > 0)
						image += delim;
					image += entry[j];
					if (entry[j].length() == 0)
						image += " "; // probably no longer required
				}
				newFile.println(image);
			}
			newFile.rewind();

			// If online, rebuild this file
			// if ( remoteFileMan != null )
			// {
			// MsgBox msgBox = new MsgBox( this,
			// "File Uploading", "Please wait while the file is uploaded ...",
			// MsgBox.NO_BUTTONS );
			// remoteFileMan.rebuildFile( newFile );
			// msgBox.dispose();
			// }
			// newFile.close(); // just in case

			statusBar.setText("List has been saved.");
			comboNames.setSelectedIndex(-1);
			fillLines();
			dataHasChanged = false;
			manageButtons();
		} catch (IOException iox) {
			statusBar.setText("Save Failed: " + iox.getMessage());
			logger.logFatal("Save Failed: " + iox.getMessage());
		}
		// catch (SQLException sqlx)
		// { statusBar.setText( "Remote rebuild failed: " + sqlx.getMessage() );
		// logger.logFatal( "Remote rebuild failed: " + sqlx.getMessage() );
		// }
		// catch (DBException dbx)
		// { statusBar.setText( "Remote rebuild failed: " + dbx.getMessage() );
		// logger.logFatal( "Remote rebuild failed: " + dbx.getMessage() );
		// }
	}

	void addNewEntry()
	{
		if (getCurrentEntry() == null)
			statusBar.setText("Cannot Add a Blank entry!");
		else {
			entries.addElement(getCurrentEntry());
			entries = organize(entries);
			dataHasChanged = true;
			manageButtons();
		}
	}

	void deleteEntry()
	{
		if (comboNames.getSelectedIndex() < 0)
			statusBar.setText("No Entry is selected");
		else {
			entries.removeElementAt(comboNames.getSelectedIndex());
			entries = organize(entries);
			dataHasChanged = true;
			comboNames.setSelectedIndex(-1);
			fillLines();
			manageButtons();
		}
	}

	void updateEntry()
	{
		if (comboNames.getSelectedIndex() < 0)
			statusBar.setText("No Entry is selected");
		else {
			entries.setElementAt(getCurrentEntry(), comboNames.getSelectedIndex());
			entries = organize(entries);
			dataHasChanged = true;
			manageButtons();
		}
	}

	public String[] getSelectedEntry()
	{
		if (getCurrentEntry() == null) {
			statusBar.setText("No Entry is selected");
			return null;
		}
		logger.logDebug("getSelectedEntry returning " + getCurrentEntry()[0]);
		statusBar.setText("returning " + getCurrentEntry()[0]);
		return getCurrentEntry();
	}

	public String[] returnSelectedEntry()
	{
		return getSelectedEntry();
	}

	public String[] getPayee()
	{
		return getCurrentEntry();
	}

	public String[] getCustomer()
	{
		return getCurrentEntry();
	}

	public String[] getContact()
	{
		return getCurrentEntry();
	}

	public String[] getVendor()
	{
		return getCurrentEntry();
	}

	public String[] getCurrentEntry()
	{ // Builds an entry from the dialog lines, or null if they are all blank
		String vendor[] = new String[4];
		vendor[0] = txtLine1.getText().trim();
		vendor[1] = txtLine2.getText().trim();
		vendor[2] = txtLine3.getText().trim();
		vendor[3] = txtLine4.getText().trim();
		String x = "";
		for (int i = 0; i < vendor.length; ++i)
			x += vendor[i];
		if (x.length() == 0)
			return null; // nothing was entered (there is no cancel button)
		return vendor;
	}

	void manageButtons()
	{
		logger.logDebug("[manageButtons] selectedIndex=" + comboNames.getSelectedIndex());
		logger.logDebug("    currentEntry is null: " + (getCurrentEntry() == null));
		btnAdd.setEnabled(getCurrentEntry() != null && comboNames.getSelectedIndex() < 0);
		btnUpdate.setEnabled(getCurrentEntry() != null && comboNames.getSelectedIndex() >= 0);
		btnDelete.setEnabled(comboNames.getSelectedIndex() >= 0);
		btnSave.setEnabled(dataHasChanged);
		btnOK.setEnabled(getCurrentEntry() != null);
	}

	void clearLines()
	{
		txtLine1.setText("");
		txtLine2.setText("");
		txtLine3.setText("");
		txtLine4.setText("");
	}

	void fillLines()
	{
		int p = comboNames.getSelectedIndex();
		if (p < 0)
			clearLines();
		else {
			txtLine1.setText(((String[]) entries.elementAt(p))[0]);
			txtLine2.setText(((String[]) entries.elementAt(p))[1]);
			txtLine3.setText(((String[]) entries.elementAt(p))[2]);
			txtLine4.setText(((String[]) entries.elementAt(p))[3]);
		}
	}

	void displayHelp()
	{
		TextDialog textDialog = new TextDialog(this, true);
		textDialog.doSetTitle("Help for File Manager");
		textDialog.setText(helpText);
		textDialog.setVisible(true);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */

	/*****
	 * FOR TESTING ***** public static void main(String args[]) { try { final Clip
	 * clip = new Clip(args, new String[] { "f=E:\\REMOTES\\QUITO\\GL08\\VENDOR.LST"
	 * , "d=|" } );
	 * 
	 * java.awt.EventQueue.invokeLater(new Runnable() { public void run() { ListMan4
	 * listMan = new ListMan4(clip.getParam("f"));
	 * listMan.setDelim(clip.getParam("d")); listMan.setVisible(true); } }); } catch
	 * (UtilitiesException ux) { Console.println( ux.getMessage() ); } } /
	 *****/

	LogFile logger;
	VL2Config vl2FileMan;
	String fileName;
	// RemoteFileMan remoteFileMan;
	String delim = "|"; // Default
	Vector<String> names;
	Vector<String[]> entries;
	boolean initializing;
	String comboBuffer;
	boolean dataHasChanged = false;
	int currentIndex = -1;
	boolean selectEntry = false;
	CheckPrinter2 checkPrinter2;

	String helpText = "             SELECTING AN ENTRY\n\n"
			+ "Expand the drop-down list of entries and click on the one you want to\n"
			+ "select, OR click on the drop-down list, type Esc to clear the list\n"
			+ "then type the first few characters of the desired entry, and click\n" + "on the one you want.\n"
			+ "If you are preparing a check, click 'OK' to pass the selected vendor\n" + "to the check writer." + "\n\n"
			+ "         EDITING THE CUSTOMER & VENDOR FILES\n\n" + "To Edit an existing entry:\n"
			+ "1.  Select the entry so that its information is displayed\n"
			+ "2.  Make the desired changes by editing the displayed lines\n"
			+ "    (do not type in the drop-down box)\n" + "3.  Click Update\n" + "\n"
			+ "To Delete an existing entry:\n" + "1.  Select the entry to be deleted\n" + "2.  Click Delete\n" + "\n"
			+ "To Add a new entry:\n" + "1.  Type Esc (to clear the entry lines)\n"
			+ "2.  Type the name and address of the new entry in the blank lines\n"
			+ "    (do not type in the drop-down box)\n" + "3.  Click Add New\n" + "\n"
			+ "After you make any of these changes (edit, delete, or add),\n"
			+ "the 'Save to File' button will be enabled.\n" + "You can continue making other changes, if needed.\n"
			+ "When you are finished making changes, click 'Save to File'\n"
			+ "This will update the file on your local system and transfer\n" + "the updated file to the server.\n\n"
			+ "If you click 'OK' or attempt to close the window without saving,\n"
			+ "the program will remind you that there are unsaved changes.\n\n"
			+ "When you are ready to exit, either click 'OK' (if enabled), or\n"
			+ "click the 'X' box in the title bar to close the window.";

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnAdd;
	private javax.swing.JButton btnDelete;
	private javax.swing.JButton btnHelp;
	private javax.swing.JButton btnOK;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnUpdate;
	private javax.swing.JPanel centerPanel;
	private javax.swing.JComboBox comboNames;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel northPanel;
	private javax.swing.JPanel southPanel;
	private javax.swing.JLabel statusBar;
	private javax.swing.JTextField txtLine1;
	private javax.swing.JTextField txtLine2;
	private javax.swing.JTextField txtLine3;
	private javax.swing.JTextField txtLine4;
	// End of variables declaration//GEN-END:variables

}
