package scoring;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;


public class CoreferenceScoringTest {

	private BCubed<Integer> bcubed;

	@Before
	public void setUp() throws Exception {
		int[][] keyValues = { { 1, 2, 3, 4, 5 }, { 6, 7 }, { 8, 9, 10, 11, 12 } };
		bcubed = new BCubed<Integer>(createPartition(keyValues));
	}

	@Test
	public void testBCubed() {
		int[][] responseValues = { { 1, 2, 3, 4, 5 },
				{ 6, 7, 8, 9, 10, 11, 12 } };
		Set<Set<Integer>> response = createPartition(responseValues);
		double[] scores = bcubed.score(response);
		// Precision
		assertEquals(16.0/21.0, scores[0], 1e-05);
		// Recall
		assertEquals(1, scores[1], 1e-05);
	}

	private Set<Set<Integer>> createPartition(int[][] valueSets) {
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
