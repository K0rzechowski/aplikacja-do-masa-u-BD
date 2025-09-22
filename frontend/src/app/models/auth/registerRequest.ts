import { LocalDate } from 'local-date';

export interface RegisterRequest {
    password: string,
    email: string,
    name: string,
    surname: string,
    date_of_birth: LocalDate,
}