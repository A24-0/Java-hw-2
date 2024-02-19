import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Задача 1
        testRectangle();

        //Задача 2
        testStringValidation();

        //Задача 3
        testCalculator();
    }
    private static void testRectangle() {
        Rectangle rect1 = new Rectangle(0, 0, 5, 5);
        Rectangle rect2 = new Rectangle(3, 3, 6, 6);
        Optional<Rectangle> overlap = rect1.overlap(rect2);
        System.out.println("Overlap: " + overlap.orElse(null));
    }

    private static void testStringValidation() {
        ValidateString palindromeValidator = new ValidatePalindrom();
        ValidateString noSpaceValidator = new ValidateNoSpace();

        String palindrome = "radar";
        String nonPalindrome = "hello";
        String stringWithSpace = "hello world";
        String stringWithoutSpace = "helloworld";

        System.out.println("Is '" + palindrome + "' palindrome? " + palindromeValidator.isValid(palindrome));
        System.out.println("Is '" + nonPalindrome + "' palindrome? " + palindromeValidator.isValid(nonPalindrome));

        System.out.println("Does '" + stringWithSpace + "' contain spaces? " + noSpaceValidator.isValid(stringWithSpace));
        System.out.println("Does '" + stringWithoutSpace + "' contain spaces? " + noSpaceValidator.isValid(stringWithoutSpace));
    }

    private static void testCalculator() {
        Calculator calculator = new Calculator();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String commandString = scanner.nextLine();

            if (commandString.equalsIgnoreCase("exit")) {
                System.out.println("Exiting calculator...");
                break;
            }

            calculator.execute(commandString);
            System.out.println("Current number: " + calculator.getCurrentNumber());
        }

        scanner.close();
    }
}

// Задача 1
class Rectangle {
    private int x;
    private int y;
    private int width;
    private int height;

    public Rectangle(int x, int y, int width, int height) {
        if (x < 0 || y < 0 || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative and dimensions must be positive");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    public Optional<Rectangle> overlap(Rectangle r) {
        int x1 = Math.max(this.x, r.x);
        int y1 = Math.max(this.y, r.y);
        int x2 = Math.min(this.x + this.width, r.x + r.width);
        int y2 = Math.min(this.y + this.height, r.y + r.height);

        if (x1 < x2 && y1 < y2) {
            return Optional.of(new Rectangle(x1, y1, x2 - x1, y2 - y1));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rectangle)) return false;
        Rectangle other = (Rectangle) obj;
        return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ") : (" + width + ", " + height + ")";
    }
}

// Задача 2
interface ValidateString {
    boolean isValid(String s);
}

class ValidateOr implements ValidateString {
    private ValidateString validator1;
    private ValidateString validator2;

    public ValidateOr(ValidateString validator1, ValidateString validator2) {
        this.validator1 = validator1;
        this.validator2 = validator2;
    }

    @Override
    public boolean isValid(String s) {
        return validator1.isValid(s) || validator2.isValid(s);
    }
}

class ValidateAnd implements ValidateString {
    private ValidateString validator1;
    private ValidateString validator2;

    public ValidateAnd(ValidateString validator1, ValidateString validator2) {
        this.validator1 = validator1;
        this.validator2 = validator2;
    }

    @Override
    public boolean isValid(String s) {
        return validator1.isValid(s) && validator2.isValid(s);
    }
}

class ValidateInverse implements ValidateString {
    private ValidateString validator;

    public ValidateInverse(ValidateString validator) {
        this.validator = validator;
    }

    @Override
    public boolean isValid(String s) {
        return !validator.isValid(s);
    }
}

class ValidatePalindrom implements ValidateString {
    @Override
    public boolean isValid(String s) {
        String reversed = new StringBuilder(s).reverse().toString();
        return s.equals(reversed);
    }
}

class ValidateNoSpace implements ValidateString {
    @Override
    public boolean isValid(String s) {
        return s.contains(" ");
    }
}

// Задача 3
class Calculator {
    private int currentNumber;
    private Deque<Integer> history = new ArrayDeque<>();
    private Deque<Integer> undone = new ArrayDeque<>();

    public Calculator() {
        this.currentNumber = 0;
    }

    public void execute(String commandString) {
        String[] parts = commandString.split(" ");
        if (parts.length == 0) {
            System.out.println("Empty command");
            return;
        }

        String commandName = parts[0];
        int oldValue = currentNumber;

        switch (commandName) {
            case "add":
                if (parts.length != 2) {
                    System.out.println("Invalid add command format. Usage: add <number>");
                    return;
                }
                int addValue = parseNumber(parts[1]);
                currentNumber += addValue;
                break;
            case "sub":
                if (parts.length != 2) {
                    System.out.println("Invalid sub command format. Usage: sub <number>");
                    return;
                }
                int subValue = parseNumber(parts[1]);
                currentNumber -= subValue;
                break;
            case "mul":
                if (parts.length != 2) {
                    System.out.println("Invalid mul command format. Usage: mul <number>");
                    return;
                }
                int mulValue = parseNumber(parts[1]);
                currentNumber *= mulValue;
                break;
            case "div":
                if (parts.length != 2) {
                    System.out.println("Invalid div command format. Usage: div <number>");
                    return;
                }
                int divValue = parseNumber(parts[1]);
                if (divValue == 0) {
                    System.out.println("Cannot divide by zero");
                    return;
                }
                currentNumber /= divValue;
                break;
            case "undo":
                if (!history.isEmpty()) {
                    undone.push(currentNumber);
                    currentNumber = history.pop();
                } else {
                    System.out.println("Cannot undo, no commands in history");
                }
                return;
            case "redo":
                if (!undone.isEmpty()) {
                    history.push(currentNumber);
                    currentNumber = undone.pop();
                } else {
                    System.out.println("Cannot redo, no commands undone");
                }
                return;
            default:
                System.out.println("Unknown command: " + commandName);
                return;
        }

        history.push(oldValue);
    }

    private int parseNumber(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + str);
            return 0;
        }
    }

    public int getCurrentNumber() {
        return currentNumber;
    }
}