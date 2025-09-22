import { LocalDate } from 'local-date';

enum UserRole {
    ADMIN,
    USER,
    MODERATOR
}

export interface User {
    id: string,
    password: string,
    email: string,
    name: string,
    surname: string,
    date_of_birth: LocalDate,
    role: UserRole
}