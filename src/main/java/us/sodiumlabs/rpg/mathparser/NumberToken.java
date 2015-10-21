package us.sodiumlabs.rpg.mathparser;

import java.math.BigDecimal;
import java.util.Stack;

public class NumberToken implements MathToken {
    private final BigDecimal payload;

    public NumberToken(final String number) {
        payload = new BigDecimal(number);
    }

    public NumberToken(final BigDecimal decimal) {
        payload = decimal;
    }

    public BigDecimal getPayload() {
        return payload;
    }

    @Override
    public boolean isOperator() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public int precedence() {
        return 0;
    }

    @Override
    public boolean rightAssociative() {
        return false;
    }

    @Override
    public BigDecimal operate(final Stack<MathToken> numbers)
            throws MathTokenizerException
    {
        throw new MathTokenizerException("Numbers cannot be used as operations!");
    }
}
