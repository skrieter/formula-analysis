package org.spldev.formula.clause.configuration.twise;

import java.util.*;

import org.spldev.formula.clause.*;

/**
 * Represents a presence condition as an expression.
 *
 * @author Sebastian Krieter
 */
public class PresenceCondition extends ClauseList {

	private static final long serialVersionUID = -292364320078721008L;

	private transient final TreeSet<Integer> groups = new TreeSet<>();

	public PresenceCondition() {
		super();
	}

	public PresenceCondition(ClauseList otherClauseList) {
		super(otherClauseList);
	}

	public PresenceCondition(Collection<? extends LiteralList> c) {
		super(c);
	}

	public PresenceCondition(int size) {
		super(size);
	}

	public void addGroup(int group) {
		groups.add(group);
	}

	public Set<Integer> getGroups() {
		return groups;
	}

	@Override
	public String toString() {
		return "Expression [" + super.toString() + "]";
	}

}
