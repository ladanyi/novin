package com.example.invoice_app.service;

import com.example.invoice_app.model.Invoice;
import com.example.invoice_app.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    public void saveInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        if (invoice != null) {
            invoice.setBuyer(invoiceDetails.getBuyer());
            invoice.setIssueDate(invoiceDetails.getIssueDate());
            invoice.setDueDate(invoiceDetails.getDueDate());
            invoice.setItem(invoiceDetails.getItem());
            invoice.setComment(invoiceDetails.getComment());
            invoice.setPrice(invoiceDetails.getPrice());
            return invoiceRepository.save(invoice);
        }
        return null;
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }
}