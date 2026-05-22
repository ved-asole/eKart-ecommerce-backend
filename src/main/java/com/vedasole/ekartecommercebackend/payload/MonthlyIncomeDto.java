package com.vedasole.ekartecommercebackend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyIncomeDto {
    private String date;
    private Double income;
}