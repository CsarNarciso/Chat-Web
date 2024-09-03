import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-login-component',
  standalone: true,
  imports: [],
  templateUrl: './login-component.component.html',
  styleUrl: './login-component.component.css'
})
export class LoginComponentComponent {

  constructor(private http: HttpClient) {}

  credentials = {username: '', password: ''};

  login(): void {
    this.http.post("https://localhost:8000/identity/login", this.credentials);
  }
}
