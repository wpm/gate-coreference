package gate.coreference;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for scoring methods that compute precision and recall between sets
 * of sets.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 * 
 * @param <T>
 *            element type
 */
public class SetScorer<T> {

	/**
	 * Set union
	 * 
	 * @param a
	 *            set
	 * @param b
	 *            set
	 * @return union of a and b
	 */
	protected Set<T> union(Set<T> a, Set<T> b) {
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
	protected Set<T> intersection(Set<T> a, Set<T> b) {
		Set<T> intersection = new HashSet<T>();
		intersection.addAll(a);
		intersection.retainAll(b);
		return intersection;
	}

	/**
	 * Set difference
	 * 
	 * @param a
	 *            set
	 * @param b
	 *            set
	 * @return all the elements of a not in b
	 */
	protected Set<T> difference(Set<T> a, Set<T> b) {
		Set<T> difference = new HashSet<T>();
		difference.addAll(a);
		difference.removeAll(b);
		return difference;
	}
}