function checkPhoneNumberLength(phoneNumber: string): boolean {
    // Remove non-digit characters and get length
    const length = phoneNumber.length; 
    return length == 9;
}

function checkPhoneNumberCharacters(phoneNumber: string): boolean {
    const phoneRegex = /^[0-9]+$/;
    return phoneRegex.test(phoneNumber);
}

const PhoneNumberValidator = {
    checkPhoneNumberLength,
    checkPhoneNumberCharacters
};

export default PhoneNumberValidator;