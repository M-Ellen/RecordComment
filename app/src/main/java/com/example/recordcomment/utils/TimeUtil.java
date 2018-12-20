package com.example.recordcomment.utils;

import android.annotation.SuppressLint;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TimeUtil {

	private static long mStartTime = 0L;

	public static boolean isMoreThan(long disTimes){
		boolean flag = disTimes < System.currentTimeMillis() - mStartTime;
		mStartTime = System.currentTimeMillis();
		return flag;
	}


	public static int getAgeFromBirthTime(String birthTimeString) {
		// 先截取到字符串中的年、月、日
		String strs[] = birthTimeString.trim().split("-");
		if(strs != null && strs.length == 3) {
			int selectYear = Integer.parseInt(strs[0]);
			int selectMonth = Integer.parseInt(strs[1]);
			int selectDay = Integer.parseInt(strs[2]);
			// 得到当前时间的年、月、日
			Calendar cal = Calendar.getInstance();
			int yearNow = cal.get(Calendar.YEAR);
			int monthNow = cal.get(Calendar.MONTH);
			int dayNow = cal.get(Calendar.DATE);

			// 用当前年月日减去生日年月日
			int yearMinus = yearNow - selectYear;
			int monthMinus = monthNow - selectMonth;
			int dayMinus = dayNow - selectDay;

			int age = yearMinus;// 先大致赋值
			if (yearMinus < 0) {// 选了未来的年份
				age = 0;
			} else if (yearMinus == 0) {// 同年的，要么为1，要么为0
				if (monthMinus < 0) {// 选了未来的月份
					age = 0;
				} else if (monthMinus == 0) {// 同月份的
					if (dayMinus < 0) {// 选了未来的日期
						age = 0;
					} else if (dayMinus >= 0) {
						age = 1;
					}
				} else if (monthMinus > 0) {
					age = 1;
				}
			} else if (yearMinus > 0) {
				if (monthMinus < 0) {// 当前月>生日月
				} else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
					if (dayMinus < 0) {
					} else if (dayMinus >= 0) {
						age = age + 1;
					}
				} else if (monthMinus > 0) {
					age = age + 1;
				}
			}
			return age;
		}

		return 0;
	}

	public static String millisecondToTimeFormat(long millisecond) {
		final int ONE_HOUR_MILLISECOND = 1 * 60 * 60 * 1000;
		final int ONE_MINUTE_MILLISECOND = 1 * 60 * 1000;
		final int ONE_SECOND_MILLISECOND = 1 * 1000;
		String time = "";
		long hours;
		long minutes;
		long seconds;
		hours = millisecond / ONE_HOUR_MILLISECOND;
		minutes = millisecond % ONE_HOUR_MILLISECOND / ONE_MINUTE_MILLISECOND;
		seconds = millisecond % ONE_HOUR_MILLISECOND % ONE_MINUTE_MILLISECOND / ONE_SECOND_MILLISECOND;
		if (hours != 0) {
			time = String.format("%02d", hours) + ":";
		}

		if (minutes != 0) {
			time = time + String.format("%02d", minutes) + ":";
		} else {
			time = time + "00:";
		}

		if (seconds != 0) {
			time = time + String.format("%02d", seconds);
		} else {
			time = time + "00";
		}

		return time;
	}

	public static String secondToTimeFormat(long second) {
		final int ONE_HOUR_SECOND = 1 * 60 * 60;
		final int ONE_MINUTE_SECOND = 1 * 60;
		String time = "";
		long hours;
		long minutes;
		long seconds;

		hours = second / ONE_HOUR_SECOND;
		minutes = second % ONE_HOUR_SECOND / ONE_MINUTE_SECOND;
		seconds = second % ONE_HOUR_SECOND % ONE_MINUTE_SECOND;
		if (hours != 0) {
			time = String.format("%02d", hours) + ":";
		}

		if (minutes != 0) {
			time = time + String.format("%02d", minutes) + ":";
		} else {
			time = time + "00:";
		}

		if (seconds != 0) {
			time = time + String.format("%02d", seconds);
		} else {
			time = time + "00";
		}

		return time;
	}

	public static String secondToTimeHMSFormat(long second) {
		final int ONE_HOUR_SECOND = 1 * 60 * 60;
		final int ONE_MINUTE_SECOND = 1 * 60;
		String time = "";
		long hours;
		long minutes;
		long seconds;

		hours = second / ONE_HOUR_SECOND;
		minutes = second % ONE_HOUR_SECOND / ONE_MINUTE_SECOND;
		seconds = second % ONE_HOUR_SECOND % ONE_MINUTE_SECOND;
		if (hours != 0) {
			time = String.format("%02d", hours) + ":";
		}else{
			time = time + "00:";
		}

		if (minutes != 0) {
			time = time + String.format("%02d", minutes) + ":";
		} else {
			time = time + "00:";
		}

		if (seconds != 0) {
			time = time + String.format("%02d", seconds);
		} else {
			time = time + "00";
		}

		return time;
	}

	public static String minuteToTimeFormat(int minute) {
		final int ONE_HOUR_MINUTE = 1 * 60;
		String time = "";
		long hours;
		long minutes;

		hours = minute / ONE_HOUR_MINUTE;
		minutes = minute % ONE_HOUR_MINUTE;
		if (hours != 0) {
			time = String.format("%02d", hours) + ":";
		}

		if (minutes != 0) {
			time = time + String.format("%02d", minutes) + ":";
		} else {
			time = time + "00:";
		}

		time = time + "00";

		return time;
	}

	public static long getCurrentTimeMills() {
		Date dt = new Date();
		Long time = dt.getTime();
		return time;
	}
	/**
	 * 获取当前时间
	 *
	 * @return
	 */
	public static String getNowTime() {
		String timeString = null;
		Time time = new Time();
		time.setToNow();
		String year = thanTen(time.year);
		String month = thanTen(time.month + 1);
		String monthDay = thanTen(time.monthDay);
		String hour = thanTen(time.hour);
		String minute = thanTen(time.minute);

		timeString = year + "-" + month + "-" + monthDay + " " + hour + ":" + minute;
		return timeString;
	}

	public static String thanTen(int str) {

		String string = null;

		if (str < 10) {
			string = "0" + str;
		} else {

			string = "" + str;

		}
		return string;
	}

	public static int timeFormatToMinute(String timeStr) {
		int minute;
		String[] timeArray = null;
		timeArray = timeStr.split(":]");
		if (timeArray != null) {
			if (timeArray.length == 3) {

			}
		}
		Integer.parseInt("30");

		return 0;
	}

	public static String getAddDurtionTime(String CurrentTime, int du) {
		Calendar now = Calendar.getInstance();

		now.add(Calendar.MINUTE, du);

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

		String dateStr = sdf.format(now.getTimeInMillis());
		return dateStr;
	}

	public static long getIntTime(String time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date date = null;
		Date sDate = null;
		try {
			date = format.parse(time);
			sDate = format.parse("00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (date.getTime() - sDate.getTime()) / 1000 / 60;
	}

	public static String getTimeDifference(String starTime, String endTime) {
		String timeString = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date parse = dateFormat.parse(starTime);
			Date parse1 = dateFormat.parse(endTime);

			long diff = parse1.getTime() - parse.getTime();

			long day = diff / (24 * 60 * 60 * 1000);
			long hour = (diff / (60 * 60 * 1000) - day * 24);
			long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);

			long hour1 = diff / (60 * 60 * 1000);
			String hourString = hour1 + "";
			long min1 = ((diff / (60 * 1000)) - hour1 * 60);
			timeString = hour1 + ":" + min1;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeString;

	}
	/**
	 * 将毫秒转化成固定格式的时间 时间格式: yyyy-MM-dd
	 *
	 * @return
	 */
	public static String getCurDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(getCurrentTimeMills());
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

	/**
	 * 将毫秒转化成固定格式的时间 时间格式: yyyy-MM-dd HH:mm:ss
	 *
	 * @param millisecond
	 * @return
	 */
	public static String getDateTimeFromMillisecond(Long millisecond) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(millisecond);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}
	/**
	 * 将时间转化成毫秒 时间格式: yyyy-MM-dd HH:mm:ss
	 *
	 * @param time
	 * @return
	 */
	public static Long timeStrToSecond(String time) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Long second = format.parse(time).getTime();
			return second;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1l;
	}

	/**
	 * 获取时间间隔
	 *
	 * @param millisecond
	 * @return
	 */
	public static String getSpaceTime(Long millisecond) {
		Calendar calendar = Calendar.getInstance();
		Long currentMillisecond = calendar.getTimeInMillis();

		// 间隔秒
		Long spaceSecond = (currentMillisecond - millisecond) / 1000;

		// 一分钟之内
		if (spaceSecond >= 0 && spaceSecond < 60) {
			return "片刻之前";
		}
		// 一小时之内
		else if (spaceSecond / 60 > 0 && spaceSecond / 60 < 60) {
			return spaceSecond / 60 + "分钟之前";
		}
		// 一天之内
		else if (spaceSecond / (60 * 60) > 0 && spaceSecond / (60 * 60) < 24) {
			return spaceSecond / (60 * 60) + "小时之前";
		}
		// 3天之内
		else if (spaceSecond / (60 * 60 * 24) > 0 && spaceSecond / (60 * 60 * 24) < 3) {
			return spaceSecond / (60 * 60 * 24) + "天之前";
		} else {
			return getDateTimeFromMillisecond(millisecond);
		}
	}

	/**
	 * 比较两个时间
	 *
	 * @param starTime
	 *            开始时间
	 * @param endString
	 *            结束时间
	 * @return 结束时间大于开始时间返回true，否则反之֮
	 */
	public boolean compareTwoTime2(String starTime, String endString) {
		boolean isDayu = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		try {
			Date parse = dateFormat.parse(starTime);
			Date parse1 = dateFormat.parse(endString);

			long diff = parse1.getTime() - parse.getTime();
			isDayu = diff >= 0;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isDayu;

	}

	public static long getNewFlagTime() {
		String dateTime = "2100-01-01 00:00:00";
		Calendar calendar = Calendar.getInstance();
		long time = 0l;
		
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
			time = calendar.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return time;
		
	}

	/**
	 * 转换时间格式
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurFormatTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		String time = formatter.format(curDate);
		return time;
	}

	/**
	 * 格式化时间展示为05'10"
	 */
	public static String formatRecordTime(int recTime) {
		if(recTime > 60) {
			recTime = 60;
		}
		return recTime + "\"";
	}

}
