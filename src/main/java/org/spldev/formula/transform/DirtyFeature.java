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
package org.spldev.formula.transform;

/**
 * Representation of a feature that will be removed from a feature model by the
 * {@link CNFSlicer}.
 *
 * @author Sebastian Krieter
 */
public class DirtyFeature implements Comparable<DirtyFeature> {

	private final int id;

	private long positiveCount;
	private long negativeCount;
	private long mixedCount;

	public DirtyFeature(int id) {
		this.id = id;

		positiveCount = 0;
		negativeCount = 0;
		mixedCount = 0;
	}

	public int getId() {
		return id;
	}

	@Override
	public int compareTo(DirtyFeature arg0) {
		return (int) Math.signum(arg0.getClauseCount() - getClauseCount());
	}

	public long getClauseCount() {
		try {
			return ((positiveCount * negativeCount) - (positiveCount + negativeCount));
		} catch (final ArithmeticException e) {
			return Long.MAX_VALUE;
		}
	}

	public boolean exp1() {
		return (positiveCount < 2) || (negativeCount < 2);
	}

	public boolean exp0() {
		return (positiveCount == 0) || (negativeCount == 0);
	}

	public long getMixedCount() {
		return mixedCount;
	}

	public long getPositiveCount() {
		return positiveCount;
	}

	public long getNegativeCount() {
		return negativeCount;
	}

	public void incPositive() {
		positiveCount++;
	}

	public void incNegative() {
		negativeCount++;
	}

	public void incMixed() {
		mixedCount++;
	}

	public void decPositive() {
		positiveCount--;
	}

	public void decNegative() {
		negativeCount--;
	}

	public void decMixed() {
		mixedCount--;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		return id == ((DirtyFeature) obj).id;
	}

}
