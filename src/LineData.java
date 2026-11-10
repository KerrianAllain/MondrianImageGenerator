import java.awt.Color;

public class LineData {
    public static final String SEPARATOR = " ";

    public final int k;
    public final double p;
    public final double q;
    public final Color nw;
    public final Color ne;
    public final Color se;
    public final Color sw;

    public LineData(String line) {
        String[] content = line.split(SEPARATOR);

        this.k = Integer.parseInt(content[0]);
        this.p = Parser.parseCoordinate(content[1]);
        this.q = Parser.parseCoordinate(content[2]);
        this.nw = Parser.parseColor(content[3]);
        this.ne = Parser.parseColor(content[4]);
        this.se = this.sw = null;
    }

    public LineData(int k, double p, double q, Color nw, Color ne, Color se, Color sw) {
        this.k = k;
        this.p = p;
        this.q = q;
        this.nw = nw;
        this.ne = ne;
        this.se = se;
        this.sw = sw;
    }

    @Override
    public String toString() {
        return String.join(
                SEPARATOR,
                String.valueOf(k),
                String.valueOf(p),
                String.valueOf(q),
                String.valueOf(nw),
                String.valueOf(ne));
    }
}
