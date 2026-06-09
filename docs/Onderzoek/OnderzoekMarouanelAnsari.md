# Research Report: Set Interface with HashSet and TreeSet in Java

## Main Question

How do HashSet and TreeSet differ from each other and from the Set interface in terms of storage and ordering, and which implementation is the most suitable for this project?

---

# Sub Questions

1. What is the Set interface in Java and what is its purpose?
2. What are the main characteristics of a HashSet?
3. What are the main characteristics of a TreeSet?
4. What are the differences between HashSet and TreeSet?
5. In which situations should you use a HashSet and when should you use a TreeSet?

---

# Learning Goals

- Understand the Set interface in Java
- Understand the differences between HashSet and TreeSet
- Analyze computational complexity
- Perform benchmark measurements
- Apply Set implementations in a real-world project

---

# Introduction

When developing software applications, choosing the correct data structure is very important. A good data structure can improve performance, readability, and maintainability of an application.

Java provides many built-in data structures inside the Java Collection Framework. One important interface is the Set interface. A Set is mainly used when duplicate values should not be allowed.

Two common implementations of the Set interface are HashSet and TreeSet. Although both prevent duplicates, they work differently internally and have different performance characteristics.

This report investigates the similarities and differences between HashSet and TreeSet. It also explains how they work internally, demonstrates their performance using measurements, and shows which implementation is the most suitable for this election project.

---

# The Set Interface

The Set interface is part of the Java Collection Framework. It stores collections of unique elements, meaning duplicate values are automatically prevented.

Unlike a List, a Set does not store elements by index position. The main purpose of a Set is to efficiently manage unique data.

Common methods include:

- `add(element)`
- `remove(element)`
- `contains(element)`
- `size()`

Both HashSet and TreeSet implement the Set interface. This means they support the same operations, but internally they behave differently.

---

# HashSet

HashSet is one of the most commonly used Set implementations in Java. It stores data using a hashing mechanism.

When an element is added, Java calculates a hash code and stores the element inside a bucket. Because of this mechanism, HashSet operations are usually very fast.

HashSet does not maintain any ordering of elements.

## Characteristics of HashSet

- Stores unique elements only
- Uses hashing internally
- Does not sort elements
- Allows one null element
- Very fast average performance

## Time Complexity

Average complexity:

- `add()` → O(1)
- `contains()` → O(1)
- `remove()` → O(1)

HashSet is therefore considered a constant-time data structure for most operations.

---

# TreeSet

TreeSet is another implementation of the Set interface.

Instead of hashing, TreeSet stores data inside a self-balancing Red-Black Tree. This means all elements are automatically sorted.

Because TreeSet constantly keeps data ordered, operations are slower than HashSet.

## Characteristics of TreeSet

- Stores unique elements only
- Automatically sorts elements
- Uses a Red-Black Tree internally
- Does not allow null elements
- Slower than HashSet

## Time Complexity

- `add()` → O(log n)
- `contains()` → O(log n)
- `remove()` → O(log n)

TreeSet therefore has logarithmic time complexity.

---

# Similarities Between HashSet and TreeSet

Both implementations share several similarities.

## Similarities

- Both implement the Set interface
- Both prevent duplicates
- Both store unique elements
- Both support generic types
- Both provide methods such as `add()`, `remove()`, and `contains()`

---

# Differences Between HashSet and TreeSet

| Feature | HashSet | TreeSet |
|---|---|---|
| Internal Structure | Hash Table | Red-Black Tree |
| Ordering | No ordering | Automatically sorted |
| Performance | Faster | Slower |
| Null Elements | Allowed | Not allowed |
| Complexity | O(1) average | O(log n) |

HashSet focuses mainly on speed, while TreeSet focuses on maintaining sorted data.

---

# Practical Example from the Election Project

In this election project, a Set implementation is used to prevent duplicate political parties from appearing in the final list.

The application processes many political parties and removes duplicates efficiently.

Original project code:

```java
private List<PartyDTO> mapParties(List<Party> parties) {
    List<PartyDTO> partyDTOs = new ArrayList<>();
    Set<String> seenPartyNames = new LinkedHashSet<>();

    if (parties == null) {
        return partyDTOs;
    }

    parties.stream()
            .filter(Objects::nonNull)
            .filter(party -> party.getName() != null && !party.getName().isBlank())
            .sorted(Comparator.comparing(Party::getName, String.CASE_INSENSITIVE_ORDER))
            .filter(party -> seenPartyNames.add(normalizePartyKey(party.getName())))
            .forEach(party -> {
                String description = partyIdeologyService.getDescriptionForParty(party.getName());
                partyDTOs.add(new PartyDTO(party.getName(), description));
            });

    return partyDTOs;
}
```

---

# Explanation of the Algorithm

The most important line is:

```java
.filter(party -> seenPartyNames.add(normalizePartyKey(party.getName())))
```

This line attempts to add a normalized party name to the Set.

If the name already exists:

```text
add() returns false
```

and the duplicate party is ignored automatically.

If the name does not exist:

```text
add() returns true
```

and the party is processed further.

This is very efficient because Set lookups and insertions are extremely fast.

---

# Benchmark Test

To demonstrate the differences between HashSet and TreeSet, benchmark tests were created.

