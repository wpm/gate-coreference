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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class PrecisionRecallAveragesTest {

	private static final double TOLERANCE = 1e-6;
	private BCubed<Integer> bcubed;

	private List<List<Set<Set<Integer>>>> sets;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bcubed = new BCubed<Integer>();
		sets = new LinkedList<List<Set<Set<Integer>>>>();

		int[][] key1Values = { { 1, 2 }, { 3, 4 } };
		int[][] response1Values = { { 1, 2, 3 } };
		Set<Set<Integer>> key1 = TestUtilities
				.createEquivalenceSets(key1Values);
		Set<Set<Integer>> response1 = TestUtilities
				.createEquivalenceSets(response1Values);
		List<Set<Set<Integer>>> set1 = new LinkedList<Set<Set<Integer>>>();
		set1.add(key1);
		set1.add(response1);

		int[][] key2Values = { { 5, 6 }, { 7, 8 } };
		int[][] response2Values = { { 5, 8, 9 } };
		Set<Set<Integer>> key2 = TestUtilities
				.createEquivalenceSets(key2Values);
		Set<Set<Integer>> response2 = TestUtilities
				.createEquivalenceSets(response2Values);
		List<Set<Set<Integer>>> set2 = new LinkedList<Set<Set<Integer>>>();
		set2.add(key2);
		set2.add(response2);
		
		sets.add(set1);
		sets.add(set2);
	}

	@Test
	public void testBCubed() {
		PrecisionRecall microAverage, macroAverage;

		PrecisionRecallAverages precisionRecallAverages = bcubed
				.scoreMultipleSets(sets);

		microAverage = precisionRecallAverages.getMicroAverage();
		// 7/18 = 0.38....
		assertEquals(7.0/18.0, microAverage.getPrecision(), TOLERANCE);
		// 7/16 = 0.4375
		assertEquals(0.4375, microAverage.getRecall(), TOLERANCE);

		macroAverage = precisionRecallAverages.getMacroAverage();
		// 7/18 = 0.38....
		assertEquals(7.0/18.0, macroAverage.getPrecision(), TOLERANCE);
		// 7/16 = 0.4375
		assertEquals(0.4375, macroAverage.getRecall(), TOLERANCE);
	}
}
