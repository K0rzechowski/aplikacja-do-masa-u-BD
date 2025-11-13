import { Massage } from "./massage";

export interface Symptom {
    id?: string,
    name: string,
    massages?: Massage[]
}
