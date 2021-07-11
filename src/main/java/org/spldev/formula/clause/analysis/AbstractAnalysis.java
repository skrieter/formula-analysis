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

import java.util.Random;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.SatSolver;
import org.spldev.util.Provider;
import org.spldev.util.Result;
import org.spldev.util.data.CacheHolder;
import org.spldev.util.job.Executor;
import org.spldev.util.job.InternalMonitor;

/**
 * Base class for an analysis using a {@link SatSolver sat solver}.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public abstract class AbstractAnalysis<T> extends SatAnalysis implements Analysis<T>, Provider<T> {

	protected Random random = new Random(112358);

	@Override
	public Result<T> apply(CacheHolder formula, InternalMonitor monitor) {
		return formula.get(CNFProvider.identifier).flatMap(cnf -> Executor.run(this, cnf, monitor));
	}

	@Override
	public final T execute(CNF cnf, InternalMonitor monitor) {
		return execute(createSolver(cnf), monitor);
	}

	@Override
	public final T execute(SatSolver solver, InternalMonitor monitor) {
		prepareSolver(solver);

		monitor.checkCancel();
		try {
			return analyze(solver, monitor);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			solver.assignmentClear(0);
		}
	}

	protected abstract T analyze(SatSolver solver, InternalMonitor monitor) throws Exception;

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
