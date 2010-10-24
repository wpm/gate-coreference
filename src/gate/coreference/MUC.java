package gate.coreference;

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
