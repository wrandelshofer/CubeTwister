package ch.randelshofer.rubik.cube;

public class CubeFactory {
    public static Cube create(int layerCount) {
        switch (layerCount) {
            case 2:
                return new PocketCube();
            case 3:
                return new RubiksCube();
            case 4:
                return new RevengeCube();
            case 5:
                return new ProfessorCube();
            case 6:
                return new Cube6();
            case 7:
                return new Cube7();
            default:
                throw new IllegalArgumentException("layerCount=" + layerCount);
        }
    }

}
