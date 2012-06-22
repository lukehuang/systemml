package com.ibm.bi.dml.hops;

import com.ibm.bi.dml.lops.CrossvalLop;
import com.ibm.bi.dml.lops.Lops;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.parser.MetaLearningFunctionParameters;
import com.ibm.bi.dml.parser.Expression.DataType;
import com.ibm.bi.dml.parser.Expression.ValueType;
import com.ibm.bi.dml.sql.sqllops.SQLLops;
import com.ibm.bi.dml.utils.HopsException;
import com.ibm.bi.dml.utils.LopsException;


public class CrossvalOp extends Hops {

	MetaLearningFunctionParameters _params ;

	public CrossvalOp(String l, DataType dt, ValueType vt, PartitionOp input, MetaLearningFunctionParameters params) {
		super(Hops.Kind.CrossvalOp, l, dt, vt);
		getInput().add(input) ;
		input.getParent().add(this) ;
		this._params = params ;
	}

	@Override
	public String getOpString() {
		String s = new String("");
		s += "CV";
		return s;
	}

	public void printMe() throws HopsException {
		if (get_visited() != VISIT_STATUS.DONE) {
			super.printMe();
			System.out.println("  CrossvalOp: " + "\n");
			for (Hops h : getInput()) {
				h.printMe();
			}
			;
		}
		set_visited(VISIT_STATUS.DONE);
	}

	public Lops constructLops() throws HopsException, LopsException {
		if(get_lops() == null) {
			Lops pLop = getInput().get(0).constructLops() ;
			CrossvalLop cvlop = new CrossvalLop(pLop, _params) ;
			set_lops(cvlop) ;
		}
		return get_lops() ;
	}

	@Override
	public SQLLops constructSQLLOPs() throws HopsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ExecType optFindExecType() throws HopsException {
		// TODO Auto-generated method stub
		return null;
	}
}
