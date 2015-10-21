package us.sodiumlabs.rpg.mathparser;

import java.math.BigDecimal;
import java.util.Stack;

public interface MathToken {
    public boolean isOperator();

    public boolean isNumber();

    public int precedence();

    public boolean rightAssociative();

    public BigDecimal operate(final Stack<MathToken> numbers) throws MathTokenizerException;
}
