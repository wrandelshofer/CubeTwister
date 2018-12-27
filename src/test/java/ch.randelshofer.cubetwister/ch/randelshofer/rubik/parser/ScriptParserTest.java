/*
 * @(#)ScriptParser.java  10.0.1  2010-11-06
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ScriptParser with DefaultNotation.
 *
 * @author Wenrer Randelshofer
 */
public class ScriptParserTest {
    private static boolean html = true;

    public ScriptParserTest() {
    }

    /**
     * Test of parse method, of class ScriptParser.
     *
     * @param script the input script
     * @throws java.lang.Exception on failure
     */
    
    public void doParse(String script, String expected) throws Exception {
        if (!html) {
            System.out.println("testParse " + script);
        }
        ScriptParser instance = new ScriptParser(new DefaultNotation());
        SequenceNode node = instance.parse(script);
        String actual=node.toString();
        if (html) {
            System.out.println("  <article>");
            System.out.println("    <section class=\"unittest\">");
            System.out.println("      <p class=\"input\">" +  htmlEscape(script) + "</p>");
            System.out.println("      <p class=\"expected\">" +
                    htmlEscape(actual) + "</p>");
            System.out.println("      <p class=\"actual\">" + "</p>");
            System.out.println("    </section>");
            System.out.println("  </article>");
        } else {
            //System.out.println("  actual: " + actual);
            //System.out.println(" DynamicTest.dynamicTest(\"1\", () -> doApply(\"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
        }
        assertTrue(node instanceof SequenceNode);
    }

    private String htmlEscape(String actual) {
        return actual.replaceAll("\n", "\\\\n")
                .replaceAll("<","&lt;")
                .replaceAll(">","&gt;");
    }



