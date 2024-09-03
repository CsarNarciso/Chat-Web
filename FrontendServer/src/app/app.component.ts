import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginComponentComponent } from './login-component/login-component.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, LoginComponentComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'FrontendServer';
}
