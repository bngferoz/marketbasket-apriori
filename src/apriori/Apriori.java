package apriori;
import java.util.*;

public class Apriori {
	static String[][] transactions = {
            {"6", "7", "8", "5", "4", "10"},
            {"3", "8", "7", "5", "4", "10"},
            {"6", "1", "5", "4"},
            {"6", "9", "2", "5", "10"},
            {"2", "8", "8", "5", "4"}
        };
    public static void main(String[] args) {
        

        double minSupport = 0.6; // Minimum support count (60%)
        double minConfidence = 0.8; // Minimum confidence count (80%)

        List<Set<String>> frequentItemSets = generateFrequentItemSets(transactions, minSupport);
        List<AssociationRule> associationRules = generateAssociationRules(frequentItemSets, minConfidence);

        System.out.println("Frequent Item Sets:");
        for (Set<String> itemSet : frequentItemSets) {
            System.out.println(itemSet);
        }

        System.out.println("\nAssociation Rules:");
        for (AssociationRule rule : associationRules) {
            System.out.println(rule);
        }
    }

    public static List<Set<String>> generateFrequentItemSets(String[][] transactions, double minSupport) {
        List<Set<String>> frequentItemSets = new ArrayList<>();
        Map<Set<String>, Integer> itemSetCount = new HashMap<>();

        // Count the occurrences of each item set
        for (String[] transaction : transactions) {
            Set<String> items = new HashSet<>(Arrays.asList(transaction));

            for (String item : items) {
                Set<String> itemSet = new HashSet<>();
                itemSet.add(item);

                if (itemSetCount.containsKey(itemSet)) {
                    itemSetCount.put(itemSet, itemSetCount.get(itemSet) + 1);
                } else {
                    itemSetCount.put(itemSet, 1);
                }
            }

            for (int i = 2; i <= items.size(); i++) {
                Set<Set<String>> subsets = generateSubsets(items, i);

                for (Set<String> subset : subsets) {
                    if (itemSetCount.containsKey(subset)) {
                        itemSetCount.put(subset, itemSetCount.get(subset) + 1);
                    } else {
                        itemSetCount.put(subset, 1);
                    }
                }
            }
        }

        // Generate frequent item sets
        int transactionCount = transactions.length;

        for (Map.Entry<Set<String>, Integer> entry : itemSetCount.entrySet()) {
            double support = (double) entry.getValue() / transactionCount;

            if (support >= minSupport) {
                frequentItemSets.add(entry.getKey());
            }
        }

        return frequentItemSets;
    }

    public static List<AssociationRule> generateAssociationRules(List<Set<String>> frequentItemSets, double minConfidence) {
        List<AssociationRule> associationRules = new ArrayList<>();

        for (Set<String> itemSet : frequentItemSets) {
            if (itemSet.size() > 1) {
                Set<Set<String>> subsets = generateSubsets(itemSet, 1);

                for (Set<String> subset : subsets) {
                    Set<String> complement = new HashSet<>(itemSet);
                    complement.removeAll(subset);

                    double confidence = calculateConfidence(itemSet, subset);

                    if (confidence >= minConfidence) {
                        AssociationRule rule = new AssociationRule(subset, complement, confidence);
                        associationRules.add(rule);
                    }
                }
            }
        }

        return associationRules;
    }

    public static Set<Set<String>> generateSubsets(Set<String> set, int k) {
        Set<Set<String>> subsets = new HashSet<>();

        if (k == 1) {
            for (String item : set) {
                Set<String> subset = new HashSet<>();
                subset.add(item);
                subsets.add(subset);
            }
        } else if (k > 1 && k <= set.size()) {
            List<String> itemList = new ArrayList<>(set);
            int[] indices = new int[k];
            int n = set.size();

            for (int i = 0; i < k; i++) {
                indices[i] = i;
            }

            while (indices[k - 1] < n) {
                Set<String> subset = new HashSet<>();

                for (int index : indices) {
                    subset.add(itemList.get(index));
                }

                subsets.add(subset);

                int t = k - 1;

                while (t != 0 && indices[t] == n - k + t) {
                    t--;
                }

                indices[t]++;

                for (int i = t + 1; i < k; i++) {
                    indices[i] = indices[i - 1] + 1;
                }
            }
        }

        return subsets;
    }

    public static double calculateConfidence(Set<String> itemSet, Set<String> antecedent) {
        double supportItemSet = 0;
        double supportAntecedent = 0;

        for (String[] transaction : transactions) {
            Set<String> items = new HashSet<>(Arrays.asList(transaction));

            if (items.containsAll(itemSet)) {
                supportItemSet++;
                if (items.containsAll(antecedent)) {
                    supportAntecedent++;
                }
            }
        }

        return supportItemSet > 0 ? supportAntecedent / supportItemSet : 0;
    }
}

class AssociationRule {
    private Set<String> antecedent;
    private Set<String> consequent;
    private double confidence;

    public AssociationRule(Set<String> antecedent, Set<String> consequent, double confidence) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.confidence = confidence;
    }

    public Set<String> getAntecedent() {
        return antecedent;
    }

    public Set<String> getConsequent() {
        return consequent;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return antecedent + " => " + consequent + " (Confidence: " + confidence + ")";
    }
}
