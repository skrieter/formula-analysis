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
package org.spldev.formula.solver;

/**
 * Exception thrown when a {@link SatSolver solver} detects an obvious
 * contradiction when adding new clauses.<br>
 * Doesn't need to be caught explicitly.
 *
 * @author Sebastian Krieter
 */
public class RuntimeContradictionException extends RuntimeException {

	private static final long serialVersionUID = -4951752949650801254L;

	public RuntimeContradictionException() {
		super();
	}

	public RuntimeContradictionException(String message) {
		super(message);
	}

	public RuntimeContradictionException(Throwable cause) {
		super(cause);
	}

	public RuntimeContradictionException(String message, Throwable cause) {
		super(message, cause);
	}

}
