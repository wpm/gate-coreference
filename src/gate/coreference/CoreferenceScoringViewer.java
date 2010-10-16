package gate.coreference;

import org.apache.log4j.Logger;

import gate.Corpus;
import gate.Resource;
import gate.creole.AbstractVisualResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.GuiType;
import gate.event.CorpusEvent;
import gate.event.CorpusListener;

/**
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
@SuppressWarnings("serial")
@CreoleResource(name = "Corpus Coreference Score", guiType = GuiType.LARGE,
		resourceDisplayed = "gate.Corpus", mainViewer = false)
public class CoreferenceScoringViewer extends AbstractVisualResource implements
		CorpusListener {

	static Logger logger = Logger.getLogger(CoreferenceScoringViewer.class
			.getName());

	private Corpus corpus;

	public Resource init() throws ResourceInstantiationException {
		logger.info("Initialize coreference viewer");
		return super.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gate.creole.AbstractVisualResource#setTarget(java.lang.Object)
	 */
	@Override
	public void setTarget(Object target) {
		// TODO Auto-generated method stub
		logger.info("Set target " + target.toString());
		if (null != corpus)
			corpus.removeCorpusListener(this);
		corpus = (Corpus) target;
		corpus.addCorpusListener(this);
		corpusUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gate.event.CorpusListener#documentAdded(gate.event.CorpusEvent)
	 */
	@Override
	public void documentAdded(CorpusEvent e) {
		// TODO Auto-generated method stub
		logger.info("Document added " + e.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gate.event.CorpusListener#documentRemoved(gate.event.CorpusEvent)
	 */
	@Override
	public void documentRemoved(CorpusEvent e) {
		// TODO Auto-generated method stub
		logger.info("Document removed " + e.toString());
	}

	/**
	 * 
	 */
	private void corpusUpdated() {
		// TODO Auto-generated method stub
	}

}
