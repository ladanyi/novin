import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  submitted = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required]
    });
  }

  get formControls() { return this.registerForm.controls; }

  register(): void {
    this.submitted = true;

    if (this.registerForm.invalid) {
      return;
    }

    const { username, password, role } = this.registerForm.value;

    this.authService.register(username, password, role).subscribe({
      next: response => {
        this.snackBar.open('Registration successful', 'Close', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });
        this.router.navigate(['/login']);
      },
      error: error => {
        this.snackBar.open('Registration failed: ' + error.error.message, 'Close', {
          duration: 3000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  navigateLogin(): void {
    this.router.navigate(['/login']);
  }

  hasError(controlName: string, errorName: string): boolean {
    return this.formControls[controlName].hasError(errorName);
  }
}
