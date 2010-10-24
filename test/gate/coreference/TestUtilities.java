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
import java.util.TreeSet;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class TestUtilities {

	final static public Set<Set<Integer>> createEquivalenceSets(int[][] valueSets) {
		Set<Set<Integer>> partition = new HashSet<Set<Integer>>();
		for (int[] valueSet : valueSets) {
			Set<Integer> set = new TreeSet<Integer>();
			for (int i : valueSet)
				set.add(i);
			partition.add(set);
		}
		return partition;
	}

}
