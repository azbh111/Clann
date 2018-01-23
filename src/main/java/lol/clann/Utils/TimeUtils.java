/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Administrator
 */
public class TimeUtils extends org.apache.commons.lang.time.DateUtils {
    public static DateFormat getDateFormat(String format){
        return new SimpleDateFormat(format);
    }
}
