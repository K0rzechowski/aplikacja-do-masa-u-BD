export interface Company {
    id?: string,
    name: string,
    city: string
}

export interface CompanyOpenTime {
    id?: string,
    company: Company,
    openTime: string,
    endTime: string
}