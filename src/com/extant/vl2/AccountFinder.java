/*
 * AccountFinder.java
 *
 * To support JComboBox's used for selection of accounts.
 * [Used in EnterTransactions, ShowBal, Analyze]
 * Initially the combo box contains all accounts, in account number order.
 * Each time a character is typed in the combo box:
 *    If the character is backspace, the most recent character (if any) is removed
 *    If the character is escape, all characters are removed, and the combo box is re-initialized.
 *    Otherwise the character is added
 *    Then:
 *    All items are removed from the combo box, and
 *    All matching accounts are inserted in the combo box
 *       (a) If the first character typed is numeric
             matching accounts are those whose normalized account numbers begin with the character(s) typed,
 *           and they are sorted by normalized account number
 *       (b) If the first character typed is non-numeric
 *           matching accounts are those whose descriptions begin with the character(s) typed,
 *           and they are sorted by description
 *           
 *    In parallel, this class receives TreeSelectionEvent's and forces the comboBox to
 *    display the selected account as the current selected item.
 *
 * Created on August 24, 2006, 10:58 AM
 */

package com.extant.vl2;

import com.extant.utilities.Sorts;
import com.extant.utilities.Strings;
import com.extant.utilities.LogFile;
//import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

/**
 *
 * @author jms
 */

public class AccountFinder implements TreeSelectionListener
{
	LogFile logger;
	Chart chart;
	ChartTree tree;
	JComboBox comboBox;
	StringBuffer buffer;

	public AccountFinder(Chart chart, ChartTree tree, LogFile logger, JComboBox comboBox)
	{
		this.chart = chart;
		this.tree = tree;
		this.logger = logger;
		this.comboBox = comboBox;
		// For debugging:
		// this.logger.setLogLevel( LogFile.DEBUG_LOG_LEVEL );
		setup();
	}

	void setup()
	{
		buffer = new StringBuffer(1000);
		initializeComboBox();
		// tree.addTreeSelectionListener(this);
	}

	public void initializeComboBox()
	{
		comboBox.removeAllItems();
		Enumeration accounts = chart.acctsByNumber();
		while (accounts.hasMoreElements())
			comboBox.addItem((Account) accounts.nextElement());
		comboBox.setSelectedIndex(-1);
	}

	public Account[] find(String clue)
	{
		logger.logDebug("AccountFinder.find( " + clue + " )");
		Enumeration accounts = chart.acctsByNumber();
		Vector<Account> answerV = new Vector<Account>(10, 10);
		while (accounts.hasMoreElements())
		{
			Account account = (Account) accounts.nextElement();
			if (Strings.isDecimalDigit(clue.charAt(0)))
			{ // Look for matching account numbers
				if (Strings.match(clue + "*", account.getAccountNo()))
					answerV.addElement(account);
				else
				// Accounts are in order, so if we stopped finding matches, we must be done
				if (answerV.size() > 0)
					break;
			} else
			{ // Look for matching account descriptions
				if (Strings.match(clue + "*", account.getTitle()))
					answerV.addElement(account);
			}
		}
		// Order the output list either by account number or description, depending on
		// first char of clue
		int ip[];
		if (Strings.isAlpha(clue.charAt(0)))
		{
			String descrs[] = new String[answerV.size()];
			for (int i = 0; i < descrs.length; ++i)
				descrs[i] = ((Account) answerV.elementAt(i)).getTitle();
			ip = Sorts.sort(descrs);
		} else
			ip = Sorts.sort(answerV); // uses the toString method in Account
		Account answer[] = new Account[answerV.size()];
		for (int i = 0; i < answerV.size(); ++i)
			answer[i] = (Account) answerV.elementAt(ip[i]);
		return answer;
	}

	public void reset()
	{
		buffer.setLength(0);
		initializeComboBox();
	}

	public void processKeyEvent(java.awt.event.KeyEvent evt)
	{
		if (evt.getKeyChar() == '\b')
		{ // don't remove these braces ;)
			if (buffer.length() > 0)
				buffer.deleteCharAt(buffer.length() - 1);
		} else if (evt.getKeyChar() == java.awt.event.KeyEvent.VK_ESCAPE)
		{
			buffer.setLength(0);
			initializeComboBox();
			return;
		} else
			buffer.append(evt.getKeyChar());
		// Console.println( "AccountFinder.processKeyEvent( " + evt.getKeyChar() + " )
		// buffer=" + buffer.toString() );

		if (buffer.length() == 0)
			initializeComboBox();
		else
		{
			comboBox.removeAllItems();
			Account accounts[] = find(buffer.toString());
			for (int i = 0; i < accounts.length; ++i)
				comboBox.addItem(accounts[i]);
		}
		if (comboBox.getItemCount() > 0)
		{
			comboBox.setSelectedIndex(0);
			comboBox.setPopupVisible(true);
		}
	}
	/*
	 * tree.addTreeSelectionListener(new TreeSelectionListener() { public void
	 * valueChanged(TreeSelectionEvent e) { DefaultMutableTreeNode node =
	 * (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	 * 
	 * // if nothing is selected if (node == null) return;
	 * 
	 * // retrieve the node that was selected Object nodeInfo =
	 * node.getUserObject(); ... // React to the node selection. ... } });
	 */

	public void valueChanged(TreeSelectionEvent evt)

	{
		TreePath newLeadSelectionPath = evt.getNewLeadSelectionPath();
		logger.logDebug("newLeadSelectionPath=" + newLeadSelectionPath);
	}

	/**
	 * @param args
	 *            the command line arguments AccountFinder d = accounting directory
	 *            f = chart file name x = what to look for
	 */
	/*****
	 * FOR TESTING ***** // This will test the matching logic, but not the GUI
	 * helper functions // (see AccountFinderTest) public static void main(String[]
	 * args) { try { Clip clip = new Clip( args, new String[] {
	 * "d=E:REMOTES\\QUITO\\GL08\\", "f=CHART.XML", "x=HANGAR" } ); if (
	 * clip.getParamCount() != 3 ) { Console.println( "Use: AccountFinder
	 * <accounting-dir> <chart filename> <clue>" ); return; } Chart chart = new
	 * Chart(); chart.init( clip.getParam( "d" ) + clip.getParam( "f" ) ); LogFile
	 * logger = new LogFile(); logger.setLogLevel( LogFile.DEBUG_LOG_LEVEL );
	 * Account answer[] = new AccountFinder( chart, logger, null ).find(
	 * clip.getParam( "x" ) ); if ( answer == null ) { Console.println( "No match
	 * found." ); return; } for ( int i=0; i<answer.length; ++i) Console.println(
	 * "[" + i + "] " + answer[i] ); } catch (UtilitiesException ux ) {
	 * Console.println( ux.getMessage() ); } catch (IOException iox) {
	 * Console.println( iox.getMessage() ); } catch (VLException vlx) {
	 * Console.println( vlx.getMessage() ); } } /
	 *****/

}
