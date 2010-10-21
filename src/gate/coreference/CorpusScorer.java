/**
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;

/**
 * Coreference precision/recall scores for a corpus.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class CorpusScorer {

	final private static String DEFAULT_KEY_NAME = "Key";

	final Map<Document, Map<Method, PrecisionRecall>> scores;

	/**
	 * Scoring is done over sets of (Start, End) offset pairs.
	 */
	private EquivalenceClassScorerFactory<List<Long>> scorerFactory = new EquivalenceClassScorerFactory<List<Long>>();

	/**
	 * @param corpus
	 *            corpus to score
	 * @param methods
	 *            scoring methods, e.g. B-Cubed or MUC
	 */
	public CorpusScorer(Corpus corpus, Set<Method> methods) {
		scores = new HashMap<Document, Map<Method, PrecisionRecall>>();
		for (Object object : corpus) {
			Document document = (Document) object;
			if (!scores.containsKey(document))
				scores.put(document, new HashMap<Method, PrecisionRecall>());
			Map<Method, PrecisionRecall> documentScores = scores.get(document);
			for (Method method : methods) {
				PrecisionRecall score = scoreDocument(document, method);
				documentScores.put(method, score);
			}
		}
	}

	/**
	 * @return the scores
	 */
	public Map<Document, Map<Method, PrecisionRecall>> getScores() {
		return scores;
	}

	/**
	 * Generate coreference scores for a single document
	 * <p>
	 * Use the default match feature and key and response names.
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
	 * Generate coreference scores for a single document
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
		EquivalenceClassScorer<List<Long>> scorer = scorerFactory.getScorer(
				method, key);
		double[] scores = scorer.score(response);
		return new PrecisionRecall(scores[0], scores[1]);
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

		// Create the scorer.
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
					PrecisionRecall mucScore = scores
							.get(EquivalenceClassScorerFactory.Method.MUC);
					if (null != mucScore)
						System.out.format("MUC %s\n", mucScore);
					PrecisionRecall bcubedScore = scores
							.get(EquivalenceClassScorerFactory.Method.BCUBED);
					if (null != bcubedScore)
						System.out.format("B-Cubed %s\n", bcubedScore);
				}
			} finally {
				Factory.deleteResource(corpus);
			}
		} finally {
			dataStore.close();
		}
	}
}
