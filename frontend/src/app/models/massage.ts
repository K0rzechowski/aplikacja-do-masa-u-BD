import { Company } from "./company";
import { Employee } from "./employee";
import { Symptom } from "./symptom";

export interface Massage {
    id?: string,
    name: string,
    company: Company,
    price: number,
    durationInMinutes: number,
    employees?: Employee[],
    symptoms?: Symptom[]
}
