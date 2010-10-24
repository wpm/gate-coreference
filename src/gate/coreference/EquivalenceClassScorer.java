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
	 * @return Precision and recall scores
	 */
	public PrecisionRecall score(Set<Set<T>> key, Set<Set<T>> response);
}
