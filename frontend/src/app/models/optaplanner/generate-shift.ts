export interface GenerateShiftRequest {
    branchOfficeId: string,
    startDate: string
}

export interface GenerateShiftResponse {
    message: string,
    shiftsCreated: number
}
