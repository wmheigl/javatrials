/**
 * MapOfMaps.java
 *
 * @author Werner M. Heigl
 * @version 2019.05.28
 */
package Collections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * This class illustrates how to convert a map of maps to a list of strings.
 * 
 */
public class MapOfMaps {

	Map<String, Map<String, String>> configuration;

	public MapOfMaps() {
		configuration = new HashMap<String, Map<String, String>>();
		Map<String, String> gridProperties = makeGridProperties();
		configuration.put("grid", gridProperties);
		Map<String, String> sourceProperties = makeSourceProperties();
		configuration.put("source", sourceProperties);
	}

	private Map<String, String> makeGridProperties() {
		LinkedHashMap<String, String> p = new LinkedHashMap<>();
		p.put("h", "20");
		p.put("x", "1500");
		p.put("y", "1500");
		p.put("z", "2000");
		return p;
	}

	private Map<String, String> makeSourceProperties() {
		Map<String, String> p = new LinkedHashMap<>();
		p.put("x", "750");
		p.put("y", "750");
		p.put("z", "1500");
		p.put("mxx", "1e8");
		p.put("myy", "1e8");
		p.put("mzz", "1e8");
		p.put("type", "BruneSmoothed");
		p.put("t0", "0");
		p.put("freq", "20");
		return p;
	}

	public static void main(String[] args) {

		MapOfMaps m = new MapOfMaps();
		for (Entry<String, Map<String, String>> entry : m.configuration.entrySet()) {
			System.out.print(entry.getKey() + " ");
			String[] strs = m.configuration.get(entry.getKey()).toString().replaceAll("\\p{P}", "").split(",");
			Arrays.stream(strs).forEach(System.out::println);			
		}
	}

}
