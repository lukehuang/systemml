package com.ibm.bi.dml.parser;

import java.util.ArrayList;

import com.ibm.bi.dml.utils.LanguageException;


public class IfStatement extends Statement{

	private ConditionalPredicate _predicate;
	private ArrayList<StatementBlock> _ifBody;
	private ArrayList<StatementBlock> _elseBody;
	
	public Statement rewriteStatement(String prefix) throws LanguageException{
		throw new LanguageException(this.printErrorLocation() + "should not call rewriteStatement for IfStatement");
	}
	
	public IfStatement(){
		 _predicate = null;
		 _ifBody = new ArrayList<StatementBlock>();
		 _elseBody = new ArrayList<StatementBlock>();
	}
	
	public void setConditionalPredicate(ConditionalPredicate pred){
		_predicate = pred;
	}
	
	
	public void addStatementBlockIfBody(StatementBlock sb){
		_ifBody.add(sb);
	}
	
	public void addStatementBlockElseBody(StatementBlock sb){
		_elseBody.add(sb);
	}
	
	public ConditionalPredicate getConditionalPredicate(){
		return _predicate;
	}
	
	public ArrayList<StatementBlock> getIfBody(){
		return _ifBody;
	}
	
	public ArrayList<StatementBlock> getElseBody(){
		return _elseBody;
	}
	
	public void setIfBody(ArrayList<StatementBlock> body){
		_ifBody = body;
	}
	
	public void setElseBody(ArrayList<StatementBlock> body){
		_elseBody = body;
	}
	
	
	@Override
	public boolean controlStatement() {
		return true;
	}
	
	public void initializeforwardLV(VariableSet activeIn) throws LanguageException{
		throw new LanguageException(this.printErrorLocation() + "should never call initializeforwardLV for IfStatement");
		
	}

	public VariableSet initializebackwardLV(VariableSet lo) throws LanguageException{
		throw new LanguageException(this.printErrorLocation() + "should never call initializeforwardLV for IfStatement");
		
	}

	public void mergeStatementBlocksIfBody(){
		_ifBody = StatementBlock.mergeStatementBlocks(_ifBody);
	}
	
	public void mergeStatementBlocksElseBody(){
		if (_elseBody.size() > 0)
			_elseBody = StatementBlock.mergeStatementBlocks(_elseBody);
	}
	
	public VariableSet variablesReadIfBody() {
		
		return null;
		
	}
	
	public VariableSet variablesReadElseBody() {
		
		System.out.println("WARNING: line " + this.getBeginLine() + ", column " + this.getBeginColumn() + " --  should not call variablesReadElseBody from IfStatement ");
		return null;
	}
	
	public  VariableSet variablesUpdatedIfBody() {
		
		System.out.println("WARNING: line " + this.getBeginLine() + ", column " + this.getBeginColumn() + " --  should not call variablesUpdatedIfBody from IfStatement ");
		return null;
	}
	
	public  VariableSet variablesUpdatedElseBody() {
		
		System.out.println("WARNING: line " + this.getBeginLine() + ", column " + this.getBeginColumn() + " --  should not call variablesUpdatedElseBody from IfStatement ");
		return null;
	}
	
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("if ( ");
		sb.append(_predicate.toString());
		sb.append(") { \n");
		for (StatementBlock block : _ifBody){
			sb.append(block.toString());
		}
		sb.append("}\n");
		if (_elseBody.size() > 0){
			sb.append(" else { \n");
			for (StatementBlock block : _elseBody){
				sb.append(block.toString());
			}
			sb.append("}\n");
		}
		return sb.toString();
	}

	
	public VariableSet variablesKill() {
		return new VariableSet();
	}

	@Override
	public VariableSet variablesRead() {
		System.out.println("WARNING: line " + this.getBeginLine() + ", column " + this.getBeginColumn() + " --  should not call variablesRead from IfStatement ");
		return null;
	}

	@Override
	public VariableSet variablesUpdated() {
		System.out.println("WARNING: line " + this.getBeginLine() + ", column " + this.getBeginColumn() + " --  should not call variablesUpdated from IfStatement ");
		return null;
	}
}
