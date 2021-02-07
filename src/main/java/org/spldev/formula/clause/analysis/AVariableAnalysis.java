package org.spldev.formula.clause.analysis;

import org.spldev.formula.clause.*;

/**
 * Base class for an analysis that works on a list of variables.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public abstract class AVariableAnalysis<T> extends AbstractAnalysis<T> {

	protected LiteralList variables;

	public LiteralList getVariables() {
		return variables;
	}

	public void setVariables(LiteralList variables) {
		this.variables = variables;
	}

}
