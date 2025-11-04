package com.statsfinder.services;

import com.statsfinder.exceptions.EmptyFileException;
import com.statsfinder.exceptions.NotIntegerNumberException;
import com.statsfinder.exceptions.XlsxFileProcessingException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScannerServiceImpl implements ScannerService {

    private static final String NUMBERS_NOT_FOUND = "В файле не найдены числа.";
    private static final String PROCESSING_ERROR = "Ошибка обработки файла";
    private static final String NOT_INTEGER_NUMBER = "В списке должны быть только целые числа.";
    private static final String EMPTY_FILE = "Файл не должен быть пустым.";

    @Override
    public int[] extractNumbersFromXlsx(MultipartFile file) {

        if (file.isEmpty()) throw new EmptyFileException(EMPTY_FILE);

        List<Integer> numbersList = extractNumbersFromMultipartFile(file);

        if (numbersList.isEmpty()) throw new EmptyFileException(NUMBERS_NOT_FOUND);

        return numbersList.stream().mapToInt(Integer::intValue).toArray();
    }

    private List<Integer> extractNumbersFromMultipartFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return extractNumbersFromWorkbook(new XSSFWorkbook(inputStream));
        } catch (IOException e) {
            throw new XlsxFileProcessingException(PROCESSING_ERROR);
        }
    }

    private List<Integer> extractNumbersFromWorkbook(Workbook workbook) {

        List<Integer> numbers = new ArrayList<>();

        for (Row row : workbook.getSheetAt(0)) {
            extractNumberFromRow(row).ifPresent(numbers::add);
        }

        return numbers;
    }

    private Optional<Integer> extractNumberFromRow(Row row) {
        Cell cell = row.getCell(0);

        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            if (numericValue % 1 != 0) throw new NotIntegerNumberException(NOT_INTEGER_NUMBER);
            return Optional.of((int) numericValue);
        }
        return Optional.empty();
    }
}
