import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { InvoiceService } from '../../services/invoice.service';

@Component({
  selector: 'app-invoice-view',
  templateUrl: './invoice-view.component.html',
  styleUrls: ['./invoice-view.component.css']
})
export class InvoiceViewComponent implements OnInit {
  invoice: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private invoiceService: InvoiceService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = +idParam;
      this.invoiceService.getInvoiceById(id).subscribe({
        next: (data) => {
          this.invoice = data;
        },
        error: (error) => {
          console.error('Error fetching invoice', error);
        }
      });
    }
  }

  back() {
    this.router.navigate(['/invoices']);
  }
}
