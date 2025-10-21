import { LocalDate } from 'local-date';

export enum UserRole {
    ADMIN = 'ADMIN',
    USER = 'USER',
    EMPLOYEE = 'EMPLOYEE'
}

export interface User {
    id: string,
    password?: string,
    email: string,
    name: string,
    surname: string,
    dateOfBirth: string,
    role: UserRole,
    companyId?: string
}

export interface UpdateProfileRequest {
    name?: string,
    surname?: string,
    dateOfBirth?: string,
    currentPassword?: string,
    newPassword?: string,
}