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
package org.spldev.formula.clause.configuration.sample;

/**
 * Computes the Cosine distance between two literal arrays.
 * Negative literals are treated as zero, positive literals as one.
 *
 * @author Sebastian Krieter
 */
public class CosineZeroDistance implements DistanceFunction {

	@Override
	public double computeDistance(final int[] literals1, final int[] literals2) {
		double sum = 0;
		double sumA = 0;
		double sumB = 0;
		for (int k = 0; k < literals1.length; k++) {
			final int a = ~literals1[k] >>> (Integer.SIZE-1);
			final int b = ~literals2[k] >>> (Integer.SIZE-1);
			sumA += a;
			sumB += b;
			sum += a & b;
		}
		final double cosineSimilarity = sum / Math.sqrt(sumA * sumB);
		return (1 - cosineSimilarity) / 2.0;
	}

	@Override
	public String getName() {
		return "CosineZero";
	}

}