The following code was used:

```java
package onderzoek;

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
```

---

# Measurement Results

The benchmark was executed three times to make the measurements more reliable. The table below shows the average execution time of the three runs.

| Elements | HashSet Add Avg. | HashSet Search Avg. | TreeSet Add Avg. | TreeSet Search Avg. |
|---|---:|---:|---:|---:|
| 10,000 | 3.33 ms | 3.67 ms | 11.67 ms | 6.33 ms |
| 100,000 | 16.00 ms | 7.67 ms | 37.67 ms | 23.00 ms |
| 500,000 | 42.33 ms | 15.00 ms | 202.67 ms | 101.67 ms |
| 1,000,000 | 121.33 ms | 66.33 ms | 299.33 ms | 260.33 ms |

---

# Analysis of the Results

The measurements show that HashSet is faster than TreeSet in almost every test, especially when the number of elements becomes larger.

For 1,000,000 elements, HashSet needed an average of 121.33 ms to add all elements, while TreeSet needed 299.33 ms. This means TreeSet was about 2.5 times slower when adding elements.

For searching 1,000,000 elements, HashSet needed an average of 66.33 ms, while TreeSet needed 260.33 ms. This means TreeSet was almost 4 times slower during search operations.

This confirms the theoretical difference between both implementations. HashSet uses hashing and has an average time complexity of O(1). TreeSet uses a Red-Black Tree and has a time complexity of O(log n), because it keeps the elements sorted internally.

Small differences between the three runs are normal. Execution time can change because of:

- Java JIT compiler optimizations
- CPU load
- garbage collection
- memory allocation
- background processes

However, the overall trend stays the same: HashSet performs faster than TreeSet when automatic sorting is not required.

---

# Relation to Computational Complexity

According to computational complexity theory:

| Complexity | Growth |
|---|---|
| O(1) | Constant |
| O(log n) | Logarithmic |

HashSet belongs mostly to:

```text
O(1)
```

TreeSet belongs to:

```text
O(log n)
```

This explains why HashSet scales better when large amounts of data are used.

The benchmark measurements support the theoretical computational complexities discussed earlier in the report.

---

# Why LinkedHashSet Was Used in the Project

In the election project, `LinkedHashSet` was used instead of HashSet.

This is because:

- duplicate party names must be prevented
- insertion order must remain consistent
- performance must remain fast

`LinkedHashSet` combines:

- HashSet performance
- predictable ordering

This makes it very suitable for displaying election parties consistently in the frontend.

---

# Why TreeSet Is Less Suitable for This Project

The original code already sorts parties manually using:

```java
.sorted(Comparator.comparing(Party::getName, String.CASE_INSENSITIVE_ORDER))
```

Because of this, TreeSet becomes unnecessary.

TreeSet would sort the data again internally, creating additional overhead and reducing performance.

Therefore, HashSet or LinkedHashSet is more efficient for this project.

---

# Conclusion

HashSet and TreeSet are both implementations of the Set interface, but they are designed for different purposes.

HashSet focuses mainly on speed and efficiency by using hashing internally. Because of its average time complexity of:

```text
O(1)
```

HashSet performs very well when large amounts of data are used.

TreeSet focuses on maintaining sorted data by using a Red-Black Tree internally. Because of this structure, TreeSet operations run in:

```text
O(log n)
```

Although TreeSet is slower, it automatically keeps elements sorted.

The benchmark measurements clearly show that HashSet performs significantly faster than TreeSet during insertion and search operations, especially when the dataset becomes larger.

In this election project, a Set is used to efficiently remove duplicate political parties. The project already sorts the parties manually using a stream comparator, meaning automatic sorting inside TreeSet would create unnecessary overhead.

For this reason, LinkedHashSet was chosen in the project. LinkedHashSet combines:

- fast HashSet performance
- duplicate prevention
- predictable insertion order

This makes it very suitable for displaying election parties consistently in the frontend.

Based on the benchmark results and project requirements, HashSet or LinkedHashSet should be used when:

- performance is important
- duplicate detection is required
- ordering does not need automatic sorting

TreeSet should only be used when:

- elements must always remain sorted
- alphabetical ordering is required
- ordered iteration is important

Examples include leaderboards, ranking systems, and sorted contact lists.

Therefore, HashSet and LinkedHashSet are more suitable for this election project than TreeSet.

---

# References

Oracle. (2024). *Java Platform SE 8: Set Interface*. Oracle Documentation. https://docs.oracle.com/javase/8/docs/api/java/util/Set.html

Oracle. (2024). *Java Platform SE 8: HashSet Class*. Oracle Documentation. https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html

Oracle. (2024). *Java Platform SE 8: TreeSet Class*. Oracle Documentation. https://docs.oracle.com/javase/8/docs/api/java/util/TreeSet.html

Oracle. (2024). *Collections Framework Tutorial*. Oracle Documentation. https://docs.oracle.com/javase/tutorial/collections/

Baeldung. (2024). *HashSet vs TreeSet in Java*. Baeldung. https://www.baeldung.com/java-hashset-vs-treeset

GeeksforGeeks. (2024). *HashSet vs TreeSet in Java*. GeeksforGeeks. https://www.geeksforgeeks.org/hashset-vs-treeset-in-java/