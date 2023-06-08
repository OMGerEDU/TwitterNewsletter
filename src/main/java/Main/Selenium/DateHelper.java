package Main.Selenium;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
    int year,month,day,hour,min;



    public DateHelper(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public DateHelper(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public DateHelper(int year, int month, int day, int hour, int min) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
    }

    public int getYear() {
        return year;
    }

    public DateHelper() {
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.hour = 0;
        this.min = 0;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public DateHelper nowToDateHelper() {
        Calendar calendar = Calendar.getInstance();
        return new DateHelper(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)
                ,calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE));

    }

    public DateHelper stringToDateHelper(String date) {
        DateHelper dateHelper = new DateHelper();
        String[] dateAsArray = date.split("/");
        try {
        dateHelper.setYear(Integer.parseInt(dateAsArray[0]));
        dateHelper.setMonth(Integer.parseInt(dateAsArray[1]));
        dateHelper.setDay(Integer.parseInt(dateAsArray[2]));
        dateHelper.setHour(Integer.parseInt(dateAsArray[3]));
        dateHelper.setMin(Integer.parseInt(dateAsArray[4]));
        }
        catch (Exception e) { System.out.println("dateHelper conversation failed, dateHelper:StringToDateHelper"); }

        return dateHelper;
    }

    public Date stringToDate(String date) {

        DateHelper dateHelper = new DateHelper();
        String[] dateAsArray = date.split("/");
        Date date1 = new Date(Integer.parseInt(dateAsArray[0]),Integer.parseInt(dateAsArray[1]),Integer.parseInt(dateAsArray[2]),Integer.parseInt(dateAsArray[3]),Integer.parseInt(dateAsArray[4]));
        try {
            dateHelper.setYear(Integer.parseInt(dateAsArray[0]));
            dateHelper.setMonth(Integer.parseInt(dateAsArray[1]));
            dateHelper.setDay(Integer.parseInt(dateAsArray[2]));
            dateHelper.setHour(Integer.parseInt(dateAsArray[3]));
            dateHelper.setMin(Integer.parseInt(dateAsArray[4]));
        }
        catch (Exception e) { System.out.println("dateHelper conversation failed, dateHelper:StringToDateHelper"); }

        return date1;
    }

    public String dateHelperToSqlFormat(DateHelper dateHelper) {
        return dateHelper.getYear()+"/"+dateHelper.getMonth()+"/"+dateHelper.getDay()+"/"+dateHelper.getHour()+"/"+dateHelper.getMin();
    }


    public DateHelper setExpireDate(int duration) {
        Calendar calendar = Calendar.getInstance();
        int lastDatOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

        DateHelper date = new DateHelper(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DATE));
        date.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        date.setMin(calendar.get(Calendar.MINUTE)-1);

        date.setDay(date.getDay()+duration);
        if (date.getDay()>lastDatOfCurrentMonth) {
            date.setMonth(date.getMonth()+(date.getDay()/30));
            date.setDay(date.getDay()%30);
        }
        if (date.getMonth()>12) {
            date.setYear(date.getYear()+(date.getMonth()/12));
            date.setMonth(date.getMonth()%12);
        }

        return date;
    }

    public String formatDateRange(String[] lastRun) {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DATE);

        // 0 = Day || 1 = Month || 2 = Year ||
        ArrayList<Integer> lastRunList = new ArrayList<>(); // we transfer from Strings to Integers to make the times shorter later on.

        for (String date:lastRun)
            lastRunList.add(Integer.parseInt(date));

        if(mYear==lastRunList.get(2)&&mMonth==lastRunList.get(1)&&mDay==lastRunList.get(0)) {
            if(lastRunList.get(0)==1) {
                lastRunList.set(0,30);
                lastRunList.set(1,lastRunList.get(1)-1);
            }
            else
                lastRunList.set(0,lastRunList.get(0)-1);
        }

        return  "until:"+mYear+"-"+mMonth+"-"+(mDay+1)+" since:"+lastRunList.get(2)+"-"+lastRunList.get(1)+"-"+(lastRunList.get(0))+" ";
    }
}

