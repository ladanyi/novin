import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';


declare var grecaptcha: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  submitted = false;
  siteKey: string = '6Le_VfUpAAAAAL4wFUDc7149WzyrACrK-YXtkqlz';
  captchaResponse: string = '';
  showCaptcha: boolean = false;
  failedAttempts: number = 0;
  recaptchaRendered: boolean = false;

  constructor(
    private authService: AuthService, 
    private router: Router, 
    private cdr: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar) { 
      this.loginForm = this.formBuilder.group({
        username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
        password: ['', [Validators.required, Validators.minLength(6)]]
      });
    }

    get formControls() { return this.loginForm.controls; }

  ngOnInit(): void {
    this.loadRecaptchaScript();
    if (this.failedAttempts >= 3) {
        this.showCaptcha = true;
        this.cdr.detectChanges();
        this.renderRecaptcha();
    }
  }

  login(): void {
    this.submitted = true;
    const { username, password } = this.loginForm.value;
    if (this.showCaptcha && !this.captchaResponse) {
      this.snackBar.open('Please complete the reCAPTCHA', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['snackbar-error']
      });
      return;
    }

    this.authService.login(username, password, this.captchaResponse).pipe(
      catchError(error => {
        this.failedAttempts++;
        console.log('Login attempt failed, attempts:', this.failedAttempts);
        if (this.failedAttempts >= 3) {
          this.showCaptcha = true;
          this.cdr.detectChanges();
          this.renderRecaptcha();
        } if (this.showCaptcha) {
          grecaptcha.reset();
        }
        this.snackBar.open('Login attempt failed. Please try again.', 'Close', {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['snackbar-error']
        });
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        this.authService.setToken(response.token);
        this.failedAttempts = 0;
        this.router.navigate(['/dashboard']);
      }
    });
  }

  resolved(captchaResponse: string): void {
    this.captchaResponse = captchaResponse;
  }

  loadRecaptchaScript() {
    if (document.getElementById('recaptcha-script')) return;

    const script = document.createElement('script');
    script.src = 'https://www.google.com/recaptcha/api.js';
    script.id = 'recaptcha-script';
    script.async = true;
    script.defer = true;
    document.body.appendChild(script);
  }

  renderRecaptcha() {
    setTimeout(() => {
      if (document.getElementById('recaptcha-container') && !this.recaptchaRendered) {
        grecaptcha.render('recaptcha-container', {
          sitekey: this.siteKey,
          callback: (response: string) => {
            this.captchaResponse = response;
            console.log('reCAPTCHA response:', response);
          }
        });
        this.recaptchaRendered = true;
      }
    }, 100);
  }

  navigateRegister(): void {
    this.router.navigate(['/register']);
  }

  hasError(controlName: string, errorName: string): boolean {
    return this.formControls[controlName].hasError(errorName);
  }
}
