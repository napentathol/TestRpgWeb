package us.sodiumlabs.rpg.mathparser.function;

import us.sodiumlabs.rpg.mathparser.MathFunction;
import us.sodiumlabs.rpg.mathparser.MathToken;
import us.sodiumlabs.rpg.mathparser.MathTokenizerException;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.function.BinaryOperator;

public class MultiBinaryFunction implements MathToken {
    private final int numArgs;

    private final BinaryOperator<BigDecimal> operation;

    public MultiBinaryFunction(final String[] args, final BinaryOperator<BigDecimal> operation)
            throws MathTokenizerException
    {
        if(args.length < 2)
            throw new MathTokenizerException("Cannot run function on less than 2 args.");

        this.numArgs = args.length;
        this.operation = operation;
    }

    @Override
    public boolean isOperator() {
        return true;
    }

    @Override
    public boolean isNumber() {
        return false;
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
    public BigDecimal operate(final Stack<MathToken> numbers) throws MathTokenizerException {
        BigDecimal out = MathFunction.popNumber(numbers);

        for(int i = 1; i < numArgs; i++) {
            out = operation.apply(out, MathFunction.popNumber(numbers));
        }

        return out;
    }
}
