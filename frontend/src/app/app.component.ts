import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NavbarComponent } from "./components/navbar/navbar.component";
import { AlertComponent } from "./components/alert/alert.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, NavbarComponent, AlertComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'app';
}
