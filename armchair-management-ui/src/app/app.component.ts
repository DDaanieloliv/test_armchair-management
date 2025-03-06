import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeatListComponent } from './components/seat-list/seat-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, SeatListComponent], // Importa o SeatListComponent
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'armchair-management-ui';
}
