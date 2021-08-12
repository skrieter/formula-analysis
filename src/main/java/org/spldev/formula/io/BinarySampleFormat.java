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
package org.spldev.formula.io;

import java.io.*;
import java.util.*;

import org.spldev.formula.clauses.*;
import org.spldev.formula.clauses.LiteralList.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.util.*;
import org.spldev.util.io.binary.*;
import org.spldev.util.io.format.*;

/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class BinarySampleFormat extends BinaryFormat<SolutionList> {

	public static final String ID = BinarySampleFormat.class.getCanonicalName();

	@Override
	public void write(SolutionList configurationList, Output out) throws IOException {
		final OutputStream outputStream = out.getOutputStream();
		final List<String> names = configurationList.getVariables().getNames();
		writeInt(outputStream, names.size());
		for (final String name : names) {
			writeString(outputStream, name);
		}
		final BitSet bs = new BitSet(names.size());
		final List<LiteralList> solutions = configurationList.getSolutions();
		writeInt(outputStream, solutions.size());
		for (final LiteralList configuration : solutions) {
			final int[] literals = configuration.getLiterals();
			for (int i = 0; i < literals.length; i++) {
				bs.set(i, literals[i] > 0);
			}
			writeBytes(outputStream, bs.toByteArray());
			bs.clear();
		}
		outputStream.flush();
	}

	@Override
	public Result<SolutionList> parse(Input source) {
		final InputStream inputStream = source.getInputStream();
		try {
			final int numberOfVariables = readInt(inputStream);
			final List<String> variableNames = new ArrayList<>(numberOfVariables);
			for (int i = 0; i < numberOfVariables; i++) {
				variableNames.add(readString(inputStream));
			}
			final VariableMap variableMap = VariableMap.fromNames(variableNames);
			final int numberOfSolutions = readInt(inputStream);
			final List<LiteralList> solutionList = new ArrayList<>(numberOfSolutions);
			final BitSet bs = BitSet.valueOf(readBytes(inputStream, (numberOfVariables + 7) / 8));
			for (int i = 0; i < numberOfSolutions; i++) {
				final int[] literals = new int[numberOfVariables];
				for (int j = 0; j < numberOfVariables; j++) {
					literals[j] = bs.get(j) ? (j + 1) : -(j + 1);
				}
				solutionList.add(new LiteralList(literals, Order.INDEX, false));
			}
			return Result.of(new SolutionList(variableMap, solutionList));
		} catch (final IOException e) {
			return Result.empty(e);
		}
	}

	@Override
	public String getFileExtension() {
		return "sample";
	}

	@Override
	public BinarySampleFormat getInstance() {
		return this;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean supportsSerialize() {
		return false;
	}

	@Override
	public boolean supportsWrite() {
		return true;
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public String getName() {
		return "BinarySample";
	}

}
