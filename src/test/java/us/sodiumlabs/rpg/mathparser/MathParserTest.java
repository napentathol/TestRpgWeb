package us.sodiumlabs.rpg.mathparser;

import org.junit.Assert;
import org.junit.Test;

public class MathParserTest {

    @Test
    public void testEval() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        Assert.assertEquals(10.5, parser.eval("1 + 3 / 2 * 5 + 5 * 2 - 8").doubleValue(), 0.000001);
        Assert.assertEquals(1.5, parser.eval("4.5 / 3").doubleValue(), 0.000001);
        Assert.assertEquals(1.5, parser.eval("4.5 % 3").doubleValue(), 0.000001);
        Assert.assertEquals(1.3, parser.eval("4.5 % 3.2").doubleValue(), 0.000001);
        Assert.assertEquals(9.75, parser.eval("1.5 \r  \t   *6.5").doubleValue(), 0.000001);
        Assert.assertEquals(2.2, parser.eval("5.5-\n         3.3").doubleValue(), 0.000001);
        Assert.assertEquals(12345.67,
                parser.eval("12246.9046                  +                                 98.7654").doubleValue(),
                0.000001);
        Assert.assertEquals(3, parser.eval("3").doubleValue(), 0.000001);
    }

    @Test
    public void testEvalParentheticals() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        Assert.assertEquals(12, parser.eval("(1 + 3) / 2 * 5 + 5 * 2 - 8").doubleValue(), 0.000001);
        Assert.assertEquals(22, parser.eval("((1 + 3) / 2 * 5 + 5) * 2 - 8").doubleValue(), 0.000001);
        Assert.assertEquals(-90, parser.eval("((1 + 3) / 2 * 5 + 5) * (2 - 8)").doubleValue(), 0.000001);
        Assert.assertEquals(-120, parser.eval("((1 + 3) / 2 * (5 + 5)) * (2 - 8)").doubleValue(), 0.000001);
        Assert.assertEquals(-120, parser.eval("((1 + (3)) / (2) * (5 + 5)) * (2 - 8)").doubleValue(), 0.000001);
    }

    @Test
    public void testEvalFunctions() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        Assert.assertEquals(4, parser.eval("MAX(1,2,3,4)").doubleValue(), 0.000001);
        Assert.assertEquals(4, parser.eval("MAX(4,3,2,1)").doubleValue(), 0.000001);
        Assert.assertEquals(9, parser.eval("MAX(4,3,2,1)+5").doubleValue(), 0.000001);
        Assert.assertEquals(14, parser.eval("5+MAX(4,3,2,1)+5").doubleValue(), 0.000001);
        Assert.assertEquals(9, parser.eval("5+MAX(4,3,2,1)").doubleValue(), 0.000001);
        Assert.assertEquals(4, parser.eval("MAX(1+1+1+1,1+1+1,1+1+1-1,1+1-1)").doubleValue(), 0.000001);
        Assert.assertEquals(4, parser.eval("MAX(1+1+1-1,1+1-1,1+1+1+1,1+1+1)").doubleValue(), 0.000001);
        Assert.assertEquals(7, parser.eval("MAX(1+1+1-1,1+1-1,1+1+1+1,MAX(5,7))").doubleValue(), 0.000001);
        Assert.assertEquals(8, parser.eval("MAX(MAX(4,MAX(7,8),6),1+1-1,1+1+1+1,MAX(5,7))").doubleValue(), 0.000001);
        Assert.assertEquals(7, parser.eval("MAX(MIN(4,MAX(7,8),6),1+1-1,1+1+1+1,MAX(5,7))").doubleValue(), 0.000001);
    }

    @Test(expected = MathTokenizerException.class)
    public void testMissingCloseParen() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("( 2 +1");
    }

    @Test(expected = MathTokenizerException.class)
    public void testMissingNestedCloseParen() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("(( 2 + 1) +8");
    }

    @Test(expected = MathTokenizerException.class)
    public void testMissingOpenParen() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval(" 2 +1)");
    }

    @Test(expected = MathTokenizerException.class)
    public void testMissingNestedOpenParen() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("( 2 + 1) +8)");
    }

    @Test(expected = MathTokenizerException.class)
    public void testNumberFailure() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("123.45.67");
    }

    @Test(expected = MathTokenizerException.class)
    public void testDoubleSameOperationFailure() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("123.45 + + 67");
    }

    @Test(expected = MathTokenizerException.class)
    public void testDoubleDifferentOperationFailure() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("123.45 / - 67");
    }

    @Test(expected = MathTokenizerException.class)
    public void testDoubleNumberFailure() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("6  5");
    }

    @Test(expected = MathTokenizerException.class)
    public void testUnexpectedTokenFailure() throws MathTokenizerException {
        final MathParser parser = new ShuntingYardParser();

        parser.eval("6 $ 5");
    }
}