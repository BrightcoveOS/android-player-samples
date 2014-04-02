package com.brightcove.player.samples.cast.basic.test;

import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import com.brightcove.player.samples.cast.basic.GoogleCastComponentTest;

import junit.framework.TestSuite;


public class Runner extends InstrumentationTestRunner {

    @Override
    public TestSuite getAllTests(){
        InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
        suite.addTestSuite(GoogleCastComponentTest.class);
        return suite;
    }

    @Override
    public ClassLoader getLoader() {
        return Runner.class.getClassLoader();
    }

}