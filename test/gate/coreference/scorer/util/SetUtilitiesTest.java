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

package gate.coreference.scorer.util;

import static org.junit.Assert.assertEquals;

import gate.coreference.scorer.util.SetUtilities;

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