    @TestFactory
    public List<DynamicTest> testParse() {
        return Arrays.asList(
                DynamicTest.dynamicTest("1", () -> doParse("R", "(ubr,bdr,dfr,fur)\n(ur,br,dr,fr)\n(+r)")),
                DynamicTest.dynamicTest("1", () -> doParse("U", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub)\n(+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("F", "(dfr,lfd,ufl,rfu)\n(rf,df,lf,uf)\n(+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("L", "(ufl,fdl,dbl,bul)\n(ul,fl,dl,bl)\n(+l)")),
                DynamicTest.dynamicTest("1", () -> doParse("D", "(drb,dbl,dlf,dfr)\n(dr,db,dl,df)\n(+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("B", "(ulb,ldb,drb,rub)\n(bu,bl,bd,br)\n(+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("R'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br)\n(-r)")),
                DynamicTest.dynamicTest("1", () -> doParse("U'", "(ubr,ulb,ufl,urf)\n(ur,ub,ul,uf)\n(-u)")),
                DynamicTest.dynamicTest("1", () -> doParse("F'", "(ufl,lfd,dfr,rfu)\n(rf,uf,lf,df)\n(-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("L'", "(dbl,fdl,ufl,bul)\n(ul,bl,dl,fl)\n(-l)")),
                DynamicTest.dynamicTest("1", () -> doParse("D'", "(dlf,dbl,drb,dfr)\n(dr,df,dl,db)\n(-d)")),
                DynamicTest.dynamicTest("1", () -> doParse("B'", "(drb,ldb,ulb,rub)\n(bu,br,bd,bl)\n(-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("R2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb)\n(++r)")),
                DynamicTest.dynamicTest("1", () -> doParse("U2", "(ulb,urf) (ufl,ubr)\n(ur,ul) (bu,fu)\n(++u)")),
                DynamicTest.dynamicTest("1", () -> doParse("F2", "(dlf,urf) (ufl,dfr)\n(rf,lf) (fu,fd)\n(++f)")),
                DynamicTest.dynamicTest("1", () -> doParse("L2", "(dlf,ulb) (ufl,dbl)\n(ul,dl) (lb,lf)\n(++l)")),
                DynamicTest.dynamicTest("1", () -> doParse("D2", "(dbl,dfr) (dlf,drb)\n(dr,dl) (bd,fd)\n(++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("B2", "(dbl,ubr) (ulb,drb)\n(bu,bd) (rb,lb)\n(++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MR", "(bu,db,fd,uf)\n(u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("MU", "(rf,fl,lb,br)\n(r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MF", "(ur,rd,dl,lu)\n(r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("ML", "(bu,uf,fd,db)\n(u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MD", "(rf,br,lb,fl)\n(r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("MB", "(ur,lu,dl,rd)\n(r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("MR'", "(bu,uf,fd,db)\n(u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MU'", "(rf,br,lb,fl)\n(r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("MF'", "(ur,lu,dl,rd)\n(r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("ML'", "(bu,db,fd,uf)\n(u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("MD'", "(rf,fl,lb,br)\n(r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MB'", "(ur,rd,dl,lu)\n(r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("MR2", "(bu,fd) (bd,fu)\n(u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MU2", "(rf,lb) (rb,lf)\n(r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MF2", "(ur,dl) (dr,ul)\n(r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("ML2", "(bu,fd) (bd,fu)\n(u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MD2", "(rf,lb) (rb,lf)\n(r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("MB2", "(ur,dl) (dr,ul)\n(r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("TR", "(ubr,bdr,dfr,fur)\n(ur,br,dr,fr) (bu,db,fd,uf)\n(+r) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("TU", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub) (rf,fl,lb,br)\n(+u) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TF", "(dfr,lfd,ufl,rfu)\n(ur,rd,dl,lu) (rf,df,lf,uf)\n(+f) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("TL", "(ufl,fdl,dbl,bul)\n(bu,uf,fd,db) (ul,fl,dl,bl)\n(+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TD", "(drb,dbl,dlf,dfr)\n(rf,br,lb,fl) (dr,db,dl,df)\n(+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("TB", "(ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (bu,bl,bd,br)\n(+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("TR'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br) (bu,uf,fd,db)\n(-r) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TU'", "(ubr,ulb,ufl,urf)\n(ur,ub,ul,uf) (rf,br,lb,fl)\n(-u) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("TF'", "(ufl,lfd,dfr,rfu)\n(ur,lu,dl,rd) (rf,uf,lf,df)\n(-f) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("TL'", "(dbl,fdl,ufl,bul)\n(bu,db,fd,uf) (ul,bl,dl,fl)\n(-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("TD'", "(dlf,dbl,drb,dfr)\n(rf,fl,lb,br) (dr,df,dl,db)\n(-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TB'", "(drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (bu,br,bd,bl)\n(-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("TR2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu)\n(++r) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TU2", "(ulb,urf) (ufl,ubr)\n(ur,ul) (rf,lb) (bu,fu) (rb,lf)\n(++u) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TF2", "(dlf,urf) (ufl,dfr)\n(ur,dl) (rf,lf) (dr,ul) (fu,fd)\n(++f) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("TL2", "(dlf,ulb) (ufl,dbl)\n(bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TD2", "(dbl,dfr) (dlf,drb)\n(rf,lb) (dr,dl) (rb,lf) (bd,fd)\n(++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("TB2", "(dbl,ubr) (ulb,drb)\n(ur,dl) (dr,ul) (bu,bd) (rb,lb)\n(++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("CR", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (bu,db,fd,uf) (ul,bl,dl,fl)\n(+r) (-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("CU", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (rf,fl,lb,br) (dr,df,dl,db)\n(+u) (-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CF", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("CL", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (bu,uf,fd,db) (ul,fl,dl,bl)\n(-r) (+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CD", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (rf,br,lb,fl) (dr,db,dl,df)\n(-u) (+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("CB", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("CR'", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (bu,uf,fd,db) (ul,fl,dl,bl)\n(-r) (+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CU'", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (rf,br,lb,fl) (dr,db,dl,df)\n(-u) (+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("CF'", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("CL'", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (bu,db,fd,uf) (ul,bl,dl,fl)\n(+r) (-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("CD'", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (rf,fl,lb,br) (dr,df,dl,db)\n(+u) (-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CB'", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("CR2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++r) (++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CU2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (rf,lb) (dr,dl) (bu,fu) (rb,lf) (bd,fd)\n(++u) (++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CF2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(ur,dl) (rf,lf) (dr,ul) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("CL2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++r) (++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CD2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (rf,lb) (dr,dl) (bu,fu) (rb,lf) (bd,fd)\n(++u) (++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("CB2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(ur,dl) (rf,lf) (dr,ul) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("SR", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (ul,bl,dl,fl)\n(+r) (-l)")),
                DynamicTest.dynamicTest("1", () -> doParse("SU", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (dr,df,dl,db)\n(+u) (-d)")),
                DynamicTest.dynamicTest("1", () -> doParse("SF", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("SL", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (ul,fl,dl,bl)\n(-r) (+l)")),
                DynamicTest.dynamicTest("1", () -> doParse("SD", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (dr,db,dl,df)\n(-u) (+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("SB", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("SR'", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (ul,fl,dl,bl)\n(-r) (+l)")),
                DynamicTest.dynamicTest("1", () -> doParse("SU'", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (dr,db,dl,df)\n(-u) (+d)")),
                DynamicTest.dynamicTest("1", () -> doParse("SF'", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("SL'", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (ul,bl,dl,fl)\n(+r) (-l)")),
                DynamicTest.dynamicTest("1", () -> doParse("SD'", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (dr,df,dl,db)\n(+u) (-d)")),
                DynamicTest.dynamicTest("1", () -> doParse("SB'", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("SR2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (ul,dl) (lb,lf)\n(++r) (++l)")),
                DynamicTest.dynamicTest("1", () -> doParse("SU2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (dr,dl) (bu,fu) (bd,fd)\n(++u) (++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("SF2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(rf,lf) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("SL2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (ul,dl) (lb,lf)\n(++r) (++l)")),
                DynamicTest.dynamicTest("1", () -> doParse("SD2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (dr,dl) (bu,fu) (bd,fd)\n(++u) (++d)")),
                DynamicTest.dynamicTest("1", () -> doParse("SB2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(rf,lf) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)", "(dfr,urf) (drb,fdl,flu,lbu,bru)\n(+ur,br,dr,fd,fl,fu,lu,bu) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)'", "(dfr,urf) (ulb,ufl,lfd,bdr,ubr)\n(+ur,ub,ul,uf,lf,df,rd,rb) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R)2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb)\n(++r)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3", "(dfr,urf) (ufl,bdr,ulb,lfd,ubr)\n(+ur,fd,lu,rb,lf,ub,dr,fu) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)'3", "(dfr,urf) (dlf,bul,rbd,luf,rub)\n(+ur,uf,rd,bu,fl,br,ul,df) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3'", "(dfr,urf) (dlf,bul,rbd,luf,rub)\n(+ur,uf,rd,bu,fl,br,ul,df) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3''", "(dfr,urf) (ufl,bdr,ulb,lfd,ubr)\n(+ur,fd,lu,rb,lf,ub,dr,fu) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R U F)3'4", "(ufl,bdr,ulb,lfd,ubr)\n(+ur,fl) (+dr,lu) (+bu,df) (+rb,uf)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R)'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br)\n(-r)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R F)'", "(+ufl,lfd,bdr,ubr,fur) (-dfr)\n(ur,fr,fu,fl,fd,dr,br)\n(-r) (-f)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R- U F)- (R' U F)'", "(+ulb,lfd,rub,luf,rfu) (+dfr) (+drb)\n(+ur,ul,lf) (rf,dr,rb) (+bu,fu,fd)\n(++r) (++u) (++f)")),
                DynamicTest.dynamicTest("1", () -> doParse("<CU>R", "(ulb,ldb,drb,rub)\n(bu,bl,bd,br)\n(+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("<CU CF>(R)", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub)\n(+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("<CU CF>(R B)", "(-dlf,bld,ulb,ubr,urf) (+ufl)\n(ur,uf,fl,dl,bl,ul,ub)\n(+u) (+l)")),
                DynamicTest.dynamicTest("1", () -> doParse("<R>U", "(dfr,luf,bul,rfu)\n(rf,fu,lu,bu)\n(+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("[CU,R]", "(+dfr,bdr,urf) (-ulb,ldb,bru)\n(ur,fr,dr,br,bu,bl,bd)\n(-r) (+b)")),
                DynamicTest.dynamicTest("1", () -> doParse("[CU CF,R]", "(-ufl,ulb,fur) (+drb,bru,rdf)\n(ur,uf,ul,ub,fr,dr,br)\n(-r) (+u)")),
                DynamicTest.dynamicTest("1", () -> doParse("[CU CF,R B]", "(-dlf,bul,bru,rdf,rbd,dbl,urf) (+ufl)\n(ur,uf,fl,dl,bu,rf,rd,rb,db,lb,lu)\n(-r) (+u) (+l) (-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("[R,U]", "(+dfr,fur) (-ulb,rub)\n(ur,ub,fr)")),
                DynamicTest.dynamicTest("1", () -> doParse("(R' U F)*", "(-dlf,dbl,bru,bdr,rdf) (+ufl,ulb)\n(+dr,df,dl,bu,br,bd) (+ul,fl,lb)\n(+l) (-d) (-b)")),
                DynamicTest.dynamicTest("1", () -> doParse("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "(+ubr,bdr,dfr,urf)\n(+ur,br,dr,fr)\n(+r,+b) (++u,d) (++f,+l)")),
                DynamicTest.dynamicTest("1", () -> doParse(".", "()")),
                DynamicTest.dynamicTest("1", () -> doParse("R . U · F", "(dfr,urf) (drb,fdl,flu,lbu,bru)\n(+ur,br,dr,fd,fl,fu,lu,bu) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("1", () -> doParse("", "()"))
        );
    }
}
