package db.demo.utils;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class TimestampUtil {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

    public static Timestamp fromStringToTimestamp(String time) {
        if (time == null){
            return  null;
        }
        String str = ZonedDateTime.parse(time).format(DateTimeFormatter.ISO_INSTANT);
        return new Timestamp(ZonedDateTime.parse(str).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public static String fromTimestampToString(Timestamp timestamp) {
         final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(timestamp.getTime());

        //return timestamp.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static String getNowString() {
        return TimestampUtil.fromTimestampToString(new Timestamp(System.currentTimeMillis()));
    }

    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }
}
