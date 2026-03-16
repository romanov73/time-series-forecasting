package ru.ulstu;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

    public static Date clearTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static List<Month> getMonths() {
        return Arrays.asList(Month.values());
    }

    public static Date instantToDate(Instant instant) {
        return Date.from(instant);
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date localDateTimeToDate(LocalDateTime localDate) {
        return Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date localTimeToDate(LocalTime localTime) {
        return Date.from(localTime.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date addDays(Date date, int count) {
        Calendar cal = getCalendar(date);
        cal.add(Calendar.DAY_OF_MONTH, count);
        return cal.getTime();
    }

    public static Date addYears(Date date, int count) {
        Calendar cal = getCalendar(date);
        cal.add(Calendar.YEAR, count);
        return cal.getTime();
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
