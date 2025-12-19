public class ArrayUtils {
    public static <T> int findFirst(T[] array, T element) {
        if (array == null) {
            return -1;
        }

        for (int i = 0; i < array.length; ++i) {
            if (element == null) {
                if (array[i] == null) {
                    return i;
                }
            } else if (element.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        // пример использования
        final String[] names = {"Alice", "Bob", "Charlie"};
        final int index = ArrayUtils.findFirst(names, "Bob"); // Ожидаем: 1 (тк нумерация в массиве начинается с нуля
    }
}