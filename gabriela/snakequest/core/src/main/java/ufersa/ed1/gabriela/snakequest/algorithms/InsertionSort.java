package ufersa.ed1.gabriela.snakequest.algorithms;

import ufersa.ed1.gabriela.snakequest.structures.GenericArray;

public class InsertionSort {

    //Método genérico de ordenação: funciona para qualquer tipo que implemente Comparable..
    public static <T extends Comparable<T>> void sort(GenericArray<T> arr) {
        for (int i = 1; i < arr.size(); i++) {
            T key = arr.get(i);
            int j = i - 1;

            // Move elementos maiores que 'key' uma posição à frente
            while (j >= 0 && arr.get(j).compareTo(key) < 0) { // ordenação decrescente
                arr.set(j + 1, arr.get(j));
                j--;
            }
            arr.set(j + 1, key);
        }
    }
}
