package gate.coreference;

import java.util.Set;

/**
 * Framework for generating a score that measures the similarity of two
 * equivalence classes.
 * 
 * @param T
 *            type of objects in equivalence sets
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public interface EquivalenceClassScorer<T> {

	/**
	 * Precision and recall scores for a pair of equivalence sets.
	 * 
	 * @param key
	 *            key equivalence classes
	 * @param response
	 *            response equivalence classes
	 * @return (precision, recall) array
	 */
	public double[] score(Set<Set<T>> key, Set<Set<T>> response);
}
