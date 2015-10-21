package us.sodiumlabs.rpg.mathparser.function;

import us.sodiumlabs.rpg.mathparser.MathFunction;
import us.sodiumlabs.rpg.mathparser.MathToken;
import us.sodiumlabs.rpg.mathparser.MathTokenizerException;

import java.math.BigDecimal;
import java.util.Stack;

public class RollerFunction implements MathToken {
    public enum RollerFunctionType {
        GRT {
            @Override
            public BigDecimal operate(final BigDecimal out, final BigDecimal numberOfDice, final BigDecimal diceSides)
                    throws MathTokenizerException
            {
                return out.max(roll(numberOfDice, diceSides));
            }
        },
        LST {
            @Override
            public BigDecimal operate(final BigDecimal out, final BigDecimal numberOfDice, final BigDecimal diceSides)
                    throws MathTokenizerException
            {
                return out.min(roll(numberOfDice, diceSides));
            }
        },
        FST;

        public BigDecimal operate(final BigDecimal out, final BigDecimal numberOfDice, final BigDecimal diceSides)
                throws MathTokenizerException
        {
            return out;
        }
    }

    public RollerFunction(final RollerFunctionType type) {
        this.type = type;
    }

    final RollerFunctionType type;

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
        final BigDecimal numberOfDice = MathFunction.popNumber(numbers);
        final BigDecimal diceSides = MathFunction.popNumber(numbers);

        final BigDecimal out = roll(numberOfDice, diceSides);

        return type.operate(out, numberOfDice, diceSides);
    }

    private static BigDecimal roll(final BigDecimal numberOfDice, final BigDecimal diceSides) throws MathTokenizerException {
        try {
            final int n = numberOfDice.intValueExact();
            final int d = diceSides.intValueExact();

            return BigDecimal.valueOf(roll(n, d));
        } catch (final ArithmeticException ex) {
            throw new MathTokenizerException("Cannot roll on a fractional number!");
        }
    }

    private static int roll(final int n, final int d) {
        int sum = 0;

        for(int i = 0; i < n; i++) {
            sum += (int)(Math.random() * d) + 1;
        }

        return sum;
    }
}
