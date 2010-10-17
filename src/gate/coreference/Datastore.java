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
