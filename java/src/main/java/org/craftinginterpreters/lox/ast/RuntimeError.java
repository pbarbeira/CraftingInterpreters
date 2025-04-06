package org.craftinginterpreters.lox.ast;

import org.craftinginterpreters.lox.Token;

public class RuntimeError extends RuntimeException{
    public final Token token;

    RuntimeError(Token token, String message){
        super(message);
        this.token = token;
    }

}
