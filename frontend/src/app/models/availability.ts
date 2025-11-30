export interface Availability {
    id?: string,
    employeeId: string,
    date: string,
    availabilityType: AvailabilityType
}

export enum AvailabilityType{
    DESIRED = 'DESIRED',
    UNDESIRED = 'UNDESIRED',
    AVAILABLE = 'AVAILABLE',
    UNAVAILABLE = 'UNAVAILABLE'
}

export const AvailabilityTypeLabels: Record<AvailabilityType, string> = {
    DESIRED: 'Pożądany',
    UNDESIRED: 'Nie pożądany',
    AVAILABLE: 'Dostępny',
    UNAVAILABLE: 'Nie dostępny'
}