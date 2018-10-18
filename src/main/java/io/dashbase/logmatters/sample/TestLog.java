package io.dashbase.logmatters.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TestLog {
  private static Logger log = LoggerFactory.getLogger(TestLog.class);
  
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

  static void logIt(Logger logger) {
	  int roll = rand.nextInt(10);
	  if (isInfo(roll)) {
		  logger.info("test info");
	  }
	  if (isError(roll)) {
		  Exception e = getException();
		  logger.error("oops, error found", e);
	  } else if (isWarn(roll)) {
		  logger.warn("got a warning");
	  }
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
	      logIt(log);
	    }
	    count ++;
	  }
	}
}
