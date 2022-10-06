package com.cubbysulotions.proo.Calendar.Utilities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {
    public static LocalDate selectedDate;

    public static String formattedDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String formattedShortDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
        return date.format(formatter);
    }

    public static String formattedDayOnly(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
        return date.format(formatter);
    }

    public static String formattedDayOfWeek(LocalDate date){
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date1 = Date.from(instant);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return new SimpleDateFormat("EE").format(dayOfWeek);
    }

    public static String formattedMonth(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        return date.format(formatter);
    }

    public static String formatteFullMonth(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        return date.format(formatter);
    }

    public static String formattedTime(LocalTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(formatter);
    }



    public static String formattedShortTime(LocalTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h a");
        return time.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String monthYearFormatter(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String monthDayFormatter(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInMonthArray() {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);

        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek)
                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), prevDaysInMonth + i - dayOfWeek));
            else if (i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i - dayOfWeek - daysInMonth));
            else
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));

        }


        return  daysInMonthArray;
    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while(current.isBefore(endDate)){
            days.add(current);
            current = current.plusDays(1);
        }

        return days;
    }

    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo)){
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }

    public static Calendar localDateToCalendar(LocalDate localDate){
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

}
