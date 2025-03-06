export interface Seat {
  seatID: number;
  position: number;
  free: boolean;
  pessoa?: {
    name: string;
    cpf: string;
  };
}
