import { User } from "./auth/user";
import { Company } from "./company";
import { Massage } from "./massage";

export interface Employee extends User {
    company: Company,
    phoneNumber: string
    massages?: Massage[]
}
