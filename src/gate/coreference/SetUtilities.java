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
	 * Union of a set of sets
	 * 
	 * @param <T>
	 *            element type
	 * @param sets
	 *            sets to combine
	 * @return union of the sets
	 */
	static public <T> Set<T> union(Set<Set<T>> sets) {
		Set<T> union = new HashSet<T>();
		for (Set<T> set : sets)
			union.addAll(set);
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