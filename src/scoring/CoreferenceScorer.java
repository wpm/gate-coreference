package scoring;

import java.util.Set;

/**
 * Coreference scorer
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public interface CoreferenceScorer<T> {

	/**
	 * Return precision and recall scores for a partitioning
	 * 
	 * @param response
	 *            response partitioning
	 * @return <precision, recall> array
	 */
	public double[] score(Set<Set<T>> response);
}
