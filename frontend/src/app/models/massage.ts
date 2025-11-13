import { Company } from "./company";
import { Employee } from "./employee";
import { Symptom } from "./symptom";

export interface Massage {
    id?: string,
    name: string,
    company: Company,
    companyId?: string,
    price: number,
    minutes: number,
    employees?: Employee[],
    symptoms?: Symptom[],
    symptomIds?: string[]
}
