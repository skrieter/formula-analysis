package org.spldev.formula.clause.analysis;

import java.util.*;

import org.spldev.formula.clause.*;

/**
 * Base class for an analysis that works on a list of clauses. Clauses can be
 * grouped together, for instance if they belong to the same constraint. Grouped
 * clauses should be handled as a unit by the implementing analysis.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public abstract class AClauseAnalysis<T> extends AbstractAnalysis<T> {

	protected List<LiteralList> clauseList;
	protected int[] clauseGroupSize;

	public List<LiteralList> getClauseList() {
		return clauseList;
	}

	public void setClauseList(List<LiteralList> clauseList) {
		this.clauseList = clauseList;
	}

	public int[] getClauseGroups() {
		return clauseGroupSize;
	}

	public void setClauseGroupSize(int[] clauseGroups) {
		this.clauseGroupSize = clauseGroups;
	}

}
