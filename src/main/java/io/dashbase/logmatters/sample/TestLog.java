package io.dashbase.logmatters.sample;

import java.util.Random;

import org.apache.log4j.Logger;

public class TestLog {
  private static Logger log = Logger.getLogger(TestLog.class);
  
  private static Random rand = new Random();
  
  private static boolean isInfo(int roll) {
    return (roll > 2);
  }
  
  private static boolean isError(int roll) {
    return roll == 0;
  }
  
  private static boolean isWarn(int roll) {
    return roll == 1 || roll == 2;
  }
  
  private static Exception getException() {
    return new Exception("some exception");    
  }
  
	public static void main(String[] args) throws Exception {
	  int numLines = 0;
	  try {
	    numLines = Integer.parseInt(args[0]);
	  } catch (Exception e) {
	    numLines = 0;
	  }
	  int count = 0;
	  
	  int numIter = numLines > 0 ? numLines : Integer.MAX_VALUE;
	  while(count < numIter) {
	    if (numLines <= 0) {
	      Thread.sleep(2000);
	    }
	    int roll = rand.nextInt(10);
	    if (isInfo(roll)) {
	      log.info("test info");
	    } else if (isError(roll)) {
	      Exception e = getException();
	      log.error("oops, error found", e);
	    } else if (isWarn(roll)) {
	      log.warn("got a warning");
	    }
	    count ++;
	  }
	}
}
