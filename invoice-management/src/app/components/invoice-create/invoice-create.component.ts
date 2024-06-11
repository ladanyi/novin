import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { InvoiceService } from '../../services/invoice.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-invoice-create',
  templateUrl: './invoice-create.component.html',
  styleUrls: ['./invoice-create.component.css']
})
export class InvoiceCreateComponent implements OnInit {
  invoiceForm: FormGroup;
  submitted = false;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private invoiceService: InvoiceService,
    private snackBar: MatSnackBar
  ) {
    this.invoiceForm = this.formBuilder.group({
      buyer: ['', Validators.required],
      issueDate: ['', Validators.required],
      dueDate: ['', Validators.required],
      item: ['', Validators.required],
      comment: [''],
      price: ['', [Validators.required, Validators.min(0)]]
    });
  }

  get formControls() {
    return this.invoiceForm.controls;
  }

  ngOnInit(): void {}

  save(): void {
    this.submitted = true;
    if (this.invoiceForm.invalid) {
      return;
    }

    this.invoiceService.createInvoice(this.invoiceForm.value).subscribe({
      next: response => {
        console.log('Response:', response); 
        this.snackBar.open('Invoice created successfully', 'Close', {
          duration: 3000
        });
        this.router.navigate(['/invoices']);
      }
    });
    this.router.navigate(['/invoices']);
  }

  back(): void {
    this.router.navigate(['/invoices']);
  }
}
