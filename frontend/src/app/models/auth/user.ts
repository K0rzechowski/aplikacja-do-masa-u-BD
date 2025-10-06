import { LocalDate } from 'local-date';

export enum UserRole {
    ADMIN = 'ADMIN',
    USER = 'USER',
    EMPLOYEE = 'EMPLOYEE'
}

export interface User {
    id?: string,
    password?: string,
    email: string,
    name: string,
    surname: string,
    date_of_birth: LocalDate,
    role: UserRole
}