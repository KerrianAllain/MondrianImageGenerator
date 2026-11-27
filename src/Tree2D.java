import java.awt.Color;
import java.util.List;
import java.util.Objects;

public class Tree2D implements Tree {
    private final Point bottomRight;
    private final Point topLeft;
    private final Color color;

    private Division division;
    private Tree2D east;
    private Tree2D west;

    private BinaryHeap<Tree2D> leafHeap;

    public Tree2D(StrategySpecs specs, List<LineData> linesData) {
        this.bottomRight = new Point(specs.width, specs.height);
        this.topLeft = new Point(0, 0);
        this.color = Color.WHITE;
        this.leafHeap = new BinaryHeap<>(this);

        this.build(specs.nbLeavesToDivide, specs.minCutDimension, specs.variant, linesData);
    }

    /**
     * Reconstructs a tree from its {@link #toString()} representation.
     *
     * @param representation The serialized tree.
     */
    public Tree2D(String representation) {
        this(parse(representation));
    }

    private Tree2D(Tree2D tree) {
        this.bottomRight = tree.bottomRight;
        this.topLeft = tree.topLeft;
        this.color = tree.color;
        this.division = tree.division;
        this.east = tree.east;
        this.west = tree.west;
        this.leafHeap = null;
    }

    private Tree2D(Point topLeft, Point bottomRight, Color color) {
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.color = color;
    }

    private static Tree2D parse(String representation) {
        TreeText.Cursor cursor = new TreeText.Cursor(representation);
        cursor.expect("T2D(");
        int width = cursor.readInt();
        cursor.expect(",");
        int height = cursor.readInt();
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Tree dimensions must be positive.");
        cursor.expect(",");
        Tree2D tree = parseNode(cursor, new Point(0, 0), new Point(width, height));
        cursor.expect(")");
        cursor.finish();
        return tree;
    }

    private static Tree2D parseNode(TreeText.Cursor cursor, Point topLeft, Point bottomRight) {
        if (cursor.startsWith("L("))
            return new Tree2D(topLeft, bottomRight, cursor.readColor());

        cursor.expect("D(");
        Axis axis;
        if (cursor.startsWith("X"))
            axis = Axis.X;
        else if (cursor.startsWith("Y"))
            axis = Axis.Y;
        else
            throw new IllegalArgumentException("Expected a division axis.");
        cursor.expect(axis.toString());
        cursor.expect(",");
        int value = cursor.readInt();
        if ((axis == Axis.X && (value <= topLeft.x || value >= bottomRight.x))
                || (axis == Axis.Y && (value <= topLeft.y || value >= bottomRight.y)))
            throw new IllegalArgumentException("Division must be inside the node boundaries.");

        Tree2D tree = new Tree2D(topLeft, bottomRight, Color.WHITE);
        tree.division = new Division(axis, value);
        Point westBottomRight = axis.withValue(bottomRight, value);
        Point eastTopLeft = axis.withValue(topLeft, value);
        cursor.expect(",");
        tree.west = parseNode(cursor, topLeft, westBottomRight);
        cursor.expect(",");
        tree.east = parseNode(cursor, eastTopLeft, bottomRight);
        cursor.expect(")");
        return tree;
    }

