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

import gate.AnnotationSet;
import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.coreference.EquivalenceClassScorerFactory.Method;
import gate.creole.ANNIEConstants;
import gate.util.GateException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Coreference precision/recall scores for a corpus. This calculates coreference
 * scores for a corpus of documents using the specified scoring methods
 * <p>
 * This class does lazy calculation of scores. It maintains a table of scores
 * for each document, but only calculates them as needed when the getScores
 * function is called. The addDocument and removeDocument functions should be
 * called whenever a document is added or removed from the corpus.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class CorpusScorer {

	static Logger logger = Logger.getLogger(CorpusScorer.class.getName());

	final public static String DEFAULT_KEY_NAME = "Key";

	/**
	 * Order the documents in the table by name. The alphabetical order is
	 * determined by the locale settings.
	 */
	public class DocumentCollator implements Comparator<Document> {
		final private Collator collator;

		public DocumentCollator() {
			collator = Collator.getInstance(Locale.getDefault());
			collator.setStrength(Collator.TERTIARY);
		}

		@Override
		public int compare(Document d1, Document d2) {
			return collator.compare(d1.getName(), d2.getName());
		}

	}

	/**
	 * Scoring is done over sets of (Start, End) offset pairs which are stored
	 * as lists of long values.
	 */
	private EquivalenceClassScorerFactory<List<Long>> scorerFactory = new EquivalenceClassScorerFactory<List<Long>>();

	/**
	 * The scoring methods to use.
	 */
	final private Set<Method> methods;

	/**
	 * The scores table. This is a map of document->method->score.
	 */
	private Map<Document, Map<Method, PrecisionRecall>> scores;

	/**
	 * Create a corpus scorer. This adds all the documents to the scores table
	 * with empty scores.
	 * 
	 * @param corpus
	 *            corpus to score
	 * @param methods
	 *            scoring methods, e.g. B-Cubed or MUC
	 */
	public CorpusScorer(Corpus corpus, Set<Method> methods) {
		this.methods = methods;
		// Create a scores table with empty entries for all the documents.
		scores = new TreeMap<Document, Map<Method, PrecisionRecall>>(
				new DocumentCollator());
		for (Object object : corpus)
			addDocument((Document) object);
	}

	/**
	 * Add an empty element to the scores table for this document.
	 * 
	 * @param document
	 *            document to add
	 */
	public void addDocument(Document document) {
		scores.put(document, null);
	}

	/**
	 * Remove this document from the scores table.
	 * 
	 * @param document
	 *            document to remove
	 */
	public void removeDocument(Document document) {
		scores.remove(document);
	}

	/**
	 * Reset the scores for a document so that they will be recalculated.
	 * 
	 * @param document
	 *            document whose scores are reset
	 */
	public void resetDocumentScores(Document document) {
		scores.put(document, null);
	}

	/**
	 * Return the scores for all the documents in the corpus, calculating scores
	 * as needed.
	 * 
	 * @return the scores table
	 */
	public Map<Document, Map<Method, PrecisionRecall>> getScores() {
		// Enumerate all the documents in the scores table.
		for (Entry<Document, Map<Method, PrecisionRecall>> entry : scores
				.entrySet()) {
			Document document = entry.getKey();
			Map<Method, PrecisionRecall> documentScores = entry.getValue();
			// If a document's scores entry is null it has not been scored yet,
			// so score it now and add the result to the scores table.
			if (null == documentScores) {
				documentScores = new HashMap<Method, PrecisionRecall>();
				scores.put(document, documentScores);
				for (Method method : methods) {
					PrecisionRecall documentScore = scoreDocument(document,
							method);
					documentScores.put(method, documentScore);
				}
			}
		}
		// At this point all the documents in the corpus have been scored.
		return scores;
	}

	/**
	 * Generate coreference scores for a single document. Use the default match
	 * feature and key and response names.
	 * 
	 * @param document
	 *            document to score
	 * @return precision/recall scores for this document
	 */
	private PrecisionRecall scoreDocument(Document document, Method method) {
		return scoreDocument(document,
				ANNIEConstants.DOCUMENT_COREF_FEATURE_NAME, DEFAULT_KEY_NAME,
				null, method);
	}

	/**
	 * Generate coreference scores for a single document.
	 * 
	 * @param document
	 *            document to score
	 * @param matchFeature
	 *            name of the document matches feature, e.g. MatchesAnnots
	 * @param keyName
	 *            name of the key match sets in the matches annotation, e.g. Key
	 * @param responseName
	 *            name of the response match sets in the matches annotation,
	 *            e.g. null
	 * @return precision/recall scores for this document
	 */
	private PrecisionRecall scoreDocument(Document document,
			String matchFeature, String keyName, String responseName,
			Method method) {
		logger.debug("Score " + document.getName() + " " + method);
		FeatureMap features = document.getFeatures();

		// Documents without coreference information get a null score.
		if (!features.containsKey(matchFeature))
			return null;

		// Extract the coreference information.
		@SuppressWarnings("unchecked")
		Map<String, Collection<Collection<Integer>>> matchIDsets = (Map<String, Collection<Collection<Integer>>>) features
				.get(matchFeature);
		Set<Set<List<Long>>> key = getMatchSets(document, matchIDsets, keyName);
		Set<Set<List<Long>>> response = getMatchSets(document, matchIDsets,
				responseName);

		// Generate score.
		EquivalenceClassScorer<List<Long>> scorer = scorerFactory
				.getScorer(method);
		return scorer.score(key, response);
	}

	/**
	 * Map sets of annotation IDs to sets of (Start, End) offset pairs.
	 * <p>
	 * The key and response sets will list different annotation IDs that
	 * correspond to the same offsets.
	 * 
	 * @param document
	 *            GATE document
	 * @param matchIDs
	 *            matching annotation IDs, e.g. from the MatchesAnnots feature
	 *            of a document
	 * @param annotationSet
	 *            set name, e.g. "key" or null
	 * @return set of sets of (Start, End) offset pairs corresponding to the
	 *         annotation IDs.
	 */
	private Set<Set<List<Long>>> getMatchSets(Document document,
			Map<String, Collection<Collection<Integer>>> matchIDs,
			String annotationSet) {
		Set<Set<List<Long>>> matchOffsetSets = new HashSet<Set<List<Long>>>();

		Collection<Collection<Integer>> matchIDsets = matchIDs
				.get(annotationSet);
		if (null == matchIDsets)
			return matchOffsetSets;

		AnnotationSet annotations = document.getAnnotations(annotationSet);
		for (Collection<Integer> matchIDset : matchIDsets) {
			Set<List<Long>> offsetSet = new HashSet<List<Long>>();

			for (Integer matchID : matchIDset) {
				List<Long> offsets = new ArrayList<Long>();
				Long start = annotations.get(matchID).getStartNode()
						.getOffset();
				Long end = annotations.get(matchID).getEndNode().getOffset();

				offsets.add(0, start);
				offsets.add(1, end);
				offsetSet.add(offsets);
			}
			matchOffsetSets.add(offsetSet);
		}
		return matchOffsetSets;
	}

	/**
	 * Print precision/recall scores for all the documents in a corpus in a data
	 * store.
	 * 
	 * @param args
	 *            first argument is the data store path, second argument is the
	 *            corpus name
	 * @throws GateException
	 */
	public static void main(String[] args) throws GateException {
		BasicConfigurator.configure();

		String dataStorePath = args[0];
		String corpusName = args[1];

		Gate.init();

		// Use both scoring methods.
		Set<Method> methods = new HashSet<Method>();
		methods.add(EquivalenceClassScorerFactory.Method.MUC);
		methods.add(EquivalenceClassScorerFactory.Method.BCUBED);

		// Open the data store.
		DataStore dataStore = Factory.openDataStore(
				"gate.persist.SerialDataStore", new File(dataStorePath).toURI()
						.toString());
		try {
			// Extract lists of corpus names and LRIDs from the data store.
			// These lists have corresponding elements.
			Corpus corpus = Datastore.loadCorpusFromDatastore(dataStore,
					corpusName);
			try {
				CorpusScorer scorer = new CorpusScorer(corpus, methods);
				Map<Document, Map<Method, PrecisionRecall>> corpusScores = scorer
						.getScores();
				for (Entry<Document, Map<Method, PrecisionRecall>> documentScores : corpusScores
						.entrySet()) {
					Document document = documentScores.getKey();
					Map<Method, PrecisionRecall> scores = documentScores
							.getValue();

					System.out.println(document.getName());
					PrecisionRecall mucScore = scores.get(Method.MUC);
					System.out.format("\tMUC: %s\n", mucScore);
					PrecisionRecall bCubedScore = scores.get(Method.BCUBED);
					System.out.format("\tB-Cubed: %s\n", bCubedScore);
				}
			} finally {
				Factory.deleteResource(corpus);
			}
		} finally {
			dataStore.close();
		}
	}
}
