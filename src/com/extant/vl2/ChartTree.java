/*
 * ChartTree.java
 *
 * Created on September 9, 2006, 8:49 AM
 */

package com.extant.vl2;

import com.extant.utilities.*;
import java.io.File;
import java.io.IOException;
//import java.util.StringTokenizer;
import java.awt.Point;
import java.awt.Dimension;
import javax.swing.JTree;
import javax.swing.tree.*;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

//import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
//import vl2.Account;
//import vl2.Chart;
//import vl2.ChartElement;
//import vl2.ComputeTotals;
//import vl2.GLCheck;
//import vl2.GLEntry;
//import vl2.ParseDocument;
//import vl2.VL2Glob;
//import vl2.VLException;
//import vl2.VLUtil;

/**
 *
 * @author jms
 */
/*
 * This class (1) builds a Jtree representation of the chart, (2) puts the tree
 * into a scroll pane, and (3) enables single-node selection, but does not (1)
 * specify the location or the size of its display, (2) make the tree visible,
 * or (3) enable tree selection events beyond this class.
 * 
 * Use class utilities.DisplayTree to position and size the frame and display
 * the tree.
 *
 * Classes that need to use the Chart Tree node selection functionality must
 * declare themselves to be listeners to node-selection events: (1) implements
 * TreeSelectionListener (2) put tree in parameter list (3)
 * tree.addSelectionListener(this); (4) add method
 * valueChanged(TreeSelectionEvent e) to take desired action
 */

public class ChartTree implements MouseListener // ,TreeSelectionListener
{
	static final String NODE_DESCR_SPEC = "no, title, type";
	DefaultTreeModel treeModel;
	ChartElement[] elementList;
	static JTree tree;
	ChartTreeNode root;
	LogFile logger = VL2.logger;
	Chart chart = VL2.chart;
	ChartTreeNode selectedNode = null;
	EventListenerList listenerList = new EventListenerList();

	ChartTree(VL2Config vl2Config, Chart chart, LogFile logger)
			throws ParserConfigurationException, SAXException, IOException, VLException
	{
		this.logger = logger;
		if (logger == null) {
			System.out.println("ChartTree constructor: logger is null");
			System.exit(1);
		}
		// For debugging:
		// this.logger.setLogLevel( LogFile.DEBUG_LOG_LEVEL );

		// if ( nodeDescrSpec.contains( "amount" ) )
		// {
		// String workDir = props.getString( "WorkDir" );
		// String glFilename = props.getString( "GLFile" );
		// logger.logDebug("ready to call extractBalances: "+glFilename);
		// VLUtil.extractBalances( glFilename, chart, begin, end, logger );
		// new ComputeTotals( props, chart, begin, end, 0, "", logger );
		tree = buildTree(chart.elementList);
	}

	// Helper method for getting the element list for a tree with no amounts
	// (does not require an initialized Chart)

	/*
	 * This method is not used in the current (1-6-2018) implementation. public
	 * ChartElement[] initFromXML(String chartFilename) throws
	 * ParserConfigurationException, SAXException, IOException { // For debugging
	 * logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
	 * 
	 * logger.log("ChartTree.initFromXML"); ParseDocument pd = new ParseDocument(
	 * chartFilename, logger ); elementList = pd.getElementList();
	 * logger.log("initFromXML returning elementList of length "+
	 * elementList.length); logger.setLogLevel(LogFile.NORMAL_LOG_LEVEL); return
	 * elementList; }
	 */

	private JTree buildTree(ChartElement elementList[]) throws VLException
	{
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		root = new ChartTreeNode(elementList[0]);
		treeModel = new DefaultTreeModel(root);
		build(elementList, 1, root, 1);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(this);
		// tree.addTreeSelectionListener( this );
		// The default is for button 1, double-click to expand/collapse the selected
		// node, unless it is a leaf.
		// So we alter this behavior to require a triple-click on button 1 to do that,
		// so we can use
		// the double-click for our messages (see mouseClicked() below)
		tree.setToggleClickCount(3);
		return tree;
	}

