package demoOnderzoek;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SetPerformanceTest {

    public static void main(String[] args) {

        int[] testSizes = {
                10_000,
                100_000,
                500_000,
                1_000_000
        };

        for (int amount : testSizes) {

            System.out.println("\n==============================");
            System.out.println("Testing with " + amount + " elements");
            System.out.println("==============================");

            testHashSet(amount);
            testTreeSet(amount);
        }
    }

    private static void testHashSet(int amount) {

        Set<String> set = new HashSet<>();

        long startAdd = System.nanoTime();

        for (int i = 0; i < amount; i++) {
            set.add("Party-" + i);
        }

        long endAdd = System.nanoTime();

        long startSearch = System.nanoTime();

        for (int i = 0; i < amount; i++) {
            set.contains("Party-" + i);
        }

        long endSearch = System.nanoTime();

        long addDuration = (endAdd - startAdd) / 1_000_000;
        long searchDuration = (endSearch - startSearch) / 1_000_000;

        System.out.println("HashSet Add Time: " + addDuration + " ms");
        System.out.println("HashSet Search Time: " + searchDuration + " ms");
    }

    private static void testTreeSet(int amount) {

        Set<String> set = new TreeSet<>();

        long startAdd = System.nanoTime();

        for (int i = 0; i < amount; i++) {
            set.add("Party-" + i);
        }

        long endAdd = System.nanoTime();

        long startSearch = System.nanoTime();

        for (int i = 0; i < amount; i++) {
            set.contains("Party-" + i);
        }

        long endSearch = System.nanoTime();

        long addDuration = (endAdd - startAdd) / 1_000_000;
        long searchDuration = (endSearch - startSearch) / 1_000_000;

        System.out.println("TreeSet Add Time: " + addDuration + " ms");
        System.out.println("TreeSet Search Time: " + searchDuration + " ms");
    }
}