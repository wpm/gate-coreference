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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import gate.coreference.scorer.BCubed;
import gate.coreference.scorer.MUC;
import gate.coreference.scorer.PrecisionRecall;
import gate.coreference.scorer.util.TestUtilities;

import java.util.Set;

/**
 * Test cases taken from:
 * <p>
 * Amit Bagga, Breck Baldwin, LREC 1998,
 * "Algorithms for scoring coreference chains"
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class EquivalenceClassScoringTest {

	private static final double TOLERANCE = 1e-6;
	private BCubed<Integer> bcubed;
	private MUC<Integer> muc;

	private Set<Set<Integer>> key, response;
	private Set<Set<Integer>> keyMissingResponse, responseMissingResponse;
	private Set<Set<Integer>> keyNoCommon, responseNoCommon;
	private Set<Set<Integer>> keyMissingKey, responseMissingKey;

	@Before
	public void setUp() throws Exception {
		bcubed = new BCubed<Integer>();
		muc = new MUC<Integer>();
		// Test case taken from the Bagga and Baldwin paper
		int[][] keyValues = { { 1, 2, 3, 4, 5 }, { 6, 7 }, { 8, 9, 10, 11, 12 } };
		int[][] responseValues = { { 1, 2, 3, 4, 5 },
				{ 6, 7, 8, 9, 10, 11, 12 } };
		key = TestUtilities.createEquivalenceSets(keyValues);
		response = TestUtilities.createEquivalenceSets(responseValues);
		// The response set is missing values
		int keyMissingResponseValues[][] = { { 1, 2 }, { 3, 4 } };
		int responseMissingResponseValues[][] = { { 1, 2 } };
		keyMissingResponse = TestUtilities
				.createEquivalenceSets(keyMissingResponseValues);
		responseMissingResponse = TestUtilities
				.createEquivalenceSets(responseMissingResponseValues);
		// The key set is missing values
		int keyMissingKeyValues[][] = { { 1, 2 } };
		int responseMissingKeyValues[][] = { { 1, 2 }, { 3, 4 } };
		keyMissingKey = TestUtilities
				.createEquivalenceSets(keyMissingKeyValues);
		responseMissingKey = TestUtilities
				.createEquivalenceSets(responseMissingKeyValues);
		// The key and response sets have no elements in common
		int keyNoCommonValues[][] = { { 1, 2 }, { 3, 4, 5 } };
		int responseNoCommonValues[][] = { { 6, 7 }, { 8, 9, 10 } };
		keyNoCommon = TestUtilities.createEquivalenceSets(keyNoCommonValues);
		responseNoCommon = TestUtilities
				.createEquivalenceSets(responseNoCommonValues);
	}

	@Test
	public void testBCubed() {
		PrecisionRecall scores = bcubed.score(key, response);
		assertEquals(16.0 / 21.0, scores.getPrecision(), TOLERANCE);
		assertEquals(1, scores.getRecall(), TOLERANCE);
	}

	@Test
	public void testBCubedNoCommonValues() {
		PrecisionRecall scores = bcubed.score(keyNoCommon, responseNoCommon);
		assertEquals(0, scores.getPrecision(), TOLERANCE);
		assertEquals(0, scores.getRecall(), TOLERANCE);
	}

	@Test
	public void testBCubedMissingResponseValue() {
		PrecisionRecall scores = bcubed.score(keyMissingResponse,
				responseMissingResponse);
		assertEquals(1, scores.getPrecision(), TOLERANCE);
		assertEquals(0.5, scores.getRecall(), TOLERANCE);
	}

	@Test
	public void testBCubedMissingKeyValue() {
		PrecisionRecall scores = bcubed
				.score(keyMissingKey, responseMissingKey);
		assertEquals(0.5, scores.getPrecision(), TOLERANCE);
		assertEquals(1, scores.getRecall(), TOLERANCE);
	}

	@Test
	public void testMUC() {
		PrecisionRecall scores = muc.score(key, response);
		assertEquals(0.9, scores.getPrecision(), TOLERANCE);
		assertEquals(1, scores.getRecall(), TOLERANCE);
	}

	@Test
	public void testMUCNoCommonValues() {
		PrecisionRecall scores = muc.score(keyNoCommon, responseNoCommon);
		assertEquals(0, scores.getPrecision(), TOLERANCE);
		assertEquals(0, scores.getRecall(), TOLERANCE);
	}

	@Test
	public void testMUCMissingResponseValue() {
		PrecisionRecall scores = muc.score(keyMissingResponse,
				responseMissingResponse);
		assertEquals(1, scores.getPrecision(), TOLERANCE);
		assertEquals(0.5, scores.getRecall(), TOLERANCE);
	}

	@Test
	public void testMUCMissingKeyValue() {
		PrecisionRecall scores = muc.score(keyMissingKey, responseMissingKey);
		assertEquals(0.5, scores.getPrecision(), TOLERANCE);
		assertEquals(1, scores.getRecall(), TOLERANCE);
	}
}
