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
import gate.creole.ANNIEConstants;
import gate.util.GateException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;

/**
 * Coreference precision/recall scores for a corpus.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class CorpusScorer {

	final private static String DEFAULT_KEY_NAME = "Key";

	private Map<Document, PrecisionRecall> score = new HashMap<Document, PrecisionRecall>();

	/**
	 * Scoring is done over sets of (Start, End) offset pairs.
	 */
	private CoreferenceScorerFactory<List<Long>> scorerFactory;

	/**
	 * Create a set of corpus scores
	 * 
	 * @param scorerFactory
	 *            specification of the scoring method, e.g. {@link BCubed}
	 */
	public CorpusScorer(CoreferenceScorerFactory<List<Long>> scorerFactory) {
		this.scorerFactory = scorerFactory;
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
	public PrecisionRecall scoreDocument(Document document) {
		return scoreDocument(document,
				ANNIEConstants.DOCUMENT_COREF_FEATURE_NAME, DEFAULT_KEY_NAME,
				null);
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
	public PrecisionRecall scoreDocument(Document document,
			String matchFeature, String keyName, String responseName) {
		FeatureMap features = document.getFeatures();

		// Documents without coreference information get a null score.
		if (!features.containsKey(matchFeature)) {
			score.put(document, null);
			return null;
		}

		// Extract the coreference information.
		@SuppressWarnings("unchecked")
		Map<String, Collection<Collection<Integer>>> matchIDsets = (Map<String, Collection<Collection<Integer>>>) features
				.get(matchFeature);
		Set<Set<List<Long>>> key = getMatchSets(document, matchIDsets, keyName);
		Set<Set<List<Long>>> response = getMatchSets(document, matchIDsets,
				responseName);

		// Generate score.
		CoreferenceScorer<List<Long>> scorer = scorerFactory.getScorer(
				CoreferenceScorerFactory.Method.BCUBED, key);
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
		CorpusScorer scorer = new CorpusScorer(
				new CoreferenceScorerFactory<List<Long>>());

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
				// Iterate over documents in the corpus and score them.
				@SuppressWarnings("rawtypes")
				Iterator iterator = corpus.iterator();
				while (iterator.hasNext()) {
					Document document = (Document) iterator.next();
					PrecisionRecall score = scorer.scoreDocument(document);
					if (null != score)
						System.out.format("%s: %s", document.getName(), score);
				}
			} finally {
				Factory.deleteResource(corpus);
			}
		} finally {
			dataStore.close();
		}
	}
}
