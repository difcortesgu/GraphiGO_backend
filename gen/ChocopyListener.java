// Generated from C:/Users/diego/Documents/Lenguajes/GraphiGO/grammar\Chocopy.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ChocopyParser}.
 */
public interface ChocopyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(ChocopyParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(ChocopyParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#class_def}.
	 * @param ctx the parse tree
	 */
	void enterClass_def(ChocopyParser.Class_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#class_def}.
	 * @param ctx the parse tree
	 */
	void exitClass_def(ChocopyParser.Class_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#class_body}.
	 * @param ctx the parse tree
	 */
	void enterClass_body(ChocopyParser.Class_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#class_body}.
	 * @param ctx the parse tree
	 */
	void exitClass_body(ChocopyParser.Class_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#class_body_def}.
	 * @param ctx the parse tree
	 */
	void enterClass_body_def(ChocopyParser.Class_body_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#class_body_def}.
	 * @param ctx the parse tree
	 */
	void exitClass_body_def(ChocopyParser.Class_body_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#func_def}.
	 * @param ctx the parse tree
	 */
	void enterFunc_def(ChocopyParser.Func_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#func_def}.
	 * @param ctx the parse tree
	 */
	void exitFunc_def(ChocopyParser.Func_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#func_body}.
	 * @param ctx the parse tree
	 */
	void enterFunc_body(ChocopyParser.Func_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#func_body}.
	 * @param ctx the parse tree
	 */
	void exitFunc_body(ChocopyParser.Func_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#typed_var}.
	 * @param ctx the parse tree
	 */
	void enterTyped_var(ChocopyParser.Typed_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#typed_var}.
	 * @param ctx the parse tree
	 */
	void exitTyped_var(ChocopyParser.Typed_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ChocopyParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ChocopyParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#global_decl}.
	 * @param ctx the parse tree
	 */
	void enterGlobal_decl(ChocopyParser.Global_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#global_decl}.
	 * @param ctx the parse tree
	 */
	void exitGlobal_decl(ChocopyParser.Global_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#nonlocal_decl}.
	 * @param ctx the parse tree
	 */
	void enterNonlocal_decl(ChocopyParser.Nonlocal_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#nonlocal_decl}.
	 * @param ctx the parse tree
	 */
	void exitNonlocal_decl(ChocopyParser.Nonlocal_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#var_def}.
	 * @param ctx the parse tree
	 */
	void enterVar_def(ChocopyParser.Var_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#var_def}.
	 * @param ctx the parse tree
	 */
	void exitVar_def(ChocopyParser.Var_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(ChocopyParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(ChocopyParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#end_file}.
	 * @param ctx the parse tree
	 */
	void enterEnd_file(ChocopyParser.End_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#end_file}.
	 * @param ctx the parse tree
	 */
	void exitEnd_file(ChocopyParser.End_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSimple_stmt(ChocopyParser.Simple_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSimple_stmt(ChocopyParser.Simple_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(ChocopyParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(ChocopyParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(ChocopyParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(ChocopyParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ChocopyParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ChocopyParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#expr_or}.
	 * @param ctx the parse tree
	 */
	void enterExpr_or(ChocopyParser.Expr_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#expr_or}.
	 * @param ctx the parse tree
	 */
	void exitExpr_or(ChocopyParser.Expr_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#expr_and}.
	 * @param ctx the parse tree
	 */
	void enterExpr_and(ChocopyParser.Expr_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#expr_and}.
	 * @param ctx the parse tree
	 */
	void exitExpr_and(ChocopyParser.Expr_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#simple_expr}.
	 * @param ctx the parse tree
	 */
	void enterSimple_expr(ChocopyParser.Simple_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#simple_expr}.
	 * @param ctx the parse tree
	 */
	void exitSimple_expr(ChocopyParser.Simple_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#cexpr}.
	 * @param ctx the parse tree
	 */
	void enterCexpr(ChocopyParser.CexprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#cexpr}.
	 * @param ctx the parse tree
	 */
	void exitCexpr(ChocopyParser.CexprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#cexpr_sum}.
	 * @param ctx the parse tree
	 */
	void enterCexpr_sum(ChocopyParser.Cexpr_sumContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#cexpr_sum}.
	 * @param ctx the parse tree
	 */
	void exitCexpr_sum(ChocopyParser.Cexpr_sumContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#cexpr_mul}.
	 * @param ctx the parse tree
	 */
	void enterCexpr_mul(ChocopyParser.Cexpr_mulContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#cexpr_mul}.
	 * @param ctx the parse tree
	 */
	void exitCexpr_mul(ChocopyParser.Cexpr_mulContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#simple_value}.
	 * @param ctx the parse tree
	 */
	void enterSimple_value(ChocopyParser.Simple_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#simple_value}.
	 * @param ctx the parse tree
	 */
	void exitSimple_value(ChocopyParser.Simple_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#cmp}.
	 * @param ctx the parse tree
	 */
	void enterCmp(ChocopyParser.CmpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#cmp}.
	 * @param ctx the parse tree
	 */
	void exitCmp(ChocopyParser.CmpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#op_suma}.
	 * @param ctx the parse tree
	 */
	void enterOp_suma(ChocopyParser.Op_sumaContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#op_suma}.
	 * @param ctx the parse tree
	 */
	void exitOp_suma(ChocopyParser.Op_sumaContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#op_mul}.
	 * @param ctx the parse tree
	 */
	void enterOp_mul(ChocopyParser.Op_mulContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#op_mul}.
	 * @param ctx the parse tree
	 */
	void exitOp_mul(ChocopyParser.Op_mulContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChocopyParser#target}.
	 * @param ctx the parse tree
	 */
	void enterTarget(ChocopyParser.TargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChocopyParser#target}.
	 * @param ctx the parse tree
	 */
	void exitTarget(ChocopyParser.TargetContext ctx);
}