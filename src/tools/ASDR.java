package tools;

import java.awt.*;
import java.util.List;

public class ASDR implements Parser {
    private int i = 0;
    private Token preAn;
    private List<Token> tokens;
    private String message = "Error on: " + (this.i + 1) + ". Expecting:" + this.tokens.get(this.i);

    public ASDR(List<Token> tokens) {
        this.tokens = tokens;
        this.preAn = this.tokens.get(i);
    }

    public void error() {
        System.out.println(message);
    }

    public void match(TipoToken tt) {
        this.preAn = tokens.get(this.i);
        if (this.preAn.tipo == tt) {
            i++;
            preAn = tokens.get(this.i);
        } else {
            error();
        }
    }

    @Override
    public boolean parse() {
        Program();
        return false;
    }

    // TODO Revisar GLC para poder realizar pruebas
    /*
     ! PROGRAM -> DECLARATION
     *
     * -----
     *
     ? DECLARATION -> FUN_DECLARATION DECLARATION
     ? DECLARATION -> VAR_DECLARATION DECLARATION
     ? DECLARATION -> STATEMENT DECLARATION
     ? DECLARATION -> Ɛ
     * 
     ? FUN_DECLARATION -> fun FUNCTION
     ? VAR_DECLARATION -> var id VAR_INIT ;
     ? VAR_INIT -> = EXPRESSION
     ? VAR_INIT -> Ɛ
     * 
     * -----
     * 
     ! STATEMENT -> EXPR_STMT
     ! STATEMENT -> FOR_STMT
     ! STATEMENT -> IF_STMT
     ! STATEMENT -> PRINT_STMT
     ! STATEMENT -> RETURN_STMT
     ! STATEMENT -> WHILE_STMT
     ! STATEMENT -> BLOCK
     * 
     ! EXPR_STMT -> EXPRESSION ;
     * 
     ! FOR_STMT -> for ( FOR_STMT1 ; FOR_STMT2 ; FOR_STMT3 ) STATEMENT
     ! FOR_STMT1 -> VAR_DECLARATION
     ! FOR_STMT1 -> EXPR_STMT
     ! FOR_STMT1 -> ;
     ! FOR_STMT2 -> EXPRESSION ;
     ! FOR_STMT2 -> ;
     ! FOR_STMT3 -> EXPRESSION
     ! FOR_STMT3 -> Ɛ
     * 
     ! IF_STMT -> if ( EXPRESSION ) STATEMENT ELSE_STATEMENT
     ! ELSE_STATEMENT -> else STATEMENT
     *
     ? PRINT_STMT -> print EXPRESSION ;
     * 
     ? RETURN_STMT -> return RETURN_EXPR_OPC ;
     ? RETURN_EXPR_OPC -> EXPRESSION
     ? RETURN_EXPR_OPC -> Ɛ
     * 
     ? WHILE_STMT -> while ( EXPRESSION ) STATEMENT
     * 
     ! BLOCK -> { DECLARATION }
     * 
     * -----
     * 
     ! EXPRESSION -> ASSIGNMENT
     * 
     ! ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
     ! ASSIGNMENT_OPC -> = EXPRESSION
     ! ASSIGNMENT_OPC -> Ɛ
     * 
     ! LOGIC_OR -> LOGIC_AND LOGIC_OR_2
     ! LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2
     ! LOGIC_OR_2 -> Ɛ
     * 
     ! LOGIC_AND -> EQUALITY LOGIC_AND_2
     ! LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2
     ! LOGIC_AND_2 -> Ɛ
     * 
     ! EQUALITY -> COMPARISON EQUALITY_2
     ! EQUALITY_2 -> != COMPARISON EQUALITY_2
     ! EQUALITY_2 -> == COMPARISON EQUALITY_2
     ! EQUALITY_2 -> Ɛ
     * 
     ! COMPARISON -> TERM COMPARISON_2
     ! COMPARISON_2 -> > TERM COMPARISON_2
     ! COMPARISON_2 -> < TERM COMPARISON_2
     ! COMPARISON_2 -> >= TERM COMPARISON_2
     ! COMPARISON_2 -> <= TERM COMPARISON_2
     ! COMPARISON_2 -> Ɛ
     *
     ! TERM -> FACTOR TERM_2
     ! TERM_2 -> + FACTOR TERM_2
     ! TERM_2 -> - FACTOR TERM_2
     ! TERM_2 -> Ɛ
     * 
     ! FACTOR -> UNARY FACTOR_2
     ! FACTOR_2 -> * UNARY FACTOR_2
     ! FACTOR_2 -> / UNARY FACTOR_2
     ! FACTOR_2 -> Ɛ
     * 
     ! UNARY -> ! UNARY
     ! UNARY -> - UNARY
     ! UNARY -> CALL
     * 
     ! CALL -> PRIMARY CALL_2
     ! CALL_2 -> ( ARGUMENTS_OPC ) CALL_2
     ! CALL_2 -> Ɛ
     * 
     ? PRIMARY -> ( EXPRESSION )
     ? PRIMARY -> NUMBER
     ? PRIMARY -> STRING
     ? PRIMARY -> TRUE
     ? PRIMARY -> FALSE
     ? PRIMARY -> IDENTIFIER
     ? PRIMARY -> NULL
     * 
     * -----
     * 
     ! FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
     ! FUNCTIONS -> FUN_DECLARATION FUNCTIONS
     ! FUNCTIONS -> Ɛ
     * 
     ! PARAMETERS_OPC -> PARAMETERS
     ! PARAMETERS_OPC -> Ɛ
     * 
     ! PARAMETERS -> id PARAMETERS_2
     ! PARAMETERS_2 -> , id PARAMETERS_2
     ! PARAMETERS_2 -> Ɛ
     * 
     ! ARGUMENTS_OPC -> EXPRESSION ARGUMENTS
     ! ARGUMENTS_OPC -> Ɛ
     * 
     ! ARGUMENTS -> , EXPRESSION ARGUMENTS
     ! ARGUMENTS -> Ɛ
     */

