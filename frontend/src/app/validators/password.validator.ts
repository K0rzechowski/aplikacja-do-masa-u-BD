import { AbstractControl, ValidationErrors } from "@angular/forms"

function matchPassword(control: AbstractControl): ValidationErrors | null {
    const confirmPassword = control.value;
    const password = control?.parent?.get('password')?.value;
    if(!password) {
        return null;
    }
    return confirmPassword === password ? null : { mismatch: true };
}

const PasswordValidator = {
   matchPassword 
};

export default PasswordValidator;