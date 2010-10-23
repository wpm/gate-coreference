package gate.coreference;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import gate.coreference.BCubed;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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

	@Before
	public void setUp() throws Exception {
		bcubed = new BCubed<Integer>();
		muc = new MUC<Integer>();
		// A typical test case.
		int[][] keyValues = { { 1, 2, 3, 4, 5 }, { 6, 7 }, { 8, 9, 10, 11, 12 } };
		int[][] responseValues = { { 1, 2, 3, 4, 5 },
				{ 6, 7, 8, 9, 10, 11, 12 } };
		key = createEquivalenceSets(keyValues);
		response = createEquivalenceSets(responseValues);
		// The response set is missing values.
		int keyMissingKeyValues[][] = { { 1, 2 }, { 3, 4 } };
		int responseMissingKeyValues[][] = { { 1, 2 } };
		keyMissingResponse = createEquivalenceSets(keyMissingKeyValues);
		responseMissingResponse = createEquivalenceSets(responseMissingKeyValues);
		// The key and response sets have no elements in common
		int keyNoCommonValues[][] = { { 1, 2 }, { 3, 4, 5 } };
		int responseNoCommonValues[][] = { { 6, 7 }, { 8, 9, 10 } };
		keyNoCommon = createEquivalenceSets(keyNoCommonValues);
		responseNoCommon = createEquivalenceSets(responseNoCommonValues);
	}

	@Test
	public void testBCubed() {
		double[] scores = bcubed.score(key, response);
		// Precision
		assertEquals(16.0 / 21.0, scores[0], TOLERANCE);
		// Recall
		assertEquals(1, scores[1], TOLERANCE);
	}

	@Test
	public void testBCubedNoCommonValues() {
		double[] scores = bcubed.score(keyNoCommon, responseNoCommon);
		// Precision
		assertEquals(0, scores[0], TOLERANCE);
		// Recall
		assertEquals(0, scores[1], TOLERANCE);
	}

	@Test
	public void testBCubedMissingResponseValue() {
		double[] scores = bcubed.score(keyMissingResponse,
				responseMissingResponse);
		// Precision
		assertEquals(1, scores[0], TOLERANCE);
		// Recall
		assertEquals(0.5, scores[1], TOLERANCE);
	}

	public void testMUC() {
		double[] scores = muc.score(key, response);
		// Precision
		assertEquals(0.9, scores[0], TOLERANCE);
		// Recall
		assertEquals(1, scores[1], TOLERANCE);
	}

	private Set<Set<Integer>> createEquivalenceSets(int[][] valueSets) {
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