    // ! PROGRAM
    private void Program() {
        Declaration();
    }

    // ! DECLARATION
    private void Declaration() {
        if (this.preAn.tipo.equals(TipoToken.FUN)) {
            FunDeclaration();
        } else if (this.preAn.tipo.equals(TipoToken.VAR)) {
            VarDeclaration();
        } else {
            Statement();
            Declaration();
        }
    }

    // ! FUN_DECLARATION
    private void FunDeclaration() {
        match(TipoToken.FUN);
        Function();
    }

    // ! VAR_DECLARATION
    private void VarDeclaration() {
        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        VarInit();
        match(TipoToken.SEMICOLON);
    }

    // ! VAR_INIT
    private void VarInit() {
        if (this.preAn.tipo.equals(TipoToken.EQUAL)) {
            match(TipoToken.EQUAL);
            Expression();
        }
    }

    // ! STATEMENT
    private void Statement() {
        if (this.preAn.tipo.equals(TipoToken.LEFT_PAREN) || this.preAn.tipo.equals(TipoToken.IDENTIFIER)) {
            ExprStmt();
        } else if (this.preAn.tipo.equals(TipoToken.FOR)) {
            ForStmt();
        } else if (this.preAn.tipo.equals(TipoToken.IF)) {
            IfStmt();
        } else if (this.preAn.tipo.equals(TipoToken.WHILE)) {
            WhileStmt();
        } else if (this.preAn.tipo.equals(TipoToken.RETURN)) {
            ReturnStmt();
        } else if (this.preAn.tipo.equals(TipoToken.LEFT_BRACE)) {
            Block();
        } else {
            error();
        }
    }

    // ! EXPR_STMT
    private void ExprStmt() {
        Expression();
        match(TipoToken.SEMICOLON);
    }

    // ! FOR_STMT
    private void ForStmt() {
        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        ForStmt1();
        match(TipoToken.SEMICOLON);
        ForStmt2();
        match(TipoToken.SEMICOLON);
        ForStmt3();
        match(TipoToken.RIGHT_PAREN);
        Statement();
    }

