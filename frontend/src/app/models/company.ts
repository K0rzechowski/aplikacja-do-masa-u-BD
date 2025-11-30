export interface Company {
    id?: string,
    name: string,
    city: string
}

export interface CompanyOpenTime {
    id?: string,
    companyId: string,
    openTime: string,
    closeTime: string
}