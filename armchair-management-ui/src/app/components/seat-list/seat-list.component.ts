import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faChair } from '@fortawesome/free-solid-svg-icons';
import { library } from '@fortawesome/fontawesome-svg-core'; // Importe a função library
import { SeatService } from '../../services/seat.service';
import { Seat } from '../../models/seat.model';
import { isPlatformBrowser} from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment'; // Caminho relativo correto p/Prod
//import { environmentDev } from '../../../environments/environment'; // Caminho relativo correto p/Dev


@Component({
  selector: 'app-seat-list',
  standalone: true,
  imports: [CommonModule, FormsModule, FontAwesomeModule], // Importe o FontAwesomeModule aqui
  templateUrl: './seat-list.component.html',
  styleUrls: ['./seat-list.component.css']
})
export class SeatListComponent implements OnInit {
  seats: Seat[] = [];
  selectedSeat: Seat | null = null;
  allocateForm = { name: '', cpf: '' };
  message: { text: string, type: 'success' | 'error' } | null = null;
  isLoading: boolean = false;
  faChair = faChair; // Defina o ícone faChair

  constructor(private seatService: SeatService,
          @Inject(PLATFORM_ID) private platformId: Object)
  {
    library.add(faChair); // Adicione o ícone à biblioteca do FontAwesome
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
        this.loadSeats(); // Só carrega os assentos no navegador
      } // Chama o método para carregar os assentos e settar as variáveis.
  }

  @HostListener('document:click', ['$event'])
  onClick(event: MouseEvent): void {
    const sidebar = document.querySelector('.sidebar');
    const seatContainer = document.querySelector('.seat-container');
    const target = event.target as HTMLElement;

    if (sidebar && !sidebar.contains(target) && seatContainer && !seatContainer.contains(target)) {
      this.selectedSeat = null;
    }
  }

  loadSeats(): void {
    this.isLoading = true;
    this.seatService.getAllSeats().subscribe({
      next: (data) => {
        this.seats = data;
        this.isLoading = false;
      },
      error: (err) => {
        if (!environment.production) {
          console.error('Erro ao carregar poltronas:', err);
        }
        this.showMessage('Erro ao carrega dados das Poltronas. Tente novamente Mais tarde.', 'error');
        this.isLoading = false;
      }
    });
  }

  selectSeat(seat: Seat): void {
    this.selectedSeat = seat;
  }

  allocateSeat(): void {
    if (this.selectedSeat) {
      this.seatService.allocateSeat(this.selectedSeat.position, this.allocateForm.name, this.allocateForm.cpf).subscribe({
        next: () => {
          this.loadSeats(); // Atualiza os assentos mostrando os novos ocupados ou desocupados.
          this.selectedSeat = null;
          this.allocateForm = { name: '', cpf: '' };
          this.showMessage('Poltrona alocada com sucesso!', 'success');
        },
        error: (err) => {
          if(!environment.production) {
            console.error('Erro ao alocar poltrona:', err);
          }
          this.showMessage('Erro ao alocar poltrona. Tente novamente.', 'error');
        }
      });
    }
  }

  removePerson(seat: Seat): void {
    if (seat.position) {
      this.seatService.removePersonFromSeat(seat.position).subscribe({
        next: () => {
          this.loadSeats();
          this.showMessage('Pessoa removida com sucesso!', 'success');
        },
        error: (err) => {
          if (!environment.production) {
            console.error('Erro ao remover pessoa:', err);
          }
          this.showMessage('Erro ao remover pessoa. Tente novamente.', 'error');
        }
      });
    }
  }

  showMessage(text: string, type: 'success' | 'error'): void {
    this.message = { text, type };
    setTimeout(() => {
      this.message = null;
    }, 10000); // 10 segundos
  }
}
