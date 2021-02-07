package org.spldev.formula.clause.analysis;

import java.util.*;

import org.spldev.formula.clause.*;

/**
 * Wrapper class for an analysis result containing additional information about
 * the performed analysis.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public class AnalysisResult<T> {

	private final String id;
	private final LiteralList assumptions;
	private final int hashCode;
	private final T result;

	public AnalysisResult(String id, LiteralList assumptions, T result) {
		this.id = id;
		this.assumptions = assumptions;
		this.result = result;
		this.hashCode = (31 * id.hashCode()) + Arrays.hashCode(assumptions.getLiterals());
	}

	public String getId() {
		return id;
	}

	public LiteralList getAssumptions() {
		return assumptions;
	}

	public T getResult() {
		return result;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final AnalysisResult<?> other = (AnalysisResult<?>) obj;
		return id.equals(other.id) && Arrays.equals(assumptions.getLiterals(), other.assumptions.getLiterals());
	}

}
