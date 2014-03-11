/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2014
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.runtime.instructions.MRInstructions;

import com.ibm.bi.dml.lops.MapMult;
import com.ibm.bi.dml.runtime.DMLRuntimeException;
import com.ibm.bi.dml.runtime.DMLUnsupportedOperationException;
import com.ibm.bi.dml.runtime.functionobjects.Multiply;
import com.ibm.bi.dml.runtime.functionobjects.Plus;
import com.ibm.bi.dml.runtime.instructions.Instruction;
import com.ibm.bi.dml.runtime.instructions.InstructionUtils;
import com.ibm.bi.dml.runtime.matrix.DistributedCacheInput;
import com.ibm.bi.dml.runtime.matrix.io.MatrixIndexes;
import com.ibm.bi.dml.runtime.matrix.io.MatrixValue;
import com.ibm.bi.dml.runtime.matrix.io.OperationsOnMatrixValues;
import com.ibm.bi.dml.runtime.matrix.mapred.CachedValueMap;
import com.ibm.bi.dml.runtime.matrix.mapred.IndexedMatrixValue;
import com.ibm.bi.dml.runtime.matrix.mapred.MRBaseForCommonInstructions;
import com.ibm.bi.dml.runtime.matrix.operators.AggregateBinaryOperator;
import com.ibm.bi.dml.runtime.matrix.operators.AggregateOperator;
import com.ibm.bi.dml.runtime.matrix.operators.Operator;


public class AggregateBinaryInstruction extends BinaryMRInstructionBase 
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2014\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	public AggregateBinaryInstruction(Operator op, byte in1, byte in2, byte out, String istr)
	{
		super(op, in1, in2, out);
		mrtype = MRINSTRUCTION_TYPE.AggregateBinary;
		instString = istr;
	}
	
	public static Instruction parseInstruction ( String str ) throws DMLRuntimeException {
		
		InstructionUtils.checkNumFields ( str, 3 );
		
		String[] parts = InstructionUtils.getInstructionParts ( str );
		
		byte in1, in2, out;
		String opcode = parts[0];
		in1 = Byte.parseByte(parts[1]);
		in2 = Byte.parseByte(parts[2]);
		out = Byte.parseByte(parts[3]);
		
		if ( opcode.equalsIgnoreCase("cpmm") 
				|| opcode.equalsIgnoreCase("rmm") 
				|| opcode.equalsIgnoreCase(MapMult.OPCODE) 
			) {
			AggregateOperator agg = new AggregateOperator(0, Plus.getPlusFnObject());
			AggregateBinaryOperator aggbin = new AggregateBinaryOperator(Multiply.getMultiplyFnObject(), agg);
			return new AggregateBinaryInstruction(aggbin, in1, in2, out, str);
		} 
		else {
			throw new DMLRuntimeException("AggregateBinaryInstruction.parseInstruction():: Unknown opcode " + opcode);
		}
		
		// return null;
	}

	@Override
	public void processInstruction(Class<? extends MatrixValue> valueClass,
			CachedValueMap cachedValues, IndexedMatrixValue tempValue,
			IndexedMatrixValue zeroInput, int blockRowFactor, int blockColFactor)
			throws DMLUnsupportedOperationException, DMLRuntimeException 
	{	
		IndexedMatrixValue in1=cachedValues.getFirst(input1);
		IndexedMatrixValue in2=cachedValues.getFirst(input2);
		
		String opcode = InstructionUtils.getOpCode(instString);
		
		if ( in2 == null && opcode.equals(MapMult.OPCODE) ) {
			// one of the input is from distributed cache.
			processMapMultInstruction(valueClass, cachedValues, in1, in2, blockRowFactor, blockColFactor);
			return;
		}
		
		if(in1==null || in2==null)
			return;

		//allocate space for the output value
		IndexedMatrixValue out;
		if(output==input1 || output==input2)
			out=tempValue;
		else
			out=cachedValues.holdPlace(output, valueClass);
		
		//process instruction
		OperationsOnMatrixValues.performAggregateBinary(
				    in1.getIndexes(), in1.getValue(), 
					in2.getIndexes(), in2.getValue(), 
					out.getIndexes(), out.getValue(), 
					((AggregateBinaryOperator)optr), false);
		
		//put the output value in the cache
		if(out==tempValue)
			cachedValues.add(output, out);
	}
	
	/**
	 * Helper function to perform map-side matrix-matrix multiplication.
	 * 
	 * @param valueClass
	 * @param cachedValues
	 * @param in1
	 * @param in2
	 * @param blockRowFactor
	 * @param blockColFactor
	 * @throws DMLRuntimeException
	 * @throws DMLUnsupportedOperationException
	 */
	private void processMapMultInstruction(Class<? extends MatrixValue> valueClass, CachedValueMap cachedValues, IndexedMatrixValue in1, IndexedMatrixValue in2, int blockRowFactor, int blockColFactor) throws DMLRuntimeException, DMLUnsupportedOperationException {

		DistributedCacheInput dcInput = MRBaseForCommonInstructions.dcValues.get(input2);
		
		long in2_cols = dcInput.getNumCols();
		int  in2_colBlocks = (int) Math.ceil(in2_cols/(double)blockColFactor);
		
		for(int bidx=1; bidx <= in2_colBlocks; bidx++) {
			
			// Matrix multiply A[i,k] %*% B[k,bid]
			
			// Setup input2 block
			IndexedMatrixValue in2Block = dcInput.getDataBlock(in1.getIndexes().getColumnIndex(), bidx, blockRowFactor, blockColFactor);
						
			MatrixValue in2BlockValue = in2Block.getValue(); 
			MatrixIndexes in2BlockIndex = in2Block.getIndexes();
			
			//allocate space for the output value
			IndexedMatrixValue out;
			out=cachedValues.holdPlace(output, valueClass);
				
			//process instruction
			OperationsOnMatrixValues.performAggregateBinary(in1.getIndexes(), in1.getValue(), 
						in2BlockIndex, in2BlockValue, out.getIndexes(), out.getValue(), 
						((AggregateBinaryOperator)optr), false);
			
		}
		
	}

}
