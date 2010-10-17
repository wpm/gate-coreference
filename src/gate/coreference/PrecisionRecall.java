/**
 * Copyright 2010 W.P. McNeill
 */
package gate.coreference;

import java.util.Formatter;

/**
 * Precision and recall scores
 * <p>
 * This object contains precision and recall scores and calculates F-score.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class PrecisionRecall {
	private double precision;
	private double recall;

	/**
	 * @param precision
	 * @param recall
	 */
	public PrecisionRecall(double precision, double recall) {
		super();
		this.precision = precision;
		this.recall = recall;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		Formatter formatter = new Formatter(s);
		formatter.format("Precision = %0.4f, Recall = %0.4f, F-score = %0.4f",
				precision, recall, getFScore());
		return s.toString();
	}

	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * @param precision
	 *            the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}

	/**
	 * @param recall
	 *            the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}

	/**
	 * Default F-score
	 * 
	 * @return harmonic mean of precision and recall
	 */
	public double getFScore() {
		return getFScore(1);
	}

	/**
	 * Weighted default F-score
	 * 
	 * @param beta
	 *            weighting factor
	 * @return weighted harmonic mean of precision and recall
	 */
	public double getFScore(double beta) {
		double betaSquared = Math.pow(beta, 2);
		return (1 + betaSquared) * precision * recall
				/ (betaSquared * (precision + recall));
	}
}
