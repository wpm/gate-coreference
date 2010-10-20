/**
 * Copyright 2010 W.P. McNeill
 */
package gate.coreference;

import java.util.Set;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class EquivalenceSetScorerFactory<T> {

	public static enum Method {
		BCUBED, MUC
	};

	public EquivalenceSetScorer<T> getScorer(Method method, Set<Set<T>> key) {
		EquivalenceSetScorer<T> scorer;

		switch (method) {
		case MUC:
			scorer = new MUC<T>(key);
			break;
		case BCUBED:
		default:
			scorer = new BCubed<T>(key);
			break;
		}
		return scorer;
	}
}
