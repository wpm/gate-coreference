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

import java.util.List;

import org.apache.log4j.Logger;

import gate.Corpus;
import gate.DataStore;
import gate.Factory;
import gate.FeatureMap;
import gate.util.GateException;

/**
 * Utility class for loading a corpus from a data store.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class Datastore {
	static Logger logger = Logger.getLogger(Datastore.class.getName());

	/**
	 * Load a corpus from a data store by name.
	 * 
	 * It is the caller's responsibility to call Factory.deleteResource() on the
	 * value returned by this function.
	 * 
	 * @param dataStore
	 *            data store
	 * @param corpusName
	 *            name of a corpus in the data store
	 * @return the specified corpus
	 * @throws GateException
	 */
	public static Corpus loadCorpusFromDatastore(DataStore dataStore,
			String corpusName) throws GateException {
		logger.info("Open corpus " + corpusName + " in " + dataStore.getName());
		// Extract lists of corpus names and LRIDs from the data store.
		// These lists have corresponding elements.
		FeatureMap params = Factory.newFeatureMap();
		@SuppressWarnings("unchecked")
		List<String> names = dataStore
				.getLrNames("gate.corpora.SerialCorpusImpl");
		@SuppressWarnings("unchecked")
		List<String> lrids = dataStore
				.getLrIds("gate.corpora.SerialCorpusImpl");
		// Find the LRID corresponding to the corpus name.
		String lrid;
		try {
			lrid = lrids.get(names.indexOf(corpusName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new GateException("Datastore does not contain corpus "
					+ corpusName);
		}
		params.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
		params.put(DataStore.LR_ID_FEATURE_NAME, lrid);
		return (Corpus) Factory.createResource("gate.corpora.SerialCorpusImpl",
				params);
	}
}
