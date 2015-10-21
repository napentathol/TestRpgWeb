package us.sodiumlabs.rpg.mathparser;

import java.util.*;

public class ShuntingYard {
    public Queue<MathToken> process(final String toEvaluate)
            throws MathTokenizerException
    {
        int position = 0;
        final Queue<MathToken> tokens = new LinkedList<>();
        final Stack<MathToken> shunt = new Stack<>();

        while(checkPosition(position, toEvaluate)) {
            position = parseAndPushToken(toEvaluate, position, tokens, shunt);
        }

        pushRemainingTokens(tokens, shunt);

        return tokens;
    }

    private boolean checkPosition(final int p, final String s) {
        return p < s.length();
    }

    //<editor-fold desc="parsing">
    private int parseAndPushToken(
            final String toEvaluate,
            final int position,
            final Queue<MathToken> tokens,
            final Stack<MathToken> shunt
    )
            throws MathTokenizerException
    {
        final char pChar = toEvaluate.charAt(position);

        if (isWhitespace(pChar)) {
            return advancePastWhitespace(toEvaluate, position + 1);
        } else if (isNumericCharacter(pChar)) {
            return advancePastNumber(toEvaluate, position, tokens);
        } else if (Operator.isOperator(pChar)) {
            return advancePastOperator(toEvaluate, position, tokens, shunt);
        } else if (isOpenParen(pChar)) {
            return advancePastParenthetical(toEvaluate, position + 1, tokens);
        } else if (isCloseParen(pChar)) {
            throw new MathTokenizerException("Missing open parenthesis.");
        } else if (isAlpha(pChar)) {
            return advancePastFunction(toEvaluate, position, tokens);
        }

        throw new MathTokenizerException("Encountered invalid token!");
    }

    private int advanceToMatchingParen(
            final String toEvaluate,
            final int position
    )
            throws MathTokenizerException
    {
        int depth = 0;

        int p = position;

        while(checkPosition(p, toEvaluate) && depth >= 0) {
            final char pChar = toEvaluate.charAt(p);

            if(isOpenParen(pChar)) {
                depth++;
            } else if(isCloseParen(pChar)) {
                depth--;
            }

            p++;
        }

        if(!checkPosition(p, toEvaluate) && !isCloseParen(toEvaluate.charAt(p - 1)))
            throw new MathTokenizerException("Missing close parenthesis.");

        return p;
    }

    private void pushRemainingTokens(final Queue<MathToken> tokens, final Stack<MathToken> shunt) {
        while (shunt.size() > 0) {
            tokens.add(shunt.pop());
        }
    }
    //</editor-fold>

    //<editor-fold desc="functions">
    private int advancePastFunction(
            final String toEvaluate,
            final int position,
            final Queue<MathToken> tokens
    ) throws MathTokenizerException {
        int p = position;

        // Advance past function name.
        while (checkPosition(p, toEvaluate) && isAlpha(toEvaluate.charAt(p))) p++;

        final String funcName = toEvaluate.substring(position, p);

        // Check function name.
        final MathFunction function = MathFunction.reverseLookup(funcName);

        // Advance past whitespace.
        while (checkPosition(p, toEvaluate) && isWhitespace(toEvaluate.charAt(p))) p++;

        // Advance past args.
        if(!checkPosition(p, toEvaluate) || !isOpenParen(toEvaluate.charAt(p)))
            throw new MathTokenizerException("Function missing open paren!");

        final int argStart = p++ + 1;

        p = advanceToMatchingParen(toEvaluate, p + 1);

        final String[] args = splitFunctionArgs(toEvaluate.substring(argStart, p - 1));

        // parse args and enqueue
        for(final String arg : args) {
            tokens.addAll(process(arg));
        }

        tokens.add(function.createMathToken(args));

        return p;
    }

    private String[] splitFunctionArgs(final String substring) throws MathTokenizerException {
        int p = 0;
        int lastArg = 0;

        final List<String> args = new ArrayList<>();

        while(checkPosition(p, substring)) {
            final char pChar = substring.charAt(p);

            if (isArgSplit(pChar)) {
                args.add(substring.substring(lastArg,p));

                lastArg = p++ + 1;
            } else if (isOpenParen(pChar)) {
                p = advanceToMatchingParen(substring, p + 1);
            } else {
                p++;
            }
        }

        args.add(substring.substring(lastArg,p));

        return args.toArray(new String[args.size()]);
    }

    private boolean isArgSplit(final char pChar) {
        return pChar == ',';
    }

    private boolean isAlpha(final char pChar) {
        return Character.isAlphabetic(pChar);
    }
    //</editor-fold>

    //<editor-fold desc="parentheticals">
    private int advancePastParenthetical(final String toEvaluate, final int position, final Queue<MathToken> tokens)
            throws MathTokenizerException
    {
        final int p = advanceToMatchingParen(toEvaluate, position);

        final Queue<MathToken> parenTokens = process(toEvaluate.substring(position, p - 1));

        tokens.addAll(parenTokens);

        return p;
    }

    private boolean isCloseParen(final char pChar) {
        return pChar == ')';
    }

    private boolean isOpenParen(final char pChar) {
        return pChar == '(';
    }
    //</editor-fold>

    //<editor-fold desc="operators">
    private int advancePastOperator(
            final String toEvaluate,
            final int position,
            final Queue<MathToken> tokens,
            final Stack<MathToken> shunt
    )
            throws MathTokenizerException
    {
        final Operator operator = Operator.parseOperator(toEvaluate.charAt(position));

        while(shunt.size() > 0) {
            final MathToken testToken = shunt.peek();

            if(operator.rightAssociative() && testToken.precedence() > operator.precedence()
                    || (!operator.rightAssociative() && testToken.precedence() >= operator.precedence())
            ) {
                tokens.add(shunt.pop());
            } else {
                break;
            }
        }

        shunt.push(operator);

        return position + 1;
    }
    //</editor-fold>

    //<editor-fold desc="numbers">
    private int advancePastNumber(final String toEvaluate, final int position, final Queue<MathToken> tokens)
            throws MathTokenizerException
    {
        int advance = position + 1;
        boolean foundDecimalPoint = false;

        while (checkPosition(advance, toEvaluate) && isNumericCharacter(toEvaluate.charAt(advance))) {
            if(isDecimalPoint(toEvaluate.charAt(advance))) {
                if(foundDecimalPoint) {
                    throw new MathTokenizerException("Encountered invalid number; has two decimal points!");
                } else {
                    foundDecimalPoint = true;
                }
            }

            advance++;
        }

        tokens.add(new NumberToken(toEvaluate.substring(position, advance)));

        return advance;
    }

    private boolean isNumericCharacter(final char pChar) {
        return isDigit(pChar) || isDecimalPoint(pChar);
    }

    private boolean isDecimalPoint(final char pChar) {
        // TODO: support internalational decimal points
        return '.' == pChar;
    }

    private boolean isDigit(final char pChar) {
        return Character.isDigit(pChar);
    }
    //</editor-fold>

    //<editor-fold desc="whitespace">
    private int advancePastWhitespace(final String toEvaluate, final int position) {
        int p = position;

        while(checkPosition(position, toEvaluate) && isWhitespace(toEvaluate.charAt(p))) p++;

        return p;
    }

    private boolean isWhitespace(final char pChar) {
        return Character.isWhitespace(pChar);
    }
    //</editor-fold>
}
