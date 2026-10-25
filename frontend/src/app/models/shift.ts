export interface Shift {
    id: string,
    companyId: string,
    branchOfficeId: string,
    branchOfficeName: string,
    startTime: string,
    endTime: string,
    roomNumber: string,
    assignedEmployeeId: string | null,
    assignedEmployeeName: string | null
}
