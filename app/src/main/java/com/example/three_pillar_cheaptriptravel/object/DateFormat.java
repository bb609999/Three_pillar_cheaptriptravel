package com.example.three_pillar_cheaptriptravel.object;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 30/4/2018.
 */

public class DateFormat {
   public static Date changeDate(Date date, int numOfDate){
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       calendar.add(Calendar.DAY_OF_MONTH,numOfDate);

       return calendar.getTime();
   }

   public static String DateToString(Date date){
       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
       return  simpleDateFormat.format(date);
   }

   public static String addDayToDate(String date,int numOfDay){


       Date date1 = new Date();

       try {
           date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
       }catch (Exception e){
           e.printStackTrace();
       }
       Calendar c = Calendar.getInstance();
       c.setTime(date1);
       c.add(Calendar.DATE, numOfDay);  // number of days to add
       return new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
   }
}
