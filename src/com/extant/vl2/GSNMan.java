package com.extant.vl2;

import java.io.IOException;

import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.UsefulFile;

/**
 *
 * @author jms
 */
public class GSNMan
{

	private static String GSN;
	VL2Config vl2Config;
	LogFile logger;

	public GSNMan(VL2Config vl2Config, LogFile logger)
	{
		try
		{
			this.vl2Config = vl2Config;
			UsefulFile GSNFile = new UsefulFile(vl2Config.getGSNFile());
			String image = GSNFile.readLine();
			if (GSN == null)
			{
				String[] temp;
				temp = image.split("\\|");
				GSN = temp[0];
			}
			GSNFile.close();
		} catch (IOException iox)
		{
			logger.logFatal("File " + vl2Config.getGSNFile() + " not found.");
		}
	}

	public static String getGSN()
	{
		return GSN;
	}

	public String incrementGSN()
	{
		System.out.println("GSN before increment=" + GSN);
		int gsn = Integer.decode(GSN);
		++gsn;
		GSN = new Integer(gsn).toString();
		try
		{
			updateNV();
		} catch (IOException iox)
		{
			System.out.println("GSNMan.incrementGSN: " + iox.getMessage());
			System.exit(200);
		}
		System.out.println("GSN after increment=" + GSN);
		return GSN;
	}

	private void updateNV() throws IOException
	{
		UsefulFile GSNFile = new UsefulFile(vl2Config.getGSNFile(), "w");

		GSNFile.println(GSN + "|" + new Julian().toString("mm/dd/yyyy hh:mm:ss"));
		GSNFile.close();
	}

	// For testing: *****
	// public static void main(String[] args)
	// {
	// logger = new LogFile();
	// String workDir = "E:\\ACCOUNTING\\REMOTES\\XINV\\GL17\\";
	//
	// GSNMan gsnman = new GSNMan();
	// gsnman.init();
	// Console.println("Initial Value = " + gsnman.getGSN());
	// gsnman.incrementGSN();
	// Console.println("after increment: GSN= " + GSN);
	// String newGSN = gsnman.init();
	// Console.println("resulting NV: " + newGSN);
	/*****/
}
