/**
 * Copyright 2010 W.P. McNeill
 */
package gate.coreference;

import java.util.Set;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class CoreferenceScorerFactory<T> {

	public static enum Method {
		BCUBED
	};

	public CoreferenceScorer<T> getScorer(Method method, Set<Set<T>> key) {
		CoreferenceScorer<T> scorer;

		switch (method) {
		case BCUBED:
		default:
			scorer = new BCubed<T>(key);
			break;
		}
		return scorer;
	}
}
