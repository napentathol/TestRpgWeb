package us.sodiumlabs.rpg.mathparser;

import us.sodiumlabs.rpg.mathparser.function.MultiBinaryFunction;
import us.sodiumlabs.rpg.mathparser.function.RollerFunction;

import java.math.BigDecimal;
import java.util.Stack;

public enum MathFunction {
    MAX {
        @Override
        public MathToken createMathToken(final String[] args) throws MathTokenizerException {
            return new MultiBinaryFunction(args, BigDecimal::max);
        }
    },
    MIN {
        @Override
        public MathToken createMathToken(final String[] args) throws MathTokenizerException {
            return new MultiBinaryFunction(args, BigDecimal::min);
        }
    },
    ROLL {
        @Override
        public MathToken createMathToken(final String[] args) throws MathTokenizerException {
            return new RollerFunction(RollerFunction.RollerFunctionType.FST);
        }
    },
    ADVN {
        @Override
        public MathToken createMathToken(final String[] args) throws MathTokenizerException {
            return new RollerFunction(RollerFunction.RollerFunctionType.GRT);
        }
    },
    DSVN {
        @Override
        public MathToken createMathToken(final String[] args) throws MathTokenizerException {
            return new RollerFunction(RollerFunction.RollerFunctionType.LST);
        }
    };

    public static BigDecimal popNumber(final Stack<MathToken> numbers) throws MathTokenizerException {
        final MathToken tmp = numbers.pop();
        if(!tmp.isNumber())
            throw new MathTokenizerException("Attempted to run a function on non number!");

        return ((NumberToken)tmp).getPayload();
    }

    public abstract MathToken createMathToken(final String[] args)
            throws MathTokenizerException;

    public static MathFunction reverseLookup(final String funcName) {
        return MathFunction.valueOf(funcName.toUpperCase());
    }
}
