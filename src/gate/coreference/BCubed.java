/**
 * This file is part of the GATE Coreference Plugin.
 *
 * The GATE Coreference Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *   
 * The GATE Coreference Plugin is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *   
 * You should have received a copy of the GNU General Public License along with the GATE
 * Coreference Plugin.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2010 W.P. McNeill
 */

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
		Map<T, Set<T>> keyTable = buildTable(key);
		Map<T, Set<T>> responseTable = buildTable(response);

		List<Double> elementPrecisions = scoreElements(keyTable, responseTable);
		int responseSize = responseTable.keySet().size();
		List<Double> elementRecalls = scoreElements(responseTable, keyTable);
		int keySize = keyTable.keySet().size();

		return calculateElementAverages(elementPrecisions, responseSize,
				elementRecalls, keySize);
	}

	/**
	 * B-Cubed scores for a set of equivalence set pairs and their micro and
	 * macro averages.
	 * 
	 * @see gate.coreference.EquivalenceClassScorer#scoreMultipleSets(java.lang.Iterable)
	 */
	@Override
	public PrecisionRecallAverages scoreMultipleSets(
			Iterable<List<Set<Set<T>>>> sets) {
		BCubedPrecisionRecallAverages scores = new BCubedPrecisionRecallAverages();
		for (List<Set<Set<T>>> equivalenceSets : sets) {
			Set<Set<T>> key = equivalenceSets.get(0);
			Set<Set<T>> response = equivalenceSets.get(1);

			Map<T, Set<T>> keyTable = buildTable(key);
			Map<T, Set<T>> responseTable = buildTable(response);

			List<Double> elementPrecisions = scoreElements(keyTable,
					responseTable);
			int responseSize = responseTable.keySet().size();

			List<Double> elementRecalls = scoreElements(responseTable, keyTable);
			int keySize = keyTable.keySet().size();

			PrecisionRecall score = calculateElementAverages(elementPrecisions,
					responseSize, elementRecalls, keySize);

			scores.addScores(score, elementPrecisions, elementRecalls);
		}
		return scores;
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
	 * Calculate score ratios for a set of elements in an equivalence set
	 * partition. These scores are averaged to get precision and recall.
	 * 
	 * @param numTable
	 *            set table of the score numerator
	 * @param denTable
	 *            set table of the score denominator
	 * @return list of scores for individual elements
	 */
	private List<Double> scoreElements(Map<T, Set<T>> numTable,
			Map<T, Set<T>> denTable) {
		List<Double> elementScores = new LinkedList<Double>();
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
			elementScores.add(numerator / denominator);
		}
		return elementScores;
	}

	/**
	 * Take the average of individual element scores to get precision and
	 * recall.
	 * 
	 * @param elementPrecisions
	 *            element precision scores
	 * @param responseSize
	 *            size of the response set
	 * @param elementRecalls
	 *            element recall scores
	 * @param keySize
	 *            size of the key set
	 * @return precision recall score for this equivalence set
	 */
	private PrecisionRecall calculateElementAverages(
			List<Double> elementPrecisions, int responseSize,
			List<Double> elementRecalls, int keySize) {
		double precision = 0;
		double recall = 0;

		for (Double ratio : elementPrecisions)
			precision += ratio;
		precision /= responseSize;

		for (Double ratio : elementRecalls)
			recall += ratio;
		recall /= keySize;

		return new PrecisionRecall(precision, recall);
	}

}
