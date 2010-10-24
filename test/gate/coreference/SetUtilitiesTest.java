package gate.coreference;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class SetUtilitiesTest {

	Set<Integer> a, b;

	@Before
	public void setUp() {
		a = new TreeSet<Integer>();
		a.add(1);
		a.add(2);
		a.add(3);
		b = new TreeSet<Integer>();
		b.add(2);
		b.add(3);
		b.add(4);
	}

	@Test
	public void testPairUnion() {
		Set<Integer> expected = new TreeSet<Integer>();
		expected.add(1);
		expected.add(2);
		expected.add(3);
		expected.add(4);
		Set<Integer> actual = SetUtilities.union(a, b);
		assertEquals(expected, actual);
	}

	@Test
	public void testListUnion() {
		Set<Integer> expected = new TreeSet<Integer>();
		expected.add(1);
		expected.add(2);
		expected.add(3);
		expected.add(4);
		Set<Set<Integer>> sets = new HashSet<Set<Integer>>();
		sets.add(a);
		sets.add(b);
		Set<Integer> actual = SetUtilities.union(sets);
		assertEquals(expected, actual);
	}

	@Test
	public void testIntersection() {
		Set<Integer> expected = new TreeSet<Integer>();
		expected.add(2);
		expected.add(3);
		Set<Integer> actual = SetUtilities.intersection(a, b);
		assertEquals(expected, actual);
	}

	@Test
	public void testDifference() {
		Set<Integer> expected = new TreeSet<Integer>();
		expected.add(1);
		Set<Integer> actual = SetUtilities.difference(a, b);
		assertEquals(expected, actual);
	}
}
