
public enum Axis {
    X {
        @Override
        public Point withValue(Point p, int v) {
            return new Point(v, p.y);
        }
    },
    Y {
        @Override
        public Point withValue(Point p, int v) {
            return new Point(p.x, v);
        }
    };

    public abstract Point withValue(Point p, int v);
}
