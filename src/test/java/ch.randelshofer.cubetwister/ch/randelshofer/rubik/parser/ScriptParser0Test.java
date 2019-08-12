package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ScriptParser0Test {
    private static boolean html = false;

    @TestFactory
    public List<DynamicTest> testParse() {
        DefaultNotation defaultNotation = new DefaultNotation();

        DefaultNotation precircumfixNotation = new DefaultNotation();
        precircumfixNotation.setName("precircumfix");
        precircumfixNotation.addToken(Symbol.CONJUGATION_DELIMITER, ",");
        precircumfixNotation.putSyntax(Symbol.COMMUTATION, Syntax.PRECIRCUMFIX);
        precircumfixNotation.putSyntax(Symbol.CONJUGATION, Syntax.PRECIRCUMFIX);
        precircumfixNotation.putSyntax(Symbol.ROTATION, Syntax.PRECIRCUMFIX);
        precircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        precircumfixNotation.putSyntax(Symbol.REPETITION, Syntax.PREFIX);
        precircumfixNotation.putSyntax(Symbol.REFLECTION, Syntax.PREFIX);
        precircumfixNotation.putSyntax(Symbol.INVERSION, Syntax.PREFIX);

        DefaultNotation prefixNotation = new DefaultNotation();
        prefixNotation.setName("prefix");
        prefixNotation.putSyntax(Symbol.COMMUTATION, Syntax.PREFIX);
        prefixNotation.putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        prefixNotation.putSyntax(Symbol.ROTATION, Syntax.PREFIX);
        prefixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PREFIX);
        prefixNotation.putSyntax(Symbol.REPETITION, Syntax.PREFIX);
        prefixNotation.putSyntax(Symbol.REFLECTION, Syntax.PREFIX);
        prefixNotation.putSyntax(Symbol.INVERSION, Syntax.PREFIX);

        DefaultNotation circumfixNotation = new DefaultNotation();
        circumfixNotation.setName("circumfix");
        circumfixNotation.addToken(Symbol.REFLECTION_BEGIN, "*");
        circumfixNotation.addToken(Symbol.REFLECTION_END, "*");
        circumfixNotation.addToken(Symbol.INVERSION_BEGIN, "'");
        circumfixNotation.addToken(Symbol.INVERSION_END, "'");
        circumfixNotation.putSyntax(Symbol.COMMUTATION, Syntax.PREFIX);
        circumfixNotation.putSyntax(Symbol.CONJUGATION, Syntax.PREFIX);
        circumfixNotation.putSyntax(Symbol.ROTATION, Syntax.PREFIX);
        circumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PREFIX);
        circumfixNotation.putSyntax(Symbol.REPETITION, Syntax.PREFIX);
        circumfixNotation.putSyntax(Symbol.REFLECTION, Syntax.CIRCUMFIX);
        circumfixNotation.putSyntax(Symbol.INVERSION, Syntax.CIRCUMFIX);

        DefaultNotation postcircumfixNotation = new DefaultNotation();
        postcircumfixNotation.setName("postcircumfix");
        postcircumfixNotation.addToken(Symbol.CONJUGATION_DELIMITER, ",");
        postcircumfixNotation.putSyntax(Symbol.COMMUTATION, Syntax.POSTCIRCUMFIX);
        postcircumfixNotation.putSyntax(Symbol.CONJUGATION, Syntax.POSTCIRCUMFIX);
        postcircumfixNotation.putSyntax(Symbol.ROTATION, Syntax.POSTCIRCUMFIX);
        postcircumfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.POSTCIRCUMFIX);

        DefaultNotation suffixNotation = new DefaultNotation();
        suffixNotation.setName("suffix");
        suffixNotation.putSyntax(Symbol.COMMUTATION, Syntax.SUFFIX);
        suffixNotation.putSyntax(Symbol.CONJUGATION, Syntax.SUFFIX);
        suffixNotation.putSyntax(Symbol.ROTATION, Syntax.SUFFIX);
        suffixNotation.putSyntax(Symbol.PERMUTATION, Syntax.SUFFIX);
        suffixNotation.putSyntax(Symbol.REPETITION, Syntax.SUFFIX);
        suffixNotation.putSyntax(Symbol.REFLECTION, Syntax.SUFFIX);
        suffixNotation.putSyntax(Symbol.INVERSION, Syntax.SUFFIX);

        DefaultNotation preinfixNotation = new DefaultNotation();
        preinfixNotation.setName("preinfix");
        preinfixNotation.addToken(Symbol.REPETITION_OPERATOR, "times");
        preinfixNotation.putSyntax(Symbol.COMMUTATION, Syntax.PREINFIX);
        preinfixNotation.putSyntax(Symbol.CONJUGATION, Syntax.PREINFIX);
        preinfixNotation.putSyntax(Symbol.ROTATION, Syntax.PREINFIX);
        preinfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.PREINFIX);
        preinfixNotation.putSyntax(Symbol.REPETITION, Syntax.PREINFIX);

        DefaultNotation postinfixNotation = new DefaultNotation();
        postinfixNotation.setName("postinfix");
        postinfixNotation.addToken(Symbol.REPETITION_OPERATOR, "times");
        postinfixNotation.putSyntax(Symbol.COMMUTATION, Syntax.POSTINFIX);
        postinfixNotation.putSyntax(Symbol.CONJUGATION, Syntax.POSTINFIX);
        postinfixNotation.putSyntax(Symbol.ROTATION, Syntax.POSTINFIX);
        postinfixNotation.putSyntax(Symbol.PERMUTATION, Syntax.POSTINFIX);
        postinfixNotation.putSyntax(Symbol.REPETITION, Syntax.POSTINFIX);


        return Arrays.asList(
                dynamicTest("prefixNotation 'R", ()->doParse(prefixNotation,"'R", "0..2ScriptNode{ 0..2InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("suffixNotation R'", ()->doParse(suffixNotation,"R'", "0..2ScriptNode{ 0..2InversionNode{ 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("circumfixNotation 'R'", ()->doParse(circumfixNotation,"'R'", "0..3ScriptNode{ 0..3InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("prefixNotation *R", ()->doParse(prefixNotation,"*R", "0..2ScriptNode{ 0..2ReflectionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("suffixNotation R*", ()->doParse(suffixNotation,"R*", "0..2ScriptNode{ 0..2ReflectionNode{ 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("circumfixNotation *R*", ()->doParse(circumfixNotation,"*R*", "0..3ScriptNode{ 0..3ReflectionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } }")),

                dynamicTest("prefixNotation 3R", ()->doParse(prefixNotation,"3R", "0..2ScriptNode{ 0..2RepetitionNode{ 3, 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("suffixNotation R3", ()->doParse(suffixNotation,"R3", "0..2ScriptNode{ 0..2RepetitionNode{ 3, 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("preinfixNotation 3 times R", ()->doParse(preinfixNotation,"3 times R", "0..9ScriptNode{ 0..9RepetitionNode{ 3, 8..9MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("postinfixNotation R times 3", ()->doParse(postinfixNotation,"R times 3", "0..9ScriptNode{ 0..9RepetitionNode{ 3, 0..1MoveNode{ax:0 lm:4 an:1} } }")),

                dynamicTest("defaultNotation R", ()->doParse(defaultNotation,"R", "0..1ScriptNode{ 0..1MoveNode{ax:0 lm:4 an:1} }")),
                dynamicTest("defaultNotation .", ()->doParse(defaultNotation,".", "0..1ScriptNode{ 0..1NOPNode{ } }")),

                dynamicTest("defaultNotation R", ()->doParse(defaultNotation,"R", "0..1ScriptNode{ 0..1MoveNode{ax:0 lm:4 an:1} }")),
                dynamicTest("defaultNotation U", ()->doParse(defaultNotation,"U", "0..1ScriptNode{ 0..1MoveNode{ax:1 lm:4 an:1} }")),
                dynamicTest("defaultNotation F", ()->doParse(defaultNotation,"F", "0..1ScriptNode{ 0..1MoveNode{ax:2 lm:4 an:1} }")),
                dynamicTest("defaultNotation L", ()->doParse(defaultNotation,"L", "0..1ScriptNode{ 0..1MoveNode{ax:0 lm:1 an:-1} }")),
                dynamicTest("defaultNotation D", ()->doParse(defaultNotation,"D", "0..1ScriptNode{ 0..1MoveNode{ax:1 lm:1 an:-1} }")),
                dynamicTest("defaultNotation B", ()->doParse(defaultNotation,"B", "0..1ScriptNode{ 0..1MoveNode{ax:2 lm:1 an:-1} }")),
                dynamicTest("defaultNotation R'", ()->doParse(defaultNotation,"R'", "0..2ScriptNode{ 0..2InversionNode{ 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("defaultNotation U'", ()->doParse(defaultNotation,"U'", "0..2ScriptNode{ 0..2InversionNode{ 0..1MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("defaultNotation F'", ()->doParse(defaultNotation,"F'", "0..2ScriptNode{ 0..2InversionNode{ 0..1MoveNode{ax:2 lm:4 an:1} } }")),
                dynamicTest("defaultNotation L'", ()->doParse(defaultNotation,"L'", "0..2ScriptNode{ 0..2InversionNode{ 0..1MoveNode{ax:0 lm:1 an:-1} } }")),
                dynamicTest("defaultNotation D'", ()->doParse(defaultNotation,"D'", "0..2ScriptNode{ 0..2InversionNode{ 0..1MoveNode{ax:1 lm:1 an:-1} } }")),
                dynamicTest("defaultNotation B'", ()->doParse(defaultNotation,"B'", "0..2ScriptNode{ 0..2InversionNode{ 0..1MoveNode{ax:2 lm:1 an:-1} } }")),
                dynamicTest("defaultNotation R2", ()->doParse(defaultNotation,"R2", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:4 an:2} }")),
                dynamicTest("defaultNotation U2", ()->doParse(defaultNotation,"U2", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:4 an:2} }")),
                dynamicTest("defaultNotation F2", ()->doParse(defaultNotation,"F2", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:4 an:2} }")),
                dynamicTest("defaultNotation L2", ()->doParse(defaultNotation,"L2", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:1 an:-2} }")),
                dynamicTest("defaultNotation D2", ()->doParse(defaultNotation,"D2", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:1 an:-2} }")),
                dynamicTest("defaultNotation B2", ()->doParse(defaultNotation,"B2", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:1 an:-2} }")),
                dynamicTest("defaultNotation MR", ()->doParse(defaultNotation,"MR", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:2 an:1} }")),
                dynamicTest("defaultNotation MU", ()->doParse(defaultNotation,"MU", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:2 an:1} }")),
                dynamicTest("defaultNotation MF", ()->doParse(defaultNotation,"MF", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:2 an:1} }")),
                dynamicTest("defaultNotation ML", ()->doParse(defaultNotation,"ML", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:2 an:-1} }")),
                dynamicTest("defaultNotation MD", ()->doParse(defaultNotation,"MD", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:2 an:-1} }")),
                dynamicTest("defaultNotation MB", ()->doParse(defaultNotation,"MB", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:2 an:-1} }")),
                dynamicTest("defaultNotation MR'", ()->doParse(defaultNotation,"MR'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:2 an:1} } }")),
                dynamicTest("defaultNotation MU'", ()->doParse(defaultNotation,"MU'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:2 an:1} } }")),
                dynamicTest("defaultNotation MF'", ()->doParse(defaultNotation,"MF'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:2 an:1} } }")),
                dynamicTest("defaultNotation ML'", ()->doParse(defaultNotation,"ML'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:2 an:-1} } }")),
                dynamicTest("defaultNotation MD'", ()->doParse(defaultNotation,"MD'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:2 an:-1} } }")),
                dynamicTest("defaultNotation MB'", ()->doParse(defaultNotation,"MB'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:2 an:-1} } }")),
                dynamicTest("defaultNotation MR2", ()->doParse(defaultNotation,"MR2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:2 an:2} }")),
                dynamicTest("defaultNotation MU2", ()->doParse(defaultNotation,"MU2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:2 an:2} }")),
                dynamicTest("defaultNotation MF2", ()->doParse(defaultNotation,"MF2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:2 an:2} }")),
                dynamicTest("defaultNotation ML2", ()->doParse(defaultNotation,"ML2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:2 an:-2} }")),
                dynamicTest("defaultNotation MD2", ()->doParse(defaultNotation,"MD2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:2 an:-2} }")),
                dynamicTest("defaultNotation MB2", ()->doParse(defaultNotation,"MB2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:2 an:-2} }")),
                dynamicTest("defaultNotation TR", ()->doParse(defaultNotation,"TR", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:6 an:1} }")),
                dynamicTest("defaultNotation TU", ()->doParse(defaultNotation,"TU", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:6 an:1} }")),
                dynamicTest("defaultNotation TF", ()->doParse(defaultNotation,"TF", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:6 an:1} }")),
                dynamicTest("defaultNotation TL", ()->doParse(defaultNotation,"TL", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:3 an:-1} }")),
                dynamicTest("defaultNotation TD", ()->doParse(defaultNotation,"TD", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:3 an:-1} }")),
                dynamicTest("defaultNotation TB", ()->doParse(defaultNotation,"TB", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:3 an:-1} }")),
                dynamicTest("defaultNotation TR'", ()->doParse(defaultNotation,"TR'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:6 an:1} } }")),
                dynamicTest("defaultNotation TU'", ()->doParse(defaultNotation,"TU'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:6 an:1} } }")),
                dynamicTest("defaultNotation TF'", ()->doParse(defaultNotation,"TF'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:6 an:1} } }")),
                dynamicTest("defaultNotation TL'", ()->doParse(defaultNotation,"TL'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:3 an:-1} } }")),
                dynamicTest("defaultNotation TD'", ()->doParse(defaultNotation,"TD'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:3 an:-1} } }")),
                dynamicTest("defaultNotation TB'", ()->doParse(defaultNotation,"TB'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:3 an:-1} } }")),
                dynamicTest("defaultNotation TR2", ()->doParse(defaultNotation,"TR2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:6 an:2} }")),
                dynamicTest("defaultNotation TU2", ()->doParse(defaultNotation,"TU2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:6 an:2} }")),
                dynamicTest("defaultNotation TF2", ()->doParse(defaultNotation,"TF2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:6 an:2} }")),
                dynamicTest("defaultNotation TL2", ()->doParse(defaultNotation,"TL2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:3 an:-2} }")),
                dynamicTest("defaultNotation TD2", ()->doParse(defaultNotation,"TD2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:3 an:-2} }")),
                dynamicTest("defaultNotation TB2", ()->doParse(defaultNotation,"TB2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:3 an:-2} }")),
                dynamicTest("defaultNotation CR", ()->doParse(defaultNotation,"CR", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:7 an:1} }")),
                dynamicTest("defaultNotation CU", ()->doParse(defaultNotation,"CU", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:7 an:1} }")),
                dynamicTest("defaultNotation CF", ()->doParse(defaultNotation,"CF", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:7 an:1} }")),
                dynamicTest("defaultNotation CL", ()->doParse(defaultNotation,"CL", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:7 an:-1} }")),
                dynamicTest("defaultNotation CD", ()->doParse(defaultNotation,"CD", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:7 an:-1} }")),
                dynamicTest("defaultNotation CB", ()->doParse(defaultNotation,"CB", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:7 an:-1} }")),
                dynamicTest("defaultNotation CR'", ()->doParse(defaultNotation,"CR'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:7 an:1} } }")),
                dynamicTest("defaultNotation CU'", ()->doParse(defaultNotation,"CU'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:7 an:1} } }")),
                dynamicTest("defaultNotation CF'", ()->doParse(defaultNotation,"CF'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:7 an:1} } }")),
                dynamicTest("defaultNotation CL'", ()->doParse(defaultNotation,"CL'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:7 an:-1} } }")),
                dynamicTest("defaultNotation CD'", ()->doParse(defaultNotation,"CD'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:7 an:-1} } }")),
                dynamicTest("defaultNotation CB'", ()->doParse(defaultNotation,"CB'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:7 an:-1} } }")),
                dynamicTest("defaultNotation CR2", ()->doParse(defaultNotation,"CR2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:7 an:2} }")),
                dynamicTest("defaultNotation CU2", ()->doParse(defaultNotation,"CU2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:7 an:2} }")),
                dynamicTest("defaultNotation CF2", ()->doParse(defaultNotation,"CF2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:7 an:2} }")),
                dynamicTest("defaultNotation CL2", ()->doParse(defaultNotation,"CL2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:7 an:-2} }")),
                dynamicTest("defaultNotation CD2", ()->doParse(defaultNotation,"CD2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:7 an:-2} }")),
                dynamicTest("defaultNotation CB2", ()->doParse(defaultNotation,"CB2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:7 an:-2} }")),
                dynamicTest("defaultNotation SR", ()->doParse(defaultNotation,"SR", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:5 an:1} }")),
                dynamicTest("defaultNotation SU", ()->doParse(defaultNotation,"SU", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:5 an:1} }")),
                dynamicTest("defaultNotation SF", ()->doParse(defaultNotation,"SF", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:5 an:1} }")),
                dynamicTest("defaultNotation SL", ()->doParse(defaultNotation,"SL", "0..2ScriptNode{ 0..2MoveNode{ax:0 lm:5 an:-1} }")),
                dynamicTest("defaultNotation SD", ()->doParse(defaultNotation,"SD", "0..2ScriptNode{ 0..2MoveNode{ax:1 lm:5 an:-1} }")),
                dynamicTest("defaultNotation SB", ()->doParse(defaultNotation,"SB", "0..2ScriptNode{ 0..2MoveNode{ax:2 lm:5 an:-1} }")),
                dynamicTest("defaultNotation SR'", ()->doParse(defaultNotation,"SR'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:5 an:1} } }")),
                dynamicTest("defaultNotation SU'", ()->doParse(defaultNotation,"SU'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:5 an:1} } }")),
                dynamicTest("defaultNotation SF'", ()->doParse(defaultNotation,"SF'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:5 an:1} } }")),
                dynamicTest("defaultNotation SL'", ()->doParse(defaultNotation,"SL'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:0 lm:5 an:-1} } }")),
                dynamicTest("defaultNotation SD'", ()->doParse(defaultNotation,"SD'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:1 lm:5 an:-1} } }")),
                dynamicTest("defaultNotation SB'", ()->doParse(defaultNotation,"SB'", "0..3ScriptNode{ 0..3InversionNode{ 0..2MoveNode{ax:2 lm:5 an:-1} } }")),
                dynamicTest("defaultNotation SR2", ()->doParse(defaultNotation,"SR2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:5 an:2} }")),
                dynamicTest("defaultNotation SU2", ()->doParse(defaultNotation,"SU2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:5 an:2} }")),
                dynamicTest("defaultNotation SF2", ()->doParse(defaultNotation,"SF2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:5 an:2} }")),
                dynamicTest("defaultNotation SL2", ()->doParse(defaultNotation,"SL2", "0..3ScriptNode{ 0..3MoveNode{ax:0 lm:5 an:-2} }")),
                dynamicTest("defaultNotation SD2", ()->doParse(defaultNotation,"SD2", "0..3ScriptNode{ 0..3MoveNode{ax:1 lm:5 an:-2} }")),
                dynamicTest("defaultNotation SB2", ()->doParse(defaultNotation,"SB2", "0..3ScriptNode{ 0..3MoveNode{ax:2 lm:5 an:-2} }")),
                dynamicTest("defaultNotation (R U F)", ()->doParse(defaultNotation,"(R U F)", "0..7ScriptNode{ 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } }")),
                dynamicTest("defaultNotation (R U F)'", ()->doParse(defaultNotation,"(R U F)'", "0..8ScriptNode{ 0..8InversionNode{ 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("defaultNotation (R)2", ()->doParse(defaultNotation,"(R)2", "0..4ScriptNode{ 0..4RepetitionNode{ 2, 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("defaultNotation R3", ()->doParse(defaultNotation,"R3", "0..2ScriptNode{ 0..2RepetitionNode{ 3, 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("defaultNotation (R U F)3", ()->doParse(defaultNotation,"(R U F)3", "0..8ScriptNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("defaultNotation (R U F)'3", ()->doParse(defaultNotation,"(R U F)'3", "0..9ScriptNode{ 0..9RepetitionNode{ 3, 0..8InversionNode{ 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("defaultNotation (R U F)3'", ()->doParse(defaultNotation,"(R U F)3'", "0..9ScriptNode{ 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("defaultNotation (R U F)3''", ()->doParse(defaultNotation,"(R U F)3''", "0..10ScriptNode{ 0..10InversionNode{ 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("defaultNotation (R U F)3'4", ()->doParse(defaultNotation,"(R U F)3'4", "0..10ScriptNode{ 0..10RepetitionNode{ 4, 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("defaultNotation (R)'", ()->doParse(defaultNotation,"(R)'", "0..4ScriptNode{ 0..4InversionNode{ 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("defaultNotation (R F)'", ()->doParse(defaultNotation,"(R F)'", "0..6ScriptNode{ 0..6InversionNode{ 0..5GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:2 lm:4 an:1} } } }")),
                t(defaultNotation, "(R- U F)- (R' U F)'",
                        "0..19ScriptNode{ 0..9InversionNode{ 0..8GroupingNode{ 1..3InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } 10..19InversionNode{ 10..18GroupingNode{ 11..13InversionNode{ 11..12MoveNode{ax:0 lm:4 an:1} } 14..15MoveNode{ax:1 lm:4 an:1} 16..17MoveNode{ax:2 lm:4 an:1} } } }"),
                dynamicTest("defaultNotation <CU>R", ()->doParse(defaultNotation,"<CU>R", "0..5ScriptNode{ 0..5ConjugationNode{ 1..3ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} }, 4..5MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("defaultNotation <CU CF>(R)", ()->doParse(defaultNotation,"<CU CF>(R)", "0..10ScriptNode{ 0..10ConjugationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..10GroupingNode{ 8..9MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("defaultNotation <CU CF>(R B)", ()->doParse(defaultNotation,"<CU CF>(R B)", "0..12ScriptNode{ 0..12ConjugationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..12GroupingNode{ 8..9MoveNode{ax:0 lm:4 an:1} 10..11MoveNode{ax:2 lm:1 an:-1} } } }")),
                dynamicTest("defaultNotation <R>U", ()->doParse(defaultNotation,"<R>U", "0..4ScriptNode{ 0..4ConjugationNode{ 1..2ScriptNode{ 1..2MoveNode{ax:0 lm:4 an:1} }, 3..4MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("defaultNotation [CU,R]", ()->doParse(defaultNotation,"[CU,R]", "0..6ScriptNode{ 0..6CommutationNode{ 1..3ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} }, 4..5MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("defaultNotation [CU CF,R]", ()->doParse(defaultNotation,"[CU CF,R]", "0..9ScriptNode{ 0..9CommutationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..8MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("defaultNotation [CU CF,R B]", ()->doParse(defaultNotation,"[CU CF,R B]", "0..11ScriptNode{ 0..11CommutationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..8MoveNode{ax:0 lm:4 an:1} 9..10MoveNode{ax:2 lm:1 an:-1} } }")),
                dynamicTest("defaultNotation [R,U]", ()->doParse(defaultNotation,"[R,U]", "0..5ScriptNode{ 0..5CommutationNode{ 1..2ScriptNode{ 1..2MoveNode{ax:0 lm:4 an:1} }, 3..4MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("defaultNotation (R)*", ()->doParse(defaultNotation,"(R)*", "0..4ScriptNode{ 0..4ReflectionNode{ 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("defaultNotation (R' U F)*", ()->doParse(defaultNotation,"(R' U F)*", "0..9ScriptNode{ 0..9ReflectionNode{ 0..8GroupingNode{ 1..3InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } }")),

                dynamicTest("prefixNotation 2(R)", ()->doParse(prefixNotation,"2(R)", "0..4ScriptNode{ 0..4RepetitionNode{ 2, 1..4GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("prefixNotation 3R", ()->doParse(prefixNotation,"3R", "0..2ScriptNode{ 0..2RepetitionNode{ 3, 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("prefixNotation 3(R U F)", ()->doParse(prefixNotation,"3(R U F)", "0..8ScriptNode{ 0..8RepetitionNode{ 3, 1..8GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("prefixNotation 3'(R U F)", ()->doParse(prefixNotation,"3'(R U F)", "0..9ScriptNode{ 0..9RepetitionNode{ 3, 1..9InversionNode{ 2..9GroupingNode{ 3..4MoveNode{ax:0 lm:4 an:1} 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("prefixNotation '3(R U F)", ()->doParse(prefixNotation,"'3(R U F)", "0..9ScriptNode{ 0..9InversionNode{ 1..9RepetitionNode{ 3, 2..9GroupingNode{ 3..4MoveNode{ax:0 lm:4 an:1} 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("prefixNotation ''3(R U F)", ()->doParse(prefixNotation,"''3(R U F)", "0..10ScriptNode{ 0..10InversionNode{ 1..10InversionNode{ 2..10RepetitionNode{ 3, 3..10GroupingNode{ 4..5MoveNode{ax:0 lm:4 an:1} 6..7MoveNode{ax:1 lm:4 an:1} 8..9MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("prefixNotation 4'3(R U F)", ()->doParse(prefixNotation,"4'3(R U F)", "0..10ScriptNode{ 0..10RepetitionNode{ 4, 1..10InversionNode{ 2..10RepetitionNode{ 3, 3..10GroupingNode{ 4..5MoveNode{ax:0 lm:4 an:1} 6..7MoveNode{ax:1 lm:4 an:1} 8..9MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("prefixNotation '(R)", ()->doParse(prefixNotation,"'(R)", "0..4ScriptNode{ 0..4InversionNode{ 1..4GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("prefixNotation '(R F)", ()->doParse(prefixNotation,"'(R F)", "0..6ScriptNode{ 0..6InversionNode{ 1..6GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} 4..5MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("prefixNotation -(-R U F) '('R U F)", ()->doParse(prefixNotation,"-(-R U F) '('R U F)", "0..19ScriptNode{ 0..9InversionNode{ 1..9GroupingNode{ 2..4InversionNode{ 3..4MoveNode{ax:0 lm:4 an:1} } 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } 10..19InversionNode{ 11..19GroupingNode{ 12..14InversionNode{ 13..14MoveNode{ax:0 lm:4 an:1} } 15..16MoveNode{ax:1 lm:4 an:1} 17..18MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("prefixNotation <CU>R", ()->doParse(prefixNotation,"<CU>R", "0..5ScriptNode{ 0..5ConjugationNode{ 1..3ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} }, 4..5MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("prefixNotation <CU CF>(R)", ()->doParse(prefixNotation,"<CU CF>(R)", "0..10ScriptNode{ 0..10ConjugationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..10GroupingNode{ 8..9MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("prefixNotation <CU CF>(R B)", ()->doParse(prefixNotation,"<CU CF>(R B)", "0..12ScriptNode{ 0..12ConjugationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..12GroupingNode{ 8..9MoveNode{ax:0 lm:4 an:1} 10..11MoveNode{ax:2 lm:1 an:-1} } } }")),
                dynamicTest("prefixNotation <R>U", ()->doParse(prefixNotation,"<R>U", "0..4ScriptNode{ 0..4ConjugationNode{ 1..2ScriptNode{ 1..2MoveNode{ax:0 lm:4 an:1} }, 3..4MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("prefixNotation [CU]R", ()->doParse(prefixNotation,"[CU]R", "0..5ScriptNode{ 0..5CommutationNode{ 1..3ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} }, 4..5MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("prefixNotation [CU CF]R", ()->doParse(prefixNotation,"[CU CF]R", "0..8ScriptNode{ 0..8CommutationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..8MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("prefixNotation [CU CF](R B)", ()->doParse(prefixNotation,"[CU CF](R B)", "0..12ScriptNode{ 0..12CommutationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..12GroupingNode{ 8..9MoveNode{ax:0 lm:4 an:1} 10..11MoveNode{ax:2 lm:1 an:-1} } } }")),
                dynamicTest("prefixNotation [R]U", ()->doParse(prefixNotation,"[R]U", "0..4ScriptNode{ 0..4CommutationNode{ 1..2ScriptNode{ 1..2MoveNode{ax:0 lm:4 an:1} }, 3..4MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("prefixNotation *(R)", ()->doParse(prefixNotation,"*(R)", "0..4ScriptNode{ 0..4ReflectionNode{ 1..4GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("prefixNotation *('R U F)", ()->doParse(prefixNotation,"*('R U F)", "0..9ScriptNode{ 0..9ReflectionNode{ 1..9GroupingNode{ 2..4InversionNode{ 3..4MoveNode{ax:0 lm:4 an:1} } 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } }")),

                dynamicTest("precircumfixNotation 2(R)", ()->doParse(precircumfixNotation,"2(R)", "0..4ScriptNode{ 0..4RepetitionNode{ 2, 1..4GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("precircumfixNotation 3R", ()->doParse(precircumfixNotation,"3R", "0..2ScriptNode{ 0..2RepetitionNode{ 3, 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("precircumfixNotation 3(R U F)", ()->doParse(precircumfixNotation,"3(R U F)", "0..8ScriptNode{ 0..8RepetitionNode{ 3, 1..8GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("precircumfixNotation 3'(R U F)", ()->doParse(precircumfixNotation,"3'(R U F)", "0..9ScriptNode{ 0..9RepetitionNode{ 3, 1..9InversionNode{ 2..9GroupingNode{ 3..4MoveNode{ax:0 lm:4 an:1} 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("precircumfixNotation '3(R U F)", ()->doParse(precircumfixNotation,"'3(R U F)", "0..9ScriptNode{ 0..9InversionNode{ 1..9RepetitionNode{ 3, 2..9GroupingNode{ 3..4MoveNode{ax:0 lm:4 an:1} 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("precircumfixNotation ''3(R U F)", ()->doParse(precircumfixNotation,"''3(R U F)", "0..10ScriptNode{ 0..10InversionNode{ 1..10InversionNode{ 2..10RepetitionNode{ 3, 3..10GroupingNode{ 4..5MoveNode{ax:0 lm:4 an:1} 6..7MoveNode{ax:1 lm:4 an:1} 8..9MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("precircumfixNotation 4'3(R U F)", ()->doParse(precircumfixNotation,"4'3(R U F)", "0..10ScriptNode{ 0..10RepetitionNode{ 4, 1..10InversionNode{ 2..10RepetitionNode{ 3, 3..10GroupingNode{ 4..5MoveNode{ax:0 lm:4 an:1} 6..7MoveNode{ax:1 lm:4 an:1} 8..9MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("precircumfixNotation '(R)", ()->doParse(precircumfixNotation,"'(R)", "0..4ScriptNode{ 0..4InversionNode{ 1..4GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("precircumfixNotation '(R F)", ()->doParse(precircumfixNotation,"'(R F)", "0..6ScriptNode{ 0..6InversionNode{ 1..6GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} 4..5MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("precircumfixNotation -(-R U F) '('R U F)", ()->doParse(precircumfixNotation,"-(-R U F) '('R U F)", "0..19ScriptNode{ 0..9InversionNode{ 1..9GroupingNode{ 2..4InversionNode{ 3..4MoveNode{ax:0 lm:4 an:1} } 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } 10..19InversionNode{ 11..19GroupingNode{ 12..14InversionNode{ 13..14MoveNode{ax:0 lm:4 an:1} } 15..16MoveNode{ax:1 lm:4 an:1} 17..18MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("precircumfixNotation <CU,R>", ()->doParse(precircumfixNotation,"<CU,R>", "0..6ScriptNode{ 0..6ConjugationNode{ 1..3ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} }, 4..5MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("precircumfixNotation <CU CF,R>", ()->doParse(precircumfixNotation,"<CU CF,R>", "0..9ScriptNode{ 0..9ConjugationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..8MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("precircumfixNotation <CU CF,R B>", ()->doParse(precircumfixNotation,"<CU CF,R B>", "0..11ScriptNode{ 0..11ConjugationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..8MoveNode{ax:0 lm:4 an:1} 9..10MoveNode{ax:2 lm:1 an:-1} } }")),
                dynamicTest("precircumfixNotation <R,U>", ()->doParse(precircumfixNotation,"<R,U>", "0..5ScriptNode{ 0..5ConjugationNode{ 1..2ScriptNode{ 1..2MoveNode{ax:0 lm:4 an:1} }, 3..4MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("precircumfixNotation [CU,R]", ()->doParse(precircumfixNotation,"[CU,R]", "0..6ScriptNode{ 0..6CommutationNode{ 1..3ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} }, 4..5MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("precircumfixNotation [CU CF,R]", ()->doParse(precircumfixNotation,"[CU CF,R]", "0..9ScriptNode{ 0..9CommutationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..8MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("precircumfixNotation [CU CF,R B]", ()->doParse(precircumfixNotation,"[CU CF,R B]", "0..11ScriptNode{ 0..11CommutationNode{ 1..6ScriptNode{ 1..3MoveNode{ax:1 lm:7 an:1} 4..6MoveNode{ax:2 lm:7 an:1} }, 7..8MoveNode{ax:0 lm:4 an:1} 9..10MoveNode{ax:2 lm:1 an:-1} } }")),
                dynamicTest("precircumfixNotation [R,U]", ()->doParse(precircumfixNotation,"[R,U]", "0..5ScriptNode{ 0..5CommutationNode{ 1..2ScriptNode{ 1..2MoveNode{ax:0 lm:4 an:1} }, 3..4MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("precircumfixNotation *(R)", ()->doParse(precircumfixNotation,"*(R)", "0..4ScriptNode{ 0..4ReflectionNode{ 1..4GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("precircumfixNotation *('R U F)", ()->doParse(precircumfixNotation,"*('R U F)", "0..9ScriptNode{ 0..9ReflectionNode{ 1..9GroupingNode{ 2..4InversionNode{ 3..4MoveNode{ax:0 lm:4 an:1} } 5..6MoveNode{ax:1 lm:4 an:1} 7..8MoveNode{ax:2 lm:4 an:1} } } }")),

                dynamicTest("suffixNotation (R)2", ()->doParse(suffixNotation,"(R)2", "0..4ScriptNode{ 0..4RepetitionNode{ 2, 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("suffixNotation R3", ()->doParse(suffixNotation,"R3", "0..2ScriptNode{ 0..2RepetitionNode{ 3, 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("suffixNotation (R U F)3", ()->doParse(suffixNotation,"(R U F)3", "0..8ScriptNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("suffixNotation (R U F)'3", ()->doParse(suffixNotation,"(R U F)'3", "0..9ScriptNode{ 0..9RepetitionNode{ 3, 0..8InversionNode{ 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("suffixNotation (R U F)3'", ()->doParse(suffixNotation,"(R U F)3'", "0..9ScriptNode{ 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("suffixNotation (R U F)3''", ()->doParse(suffixNotation,"(R U F)3''", "0..10ScriptNode{ 0..10InversionNode{ 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("suffixNotation (R U F)3'4", ()->doParse(suffixNotation,"(R U F)3'4", "0..10ScriptNode{ 0..10RepetitionNode{ 4, 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("suffixNotation (R)'", ()->doParse(suffixNotation,"(R)'", "0..4ScriptNode{ 0..4InversionNode{ 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("suffixNotation (R F)'", ()->doParse(suffixNotation,"(R F)'", "0..6ScriptNode{ 0..6InversionNode{ 0..5GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("suffixNotation (R- U F)- (R' U F)'", ()->doParse(suffixNotation,"(R- U F)- (R' U F)'", "0..19ScriptNode{ 0..9InversionNode{ 0..8GroupingNode{ 1..3InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } 10..19InversionNode{ 10..18GroupingNode{ 11..13InversionNode{ 11..12MoveNode{ax:0 lm:4 an:1} } 14..15MoveNode{ax:1 lm:4 an:1} 16..17MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("suffixNotation R<CU>", ()->doParse(suffixNotation,"R<CU>", "0..5ScriptNode{ 0..5ConjugationNode{ 2..4ScriptNode{ 2..4MoveNode{ax:1 lm:7 an:1} }, 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("suffixNotation (R)<CU CF>", ()->doParse(suffixNotation,"(R)<CU CF>", "0..10ScriptNode{ 0..10ConjugationNode{ 4..9ScriptNode{ 4..6MoveNode{ax:1 lm:7 an:1} 7..9MoveNode{ax:2 lm:7 an:1} }, 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("suffixNotation (R B)<CU CF>", ()->doParse(suffixNotation,"(R B)<CU CF>", "0..12ScriptNode{ 0..12ConjugationNode{ 6..11ScriptNode{ 6..8MoveNode{ax:1 lm:7 an:1} 9..11MoveNode{ax:2 lm:7 an:1} }, 0..5GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:2 lm:1 an:-1} } } }")),
                dynamicTest("suffixNotation U<R>", ()->doParse(suffixNotation,"U<R>", "0..4ScriptNode{ 0..4ConjugationNode{ 2..3ScriptNode{ 2..3MoveNode{ax:0 lm:4 an:1} }, 0..1MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("suffixNotation R[CU]", ()->doParse(suffixNotation,"R[CU]", "0..5ScriptNode{ 0..5CommutationNode{ 2..4ScriptNode{ 2..4MoveNode{ax:1 lm:7 an:1} }, 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("suffixNotation R[CU CF]", ()->doParse(suffixNotation,"R[CU CF]", "0..8ScriptNode{ 0..8CommutationNode{ 2..7ScriptNode{ 2..4MoveNode{ax:1 lm:7 an:1} 5..7MoveNode{ax:2 lm:7 an:1} }, 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("suffixNotation (R B)[CU CF]", ()->doParse(suffixNotation,"(R B)[CU CF]", "0..12ScriptNode{ 0..12CommutationNode{ 6..11ScriptNode{ 6..8MoveNode{ax:1 lm:7 an:1} 9..11MoveNode{ax:2 lm:7 an:1} }, 0..5GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:2 lm:1 an:-1} } } }")),
                dynamicTest("suffixNotation U[R]", ()->doParse(suffixNotation,"U[R]", "0..4ScriptNode{ 0..4CommutationNode{ 2..3ScriptNode{ 2..3MoveNode{ax:0 lm:4 an:1} }, 0..1MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("suffixNotation (R)*", ()->doParse(suffixNotation,"(R)*", "0..4ScriptNode{ 0..4ReflectionNode{ 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("suffixNotation (R' U F)*", ()->doParse(suffixNotation,"(R' U F)*", "0..9ScriptNode{ 0..9ReflectionNode{ 0..8GroupingNode{ 1..3InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } }")),

                dynamicTest("postcircumfixNotation (R)2", ()->doParse(postcircumfixNotation,"(R)2", "0..4ScriptNode{ 0..4RepetitionNode{ 2, 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("postcircumfixNotation R3", ()->doParse(postcircumfixNotation,"R3", "0..2ScriptNode{ 0..2RepetitionNode{ 3, 0..1MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("postcircumfixNotation (R U F)3", ()->doParse(postcircumfixNotation,"(R U F)3", "0..8ScriptNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("postcircumfixNotation (R U F)'3", ()->doParse(postcircumfixNotation,"(R U F)'3", "0..9ScriptNode{ 0..9RepetitionNode{ 3, 0..8InversionNode{ 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("postcircumfixNotation (R U F)3'", ()->doParse(postcircumfixNotation,"(R U F)3'", "0..9ScriptNode{ 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } }")),
                dynamicTest("postcircumfixNotation (R U F)3''", ()->doParse(postcircumfixNotation,"(R U F)3''", "0..10ScriptNode{ 0..10InversionNode{ 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("postcircumfixNotation (R U F)3'4", ()->doParse(postcircumfixNotation,"(R U F)3'4", "0..10ScriptNode{ 0..10RepetitionNode{ 4, 0..9InversionNode{ 0..8RepetitionNode{ 3, 0..7GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:1 lm:4 an:1} 5..6MoveNode{ax:2 lm:4 an:1} } } } } }")),
                dynamicTest("postcircumfixNotation (R)'", ()->doParse(postcircumfixNotation,"(R)'", "0..4ScriptNode{ 0..4InversionNode{ 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("postcircumfixNotation (R F)'", ()->doParse(postcircumfixNotation,"(R F)'", "0..6ScriptNode{ 0..6InversionNode{ 0..5GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("postcircumfixNotation (R- U F)- (R' U F)'", ()->doParse(postcircumfixNotation,"(R- U F)- (R' U F)'", "0..19ScriptNode{ 0..9InversionNode{ 0..8GroupingNode{ 1..3InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } 10..19InversionNode{ 10..18GroupingNode{ 11..13InversionNode{ 11..12MoveNode{ax:0 lm:4 an:1} } 14..15MoveNode{ax:1 lm:4 an:1} 16..17MoveNode{ax:2 lm:4 an:1} } } }")),
                dynamicTest("postcircumfixNotation <R,CU>", ()->doParse(postcircumfixNotation,"<R,CU>", "0..6ScriptNode{ 0..6ConjugationNode{ 3..5ScriptNode{ 3..5MoveNode{ax:1 lm:7 an:1} }, 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("postcircumfixNotation <(R),CU CF>", ()->doParse(postcircumfixNotation,"<(R),CU CF>", "0..11ScriptNode{ 0..11ConjugationNode{ 5..10ScriptNode{ 5..7MoveNode{ax:1 lm:7 an:1} 8..10MoveNode{ax:2 lm:7 an:1} }, 1..4GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("postcircumfixNotation <(R B),CU CF>", ()->doParse(postcircumfixNotation,"<(R B),CU CF>", "0..13ScriptNode{ 0..13ConjugationNode{ 7..12ScriptNode{ 7..9MoveNode{ax:1 lm:7 an:1} 10..12MoveNode{ax:2 lm:7 an:1} }, 1..6GroupingNode{ 2..3MoveNode{ax:0 lm:4 an:1} 4..5MoveNode{ax:2 lm:1 an:-1} } } }")),
                dynamicTest("postcircumfixNotation <U, R>", ()->doParse(postcircumfixNotation,"<U, R>", "0..6ScriptNode{ 0..6ConjugationNode{ 3..5ScriptNode{ 4..5MoveNode{ax:0 lm:4 an:1} }, 1..2MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("postcircumfixNotation [R,CU]", ()->doParse(postcircumfixNotation,"[R,CU]", "0..6ScriptNode{ 0..6CommutationNode{ 3..5ScriptNode{ 3..5MoveNode{ax:1 lm:7 an:1} }, 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("postcircumfixNotation [R,CU CF]", ()->doParse(postcircumfixNotation,"[R,CU CF]", "0..9ScriptNode{ 0..9CommutationNode{ 3..8ScriptNode{ 3..5MoveNode{ax:1 lm:7 an:1} 6..8MoveNode{ax:2 lm:7 an:1} }, 1..2MoveNode{ax:0 lm:4 an:1} } }")),
                dynamicTest("postcircumfixNotation [R B,CU CF]", ()->doParse(postcircumfixNotation,"[R B,CU CF]", "0..11ScriptNode{ 0..11CommutationNode{ 5..10ScriptNode{ 5..7MoveNode{ax:1 lm:7 an:1} 8..10MoveNode{ax:2 lm:7 an:1} }, 1..2MoveNode{ax:0 lm:4 an:1} 3..4MoveNode{ax:2 lm:1 an:-1} } }")),
                dynamicTest("postcircumfixNotation [U,R]", ()->doParse(postcircumfixNotation,"[U,R]", "0..5ScriptNode{ 0..5CommutationNode{ 3..4ScriptNode{ 3..4MoveNode{ax:0 lm:4 an:1} }, 1..2MoveNode{ax:1 lm:4 an:1} } }")),
                dynamicTest("postcircumfixNotation (R)*", ()->doParse(postcircumfixNotation,"(R)*", "0..4ScriptNode{ 0..4ReflectionNode{ 0..3GroupingNode{ 1..2MoveNode{ax:0 lm:4 an:1} } } }")),
                dynamicTest("postcircumfixNotation (R' U F)*", ()->doParse(postcircumfixNotation,"(R' U F)*", "0..9ScriptNode{ 0..9ReflectionNode{ 0..8GroupingNode{ 1..3InversionNode{ 1..2MoveNode{ax:0 lm:4 an:1} } 4..5MoveNode{ax:1 lm:4 an:1} 6..7MoveNode{ax:2 lm:4 an:1} } } }")),

                dynamicTest("defaultNotation (+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", ()->doParse(defaultNotation,"(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "0..61ScriptNode{ 0..18CornerPermutation{sign:2 0:0,2:2,3:0,1:2} 19..33EdgePermutation{sign:1 0:0,4:1,2:0,1:1} 34..38SidePermutation{sign:3 0:0} 39..44SidePermutation{sign:0 0:0,5:0} 45..52SidePermutation{sign:2 1:0,4:0} 53..61SidePermutation{sign:2 2:0,3:3} }")),
                dynamicTest("defaultNotation .", ()->doParse(defaultNotation,".", "0..1ScriptNode{ 0..1NOPNode{ } }")),
                dynamicTest("defaultNotation R . U · F", ()->doParse(defaultNotation,"R . U · F", "0..9ScriptNode{ 0..1MoveNode{ax:0 lm:4 an:1} 2..3NOPNode{ } 4..5MoveNode{ax:1 lm:4 an:1} 6..7NOPNode{ } 8..9MoveNode{ax:2 lm:4 an:1} }")),
                dynamicTest("defaultNotation ", ()->doParse(defaultNotation,"", "0..0ScriptNode{ }")),

                dynamicTest("defaultNotation R /*comment*/ U F", ()->doParse(defaultNotation,"R /*comment*/ U F", "0..17ScriptNode{ 0..1MoveNode{ax:0 lm:4 an:1} 14..15MoveNode{ax:1 lm:4 an:1} 16..17MoveNode{ax:2 lm:4 an:1} }"))
        );

    }
    

    private DynamicTest t(Notation notation, String script, String expected) {
        return dynamicTest(notation.getName()+" "+script,()->doParse(notation,script,expected));
    }
    
    
    /**
     * Test of parse method, of class ScriptParser.
     *
     * @param notation
     * @param script   the input script
     * @throws java.lang.Exception on failure
     */

    public void doParse(Notation notation, String script, String expected) throws Exception {
        ScriptParser0 instance = new ScriptParser0(notation);
        try {
            Node node = instance.parse(script);
            String actual = node.toString();
            if (html) {
                System.out.println("  <article>");
                System.out.println("    <section class=\"unittest\">");
                System.out.println("      <p class=\"input\">" + htmlEscape(script) + "</p>");
                System.out.println("      <p class=\"expected\">" +
                        htmlEscape(actual) + "</p>");
                System.out.println("      <p class=\"actual\">" + "</p>");
                System.out.println("    </section>");
                System.out.println("  </article>");
            } else {
                // System.out.println("  actual: " + actual);
                System.out.println(" DynamicTest.dynamicTest(\"" + notation.getName() + "\", () -> doParse(" + notation.getName() + ", \"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
            }
            // assertEquals(expected,actual);
        } catch(ParseException t) {
            System.out.println(" DynamicTest.dynamicTest(\"" + notation.getName() + "\", () -> doParse(" + notation.getName() + ", \"" + script + "\", \"" + expected.replaceAll("\n", "\\\\n") + "\")),");
        }
    }

    private String htmlEscape(String actual) {
        return actual.replaceAll("\n", "\\\\n")
                .replaceAll("<","&lt;")
                .replaceAll(">","&gt;");
    }
}