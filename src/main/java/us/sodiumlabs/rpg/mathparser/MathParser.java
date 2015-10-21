package us.sodiumlabs.rpg.mathparser;

import java.math.BigDecimal;

public interface MathParser {
    BigDecimal eval(final String s) throws MathTokenizerException;
}
