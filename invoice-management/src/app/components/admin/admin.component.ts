import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  users: any[] = [];
  allRoles: string[] = ['Admin', 'Accountant', 'User'];
  selectedRoles: { [key: number]: string[] } = {};

  constructor(private authService: AuthService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.authService.getAllUsers().subscribe((data: any[]) => {
      this.users = data;
      this.users.forEach(user => {
        this.selectedRoles[user.id] = user.roles;
      });
    });
  }

  editRoles(user: any): void {
    if (user && user.id) {
      const roles = this.selectedRoles[user.id].map(role => `ROLE_${role.toUpperCase()}`);
      this.authService.updateUserRoles(user.id, roles).subscribe({
        next: () => {
          this.snackBar.open('Roles updated successfully', 'Close', { duration: 3000 });
          this.loadUsers();
        },
        error: error => {
          this.snackBar.open('Error updating roles', 'Close', { duration: 3000 });
        }
      });
    } else {
      console.error('User id is undefined:', user);
    }
  }

  deleteUser(id: number): void {
    this.authService.deleteUser(id).subscribe({
      next: () => {
        this.snackBar.open('User deleted successfully', 'Close', { duration: 3000 });
        this.loadUsers();
      },
      error: error => {
        this.snackBar.open('Error deleting user', 'Close', { duration: 3000 });
      }
    });
  }
}
