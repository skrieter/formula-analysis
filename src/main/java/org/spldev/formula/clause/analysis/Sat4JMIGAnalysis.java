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
import org.spldev.formula.clause.solver.*;

/**
 * Base class for an analysis using a {@link SatSolver sat solver}.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public abstract class Sat4JMIGAnalysis<T> extends AbstractAnalysis<T, Sat4JMIGSolver> {

	protected LiteralList assumptions = null;

	protected boolean timeoutOccured = false;
	private boolean throwTimeoutException = true;
	private int timeout = 1000;

	protected Random random = new Random(112358);

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	@Override
	protected Sat4JMIGSolver createSolver(ModelRepresentation c) throws RuntimeContradictionException {
		return new Sat4JMIGSolver(c);
	}

	@Override
	protected void prepareSolver(Sat4JMIGSolver solver) {
		Objects.nonNull(solver);
		Objects.nonNull(solver.mig);
		Objects.nonNull(solver.sat4j);
		solver.sat4j.setTimeout(timeout);
		if (assumptions != null) {
			solver.sat4j.assignmentPushAll(assumptions.getLiterals());
		}
		assumptions = new LiteralList(solver.sat4j.getAssignmentArray());
		timeoutOccured = false;
	}

	@Override
	protected void resetSolver(Sat4JMIGSolver solver) {
		solver.sat4j.assignmentClear(0);
	}

	protected final void reportTimeout() throws RuntimeTimeoutException {
		timeoutOccured = true;
		if (throwTimeoutException) {
			throw new RuntimeTimeoutException();
		}
	}

	public final LiteralList getAssumptions() {
		return assumptions;
	}

	public final void setAssumptions(LiteralList assumptions) {
		this.assumptions = assumptions;
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
