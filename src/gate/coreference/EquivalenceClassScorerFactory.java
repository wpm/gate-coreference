/**
 * Copyright 2010 W.P. McNeill
 */
package gate.coreference;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class EquivalenceClassScorerFactory<T> {

	public static enum Method {
		BCUBED, MUC
	};

	public EquivalenceClassScorer<T> getScorer(Method method) {
		EquivalenceClassScorer<T> scorer;

		switch (method) {
		case MUC:
			scorer = new MUC<T>();
			break;
		case BCUBED:
		default:
			scorer = new BCubed<T>();
			break;
		}
		return scorer;
	}
}
