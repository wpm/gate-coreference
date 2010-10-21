/**
 * Copyright 2010 W.P. McNeill
 */
package gate.coreference;

import java.util.Set;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class EquivalenceClassScorerFactory<T> {

	public static enum Method {
		BCUBED, MUC
	};

	public EquivalenceClassScorer<T> getScorer(Method method, Set<Set<T>> key) {
		EquivalenceClassScorer<T> scorer;

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