    // ! FOR_STMT_1
    private void ForStmt1() {
        if (this.preAn.tipo.equals(TipoToken.VAR)) {
            VarDeclaration();
        } else if (this.preAn.tipo.equals(TipoToken.SEMICOLON)) {
            match(TipoToken.SEMICOLON);
        } else {
            ExprStmt();
        }
    }

    // ! FOR_STMT_2
    private void ForStmt2() {
        if (!this.preAn.tipo.equals(TipoToken.SEMICOLON)) {
            Expression();
        }
        match(TipoToken.SEMICOLON);
    }

    // ! FOR_STMT_3
    private void ForStmt3() {
        if (!this.preAn.tipo.equals(TipoToken.RIGHT_PAREN)) {
            Expression();
        }
    }

    // ! IF_STMT
    private void IfStmt() {
        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        Expression();
        match(TipoToken.RIGHT_PAREN);
        Statement();
        ElseStmt();
    }

    // ! ELSE_STMT
    private void ElseStmt() {
        if (this.preAn.tipo.equals(TipoToken.ELSE)) {
            match(TipoToken.ELSE);
            Statement();
        }
    }

    // ! PRINT_STMT
    private void PrintStmt() {
        match(TipoToken.PRINT);
        Expression();
        match(TipoToken.SEMICOLON);
    }

    // ! RETURN_STMT
    private void ReturnStmt() {
        match(TipoToken.RETURN);
        Expression();
        match(TipoToken.SEMICOLON);
    }

