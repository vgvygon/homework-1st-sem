public class Calculator<T extends Number> {
    public double sum(T a, T b) {
        if (a == null || b == null) {
            return Double.NaN;
        }
        return a.doubleValue() + b.doubleValue();
    }

    public double substract(T a, T b) {
        if (a == null || b == null) {
            return Double.NaN;
        }

        return a.doubleValue() - b.doubleValue();
    }

    public double multiply(T a, T b) {
        if (a == null || b == null) {
            return Double.NaN;
        }

        return a.doubleValue() * b.doubleValue();
    }

    public double divide(T a, T b) {
        if (a == null || b == null || b.doubleValue() == 0) {
            return Double.NaN;
        }

        return a.doubleValue() / b.doubleValue();
    }

    public static void main(String[] args) {
        // пример использования
        final Calculator<Integer> intCalc = new Calculator<>();
        final double result = intCalc.sum(5, 3); // 8.0

        final Calculator<Double> doubleCalc = new Calculator<>();
        final double div = doubleCalc.divide(10.0, 4.0); // 2.5
    }
}