	private int build(ChartElement elementList[], int index, ChartTreeNode parent, int level) throws VLException
	{
		logger.logDebug("entering build  index=" + index);
		while (elementList[index].getLevel() == level) {
			logger.logDebug("processing " + elementList[index].toString() + "  index=" + index + "  level=" + level);
			if (isElement(elementList[index], new String[] { "chart", "section", "group" })) {
				logger.logDebug("adding expandable node to " + parent.toString() + ": " + elementList[index]);
				ChartTreeNode x = new ChartTreeNode(elementList[index]);
				parent.add(x);
				index = build(elementList, index + 1, x, level + 1);
				logger.logDebug("continuing build  index=" + index + "  level=" + level);
				if (index >= elementList.length)
					return index;
			} else { // Either an Account or a Total element
				logger.logDebug("processing " + elementList[index].toString());
				String title = elementList[index].getAttribute("title");
				if (!title.startsWith("*****")) { // Bypass warnings
					logger.logDebug("adding node to " + parent.toString() + ": " + elementList[index]);
					ChartTreeNode accountNode = new ChartTreeNode(elementList[index]);
					parent.add(accountNode);

					/*****
					 * { // To include transactions in the tree: // If this is an account element,
					 * use the node just created (accountNode) as parent // and add the items in
					 * GLEntries from that account as new DefaultMutableTreeNode's // formatted with
					 * simple String's // This block (marked with / 5 stars: ***** ) can be removed
					 * if you do not want this feature if ( isElement( elementList[index], new
					 * String[] { "account" } ) ) { Account account = chart.getAccount(
					 * elementList[index].getAccountIndex() ); for (int i=0;
					 * i<account.glEntries.size(); ++i) { GLEntry glEntry =
					 * account.glEntries.elementAt( i ); String transString =
					 * glEntry.getJulianDate().toString( "mm-dd") + " " + Strings.formatPennies(
					 * glEntry.getLongAmount(), "," ) + " " + glEntry.getDescr(); // These nodes are
					 * not ChartTreeNode's ... maybe a problem somewhere ... DefaultMutableTreeNode
					 * transNode = new DefaultMutableTreeNode( transString ); accountNode.add(
					 * transNode ); } } } /
					 *****/

				}
				if (++index >= elementList.length)
					return index;
			}
		}
		logger.logDebug("returning from build  index=" + index + "  level=" + level);
		return index;
	}

	public ChartElement[] getElementList()
	{
		return elementList;
	}

	private String formatNode(ChartElement element, String nodeDescrSpec) throws VLException
	{
		String nodeData = "";
		// There are no options for the text-only elements
		if (element.getName().equals("chart") || element.getName().equals("section")
				|| element.getName().equals("group"))
			return element.getAttribute("title");

		String attributeValue = null;
		if (nodeDescrSpec.contains("no")) {
			attributeValue = element.getAttribute("no");
			if (attributeValue != null)
				nodeData += "[" + attributeValue + "] ";
		}
		if (nodeDescrSpec.contains("title")) {
			attributeValue = element.getAttribute("title");
			if (attributeValue != null)
				nodeData += attributeValue + " ";
		}
		if (nodeDescrSpec.contains("type")) {
			attributeValue = element.getAttribute("type");
			if (attributeValue != null)
				nodeData += "'" + attributeValue + "' ";
		}

		if (element.getName().equals("account") || element.getName().equals("total")) {
			// String formatAmount = "??1";
			if (nodeDescrSpec.contains("amount")) {
				long amount = 0L;
				if (element.getName().equals("account"))
					amount = chart.getAccount(element.getAccountIndex()).getEndBal();
				else if (element.getName().equals("total"))
					amount = element.getTotal();
				String type = element.getAttribute("type");
				if (type.equals("L") || type.equals("I") || type.equals("R"))
					amount = -amount;
				nodeData += "$" + Strings.formatPennies(amount, chart.getDollarFormat());
			}
			logger.logDebug("nodeData='" + nodeData + "'");
			return nodeData;
		}
		return element.toString();
	}

	// From TreeSelectionListener:
	// public void valueChanged( TreeSelectionEvent e )
	// {
	// //System.out.println( "TreeSelectionListener.valueChanged");
	// if (e.getNewLeadSelectionPath() == null )
	// {
	// Console.println( "Selection is null");
	// selectedNode = null;
	// return;
	// }
	// TreePath newSelection = e.getNewLeadSelectionPath();
	// selectedNode = (ChartTreeNode)newSelection.getLastPathComponent();
	// Console.println( "TreeSelectionListener: " + selectedNode.toString() );
	// }

