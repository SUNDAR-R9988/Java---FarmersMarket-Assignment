package com.farmersmarket.util;

import com.farmersmarket.exception.InvalidInputException;
import java.util.Scanner;

public class ValidationUtil {

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(">> Error: Input cannot be empty. Please try again.");
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value > 0) {
                    return value;
                }
                System.out.println(">> Error: Value must be a positive integer (> 0).");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid numeric input. Please enter a valid whole number.");
            }
        }
    }

    public static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value > 0.0) {
                    return value;
                }
                System.out.println(">> Error: Amount must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid decimal input. Please enter a valid number (e.g., 45.50).");
            }
        }
    }

    public static int readMenuChoice(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Enter your choice (" + min + " - " + max + "): ");
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println(">> Error: Please select an option between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid input! Please enter a number.");
            }
        }
    }

    public static String readPhoneNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String phone = scanner.nextLine().trim();
            if (phone.matches("\\d{10}")) {
                return phone;
            }
            System.out.println(">> Error: Invalid phone number. Please enter a 10-digit mobile number.");
        }
    }
}
