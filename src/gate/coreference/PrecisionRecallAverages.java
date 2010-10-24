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

/**
 * Precision and recall scores for a list of equivalence set pairs along with
 * their micro and macro averages.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public interface PrecisionRecallAverages {

	/**
	 * @return scores for the individual equivalence set pairs
	 */
	public Iterable<PrecisionRecall> getScores();

	/**
	 * The micro average is the average of the precision and recall scores for
	 * the individual equivalence set pairs.
	 * 
	 * @return the micro average
	 */
	public PrecisionRecall getMicroAverage();

	/**
	 * The macro average is the precision and recall scores generated by scoring
	 * all equivalence set pairs at once.
	 * 
	 * @return the macro average
	 */
	public PrecisionRecall getMacroAverage();
}
