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

/**
 * Computes binomial coefficients and factorial.
 *
 * @author Sebastian Krieter
 */
public class BinomialCalculator {

	private final long[][] binomial;
	private final long[] factorial;

	public BinomialCalculator(int t, int n) {
		binomial = new long[n + 1][t + 1];
		factorial = new long[t + 1];
	}

	public long factorial(int k) {
		long f = factorial[k];
		if (f == 0) {
			f = 1;
			for (int i = 2; i <= k; i++) {
				f *= i;
			}
			factorial[k] = f;
		}
		return f;
	}

	public long binomial(int n, int k) {
		if (n < k) {
			return 0;
		}
		long b = binomial[n][k];
		if (b == 0) {
			if (k > (n - k)) {
				k = n - k;
			}

			b = 1;
			for (int i = 1, m = n; i <= k; i++, m--) {
				b = Math.multiplyExact(b, m) / i;
			}
			binomial[n][k] = b;
		}
		return b;
	}

}
