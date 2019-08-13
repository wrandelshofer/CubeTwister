package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cubes;
import ch.randelshofer.rubik.RubiksCube;
import ch.randelshofer.rubik.notation.DefaultNotation;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    @TestFactory
    public List<DynamicTest> testApply() {
        return Arrays.asList(
                DynamicTest.dynamicTest("1", () -> doApply("R", "(ubr,bdr,dfr,fur)\n(ur,br,dr,fr)\n(+r)")),
                DynamicTest.dynamicTest("1", () -> doApply("U", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub)\n(+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("F", "(dfr,lfd,ufl,rfu)\n(rf,df,lf,uf)\n(+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("L", "(ufl,fdl,dbl,bul)\n(ul,fl,dl,bl)\n(+l)")),
                DynamicTest.dynamicTest("1", () -> doApply("D", "(drb,dbl,dlf,dfr)\n(dr,db,dl,df)\n(+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("B", "(ulb,ldb,drb,rub)\n(bu,bl,bd,br)\n(+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("R'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br)\n(-r)")),
                DynamicTest.dynamicTest("1", () -> doApply("U'", "(ubr,ulb,ufl,urf)\n(ur,ub,ul,uf)\n(-u)")),
                DynamicTest.dynamicTest("1", () -> doApply("F'", "(ufl,lfd,dfr,rfu)\n(rf,uf,lf,df)\n(-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("L'", "(dbl,fdl,ufl,bul)\n(ul,bl,dl,fl)\n(-l)")),
                DynamicTest.dynamicTest("1", () -> doApply("D'", "(dlf,dbl,drb,dfr)\n(dr,df,dl,db)\n(-d)")),
                DynamicTest.dynamicTest("1", () -> doApply("B'", "(drb,ldb,ulb,rub)\n(bu,br,bd,bl)\n(-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("R2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb)\n(++r)")),
                DynamicTest.dynamicTest("1", () -> doApply("U2", "(ulb,urf) (ufl,ubr)\n(ur,ul) (bu,fu)\n(++u)")),
                DynamicTest.dynamicTest("1", () -> doApply("F2", "(dlf,urf) (ufl,dfr)\n(rf,lf) (fu,fd)\n(++f)")),
                DynamicTest.dynamicTest("1", () -> doApply("L2", "(dlf,ulb) (ufl,dbl)\n(ul,dl) (lb,lf)\n(++l)")),
                DynamicTest.dynamicTest("1", () -> doApply("D2", "(dbl,dfr) (dlf,drb)\n(dr,dl) (bd,fd)\n(++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("B2", "(dbl,ubr) (ulb,drb)\n(bu,bd) (rb,lb)\n(++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MR", "(bu,db,fd,uf)\n(u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("MU", "(rf,fl,lb,br)\n(r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MF", "(ur,rd,dl,lu)\n(r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("ML", "(bu,uf,fd,db)\n(u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MD", "(rf,br,lb,fl)\n(r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("MB", "(ur,lu,dl,rd)\n(r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("MR'", "(bu,uf,fd,db)\n(u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MU'", "(rf,br,lb,fl)\n(r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("MF'", "(ur,lu,dl,rd)\n(r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("ML'", "(bu,db,fd,uf)\n(u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("MD'", "(rf,fl,lb,br)\n(r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MB'", "(ur,rd,dl,lu)\n(r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("MR2", "(bu,fd) (bd,fu)\n(u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MU2", "(rf,lb) (rb,lf)\n(r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MF2", "(ur,dl) (dr,ul)\n(r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("ML2", "(bu,fd) (bd,fu)\n(u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MD2", "(rf,lb) (rb,lf)\n(r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("MB2", "(ur,dl) (dr,ul)\n(r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("TR", "(ubr,bdr,dfr,fur)\n(ur,br,dr,fr) (bu,db,fd,uf)\n(+r) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("TU", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub) (rf,fl,lb,br)\n(+u) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TF", "(dfr,lfd,ufl,rfu)\n(ur,rd,dl,lu) (rf,df,lf,uf)\n(+f) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("TL", "(ufl,fdl,dbl,bul)\n(bu,uf,fd,db) (ul,fl,dl,bl)\n(+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TD", "(drb,dbl,dlf,dfr)\n(rf,br,lb,fl) (dr,db,dl,df)\n(+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("TB", "(ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (bu,bl,bd,br)\n(+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("TR'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br) (bu,uf,fd,db)\n(-r) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TU'", "(ubr,ulb,ufl,urf)\n(ur,ub,ul,uf) (rf,br,lb,fl)\n(-u) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("TF'", "(ufl,lfd,dfr,rfu)\n(ur,lu,dl,rd) (rf,uf,lf,df)\n(-f) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("TL'", "(dbl,fdl,ufl,bul)\n(bu,db,fd,uf) (ul,bl,dl,fl)\n(-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("TD'", "(dlf,dbl,drb,dfr)\n(rf,fl,lb,br) (dr,df,dl,db)\n(-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TB'", "(drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (bu,br,bd,bl)\n(-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("TR2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu)\n(++r) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TU2", "(ulb,urf) (ufl,ubr)\n(ur,ul) (rf,lb) (bu,fu) (rb,lf)\n(++u) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TF2", "(dlf,urf) (ufl,dfr)\n(ur,dl) (rf,lf) (dr,ul) (fu,fd)\n(++f) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("TL2", "(dlf,ulb) (ufl,dbl)\n(bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TD2", "(dbl,dfr) (dlf,drb)\n(rf,lb) (dr,dl) (rb,lf) (bd,fd)\n(++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("TB2", "(dbl,ubr) (ulb,drb)\n(ur,dl) (dr,ul) (bu,bd) (rb,lb)\n(++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("CR", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (bu,db,fd,uf) (ul,bl,dl,fl)\n(+r) (-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("CU", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (rf,fl,lb,br) (dr,df,dl,db)\n(+u) (-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CF", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("CL", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (bu,uf,fd,db) (ul,fl,dl,bl)\n(-r) (+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CD", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (rf,br,lb,fl) (dr,db,dl,df)\n(-u) (+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("CB", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("CR'", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (bu,uf,fd,db) (ul,fl,dl,bl)\n(-r) (+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CU'", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (rf,br,lb,fl) (dr,db,dl,df)\n(-u) (+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("CF'", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("CL'", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (bu,db,fd,uf) (ul,bl,dl,fl)\n(+r) (-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("CD'", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (rf,fl,lb,br) (dr,df,dl,db)\n(+u) (-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CB'", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("CR2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++r) (++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CU2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (rf,lb) (dr,dl) (bu,fu) (rb,lf) (bd,fd)\n(++u) (++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CF2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(ur,dl) (rf,lf) (dr,ul) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("CL2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++r) (++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CD2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (rf,lb) (dr,dl) (bu,fu) (rb,lf) (bd,fd)\n(++u) (++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("CB2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(ur,dl) (rf,lf) (dr,ul) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("SR", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (ul,bl,dl,fl)\n(+r) (-l)")),
                DynamicTest.dynamicTest("1", () -> doApply("SU", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (dr,df,dl,db)\n(+u) (-d)")),
                DynamicTest.dynamicTest("1", () -> doApply("SF", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("SL", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (ul,fl,dl,bl)\n(-r) (+l)")),
                DynamicTest.dynamicTest("1", () -> doApply("SD", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (dr,db,dl,df)\n(-u) (+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("SB", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("SR'", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (ul,fl,dl,bl)\n(-r) (+l)")),
                DynamicTest.dynamicTest("1", () -> doApply("SU'", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (dr,db,dl,df)\n(-u) (+d)")),
                DynamicTest.dynamicTest("1", () -> doApply("SF'", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("SL'", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (ul,bl,dl,fl)\n(+r) (-l)")),
                DynamicTest.dynamicTest("1", () -> doApply("SD'", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (dr,df,dl,db)\n(+u) (-d)")),
                DynamicTest.dynamicTest("1", () -> doApply("SB'", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("SR2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (ul,dl) (lb,lf)\n(++r) (++l)")),
                DynamicTest.dynamicTest("1", () -> doApply("SU2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (dr,dl) (bu,fu) (bd,fd)\n(++u) (++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("SF2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(rf,lf) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("SL2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (ul,dl) (lb,lf)\n(++r) (++l)")),
                DynamicTest.dynamicTest("1", () -> doApply("SD2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (dr,dl) (bu,fu) (bd,fd)\n(++u) (++d)")),
                DynamicTest.dynamicTest("1", () -> doApply("SB2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(rf,lf) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R U F)", "(dfr,urf) (drb,fdl,flu,lbu,bru)\n(+ur,br,dr,fd,fl,fu,lu,bu) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R U F)'", "(dfr,urf) (ulb,ufl,lfd,bdr,ubr)\n(+ur,ub,ul,uf,lf,df,rd,rb) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R)2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb)\n(++r)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R U F)3", "(dfr,urf) (ufl,bdr,ulb,lfd,ubr)\n(+ur,fd,lu,rb,lf,ub,dr,fu) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R U F)'3", "(dfr,urf) (dlf,bul,rbd,luf,rub)\n(+ur,uf,rd,bu,fl,br,ul,df) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("(R U F)3'", () -> doApply("(R U F)3'", "(dfr,urf) (dlf,bul,rbd,luf,rub)\n(+ur,uf,rd,bu,fl,br,ul,df) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R U F)3''", "(dfr,urf) (ufl,bdr,ulb,lfd,ubr)\n(+ur,fd,lu,rb,lf,ub,dr,fu) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R U F)3'4", "(ufl,bdr,ulb,lfd,ubr)\n(+ur,fl) (+dr,lu) (+bu,df) (+rb,uf)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R)'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br)\n(-r)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R F)'", "(+ufl,lfd,bdr,ubr,fur) (-dfr)\n(ur,fr,fu,fl,fd,dr,br)\n(-r) (-f)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R- U F)- (R' U F)'", "(+ulb,lfd,rub,luf,rfu) (+dfr) (+drb)\n(+ur,ul,lf) (rf,dr,rb) (+bu,fu,fd)\n(++r) (++u) (++f)")),
                DynamicTest.dynamicTest("1", () -> doApply("<CU>R", "(ulb,ldb,drb,rub)\n(bu,bl,bd,br)\n(+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("<CU CF>(R)", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub)\n(+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("<CU CF>(R B)", "(-dlf,bld,ulb,ubr,urf) (+ufl)\n(ur,uf,fl,dl,bl,ul,ub)\n(+u) (+l)")),
                DynamicTest.dynamicTest("1", () -> doApply("<R>U", "(dfr,luf,bul,rfu)\n(rf,fu,lu,bu)\n(+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("[CU,R]", "(+dfr,bdr,urf) (-ulb,ldb,bru)\n(ur,fr,dr,br,bu,bl,bd)\n(-r) (+b)")),
                DynamicTest.dynamicTest("1", () -> doApply("[CU CF,R]", "(-ufl,ulb,fur) (+drb,bru,rdf)\n(ur,uf,ul,ub,fr,dr,br)\n(-r) (+u)")),
                DynamicTest.dynamicTest("1", () -> doApply("[CU CF,R B]", "(-dlf,bul,bru,rdf,rbd,dbl,urf) (+ufl)\n(ur,uf,fl,dl,bu,rf,rd,rb,db,lb,lu)\n(-r) (+u) (+l) (-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("[R,U]", "(+dfr,fur) (-ulb,rub)\n(ur,ub,fr)")),
                DynamicTest.dynamicTest("1", () -> doApply("(R' U F)*", "(-dlf,dbl,bru,bdr,rdf) (+ufl,ulb)\n(+dr,df,dl,bu,br,bd) (+ul,fl,lb)\n(+l) (-d) (-b)")),
                DynamicTest.dynamicTest("1", () -> doApply("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "(+ubr,bdr,dfr,urf)\n(+ur,br,dr,fr)\n(+r,+b) (++u,d) (++f,+l)")),
                DynamicTest.dynamicTest("1", () -> doApply(".", "()")),
                DynamicTest.dynamicTest("1", () -> doApply("R . U · F", "(dfr,urf) (drb,fdl,flu,lbu,bru)\n(+ur,br,dr,fd,fl,fu,lu,bu) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("1", () -> doApply("", "()"))
        );
    }

    private static boolean html = false;

    void doApply(String script, String expected) throws Exception {
        if (!html) {
              //  System.out.println("doApply script: " + script);
              //  System.out.println("  expected: " + expected);
        }

        DefaultNotation notation = new DefaultNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        RubiksCube cube = new RubiksCube();
        instance.applyTo(cube, false);
        String actual = Cubes.toPermutationString(cube, notation);

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

        assertEquals(expected, actual);
    }

    private String htmlEscape(String actual) {
        return actual.replaceAll("\n", "\\\\n")
        .replaceAll("<","&lt;")
        .replaceAll(">","&gt;");
    }

    @TestFactory
    public List<DynamicTest> testResolvedIterable() {
        return Arrays.asList(
                DynamicTest.dynamicTest("Move.1", () -> doResolvedIterable("R", "R")),
                DynamicTest.dynamicTest("Sequence.1", () -> doResolvedIterable("(R U F)", "R U F")),
                DynamicTest.dynamicTest("Repetition.1", () -> doResolvedIterable("(R)2", "R R")),
                DynamicTest.dynamicTest("Repetition.2", () -> doResolvedIterable("(R U F)3 D1", "R U F R U F R U F D")),
                DynamicTest.dynamicTest("Inversion.1", () -> doResolvedIterable("(R)'", "R'")),
                DynamicTest.dynamicTest("Inversion.2", () -> doResolvedIterable("(R F)'", "F' R'")),
                DynamicTest.dynamicTest("Inversion.3", () -> doResolvedIterable("(R- U F)- (R' U F)'", "F' U' R F' U' R")),
                DynamicTest.dynamicTest("Conjugation.1", () -> doResolvedIterable("<CU>R", "CU R CD")),
                DynamicTest.dynamicTest("Conjugation.2", () -> doResolvedIterable("<CU CF>(R)", "CU CF R CB CD")),
                DynamicTest.dynamicTest("Conjugation.3", () -> doResolvedIterable("<CU CF>(R B)", "CU CF R B CB CD")),
                DynamicTest.dynamicTest("Conjugation.4", () -> doResolvedIterable("<R>U", "R U R'")),
                DynamicTest.dynamicTest("Commutation.1", () -> doResolvedIterable("[CU,R]", "CU R CD R'")),
                DynamicTest.dynamicTest("Commutation.2", () -> doResolvedIterable("[CU CF,R]", "CU CF R CB CD R'")),
                DynamicTest.dynamicTest("Commutation.3", () -> doResolvedIterable("[CU CF,R B]", "CU CF R B CB CD B' R'")),
                DynamicTest.dynamicTest("Commutation.4", () -> doResolvedIterable("[R,U]", "R U R' U'")),
                DynamicTest.dynamicTest("Reflection.1", () -> doResolvedIterable("(R' U F)*", "L D' B'")),
                DynamicTest.dynamicTest("Permutation.1", () -> doResolvedIterable("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)")),
                DynamicTest.dynamicTest("NOP.1", () -> doResolvedIterable(".", "")),
                DynamicTest.dynamicTest("NOP.2", () -> doResolvedIterable("R . U · F", "R U F"))
        );
    }

    void doResolvedIterable(String script, String expected) throws Exception {
        System.out.println("doResolvedIterable script: " + script);
        System.out.println("  expected: " + expected);

        DefaultNotation notation = new DefaultNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        Map<String, MacroNode> macros = Collections.emptyMap();

        StringWriter out = new StringWriter();
        try (PrintWriter w = new PrintWriter(out)) {
            boolean first = true;
            for (Node node : instance.resolvedIterable(false)) {
                System.out.println(node);
                if (first) {
                    first = false;
                } else {
                    w.print(" ");
                }
                node.writeTokens(w, notation, macros);
            }
        }

        String actual = out.toString();

        assertEquals(expected, actual);
    }
}