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
