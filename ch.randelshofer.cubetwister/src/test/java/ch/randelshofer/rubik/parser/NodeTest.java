/*
 * @(#)NodeTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.rubik.notation.DefaultScriptNotation;
import ch.randelshofer.rubik.parser.ast.MacroNode;
import ch.randelshofer.rubik.parser.ast.MoveNode;
import ch.randelshofer.rubik.parser.ast.Node;
import ch.randelshofer.rubik.parser.ast.PermutationCycleNode;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeTest {
    @Nonnull
    @TestFactory
    public List<DynamicTest> testApply() {
        return Arrays.asList(
                DynamicTest.dynamicTest("R", () -> doTestApply("R", "(ubr,bdr,dfr,fur)\n(ur,br,dr,fr)\n(+r)")),
                DynamicTest.dynamicTest("U", () -> doTestApply("U", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub)\n(+u)")),
                DynamicTest.dynamicTest("F", () -> doTestApply("F", "(dfr,lfd,ufl,rfu)\n(rf,df,lf,uf)\n(+f)")),
                DynamicTest.dynamicTest("L", () -> doTestApply("L", "(ufl,fdl,dbl,bul)\n(ul,fl,dl,bl)\n(+l)")),
                DynamicTest.dynamicTest("D", () -> doTestApply("D", "(drb,dbl,dlf,dfr)\n(dr,db,dl,df)\n(+d)")),
                DynamicTest.dynamicTest("B", () -> doTestApply("B", "(ulb,ldb,drb,rub)\n(bu,bl,bd,br)\n(+b)")),
                DynamicTest.dynamicTest("R'", () -> doTestApply("R'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br)\n(-r)")),
                DynamicTest.dynamicTest("U'", () -> doTestApply("U'", "(ubr,ulb,ufl,urf)\n(ur,ub,ul,uf)\n(-u)")),
                DynamicTest.dynamicTest("F'", () -> doTestApply("F'", "(ufl,lfd,dfr,rfu)\n(rf,uf,lf,df)\n(-f)")),
                DynamicTest.dynamicTest("L'", () -> doTestApply("L'", "(dbl,fdl,ufl,bul)\n(ul,bl,dl,fl)\n(-l)")),
                DynamicTest.dynamicTest("D'", () -> doTestApply("D'", "(dlf,dbl,drb,dfr)\n(dr,df,dl,db)\n(-d)")),
                DynamicTest.dynamicTest("B'", () -> doTestApply("B'", "(drb,ldb,ulb,rub)\n(bu,br,bd,bl)\n(-b)")),
                DynamicTest.dynamicTest("R2", () -> doTestApply("R2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb)\n(++r)")),
                DynamicTest.dynamicTest("U2", () -> doTestApply("U2", "(ulb,urf) (ufl,ubr)\n(ur,ul) (bu,fu)\n(++u)")),
                DynamicTest.dynamicTest("F2", () -> doTestApply("F2", "(dlf,urf) (ufl,dfr)\n(rf,lf) (fu,fd)\n(++f)")),
                DynamicTest.dynamicTest("L2", () -> doTestApply("L2", "(dlf,ulb) (ufl,dbl)\n(ul,dl) (lb,lf)\n(++l)")),
                DynamicTest.dynamicTest("D2", () -> doTestApply("D2", "(dbl,dfr) (dlf,drb)\n(dr,dl) (bd,fd)\n(++d)")),
                DynamicTest.dynamicTest("B2", () -> doTestApply("B2", "(dbl,ubr) (ulb,drb)\n(bu,bd) (rb,lb)\n(++b)")),
                DynamicTest.dynamicTest("MR", () -> doTestApply("MR", "(bu,db,fd,uf)\n(u,++b,-d,+f)")),
                DynamicTest.dynamicTest("MU", () -> doTestApply("MU", "(rf,fl,lb,br)\n(r,-f,+l,++b)")),
                DynamicTest.dynamicTest("MF", () -> doTestApply("MF", "(ur,rd,dl,lu)\n(r,++d,-l,+u)")),
                DynamicTest.dynamicTest("ML", () -> doTestApply("ML", "(bu,uf,fd,db)\n(u,+f,-d,++b)")),
                DynamicTest.dynamicTest("MD", () -> doTestApply("MD", "(rf,br,lb,fl)\n(r,++b,+l,-f)")),
                DynamicTest.dynamicTest("MB", () -> doTestApply("MB", "(ur,lu,dl,rd)\n(r,+u,-l,++d)")),
                DynamicTest.dynamicTest("MR'", () -> doTestApply("MR'", "(bu,uf,fd,db)\n(u,+f,-d,++b)")),
                DynamicTest.dynamicTest("MU'", () -> doTestApply("MU'", "(rf,br,lb,fl)\n(r,++b,+l,-f)")),
                DynamicTest.dynamicTest("MF'", () -> doTestApply("MF'", "(ur,lu,dl,rd)\n(r,+u,-l,++d)")),
                DynamicTest.dynamicTest("ML'", () -> doTestApply("ML'", "(bu,db,fd,uf)\n(u,++b,-d,+f)")),
                DynamicTest.dynamicTest("MD'", () -> doTestApply("MD'", "(rf,fl,lb,br)\n(r,-f,+l,++b)")),
                DynamicTest.dynamicTest("MB'", () -> doTestApply("MB'", "(ur,rd,dl,lu)\n(r,++d,-l,+u)")),
                DynamicTest.dynamicTest("MR2", () -> doTestApply("MR2", "(bu,fd) (bd,fu)\n(u,-d) (f,+b)")),
                DynamicTest.dynamicTest("MU2", () -> doTestApply("MU2", "(rf,lb) (rb,lf)\n(r,+l) (f,-b)")),
                DynamicTest.dynamicTest("MF2", () -> doTestApply("MF2", "(ur,dl) (dr,ul)\n(r,-l) (u,+d)")),
                DynamicTest.dynamicTest("ML2", () -> doTestApply("ML2", "(bu,fd) (bd,fu)\n(u,-d) (f,+b)")),
                DynamicTest.dynamicTest("MD2", () -> doTestApply("MD2", "(rf,lb) (rb,lf)\n(r,+l) (f,-b)")),
                DynamicTest.dynamicTest("MB2", () -> doTestApply("MB2", "(ur,dl) (dr,ul)\n(r,-l) (u,+d)")),
                DynamicTest.dynamicTest("TR", () -> doTestApply("TR", "(ubr,bdr,dfr,fur)\n(ur,br,dr,fr) (bu,db,fd,uf)\n(+r) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("TU", () -> doTestApply("TU", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub) (rf,fl,lb,br)\n(+u) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("TF", () -> doTestApply("TF", "(dfr,lfd,ufl,rfu)\n(ur,rd,dl,lu) (rf,df,lf,uf)\n(+f) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("TL", () -> doTestApply("TL", "(ufl,fdl,dbl,bul)\n(bu,uf,fd,db) (ul,fl,dl,bl)\n(+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("TD", () -> doTestApply("TD", "(drb,dbl,dlf,dfr)\n(rf,br,lb,fl) (dr,db,dl,df)\n(+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("TB", () -> doTestApply("TB", "(ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (bu,bl,bd,br)\n(+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("TR'", () -> doTestApply("TR'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br) (bu,uf,fd,db)\n(-r) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("TU'", () -> doTestApply("TU'", "(ubr,ulb,ufl,urf)\n(ur,ub,ul,uf) (rf,br,lb,fl)\n(-u) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("TF'", () -> doTestApply("TF'", "(ufl,lfd,dfr,rfu)\n(ur,lu,dl,rd) (rf,uf,lf,df)\n(-f) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("TL'", () -> doTestApply("TL'", "(dbl,fdl,ufl,bul)\n(bu,db,fd,uf) (ul,bl,dl,fl)\n(-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("TD'", () -> doTestApply("TD'", "(dlf,dbl,drb,dfr)\n(rf,fl,lb,br) (dr,df,dl,db)\n(-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("TB'", () -> doTestApply("TB'", "(drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (bu,br,bd,bl)\n(-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("TR2", () -> doTestApply("TR2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu)\n(++r) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("TU2", () -> doTestApply("TU2", "(ulb,urf) (ufl,ubr)\n(ur,ul) (rf,lb) (bu,fu) (rb,lf)\n(++u) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("TF2", () -> doTestApply("TF2", "(dlf,urf) (ufl,dfr)\n(ur,dl) (rf,lf) (dr,ul) (fu,fd)\n(++f) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("TL2", () -> doTestApply("TL2", "(dlf,ulb) (ufl,dbl)\n(bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("TD2", () -> doTestApply("TD2", "(dbl,dfr) (dlf,drb)\n(rf,lb) (dr,dl) (rb,lf) (bd,fd)\n(++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("TB2", () -> doTestApply("TB2", "(dbl,ubr) (ulb,drb)\n(ur,dl) (dr,ul) (bu,bd) (rb,lb)\n(++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("CR", () -> doTestApply("CR", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (bu,db,fd,uf) (ul,bl,dl,fl)\n(+r) (-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("CU", () -> doTestApply("CU", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (rf,fl,lb,br) (dr,df,dl,db)\n(+u) (-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("CF", () -> doTestApply("CF", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("CL", () -> doTestApply("CL", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (bu,uf,fd,db) (ul,fl,dl,bl)\n(-r) (+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("CD", () -> doTestApply("CD", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (rf,br,lb,fl) (dr,db,dl,df)\n(-u) (+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("CB", () -> doTestApply("CB", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("CR'", () -> doTestApply("CR'", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (bu,uf,fd,db) (ul,fl,dl,bl)\n(-r) (+l) (u,+f,-d,++b)")),
                DynamicTest.dynamicTest("CU'", () -> doTestApply("CU'", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (rf,br,lb,fl) (dr,db,dl,df)\n(-u) (+d) (r,++b,+l,-f)")),
                DynamicTest.dynamicTest("CF'", () -> doTestApply("CF'", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(ur,lu,dl,rd) (rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b) (r,+u,-l,++d)")),
                DynamicTest.dynamicTest("CL'", () -> doTestApply("CL'", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (bu,db,fd,uf) (ul,bl,dl,fl)\n(+r) (-l) (u,++b,-d,+f)")),
                DynamicTest.dynamicTest("CD'", () -> doTestApply("CD'", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (rf,fl,lb,br) (dr,df,dl,db)\n(+u) (-d) (r,-f,+l,++b)")),
                DynamicTest.dynamicTest("CB'", () -> doTestApply("CB'", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(ur,rd,dl,lu) (rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b) (r,++d,-l,+u)")),
                DynamicTest.dynamicTest("CR2", () -> doTestApply("CR2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++r) (++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("CU2", () -> doTestApply("CU2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (rf,lb) (dr,dl) (bu,fu) (rb,lf) (bd,fd)\n(++u) (++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("CF2", () -> doTestApply("CF2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(ur,dl) (rf,lf) (dr,ul) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("CL2", () -> doTestApply("CL2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (bu,fd) (bd,fu) (ul,dl) (lb,lf)\n(++r) (++l) (u,-d) (f,+b)")),
                DynamicTest.dynamicTest("CD2", () -> doTestApply("CD2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (rf,lb) (dr,dl) (bu,fu) (rb,lf) (bd,fd)\n(++u) (++d) (r,+l) (f,-b)")),
                DynamicTest.dynamicTest("CB2", () -> doTestApply("CB2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(ur,dl) (rf,lf) (dr,ul) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b) (r,-l) (u,+d)")),
                DynamicTest.dynamicTest("SR", () -> doTestApply("SR", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (ul,bl,dl,fl)\n(+r) (-l)")),
                DynamicTest.dynamicTest("SU", () -> doTestApply("SU", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (dr,df,dl,db)\n(+u) (-d)")),
                DynamicTest.dynamicTest("SF", () -> doTestApply("SF", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b)")),
                DynamicTest.dynamicTest("SL", () -> doTestApply("SL", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (ul,fl,dl,bl)\n(-r) (+l)")),
                DynamicTest.dynamicTest("SD", () -> doTestApply("SD", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (dr,db,dl,df)\n(-u) (+d)")),
                DynamicTest.dynamicTest("SB", () -> doTestApply("SB", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b)")),
                DynamicTest.dynamicTest("SR'", () -> doTestApply("SR'", "(dfr,bdr,ubr,fur) (ufl,fdl,dbl,bul)\n(ur,fr,dr,br) (ul,fl,dl,bl)\n(-r) (+l)")),
                DynamicTest.dynamicTest("SU'", () -> doTestApply("SU'", "(ubr,ulb,ufl,urf) (drb,dbl,dlf,dfr)\n(ur,ub,ul,uf) (dr,db,dl,df)\n(-u) (+d)")),
                DynamicTest.dynamicTest("SF'", () -> doTestApply("SF'", "(ufl,lfd,dfr,rfu) (ulb,ldb,drb,rub)\n(rf,uf,lf,df) (bu,bl,bd,br)\n(-f) (+b)")),
                DynamicTest.dynamicTest("SL'", () -> doTestApply("SL'", "(ubr,bdr,dfr,fur) (dbl,fdl,ufl,bul)\n(ur,br,dr,fr) (ul,bl,dl,fl)\n(+r) (-l)")),
                DynamicTest.dynamicTest("SD'", () -> doTestApply("SD'", "(ufl,ulb,ubr,urf) (dlf,dbl,drb,dfr)\n(ur,uf,ul,ub) (dr,df,dl,db)\n(+u) (-d)")),
                DynamicTest.dynamicTest("SB'", () -> doTestApply("SB'", "(dfr,lfd,ufl,rfu) (drb,ldb,ulb,rub)\n(rf,df,lf,uf) (bu,br,bd,bl)\n(+f) (-b)")),
                DynamicTest.dynamicTest("SR2", () -> doTestApply("SR2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (ul,dl) (lb,lf)\n(++r) (++l)")),
                DynamicTest.dynamicTest("SU2", () -> doTestApply("SU2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (dr,dl) (bu,fu) (bd,fd)\n(++u) (++d)")),
                DynamicTest.dynamicTest("SF2", () -> doTestApply("SF2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(rf,lf) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b)")),
                DynamicTest.dynamicTest("SL2", () -> doTestApply("SL2", "(drb,urf) (ubr,dfr) (dlf,ulb) (ufl,dbl)\n(ur,dr) (rf,rb) (ul,dl) (lb,lf)\n(++r) (++l)")),
                DynamicTest.dynamicTest("SD2", () -> doTestApply("SD2", "(ulb,urf) (dbl,dfr) (ufl,ubr) (dlf,drb)\n(ur,ul) (dr,dl) (bu,fu) (bd,fd)\n(++u) (++d)")),
                DynamicTest.dynamicTest("SB2", () -> doTestApply("SB2", "(dlf,urf) (ufl,dfr) (dbl,ubr) (ulb,drb)\n(rf,lf) (bu,bd) (rb,lb) (fu,fd)\n(++f) (++b)")),
                DynamicTest.dynamicTest("(R U F)", () -> doTestApply("(R U F)", "(dfr,urf) (drb,fdl,flu,lbu,bru)\n(+ur,br,dr,fd,fl,fu,lu,bu) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("(R U F)'", () -> doTestApply("(R U F)'", "(dfr,urf) (ulb,ufl,lfd,bdr,ubr)\n(+ur,ub,ul,uf,lf,df,rd,rb) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("(R)2", () -> doTestApply("(R)2", "(drb,urf) (ubr,dfr)\n(ur,dr) (rf,rb)\n(++r)")),
                DynamicTest.dynamicTest("(R U F)3", () -> doTestApply("(R U F)3", "(dfr,urf) (ufl,bdr,ulb,lfd,ubr)\n(+ur,fd,lu,rb,lf,ub,dr,fu) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("(R U F)'3", () -> doTestApply("(R U F)'3", "(dfr,urf) (dlf,bul,rbd,luf,rub)\n(+ur,uf,rd,bu,fl,br,ul,df) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("(R U F)3'", () -> doTestApply("(R U F)3'", "(dfr,urf) (dlf,bul,rbd,luf,rub)\n(+ur,uf,rd,bu,fl,br,ul,df) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("(R U F)3''", () -> doTestApply("(R U F)3''", "(dfr,urf) (ufl,bdr,ulb,lfd,ubr)\n(+ur,fd,lu,rb,lf,ub,dr,fu) (+rf)\n(-r) (-u) (-f)")),
                DynamicTest.dynamicTest("(R U F)3'4", () -> doTestApply("(R U F)3'4", "(ufl,bdr,ulb,lfd,ubr)\n(+ur,fl) (+dr,lu) (+bu,df) (+rb,uf)")),
                DynamicTest.dynamicTest("(R)'", () -> doTestApply("(R)'", "(dfr,bdr,ubr,fur)\n(ur,fr,dr,br)\n(-r)")),
                DynamicTest.dynamicTest("F' R'", () -> doTestApply("F' R'", "(+ufl,lfd,bdr,ubr,fur) (-dfr)\n(ur,fr,fu,fl,fd,dr,br)\n(-r) (-f)")),
                DynamicTest.dynamicTest("(R F)'", () -> doTestApply("(R F)'", "(+ufl,lfd,bdr,ubr,fur) (-dfr)\n(ur,fr,fu,fl,fd,dr,br)\n(-r) (-f)")),
                DynamicTest.dynamicTest("(R- U F)- (R' U F)'", () -> doTestApply("(R- U F)- (R' U F)'", "(+ulb,lfd,rub,luf,rfu) (+dfr) (+drb)\n(+ur,ul,lf) (rf,dr,rb) (+bu,fu,fd)\n(++r) (++u) (++f)")),
                DynamicTest.dynamicTest("<CU>R", () -> doTestApply("<CU>R", "(ulb,ldb,drb,rub)\n(bu,bl,bd,br)\n(+b)")),
                DynamicTest.dynamicTest("<CD>'R", () -> doTestApply("<CD>'R", "(ulb,ldb,drb,rub)\n(bu,bl,bd,br)\n(+b)")),
                DynamicTest.dynamicTest("<CU CF>(R)", () -> doTestApply("<CU CF>(R)", "(ufl,ulb,ubr,urf)\n(ur,uf,ul,ub)\n(+u)")),
                DynamicTest.dynamicTest("<CU CF>(R B)", () -> doTestApply("<CU CF>(R B)", "(-dlf,bld,ulb,ubr,urf) (+ufl)\n(ur,uf,fl,dl,bl,ul,ub)\n(+u) (+l)")),
                DynamicTest.dynamicTest("(<CU CF>(R B))'", () -> doTestApply("(<CU CF>(R B))'", "(+ubr,ulb,bld,dlf,rfu) (-ufl)\n(ur,ub,ul,bl,dl,fl,uf)\n(-u) (-l)")),
                DynamicTest.dynamicTest("<R>U", () -> doTestApply("<R>U", "(dfr,luf,bul,rfu)\n(rf,fu,lu,bu)\n(+u)")),
                DynamicTest.dynamicTest("[CU,R]", () -> doTestApply("[CU,R]", "(+dfr,bdr,urf) (-ulb,ldb,bru)\n(ur,fr,dr,br,bu,bl,bd)\n(-r) (+b)")),
                DynamicTest.dynamicTest("[CU CF,R]", () -> doTestApply("[CU CF,R]", "(-ufl,ulb,fur) (+drb,bru,rdf)\n(ur,uf,ul,ub,fr,dr,br)\n(-r) (+u)")),
                DynamicTest.dynamicTest("[CU CF,R B]", () -> doTestApply("[CU CF,R B]", "(-dlf,bul,bru,rdf,rbd,dbl,urf) (+ufl)\n(ur,uf,fl,dl,bu,rf,rd,rb,db,lb,lu)\n(-r) (+u) (+l) (-b)")),
                DynamicTest.dynamicTest("[CU CF,R B]'", () -> doTestApply("[CU CF,R B]'", "(+dbl,rbd,rdf,bru,bul,dlf,rfu) (-ufl)\n(ur,lu,lb,db,rb,rd,rf,bu,dl,fl,uf)\n(+r) (-u) (-l) (+b)")),
                DynamicTest.dynamicTest("[R,U]", () -> doTestApply("[R,U]", "(+dfr,fur) (-ulb,rub)\n(ur,ub,fr)")),
                DynamicTest.dynamicTest("(R' U F)*", () -> doTestApply("(R' U F)*", "(-dlf,dbl,bru,bdr,rdf) (+ufl,ulb)\n(+dr,df,dl,bu,br,bd) (+ul,fl,lb)\n(+l) (-d) (-b)")),
                DynamicTest.dynamicTest("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", () -> doTestApply("(+urf,bru,drb,frd) (+ur,br,dr,fr) (+r) (r,b) (++u,d) (++f,+l)", "(+ubr,bdr,dfr,urf)\n(+ur,br,dr,fr)\n(+r,+b) (++u,d) (++f,+l)")),
                DynamicTest.dynamicTest(".", () -> doTestApply(".", "()")),
                DynamicTest.dynamicTest("R . U · F", () -> doTestApply("R . U · F", "(dfr,urf) (drb,fdl,flu,lbu,bru)\n(+ur,br,dr,fd,fl,fu,lu,bu) (+rf)\n(+r) (+u) (+f)")),
                DynamicTest.dynamicTest("<empty>", () -> doTestApply("", "()"))
        );
    }

    private static boolean html = false;

    void doTestApply(@Nonnull String script, String expected) throws Exception {
        if (html) {
            System.out.println("  <article>");
            System.out.println("    <section class=\"unittest\">");
            System.out.println("      <p class=\"input\">" + htmlEscape(script) + "</p>");
            System.out.println("      <p class=\"expected\">" +
                    htmlEscape(expected) + "</p>");
            System.out.println("      <p class=\"actual\">" + "</p>");
            System.out.println("    </section>");
            System.out.println("  </article>");
        } else {
            System.out.println(" DynamicTest.dynamicTest(\"" + script + "\", () -> doTestApply(\"" + script + "\", \"" + expected.replaceAll("\n", "\\\\n") + "\")),");
        }
        doApply(script, expected);
        doInverseApply(script, expected);
        doInverseThenApply(script, expected);
        doResolvedIterableApply(script, expected);
        doInverseResolvedIterableApply(script, expected);
    }

    void doApply(@Nonnull String script, String expected) throws Exception {
        if (!html) {
            System.out.println("doApply script: " + script);
            System.out.println("  expected: " + expected);
        }
        DefaultScriptNotation notation = new DefaultScriptNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        RubiksCube cube = new RubiksCube();
        instance.applyTo(cube, false);
        String actual = Cubes.toPermutationString(cube, notation);

        if (!html) {
            System.out.println("  actual: " + actual);
        }

        assertEquals(expected, actual);
    }

    void doInverseApply(@Nonnull String script, String expected) throws Exception {
        if (!html) {
            System.out.println("doInverseApply script: " + script);
            System.out.println("  expected: " + expected);
        }
        DefaultScriptNotation notation = new DefaultScriptNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        RubiksCube cube = new RubiksCube();

        // WHEN: we do an inverse apply
        instance.applyTo(cube, true);

        // THEN: if we take the resulting permutation and inverse apply that
        //       permutation we should get the expected result
        String permutationString = Cubes.toPermutationString(cube, notation);
        Node parsedPermutation = parser.parse(permutationString);
        parsedPermutation.invert();
        cube.reset();
        parsedPermutation.applyTo(cube, false);
        String actual = Cubes.toPermutationString(cube, notation);

        if (!html) {
            System.out.println("  actual: " + actual);
            //System.out.println(" DynamicTest.dynamicTest(\"1\", () -> doApply(\"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
        }

        assertEquals(expected, actual);
    }

    void doInverseThenApply(@Nonnull String script, String expected) throws Exception {
        if (!html) {
            System.out.println("doInverseThenApply script: " + script);
            System.out.println("  expected: " + expected);
        }
        DefaultScriptNotation notation = new DefaultScriptNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        RubiksCube cube = new RubiksCube();

        // WHEN: we do an inverse, and then apply
        instance.invert();
        ;
        instance.applyTo(cube, false);

        // THEN: if we take the resulting permutation and inverse apply that
        //       permutation we should get the expected result
        String permutationString = Cubes.toPermutationString(cube, notation);
        Node parsedPermutation = parser.parse(permutationString);
        parsedPermutation.invert();
        cube.reset();
        parsedPermutation.applyTo(cube, false);
        String actual = Cubes.toPermutationString(cube, notation);

        if (!html) {
            System.out.println("  actual: " + actual);
            //System.out.println(" DynamicTest.dynamicTest(\"1\", () -> doApply(\"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
        }

        assertEquals(expected, actual);
    }

    void doResolvedIterableApply(@Nonnull String script, String expected) throws Exception {
        if (!html) {
            System.out.println("doResolvedIterationApply script: " + script);
            System.out.println("  expected: " + expected);
        }

        DefaultScriptNotation notation = new DefaultScriptNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        RubiksCube cube = new RubiksCube();

        // WHEN: We apply a resolved iterable
        for (Node node : instance.resolvedIterable(false)) {
            if ((node instanceof PermutationCycleNode)
                    || (node instanceof MoveNode)) {

                node.applyTo(cube, false);
            } else {
                throw new AssertionError("Unresolved node: " + node);
            }
        }

        // THEN: we should get the expected result
        String actual = Cubes.toPermutationString(cube, notation);

        if (!html) {
            System.out.println("  actual: " + actual);
            //System.out.println(" DynamicTest.dynamicTest(\"1\", () -> doApply(\"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
        }

        assertEquals(expected, actual);
    }

    void doInverseResolvedIterableApply(@Nonnull String script, String expected) throws Exception {
        if (!html) {
            System.out.println("doResolvedIterationApply script: " + script);
            System.out.println("  expected: " + expected);
        }

        DefaultScriptNotation notation = new DefaultScriptNotation();
        ScriptParser parser = new ScriptParser(notation);
        Node instance = parser.parse(script);
        RubiksCube cube = new RubiksCube();

        // WHEN: We apply an inverse resolved iterable
        for (Node node : instance.resolvedIterable(true)) {
            if ((node instanceof PermutationCycleNode)
                    || (node instanceof MoveNode)) {

                node.applyTo(cube, false);
            } else {
                throw new AssertionError("Unresolved node: " + node);
            }
        }

        // THEN: if we take the resulting permutation and inverse apply that
        //       permutation we should get the expected result
        String permutationString = Cubes.toPermutationString(cube, notation);
        Node parsedPermutation = parser.parse(permutationString);
        parsedPermutation.invert();
        cube.reset();
        parsedPermutation.applyTo(cube, false);
        String actual = Cubes.toPermutationString(cube, notation);

        if (!html) {
            System.out.println("  actual: " + actual);
            //System.out.println(" DynamicTest.dynamicTest(\"1\", () -> doApply(\"" + script + "\", \"" + actual.replaceAll("\n", "\\\\n") + "\")),");
        }

        assertEquals(expected, actual);
    }

    @Nonnull
    private String htmlEscape(@Nonnull String actual) {
        return actual.replaceAll("\n", "\\\\n")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testResolvedIterable() {
        return Arrays.asList(
                DynamicTest.dynamicTest("Move.1", () -> doResolvedIterable("R", "R")),
                DynamicTest.dynamicTest("Sequence.1", () -> doResolvedIterable("(R U F)", "R U F")),
                DynamicTest.dynamicTest("Repetition.1", () -> doResolvedIterable("(R)2", "R R")),
                DynamicTest.dynamicTest("Repetition.2", () -> doResolvedIterable("(R U F)3 D1", "R U F R U F R U F D")),
                DynamicTest.dynamicTest("Reflection.1", () -> doResolvedIterable("(R U F)*", "L' D' B'")),
                //FIXME implement reflection of permutations
                //DynamicTest.dynamicTest("Reflection: perm(R)*->perm(L')", () -> doResolvedIterable("((ubr,bdr,dfr,fur) (ur,br,dr,fr) (+r))*", "(dbl,fdl,ufl,bul) (ul,bl,dl,fl) (-l)")),
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

        DefaultScriptNotation notation = new DefaultScriptNotation();
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