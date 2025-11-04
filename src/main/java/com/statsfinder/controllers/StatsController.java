package com.statsfinder.controllers;

import com.statsfinder.exceptions.EmptyFileException;
import com.statsfinder.exceptions.InvalidFileFormatException;
import com.statsfinder.services.ScannerService;
import com.statsfinder.services.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;

@RestController
@RequestMapping("/api/file-scanner")
@RequiredArgsConstructor
@Schema(description = "Поиск значений из файлов.")
public class StatsController {

    private static final String EMPTY_FILE = "Файл не должен быть пустым";
    private static final String INVALID_FILE_FORMAT = "Поддерживаются только файлы формата xlsx.";

    private final ScannerService scannerService;
    private final StatsService statsService;

    @PostMapping(value = "/find-min-number/xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Загрузить файл со списком чисел и найти минимальное по заданному порядковому номеру число.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Integer.class))}),
        @ApiResponse(responseCode = "400", description = "Неверный формат файла или данных"),
        @ApiResponse(responseCode = "500", description = "Ошибка обработки файла")
    })
    public ResponseEntity<Integer> getMinNumberFromXlsx(
        @Parameter(description = "XLSX файл с числами", required = true)
        @RequestParam("file") MultipartFile file,
        @Parameter(description = "Номер минимального значения", required = true)
        @RequestParam Integer indexNumber) {

        if (file.isEmpty()) throw new EmptyFileException(EMPTY_FILE);

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx"))
            throw new InvalidFileFormatException(INVALID_FILE_FORMAT);

        return ResponseEntity.ok(
            statsService.findMinNumber(scannerService.extractNumbersFromXlsx(file), indexNumber)
        );
    }

}
