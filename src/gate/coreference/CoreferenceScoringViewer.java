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
 * GATE visual resource for corpus coreference scores.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
@SuppressWarnings("serial")
@CreoleResource(name = "Corpus Coreference Score", guiType = GuiType.LARGE,
		resourceDisplayed = "gate.Corpus", mainViewer = false)
public class CoreferenceScoringViewer extends AbstractVisualResource implements
		CorpusListener {

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
	 * Data model for the table in which the document scores are displayed.
	 */
	private DefaultTableModel documentTableModel;

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
		documentTableModel = initModel();
		initViewer();
		return super.init();
	}

	private DefaultTableModel initModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Document");
		model.addColumn("B-Cubed Precision");
		model.addColumn("B-Cubed Recall");
		model.addColumn("B-Cubed F-score");
		model.addColumn("MUC Precision");
		model.addColumn("MUC Recall");
		model.addColumn("MUC F-score");
		return model;
	}

	private void initViewer() {
		setLayout(new BorderLayout());
		documentTable = new XJTable() {
			/**
			 * This document scores table is not editable.
			 * 
			 * @see javax.swing.JTable#isCellEditable(int, int)
			 */
			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		documentTable.setModel(documentTableModel);
		documentTable.setEnableHidingColumns(true);
		documentTable.setAutoResizeMode(XJTable.AUTO_RESIZE_ALL_COLUMNS);
		add(new JScrollPane(documentTable));
	}

	@Override
	public void setTarget(Object target) {
		if (null != corpus && corpus != target)
			corpus.removeCorpusListener(this);
		corpus = (Corpus) target;
		logger.debug("Set target " + corpus.getName());
		scorer = new CorpusScorer(corpus, methods);
		corpus.addCorpusListener(this);
		updateDocumentTable();
	}

	@Override
	public void documentAdded(CorpusEvent e) {
		Document document = e.getDocument();
		logger.debug("Document added: " + document.getName());
		scorer.addDocument(document);
		updateDocumentTable();
	}

	@Override
	public void documentRemoved(CorpusEvent e) {
		Document document = e.getDocument();
		logger.debug("Document removed: " + document.getName());
		scorer.removeDocument(document);
		updateDocumentTable();
	}

	/**
	 * This function is called whenever the corpus changes. It recalculates the
	 * coreference scores and updates the table model.
	 */
	private void updateDocumentTable() {
		documentTableModel = initModel();
		documentTable.setModel(documentTableModel);

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
