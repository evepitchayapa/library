package com.ascend.library.service;

import com.ascend.library.exception.InvalidPublishDateException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ValidationService {
    public LocalDate validateDate (LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        int year = date.getYear();
        if (date.isAfter(currentDate)) {
            date = LocalDate.of(date.getYear() - 543, date.getMonth(), date.getDayOfMonth());
            if (date.isAfter(currentDate)) {
                throw new InvalidPublishDateException("Invalid date should be before or equal current date.");
            }
        }
        return date;
    }
}
