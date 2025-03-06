import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment'; // Caminho relativo correto
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Seat } from '../models/seat.model';

@Injectable({
  providedIn: 'root'
})
export class SeatService {
  private apiUrl = `${environment.apiUrl}/seats`; // Use a URL da API do environment

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    console.error('Erro na requisição:', error);
    return throwError(() => new Error('Ocorreu um erro ao carregar os dados.'));
  }

  getAllSeats(): Observable<Seat[]> {
    return this.http.get<Seat[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  getSeatByPosition(position: number): Observable<Seat> {
    return this.http.get<Seat>(`${this.apiUrl}/${position}`).pipe(
      catchError(this.handleError)
    );
  }

  allocateSeat(position: number, name: string, cpf: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/allocate`, { position, name, cpf }).pipe(
      catchError((error) => {
        console.error('Erro ao alocar poltrona:', error);
        throw error; // Reenvia o erro para o componente
      })
    );
  }

  removePersonFromSeat(position: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/remove/${position}`, {}).pipe(
      catchError(this.handleError)
    );
  }
}
