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
package org.spldev.formula.clause.solver.strategy;

import org.sat4j.specs.ISolver;

/**
 * Modified variable order for {@link ISolver}.<br>
 * Uses the {@link UniformRandomSelectionStrategy}.
 *
 * @author Sebastian Krieter
 */
public class FixedOrderHeap2 extends FixedOrderHeap {

	private static final long serialVersionUID = 1L;

	private final UniformRandomSelectionStrategy selectionStrategy;

	public FixedOrderHeap2(UniformRandomSelectionStrategy strategy, int[] order) {
		super(strategy, order);
		selectionStrategy = strategy;
	}

	@Override
	public void undo(int x) {
		super.undo(x);
		selectionStrategy.undo(x);
	}

}
