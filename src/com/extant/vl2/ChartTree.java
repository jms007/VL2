/*
 * ChartTree.java
 *
 * Created on September 9, 2006, 8:49 AM
 */

package com.extant.vl2;

import com.extant.utilities.*;
import java.io.File;
import java.io.IOException;
import javax.swing.JTree;
import javax.swing.tree.*;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
 * declare themselves to be listeners to node-selection events: (1) implement
 * TreeSelectionListener (2) put tree in parameter list (3)
 * tree.addSelectionListener(this); and (4) add method
 * valueChanged(TreeSelectionEvent e) to take desired action
 */

public class ChartTree implements MouseListener // ,TreeSelectionListener
{
	static final String NODE_DESCR_SPEC = "no, title, type, balances";
	DefaultTreeModel treeModel;
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
		if (logger == null)
		{
			System.out.println("ChartTree constructor: logger is null");
			System.exit(1);
		}
		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("chart.chartElements size=" + chart.chartElements.size());

		tree = buildTree(chart.chartElements);
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

	private JTree buildTree(Vector<ChartElement2> chartElements) throws VLException
	{
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		root = new ChartTreeNode(chart.chartElements.elementAt(0));
		treeModel = new DefaultTreeModel(root);
		build(chart.chartElements, 1, root, 1);
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

	private int build(Vector<ChartElement2> chartElements, int index, ChartTreeNode parent, int level)
			throws VLException
	{
		logger.logDebug("entering build  index=" + index);
		while (chartElements.elementAt(index).getLevel() == level)
		{
			logger.logDebug("processing " + chart.chartElements.elementAt(index).toString() + "  index=" + index
					+ "  level=" + level);
			if (isElement(chartElements.elementAt(index), new String[] { "chart", "section", "group" }))
			{
				logger.logDebug(
						"adding expandable node to " + parent.toString() + ": " + chartElements.elementAt(index));
				ChartTreeNode x = new ChartTreeNode(chartElements.elementAt(index));
				parent.add(x);
				index = build(chartElements, index + 1, x, level + 1);
				logger.logDebug("continuing build  index=" + index + "  level=" + level);
				if (index >= chartElements.size())
					return index;
			} else
			{ // Either an Account or a Total element
				logger.logDebug("processing " + chartElements.elementAt(index).toString());
				String title = chartElements.elementAt(index).getAttribute("title");
				if (!title.startsWith("*****"))
				{ // Bypass warning
					logger.logDebug("adding node to " + parent.toString() + ": " + chartElements.elementAt(index));
					ChartTreeNode accountNode = new ChartTreeNode(chartElements.elementAt(index));
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
				if (++index >= chartElements.size())
					return index;
			}
		}
		logger.logDebug("returning from build:  index=" + index + "  level=" + level);
		return index;
	}

	// public ChartElement2[] getElementList()
	// {
	// return chart.elementList;
	// }
	//

	private String formatNode(ChartElement2 element, String nodeDescrSpec) throws VLException
	{
		long beginBal = 0L;
		long deltaBal = 0L;
		long totalBal = 0L;
		String attributeValue;
		String nodeData = "";

		// For Debugging
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		// logger.logDebug("Enter formatNode: nodeDescrSpec=" + nodeDescrSpec);

		// There are no options for the text-only elements
		if (element.getName().equals("chart") || element.getName().equals("section")
				|| element.getName().equals("group"))
		{
			nodeData += element.getAttribute("title");
			// logger.logDebug("after chart,section,group: returning " + nodeData);
			return nodeData;
		}

		if (element.getName().equals("account") || element.getName().equals("total"))
		{
			if (nodeDescrSpec.contains("no"))
			{
				attributeValue = element.getAttribute("no");
				if (attributeValue != null)
					nodeData += "[" + attributeValue + "] ";
			}
			if (nodeDescrSpec.contains("title"))
			{
				attributeValue = element.getAttribute("title");
				if (attributeValue != null)
					nodeData += attributeValue + " ";
			}
			if (nodeDescrSpec.contains("type"))
			{
				attributeValue = element.getAttribute("type");
				if (attributeValue != null)
					nodeData += "'" + attributeValue + "' ";
			}

			if (NODE_DESCR_SPEC.contains("balances")
					&& (element.getName().equals("account") || element.getName().equals("total")))
			{
				beginBal = element.beginBal;
				deltaBal = element.deltaBal;
				totalBal = beginBal + deltaBal;
				String type = element.getAttribute("type");
				if (type.equals("L") || type.equals("I") || type.equals("R"))
				{
					beginBal = -beginBal;
					deltaBal = -deltaBal;
					totalBal = -totalBal;
				}
				nodeData += " begin=$" + Strings.formatPennies(beginBal, chart.getDollarFormat());
				nodeData += " delta=$" + Strings.formatPennies(deltaBal, chart.getDollarFormat());
				nodeData += " total=$" + Strings.formatPennies(totalBal, chart.getDollarFormat());
			}
			logger.logDebug("after " + NODE_DESCR_SPEC + ": nodeData=" + nodeData);
		}
		logger.logDebug("after account,total: nodeData='" + nodeData + "'");
		logger.logDebug("returning nodeData=" + nodeData);
		return nodeData;
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
		if (selectedNode != null)
		{
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
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == PropertyChangeListener.class)
			{
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

	public static boolean isElement(ChartElement2 element, String[] elementNames)
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
		try
		{
			Clip clip = new Clip(args,
					new String[] { "d=E:\\ACCOUNTING\\EXTANT\\GL17\\", "p=E:\\ACCOUNTING\\EXTANT\\EXTANT.properties" });
			LogFile logger = new LogFile();
			logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
			String workDir = clip.getParam("d");
			// XProperties props = new XProperties(clip.getParam("p"));
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
			// ChartElement2 elements[] = chart.getElementList();
			// new DisplayTree( "Chart of Accounts", tree, new Point(200,200), new
			// Dimension(400, 1000) );
			// ViewTree viewTree = new ViewTree("View Chart Tree", tree);
		} catch (Throwable x)
		{
			x.printStackTrace();
		}
	}

	/***** END OF TEST *****/

	public class ChartTreeNode extends DefaultMutableTreeNode
	{
		ChartElement2 element;
		String no;
		String descr;

		public ChartTreeNode(ChartElement2 element)
		{
			this.element = element;
		}

		public ChartElement2 getElement()
		{
			return element;
		}

		public String toString()
		{
			try
			{
				return formatNode(element, NODE_DESCR_SPEC);
			} catch (VLException vlx)
			{
				return "<ERROR>";
			}
		}
	}
}
