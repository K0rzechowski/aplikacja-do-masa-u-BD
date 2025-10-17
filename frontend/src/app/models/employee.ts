import { User } from "./auth/user";
import { Company } from "./company";

export interface Employee  extends User {
    company: Company,
    phoneNumber: string
}
