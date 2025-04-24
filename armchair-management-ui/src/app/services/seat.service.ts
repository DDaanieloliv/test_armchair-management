import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment'; // Caminho relativo correto p/Prod
//import { environmentDev } from '../../environments/environment'; // Caminho relativo correto p/Dev
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Seat } from '../models/seat.model';
import { timeout } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class SeatService {
  private apiUrl = `${environment.apiUrl}/seats`; // Use a URL da API do environment

  constructor(private http: HttpClient) {}

 private handleError(error: HttpErrorResponse) {
   let errorMessage = 'Ocorreu um erro inesperado. Tente novamente mais tarde.';

   if (error.status === 0) {
     errorMessage = 'Falha na conexão com o servidor.';
   } else if (error.status >= 500) {
     errorMessage = 'Erro interno no servidor. Nossa equipe já está verificando.';
   } else if (error.status === 404) {
     errorMessage = 'Recurso não encontrado.';
   } else if (error.status === 403) {
     errorMessage = 'Você não tem permissão para acessar esse recurso.';
   }

   // No modo de produção, não exibir detalhes no console
   if (!environment.production) {
     console.error('Erro na requisição:', {
       status: error.status,
       message: errorMessage
     });
   }

   return throwError(() => new Error(errorMessage));
 }

  getAllSeats(): Observable<Seat[]> {
    return this.http.get<Seat[]>(this.apiUrl).pipe(
      timeout(10000), // Limita a requisição a 10 segundos
         catchError((error) => {
           if (error.name === 'TimeoutError') {
             return throwError(() => new Error('Tempo limite de conexão excedido. Tente novamente.'));
           }
           return this.handleError(error);
         })
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
