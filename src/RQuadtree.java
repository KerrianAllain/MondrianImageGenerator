
import java.awt.Color;
import java.util.List;
import java.util.Objects;

public class RQuadtree implements Tree {

    private final Point bottomRight;
    private final Point topLeft;
    private final Color color;
    private final int lineWidth;

    private DoubleDivision division;
    private RQuadtree nw;
    private RQuadtree ne;
    private RQuadtree se;
    private RQuadtree sw;

    private BinaryHeap<RQuadtree> leafHeap;

    /**
     * Constructs a tree from specifications.
     *
     * @param specs The Strategy specifications.
     * @param linesData The data to use for the construction.
     */
    public RQuadtree(StrategySpecs specs, List<LineData> linesData) {
        this.bottomRight = new Point(specs.width, specs.height);
        this.topLeft = new Point(0, 0);
        this.color = Color.WHITE;
        this.lineWidth = specs.lineWidth;
        this.leafHeap = new BinaryHeap<>(this);

        this.build(specs.nbLeavesToDivide, specs.minCutDimension, linesData);
    }

    /**
     * Reconstructs a tree from its {@link #toString()} representation.
     *
     * @param representation The serialized tree.
     */
    public RQuadtree(String representation) {
        this(parse(representation));
    }

    private RQuadtree(Point topLeft, Point bottomRight, Color color, int lineWidth) {
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.color = color;
        this.lineWidth = lineWidth;
    }

    /**
     * Builds the RQuadtree from the given data.
     *
     * <p>
     * <b>Complexity: O(n²)</b>, to be more specific, in <b>θ(n * m)</b>, where
     * {@code n} is the number of lines to process, and {@code m} is the
     * complexity of {@link RQuadtree#leafOfMaximumWeight(int)} (which increases
     * at each iteration of loop because of the insertions).
     * </p>
     *
     * @param nbLeavesToDivide The maximum number of leaves authorized.
     * @param minCutDimension The minimal square cut dimension.
     * @param v The variant used to represent the RQuadtree.
     * @param lines The list of lines from the source file.
     */
    private void build(int nbLeavesToDivide, int minCutDimension, List<LineData> lines) {
        int divisions = 0;

        for (LineData line : lines) {
            if (divisions == nbLeavesToDivide) {
                break;
            }

            RQuadtree workingLeaf = this.leafHeap.pop();
            if (workingLeaf.width() < minCutDimension || workingLeaf.height() < minCutDimension) {
                break;
            }

            workingLeaf.division = new DoubleDivision(workingLeaf.topLeft, line, workingLeaf);

            workingLeaf.nw = workingLeaf.buildLeaf(line.nw, ChildType.NW);
            workingLeaf.ne = workingLeaf.buildLeaf(line.ne, ChildType.NE);
            workingLeaf.se = workingLeaf.buildLeaf(line.se, ChildType.SE);
            workingLeaf.sw = workingLeaf.buildLeaf(line.sw, ChildType.SW);

            this.leafHeap.push(workingLeaf.nw);
            this.leafHeap.push(workingLeaf.ne);
            this.leafHeap.push(workingLeaf.se);
            this.leafHeap.push(workingLeaf.sw);

            divisions++;
        }
    }

    /**
     * Builds a child RQuadtree leaf based on the split geometry and child type.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @param childColor Specified color for the child.
     * @param childType Whether this is a LEFT or RIGHT child node.
     * @return A new RQuadtree instance representing the split child leaf.
     */
    private RQuadtree buildLeaf(Color childColor, ChildType childType) {
        Color nextColor = (Objects.isNull(childColor)) ? this.color : childColor;
        switch (childType) {
            case NW:
                return new RQuadtree(this.topLeft, new Point(this.division.xValue, this.division.yValue), nextColor,
                        this.lineWidth);

            case NE:
                return new RQuadtree(new Point(this.division.xValue, this.topLeft.y),
                        new Point(this.bottomRight.x, this.division.yValue), nextColor, this.lineWidth);

            case SE:
                return new RQuadtree(new Point(this.division.xValue, this.division.yValue), this.bottomRight,
                        nextColor, this.lineWidth);

            case SW:
                return new RQuadtree(new Point(this.topLeft.x, this.division.yValue),
                        new Point(this.division.xValue, this.bottomRight.y), nextColor, this.lineWidth);
            default:
                throw new AssertionError();
        }
    }

    /**
     * @return {@code true} if the tree has no child, {@code false} otherwise.
     */
    private boolean isLeaf() {
        return Objects.isNull(this.nw);
    }

    /**
     * Build the image representation of the tree.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the number of pixels.
     * </p>
     *
     * @param lineWidth The line width of separation between rectangles.
     * @return The resulting image.
     */
    @Override
    public Image toImage() {
        Image image = new Image(this.width(), this.height());
        return this.colorImage(image, this.lineWidth);
    }

