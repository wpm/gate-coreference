package gate.coreference;

import java.util.HashSet;
import java.util.Set;

/**
 * Set operation utilities.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class SetUtilities {

	/**
	 * Set union
	 * 
	 * @param <T>
	 *            element type
	 * @param a
	 *            set
	 * @param b
	 *            set
	 * @return union of a and b
	 */
	static public <T> Set<T> union(Set<T> a, Set<T> b) {
		Set<T> union = new HashSet<T>(a);
		union.addAll(b);
		return union;
	}

	/**
	 * Set intersection
	 * 
	 * @param <T>
	 *            element type
	 * @param a
	 *            set
	 * @param b
	 *            set
	 * @return intersection of a and b
	 */
	static public <T> Set<T> intersection(Set<T> a, Set<T> b) {
		Set<T> intersection = new HashSet<T>(a);
		intersection.retainAll(b);
		return intersection;
	}

	/**
	 * Set difference
	 * 
	 * @param <T>
	 *            element type
	 * @param a
	 *            set
	 * @param b
	 *            set
	 * @return all the elements of a not in b
	 */
	static public <T> Set<T> difference(Set<T> a, Set<T> b) {
		Set<T> difference = new HashSet<T>(a);
		difference.removeAll(b);
		return difference;
	}
}