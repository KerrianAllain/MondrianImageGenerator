import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Test_TreeSerialization {
    private static StrategySpecs specs(int strategy) {
        return new StrategySpecs(new String[] {
                String.valueOf(strategy), "unused", "100", "80", "2", "5", "7", "A"
        });
    }

    private static List<LineData> lines() {
        return Arrays.asList(
                new LineData(1, 0.5, 0.5, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW),
                new LineData(2, 0.3, 0.7, Color.CYAN, Color.MAGENTA, Color.BLACK, Color.WHITE));
    }

    private static void testTree2DRoundTrip() {
        Tree2D original = new Tree2D(specs(2), lines());
        Tree2D restored = new Tree2D(original.toString());

        assert original.toString().equals(restored.toString());
        assert original.width() == restored.width();
        assert original.height() == restored.height();
        assert original.toString().startsWith("T2D(100,80,7,");

        Tree2D leaf = new Tree2D("T2D(20,30,3,L(12,34,56))");
        assert leaf.toString().equals("T2D(20,30,3,L(12,34,56))");
        assert new Tree2D("T2D(20,30,L(12,34,56))").toString().equals("T2D(20,30,1,L(12,34,56))");
    }

    private static void testRQuadtreeRoundTrip() {
        RQuadtree original = new RQuadtree(specs(3), lines());
        RQuadtree restored = new RQuadtree(original.toString());

        assert original.toString().equals(restored.toString());
        assert original.width() == restored.width();
        assert original.height() == restored.height();
        assert original.toString().startsWith("RQT(100,80,7,");
        assert original.toString().contains("0,255,0");
        assert original.toString().contains("255,255,0");

        RQuadtree leaf = new RQuadtree("RQT(20,30,3,L(12,34,56))");
        assert leaf.toString().equals("RQT(20,30,3,L(12,34,56))");
        assert new RQuadtree("RQT(20,30,L(12,34,56))").toString().equals("RQT(20,30,1,L(12,34,56))");
    }

    private static void testInvalidRepresentation() {
        try {
            new Tree2D("T2D(10,10,D(X,0,L(0,0,0),L(0,0,0)))");
            assert false : "Out-of-bounds divisions must be rejected.";
        } catch (IllegalArgumentException expected) {
            assert true;
        }
    }

    public static void main(String[] args) {
        testTree2DRoundTrip();
        testRQuadtreeRoundTrip();
        testInvalidRepresentation();
    }
}
