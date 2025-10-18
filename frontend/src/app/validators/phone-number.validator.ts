import { AbstractControl, ValidationErrors } from "@angular/forms";

function checkPhoneNumberLength(control: AbstractControl): ValidationErrors | null {
    const phoneNumber = control.value;
    const lengthIs9 = typeof phoneNumber === 'string' && phoneNumber.length === 9; 
    return lengthIs9 ? null : { checkPhoneNumberLength: true };
}

function checkPhoneNumberCharacters(control: AbstractControl): ValidationErrors | null {
    const phoneNumber = control.value;
    const phoneRegex = /^[0-9]+$/;
    const correctCharacters = typeof phoneNumber === 'string' && phoneRegex.test(phoneNumber);
    return correctCharacters ? null : { checkPhoneNumberCharacters: true};
}

const PhoneNumberValidator = {
    checkPhoneNumberLength,
    checkPhoneNumberCharacters
};

export default PhoneNumberValidator;