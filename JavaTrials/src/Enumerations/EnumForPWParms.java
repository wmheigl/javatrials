/**
 * EnumForPWParms.java
 *
 * @author Werner M. Heigl
 * @version 2019.07.15
 */
package Enumerations;

import java.util.Arrays;

/**
 * This class illustrates how to use an enum that holds the string constants to access PWParms in NanoSeis proc files
 * 
 */
public class EnumForPWParms {

	public static void main(String[] args) {
		
		System.out.println("Enum.name() = " + Parms.PARTICLE_MOTION.name());
		System.out.println("Enum.ordinal() = " + Parms.PARTICLE_MOTION.ordinal());
		System.out.println("Enum.getClass() = " + Parms.PARTICLE_MOTION.getClass().getName());
		System.out.println("Enum.getDeclaringClass() = " + Parms.PARTICLE_MOTION.getDeclaringClass().getName());
		System.out.println("Enum.values().length = " + Parms.values().length);
		System.out.println("Enum.values().toString() = " + Arrays.toString(Parms.values()));
		System.out.println("Enum.valueOf() = " + Parms.valueOf("DENSITY"));
			
		System.out.println("Enum.toString() = " + Parms.PARTICLE_MOTION.toString());

	}

}
