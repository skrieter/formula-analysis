/* -----------------------------------------------------------------------------
 * Formula-Analysis-Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis-Lib.
 * 
 * Formula-Analysis-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
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
