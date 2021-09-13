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
package org.spldev.formula.analysis.sat4j.twise;

import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import org.spldev.formula.clauses.*;

/**
 * Manages and manipulates a list of {@link PresenceCondition presence
 * conditions}.
 *
 * @author Sebastian Krieter
 */
public class PresenceConditionManager {

	private final List<List<PresenceCondition>> dictionary = new ArrayList<>();
	private final List<List<PresenceCondition>> groupedPresenceConditions = new ArrayList<>();

	public PresenceConditionManager(TWiseConfigurationUtil util, List<List<ClauseList>> expressions) {
		final LiteralList coreDeadFeature = util.getDeadCoreFeatures();
		final int numberOfVariables = util.getCnf().getVariableMap().size();

		final HashMap<PresenceCondition, PresenceCondition> presenceConditionSet = new HashMap<>();

		dictionary.add(null);
		for (int i = 0; i < numberOfVariables; i++) {
			dictionary.add(new ArrayList<PresenceCondition>());
			dictionary.add(new ArrayList<PresenceCondition>());
		}

		int groupIndex = 0;
		for (final List<ClauseList> group : expressions) {
			final List<PresenceCondition> newNodeList = new ArrayList<>();
			expressionLoop: for (final ClauseList clauses : group) {
				final List<LiteralList> newClauses = new ArrayList<>();
				for (final LiteralList clause : clauses) {
					// If clause can be satisfied
					if ((clause.countConflicts(coreDeadFeature) == 0)) {
						// If clause is already satisfied
						if (coreDeadFeature.containsAll(clause)) {
							continue expressionLoop;
						} else {
							newClauses.add(clause.clone());
						}
					}
				}
				if (!newClauses.isEmpty()) {
					final PresenceCondition pc = new PresenceCondition(new ClauseList(newClauses));
					PresenceCondition mappedPc = presenceConditionSet.get(pc);
					if (mappedPc == null) {
						mappedPc = pc;
						presenceConditionSet.put(mappedPc, mappedPc);

						for (final LiteralList literalSet : mappedPc) {
							for (final int literal : literalSet.getLiterals()) {
								final int dictionaryIndex = literal < 0 ? numberOfVariables - literal : literal;
								dictionary.get(dictionaryIndex).add(mappedPc);
							}
						}
					}
					mappedPc.addGroup(groupIndex);
					Collections.sort(mappedPc, (o1, o2) -> o1.size() - o2.size());
					newNodeList.add(mappedPc);
				}
			}
			groupedPresenceConditions.add(newNodeList);
			groupIndex++;
		}
	}

	public void shuffle(Random random) {
		for (final List<PresenceCondition> pcs : groupedPresenceConditions) {
			Collections.shuffle(pcs, random);
		}
	}

	public void shuffleSort(Random random) {
		for (final List<PresenceCondition> list : groupedPresenceConditions) {
			final Map<Integer, List<PresenceCondition>> groupedPCs = list.stream().collect(Collectors.groupingBy(
				PresenceCondition::size));
			for (final List<PresenceCondition> pcList : groupedPCs.values()) {
				Collections.shuffle(pcList, random);
			}
			final List<Entry<Integer, List<PresenceCondition>>> shuffledPCs = new ArrayList<>(groupedPCs.entrySet());
			Collections.sort(shuffledPCs, (a, b) -> a.getKey() - b.getKey());
			list.clear();
			for (final Entry<Integer, List<PresenceCondition>> entry : shuffledPCs) {
				list.addAll(entry.getValue());
			}
		}
	}

	public void sort() {
		for (final List<PresenceCondition> list : groupedPresenceConditions) {
			Collections.sort(list, this::comparePresenceConditions);
		}
	}

	private int comparePresenceConditions(PresenceCondition o1, PresenceCondition o2) {
		final int clauseCountDiff = o1.size() - o2.size();
		if (clauseCountDiff != 0) {
			return clauseCountDiff;
		}
		int clauseLengthDiff = 0;
		for (int i = 0; i < o1.size(); i++) {
			clauseLengthDiff += o2.get(i).size() - o1.get(i).size();
		}
		return clauseLengthDiff;
	}

	public List<List<PresenceCondition>> getDictionary() {
		return dictionary;
	}

	public List<List<PresenceCondition>> getGroupedPresenceConditions() {
		return groupedPresenceConditions;
	}

}
