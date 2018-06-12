package org.bitbucket.ytimes.client.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by andrey on 27.05.17.
 */
public class Main {

    public static void main( String[] args ) throws Exception {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("root-context.xml");
    }

}
