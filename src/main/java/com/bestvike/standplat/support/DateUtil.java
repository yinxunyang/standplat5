package com.bestvike.standplat.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class DateUtil {
	private DateUtil(){

	}

	public static Date parseDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	}
	public static String getDate() {
		Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdf.format(date);
        return nowDate;
	}
	public static String getDate2() {
		Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String nowDate = sdf.format(date);
        return nowDate;
	}
	public static String getMonth() { 		
		Date date = new Date();         
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");         
		String nowDate = sdf.format(date);         
		return nowDate; 	
	}

	public static String getMonth(int add) { 		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, add);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");         
		String nowDate = sdf.format(cal.getTime());         
		return nowDate; 	
	}
	
	public static Date parseYearMonth(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date nowDate = sdf.parse(date);
        return nowDate;
	}
	public static String formatYearMonthDay(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdf.format(date);
        return nowDate;
	}
	public static String formatYearMonth(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String nowDate = sdf.format(date);
        return nowDate;
	}
	public static Date parseDateTime(String date) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = sdf.parse(date);
		return date1;
	}
	
	public static String getTime() {
		Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(date);
        return nowTime;
	}
	
	public static String getDateAndTime() {
		Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(date);
        return now;
	}
	public static String getDateTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String now = sdf.format(date);
		return now;
	}

	public static String getDateAndTime(int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, days);
		String format = sdf.format(c.getTime());
		return format;
	}

	public static String parseDateTo19Str(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(date);
		return str;
	}
	
	public static String getDateAndTime2() {
		Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String now = sdf.format(date);
        return now;
	}
	
	/**
	 * 查询指定年月的最后一天
	 * @param yearMonth 年-月，格式：yyyy-MM
	 */
	public static String getYearMonthLastDay(String yearMonth){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(yearMonth.substring(0,4)));
		cal.set(Calendar.MONTH,Integer.parseInt(yearMonth.substring(5,7)));
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
	}
	
	/**
	 * 得到两日期相差几个月
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
    public static int getMonth(String startDate, String endDate) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");
        Date startDate1 = f.parse(startDate);
        Date endDate1 = f.parse(endDate);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate1);

        int sYear = startCal.get(Calendar.YEAR);
        int sMonth = startCal.get(Calendar.MONTH);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate1);
        int eYear = endCal.get(Calendar.YEAR);
        int eMonth = endCal.get(Calendar.MONTH);

        return ((eYear - sYear) * 12 + (eMonth - sMonth + 1));
    }
	public static List<String> getYearMonthList(String start, String end) {
		List<String> ret = new ArrayList<String>();
		try {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");
	        Date startDate1 = f.parse(start.substring(0,7));
	        Date endDate1 = f.parse(end.substring(0,7));
	        
	        Calendar startCal = Calendar.getInstance();
	        startCal.setTime(startDate1);
	        do{
		        ret.add(f.format(startCal.getTime()));
		        startCal.add(Calendar.MONTH, 1);
	        }while(startCal.getTime().compareTo(endDate1)<=0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String parseCertNoToBirthDt(String certNo){
		String birthDt = certNo.substring(6,10) + "-"+certNo.substring(10,12) + "-"+certNo.substring(12,14);
		return birthDt;
	}

	/**
	 * 计算两个日期之间相差的天数
	 * @param bdate  较大的时间
	 * @param smdate 较小的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int dateDiff(Date bdate,Date smdate) throws ParseException
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		smdate=sdf.parse(sdf.format(smdate));
		bdate=sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days=(time2-time1)/(1000*3600*24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * @Author 高德禄
	 * @Description //TODO 字符串的日期间隔计算
	 * @Date 16:09 2018/7/27
	 * @Param [bdate, smdate]
	 * @return int
	 **/
	public static int dateDiff(String bdate,String smdate) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf1.parse(smdate));
			long time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(bdate));
			long time2 = cal.getTimeInMillis();
			long between_days=(time1-time2)/(1000*3600*24);
			return Integer.parseInt(String.valueOf(between_days));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 计算当前时间到第二天凌晨的秒数
	 * @return
	 */
	public static int secondDiffNow2Tom(){
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		final double diff = cal.getTimeInMillis() - System.currentTimeMillis();
		return (int)(diff/1000);
	}

	/**
	 * @Author 高德禄
	 * @Description //TODO 查询传入时间和当前时间相差的分钟数
	 * @Date 14:24 2018/8/13
	 * @Param [QueryDt]
	 * @return int
	 **/
	public static double queryDiffMin(String QueryDt) throws ParseException{
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date begin = dfs.parse(QueryDt);
		Date end = dfs.parse(DateUtil.getDateAndTime());
		double between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒
		double min=between/60;
		return min;
	}
    /**
     * @Author 高德禄
     * @Description //TODO 计算指定日期之后第七天日期
     * @Date 16:10 2018/7/27
     * @Param [date]
     * @return java.lang.String
     **/
    public static String queryNextSevenDay(int year,int month,int date){
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DATE,date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
        Date today = calendar.getTime();
        String result = sm.format(today);
        return result;
    }

    /**
     * @Author 高德禄
     * @Description //TODO 查询指定月第一天
     * @Date 16:14 2018/7/27
     * @Param [year, month]
     * @return java.lang.String
     **/
    public static String queryFistDayOfMonth(int year,int month){
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calstar = Calendar.getInstance();
        calstar.set(Calendar.YEAR,year);
        calstar.set(Calendar.MONTH,month);
        calstar.set(Calendar.DAY_OF_MONTH, calstar.getActualMinimum(Calendar.DATE));
        String startDt = sm.format(calstar.getTime());
        return startDt;
    }

    /**
     * @Author 高德禄
     * @Description //TODO  查询指定年月最后一天
     * @Date 16:17 2018/7/27
     * @Param [year, month]
     * @return java.lang.String
     **/
    public static String queryLastDayOfMonth(int year,int month){
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.YEAR,year);
        calEnd.set(Calendar.MONTH,month);
        calEnd.set(Calendar.DAY_OF_MONTH,calEnd.getActualMaximum(Calendar.DATE));
        String endDt = sm.format(calEnd.getTime());
        return endDt;
    }
    /**
     * @Author 高德禄
     * @Description //TODO 查询列表中距离某个日期最近的一个日期
     * @Date 17:13 2018/8/24
     * @Param [lastSetlDtList, curPrcsDt]
     * @return void
     **/
    public static String getLatestDt(List<String> lastSetlDtList, String curPrcsDt) {
    	String result = "";
    	int diff ;//日期差
        int diff1 = 0;//临时时间差
		for (int i=0;i<lastSetlDtList.size();i++) {
			diff = dateDiff2(lastSetlDtList.get(i),curPrcsDt);
			if (i==0){//第一次遍历初始化赋值
			    diff1 = diff;
            }
            if (diff<0){
			    continue;
            }
			if(diff<=diff1){
				 result = lastSetlDtList.get(i);
				 diff1 = diff;
			}
		}
    	return result;
    }

	/**
	 * @Author 高德禄
	 * @Description //TODO 字符串的日期间隔计算
	 * @Date 16:09 2018/7/27
	 * @Param [bdate, smdate]
	 * @return int
	 **/
	public static int dateDiff2(String bdate,String smdate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(smdate));//目标日期
			long time2 = cal.getTimeInMillis();
			cal.setTime(sdf1.parse(bdate));//当前日期
			long time1 = cal.getTimeInMillis();
			long between_days=(time1-time2)/(1000*3600*24);
			return Integer.parseInt(String.valueOf(between_days));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String queryLastHalfYear(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -6);
		Date m6 = c.getTime();
		String mon6 = format.format(m6);
		return mon6;
	}

	/**
	 * 当前月的第一天  yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String queryFirstDayOfMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		//将小时至0
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		//将分钟至0
		calendar.set(Calendar.MINUTE, 0);
		//将秒至0
		calendar.set(Calendar.SECOND,0);
		//将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		//获得当前月第一天
		Date sdate = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return  sdf.format(sdate);
	}

	/**
	 * 获取当前月的对后一天 23:59:59
	 * @return
	 */
	public static String queryLastDayOfMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		//将小时至0
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		//将分钟至0
		calendar.set(Calendar.MINUTE, 0);
		//将秒至0
		calendar.set(Calendar.SECOND,0);
		//将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		//将当前月加1；
		calendar.add(Calendar.MONTH, 1);
		//在当前月的下一月基础上减去1毫秒
		calendar.add(Calendar.MILLISECOND, -1);
		//获得当前月最后一天
		Date sdate = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return  sdf.format(sdate);
	}

	public static String dateFormat(String dateString){
		String trim = dateString.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "").trim();
		return trim;
	}

	public static int differentDays(String date1, String date2) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date11 = format.parse(date1);
		Date date21 = format.parse(date2);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date11);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date21);
		int day1= cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if(year1 != year2)   //同一年
		{
			int timeDistance = 0 ;
			for(int i = year1 ; i < year2 ; i ++)
			{
				if(i%4==0 && i%100!=0 || i%400==0)    //闰年
				{
					timeDistance += 366;
				}
				else    //不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2-day1) ;
		}
		else    //不同年
		{
			System.out.println("判断day2 - day1 : " + (day2-day1));
			return day2-day1;
		}
	}

	public static boolean twoDateIn(String date1,String date2){
		String dateAndTime = dateFormat(getDateAndTime());
		if(Long.parseLong(dateAndTime) >=Long.parseLong(dateFormat(date1)) && Long.parseLong(dateAndTime) < Long.parseLong(dateFormat(date2))){
			return true;
		}
		return false;
	}

	public static boolean oneDateInTwo(String data,String start,String end){
		String dateAndTime = dateFormat(data);
		if(Long.parseLong(dateAndTime) >=Long.parseLong(dateFormat(start)) && Long.parseLong(dateAndTime) <=Long.parseLong(dateFormat(end))){
			return true;
		}
		return false;
	}
}
