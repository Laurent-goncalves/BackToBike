package com.g.laurent.backtobike.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.WeatherIcons;
import com.g.laurent.backtobike.Views.EventViewHolder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UtilsTime {

    public static int getNumberOfDaysBetweenTwoDate(String date1, String date2){

        Date d1 = new Date();
        Date d2 = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("fr"));

        try {
            d1 = dateFormat.parse(date1);
            d2 = dateFormat.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (int)( (d2.getTime() - d1.getTime()) / (1000*60*60*24));
    }

    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("fr"));
        return dateFormat.format(new Date());
    }

    public static Boolean isDateInsidePeriod(String dateTest, String dateInf, String dateSup){

        Boolean answer = true;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("fr"));

        try {
            Date datetest = dateFormat.parse(dateTest);
            Date dateinf = dateFormat.parse(dateInf);
            Date datesup = dateFormat.parse(dateSup);

            answer = datetest.compareTo(datesup) <= 0 && datetest.compareTo(dateinf) >= 0;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return answer;
    }

    public static int getSeasonNumber(){

        String today = getTodayDate();
        String year = today.substring(6,10);

        String springStart = "20/03/" + year;
        String springEnd = "21/06/" + year;
        String summerStart = "22/06/" + year;
        String summerEnd = "22/09/" + year;
        String autumnStart = "23/09/" + year;
        String autumnEnd = "20/12/" + year;

        if(isDateInsidePeriod(today, springStart, springEnd))
            return 1;
        else if (isDateInsidePeriod(today, summerStart, summerEnd))
            return 2;
        else if (isDateInsidePeriod(today, autumnStart, autumnEnd))
            return 3;
        else // winter
            return 0;
    }

    public static List<BikeEvent> getListBikeEventByChronologicalOrder(List<BikeEvent> listBikeEvent){

        List<BikeEvent> listNotSorted = new ArrayList<>(listBikeEvent);
        List<BikeEvent> listSorted = new ArrayList<>();

        if(listNotSorted.size()>0){

            int sizeList = listNotSorted.size();

            for(int i = 0 ; i < sizeList ; i++){

                if(listNotSorted.size()>0){

                    BikeEvent bikeEventRef = listNotSorted.get(0);
                    int index = 0;

                    for(int j = 0 ; j < listNotSorted.size() ; j++){
                        if(isBefore(listNotSorted.get(j), bikeEventRef)) {
                            bikeEventRef = listNotSorted.get(j);
                            index = j;
                        }
                    }

                    listSorted.add(bikeEventRef);
                    listNotSorted.remove(index);
                }
            }
        }

        return listSorted;
    }

    public static void deleteOverdueEvents(Context context, String userId){

        List<BikeEvent> listBikeEvents = BikeEventHandler.getAllBikeEvents(context, userId);

        if(listBikeEvents!=null){
            if(listBikeEvents.size()>0){
                for(BikeEvent event : listBikeEvents){
                    if(UtilsTime.isBefore(event.getDate(), UtilsTime.getTodayDate())){

                        // Delete event in database
                        BikeEventHandler.deleteBikeEvent(context,event ,userId);

                        // Delete event in Firebase
                        FirebaseUpdate firebaseUpdate = new FirebaseUpdate(context);
                        firebaseUpdate.deleteEvent(userId, event);
                    }
                }
            }
        }
    }

    public static boolean isBefore(BikeEvent bikeEvent1, BikeEvent bikeEvent2) {

        Calendar dateComp = Calendar.getInstance();
        dateComp.set(Calendar.YEAR, Integer.parseInt(bikeEvent1.getDate().substring(6,10)));
        dateComp.set(Calendar.MONTH, Integer.parseInt(bikeEvent1.getDate().substring(3,5)));
        dateComp.set(Calendar.DAY_OF_MONTH, Integer.parseInt(bikeEvent1.getDate().substring(0,2)));

        Calendar dateRef = Calendar.getInstance();
        dateRef.set(Calendar.YEAR, Integer.parseInt(bikeEvent2.getDate().substring(6,10)));
        dateRef.set(Calendar.MONTH, Integer.parseInt(bikeEvent2.getDate().substring(3,5)));
        dateRef.set(Calendar.DAY_OF_MONTH, Integer.parseInt(bikeEvent2.getDate().substring(0,2)));

        Boolean isSameDay = dateComp.get(Calendar.YEAR)== dateRef.get(Calendar.YEAR)
                && dateComp.get(Calendar.MONTH) == dateRef.get(Calendar.MONTH)
                && dateComp.get(Calendar.DAY_OF_MONTH) == dateRef.get(Calendar.DAY_OF_MONTH);

        if(isSameDay){ // compare time

            int hour1 = Integer.parseInt(getHour(bikeEvent1.getTime()));
            int hour2 = Integer.parseInt(getHour(bikeEvent2.getTime()));

            int min1 = Integer.parseInt(getMinutes(bikeEvent1.getTime()));
            int min2 = Integer.parseInt(getMinutes(bikeEvent2.getTime()));

            if(hour1 < hour2)
                return true;
            else if(hour1 == hour2){
                return min1 <= min2;
            } else
                return false;
        } else {
            return dateComp.get(Calendar.YEAR) < dateRef.get(Calendar.YEAR) || dateComp.get(Calendar.YEAR) == dateRef.get(Calendar.YEAR)
                    && (dateComp.get(Calendar.MONTH) < dateRef.get(Calendar.MONTH) || dateComp.get(Calendar.MONTH) == dateRef.get(Calendar.MONTH)
                    && dateComp.get(Calendar.DAY_OF_MONTH) < dateRef.get(Calendar.DAY_OF_MONTH));
        }
    }

    private static String getHour(String time){
        String hour;

        if(time.length()==4){
            hour = time.substring(0,1);
        } else {
            hour = time.substring(0,2);
        }
        return hour;
    }

    private static String getMinutes(String time){
        String minute;

        if(time.length()==4){
            minute = time.substring(2,4);
        } else {
            minute = time.substring(3,5);
        }
        return minute;
    }

    public static boolean isBefore(String date1, String date2) {

        Calendar dateComp = Calendar.getInstance();
        dateComp.set(Calendar.YEAR, Integer.parseInt(date1.substring(6,10)));
        dateComp.set(Calendar.MONTH, Integer.parseInt(date1.substring(3,5)));
        dateComp.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date1.substring(0,2)));

        Calendar dateRef = Calendar.getInstance();
        dateRef.set(Calendar.YEAR, Integer.parseInt(date2.substring(6,10)));
        dateRef.set(Calendar.MONTH, Integer.parseInt(date2.substring(3,5)));
        dateRef.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date2.substring(0,2)));

        return dateComp.get(Calendar.YEAR) < dateRef.get(Calendar.YEAR) || dateComp.get(Calendar.YEAR) == dateRef.get(Calendar.YEAR)
                    && (dateComp.get(Calendar.MONTH) < dateRef.get(Calendar.MONTH) || dateComp.get(Calendar.MONTH) == dateRef.get(Calendar.MONTH)
                    && dateComp.get(Calendar.DAY_OF_MONTH) < dateRef.get(Calendar.DAY_OF_MONTH));

    }

    public static Calendar getCalendarAlarm2daysbefore(String date){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(6,10)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(3,5))-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0,2)));
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);

        calendar.add(Calendar.DATE, -2);

        return calendar;
    }

    public static Calendar getCalendarAlarmdayevent(String date, String time){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(6,10)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(3,5))-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0,2)));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(getHour(time)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(getMinutes(time)));

        calendar.add(Calendar.HOUR_OF_DAY, -4);

        return calendar;
    }

    public static int generatePendingIntentID_2days(String date, String time){
        String id = getHour(time) + getMinutes(time) + date.substring(0,2) + date.substring(3,5) + date.substring(9,10);
        return Integer.parseInt(id) + 2;
    }

    public static int generatePendingIntentID_4hours(String date, String time){
        String id = getHour(time) + getMinutes(time) + date.substring(0,2) + date.substring(3,5) + date.substring(9,10);
        return Integer.parseInt(id) + 4;
    }

    public static String createStringDate(int year, int month, int dayOfMonth){

        String Day;
        int Month = month + 1;
        String new_month;

        if(dayOfMonth<10)
            Day = "0" + dayOfMonth;
        else
            Day = String.valueOf(dayOfMonth);

        if(Month<10)
            new_month = "0" + Month;
        else
            new_month = String.valueOf(Month);

        return Day + "/" + new_month + "/" + year;
    }

    public static String createStringTime(int hourOfDay, int minute){
        if(minute<10)
            return hourOfDay + ":0" + minute;
        else
            return hourOfDay + ":" + minute;
    }

    public static String getDateWeather(Context context, String date){

        // Get day number
        int dayNum = getDayNumber(date);

        // Get day name
        String dayName = context.getResources().getString(WeatherIcons.daysIds[dayNum-1]);

        // Build date
        return dayName + " " + getShortDate(date);
    }

    public static String getDateEvent(Context context, String date){

        // Get day number
        int dayNum = getDayNumber(date);

        // Build date
        return context.getResources().getString(EventViewHolder.DaysName.daysName[dayNum-1]);
    }

    private static String getShortDate(String dateWeather){
        return dateWeather.substring(8,10) + "/" + dateWeather.substring(5,7);
    }

    @SuppressLint("SimpleDateFormat")
    private static int getDayNumber(String dateString){

        String format = null;

        if(dateString!=null){
            if(dateString.substring(4,5).equals("-"))
                format = "yyyy-MM-dd";
            else if(dateString.substring(2,3).equals("/"))
                format = "dd/MM/yyyy";

            if(format!=null){
                SimpleDateFormat df = new SimpleDateFormat(format);
                Date date = null;

                try {
                    date = df.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                return cal.get(Calendar.DAY_OF_WEEK);

            } else
                return 0;
        }
        return 0;
    }

}
