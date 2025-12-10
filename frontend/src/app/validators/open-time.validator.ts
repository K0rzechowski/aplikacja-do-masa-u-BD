import { AbstractControl, ValidationErrors } from "@angular/forms";
import moment from "moment";
import { Moment } from "moment";

function checkTime(control: AbstractControl): ValidationErrors | null {
    const closeTime = momentToTimeString(control.value);
    const startTime = momentToTimeString(control.parent?.get('startTime')?.value);

    if (!startTime) {
        return null;
    }
    const closeIsNotBeforeStart = startTime < closeTime;
    return closeIsNotBeforeStart ? null : {closingEarlier: true}
}

function momentToTimeString(m: Moment | Date): Date {
    const mnt = moment.isMoment(m) ? m : moment(m);

    return new Date(mnt.toISOString());
}

const OpenTimeValidator = {
    checkTime
};

export default OpenTimeValidator;