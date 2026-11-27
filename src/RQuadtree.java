import java.awt.Color;
import java.util.List;
import java.util.Objects;

public class RQuadtree implements Tree {
    private final Point bottomRight;
    private final Point topLeft;
    private final Color color;

    private DoubleDivision division;
    private RQuadtree NW;
    private RQuadtree NE;
    private RQuadtree SE;
    private RQuadtree SW;

    private BinaryHeap<RQuadtree> leafHeap;

    public RQuadtree(StrategySpecs specs, List<LineData> linesData) {
        this.bottomRight = new Point(specs.width, specs.height);
        this.topLeft = new Point(0, 0);
        this.color = Color.WHITE;
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

    private RQuadtree(RQuadtree tree) {
        this.bottomRight = tree.bottomRight;
        this.topLeft = tree.topLeft;
        this.color = tree.color;
        this.division = tree.division;
        this.NW = tree.NW;
        this.NE = tree.NE;
        this.SE = tree.SE;
        this.SW = tree.SW;
        this.leafHeap = null;
    }

    private RQuadtree(Point topLeft, Point bottomRight, Color color) {
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.color = color;
    }

    private static RQuadtree parse(String representation) {
        TreeText.Cursor cursor = new TreeText.Cursor(representation);
        cursor.expect("RQT(");
        int width = cursor.readInt();
        cursor.expect(",");
        int height = cursor.readInt();
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Tree dimensions must be positive.");
        cursor.expect(",");
        RQuadtree tree = parseNode(cursor, new Point(0, 0), new Point(width, height));
        cursor.expect(")");
        cursor.finish();
        return tree;
    }

    private static RQuadtree parseNode(TreeText.Cursor cursor, Point topLeft, Point bottomRight) {
        if (cursor.startsWith("L("))
            return new RQuadtree(topLeft, bottomRight, cursor.readColor());

        cursor.expect("D(");
        int xValue = cursor.readInt();
        cursor.expect(",");
        int yValue = cursor.readInt();
        if (xValue <= topLeft.x || xValue >= bottomRight.x
                || yValue <= topLeft.y || yValue >= bottomRight.y)
            throw new IllegalArgumentException("Division must be inside the node boundaries.");

        RQuadtree tree = new RQuadtree(topLeft, bottomRight, Color.WHITE);
        tree.division = new DoubleDivision(xValue, yValue);
        Point split = new Point(xValue, yValue);
        cursor.expect(",");
        tree.NW = parseNode(cursor, topLeft, split);
        cursor.expect(",");
        tree.NE = parseNode(cursor, new Point(xValue, topLeft.y), new Point(bottomRight.x, yValue));
        cursor.expect(",");
        tree.SE = parseNode(cursor, split, bottomRight);
        cursor.expect(",");
        tree.SW = parseNode(cursor, new Point(topLeft.x, yValue), new Point(xValue, bottomRight.y));
        cursor.expect(")");
        return tree;
    }

    /**
     * Builds the RQuadtree from the given data.
     *
     * <p>
     * <b>Complexity: O(n²)</b>, to be more specific, in <b>θ(n * m)</b>,
     * where {@code n} is the number of lines to process,
     * and {@code m} is the complexity of {@link RQuadtree#leafOfMaximumWeight(int)}
     * (which increases at each iteration of loop because of the insertions).
     * </p>
     *
     * @param nbLeavesToDivide The maximum number of leaves authorized.
     * @param minCutDimension  The minimal square cut dimension.
     * @param v                The variant used to represent the RQuadtree.
     * @param lines            The list of lines from the source file.
     */
    private void build(int nbLeavesToDivide, int minCutDimension, List<LineData> lines) {
        int divisions = 0;

        for (LineData line : lines) {
            if (divisions == nbLeavesToDivide)
                break;

            RQuadtree workingLeaf = this.leafHeap.pop();
            if (workingLeaf.width() < minCutDimension || workingLeaf.height() < minCutDimension)
                break;

            workingLeaf.division = new DoubleDivision(workingLeaf.topLeft, line, workingLeaf);

            workingLeaf.NW = workingLeaf.buildLeaf(line.nw, ChildType.NW);
            workingLeaf.NE = workingLeaf.buildLeaf(line.ne, ChildType.NE);
            workingLeaf.SE = workingLeaf.buildLeaf(line.se, ChildType.SE);
            workingLeaf.SW = workingLeaf.buildLeaf(line.sw, ChildType.SW);

            this.leafHeap.push(workingLeaf.NW);
            this.leafHeap.push(workingLeaf.NE);
            this.leafHeap.push(workingLeaf.SE);
            this.leafHeap.push(workingLeaf.SW);

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
     * @param childType  Whether this is a LEFT or RIGHT child node.
     * @return A new RQuadtree instance representing the split child leaf.
     */
    private RQuadtree buildLeaf(Color childColor, ChildType childType) {
        Color nextColor = (Objects.isNull(childColor)) ? this.color : childColor;
        switch (childType) {
            case NW:
                return new RQuadtree(this.topLeft, new Point(this.division.xValue, this.division.yValue), nextColor);

            case NE:
                return new RQuadtree(new Point(this.division.xValue, this.topLeft.y),
                        new Point(this.bottomRight.x, this.division.yValue), nextColor);

            case SE:
                return new RQuadtree(new Point(this.division.xValue, this.division.yValue), this.bottomRight,
                        nextColor);

            case SW:
                return new RQuadtree(new Point(this.topLeft.x, this.division.yValue),
                        new Point(this.division.xValue, this.bottomRight.y), nextColor);
            default:
                throw new AssertionError();
        }
    }

    /**
     * @return {@code true} if the tree has no child, {@code false} otherwise.
     */
    private boolean isLeaf() {
        return Objects.isNull(this.NW);
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
    public Image toImage(int lineWidth) {
        Image image = new Image(this.width(), this.height());
        return this.colorImage(image, lineWidth);
    }

    /**
     * Color an image a rectangle at a time.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the number of pixels.
     * </p>
     *
     * @param image     The image to progressively color.
     * @param lineWidth The line width of separation between rectangles.
     * @return The colored image.
     */
    private Image colorImage(Image image, int lineWidth) {
        if (this.isLeaf()) {
            image.setRectangle(this.topLeft.x, this.bottomRight.x, this.topLeft.y, this.bottomRight.y, this.color);
            return image;
        }

        image = this.NW.colorImage(image, lineWidth);
        image = this.NE.colorImage(image, lineWidth);
        image = this.SE.colorImage(image, lineWidth);
        image = this.SW.colorImage(image, lineWidth);

        if (lineWidth > 0)
            this.drawSeparationLine(image, lineWidth);

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
     * @param image     The image to add a separation line.
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RQT(")
                .append(this.width()).append(",").append(this.height()).append(",");
        this.appendNode(sb);
        return sb.append(")").toString();
    }

    private void appendNode(StringBuilder sb) {
        if (this.isLeaf()) {
            sb.append("L(").append(this.color.getRed()).append(",")
                    .append(this.color.getGreen()).append(",").append(this.color.getBlue()).append(")");
            return;
        }
        sb.append("D(").append(this.division.xValue).append(",").append(this.division.yValue).append(",");
        this.NW.appendNode(sb);
        sb.append(",");
        this.NE.appendNode(sb);
        sb.append(",");
        this.SE.appendNode(sb);
        sb.append(",");
        this.SW.appendNode(sb);
        sb.append(")");
    }
}
