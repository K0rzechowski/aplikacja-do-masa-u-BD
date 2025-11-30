export interface Availability {
    id?: string,
    employeeId: string,
    date: string,
    type: AvailabilityType
}

export enum AvailabilityType{
    DESIRED = 'POŻĄDANY',
    UNDESIRED = 'NIE POŻĄDANY',
    AVAILABLE = 'DOSTĘPNY',
    UNAVAILABLE = 'NIEDOSTĘPNY'
}