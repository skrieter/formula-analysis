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
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.util.data.*;

/**
 * Assumptions for a {@link Sat4JSolver}.
 *
 * @author Sebastian Krieter
 */
public class Sat4JAssumptions implements Assignment {

	protected final VecInt assumptions;
	protected final VariableMap variables;

	public VecInt getAssumptions() {
		return assumptions;
	}

	public Sat4JAssumptions(VariableMap variables) {
		this.variables = variables;
		assumptions = new VecInt(variables.size());
	}

	public void clear() {
		assumptions.clear();
	}

	public void clear(int newSize) {
		assumptions.shrinkTo(newSize);
	}

	public void ensureSize(int size) {
		assumptions.ensure(size);
	}

	public Integer pop() {
		final int topElement = assumptions.get(assumptions.size());
		assumptions.pop();
		return topElement;
	}

	public void pop(int count) {
		assumptions.shrinkTo(assumptions.size() - count);
	}

	public void push(int var) {
		assumptions.push(var);
	}

	public void pushAll(int[] vars) {
		assumptions.pushAll(new VecInt(vars));
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

	public int peek() {
		return assumptions.get(assumptions.size() - 1);
	}

	public int peek(int i) {
		return assumptions.get(i);
	}

	@Override
	public void set(int index, Object assignment) {
		if (assignment instanceof Boolean) {
			for (int i = 0; i < assumptions.size(); i++) {
				final int l = assumptions.unsafeGet(i);
				if (Math.abs(l) == index) {
					assumptions.set(i, (Boolean) assignment ? l : -l);
					return;
				}
			}
			assumptions.push((Boolean) assignment ? index : -index);
		}
	}

	public void set(String name, Object assignment) {
		final int index = variables.getIndex(name).orElse(-1);
		if (index > 0) {
			set(index, assignment);
		}
	}

	@Override
	public Optional<Object> get(int index) {
		for (int i = 0; i < assumptions.size(); i++) {
			final int l = assumptions.unsafeGet(i);
			if (Math.abs(l) == index) {
				return Optional.of(l);
			}
		}
		return Optional.empty();
	}

	public Optional<Object> get(String name) {
		final int index = variables.getIndex(name).orElse(-1);
		return index > 0 ? get(index) : Optional.empty();
	}

	public VariableMap getVariables() {
		return variables;
	}

	@Override
	public List<Pair<Integer, Object>> getAll() {
		final List<Pair<Integer, Object>> map = new ArrayList<>();
		for (int i = 0; i < assumptions.size(); i++) {
			final int l = assumptions.unsafeGet(i);
			if (l != 0) {
				map.add(new Pair<>(Math.abs(l), l > 0));
			}
		}
		return map;
	}

}