    /**
     * Color an image a rectangle at a time.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the number of pixels.
     * </p>
     *
     * @param image The image to progressively color.
     * @param lineWidth The line width of separation between rectangles.
     * @return The colored image.
     */
    private Image colorImage(Image image, int lineWidth) {
        if (this.isLeaf()) {
            image.setRectangle(this.topLeft.x, this.bottomRight.x, this.topLeft.y, this.bottomRight.y, this.color);
            return image;
        }

        image = this.nw.colorImage(image, lineWidth);
        image = this.ne.colorImage(image, lineWidth);
        image = this.se.colorImage(image, lineWidth);
        image = this.sw.colorImage(image, lineWidth);

        if (lineWidth > 0) {
            this.drawSeparationLine(image, lineWidth);
        }

        return image;
    }

    /**
     * Draw a line of separation between rectangles.
     *
     * <p>
     * <b>Complexity: depends on
     * {@link Image#setRectangle(int, int, int, int, Color)}</b>.
     * </p>
     *
     * @param image The image to add a separation line.
     * @param lineWidth The width of the separation line.
     */
    private void drawSeparationLine(Image image, int lineWidth) {
        int halfWidth = lineWidth / 2;
        int restWidth = lineWidth - halfWidth;

        Point xStart = new Point(this.division.xValue - halfWidth, this.topLeft.y);
        Point xEnd = new Point(this.division.xValue + restWidth, this.bottomRight.y);

        Point yStart = new Point(this.topLeft.x, this.division.yValue - halfWidth);
        Point yEnd = new Point(this.bottomRight.x, this.division.yValue + restWidth);

        image.setRectangle(xStart.x, xEnd.x, xStart.y, xEnd.y, Color.BLACK);
        image.setRectangle(yStart.x, yEnd.x, yStart.y, yEnd.y, Color.BLACK);
    }

    @Override
    public int width() {
        return this.bottomRight.x - this.topLeft.x;
    }

    @Override
    public int height() {
        return this.bottomRight.y - this.topLeft.y;
    }

    @Override
    public Point topLeftPoint() {
        return this.topLeft;
    }

    private RQuadtree(RQuadtree tree) {
        this.bottomRight = tree.bottomRight;
        this.topLeft = tree.topLeft;
        this.color = tree.color;
        this.lineWidth = tree.lineWidth;

        this.division = tree.division;
        this.nw = tree.nw;
        this.ne = tree.ne;
        this.se = tree.se;
        this.sw = tree.sw;

        this.leafHeap = null;
    }

    private static RQuadtree parse(String representation) {
        return TreeText.parse(representation, "RQT", RQuadtree::parseNode);
    }

    private static RQuadtree parseNode(TreeText.Cursor cursor, Point topLeft, Point bottomRight, int lineWidth) {
        if (cursor.startsWith("L(")) {
            return new RQuadtree(topLeft, bottomRight, cursor.readColor(), lineWidth);
        }

        cursor.expect("D(");
        int xValue = cursor.readInt();
        cursor.expect(",");
        int yValue = cursor.readInt();
        if (xValue <= topLeft.x || xValue >= bottomRight.x || yValue <= topLeft.y || yValue >= bottomRight.y) {
            throw new IllegalArgumentException("Division must be inside the node boundaries.");
        }

        RQuadtree tree = new RQuadtree(topLeft, bottomRight, Color.WHITE, lineWidth);
        tree.division = new DoubleDivision(xValue, yValue);
        Point split = new Point(xValue, yValue);

        cursor.expect(",");
        tree.nw = parseNode(cursor, topLeft, split, lineWidth);
        cursor.expect(",");
        tree.ne = parseNode(cursor, new Point(xValue, topLeft.y), new Point(bottomRight.x, yValue), lineWidth);
        cursor.expect(",");
        tree.se = parseNode(cursor, split, bottomRight, lineWidth);
        cursor.expect(",");
        tree.sw = parseNode(cursor, new Point(topLeft.x, yValue), new Point(xValue, bottomRight.y), lineWidth);
        cursor.expect(")");
        return tree;
    }

    @Override
    public String toString() {
        return TreeText.write("RQT", this.width(), this.height(), this.lineWidth, this::appendNode);
    }

    private void appendNode(StringBuilder sb) {
        if (this.isLeaf()) {
            TreeText.appendLeaf(sb, this.color);
            return;
        }

        sb.append("D(").append(this.division.xValue).append(",").append(this.division.yValue).append(",");
        this.nw.appendNode(sb);
        sb.append(",");
        this.ne.appendNode(sb);
        sb.append(",");
        this.se.appendNode(sb);
        sb.append(",");
        this.sw.appendNode(sb);
        sb.append(")");
    }
}
