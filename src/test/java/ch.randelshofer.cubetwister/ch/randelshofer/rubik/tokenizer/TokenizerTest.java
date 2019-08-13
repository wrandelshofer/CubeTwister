/* @(#)TokenizerTest.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import ch.randelshofer.rubik.notation.DefaultNotation;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TokenizerTest {

    @TestFactory
    public List<DynamicTest> testTokenizerWithDefaultNotation() {
        DefaultNotation defaultNotation = new DefaultNotation();
        Tokenizer tt=new Tokenizer();
        tt.skipWhitespace();
        for (String token : defaultNotation.getTokens()) {
            tt.addKeyword(token);
        }

        return Arrays.asList(
                dynamicTest("1",()->doTokenizer(tt,"<CU CF>'(R)","0..1:KEY:<, 1..3:KEY:CU, 4..6:KEY:CF, 6..8:KEY:>', 8..9:KEY:(, 9..10:KEY:R, 10..11:KEY:)"))
        );

    }
    @TestFactory
    public List<DynamicTest> testTokenizer() {
        Tokenizer ttNone=new Tokenizer();
        Tokenizer ttWhitespace=new Tokenizer();
        ttWhitespace.skipWhitespace();
        Tokenizer ttWhitespaceNumber=new Tokenizer();
        ttWhitespaceNumber.skipWhitespace();
        ttWhitespaceNumber.addNumbers();
        Tokenizer ttWhitespaceNumberKey=new Tokenizer();
        ttWhitespaceNumberKey.skipWhitespace();
        ttWhitespaceNumberKey.addNumbers();
        ttWhitespaceNumberKey.addKeyword("tom");
        ttWhitespaceNumberKey.addKeyword("tomato");
        ttWhitespaceNumberKey.addKeyword("two2");
        ttWhitespaceNumberKey.addKeyword("3three");
        return Arrays.asList(
                dynamicTest("1",()->doTokenizer(ttWhitespace,"lorem ipsum","0..5:WORD:lorem, 6..11:WORD:ipsum")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumber,"1 2","0..1:NUM:1, 2..3:NUM:2")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumber,"1lorem 2ipsum","0..1:NUM:1, 1..6:WORD:lorem, 7..8:NUM:2, 8..13:WORD:ipsum")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumber,"lorem1 ipsum2","0..5:WORD:lorem, 5..6:NUM:1, 7..12:WORD:ipsum, 12..13:NUM:2")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumber,"16 21","0..2:NUM:16, 3..5:NUM:21")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumber,"-16","0..1:WORD:-, 1..3:NUM:16")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"tom","0..3:KEY:tom")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"toma","0..3:KEY:tom, 3..4:WORD:a")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"tomato","0..6:KEY:tomato")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"tomatoto","0..6:KEY:tomato, 6..8:WORD:to")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"tomatotom","0..6:KEY:tomato, 6..9:KEY:tom")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"to14matotom","0..2:WORD:to, 2..4:NUM:14, 4..11:WORD:matotom")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"tomato tom","0..6:KEY:tomato, 7..10:KEY:tom")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"tom ato tom","0..3:KEY:tom, 4..7:WORD:ato, 8..11:KEY:tom")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"two2 3three","0..4:KEY:two2, 5..11:KEY:3three")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"two24 63three","0..4:KEY:two2, 4..5:NUM:4, 6..8:NUM:63, 8..13:WORD:three")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"two24 6 3three","0..4:KEY:two2, 4..5:NUM:4, 6..7:NUM:6, 8..14:KEY:3three")),
                dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"two24 63thre","0..4:KEY:two2, 4..5:NUM:4, 6..8:NUM:63, 8..12:WORD:thre")),
               // dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"lorem/* comment */ipsum","0..4:KEY:two2, 4..5:NUM:4, 6..8:NUM:63, 8..12:WORD:thre")),
               // dynamicTest("1",()->doTokenizer(ttWhitespaceNumberKey,"lorem// comment\nipsum","0..4:KEY:two2, 4..5:NUM:4, 6..8:NUM:63, 8..12:WORD:thre")),
                dynamicTest("1",()->doTokenizer(ttNone,"lorem ipsum","0..11:WORD:lorem ipsum"))
        );
    }

    private void doTokenizer(Tokenizer instance, String input, String expected) {
        doNextToken(instance,input,expected);
        doPushBack(instance,input,expected);
    }
    private void doNextToken(Tokenizer instance, String input, String expected) {
        instance.setInput(input);
        StringBuffer buf=new StringBuffer();
        while (instance.nextToken()!=Tokenizer.TT_EOF) {
            if (buf.length()!=0)buf.append(", ");
            buf.append(instance.getStartPosition());
            buf.append("..");
            buf.append(instance.getEndPosition());
            buf.append(':');
            switch (instance.getTokenType()) {
                case Tokenizer.TT_NUMBER:
                    buf.append("NUM");
                    buf.append(':');
                    buf.append(instance.getNumericValue());
                    break;
                    case Tokenizer.TT_KEYWORD:
                        buf.append("KEY");
                        buf.append(':');
                        buf.append(instance.getStringValue());
                        break;
                case Tokenizer.TT_WORD:
                    buf.append("WORD");
                    buf.append(':');
                    buf.append(instance.getStringValue());
                    break;
                default:
                    throw new AssertionError("Unexpected token type: "+instance.getTokenType());
            }
        }
        String actual=buf.toString();
        //System.out.println("dynamicTest(\"1\",()->doTokenizer(ttNone,\""+input+"\",\""+actual+"\"))");
        assertEquals(expected,actual);
    }
    private void doPushBack(Tokenizer instance, String input, String expected) {
        instance.setInput(input);
        StringBuffer buf=new StringBuffer();
        while (instance.nextToken()!=Tokenizer.TT_EOF) {
            instance.pushBack();
            instance.nextToken();
            if (buf.length()!=0)buf.append(", ");
            buf.append(instance.getStartPosition());
            buf.append("..");
            buf.append(instance.getEndPosition());
            buf.append(':');
            switch (instance.getTokenType()) {
                case Tokenizer.TT_NUMBER:
                    buf.append("NUM");
                    buf.append(':');
                    buf.append(instance.getNumericValue());
                    break;
                case Tokenizer.TT_KEYWORD:
                    buf.append("KEY");
                    buf.append(':');
                    buf.append(instance.getStringValue());
                    break;
                case Tokenizer.TT_WORD:
                    buf.append("WORD");
                    buf.append(':');
                    buf.append(instance.getStringValue());
                    break;
                default:
                    throw new AssertionError("Unexpected token type: "+instance.getTokenType());
            }
        }
        String actual=buf.toString();
        assertEquals(expected,actual);
    }
}