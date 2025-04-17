package kr.dev.wany.BatchDev.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/************************************
 * Name : Test
 * To-Do : 
 * Developer : twkim
 * Date : 2025-04-17 오후 1:29
 ************************************/
public class Test {

    private static final Logger logloc = LoggerFactory.getLogger( Test.class );

    public static void main(String[] args) {
        /**
         * feature/twkim_1
         * add_branch_Test
         */


        for( int idx = 0 ; idx < 10 ; idx++) {
            logloc.info( "idx ---> {}", idx );
        }

    }
}
