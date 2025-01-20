package viikko2;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;



public class tehtava1 {
    public static void main(String[] args) {
        String birthdateString = System.getenv("BIRTHDATE");
        if(birthdateString == null) {
            System.out.println("Ympäristömuuttujaa 'BIRTHDATE' ei ole asetettu.");
            System.out.println("Pystyt asettamaan ympäristömuuttujan avaamalla terminaalin (Linux/macOS) tai komentokehotteen (Windows) ja kirjoittamalla komennon.");
            System.out.println("Aseta se muodossa YYYY-MM-DD, esimerkiksi:");
            System.out.println("export BIRTHDATE=1999-02-20 (Linux/macOS)");
            System.out.println("set BIRTHDATE=2000-05-01 (Windows)");
            System.out.println("Kun olet tehnyt ylläolevan, aja ohjelma uudestaan.");
            return;
        }
        try {
            // Convert the string to LocalDate
            LocalDate userBirthdate = LocalDate.parse(birthdateString);
            LocalDate currentDate = LocalDate.now();
            long ageInDays = java.time.temporal.ChronoUnit.DAYS.between(userBirthdate, currentDate);


            if (userBirthdate.isBefore(currentDate) 
            && userBirthdate.getMonth() == currentDate.getMonth() 
            && userBirthdate.getDayOfMonth() == currentDate.getDayOfMonth()) {
                System.out.println("Happy birthday!");
            }

            if (userBirthdate.isAfter(currentDate)) {
                System.out.println("Hey, you are not born yet.");
            } else {
                System.out.println("You are " + ageInDays + " days old.");
            }


            if(ageInDays % 1000 == 0){
                System.out.println("That's a nice round number!");
            }

        } catch (DateTimeParseException e) {
            System.out.println("Virheellinen päivämäärämuoto. Käytä ISO 8601 -muotoa (YYYY-MM-DD).");
        }
    }
}