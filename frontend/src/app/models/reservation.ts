import { User } from "./auth/user";
import { BranchOffice } from "./branch-office";
import { Employee } from "./employee";
import { Massage } from "./massage";
import { Room } from "./room";

export interface ReservationShift {
    id: string,
    startTime: string,
    endTime: string,
    room?: Room,
    branchOffice?: BranchOffice,
    assignedEmployee?: Employee | null
}

export interface Reservation {
    id: string,
    customer: User,
    status: ReservationStatus,
    shift: ReservationShift,
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
