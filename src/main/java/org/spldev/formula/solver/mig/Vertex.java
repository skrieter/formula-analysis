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

public class Vertex implements Comparable<Vertex> {

	public enum Status {
		Normal, Core, Dead
	}

	private final int literal;

	private Status status = Status.Normal;

	ArrayList<LiteralList> complexClauses = new ArrayList<>();
	ArrayList<Vertex> stronglyConnetedVertices = new ArrayList<>();

	public Vertex(int literal) {
		this.literal = literal;
	}

	public int getVar() {
		return literal;
	}

	public List<LiteralList> getComplexClauses() {
		return complexClauses;
	}

	public List<Vertex> getStrongEdges() {
		return stronglyConnetedVertices;
	}

	public void addStronglyConnected(Vertex vertex) {
		stronglyConnetedVertices.add(vertex);
	}

	public void addWeaklyConnected(LiteralList clause) {
		complexClauses.add(clause);
	}

	public Status getStatus() {
		return status;
	}

	public boolean isCore() {
		return status == Status.Core;
	}

	public boolean isDead() {
		return status == Status.Dead;
	}

	public boolean isNormal() {
		return status == Status.Normal;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return literal;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		return literal == ((Vertex) obj).literal;
	}

	@Override
	public int compareTo(Vertex other) {
		return literal - other.literal;
	}

	@Override
	public String toString() {
		return String.valueOf(literal);
	}

	public void finish() {
		complexClauses = new ArrayList<>(new HashSet<>(complexClauses));
		stronglyConnetedVertices = new ArrayList<>(new HashSet<>(stronglyConnetedVertices));
		stronglyConnetedVertices.remove(this);
		Collections.sort(complexClauses);
		Collections.sort(stronglyConnetedVertices);
//		complexClauses.trimToSize();
//		stronglyConnetedVertices.trimToSize();
	}

}
