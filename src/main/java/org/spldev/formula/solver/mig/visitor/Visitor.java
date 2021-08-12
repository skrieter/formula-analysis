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
package org.spldev.formula.solver.mig.visitor;

public interface Visitor<T> {

	public enum VisitResult {
		Cancel, Continue, Skip, Select
	}

	/**
	 * Called when the traverser first reaches the literal via a strong path and the
	 * corresponding variable is still undefined.
	 *
	 * @param literal the literal reached
	 * @return VisitResult
	 */
	VisitResult visitStrong(int literal);

	/**
	 * Called when the traverser first reaches the literal via a weak path and the
	 * corresponding variable is still undefined.
	 *
	 * @param literal the literal reached
	 * @return VisitResult
	 */
	VisitResult visitWeak(int literal);

	T getResult();

}
