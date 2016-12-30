package org.ybm.bakum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main
{
	static DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	public static void main(String[] args) throws Exception
	{
		System.out.println(dateFormatter.parse("9.122016 5:23"));
	}
}
