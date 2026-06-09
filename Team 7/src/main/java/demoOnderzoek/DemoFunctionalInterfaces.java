package demoOnderzoek;
import java.util.ArrayList;
import java.util.List;

public class DemoFunctionalInterfaces {
    public static void main(String[] args) {

        List<Integer> nummers = new ArrayList<>();

        // lijst maken
        for (int i = 0; i < 1000000; i++) {
            nummers.add(i);
        }
        // Zonder lambda-expressie
        long startLoop = System.nanoTime();
        List<Integer> loopResultaat = new ArrayList<>();

        for (Integer nummer : nummers) {
            if (nummer > 500000) {
                loopResultaat.add(nummer);
            }
        }
        long eindLoop = System.nanoTime();
        System.out.println("For-loop manier: " + loopResultaat.size());

        // Met lambda-expressie
        long startLambda = System.nanoTime();
        List<Integer> lambdaResultaat = nummers.stream()
                .filter(x -> x > 500000)
                .toList();

        long eindLambda = System.nanoTime();
        System.out.println("Lambda manier: " + lambdaResultaat.size());

        long tijdLoop = eindLoop - startLoop;
        long tijdLambda = eindLambda - startLambda;

        System.out.println("Tijd for-loop: " + tijdLoop + " nanoseconden");
        System.out.println("Tijd lambda: " + tijdLambda + " nanoseconden");
    }
}