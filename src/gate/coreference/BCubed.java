package gate.coreference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * B-Cubed coreference scorer
 * <p>
 * Amit Bagga, Breck Baldwin, LREC 1998,
 * "Algorithms for scoring coreference chains"
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class BCubed<T> implements CoreferenceScorer<T> {

	final private Map<T, Set<T>> keyTable;

	/**
	 * @param key
	 *            key partitioning
	 */
	BCubed(Set<Set<T>> key) {
		keyTable = buildTable(key);
	}

	@Override
	public double[] score(Set<Set<T>> response) {
		double[] scores = { 0.0, 0.0 };
		Map<T, Set<T>> responseTable = buildTable(response);
		Set<T> domain = union(keyTable.keySet(), responseTable.keySet());
		for (T element : domain) {
			scores[0] += precision(element, responseTable);
			scores[1] += recall(element, responseTable);
		}
		scores[0] /= domain.size();
		scores[1] /= domain.size();
		return scores;
	}

	/**
	 * Precision for a single element of the domain
	 * 
	 * @param element
	 *            element in the domain
	 * @param responseTable
	 *            table of elements to response sets
	 * @return precision
	 */
	private float precision(T element, Map<T, Set<T>> responseTable) {
		Set<T> response = responseTable.get(element);
		float precision = intersection(keyTable.get(element), response).size();
		precision /= response.size();
		return precision;
	}

	/**
	 * Recall for a single element of the domain
	 * 
	 * @param element
	 *            element in the domain
	 * @param responseTable
	 *            table of elements to response sets
	 * @return recall
	 */
	private float recall(T element, Map<T, Set<T>> responseTable) {
		Set<T> key = keyTable.get(element);
		float recall = intersection(key, responseTable.get(element)).size();
		recall /= key.size();
		return recall;
	}

	/**
	 * Build a table of elements to sets
	 * 
	 * @param sets
	 */
	private Map<T, Set<T>> buildTable(Set<Set<T>> sets) {
		Map<T, Set<T>> table = new HashMap<T, Set<T>>();
		for (Set<T> set : sets)
			for (T item : set) {
				if (table.containsKey(item))
					throw new IllegalArgumentException("Element " + item
							+ " appears in more than one set");
				table.put(item, set);
			}
		return table;
	}

	/**
	 * Set union
	 * 
	 * @param a
	 *            set
	 * @param b
	 *            set
	 * @return union of a and b
	 */
	private Set<T> union(Set<T> a, Set<T> b) {
		Set<T> union = new HashSet<T>();
		union.addAll(a);
		union.addAll(b);
		return union;
	}

	/**
	 * Set intersection
	 * 
	 * @param a
	 *            set
	 * @param b
	 *            set
	 * @return intersection of a and b
	 */
	private Set<T> intersection(Set<T> a, Set<T> b) {
		Set<T> intersection = new HashSet<T>();
		intersection.addAll(a);
		intersection.retainAll(b);
		return intersection;
	}

}
