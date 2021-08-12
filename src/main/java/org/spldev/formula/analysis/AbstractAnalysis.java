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

import java.util.function.*;

import org.spldev.formula.*;
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

	protected abstract Identifier<T> getIdentifier();

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

	protected S solver;

	public void setSolver(S solver) {
		this.solver = solver;
	}

	protected Object getParameters() {
		return defaultParameters;
	}

	@Override
	public Result<T> getResult(ModelRepresentation kc) {
		return kc.getCache().get(
			new AnalysisResultProvider(
				m -> Executor.run(this::execute, kc, m)));
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
		prepareSolver(solver);
		monitor.checkCancel();
		try {
			return analyze(solver, monitor);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			resetSolver(solver);
		}
	}

	protected abstract T analyze(S solver, InternalMonitor monitor) throws Exception;

	protected abstract S createSolver(ModelRepresentation c) throws RuntimeContradictionException;

	protected abstract void prepareSolver(S solver);

	protected abstract void resetSolver(S solver);

}
