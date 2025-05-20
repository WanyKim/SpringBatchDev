package kr.dev.wany.test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/************************************
 * Name : Test
 * To-Do : 
 * Developer : twkim
 * Date : 2025-04-17 오후 1:18
 ************************************/
public class Test {
    /**
     * Git Test : feature/twkim_2
     *
     */

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println( Date.from(now.truncatedTo( ChronoUnit.DAYS).atZone( ZoneId.systemDefault()).toInstant()) );
    }
}
