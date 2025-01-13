import java.util.Scanner;

public class tehtava1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Generate a random number between 1 and 100:
        int numberToGuess = (int) (Math.random() * 100) + 1;

        int numberOfTries = 0;
        int guess;
        boolean hasGuessedCorrectly = false;

        System.out.println("Tervetuloa numeronarvauspeliin!");

        // The game itself, 7 attempts to guess the number
        while (numberOfTries < 7 && !hasGuessedCorrectly) {
            System.out.print("Anna arvauksesi (1-100): ");
            guess = scanner.nextInt();
            numberOfTries++;

            if (guess < 1 || guess > 100) {
                System.out.println("Arvauksen tulee olla numero väliltä 1 - 100.");
            } else if (guess < numberToGuess) {
                System.out.println("Luku on suurempi!");
            } else if (guess > numberToGuess) {
                System.out.println("Luku on pienempi!");
            } else {
                // The guess is correct
                System.out.println("Oikein! Voitit pelin!");
                hasGuessedCorrectly = true;
            }
        }

        // Correct number was not guessed
        if (!hasGuessedCorrectly) {
            System.out.println("Arvaukset loppuivat! Oikea numero oli: " + numberToGuess);
        }

        scanner.close();
    }
}