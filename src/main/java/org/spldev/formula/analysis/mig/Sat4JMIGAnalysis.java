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
package org.spldev.formula.analysis.mig;

import java.util.*;

import org.spldev.formula.analysis.*;
import org.spldev.formula.solver.*;
import org.spldev.formula.solver.mig.*;
import org.spldev.util.job.*;

/**
 * Base class for analyses using a {@link Sat4JMIGSolver}.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public abstract class Sat4JMIGAnalysis<T> extends AbstractAnalysis<T, Sat4JMIGSolver, MIG> {

	protected boolean timeoutOccured = false;
	private boolean throwTimeoutException = true;
	private int timeout = 1000;

	protected Random random = new Random(112358);

	public Sat4JMIGAnalysis() {
		super();
		solverInputProvider = MIGProvider.fromFormula();
	}

	@Override
	protected Object getParameters() {
		return assumptions != null ? assumptions : super.getParameters();
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public final T execute(InternalMonitor monitor) {
		return solver != null ? execute(solver, monitor) : null;
	}

	@Override
	protected Sat4JMIGSolver createSolver(MIG input) throws RuntimeContradictionException {
		return new Sat4JMIGSolver(input);
	}

	@Override
	protected void prepareSolver(Sat4JMIGSolver solver) {
		super.prepareSolver(solver);
		solver.setTimeout(timeout);
		timeoutOccured = false;
	}

	protected final void reportTimeout() throws RuntimeTimeoutException {
		timeoutOccured = true;
		if (throwTimeoutException) {
			throw new RuntimeTimeoutException();
		}
	}

	public final boolean isThrowTimeoutException() {
		return throwTimeoutException;
	}

	public final void setThrowTimeoutException(boolean throwTimeoutException) {
		this.throwTimeoutException = throwTimeoutException;
	}

	public final boolean isTimeoutOccured() {
		return timeoutOccured;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
