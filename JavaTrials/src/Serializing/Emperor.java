/**
 * Emperor.java
 *
 * @author Werner M. Heigl
 * @version 2019.03.22
 */
package Serializing;

import java.io.Serializable;

/**
 * Toy class of an emperor.
 * 
 */
class Emperor implements Serializable {

	private static final long serialVersionUID = 1L;
	transient int a;
	static int b;
	String name;
	int age;

	// Default constructor
	public Emperor(String name, int age, int a, int b) {
		this.name = name;
		this.age = age;
		this.a = a;
		Emperor.b = b;
	}

}