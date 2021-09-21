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

public abstract class AggregatableMetrics {

	public final class DoubleMetric implements SampleMetric {
		private final String name;
		private final DoubleSupplier aggregate;

		public DoubleMetric(String name, DoubleSupplier aggregate) {
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
			return name;
		}
	}

	protected SolutionList sample;

	private double[] values = null;

	protected double min = -1;
	protected double max = -1;
	protected double mean = -1;
	protected double median = -1;
	protected double variance = -1;
	protected double standardDeviation = -1;

	public List<SampleMetric> getAllAggregates() {
		final List<SampleMetric> aggregates = new ArrayList<>(6);
		aggregates.add(getAggregate("min", this::getMin));
		aggregates.add(getAggregate("max", this::getMax));
		aggregates.add(getAggregate("mean", this::getMean));
		aggregates.add(getAggregate("median", this::getMedian));
		aggregates.add(getAggregate("variance", this::getVariance));
		aggregates.add(getAggregate("standardDeviation", this::getStandardDeviation));
		return aggregates;
	}

	public abstract SampleMetric getAggregate(String name, DoubleSupplier aggregate);

	public double[] getValues() {
		if (values == null) {
			values = computeValues();
		}
		return values;
	}

	protected abstract double[] computeValues();

	public void setSample(SolutionList sample) {
		if ((this.sample == null) || (this.sample != sample)) {
			this.sample = sample;
			reset();
		}
	}

	protected void reset() {
		values = null;
		min = -1;
		max = -1;
		mean = -1;
		median = -1;
		variance = -1;
		standardDeviation = -1;
	}

	protected double getMin() {
		if (min < 0) {
			min = Double.MAX_VALUE;
			for (final double count : getValues()) {
				if (min > count) {
					min = count;
				}
			}
		}
		return min;
	}

	protected double getMax() {
		if (max < 0) {
			max = 0;
			for (final double count : getValues()) {
				if (max < count) {
					max = count;
				}
			}
		}
		return max;
	}

	protected double getMean() {
		if (mean < 0) {
			double sum = 0;
			for (final double count : getValues()) {
				sum += count;
			}
			mean = sum / values.length;
		}
		return mean;
	}

	protected double getMedian() {
		if (median < 0) {
			final double[] counts = getValues();
			final double[] sortedCounts = Arrays.copyOf(counts, counts.length);
			Arrays.sort(sortedCounts);

			final int middle = sortedCounts.length / 2;
			median = ((sortedCounts.length % 2) != 0) //
				? sortedCounts[middle] //
				: (sortedCounts[middle - 1] + sortedCounts[middle]) / 2.0;
		}
		return median;
	}

	protected double getVariance() {
		if (variance < 0) {
			final double mean = getMean();
			variance = 0;
			for (final double count : getValues()) {
				final double diff = count - mean;
				variance += diff * diff;
			}
			variance /= values.length;
		}
		return variance;
	}

	protected double getStandardDeviation() {
		if (standardDeviation < 0) {
			standardDeviation = Math.sqrt(getVariance());
		}
		return standardDeviation;
	}

}
