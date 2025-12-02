import { OptimizedShift } from "./optimized-shifts";

export interface OptimizedScheduleResponse {
    message: string,
    shiftsOptimized: number,
    score: string,
    shifts: OptimizedShift[]
}