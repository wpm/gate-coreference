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

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.coreference.EquivalenceClassScorerFactory.Method;
import gate.persist.PersistenceException;
import gate.util.GateException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CorpusScorerTest {

	private DataStore dataStore;
	private Corpus corpus;
	private Map<Document, Map<Method, PrecisionRecall>> corpusScores;

	@BeforeClass
	public static void initializeGate() throws GateException {
		Gate.init();
	}

	@Before
	public void setUp() throws Exception {
		dataStore = Factory.openDataStore("gate.persist.SerialDataStore",
				new File("test-datastore").toURI().toString());
		corpus = Datastore.loadCorpusFromDatastore(dataStore, "Coreference");

		Set<Method> methods = new HashSet<Method>();
		methods.add(EquivalenceClassScorerFactory.Method.MUC);
		methods.add(EquivalenceClassScorerFactory.Method.BCUBED);
		CorpusScorer scorer = new CorpusScorer(corpus, methods);
		corpusScores = scorer.getScores();
	}

	@After
	public void tearDown() throws PersistenceException {
		Factory.deleteResource(corpus);
		dataStore.close();
	}

	/**
	 * Key and response are the same.
	 */
	@Test
	public void allPrecisionAllRecall() {
		Map<Method, PrecisionRecall> scores = getScoresByName("All Precision All Recall");
		assertEquals(new PrecisionRecall(1, 1), scores.get(Method.MUC));
		assertEquals(new PrecisionRecall(1, 1), scores.get(Method.BCUBED));
	}

	@Test
	public void somePrecisionSomeRecall() {
		Map<Method, PrecisionRecall> scores = getScoresByName("Some Precision Some Recall");
		assertEquals(new PrecisionRecall(2.0 / 3.0, 2.0 / 3.0),
				scores.get(Method.MUC));
		assertEquals(new PrecisionRecall(2.0 / 3.0, 2.0 / 3.0),
				scores.get(Method.BCUBED));
	}

	/**
	 * Document contains no coreference information.
	 */
	@Test
	public void noKeyNoResponse() {
		Map<Method, PrecisionRecall> scores = getScoresByName("No Key No Response");
		assertEquals(null, scores.get(Method.MUC));
		assertEquals(null, scores.get(Method.BCUBED));
	}

	/**
	 * Document contains a key but no response.
	 */
	@Test
	public void keyNoResponse() {
		Map<Method, PrecisionRecall> scores = getScoresByName("Key No Response");
		assertEquals(new PrecisionRecall(Double.NaN, 0), scores.get(Method.MUC));
		assertEquals(new PrecisionRecall(Double.NaN, 0),
				scores.get(Method.BCUBED));
	}

	/**
	 * Retrieve the coreference scores for a document with a particular name.
	 * This assumes document names are unique in the corpus.
	 * 
	 * @param documentName
	 *            name of the document
	 * @return scores for the document
	 */
	private Map<Method, PrecisionRecall> getScoresByName(String documentName) {
		for (Entry<Document, Map<Method, PrecisionRecall>> documentScores : corpusScores
				.entrySet()) {
			String name = documentScores.getKey().getName();
			Map<Method, PrecisionRecall> scores = documentScores.getValue();
			if (documentName.equals(name))
				return scores;
		}
		return null;
	}
}
