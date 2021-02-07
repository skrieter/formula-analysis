package org.spldev.formula.clause.mig.visitor;

public interface ITraverser {

	Visitor<?> getVisitor();

	void setVisitor(Visitor<?> visitor);

	void setModel(int[] model);

	void traverse(int... curLiterals);

	void traverseStrong(int... curLiterals);

}
