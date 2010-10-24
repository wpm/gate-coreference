package gate.coreference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * B-Cubed coreference scorer.
 * <p>
 * Amit Bagga, Breck Baldwin, LREC 1998,
 * "Algorithms for scoring coreference chains"
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class BCubed<T> implements EquivalenceClassScorer<T> {

	/**
	 * B-Cubed scores for a pair of equivalence sets
	 * 
	 * @see gate.coreference.EquivalenceClassScorer#score(java.util.Set,
	 *      java.util.Set)
	 */
	@Override
	public PrecisionRecall score(Set<Set<T>> key, Set<Set<T>> response) {
		double[] scores = { 0.0, 0.0 };
		Map<T, Set<T>> keyTable = buildTable(key);
		Map<T, Set<T>> responseTable = buildTable(response);

		// Precision
		for (Double ratio : comparePartitions(keyTable, responseTable))
			scores[0] += ratio;
		scores[0] /= responseTable.keySet().size();

		// Recall
		for (Double ratio : comparePartitions(responseTable, keyTable))
			scores[1] += ratio;
		scores[1] /= keyTable.keySet().size();

		return new PrecisionRecall(scores[0], scores[1]);
	}

	/**
	 * Build a table of elements to sets
	 * 
	 * @param sets
	 *            equivalence sets
	 * @return table of the sets indexed by their elements
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
	 * @param numTable
	 *            set table of the score numerator
	 * @param denTable
	 *            set table of the score denominator
	 * @return list of score ratios
	 */
	private List<Double> comparePartitions(Map<T, Set<T>> numTable,
			Map<T, Set<T>> denTable) {
		List<Double> ratios = new LinkedList<Double>();
		for (T element : denTable.keySet()) {
			double numerator, denominator;
			if (!numTable.containsKey(element))
				numerator = 0;
			else {
				Set<T> intersection = SetUtilities.intersection(
						numTable.get(element), denTable.get(element));
				numerator = intersection.size();
			}
			denominator = denTable.get(element).size();
			ratios.add(numerator / denominator);
		}
		return ratios;
	}

}
