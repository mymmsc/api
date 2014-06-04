package org.mymmsc.api.assembly.samples;

import java.util.Date;

import org.mymmsc.api.assembly.Api;

public class TestDate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "2012-5-6 12:00:00";
		java.sql.Date d1 = Api.valueOf(java.sql.Date.class, s);
		System.out.println(Api.toString(d1));
		java.sql.Timestamp ts = Api.getNow();
		System.out.println(ts);
		String sTime = Api.toString(new Date(),
				"yyyy-MM-dd hh:mm:ss.SSS");
		System.out.println(sTime);
		System.out.println(Api.getWeekDay(new Date()));
		long aa = Api.getTimeInMillis(Api.toDate("20060112012739000",
				"yyyyMMddhhmmssSSS"));
		long a = Api.getTimeInMillis(new Date());

		System.out.println("aa = " + aa + ", a = " + a);
		java.util.Calendar date = java.util.Calendar.getInstance();

		// System.out.println(date.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println(Api.getField(Api.getFirstDayOfWeek(date.getTime()),
				java.util.Calendar.DAY_OF_MONTH));
		java.util.Date date1 = Api.toDate("20040201080910", "yyyyMMddhhmmss");
		java.util.Date date2 = Api.addDate(date1, java.util.Calendar.DAY_OF_YEAR,
				-1000);
		System.out.println(Api.getField(Api.getLastDayOfMonth(date1),
				java.util.Calendar.SECOND));
		System.out.println(Api.toString(date2, "yyyy-MM-dd hh:mm:ss.SSS"));
		System.out.println(Api.diffDays(date1, date2));

	}

}
