/**
 * Copyright 2010 W.P. McNeill
 */
package gate.coreference;

import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class CorpusScore {

	final private static String DEFAULT_MATCH_FEATURE = "MatchesAnnots";
	final private static String DEFAULT_KEY_FEATURE = "Key";

	private Map<Document, PrecisionRecall> score;
	/**
	 * Scoring is done over sets of (Start, End) offset pairs.
	 */
	private CoreferenceScorerFactory<List<Integer>> scorerFactory;

	/**
	 * 
	 */
	public CorpusScore(CoreferenceScorerFactory<List<Integer>> scorerFactory) {
		this.scorerFactory = scorerFactory;
	}

	/**
	 * Generate coreference scores for a single document
	 * 
	 * @param document
	 *            document to score
	 * @return precision/recall scores for this document
	 */
	private PrecisionRecall scoreDocument(Document document) {
		FeatureMap features = document.getFeatures();

		// Documents without coreference information get a null score.
		if (!features.containsKey(DEFAULT_MATCH_FEATURE)) {
			score.put(document, null);
			return null;
		}
		// Extract the coreference information.
		FeatureMap matchIDsets = (FeatureMap) features
				.get(DEFAULT_MATCH_FEATURE);
		Set<Set<List<Integer>>> key = getMatchSets(document, matchIDsets,
				DEFAULT_KEY_FEATURE);
		Set<Set<List<Integer>>> response = getMatchSets(document, matchIDsets,
				null);

		// Generate score.
		CoreferenceScorer<List<Integer>> scorer = scorerFactory.getScorer(
				CoreferenceScorerFactory.Method.BCUBED, key);
		double[] scores = scorer.score(response);
		return new PrecisionRecall(scores[0], scores[1]);
	}

	/**
	 * Map sets of annotation IDs to sets of (Start, End) offset pairs.
	 * 
	 * @param document
	 *            GATE document
	 * @param matchIDsets
	 *            matching annotation IDs, e.g. from the MatchesAnnots feature
	 *            of a document
	 * @param annotationSet
	 *            set name, e.g. "key" or null
	 * @return set of sets of (Start, End) offset pairs corresponding to the
	 *         annotation IDs.
	 */
	private Set<Set<List<Integer>>> getMatchSets(Document document,
			FeatureMap matchIDs, String annotationSet) {
		Set<Set<List<Integer>>> matchOffsetSets = new HashSet<Set<List<Integer>>>();

		@SuppressWarnings("unchecked")
		Set<Set<Integer>> matchIDsets = (Set<Set<Integer>>) matchIDs
				.get(annotationSet);
		AnnotationSet annotations = document.getAnnotations(annotationSet);
		for (Set<Integer> matchIDset : matchIDsets) {
			Set<List<Integer>> offsetSet = new HashSet<List<Integer>>();

			for (Integer matchID : matchIDset) {
				List<Integer> offsets = new ArrayList<Integer>();

				FeatureMap features = annotations.get(matchID).getFeatures();
				offsets.add(0, (Integer) features.get("Start"));
				offsets.add(1, (Integer) features.get("End"));
			}
			matchOffsetSets.add(offsetSet);
		}
		return matchOffsetSets;
	}
	
	// TODO Add main function for running outside the GATE GUI.
}
