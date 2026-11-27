public class Test_StrategySpecs {
    public static void main(String[] args) {
        StrategySpecs specs = new StrategySpecs(new String[] {
                "2", "unused", "100", "80", "12", "5", "1", "A"
        });

        assert specs.fileLength(null) == 12;
        assert specs.fileLength("7") == 7;
    }
}
