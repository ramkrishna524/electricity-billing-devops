package com.electricity.billing.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electricity.billing.model.Bill;
import com.electricity.billing.service.BillService;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping
    public String createBill(@RequestBody Bill bill) {

        billService.createBill(bill);

        return "Bill created successfully";
    }
    
    @GetMapping("/{serviceId}")
    public List<Bill> getBills(@PathVariable Long serviceId) {
        return billService.getBillsByServiceId(serviceId);
    }
}