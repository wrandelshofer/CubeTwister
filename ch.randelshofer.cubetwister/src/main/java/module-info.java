module ch.randelshofer.cubetwister {
    requires java.desktop;

    requires javahelp;
    requires org.jhotdraw7.application;
    requires org.jhotdraw7.draw;
    requires org.monte.media;
    requires org.monte.media.swing;
    requires org.kociemba.twophase;

    exports ch.randelshofer.rubik;
    exports ch.randelshofer.geom3d;
    exports ch.randelshofer.rubik.parser;
    exports ch.randelshofer.rubik.cube;
    exports ch.randelshofer.rubik.notation;
    exports ch.randelshofer.io;
}