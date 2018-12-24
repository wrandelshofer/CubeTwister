module ch.randelshofer.cubetwister {
    requires java.desktop;

    requires jhall;
    requires org.jhotdraw7.application;
    requires org.jhotdraw7.draw;
    requires org.monte.media;
    requires org.kociemba.twophase;
    requires org.junit.jupiter.api;

    opens ch.randelshofer.rubik to org.junit.jupiter.api;
}