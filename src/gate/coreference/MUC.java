package gate.coreference;

import java.util.HashSet;
import java.util.Set;

/**
 * MUC coreference scorer
 * <p>
 * Amit Bagga, Breck Baldwin, LREC 1998,
 * "Algorithms for scoring coreference chains"
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class MUC<T> implements CoreferenceScorer<T> {

	private Set<Set<T>> key;

	/**
	 * @param key
	 *            key partitioning
	 */
	MUC(Set<Set<T>> key) {
		this.key = key;
	}

	@Override
	public double[] score(Set<Set<T>> response) {
		double[] scores = { 0.0, 0.0 };
		scores[0] = MUCscore(key, response);
		scores[1] = MUCscore(response, key);
		return scores;
	}

	private double MUCscore(Set<Set<T>> keySets, Set<Set<T>> responseSets) {
		double score = 0.0;
		Set<T> responseUnion = new HashSet<T>();
		for (Set<T> responseSet : responseSets)
			responseUnion.addAll(responseSet);
		for (Set<T> keySet : keySets) {
			int num = 0;
			for (Set<T> responseSet : responseSets)
				num += SetUtilities.intersection(keySet, responseSet).size() - 1;
			// Implicit partitions for elements not in the response.
			num += SetUtilities.difference(keySet, responseUnion).size();
			double den = keySet.size() - 1;
			score += num / den;
		}
		return score;
	}

}