    // ! WHILE_STMT
    private void WhileStmt() {
        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);
        Expression();
        match(TipoToken.RIGHT_PAREN);
        Statement();
    }

    // ! BLOCK
    private void Block() {
        match(TipoToken.LEFT_BRACE);
        Declaration();
        match(TipoToken.RIGHT_BRACE);
    }

    // ! EXPRESSION
    private boolean Expression() {
        Assignment();
        return false;
    }

    // ! ASSIGNMENT
    private void Assignment() {
        LogicOr();
        AssignmentOpc();
    }

    // ! ASSIGNMENTOPC
    private void AssignmentOpc() {
        if (this.preAn.tipo.equals(TipoToken.EQUAL)) {
            match(TipoToken.EQUAL);
            Assignment();
        }
    }

    // ! LOGIC_OR
    private void LogicOr() {
        LogicAnd();
        LogicOr2();
    }

    // ! LOGIC_OR_2
    private void LogicOr2() {
        if (this.preAn.tipo.equals(TipoToken.OR)) {
            match(TipoToken.OR);
            LogicAnd();
            LogicOr2();
        }
    }

    // ! LOGIC_AND
    private void LogicAnd() {
        Equality();
        LogicAnd2();
    }

    // ! LOGIC_AND_2
    private void LogicAnd2() {
        if (this.preAn.tipo.equals(TipoToken.AND)) {
            match(TipoToken.AND);
            Equality();
            LogicAnd2();
        }
    }

    // ! EQUALITY
    private void Equality() {
        Comparison();
        Equality2();
    }

    // ! EQUALITY_2
    private void Equality2() {
        if (this.preAn.tipo.equals(TipoToken.BANG_EQUAL)) {
            match(TipoToken.BANG_EQUAL);
            Comparison();
            Equality2();
        } else if (this.preAn.tipo.equals(TipoToken.EQUAL_EQUAL)) {
            match(TipoToken.EQUAL_EQUAL);
            Comparison();
            Equality2();
        }
    }

    // ! COMPARISON
    private void Comparison() {
        Term();
        Comparison2();
    }

    // ! COMPARISON_2
    private void Comparison2() {
        if (this.preAn.tipo.equals(TipoToken.GREATER)) {
            match(TipoToken.GREATER);
            Term();
            Comparison2();
        } else if (this.preAn.tipo.equals(TipoToken.GREATER_EQUAL)) {
            match(TipoToken.GREATER_EQUAL);
            Term();
            Comparison2();
        } else if (this.preAn.tipo.equals(TipoToken.LESS)) {
            match(TipoToken.LESS);
            Term();
            Comparison2();
        } else if (this.preAn.tipo.equals(TipoToken.LESS_EQUAL)) {
            match(TipoToken.LESS_EQUAL);
            Term();
            Comparison2();
        }
    }

    // ! TERM
    private void Term() {
        Factor();
        Term2();
    }

    // ! TERM_2
    private void Term2() {
        if (this.preAn.tipo.equals(TipoToken.MINUS)) {
            match(TipoToken.MINUS);
            Factor();
            Term2();
        } else if (this.preAn.tipo.equals(TipoToken.PLUS)) {
            match(TipoToken.PLUS);
            Factor();
            Term2();
        }
    }

    // ! FACTOR
    private void Factor() {
        Unary();
        Factor2();
    }

    // ! FACTOR_2
    private void Factor2() {
        if (this.preAn.tipo.equals(TipoToken.SLASH)) {
            match(TipoToken.SLASH);
            Unary();
            Factor2();
        } else if (this.preAn.tipo.equals(TipoToken.STAR)) {
            match(TipoToken.STAR);
            Unary();
            Factor2();
        }
    }

    // ! UNARY
    private void Unary() {
        switch (this.preAn.tipo) {
            case BANG:
                match(TipoToken.BANG);
                Unary();
                break;
            case MINUS:
                match(TipoToken.MINUS);
                Unary();
                break;
            default:
                Call();
                break;
        }
    }

    // ! CALL
    private void Call() {
        Primary();
        Call2();
    }

    // ! CALL_2
    private void Call2() {
        if (this.preAn.tipo.equals(TipoToken.LEFT_PAREN)) {
            match(TipoToken.LEFT_PAREN);
            ArgumentsOpc();
            if (this.preAn.tipo.equals(TipoToken.RIGHT_PAREN))
                match(TipoToken.RIGHT_PAREN);
            else
                error();
            Call2();
        }
    }

    // ! PRIMARY
    private void Primary() {
        if (this.preAn.tipo.equals(TipoToken.IDENTIFIER)) {
            match(TipoToken.IDENTIFIER);
        } else if (this.preAn.tipo.equals(TipoToken.NUMBER)) {
            match(TipoToken.NUMBER);
        } else if (this.preAn.tipo.equals(TipoToken.STRING)) {
            match(TipoToken.STRING);
        } else if (this.preAn.tipo.equals(TipoToken.TRUE)) {
            match(TipoToken.TRUE);
        } else if (this.preAn.tipo.equals(TipoToken.FALSE)) {
            match(TipoToken.FALSE);
        } else if (this.preAn.tipo.equals(TipoToken.NULL)) {
            match(TipoToken.NULL);
        } else if (this.preAn.tipo.equals(TipoToken.LEFT_PAREN)) {
            match(TipoToken.LEFT_PAREN);
            Expression();
            if (this.preAn.tipo.equals(TipoToken.RIGHT_PAREN))
                match(TipoToken.RIGHT_PAREN);
            else
                error();
        }
    }

    // ! FUNCTION
    private void Function() {
        match(TipoToken.IDENTIFIER);
        match(TipoToken.LEFT_PAREN);
        ArgumentsOpc();
        match(TipoToken.RIGHT_PAREN);
        Block();
    }

    // ! FUNCTIONS
    private void Functions() {
        if (this.preAn.tipo.equals(TipoToken.IDENTIFIER)) {
            match(TipoToken.IDENTIFIER);
            Function();
            Functions();
        }
    }

    // ! PARAMETERS
    private void Parameters() {
        match(TipoToken.IDENTIFIER);
        ParametersOpc();
    }

    // ! PARAMETERSOPc
    private void ParametersOpc() {
        if (this.preAn.tipo == TipoToken.COMMA) {
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            ParametersOpc();
        }
    }

    // ! ARGUMENTSOPC
    private void ArgumentsOpc() {
        Expression();
        Arguments();
    }

    // ! ARGUMENTS
    private void Arguments() {
        if (this.preAn.tipo == TipoToken.COMMA) {
            match(TipoToken.COMMA);
            Expression();
            Arguments();
        }
    }
}