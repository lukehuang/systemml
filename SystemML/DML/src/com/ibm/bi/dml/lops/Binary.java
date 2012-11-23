package com.ibm.bi.dml.lops;

import com.ibm.bi.dml.lops.LopProperties.ExecLocation;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.lops.compile.JobType;
import com.ibm.bi.dml.parser.Expression.*;
import com.ibm.bi.dml.utils.LopsException;


/**
 * Lop to perform binary operation. Both inputs must be matrices or vectors. 
 * Example - A = B + C, where B and C are matrices or vectors.
 */

public class Binary extends Lops 
{
	public enum OperationTypes {
		ADD, SUBTRACT, MULTIPLY, DIVIDE,
		LESS_THAN, LESS_THAN_OR_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS, EQUALS, NOT_EQUALS,
		AND, OR, 
		MAX, MIN, NOTSUPPORTED};	
	OperationTypes operation;
	

	
	/**
	 * Constructor to perform a binary operation.
	 * @param input
	 * @param op
	 */

	public Binary(Lops input1, Lops input2, OperationTypes op, DataType dt, ValueType vt, ExecType et) {
		super(Lops.Type.Binary, dt, vt);
		init(input1, input2, op, dt, vt, et);
		
	}
	
	public Binary(Lops input1, Lops input2, OperationTypes op, DataType dt, ValueType vt) {
		super(Lops.Type.Binary, dt, vt);	
		init(input1, input2, op, dt, vt, ExecType.MR);
	}
	
	private void init(Lops input1, Lops input2, OperationTypes op, DataType dt, ValueType vt, ExecType et) 
	{
		operation = op;
		this.addInput(input1);
		this.addInput(input2);
		input1.addOutput(this);
		input2.addOutput(this);
		
		boolean breaksAlignment = false;
		boolean aligner = false;
		boolean definesMRJob = false;
		
		if ( et == ExecType.MR ) {
			lps.addCompatibility(JobType.GMR);
			lps.addCompatibility(JobType.RAND);
			lps.addCompatibility(JobType.REBLOCK_BINARY);
			lps.addCompatibility(JobType.REBLOCK_TEXT);
			this.lps.setProperties( et, ExecLocation.Reduce, breaksAlignment, aligner, definesMRJob );
		}
		else if ( et == ExecType.CP ){
			lps.addCompatibility(JobType.INVALID);
			this.lps.setProperties( et, ExecLocation.ControlProgram, breaksAlignment, aligner, definesMRJob );
		}
	}

	@Override
	public String toString() {
	
		return " Operation: " + operation;

	}

	/**
	 * method to get operation type
	 * @return
	 */
	 
	public OperationTypes getOperationType()
	{
		return operation;
	}

	private String getOpcode() {
		switch(operation) {
		/* Arithmetic */
		case ADD:
			return "+";
		case SUBTRACT:
			return "-";
		case MULTIPLY:
			return "*";
		case DIVIDE:
			return "/";
		
		/* Relational */
		case LESS_THAN:
			return "<";
		case LESS_THAN_OR_EQUALS:
			return "<=";
		case GREATER_THAN:
			return ">";
		case GREATER_THAN_OR_EQUALS:
			return ">=";
		case EQUALS:
			return "==";
		case NOT_EQUALS:
			return "!=";
		
			/* Boolean */
		case AND:
			return "&&";
		case OR:
			return "||";
		
		
		/* Builtin Functions */
		case MIN:
			return "min";
		case MAX:
			return "max";
			
		default:
			throw new UnsupportedOperationException(this.printErrorLocation() + "Instruction is not defined for Binary operation: " + operation);
		}
	}
	
	@Override
	public String getInstructions(String input1, String input2, String output) 
		throws LopsException 
	{
		StringBuilder sb = new StringBuilder();
		sb.append( getExecType() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( getOpcode() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( input1 );
		sb.append( DATATYPE_PREFIX );
		sb.append( getInputs().get(0).get_dataType() );
		sb.append( VALUETYPE_PREFIX );
		sb.append( getInputs().get(0).get_valueType() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( input2 );
		sb.append( DATATYPE_PREFIX );
		sb.append( getInputs().get(1).get_dataType() );
		sb.append( VALUETYPE_PREFIX );
		sb.append( getInputs().get(1).get_valueType() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( output );
		sb.append( DATATYPE_PREFIX );
		sb.append( get_dataType() );
		sb.append( VALUETYPE_PREFIX );
		sb.append( get_valueType() );
		
		return sb.toString();
	}
	
	@Override
	public String getInstructions(int input_index1, int input_index2, int output_index) throws LopsException
	{
		StringBuilder sb = new StringBuilder();
		sb.append( getExecType() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( getOpcode() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( input_index1 );
		sb.append( DATATYPE_PREFIX );
		sb.append( getInputs().get(0).get_dataType() );
		sb.append( VALUETYPE_PREFIX );
		sb.append( getInputs().get(0).get_valueType() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( input_index2 );
		sb.append( DATATYPE_PREFIX );
		sb.append( getInputs().get(1).get_dataType() );
		sb.append( VALUETYPE_PREFIX );
		sb.append( getInputs().get(1).get_valueType() );
		sb.append( OPERAND_DELIMITOR );
		sb.append( output_index );
		sb.append( DATATYPE_PREFIX );
		sb.append( get_dataType() );
		sb.append( VALUETYPE_PREFIX );
		sb.append( get_valueType() );
		
		return sb.toString();
	}

 
}