    /**
     * Build the Tree2D from the given data.
     *
     * <p>
     * <b>Complexity: O(n²)</b>, to be more specific, in <b>θ(n * m)</b>,
     * where {@code n} is the number of lines to process,
     * and {@code m} is the complexity of {@link Tree2D#leafOfMaximumWeight(int)}
     * (which increases at each iteration of loop because of the insertions).
     * </p>
     *
     * @param nbLeavesToDivide The maximum number of leaves authorized.
     * @param minCutDimension  The minimal square cut dimension.
     * @param v                The variant used to represent the Tree2D.
     * @param lines            The list of lines from the source file.
     */
    private void build(int nbLeavesToDivide, int minCutDimension, Variant v, List<LineData> lines) {
        int divisions = 0;

        for (LineData line : lines) {
            if (divisions == nbLeavesToDivide)
                break;

            Tree2D workingLeaf = this.leafHeap.pop();
            if (Objects.isNull(workingLeaf) || workingLeaf.width() < minCutDimension
                    || workingLeaf.height() < minCutDimension)
                break;

            Axis axis = Utils.determineAxis(v, workingLeaf.width(), workingLeaf.height());
            workingLeaf.division = new Division(axis, workingLeaf.topLeft, line, workingLeaf);

            workingLeaf.west = workingLeaf.buildLeaf(line.nw, ChildType.NW);
            workingLeaf.east = workingLeaf.buildLeaf(line.ne, ChildType.NE);

            this.leafHeap.push(workingLeaf.west);
            this.leafHeap.push(workingLeaf.east);

            divisions++;
        }
    }

    /**
     * Build a child Tree2D leaf based on the split geometry and child type.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @param childColor Specified color for the child.
     * @param childType  Whether this is a LEFT or RIGHT child node.
     * @return A new Tree2D instance representing the split child leaf.
     */
    private Tree2D buildLeaf(Color childColor, ChildType childType) {
        Point splitPoint = this.division.getSplitPoint(childType, this.topLeft, this.bottomRight);
        Point nextBottomRight = (childType == ChildType.NE) ? this.bottomRight : splitPoint;
        Point nextTopLeft = (childType == ChildType.NW) ? this.topLeft : splitPoint;
        Color nextColor = (Objects.isNull(childColor)) ? this.color : childColor;
        return new Tree2D(nextTopLeft, nextBottomRight, nextColor);
    }

    /**
     * @return {@code true} if the tree has no west child, {@code false} otherwise.
     */
    private boolean isLeaf() {
        return Objects.isNull(this.west);
    }

    /**
     * Determines the leaf that has the maximum weight and respects minCutDimension.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the number of nodes.
     * </p>
     *
     * @param minCutDimension Minimum allowed width and height for a cut.
     * @return The leaf with the maximum weight, or null if no valid leaf exists.
     */
    private Tree2D leafOfMaximumWeight(int minCutDimension) {
        if (this.isLeaf()) {
            boolean isValid = this.width() >= minCutDimension && this.height() >= minCutDimension;
            return isValid ? this : null;
        }

        Tree2D leftMaximum = this.west.leafOfMaximumWeight(minCutDimension);
        Tree2D rightMaximum = this.east.leafOfMaximumWeight(minCutDimension);

        if (Objects.isNull(leftMaximum))
            return rightMaximum;
        if (Objects.isNull(rightMaximum))
            return leftMaximum;

        Tree2D tree = Utils.maxWeightValue(leftMaximum, rightMaximum);
        if (tree != null)
            return tree;

        int comparison = Utils.highestLeftCoordinate(leftMaximum.topLeft, rightMaximum.topLeft);
        return (comparison == -1) ? leftMaximum : rightMaximum;
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

        image = this.west.colorImage(image, lineWidth);
        image = this.east.colorImage(image, lineWidth);

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
        Point start, end;
        int halfWidth = lineWidth / 2;
        int restWidth = lineWidth - halfWidth;

        switch (this.division.axis) {
            case X:
                start = new Point(this.division.value - halfWidth, this.topLeft.y);
                end = new Point(this.division.value + restWidth, this.bottomRight.y);
                break;

            case Y:
                start = new Point(this.topLeft.x, this.division.value - halfWidth);
                end = new Point(this.bottomRight.x, this.division.value + restWidth);
                break;

            default:
                start = this.topLeft;
                end = this.bottomRight;
                break;
        }

        image.setRectangle(start.x, end.x, start.y, end.y, Color.BLACK);
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
        StringBuilder sb = new StringBuilder("T2D(")
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
        sb.append("D(").append(this.division.axis).append(",").append(this.division.value).append(",");
        this.west.appendNode(sb);
        sb.append(",");
        this.east.appendNode(sb);
        sb.append(")");
    }
}
