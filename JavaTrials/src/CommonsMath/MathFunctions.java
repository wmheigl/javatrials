/**
 * MathFunctions.java
 *
 * @author Werner M. Heigl
 * @version 2019.10.30
 */
package CommonsMath;

public class MathFunctions {

  public static void main(String[] args) {

    double azimuth = 60; // degrees, clockwise from North
    double xCoord = -1;
    double yCoord = -0;//0.5;
    
    System.out.println("Angle from Math.atan2() = " + Math.toDegrees(Math.atan2(yCoord, xCoord)));
    
    System.out.println("Success");
  }

}
