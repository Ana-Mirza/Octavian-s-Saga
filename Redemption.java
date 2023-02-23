import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Redemption {
    int N;
    int M;
    int P;
    int K;

    ArrayList<String> cardsOwned;
    ArrayList<String> packageCards;
    ArrayList<Integer> freq;
    ArrayList<Integer> coveredCards;
    HashMap<String, Integer> hashMap;

    ArrayList<ArrayList<Integer>> InputMat;
    ArrayList<Integer> subsetIndices;

    public Redemption() {
        InputMat = new ArrayList<>();
        cardsOwned = new ArrayList<>();
        packageCards = new ArrayList<>();
        subsetIndices = new ArrayList<>();
        freq = new ArrayList<>();
        coveredCards = new ArrayList<>();
    }

    public void updateArrays(int index) {
        /* add subset in list of packages chosen */
        subsetIndices.add(index + 1);

        /* remove cards covered from frequency array */
        InputMat.get(index).forEach(card ->
                freq.set(card, 0));

        /* add cards in covered cards array */
        InputMat.get(index).forEach(card -> {
            if (!coveredCards.contains(card))
                coveredCards.add(card);
                });
    }

    public void transformInput() {
        N = hashMap.size();
        M = InputMat.size();
        K = 0;
    }
    public void solve() throws IOException, InterruptedException {
        /* read input data */
        readProblemData();

        /* transform input */
        transformInput();

        /* search for packages until all cards needed are covered */
        while (coveredCards.size() != N) {
            /* find all elements covered only by one subset */
            for (int j = 0; j < freq.size(); j++) {
		if (freq.get(j) == 1) {
                    int index = j;

		    /* search for package containing the card */
                    for (int i = 0; i < InputMat.size(); i++) {

                        /* packages found - update status of arrays */
                        if (InputMat.get(i).contains(index)) {
                            updateArrays(i);
                            break;
                        }
                    }
                }
            }

            /* all cards needed were covered */
            if (coveredCards.size() == N)
                break;

            /* find subset with best covering */
            ArrayList<Integer> covering = new ArrayList<>();
            InputMat.forEach(list -> covering.add(0));
	    int max = -1;
            int index = -1;

            for (int i = 0; i < InputMat.size(); i++) {

                /* package not chosen already */
                if (!subsetIndices.contains(i + 1)) {
                    for (int j = 0; j < InputMat.get(i).size(); j++) {
                        int card = InputMat.get(i).get(j);

                        /* update covering */
                        if (!coveredCards.contains(card)) {
                            covering.set(i, covering.get(i) + 1);

			   /* package with best covering */
                            if (covering.get(i) > max) {
                                max = covering.get(i);
                                index = i;
                            }
                        }
                    }
                }
            }

            /* update arrays */
            updateArrays(index);
        }

        /* print result */
        writeAnswer();
    }

    public void readProblemData() throws IOException {
        Scanner scanner = new Scanner(System.in);
        hashMap = new HashMap<>();

        /* read input */
        N = scanner.nextInt();      /* number of cards owned*/
        M = scanner.nextInt();      /* number of cards in package */
        P = scanner.nextInt();      /* number of packages available for buying */

        /* read cards owned */
        for (int i = 0; i <= N; i++) {
            String card = scanner.nextLine();
            if (!card.isEmpty())
                cardsOwned.add(card);
        }

        /* read cards in desired package */
        for (int i = 0; i < M; i++) {
            packageCards.add(scanner.nextLine());

            /* place card in hashmap for cards needed if not owned */
            if (!cardsOwned.contains(packageCards.get(packageCards.size() - 1)))
                hashMap.put(packageCards.get(packageCards.size() - 1),
                        hashMap.size() + 1);
        }

        /* create frequency vector */
        for (int i = 0; i <= hashMap.size(); i++) {
            freq.add(0);
        }

        /* read packages given for buying */
        for (int i = 0; i < P; i++) {
            int x = Integer.parseInt(scanner.nextLine());
            InputMat.add(new ArrayList<>());

            /* read cards in package x_i */
            for (int j = 0; j < x; j++) {
                String card = scanner.nextLine();

                /* add card in list of package cards if not owned */
                if (hashMap.containsKey(card)) {
                    InputMat.get(i).add(hashMap.get(card));
                    int index = hashMap.get(card);
                    freq.set(index, freq.get(index) + 1);
                }
            }
        }
    }


    public void writeAnswer() throws IOException {
        /* print number of packages bought */
        System.out.println(subsetIndices.size());

        /* print indices of packages bought */
        subsetIndices.forEach(subset -> System.out.print(subset + " "));
        System.out.print("\n");
    }

    public static void main(String argv[]) throws IOException, InterruptedException {
        Redemption redemption = new Redemption();
        redemption.solve();
    }
}
