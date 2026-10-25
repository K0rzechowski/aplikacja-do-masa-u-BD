export interface Review {
    id?: string,
    reservationId: string,
    rating: number,
    comment?: string,
    dateOfCreation?: string
}
