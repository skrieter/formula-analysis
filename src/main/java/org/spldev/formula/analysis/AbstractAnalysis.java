/* -----------------------------------------------------------------------------
 * Formula-Analysis Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis Lib.
 * 
 * Formula-Analysis Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula-analysis> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.analysis;

import java.util.*;
import java.util.function.*;

import org.spldev.formula.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.solver.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Base class for an analysis using any {@link Solver solver}.
 *
 * @param <T> Type of the analysis result.
 * @param <S> Type of the solver for this analysis.
 *
 * @author Sebastian Krieter
 */
public abstract class AbstractAnalysis<T, S extends Solver> implements Analysis<T> {

	protected static Object defaultParameters = new Object();

	public class AnalysisResultProvider implements Provider<T> {
		private final Function<InternalMonitor, Result<T>> function;

		public AnalysisResultProvider(Function<InternalMonitor, Result<T>> function) {
			this.function = function;
		}

		@Override
		public Identifier<T> getIdentifier() {
			return AbstractAnalysis.this.getIdentifier();
		}

		@Override
		public Object getParameters() {
			return AbstractAnalysis.this.getParameters();
		}

		@Override
		public Result<T> apply(CacheHolder c, InternalMonitor m) {
			return function.apply(m);
		}
	}

	protected final Assignment assumptions = new IndexAssignment();
	protected final List<Formula> assumedConstraints = new ArrayList<>();
	protected S solver;

//
//	public void setSolver(S solver) {
//		this.solver = solver;
//	}

	public Assignment getAssumptions() {
		return assumptions;
	}

	public List<Formula> getAssumedConstraints() {
		return assumedConstraints;
	}

	protected Object getParameters() {
		return defaultParameters;
	}

	@Override
	public Result<T> getResult(ModelRepresentation rep) {
		return rep.getCache().get(new AnalysisResultProvider(m -> Executor.run(this::execute, rep, m)));
	}

	@Override
	public final T execute(ModelRepresentation c, InternalMonitor monitor) {
		if (solver == null) {
			solver = createSolver(c);
		}
		return execute(solver, monitor);
	}

	public T execute(S solver, InternalMonitor monitor) {
		if (this.solver == null) {
			this.solver = solver;
		}
		monitor.checkCancel();
		prepareSolver(solver);
		try {
			return analyze(solver, monitor);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			resetSolver(solver);
		}
	}

	/*
	 * 1. Create analysis with MR Create analysis with MR and params
	 * 
	 * 2. Create Sub-analysis within other analysis Create Sub-analysis within other
	 * analysis and params
	 * 
	 */

	protected abstract Identifier<T> getIdentifier();

	protected abstract S createSolver(ModelRepresentation c) throws RuntimeContradictionException;

	protected void prepareSolver(S solver) {
		solver.getAssumptions().setAll(assumptions.getAll());
		solver.getDynamicFormula().push(assumedConstraints);
	}

	protected abstract T analyze(S solver, InternalMonitor monitor) throws Exception;

	protected void resetSolver(S solver) {
		solver.getAssumptions().resetAll(assumptions.getAll());
		solver.getDynamicFormula().pop(assumedConstraints.size());
	}

}
