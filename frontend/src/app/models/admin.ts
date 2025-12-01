import { User } from "./auth/user";
import { Company } from "./company";

export interface Admin extends User {
    companyId: string,
    recaptchaResponse: string
}
