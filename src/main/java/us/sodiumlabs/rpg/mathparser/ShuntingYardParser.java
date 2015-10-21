package us.sodiumlabs.rpg.mathparser;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShuntingYardParser implements MathParser {

    final ShuntingYard shuntingYard = new ShuntingYard();

    final PolishProcessor polishProcessor = new PolishProcessor();

    @Override
    public BigDecimal eval(final String s)
            throws MathTokenizerException
    {
        return polishProcessor.process(shuntingYard.process(s));
    }
}
