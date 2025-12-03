export interface GenerateShiftRequest {
    companyId: string,
    startDate: string
}

export interface GenerateShiftResponse {
    message: string,
    shiftsCreated: number
}