import {inRange} from "lodash";

// all 2xx status codes are successes
export const isSuccessStatus = (status: number) : boolean => inRange(status, 200, 300);
