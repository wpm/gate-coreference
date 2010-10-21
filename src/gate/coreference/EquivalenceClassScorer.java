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
	 * Return precision and recall scores for a partitioning
	 * 
	 * @param response
	 *            response partitioning
	 * @return <precision, recall> array
	 */
	public double[] score(Set<Set<T>> response);
}
