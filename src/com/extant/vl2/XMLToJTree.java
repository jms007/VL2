package com.extant.vl2;

import com.extant.utilities.Console;
import com.extant.utilities.LogFile;
import com.extant.utilities.Strings;
import com.extant.utilities.XProperties;
import static com.extant.vl2.ChartTree.isElement;
//import static com.extant.vl2.ChartTree.tree;
import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
//import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author jms
 * 
 *         This class (1) constructs a Document from CHART.XML, (2) builds a
 *         JTree representation of the chart, and (3) returns the JTree.
 *
 */

public class XMLToJTree
// implements TreeSelectionListener, MouseListener
{
	public void getElementList(XProperties props, String chartXMLFilename, LogFile logger)
			throws ParserConfigurationException, SAXException, IOException
	{
		ParseDocument pd = new ParseDocument(chartXMLFilename, logger);
		pd.getElementList();
	}

	public void buildJTree() throws VLException
	{
		jTree = buildTree(elementList);
	}

	// public JTree XMLToJTree( XProperties props, String chartXMLFilename, LogFile
	// logger )
	// throws ParserConfigurationException, SAXException, IOException, VLException
	// {
	// if (logger == null)
	// {
	// System.out.println("XMLToJTree constructor: logger is null");
	// System.exit(1);
	// }
	// this.props = props;
	// this.chartXMLFilename = chartXMLFilename;
	// this.logger = logger;
	// // For debugging:
	// // this.logger.setLogLevel( LogFile.DEBUG_LOG_LEVEL );
	// //elementList = chart.getElementList();
	//
	// //elementList = initFromXML(chartXMLFilename);
	// tree = buildTree(elementList);
	// return tree;
	// }
	//
	// public ChartElement[] initFromXML(String chartXMLFilename)
	// throws ParserConfigurationException, SAXException, IOException
	// {
	// // For debugging
	// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
	//
	// logger.log("ChartTree.initFromXML");
	// ParseDocument pd = new ParseDocument( chartXMLFilename, logger );
	// elementList = pd.getElementList();
	// logger.log("initFromXML returning elementList of length "+
	// elementList.length);
	// logger.setLogLevel(LogFile.NORMAL_LOG_LEVEL);
	// return elementList;
	// }

	public JTree buildTree(ChartElement2 elementList[]) throws VLException
	{
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(elementList[0]);
		treeModel = new DefaultTreeModel(root);
		build(elementList, 1, (ChartTreeNode) root, 1);
		jTree = new JTree(treeModel);

		// tree.addMouseListener(this);
		// tree.addTreeSelectionListener( this );
		// // The default is for button 1, double-click to expand/collapse the selected
		// node, unless it is a leaf.
		// // We alter this behavior to require a triple-click on button 1 to do that,
		// so we can use
		// // the double-click for our messages (see mouseClicked() below)
		// tree.setToggleClickCount(3);
		return jTree;
	}

	private int build(ChartElement2 elementList[], int startIndex, ChartTreeNode parent, int level) throws VLException
	{
		int index = startIndex;
		logger.logDebug("entering build  index=" + index);
		while (elementList[index].getLevel() == level)
		{
			logger.logDebug("processing " + elementList[index].toString() + "  index=" + index + "  level=" + level);
			if (isElement(elementList[index], new String[] { "chart", "section", "group" }))
			{
				logger.logDebug("adding expandable node to " + parent.toString() + ": " + elementList[index]);
				ChartTreeNode x = new ChartTreeNode(elementList[index]);
				parent.add(x);
				index = build(elementList, index + 1, x, level + 1);
				logger.logDebug("continuing build  index=" + index + "  level=" + level);
				if (index >= elementList.length)
					return index;
			} else
			{ // Either an Account or a Total element
				logger.logDebug("processing " + elementList[index].toString());
				String title = elementList[index].getAttribute("title");
				if (!title.startsWith("*****")) // Bypass warnings
				{
					logger.logDebug("adding node to " + parent.toString() + ": " + elementList[index]);
					ChartTreeNode accountNode = new ChartTreeNode(elementList[index]);
					parent.add(accountNode);

					/*****
					 * { // To include transactions in the tree: // If this is an account element,
					 * use the node just created (accountNode) as parent // and add the items in
					 * GLEntries from that account as new DefaultMutableTreeNode's // formatted with
					 * simple String's // This block (marked with / 5 stars: ***** ) can be removed
					 * if you do not want this feature. if ( isElement( elementList[index], new
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

	// From TreeSelectionListener:
	public void valueChanged(TreeSelectionEvent e)
	{
		// System.out.println( "TreeSelectionListener.valueChanged");
		if (e.getNewLeadSelectionPath() == null)
		{
			// Console.println( "Selection is null");
			selectedNode = null;
			return;
		}
		TreePath newSelection = e.getNewLeadSelectionPath();
		selectedNode = (ChartTreeNode) newSelection.getLastPathComponent();
		Console.println("TreeSelectionListener: " + selectedNode.toString());
	}

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

	XProperties props;
	LogFile logger;
	String chartXMLFilename;
	static final String nodeDescrSpec = "no, title, type, beginBal, deltaBal";
	int index;
	ChartElement2[] elementList;
	JTree jTree;
	DefaultTreeModel treeModel;
	ChartTreeNode selectedNode = null;
	EventListenerList listenerList = new EventListenerList();

	/* EMBEDDED CLASS */
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
				return formatNode(element, nodeDescrSpec);
			} catch (VLException vlx)
			{
				return "<ERROR>";
			}
		}

		private String formatNode(ChartElement2 element, String nodeDescrSpec) throws VLException
		{ // There are no options for the text-only elements
			if (element.getName().equals("chart") || element.getName().equals("section")
					|| element.getName().equals("group"))
				return element.getAttribute("title");

			if (element.getName().equals("account") || element.getName().equals("total"))
			{
				// if ( nodeDescrSpec.contains( "amount" ) )
				// {
				// long amount=0L;
				// if ( element.getName().equals( "account" ) )
				// amount = chart.getAccount( element.getAccountIndex() ).getEndBal();
				// else if ( element.getName().equals( "total" ) )
				// amount = element.getTotal();
				// String type = element.getAttribute("type");
				// if ( type.equals( "L" ) || type.equals("I") || type.equals("R"))
				// amount = -amount;
				// formatAmount = Strings.formatPennies( amount, chart.getDollarFormat() );
				// }
				String nodeData = "";
				if (nodeDescrSpec.contains("no"))
					nodeData += "[" + element.getAttribute("no") + "] ";
				if (nodeDescrSpec.contains("title"))
					nodeData += element.getAttribute("title");
				if (nodeDescrSpec.contains("type"))
					nodeData += " '" + element.getAttribute("title") + "'";
				if (nodeDescrSpec.contains("beginBal"))
					nodeData += "begin=" + Strings.formatPennies(element.beginBal);
				if (nodeDescrSpec.contains("deltaBal"))
					nodeData += "delta=" + Strings.formatPennies(element.deltaBal);

				// StringTokenizer st = new StringTokenizer( nodeDescrSpec, "," );
				// while ( st.hasMoreElements() )
				// {
				// String descrSpec = (String)st.nextElement();
				// if ( descrSpec.equals( "no" ) )
				// {
				// if ( element.getAttribute( "no" ).length() > 0 )
				// nodeData += "[" + element.getAttribute( "no" ) + "] ";
				// }
				// else if ( descrSpec.equals( "title" ) )
				// nodeData += element.getAttribute( "title" ) + " ";
				// else if ( descrSpec.equals( "amount" ) )
				// nodeData += formatAmount + " ";
				// //else nodeData += "?" + descrSpec + "? ";
				// }
				return nodeData;
			}
			return element.toString();
		}
	}

}
