/**
 * Example1.java
 *
 * @author Werner M. Heigl
 * @version 2019.03.22
 */
package Serializing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class illustrates serialization.
 * 
 */
public class Example1 {

	public static void printdata(Emperor object1) {

		System.out.println("Name = " + object1.name);
		System.out.println("Age = " + object1.age);
		System.out.println("a = " + object1.a);
		System.out.println("b = " + Emperor.b);
	}

	public static void main(String[] args) {
		Emperor object = new Emperor("Alexander The Great", 20, 2, 1000);
		String filename = "shubham.txt";

		// Serialization
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {

			// Method for serialization of object
			out.writeObject(object);

			System.out.println("Object has been serialized\n" + "Data before Deserialization.");
			printdata(object);

			// value of static variable changed
			Emperor.b = 2000;
		}
		catch (IOException ex) {
			System.out.println("IOException is caught");
		}

		object = null;

		// Deserialization
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {

			// Method for deserialization of object
			object = (Emperor) in.readObject();

			System.out.println("Object has been deserialized\n" + "Data after Deserialization.");
			printdata(object);

			Files.delete(Paths.get(filename));
		}
		catch (IOException ex) {
			System.out.println("IOException is caught");
		}
		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException" + " is caught");
		}		
	}
}
