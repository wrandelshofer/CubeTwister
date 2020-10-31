# CubeTwister
Companion software for Rubik's Cube like puzzles.

# Usage with JShell

You have to build CubeTwister with IntelliJ IDEA, because
the Ant scripts are currently outdated.

    cd CubeTwister
    jshell --module-path out/production:lib/compile/modulepath/jhotdraw:lib/compile/classpath/javahelp/jhall.jar:lib/compile/modulepath/montemedia:lib/compile/modulepath/nanoxml
    /env --add-modules ch.randelshofer.cubetwister