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
package org.spldev.formula.configuration.sample;

import java.util.*;
import java.util.function.*;

import org.spldev.formula.clauses.*;

public class CountMetrics {

	public final class CountMetric implements SampleMetric {
		private final String name;
		private final DoubleSupplier aggregate;

		public CountMetric(String name, DoubleSupplier aggregate) {
			this.name = name;
			this.aggregate = aggregate;
		}

		@Override
		public double get(SolutionList sample) {
			setSample(sample);
			return aggregate.getAsDouble();
		}

		@Override
		public String getName() {
			return name + countFunction.getName() + "Count";
		}

	}

	private final CountFunction countFunction;

	private SolutionList sample;

	private double[] counts = null;
	private double min = -1;
	private double max = -1;
	private double mean = -1;
	private double median = -1;
	private double variance = -1;
	private double standardDeviation = -1;

	public CountMetrics(CountFunction countFunction) {
		this.countFunction = countFunction;
	}

	public static List<CountMetric> getAllAggregates(CountFunction countFunction) {
		final CountMetrics metrics = new CountMetrics(countFunction);
		final List<CountMetric> aggregates = new ArrayList<>(6);
		aggregates.add(metrics.getMinCount());
		aggregates.add(metrics.getMaxCount());
		aggregates.add(metrics.getMeanCount());
		aggregates.add(metrics.getMedianCount());
		aggregates.add(metrics.getVarianceCount());
		aggregates.add(metrics.getStandardDeviationCount());
		return aggregates;
	}

	public double[] getCounts() {
		if (counts == null) {
			final List<LiteralList> solutions = sample.getSolutions();
			final int size = solutions.size();
			counts = new double[size];
			for (int i = 0; i < (size - 1); i++) {
				counts[i] = countFunction.computeCount(solutions.get(i));
			}
		}
		return counts;
	}

	public void setSample(SolutionList sample) {
		if ((this.sample == null) || (this.sample != sample)) {
			this.sample = sample;
			counts = null;
			min = -1;
			max = -1;
			mean = -1;
			median = -1;
			variance = -1;
			standardDeviation = -1;
		}
	}

	public CountMetric getMinCount() {
		return new CountMetric("Min", this::getMin);
	}

	public CountMetric getMaxCount() {
		return new CountMetric("Max", this::getMax);
	}

	public CountMetric getMeanCount() {
		return new CountMetric("Mean", this::getMean);
	}

	public CountMetric getMedianCount() {
		return new CountMetric("Median", this::getMedian);
	}

	public CountMetric getVarianceCount() {
		return new CountMetric("Variance", this::getVariance);
	}

	public CountMetric getStandardDeviationCount() {
		return new CountMetric("StandardDeviation", this::getStandardDeviation);
	}

	private double getMin() {
		if (min < 0) {
			min = Double.MAX_VALUE;
			for (final double count : getCounts()) {
				if (min > count) {
					min = count;
				}
			}
		}
		return min;
	}

	private double getMax() {
		if (max < 0) {
			max = 0;
			for (final double count : getCounts()) {
				if (max < count) {
					max = count;
				}
			}
		}
		return max;
	}

	private double getMean() {
		if (mean < 0) {
			double sum = 0;
			for (final double count : getCounts()) {
				sum += count;
			}
			mean = sum / counts.length;
		}
		return mean;
	}

	private double getMedian() {
		if (median < 0) {
			final double[] counts = getCounts();
			final double[] sortedCounts = Arrays.copyOf(counts, counts.length);
			Arrays.sort(sortedCounts);

			final int middle = sortedCounts.length / 2;
			median = ((sortedCounts.length % 2) != 0) //
				? sortedCounts[middle] //
				: (sortedCounts[middle - 1] + sortedCounts[middle]) / 2.0;
		}
		return median;
	}

	private double getVariance() {
		if (variance < 0) {
			final double mean = getMean();
			variance = 0;
			for (final double count : getCounts()) {
				final double diff = count - mean;
				variance += diff * diff;
			}
			variance /= counts.length;
		}
		return variance;
	}

	private double getStandardDeviation() {
		if (standardDeviation < 0) {
			standardDeviation = Math.sqrt(getVariance());
		}
		return standardDeviation;
	}

}
