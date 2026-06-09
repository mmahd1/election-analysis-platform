package demoOnderzoek;

import com.team7.hboict.dto.CandidateDTO;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class DemoQueueLinkedListVsArrayDequeCandidates {

    public static void main(String[] args) {

        System.out.println("LinkedList and ArrayDeque Queue Demo\n");

        demoFifoBasics("LinkedList", new LinkedList<>());
        demoFifoBasics("ArrayDeque", new ArrayDeque<>());

        demoEmptyQueueBehaviour("LinkedList", new LinkedList<>());
        demoEmptyQueueBehaviour("ArrayDeque", new ArrayDeque<>());

        demoNullBehaviour("LinkedList", new LinkedList<>());
        demoNullBehaviour("ArrayDeque", new ArrayDeque<>());

        demoPerformance();

        System.out.println("Demo finished.");
    }

    private static void demoFifoBasics(String name, Queue<CandidateDTO> queue) {

        System.out.println("FIFO demo using " + name);

        CandidateDTO c1 = candidate(1, "Amina", "El Idrissi", "F", 1200, "T7");
        CandidateDTO c2 = candidate(2, "Bram", "Jansen", "M", 850, "T7");
        CandidateDTO c3 = candidate(3, "Chloe", "de Vries", "F", 640, "T7");

        System.out.println("Adding 3 candidates to the queue.");

        queue.offer(c1);
        queue.offer(c2);
        queue.offer(c3);

        System.out.println("Queue size after adding candidates: " + queue.size());

        System.out.println("First candidate in queue: " + shortName(queue.peek()));

        System.out.println("Removing candidates from the queue:");

        System.out.println(shortName(queue.poll()));
        System.out.println(shortName(queue.poll()));
        System.out.println(shortName(queue.poll()));

        System.out.println("Queue size after removing everything: " + queue.size());
        System.out.println();
    }

    private static void demoEmptyQueueBehaviour(String name, Queue<CandidateDTO> queue) {

        System.out.println("Empty queue demo using " + name);

        System.out.println("peek() returns: " + queue.peek());
        System.out.println("poll() returns: " + queue.poll());

        try {
            queue.element();
        } catch (Exception ex) {
            System.out.println("element() gives exception: " + ex.getClass().getSimpleName());
        }

        try {
            queue.remove();
        } catch (Exception ex) {
            System.out.println("remove() gives exception: " + ex.getClass().getSimpleName());
        }

        System.out.println();
    }

    private static void demoNullBehaviour(String name, Queue<CandidateDTO> queue) {

        System.out.println("Null value demo using " + name);

        try {
            boolean result = queue.offer(null);

            System.out.println("Adding null worked: " + result);
            System.out.println("peek() returns: " + queue.peek());

        } catch (Exception ex) {

            System.out.println("Adding null caused exception: " + ex.getClass().getSimpleName());
        }

        queue.clear();

        System.out.println();
    }

    private static void demoPerformance() {

        System.out.println("Simple performance demo");

        int n = 300_000;

        CandidateDTO[] candidates = new CandidateDTO[n];

        for (int i = 0; i < n; i++) {

            candidates[i] = candidate(
                    (long) i,
                    "Name" + i,
                    "Surname" + i,
                    "X",
                    i % 5000,
                    "T7"
            );
        }

        runOnce(new ArrayDeque<>(), candidates);
        runOnce(new LinkedList<>(), candidates);

        long arrayDequeTime = runOnce(new ArrayDeque<>(), candidates);
        long linkedListTime = runOnce(new LinkedList<>(), candidates);

        System.out.printf("ArrayDeque time: %.2f ms%n", arrayDequeTime / 1_000_000.0);
        System.out.printf("LinkedList time: %.2f ms%n", linkedListTime / 1_000_000.0);

        System.out.println();
    }

    private static long runOnce(Queue<CandidateDTO> queue, CandidateDTO[] candidates) {

        long start = System.nanoTime();

        for (CandidateDTO c : candidates) {
            queue.offer(c);
        }

        while (!queue.isEmpty()) {
            queue.poll();
        }

        return System.nanoTime() - start;
    }

    private static CandidateDTO candidate(
            long id,
            String firstName,
            String lastName,
            String gender,
            int votes,
            String shortCode
    ) {

        return new CandidateDTO(id, firstName, lastName, gender, votes, shortCode);
    }

    private static String shortName(CandidateDTO c) {

        if (c == null) {
            return "null";
        }

        return c.getFirstName() + " " + c.getLastName();
    }
}