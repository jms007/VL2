package com.extant.vl2;

public enum ListType {

	VENDOR, CONTACT, CUSTOMER;

	public String toString()
	{
		return name().toLowerCase();
	}

}
