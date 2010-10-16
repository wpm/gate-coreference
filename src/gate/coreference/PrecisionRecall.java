/**
 * Copyright 2010 W.P. McNeill
 */
package gate.coreference;

/**
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

	public double getFScore() {
		return getFScore(1);
	}
	
	public double getFScore(double beta) {
		double betaSquared = Math.pow(beta, 2);
		return (1 + betaSquared) * precision * recall
				/ (betaSquared * (precision + recall));
	}
}
