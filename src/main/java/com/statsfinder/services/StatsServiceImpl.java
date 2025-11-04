package com.statsfinder.services;

import com.statsfinder.exceptions.WrongIndexException;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {

    private final String WRONG_INDEX = "Номер запрошенного числа меньше 1 или превышает длину списка чисел";

    @Override
    public int findMinNumber(int[] numbers, int indexNumber) {

        if (indexNumber < 1 || indexNumber > numbers.length) throw new WrongIndexException(WRONG_INDEX);

        return sortArray(numbers.clone())[indexNumber - 1];
    }

    private int[] sortArray(int[] numbers) {

        int n = numbers.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    int temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

        return numbers;
    }

}