	// From MouseListener: (there is an adapter, but I don't know how to use it)
	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mouseClicked(MouseEvent e)
	{
		int button = e.getButton();
		int clickCount = e.getClickCount();
		if (selectedNode != null) {
			// Console.println( "dispatch button " + button + " " + clickCount + " click(s)
			// on " + selectedNode.toString() );
			// Send message only if this is an account node
			// if ( Strings.regexMatch( "\\[\\d+\\].*", selectedNode.toString() ) ) //
			// doesn't work for acct/subacct
			// regex for normalized account numbers with brackets followed by other stuff:
			// "(\\[\\d*\\])|(\\[\\d*/[a-zA-Z0-9]*\\]).*"
			if (selectedNode.toString().contains("[")) // this is probably good enough
				firePropertyChange(button + ":" + clickCount + ":" + selectedNode.toString());
			// if ( button == 1 && e.getClickCount() == 2 )
			// { // left mouse button (this will expand/collapse the selected node, unless
			// it is a leaf)
			// Console.println( "dispatch button 1 doubleClick on " +
			// selectedNode.toString() );
			// firePropertyChange( "1:2:" + selectedNode.toString() );
			// e.consume();
			// }
			// else if ( button == 2 )
			// { // mouse "wheel" button
			// Console.println( "dispatch button 2 " + e.getClickCount() + " click on " +
			// selectedNode.toString() );
			// firePropertyChange( "2:"+ e.getClickCount() + ":" + selectedNode.toString()
			// );
			// }
			// else if ( button == 3 )
			// { // right mouse button
			// Console.println( "dispatch button 3 " + e.getClickCount() + " click on " +
			// selectedNode.toString() );
			// firePropertyChange( "3:1:" + selectedNode.toString() );
			// }
		}
	}

	// This is our homemade inter-class messaging service, piggybacking on
	// PropertyChange Events
	protected void firePropertyChange(String message)
	{
		PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, "SELECT", null, message);
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				// Lazily create the event:
				// if (propertyChangeEvent == null)
				// fooEvent = new FooEvent(this);
				((PropertyChangeListener) listeners[i + 1]).propertyChange(propertyChangeEvent);
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listenerList.add(PropertyChangeListener.class, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listenerList.remove(PropertyChangeListener.class, listener);
	}

	public static boolean isElement(ChartElement element, String[] elementNames)
	{
		for (int i = 0; i < elementNames.length; ++i)
			if (element.name.equals(elementNames[i]))
				return true;
		return false;
	}

	public JTree getJTree()
	{
		return tree;
	}

	public boolean isLeaf(Object node)
	{
		return ((ChartTreeNode) node).isLeaf();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	/***** FOR TESTING *****/
	public static void main(String[] args)
	{
		try {
			Clip clip = new Clip(args,
					new String[] { "d=E:\\ACCOUNTING\\EXTANT\\GL17\\", "p=E:\\ACCOUNTING\\EXTANT\\EXTANT.properties" });
			LogFile logger = new LogFile();
			logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
			String workDir = clip.getParam("d");
			XProperties props = new XProperties(clip.getParam("p"));
			if (!workDir.endsWith(File.separator))
				workDir += File.separator;
			String chartFile = workDir + "CHART.XML";
			String glFilename = workDir + "GL0010.DAT";
			Chart chart = new Chart();
			chart.init(chartFile, logger);
			// GLCheck glCheck = new GLCheck();
			logger.logDebug("ChartTree.main: glFilename=" + glFilename);
			// glCheck.checkTokenFile( glFilename );
			// Julian begin = glCheck.getEarliestDate();
			// Julian end = glCheck.getLatestDate();
			// ( XProperties props, String chartFilename, Julian begin, Julian end, LogFile
			// logger )
			// ChartTree chartTree = new ChartTree(props, chart, logger);
			ChartElement elements[] = chart.getElementList();
			// new DisplayTree( "Chart of Accounts", tree, new Point(200,200), new
			// Dimension(400, 1000) );
			ViewTree viewTree = new ViewTree("View Chart Tree", tree);
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	/***** END OF TEST *****/

	public class ChartTreeNode extends DefaultMutableTreeNode {
		ChartElement element;
		String no;
		String descr;

		public ChartTreeNode(ChartElement element)
		{
			this.element = element;
		}

		public ChartElement getElement()
		{
			return element;
		}

		public String toString()
		{
			try {
				return formatNode(element, NODE_DESCR_SPEC);
			} catch (VLException vlx) {
				return "<ERROR>";
			}
		}
	}
}
