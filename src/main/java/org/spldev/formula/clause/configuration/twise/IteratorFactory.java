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
package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Instantiates an implementation of {@link ICombinationIterator}.
 *
 * @author Sebastian Krieter
 */
public class IteratorFactory {

	public enum IteratorID {
		InverseDefault, Default, Lexicographic, InverseLexicographic, RandomPartition, Partition
	}

	public static ICombinationIterator getIterator(IteratorID id, List<PresenceCondition> expressions, int t) {
		switch (id) {
		case Default:
			return new InverseDefaultIterator(t, expressions);
		case InverseDefault:
			return new DefaultIterator(t, expressions);
		case InverseLexicographic:
			return new InverseLexicographicIterator(t, expressions);
		case Lexicographic:
			return new LexicographicIterator(t, expressions);
		case Partition:
			return new PartitionIterator(t, expressions);
		case RandomPartition:
			return new RandomPartitionIterator(t, expressions);
		default:
			return null;
		}
	}
}
