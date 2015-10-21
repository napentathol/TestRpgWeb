package us.sodiumlabs.rpg.mathparser;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.Stack;

public class PolishProcessor {
    public BigDecimal process(final Queue<MathToken> tokens) throws MathTokenizerException {
        final Stack<MathToken> numbers = new Stack<>();

        while(tokens.size() > 0) {
            final MathToken token = tokens.remove();

            if(token.isNumber()) {
                numbers.push(token);
            } else if(token.isOperator()) {
                numbers.push(
                    new NumberToken(token.operate(numbers)));
            }
        }

        if(numbers.size() > 1)
            throw new MathTokenizerException("Not enough operations!");

        if(numbers.size() < 1)
            throw new MathTokenizerException("Unable to calculate!");

        final MathToken token = numbers.pop();

        if(!token.isNumber())
            throw new MathTokenizerException("Result is not a number!");

        return ((NumberToken)token).getPayload();
    }
}
