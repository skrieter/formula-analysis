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
package org.spldev.formula.solver.sat4j;

import java.util.*;

import org.sat4j.core.*;
import org.spldev.formula.solver.*;

/**
 * Assumptions for a {@link Sat4JSolver}.
 *
 * @author Sebastian Krieter
 */
public class Sat4JAssumptions implements Assumptions<Integer> {

	protected final VecInt assumptions;

	public VecInt getAssumptions() {
		return assumptions;
	}

	public Sat4JAssumptions(int size) {
		assumptions = new VecInt(size);
	}

	protected Sat4JAssumptions(Sat4JAssumptions oldAssumptions) {
		assumptions = new VecInt(0);
		oldAssumptions.assumptions.copyTo(assumptions);
	}

	@Override
	public void clear() {
		assumptions.clear();
	}

	@Override
	public void clear(int newSize) {
		assumptions.shrinkTo(newSize);
	}

	public void ensureSize(int size) {
		assumptions.ensure(size);
	}

	@Override
	public Integer pop() {
		final int topElement = assumptions.get(assumptions.size());
		assumptions.pop();
		return topElement;
	}

	@Override
	public void pop(int count) {
		assumptions.shrinkTo(assumptions.size() - count);
	}

	@Override
	public void push(Integer var) {
		assumptions.push(var);
	}

	public void push(int var) {
		assumptions.push(var);
	}

	public void pushAll(int[] vars) {
		assumptions.pushAll(new VecInt(vars));
	}

	@Override
	public void replaceLast(Integer var) {
		assumptions.pop().unsafePush(var);
	}

	public void replaceLast(int var) {
		assumptions.pop().unsafePush(var);
	}

	public void remove(int i) {
		assumptions.delete(i);
	}

	public void set(int index, int var) {
		assumptions.set(index, var);
	}

	@Override
	public int size() {
		return assumptions.size();
	}

	public int[] asArray() {
		return Arrays.copyOf(assumptions.toArray(), assumptions.size());
	}

	public int[] asArray(int from) {
		return Arrays.copyOfRange(assumptions.toArray(), from, assumptions.size());
	}

	public int[] asArray(int from, int to) {
		return Arrays.copyOfRange(assumptions.toArray(), from, to);
	}

	public int get(int i) {
		return assumptions.get(i);
	}

	@Override
	public Integer peek() {
		return assumptions.get(assumptions.size() - 1);
	}

}
