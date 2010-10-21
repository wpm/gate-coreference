package gate.coreference;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import gate.coreference.BCubed;

import java.util.HashSet;
import java.util.Set;

/**
 * Test cases taken from:
 * <p>
 * Amit Bagga, Breck Baldwin, LREC 1998,
 * "Algorithms for scoring coreference chains"
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class EquivalenceSetScoringTest {

	private static final double TOLERANCE = 1e-6;
	private BCubed<Integer> bcubed;
	private MUC<Integer> muc;

	private Set<Set<Integer>> response;

	@Before
	public void setUp() throws Exception {
		int[][] keyValues = { { 1, 2, 3, 4, 5 }, { 6, 7 }, { 8, 9, 10, 11, 12 } };
		Set<Set<Integer>> key = createEquivalenceSets(keyValues);
		bcubed = new BCubed<Integer>(key);
		muc = new MUC<Integer>(key);
		int[][] responseValues = { { 1, 2, 3, 4, 5 },
				{ 6, 7, 8, 9, 10, 11, 12 } };
		response = createEquivalenceSets(responseValues);
	}

	@Test
	public void testBCubed() {
		double[] scores = bcubed.score(response);
		// Precision
		assertEquals(16.0 / 21.0, scores[0], TOLERANCE);
		// Recall
		assertEquals(1, scores[1], TOLERANCE);
	}

	@Test
	public void testBCubedNoOverlap() {
		int[][] responseValues = { { 13, 14, 15 } };
		Set<Set<Integer>> noOverlapResponse = createEquivalenceSets(responseValues);
		double[] scores = bcubed.score(noOverlapResponse);
		// Precision
		assertEquals(0, scores[0], TOLERANCE);
		// Recall
		assertEquals(0, scores[1], TOLERANCE);

	}

	public void testMUC() {
		double[] scores = muc.score(response);
		// Precision
		assertEquals(0.9, scores[0], TOLERANCE);
		// Recall
		assertEquals(1, scores[1], TOLERANCE);
	}

	private Set<Set<Integer>> createEquivalenceSets(int[][] valueSets) {
		Set<Set<Integer>> partition = new HashSet<Set<Integer>>();
		for (int[] valueSet : valueSets) {
			Set<Integer> set = new HashSet<Integer>();
			for (int i : valueSet)
				set.add(i);
			partition.add(set);
		}
		return partition;
	}
}
