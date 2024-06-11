import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-invoice-list',
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.css']
})
export class InvoiceListComponent implements OnInit {
  invoices: any[] = [];

  constructor(private dataService: DataService, private router: Router) {}

  ngOnInit(): void {
    this.dataService.getInvoices().subscribe(data => {
      this.invoices = data;
    });
  }

  viewInvoice(id: number) {
    this.router.navigate([`/invoices/view/${id}`]);
  }

  createInvoice() {
    this.router.navigate(['/invoices/create']);
  }
}