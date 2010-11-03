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

import java.util.Collection;
import java.util.List;

/**
 * Miscellaneous arithmetic utilities.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class NumericUtilities {

	/**
	 * @param <T>
	 *            numeric type
	 * @param values
	 *            collection of values
	 * @return the average of the values
	 */
	static public final <T extends Number> double average(Collection<T> values) {
		double average = 0;
		for (T value : values)
			average += value.doubleValue();
		average /= values.size();
		return average;
	}

	/**
	 * Sum a list of integers.
	 * 
	 * @param terms
	 * @return sum of the terms
	 */
	static public int sumTerms(List<Integer> terms) {
		int sum = 0;
		for (Integer term : terms)
			sum += term;
		return sum;
	}
}
