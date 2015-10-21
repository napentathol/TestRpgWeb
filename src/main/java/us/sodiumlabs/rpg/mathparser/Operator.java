package us.sodiumlabs.rpg.mathparser;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.function.BinaryOperator;

public enum Operator implements MathToken {
    ADD('+', 2, BigDecimal::add),
    SUB('-', 2, BigDecimal::subtract),
    MUL('*', 3, BigDecimal::multiply),
    DIV('/', 3, BigDecimal::divide),
    MOD('%', 3, BigDecimal::remainder);

    private final char operator;

    private final int precedence;

    private final BinaryOperator<BigDecimal> operation;

    private Operator(final char oper, final int p, final BinaryOperator<BigDecimal> operation) {
        operator = oper;
        precedence = p;
        this.operation = operation;
    }

    public char getOperator() {
        return operator;
    }

    @Override
    public int precedence() {
        return precedence;
    }

    @Override
    public boolean rightAssociative() {
        return false;
    }

    public static boolean isOperator(final char c) {
        for(final Operator o : Operator.values()) {
            if(o.getOperator() == c) {
                return true;
            }
        }

        return false;
    }

    public static Operator parseOperator(final char c)
            throws MathTokenizerException
    {
        for(final Operator o : Operator.values()) {
            if(o.getOperator() == c) {
                return o;
            }
        }

        throw new MathTokenizerException("Unable to parse operator [" + c + "] operator may be missing!");
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isOperator() {
        return true;
    }

    @Override
    public BigDecimal operate(final Stack<MathToken> numbers) throws MathTokenizerException {
        if(numbers.size() < 2)
            throw new MathTokenizerException("Operation [" + operator + "] supports a minimum of 2 arguments");

        final MathToken token1 = numbers.pop();
        final MathToken token2 = numbers.pop();

        if(!(token1.isNumber() && token2.isNumber()))
            throw new MathTokenizerException("Operation [" + operator + "] cannot be performed on non-numbers");

        return operation.apply(
            ((NumberToken)token2).getPayload(),
            ((NumberToken)token1).getPayload()
        );
    }
}
