package gate.coreference;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import gate.Corpus;
import gate.Document;
import gate.Resource;
import gate.coreference.EquivalenceClassScorerFactory.Method;
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

	private final Method[] scoreColumns = {
			EquivalenceClassScorerFactory.Method.BCUBED,
			EquivalenceClassScorerFactory.Method.MUC };

	static Logger logger = Logger.getLogger(CoreferenceScoringViewer.class
			.getName());

	private Corpus corpus;

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
		documentTableModel = new DefaultTableModel();
		documentTableModel.addColumn("Document");
		documentTableModel.addColumn("B-Cubed");
		documentTableModel.addColumn("MUC");
	}

	private void initViewer() {
		setLayout(new BorderLayout());
		documentTable = new XJTable() {
			@Override
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
		corpusUpdated();
	}

	@Override
	public void documentRemoved(CorpusEvent e) {
		logger.info("Document removed " + e.toString());
		corpusUpdated();
	}

	/**
	 * Update the scoring table model.
	 */
	private void corpusUpdated() {
		// Use both scoring methods.
		Set<Method> methods = new HashSet<Method>();
		methods.add(EquivalenceClassScorerFactory.Method.MUC);
		methods.add(EquivalenceClassScorerFactory.Method.BCUBED);

		CorpusScorer scorer = new CorpusScorer(corpus, methods);

		Map<Document, Map<Method, PrecisionRecall>> corpusScores = scorer
				.getScores();
		for (Entry<Document, Map<Method, PrecisionRecall>> documentScores : corpusScores
				.entrySet()) {
			Document document = documentScores.getKey();
			Map<Method, PrecisionRecall> scores = documentScores.getValue();

			Vector<Object> rowData = new Vector<Object>();
			rowData.add(document.getName());

			for (int i = 0; i < scoreColumns.length; i++) {
				PrecisionRecall score = scores.get(scoreColumns[i]);
				if (null != score)
					rowData.add(score.toString());
				else
					rowData.add(null);
			}

			documentTableModel.addRow(rowData);
		}
	}

}
