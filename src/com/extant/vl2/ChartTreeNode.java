package com.extant.vl2;

import javax.swing.tree.DefaultMutableTreeNode;
import com.extant.utilities.Strings;

public class ChartTreeNode extends DefaultMutableTreeNode
{
	ChartElement element;
	String no;
	String descr;
	static final String nodeDescrSpec = "no, title, type, beginBal, deltaBal";

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
		try
		{
			return formatNode(element, nodeDescrSpec);
		} catch (VLException vlx)
		{
			return "<ERROR>";
		}
	}

	private String formatNode(ChartElement element, String nodeDescrSpec) throws VLException
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

			return nodeData;
		}
		return element.toString();
	}
}
