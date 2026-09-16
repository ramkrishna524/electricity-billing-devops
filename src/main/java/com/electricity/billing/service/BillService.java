package com.electricity.billing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.electricity.billing.dao.BillDao;
import com.electricity.billing.dao.UserDao;
import com.electricity.billing.model.Bill;
import com.electricity.billing.model.User;


@Service
public class BillService {

    private final BillDao billDao;
    private final BillingService billingService;
    private final UserDao userDao;

    public BillService(BillDao billDao,
                       BillingService billingService,
                       UserDao userDao) {
        this.billDao = billDao;
        this.billingService = billingService;
        this.userDao = userDao;
    }

    public void createBill(Bill bill) {

        User user = userDao.findByServiceId(bill.getServiceId());

        double amount = billingService.calculateBill(
                user.getUsageType(),
                bill.getUnitsUsed()
        );

        bill.setAmount(amount);

        billDao.saveBill(bill);
    }
    
    public List<Bill> getBillsByServiceId(Long serviceId) {
        return billDao.findByServiceId(serviceId);
    }
}