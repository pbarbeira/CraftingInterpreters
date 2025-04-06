package org.craftinginterpreters.lox.ast;

import org.craftinginterpreters.lox.Lox;
import org.craftinginterpreters.lox.Token;

public class AstInterpreter implements Expr.Visitor<Object> {
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = _evaluate(expr.left);
        Object right = _evaluate(expr.right);

        switch(expr.operator.type){
            case MINUS:
                _checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                _checkNumberOperands(expr.operator, left, right);
                _checkDivideByZero(expr.operator, right);
                return (double) left / (double) right;
            case STAR:
                _checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case PLUS:
                if(left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if(left instanceof String || right instanceof String) {
                    return _stringify(left) +  _stringify(right);
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case GREATER:
                _checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                _checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                _checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                _checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                _checkNumberOperands(expr.operator, left, right);
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                _checkNumberOperands(expr.operator, left, right);
                return isEqual(left, right);
        };
        return null;
    }

    private boolean isEqual(Object a, Object b){
        if(a == null & b == null){
            return true;
        }
        if(a == null){
            return false;
        }
        return a.equals(b);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return _evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = _evaluate(expr.right);

        switch(expr.operator.type){
            case BANG: return !isTruthy(right);
            case MINUS:
                _checkNumberOperand(expr.operator, right);
                return -(double)right;
        }
        return null;
    }

    private Object _evaluate(Expr expr){
        return expr.accept(this);
    }

    private boolean isTruthy(Object object){
        if(object == null) {
            return false;
        }
        if(object instanceof Boolean) {
            return (boolean) object;
        }
        return true;
    }

    private void _checkNumberOperand(Token operator, Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void _checkNumberOperands(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void _checkDivideByZero(Token operator, Object right){
        if((double)right != 0) return;
        throw new RuntimeError(operator, "Can't divide by 0.");
    }

    public void interpret(Expr expression){
        try{
            Object value = _evaluate(expression);
            System.out.println(_stringify(value));
        }catch(RuntimeError error){
            Lox.runtimeError(error);
        }
    }

    private String _stringify(Object obj){
        if(obj == null){
            return "nil";
        }
        if(obj instanceof Double){
            String text = obj.toString();
            if(text.endsWith(".0")){
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return obj.toString();
    }
}
