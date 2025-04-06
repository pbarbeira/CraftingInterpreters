package org.craftinginterpreters.challenges;


import org.craftinginterpreters.lox.Token;
import org.craftinginterpreters.lox.TokenType;
import org.craftinginterpreters.lox.ast.Expr;

import java.util.Stack;

public class RpnPrinter implements Expr.Visitor<String> {
    Stack<String> operationStack = new Stack<>();
    Stack<String> literalStack = new Stack<>();

    public static void main(String[] args){
        Expr expr = new Expr.Binary(
                new Expr.Binary(
                        new Expr.Literal(1),
                        new Token(TokenType.PLUS, "+", null, 1),
                        new Expr.Literal(2)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Binary(
                        new Expr.Literal(4),
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(3)
                )
        );
        System.out.println(new RpnPrinter().print(expr));
    }

    String print(Expr expr){
        return expr.accept(this);
    }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return expr.left.accept(this)
                + " " + expr.right.accept(this)
                + " " + expr.operator.lexeme;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.right.accept(this)
                + " " + expr.operator.lexeme;
    }
}
