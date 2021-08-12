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
package org.spldev.formula.solver.mig;

import java.util.*;

import org.spldev.formula.clauses.*;

/**
 * Compares the dependencies of the {@link LiteralList literals} using a
 * {@link MIG}.
 *
 * @author Sebastian Krieter
 */
public class MIGComparator implements Comparator<LiteralList> {

	private static class VertexInfo {

		int weakOut, weakIn, strongOut, strongIn;

		@Override
		public String toString() {
			return "VertexInfo [weakOut=" + weakOut + ", weakIn=" + weakIn + ", strongOut=" + strongOut + ", strongIn="
				+ strongIn + "]";
		}
	}

	private final VertexInfo[] vertexInfos;

	public MIGComparator(MIG mig) {
		vertexInfos = new VertexInfo[mig.getVertices().size()];
		for (final Vertex vertex : mig.getVertices()) {
			vertexInfos[MIG.getVertexIndex(vertex)] = new VertexInfo();
		}
		for (final Vertex vertex : mig.getVertices()) {
			final VertexInfo vertexInfo = vertexInfos[MIG.getVertexIndex(vertex)];
			vertexInfo.strongOut = vertex.getStrongEdges().size();
			vertexInfo.weakOut = vertex.getComplexClauses().size();
			for (final Vertex strongEdge : vertex.getStrongEdges()) {
				vertexInfos[MIG.getVertexIndex(strongEdge)].strongIn++;
			}
			for (final LiteralList clause : vertex.getComplexClauses()) {
				for (final int literal : clause.getLiterals()) {
					if (literal != vertex.getVar()) {
						vertexInfos[MIG.getVertexIndex(literal)].weakIn++;
					}
				}
			}
		}
	}

	@Override
	public int compare(LiteralList o1, LiteralList o2) {
		final double f1 = computeValue(o1);
		final double f2 = computeValue(o2);
		return (int) Math.signum(f1 - f2);
	}

	public String getValue(LiteralList o1) {
		final VertexInfo vi1 = vertexInfos[MIG.getVertexIndex(o1.getLiterals()[0])];
		final double f1 = computeValue(o1);
		return o1 + " | " + vi1 + " -> " + f1;
	}

	public double computeValue(LiteralList... set) {
		int vIn = 0;
		int vOut = 0;
		for (final LiteralList literalSet : set) {
			for (final int literal : literalSet.getLiterals()) {
				final VertexInfo info = vertexInfos[MIG.getVertexIndex(literal)];
				vIn += (info.strongIn) + info.weakIn;
				vOut += (info.strongOut) + info.weakOut;
			}
		}
		return vIn - (vOut * vOut);
	}

	public int getOut(LiteralList... set) {
		int vOut = 0;
		for (final LiteralList literalSet : set) {
			for (final int literal : literalSet.getLiterals()) {
				final VertexInfo info = vertexInfos[MIG.getVertexIndex(literal)];
				vOut += info.strongOut + info.weakOut;
			}
		}
		return vOut;
	}

}
