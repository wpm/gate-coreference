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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import gate.Corpus;
import gate.Document;
import gate.Resource;
import gate.coreference.scorer.PrecisionRecall;
import gate.coreference.scorer.EquivalenceClassScorerFactory.Method;
import gate.creole.AbstractVisualResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.GuiType;
import gate.event.CorpusEvent;
import gate.event.CorpusListener;
import gate.event.FeatureMapListener;
import gate.swing.XJTable;

/**
 * GATE visual resource for corpus coreference scores.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
@SuppressWarnings("serial")
@CreoleResource(name = "Corpus Coreference Score", guiType = GuiType.LARGE,
		resourceDisplayed = "gate.Corpus", mainViewer = false)
public class CoreferenceScoringViewer extends AbstractVisualResource implements
		CorpusListener {

	/**
	 * An {@link XJTable} whose cells are not editable.
	 */
	private class ImmutableXJTable extends XJTable {
		/**
		 * @param model
		 *            data model for this table
		 */
		public ImmutableXJTable(TableModel model) {
			super(model);
			setEnableHidingColumns(true);
			setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;
		}
	}

	/**
	 * Object that listens for changes to an individual document's feature map
	 * and relays those changes on to the viewer.
	 * <p>
	 * I have to store information about the document here because the
	 * featureMapUpdated function does not tell you which feature map has been
	 * updated, and I don't want to have to recalculate coreference scores for
	 * the entire corpus when a single document is changed.
	 */
	private class DocumentFeatureMapListener implements FeatureMapListener {

		final private CoreferenceScoringViewer viewer;
		final private Document document;

		public DocumentFeatureMapListener(CoreferenceScoringViewer viewer,
				Document document) {
			this.viewer = viewer;
			this.document = document;
			document.getFeatures().addFeatureMapListener(this);
		}

		@Override
		public void featureMapUpdated() {
			viewer.documentFeatureMapUpdated(document);
		}

		public void removeListener() {
			document.getFeatures().removeFeatureMapListener(this);
		}

	}

	static Logger logger = Logger.getLogger(CoreferenceScoringViewer.class
			.getName());

	/**
	 * Scoring methods to use.
	 */
	final private Set<Method> methods = new HashSet<Method>();

	/**
	 * Corpus whose coreference scores are displayed.
	 */
	private Corpus corpus;

	/**
	 * Scorer of the corpus.
	 */
	private CorpusScorer scorer;

	/**
	 * Table in which the document scores are displayed.
	 */
	private XJTable documentTable;

	/**
	 * Table in which the average scores are displayed.
	 */
	private ImmutableXJTable averagesTable;

	/**
	 * Data model for the table in which the document scores are displayed.
	 */
	private DefaultTableModel documentTableModel;

	/**
	 * Data model for the table in which the score averages are displayed.
	 */
	private DefaultTableModel averagesTableModel;

	/**
	 * Set of objects that listen for changes to the feature maps of the
	 * documents in the corpus.
	 */
	private Set<DocumentFeatureMapListener> documentListeners = new HashSet<DocumentFeatureMapListener>();

	/**
	 * Specify the scoring methods used by this viewer.
	 */
	public CoreferenceScoringViewer() {
		methods.add(Method.MUC);
		methods.add(Method.BCUBED);
	}

	@Override
	public Resource init() throws ResourceInstantiationException {
		logger.debug("Initialize coreference viewer");
		initModel();
		initViewer();
		return super.init();
	}

	/**
	 * Create the scoring table model.
	 * 
	 * @return a new scoring table model with no data in it
	 */
	private void initModel() {
		// Document scores table model.
		documentTableModel = new DefaultTableModel();
		documentTableModel.addColumn("Document");
		documentTableModel.addColumn("B-Cubed Precision");
		documentTableModel.addColumn("B-Cubed Recall");
		documentTableModel.addColumn("B-Cubed F-score");
		documentTableModel.addColumn("MUC Precision");
		documentTableModel.addColumn("MUC Recall");
		documentTableModel.addColumn("MUC F-score");
		// Averages table model.
		averagesTableModel = new DefaultTableModel();
		averagesTableModel.addColumn("Average");
		averagesTableModel.addColumn("B-Cubed Precision");
		averagesTableModel.addColumn("B-Cubed Recall");
		averagesTableModel.addColumn("B-Cubed F-score");
		averagesTableModel.addColumn("MUC Precision");
		averagesTableModel.addColumn("MUC Recall");
		averagesTableModel.addColumn("MUC F-score");
	}

	/**
	 * Initialize the user interface components.
	 */
	private void initViewer() {
		setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();

		documentTable = new ImmutableXJTable(documentTableModel);
		tabbedPane.addTab("Scores", null, new JScrollPane(documentTable),
				"Individual document coreference scores");

		averagesTable = new ImmutableXJTable(averagesTableModel);
		tabbedPane.addTab("Averages", null, new JScrollPane(averagesTable),
				"Micro and macro averages");

		add(tabbedPane);
	}

	/**
	 * Register the viewer as a listener for the corpus and the feature maps of
	 * all the documents it contains, create a scorer object for the new corpus
	 * and use it to update the table model.
	 * 
	 * @see gate.creole.AbstractVisualResource#setTarget(java.lang.Object)
	 */
	@Override
	public void setTarget(Object target) {
		if (null != corpus && corpus != target) {
			// Deregister listeners from the previous corpus.
			corpus.removeCorpusListener(this);
			for (DocumentFeatureMapListener listener : documentListeners)
				listener.removeListener();
		}
		corpus = (Corpus) target;
		logger.debug("Set target " + corpus.getName());
		// Register listeners for the corpus and the documents it contains.
		corpus.addCorpusListener(this);
		for (Object object : corpus) {
			Document document = (Document) object;
			documentListeners
					.add(new DocumentFeatureMapListener(this, document));
		}
		// Create a new corpus scorer and use it to update the table model.
		scorer = new CorpusScorer(corpus, methods);
		updateTables();
	}

	@Override
	public void documentAdded(CorpusEvent e) {
		Document document = e.getDocument();
		logger.debug("Document added: " + document.getName());
		scorer.addDocument(document);
		updateTables();
	}

	@Override
	public void documentRemoved(CorpusEvent e) {
		Document document = e.getDocument();
		logger.debug("Document removed: " + document.getName());
		scorer.removeDocument(document);
		updateTables();
	}

	/**
	 * Called when a document's feature map has changed.
	 * 
	 * @param document
	 *            document whose feature map has changed
	 */
	public void documentFeatureMapUpdated(Document document) {
		logger.debug("Document feature map updated: " + document.getName());
		scorer.resetDocumentScores(document);
		updateTables();
	}

	/**
	 * Called whenever the corpus changes in a way that could affect the
	 * coreference scores. It recalculates the coreference scores and updates
	 * the table models.
	 */
	private void updateTables() {
		initModel();
		documentTable.setModel(documentTableModel);
		averagesTable.setModel(averagesTableModel);

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Map<Document, Map<Method, PrecisionRecall>> corpusScores = scorer
				.getScores();
		for (Entry<Document, Map<Method, PrecisionRecall>> documentScores : corpusScores
				.entrySet()) {
			Document document = documentScores.getKey();
			Map<Method, PrecisionRecall> scores = documentScores.getValue();

			Vector<Object> rowData = new Vector<Object>();

			rowData.add(document.getName());

			PrecisionRecall bcubed = scores.get(Method.BCUBED);
			if (null != bcubed) {
				rowData.add(bcubed.getPrecision());
				rowData.add(bcubed.getRecall());
				rowData.add(bcubed.getFScore());
			} else {
				rowData.add("NA");
				rowData.add("NA");
				rowData.add("NA");
			}

			PrecisionRecall muc = scores.get(Method.MUC);
			if (null != muc) {
				rowData.add(muc.getPrecision());
				rowData.add(muc.getRecall());
				rowData.add(muc.getFScore());
			} else {
				rowData.add("NA");
				rowData.add("NA");
				rowData.add("NA");
			}

			documentTableModel.addRow(rowData);
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
