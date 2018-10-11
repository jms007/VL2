package com.extant.vl2;

import com.extant.utilities.Console;
import com.extant.utilities.LogFile;
import java.io.IOException;

/**
 *
 * @author jms
 */
@SuppressWarnings("serial")
public class AccountFinderTest extends javax.swing.JFrame
{
	public AccountFinderTest(String dir, Chart chart, LogFile logger)
	{
		// logger = VL2Glob.logger;
		this.dir = dir;
		this.chart = chart;
		initComponents();
		setup();
	}

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		comboBox = new javax.swing.JComboBox();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Account Finder Test");
		setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
		getContentPane().setLayout(new java.awt.FlowLayout());

		comboBox.setPreferredSize(new java.awt.Dimension(300, 25));
		comboBox.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyTyped(java.awt.event.KeyEvent evt)
			{
				comboBoxKeyTyped(evt);
			}
		});
		getContentPane().add(comboBox);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void comboBoxKeyTyped(java.awt.event.KeyEvent evt)
	{// GEN-FIRST:event_comboBoxKeyTyped
		accountFinder.processKeyEvent(evt);
	}// GEN-LAST:event_comboBoxKeyTyped

	private void setup()
	{
		logger = new LogFile();
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		// accountFinder = new AccountFinder( chart, logger, comboBox );
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[])
	{
		LogFile logger = new LogFile();
		String dir = "E:\\REMOTES\\QUITO\\GL08\\";
		Chart chart = new Chart();
		try
		{
			chart.init(dir + "CHART.XML", logger);
		} catch (IOException iox)
		{
			Console.println(iox.getMessage());
		} catch (VLException vlx)
		{
			Console.println(vlx.getMessage());
		}

		new AccountFinderTest(dir, chart, logger).setVisible(true);
	}

	LogFile logger;
	String dir;
	Chart chart;
	AccountFinder accountFinder;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JComboBox comboBox;
	// End of variables declaration//GEN-END:variables

}
