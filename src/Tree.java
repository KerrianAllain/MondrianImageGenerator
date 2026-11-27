
public interface Tree {

    public int width();

    public int height();

    public Point topLeftPoint();

    /**
     * Render this tree as an image.
     *
     * <p>
     * <b>Complexity: O(p)</b>, where {@code p} is the image length.
     * </p>
     *
     * @param lineWidth The separation line width.
     * @return The rendered image.
     */
    public Image toImage(int lineWidth);
}
