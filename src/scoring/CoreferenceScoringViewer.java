package scoring;

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

	public Resource init() throws ResourceInstantiationException {
		return super.init();
	}
	
	/* (non-Javadoc)
	 * @see gate.creole.AbstractVisualResource#setTarget(java.lang.Object)
	 */
	@Override
	public void setTarget(Object target) {
		// TODO Auto-generated method stub
		super.setTarget(target);
	}
	
	/* (non-Javadoc)
	 * @see gate.event.CorpusListener#documentAdded(gate.event.CorpusEvent)
	 */
	@Override
	public void documentAdded(CorpusEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gate.event.CorpusListener#documentRemoved(gate.event.CorpusEvent)
	 */
	@Override
	public void documentRemoved(CorpusEvent e) {
		// TODO Auto-generated method stub

	}

}
