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
public class BCubed<T> implements EquivalenceClassScorer<T> {

	@Override
	public double[] score(Set<Set<T>> key, Set<Set<T>> response) {
		double[] scores = { 0.0, 0.0 };
		Map<T, Set<T>> keyTable = buildTable(key);
		Map<T, Set<T>> responseTable = buildTable(response);
		Set<T> domain = SetUtilities.union(keyTable.keySet(),
				responseTable.keySet());
		for (T element : domain) {
			scores[0] += precision(element, keyTable, responseTable);
			scores[1] += recall(element, keyTable, responseTable);
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
	 * @param keyTable
	 *            table of elements to key sets
	 * @param responseTable
	 *            table of elements to response sets
	 * @return precision
	 */
	private float precision(T element, Map<T, Set<T>> keyTable,
			Map<T, Set<T>> responseTable) {
		Set<T> key = getTableSet(element, keyTable);
		Set<T> response = getTableSet(element, responseTable);
		float precision = SetUtilities.intersection(key, response).size();
		int denominator = response.size();
		precision = denominator > 0 ? precision / denominator : 0;
		return precision;
	}

	/**
	 * Recall for a single element of the domain
	 * 
	 * @param element
	 *            element in the domain
	 * @param keyTable
	 *            table of elements to key sets
	 * @param responseTable
	 *            table of elements to response sets
	 * @return recall
	 */
	private float recall(T element, Map<T, Set<T>> keyTable,
			Map<T, Set<T>> responseTable) {
		Set<T> key = getTableSet(element, keyTable);
		Set<T> response = getTableSet(element, responseTable);
		float recall = SetUtilities.intersection(key, response).size();
		int denominator = key.size();
		recall = denominator > 0 ? recall / key.size() : 0;
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
	 * Lookup a set in the specified table
	 * 
	 * @param element
	 *            the element to look up
	 * @param table
	 *            table of elements to sets
	 * @return the set corresponding to the element
	 */
	private Set<T> getTableSet(T element, Map<T, Set<T>> table) {
		return table.containsKey(element) ? table.get(element)
				: new HashSet<T>();
	}

}
