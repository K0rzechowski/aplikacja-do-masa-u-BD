import { User } from "./auth/user";
import { Massage } from "./massage";
import { OptimizedShift } from "./optaplanner/optimized-shifts";

export interface Reservation {
    id: string,
    customer: User,
    status: ReservationStatus,
    shift: OptimizedShift,
    massageForCompany: Massage
}

export enum ReservationStatus {
    RESERVED = 'RESERVED',
    CANCELLED = 'CANCELLED',
    COMPLETED = 'COMPLETED'
}

export const ReservationStatusLabels: Record<ReservationStatus, string> = {
    RESERVED: 'Zarezerwowana',
    CANCELLED: 'Anulowana',
    COMPLETED: 'Zakończona'
}