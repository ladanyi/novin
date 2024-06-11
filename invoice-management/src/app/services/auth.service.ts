import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import jwt_decode from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // A backend API URL-je
  private tokenKey = 'token';
  private usernameKey = 'username';
  private rolesKey = 'roles';

  constructor(private http: HttpClient) { }

  login(username: string, password: string, captchaResponse: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { username, password, captchaResponse }).pipe(
      tap(response => {
        this.setToken(response.token);
        this.setUserDetails(response.token);
      })
    );
  }

  private setUserDetails(token: string): void {
    //const decodedToken: any = jwt_decode(token);
    //localStorage.setItem(this.usernameKey, decodedToken.sub);
    //localStorage.setItem(this.rolesKey, JSON.stringify(decodedToken.roles));
  }

  register(username: string, password: string, role: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, { username, password, role });
  }

  logout(): void {
    this.http.post<any>(`${this.apiUrl}/logout`, {}).subscribe(() => {
      this.removeToken();
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.usernameKey);
      localStorage.removeItem(this.rolesKey);
    });
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  removeToken(): void {
    localStorage.removeItem('token');
  }

  getUserRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];

    const payload = atob(token.split('.')[1]);
    const parsedPayload = JSON.parse(payload);
    return parsedPayload.roles || [];
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/users`);
  }

  updateUserRoles(id: number, roles: string[]): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/${id}/roles`, roles);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/users/${id}`);
  }
}
