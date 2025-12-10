import { AbstractControl, ValidationErrors } from "@angular/forms";
import moment from "moment";
import { Moment } from "moment";

export function checkIfMonday(control: AbstractControl): ValidationErrors | null {
    const chosenDate = momentToDate(control.value);
    console.log(chosenDate)
    const isMonday = chosenDate instanceof Date && chosenDate.getDay() === 1;
    console.log(chosenDate.getDay() === 1)
    return isMonday ? null : {notMonday: true}
}

function momentToDate(m: Moment | Date): Date {
    const mnt = moment.isMoment(m) ? m : moment(m);

    return new Date(mnt.toISOString());
}