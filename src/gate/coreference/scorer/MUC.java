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

package gate.coreference.scorer;


import gate.coreference.scorer.util.NumericUtilities;
import gate.coreference.scorer.util.SetUtilities;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * MUC coreference scorer
 * <p>
 * Amit Bagga, Breck Baldwin, LREC 1998,
 * "Algorithms for scoring coreference chains"
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class MUC<T> implements EquivalenceClassScorer<T> {

	@Override
	public PrecisionRecall score(Set<Set<T>> key, Set<Set<T>> response) {
		double precision = MUCscore(response, key);
		double recall = MUCscore(key, response);
		return new PrecisionRecall(precision, recall);
	}

	@Override
	public PrecisionRecallAverages scoreMultipleSets(
			Iterable<List<Set<Set<T>>>> sets) {
		MUCPrecisionRecallAverages scores = new MUCPrecisionRecallAverages();
		for (List<Set<Set<T>>> equivalenceSets : sets) {
			int numerator, denominator;
			double precision, recall;

			Set<Set<T>> key = equivalenceSets.get(0);
			Set<Set<T>> response = equivalenceSets.get(1);

			// Precision
			List<List<Integer>> precisionTerms = MUCscoreTerms(response, key);
			numerator = NumericUtilities.sumTerms(precisionTerms.get(0));
			denominator = NumericUtilities.sumTerms(precisionTerms.get(1));
			precision = (double) numerator / denominator;

			// Recall
			List<List<Integer>> recallTerms = MUCscoreTerms(key, response);
			numerator = NumericUtilities.sumTerms(recallTerms.get(0));
			denominator = NumericUtilities.sumTerms(recallTerms.get(1));
			recall = (double) numerator / denominator;

			PrecisionRecall score = new PrecisionRecall(precision, recall);
			scores.addScore(score);
			scores.addPrecisionTerms(precisionTerms.get(0), precisionTerms.get(1));
			scores.addRecallTerms(recallTerms.get(0), recallTerms.get(1));
		}
		return scores;
	}

	/**
	 * Calculate MUC score. Precision and recall are obtained by swapping the
	 * key and response sets.
	 * 
	 * @param keySets
	 *            key equivalence classes
	 * @param responseSets
	 *            response equivalence classes
	 * @return MUC score of the key sets partitioned on the response sets
	 */
	private double MUCscore(Set<Set<T>> keySets, Set<Set<T>> responseSets) {
		int numerator = 0;
		int denominator = 0;
		for (Set<T> keySet : keySets) {
			int s = keySet.size();
			numerator += s - partitionSize(keySet, responseSets);
			denominator += s - 1;
		}
		return ((double) numerator) / denominator;
	}

	/**
	 * Calculate numerator and denominator terms in a MUC score. Precision and
	 * recall are obtained by swapping the key and response sets. This is used
	 * instead of {@link MUCscore} when we are calculating macro averages.
	 * 
	 * @param keySets
	 *            key equivalence classes
	 * @param responseSets
	 *            response equivalence classes
	 * @return list of terms in the numerator and denominator of the score
	 */
	private List<List<Integer>> MUCscoreTerms(Set<Set<T>> keySets,
			Set<Set<T>> responseSets) {
		List<List<Integer>> terms = new LinkedList<List<Integer>>();
		List<Integer> numeratorTerms = new LinkedList<Integer>();
		List<Integer> denominatorTerms = new LinkedList<Integer>();

		for (Set<T> keySet : keySets) {
			int s = keySet.size();
			numeratorTerms.add(s - partitionSize(keySet, responseSets));
			denominatorTerms.add(s - 1);
		}
		terms.add(numeratorTerms);
		terms.add(denominatorTerms);
		return terms;
	}

	/**
	 * @param keySet
	 *            key set
	 * @param responseSets
	 *            response sets on which to partition the key set
	 * @return size of the partition of the key set on the response sets
	 */
	private int partitionSize(Set<T> keySet, Set<Set<T>> responseSets) {
		Set<T> union = SetUtilities.union(responseSets);
		int n = SetUtilities.difference(keySet, union).size();
		for (Set<T> responseSet : responseSets)
			if (!SetUtilities.intersection(keySet, responseSet).isEmpty())
				n++;
		return n;
	}

}
