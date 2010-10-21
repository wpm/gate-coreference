package gate.coreference;

import java.awt.BorderLayout;
import java.text.Collator;
import java.util.Locale;

import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import gate.Corpus;
import gate.Resource;
import gate.creole.AbstractVisualResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.GuiType;
import gate.event.CorpusEvent;
import gate.event.CorpusListener;
import gate.swing.XJTable;

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

	private Collator collator;

	private DefaultTableModel documentTableModel;

	private XJTable documentTable;

	@Override
	public Resource init() throws ResourceInstantiationException {
		logger.info("Initialize coreference viewer");
		initModel();
		initViewer();
		return super.init();
	}

	private void initModel() {
		collator = Collator.getInstance(Locale.ENGLISH);
		collator.setStrength(Collator.TERTIARY);
		documentTableModel = new DefaultTableModel();
		documentTableModel.addColumn("Document");
		documentTableModel.addColumn("B-Cubed");
		documentTableModel.addColumn("MUC");
	}

	private void initViewer() {
		setLayout(new BorderLayout());
		documentTable = new XJTable() {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		documentTable.setModel(documentTableModel);
		documentTable.setSortable(false);
		documentTable.setEnableHidingColumns(true);
		documentTable.setAutoResizeMode(XJTable.AUTO_RESIZE_ALL_COLUMNS);
		add(new JScrollPane(documentTable));
	}

	@Override
	public void setTarget(Object target) {
		logger.info("Set target " + target.toString());
		if (null != corpus && corpus != target)
			corpus.removeCorpusListener(this);
		corpus = (Corpus) target;
		corpus.addCorpusListener(this);
		corpusUpdated();
	}

	@Override
	public void documentAdded(CorpusEvent e) {
		logger.info("Document added " + e.toString());
	}

	@Override
	public void documentRemoved(CorpusEvent e) {
		logger.info("Document removed " + e.toString());
	}

	/**
	 * 
	 */
	private void corpusUpdated() {
		// TODO Auto-generated method stub
	}

}
