package com.statsfinder.services;

import org.springframework.web.multipart.MultipartFile;

public interface ScannerService {
    int[] extractNumbersFromXlsx(MultipartFile file);
}
