package org.craftinginterpreters.lox;

import org.craftinginterpreters.lox.ast.AstInterpreter;
import org.craftinginterpreters.lox.ast.AstPrinter;
import org.craftinginterpreters.lox.ast.Expr;
import org.craftinginterpreters.lox.ast.RuntimeError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static final AstInterpreter _interpreter = new AstInterpreter();
    static boolean _hadError = false;
    static boolean _hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }else if(args.length == 1){
            runFile(args[0]);
        }else{
            runPrompt();
        }
    }

    static void error(int line, String message){
        report(line, "", message);
    }

    static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line, " at end", message);
        }else{
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error){
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        _hadRuntimeError = true;
    }

    private static void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        _hadError = true;
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if(_hadError) {
            System.exit(65);
        }
        if(_hadRuntimeError){
            System.exit(70);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            _hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expr = parser.parse();

        if(_hadError) return;

        _interpreter.interpret(expr);
    }
}