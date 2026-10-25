-- Constraints Hibernate cannot generate from entities. Runs after Hibernate DDL on every start, so each one is recreated.

CREATE EXTENSION IF NOT EXISTS btree_gist;

-- one active reservation per time slot (cancelled ones stay as history)
CREATE UNIQUE INDEX IF NOT EXISTS uq_reservation_active_time_slot
    ON reservation (time_slot_id) WHERE status = 'RESERVED';

-- a room hosts at most one shift at a time
ALTER TABLE shift DROP CONSTRAINT IF EXISTS ex_shift_room_overlap;
ALTER TABLE shift ADD CONSTRAINT ex_shift_room_overlap
    EXCLUDE USING gist (room_id WITH =, tsrange(start_time, end_time) WITH &&);

-- an employee works at most one shift at a time
ALTER TABLE shift DROP CONSTRAINT IF EXISTS ex_shift_employee_overlap;
ALTER TABLE shift ADD CONSTRAINT ex_shift_employee_overlap
    EXCLUDE USING gist (assigned_employee_id WITH =, tsrange(start_time, end_time) WITH &&);
