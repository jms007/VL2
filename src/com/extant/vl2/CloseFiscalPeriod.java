package com.extant.vl2;

public class CloseFiscalPeriod
{
	/* The CloseFiscalPeriod procedure invokes the following sequence:
	 * 	InitClose(String entity, int oldYear);
	 * 	createClosingBSAccounts();
	 * 	createClosingPLAccounts();
	 * 	createBALFEntries();
	 * 	createCloseReport();
	 */
	
	String entityName;
	int oldYear;
	int newYear;
	
	public void InitClose(VL2Config vl2Config, int oldYear)
	{
		entityName = vl2Config.getEntityName();
		oldYear = vl2Config.getyyyy();
		newYear = oldYear + 1;
	}
}
