/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package org.spldev.formula.clause.io;

import java.io.*;
import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.util.*;
import org.spldev.util.io.format.*;
import org.spldev.util.logging.*;

/**
 * Reads and writes grouped propositional expressions in CNF.
 *
 * @author Sebastian Krieter
 */
public class ExpressionGroupFormat implements Format<List<List<ClauseList>>> {

	public static final String ID = ExpressionGroupFormat.class.getSimpleName();

	@Override
	public String serialize(List<List<ClauseList>> expressionGroups) {
		final StringBuilder sb = new StringBuilder();
		for (final List<? extends ClauseList> expressionGroup : expressionGroups) {
			sb.append("g ");
			sb.append(expressionGroup.size());
			sb.append(System.lineSeparator());
			for (final ClauseList expression : expressionGroup) {
				sb.append("e ");
				for (final LiteralList literalSet : expression) {
					for (final int literal : literalSet.getLiterals()) {
						sb.append(literal);
						sb.append(" ");
					}
					sb.append("|");
				}
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	@Override
	public Result<List<List<ClauseList>>> parse(CharSequence source) {
		final ArrayList<List<ClauseList>> expressionGroups = new ArrayList<>();
		ArrayList<ClauseList> expressionGroup = null;
		try (final BufferedReader reader = new BufferedReader(new StringReader(source.toString()))) {
			final LineIterator lineIterator = new LineIterator(reader);
			try {
				for (String line = lineIterator.get(); line != null; line = lineIterator.get()) {
					final char firstChar = line.charAt(0);
					switch (firstChar) {
					case 'g':
						final int groupSize = Integer.parseInt(line.substring(2).trim());
						expressionGroup = new ArrayList<>(groupSize);
						expressionGroups.add(expressionGroup);
						break;
					case 'e':
						if (expressionGroup == null) {
							throw new Exception("No group defined.");
						}
						final String expressionString = line.substring(2).trim();
						final String[] clauseStrings = expressionString.split("\\|");
						final ClauseList expression = new ClauseList();
						for (final String clauseString : clauseStrings) {
							final String[] literalStrings = clauseString.split("\\s+");
							final int[] literals = new int[literalStrings.length];
							int index = 0;
							for (final String literalString : literalStrings) {
								if (!literalString.isEmpty()) {
									final int literal = Integer.parseInt(literalString);
									literals[index++] = literal;
								}
							}
							expression.add(new LiteralList(Arrays.copyOfRange(literals, 0, index)));
						}
						expressionGroup.add(expression);
						break;
					default:
						break;
					}
				}
			} catch (final Exception e) {
				return Result.empty(new ParseProblem(e, lineIterator.getLineCount()));
			}

		} catch (final IOException e) {
			Logger.logError(e);
		}
		return Result.of(expressionGroups);
	}

	@Override
	public String getFileExtension() {
		return "expression";
	}

	@Override
	public ExpressionGroupFormat getInstance() {
		return this;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public String getName() {
		return "Expression Groups";
	}

}
