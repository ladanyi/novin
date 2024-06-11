import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { InvoiceListComponent } from './components/invoice-list/invoice-list.component';
import { InvoiceViewComponent } from './components/invoice-view/invoice-view.component';
import { InvoiceCreateComponent } from './components/invoice-create/invoice-create.component';
import { AdminComponent } from './components/admin/admin.component';

const routes: Routes = [{ path: '', redirectTo: '/login', pathMatch: 'full' },
{ path: 'login', component: LoginComponent },
{ path: 'register', component: RegisterComponent },
{ path: 'dashboard', component: DashboardComponent },
{ path: 'invoices', component: InvoiceListComponent },
{ path: 'invoices/view/:id', component: InvoiceViewComponent },
{ path: 'invoices/create', component: InvoiceCreateComponent },
{ path: 'admin', component: AdminComponent },];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
